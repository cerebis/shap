/**
 *
 * Copyright 2010 Matthew Z DeMaere.
 * 
 * This file is part of SHAP.
 *
 * SHAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SHAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SHAP.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.mzd.shap.spring.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mzd.shap.ApplicationException;
import org.mzd.shap.domain.DataAccessException;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.dao.FeatureDao;
import org.mzd.shap.domain.dao.SequenceDao;
import org.mzd.shap.io.GenbankWriter;
import org.mzd.shap.io.genbank.CodingSequenceWriter;
import org.mzd.shap.io.genbank.Division;
import org.mzd.shap.io.genbank.GeneWriter;
import org.mzd.shap.io.genbank.MoleculeType;
import org.mzd.shap.io.genbank.RibosomalRnaWriter;
import org.mzd.shap.io.genbank.SourceWriter;
import org.mzd.shap.io.genbank.TransferRnaWriter;


public class GenbankWriterImpl implements GenbankWriter {
	private static DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
	private SequenceDao sequenceDao;
	private FeatureDao featureDao;
	private List<String> annotatorPrecendence = new ArrayList<String>();
	private DescriptionEditor defaultEditor = new DescriptionEditor("^(.*)$");
	private Map<String,DescriptionEditor> editors = new HashMap<String,DescriptionEditor>();
	
	/*
	 * TODO: This method should optionally avoid breaking lines on non-whitespace. It
	 * appears that some records types interpret breaks as being whitespace when
	 * reconstructing the single line string.
	 */
	protected String wrappedString(int colSize, int maxSize, String string) {
		if (string.length() <= maxSize) {
			return string;
		}
		else {
			StringBuffer buff = new StringBuffer();
			buff.append(string.substring(0,maxSize) + "\n");
			int width = maxSize - colSize;
			int n = maxSize;
			while (true) {
				// write left margin
				for (int i=0; i<colSize; i++) {
					buff.append(" ");
				}
				
				if (n + width < string.length()) {
					buff.append(string.substring(n, n + width) + "\n");
					n += width;
				}
				else {
					buff.append(string.substring(n));
					break;
				}
			}
			
			return buff.toString();
		}
	}
	
	protected void writeOrigin(Writer writer, String data) throws IOException {
		final int numBlocks = 6;
		final int basesPerBlock = 10;
		
		writer.write("ORIGIN\n");
		
		final int length = data.length();
		
		int i = 0;
		while (i < length) {
			writer.write(String.format("%9d ", i+1));
			
			for (int n=0; n<numBlocks && i<length; n++) {
				int bases = length - i < basesPerBlock ? length - i : basesPerBlock;
				writer.write(data.substring(i, i+bases));
				i += bases;
				if (n<numBlocks-1 && i<length) {
					writer.write(' ');
				}
			}
			
			writer.write('\n');
		}
		
		// Terminator line.
		writer.write("//\n");
	} 
	
	protected void writeEntry(Sequence sequence, Writer writer) throws IOException, DataAccessException {

		sequence = getSequenceDao().loadWithData(sequence.getId());
		
		// Locus
		writer.write(
				 String.format("%-11s %s %d bp %s %s %s\n",
						 "LOCUS",
						 sequence.getName(), 
						 sequence.getDataLength(), 
						 MoleculeType.DNA, 
						 Division.EnvironmentalSampling, 
						 dateFormat.format(new Date())));
		
		// Features
		writer.write(
				String.format("%-20s %s\n", "FEATURES", "Location/Qualifiers"));
		
		new SourceWriter(writer).write(sequence);
		
		for (Feature f : getFeatureDao().findResolvedSet(sequence, null)) {
			
			// write gene feature
			new GeneWriter(writer,getAnnotatorPrecendence()).write(f);
			
			// write product feature (CDS/tRNA/rRNA)
			switch (f.getType()) {
				case TransferRNA:
					new TransferRnaWriter(writer).write(f);
					break;
				case RibosomalRNA:
					new RibosomalRnaWriter(writer).write(f);
					break;
				case OpenReadingFrame:
					new CodingSequenceWriter(
							writer,
							getAnnotatorPrecendence(),
							defaultEditor,
							getEditors())
						.write(f);
					break;
				case NonCoding:
				case Undefined:
			}
			
			getFeatureDao().evict(f);
		}
		
		// Origin
		writeOrigin(writer, sequence.getData().getValue());
	}
	
	public void writeFile(Sequence sequence, File outputFile) throws ApplicationException, IOException {
		Writer writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(outputFile,true));
			writeEntry(sequence,writer);
		}
		finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	public void write(Sequence sequence, Writer output) throws ApplicationException, IOException {
		writeEntry(sequence,output);
	}

	public SequenceDao getSequenceDao() {
		return sequenceDao;
	}
	public void setSequenceDao(SequenceDao sequenceDao) {
		this.sequenceDao = sequenceDao;
	}

	public FeatureDao getFeatureDao() {
		return featureDao;
	}
	public void setFeatureDao(FeatureDao featureDao) {
		this.featureDao = featureDao;
	}

	public List<String> getAnnotatorPrecendence() {
		return annotatorPrecendence;
	}
	public void setAnnotatorPrecendence(List<String> annotatorPrecendence) {
		this.annotatorPrecendence = annotatorPrecendence;
	}

	public Map<String, DescriptionEditor> getEditors() {
		return editors;
	}
	public void setEditors(Map<String, DescriptionEditor> editors) {
		this.editors = editors;
	}
	
}
