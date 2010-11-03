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
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Order;
import org.mzd.shap.domain.DataAccessException;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.FeatureException;
import org.mzd.shap.domain.FeatureType;
import org.mzd.shap.domain.MoleculeType;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.SequenceException;
import org.mzd.shap.domain.Taxonomy;
import org.mzd.shap.domain.dao.FeatureDao;
import org.mzd.shap.domain.dao.SampleDao;
import org.mzd.shap.domain.dao.SequenceDao;
import org.mzd.shap.io.Fasta;
import org.mzd.shap.io.FastaWriter;

public class FastaWriterImpl implements FastaWriter {
	private final static int PAGE_SIZE = 500;
	private FeatureDao featureDao;
	private SequenceDao sequenceDao;
	private SampleDao sampleDao;
	
	/**
	 * Open a BufferedWriter on supplied File.
	 * 
	 * @param outputPath
	 * @param append
	 * @return
	 * @throws IOException
	 */
	protected Writer openWriter(File outputPath, boolean append) throws IOException {
		return new BufferedWriter(new FileWriter(outputPath,append));
	}
	
	public void write(Feature feature, MoleculeType moleculeType, Writer output, boolean verbose) 
			throws IOException, FeatureException, SequenceException {
		
		Fasta fasta = feature.toFasta(moleculeType, verbose);
		fasta.write(output);
	}

	public void write(Feature feature, MoleculeType moleculeType, File outputPath, boolean verbose, boolean append) 
			throws IOException, FeatureException, SequenceException {

		Writer output = null;
		try {
			output = openWriter(outputPath,append);
			write(feature,moleculeType,output,verbose);
		}
		finally {
			if (output != null) {
				output.close();
			}
		}
	}

	public void writeFeatures(Collection<Feature> features, MoleculeType moleculeType, Writer output, boolean verbose) 
			throws IOException, FeatureException, SequenceException {
	
		for (Feature feat : features) {
			feat = getFeatureDao().loadWithData(feat.getId());
			write(feat,moleculeType,output,verbose);
			getFeatureDao().evict(feat);
		}
	}

	public void writeFeatures(Collection<Feature> features, MoleculeType moleculeType, File outputPath, boolean verbose, boolean append) 
			throws IOException, FeatureException, SequenceException {

		Writer output = null;
		try {
			output = openWriter(outputPath,append);
			writeFeatures(features,moleculeType,output,verbose);
		}
		finally {
			if (output != null) {
				output.close();
			}
		}
	}

	public void writeFeatures(Sequence sequence, MoleculeType moleculeType, Writer output, boolean verbose)
		throws IOException, DataAccessException, FeatureException, SequenceException {
		
		sequence = getSequenceDao().findByID(sequence.getId());
		
		FeatureType reportOnly = 
			moleculeType == MoleculeType.Protein ? 
					FeatureType.OpenReadingFrame : null;
		
		for (Feature feat : getFeatureDao().findResolvedSet(sequence, reportOnly)) {
			write(feat,moleculeType,output,verbose);
			getFeatureDao().evict(feat);
		}
		
		getSequenceDao().evict(sequence);
	}

	public void writeFeatures(Sequence sequence, MoleculeType moleculeType, File outputPath, boolean verbose, boolean append)
		throws IOException, DataAccessException, FeatureException, SequenceException {
		
		Writer output = null;
		try {
			output = openWriter(outputPath,append);
			writeFeatures(sequence,moleculeType,output,verbose);
		}
		finally {
			if (output != null) {
				output.close();
			}
		}
	}

	public void writeFeatures(Sample sample, MoleculeType moleculeType, Writer output, boolean verbose, Collection<Taxonomy> excludedTaxa)
		throws IOException, DataAccessException, FeatureException, SequenceException {
	
		sample = getSampleDao().findByID(sample.getId());
		
		for (Sequence seq : getSequenceDao().findBySample(sample)) {
			if (excludedTaxa == null || !excludedTaxa.contains(seq.getTaxonomy())) {
				// TODO this should be done in pages
				for (Feature feat : getFeatureDao().findResolvedSet(seq, FeatureType.OpenReadingFrame)) {
					write(feat,moleculeType,output,verbose);
					getFeatureDao().evict(feat);
				}
				getSequenceDao().evict(seq);
			}
		}
		
		getSampleDao().evict(sample);
	}

	public void writeFeatures(Sample sample, MoleculeType moleculeType, File outputPath, boolean verbose, boolean append, Collection<Taxonomy> excludedTaxa)
		throws IOException, DataAccessException, FeatureException, SequenceException {
		
		Writer output = null;
		try {
			output = openWriter(outputPath,append);
			writeFeatures(sample,moleculeType,output,verbose,excludedTaxa);
		}
		finally {
			if (output != null) {
				output.close();
			}
		}
	}
	
	public void writeSequence(Sequence sequence, Writer output) throws IOException {
		sequence = getSequenceDao().findByID(sequence.getId());
		sequence.writeFasta(output);
		getSequenceDao().evict(sequence);
	}
	
	public void writeSequence(Sequence sequence, File outputPath, boolean append) throws IOException {
		
		Writer output = null;
		try {
			output = openWriter(outputPath,append);
			writeSequence(sequence,output);
		}
		finally {
			if (output != null) {
				output.close();
			}
		}
	}

	public void writeSequences(Sample sample, Writer output, Collection<Taxonomy> excludedTaxa) throws IOException {
		
		sample = getSampleDao().findByID(sample.getId());
		
		long totalSequences = getSequenceDao().countBySample(sample);
		
		// TODO comparing int to long!
		for (int firstSequence=0; firstSequence<totalSequences; firstSequence+=PAGE_SIZE) {
			
			List<Sequence> pagedSeq = getSequenceDao()
				.pageBySample(firstSequence, PAGE_SIZE, Order.asc("id"), sample);
			
			for (Sequence seq : pagedSeq) {
				if (excludedTaxa == null || !excludedTaxa.contains(seq.getTaxonomy())) {
					writeSequence(seq,output);
				}
			}
		}
		
		getSampleDao().evict(sample);
	}

	public void writeSequences(Sample sample, File outputPath, boolean append, Collection<Taxonomy> excludedTaxa) throws IOException {
		
		Writer output = null;
		try {
			output = openWriter(outputPath,append);
			writeSequences(sample,output,excludedTaxa);
		}
		finally {
			if (output != null) {
				output.close();
			}
		}
	}
	
	public FeatureDao getFeatureDao() {
		return featureDao;
	}
	public void setFeatureDao(FeatureDao featureDao) {
		this.featureDao = featureDao;
	}

	public SequenceDao getSequenceDao() {
		return sequenceDao;
	}
	public void setSequenceDao(SequenceDao sequenceDao) {
		this.sequenceDao = sequenceDao;
	}

	public SampleDao getSampleDao() {
		return sampleDao;
	}
	public void setSampleDao(SampleDao sampleDao) {
		this.sampleDao = sampleDao;
	}
	
}
