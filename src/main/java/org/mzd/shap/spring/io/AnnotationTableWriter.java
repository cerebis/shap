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
import org.mzd.shap.analysis.Annotator;
import org.mzd.shap.analysis.AnnotatorDao;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.AnnotationType;
import org.mzd.shap.domain.DataAccessException;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Project;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.SequenceException;
import org.mzd.shap.domain.Taxonomy;
import org.mzd.shap.domain.dao.AnnotationDao;
import org.mzd.shap.domain.dao.FeatureDao;
import org.mzd.shap.domain.dao.SampleDao;
import org.mzd.shap.domain.dao.SequenceDao;
import org.mzd.shap.io.TableWriter;
import org.mzd.shap.util.StringUtils;

public class AnnotationTableWriter implements TableWriter {
	private SampleDao sampleDao;
	private SequenceDao sequenceDao;
	private FeatureDao featureDao;
	private AnnotatorDao annotatorDao;
	private AnnotationDao annotationDao;
	private static final String CONF_FORMAT = "%.3e";
	private static final String BAD_CHARS = "\t>";
	private static final String SUBST_CHAR = " ";
	
	/**
	 * Open the destination {@link Writer}.
	 * 
	 * @param outputFile - the file for which to open a Writer.
	 * @param append - select whether or not file should be appended to.
	 * @return BufferedWriter
	 * @throws IOException
	 */
	protected BufferedWriter openWriter(File outputFile, boolean append) throws IOException {
		if (!append && outputFile.exists()) {
			throw new IOException("File \"" + outputFile.getPath() + "\" already exists");
		}
		return new BufferedWriter(new FileWriter(outputFile,append));
	}
	
	/**
	 * Write out the header columns for the given set of {@link Annotator}S.
	 * 
	 * @param annotators - the list of {@link Annotator}S
	 * @param output - destination
	 * @throws IOException
	 */
	protected void writeAnnotatorsHeader(List<Annotator> annotators, Writer output) throws IOException {
		for (Annotator antr : annotators) {
			String name = antr.getName().toUpperCase();
			output.append(
					"\t" + name + "_acc" + 
					"\t" + name + "_desc" + 
					"\t" + name + "_conf"); 
		}
	}
	
	/**
	 * Write out the annotations columns derived from the specified {@link Annotator}S
	 * for a given {@link Feature}.
	 * 
	 * @param feature - source feature
	 * @param annotators - list of annotators to include
	 * @param output - destination
	 * @throws IOException
	 */
	protected void writeAnnotations(Feature feature, List<Annotator> annotators, Writer output) throws IOException {
		
		for (Annotator antr : annotators) {
			Annotation a = getAnnotationDao()
				.findByAnnotatorAndFeature(antr, feature, AnnotationType.Product);

			if (a != null && a.getDescription() != null) {
				
				// Make sure we don't introduce any illegal characters
				String acc =  StringUtils.stripCharacters(
						BAD_CHARS, SUBST_CHAR, a.getAccession())
						.trim();
				
				String desc = StringUtils.stripCharacters(
						BAD_CHARS, SUBST_CHAR, a.getDescription())
						.trim();
				
				// Handle annotations which have no associated confidence score
				String conf = a.getConfidence() == null ? 
						"n/a" : String.format(CONF_FORMAT,a.getConfidence());
				
				output.append("\t" + acc + "\t" + desc + "\t" + conf);
			}
			else {
				output.append("\t\t\t");
			}

			getAnnotationDao().evict(a);
		}
	}
		
	public void writeFeatures(Collection<Feature> features, Writer output) throws IOException, DataAccessException, SequenceException {
		// Header
		output.append("#FeatureId\tSequenceName");
		List<Annotator> definedAnnotators = getAnnotatorDao().findAll(Order.asc("name"));
		writeAnnotatorsHeader(definedAnnotators,output);
		output.append('\n');
		
		// Body
		for (Feature f : features) {
			f = getFeatureDao().reattach(f);
			output.append(f.getId() + "\t" + f.getSequence().getName());
			writeAnnotations(f,definedAnnotators,output);
			output.append('\n');
			getFeatureDao().evict(f);
		}
		output.append('\n');
	}
	
	public void writeFeatures(Collection<Feature> features, File outputFile, boolean append) throws IOException, DataAccessException, SequenceException {
		BufferedWriter output = null;
		try {
			output = openWriter(outputFile,append);
			writeFeatures(features,output);
		}
		finally {
			if (output != null) {
				output.close();
			}
		}
	}

	public void writeFeatures(Sequence sequence, Writer output) throws IOException, DataAccessException {
		// Preamble
		output.append("ANNOTATION TABLE\n");
		output.append("SEQUENCE: " + sequence.getName());
		if (sequence.getDescription() == null) {
			output.append("\n");
		}
		else {
			output.append(" " + sequence.getDescription() + "\n");
		}
		Sample sample = sequence.getSample();
		output.append("SAMPLE: " + sample.getName() + " " + sample.getDescription() + "\n");
		Project project = sample.getProject();
		output.append("PROJECT: " + project.getName() + " " + project.getDescription() + "\n");
		
		// Header
		output.append("#FEATURE_ID\tLOCATION\tDECTECTOR\tPARTIAL\tFEATURE_conf");
		
		// Just report results for those annotators which 
		// were applied to this sequence.
		List<Annotator> usedAnnotators = getAnnotatorDao().findUsedBySequence(sequence); 
		
		writeAnnotatorsHeader(usedAnnotators, output);
		output.append('\n');

		// Body
		for (Feature f : getFeatureDao().findResolvedSet(sequence, null)) {
			output.append(f.getId() + "\t");
			output.append(f.getLocation() + "\t");
			output.append(f.getDetector().getName() + "\t");
			output.append(f.isPartial() + "\t");
			
			if (f.getConfidence() != null) {
				output.append(String.format("%6.3e",f.getConfidence()));
			}
			
			writeAnnotations(f,usedAnnotators,output);
			output.append('\n');
			getFeatureDao().evict(f);
		}
		output.append('\n');
	}
	
	public void writeFeatures(Sequence sequence, File outputFile, boolean append) throws IOException, DataAccessException {
		BufferedWriter output = null;
		try {
			output = openWriter(outputFile,append);
			sequence = getSequenceDao().findByID(sequence.getId());
			writeFeatures(sequence,output);
		}
		finally {
			if (output != null) {
				output.close();
			}
		}
	}
	
	protected void writeFeatures(Sequence sequence, List<Annotator> usedAnnotators, Writer output) 
			throws IOException, DataAccessException {
		
		sequence = getSequenceDao().findByID(sequence.getId());
		
		// Body
		for (Feature f : getFeatureDao().findResolvedSet(sequence, null)) {
			output.append(sequence.getName() + "\t");
			output.append(f.getId() + "\t");
			output.append(f.getLocation() + "\t");
			output.append(f.getDetector().getName() + "\t");
			output.append(f.isPartial() + "\t");
			
			if (f.getConfidence() != null) {
				output.append(String.format("%6.3e",f.getConfidence()));
			}
			
			writeAnnotations(f,usedAnnotators,output);
			output.append('\n');
			getFeatureDao().evict(f);
		}
		
		getSequenceDao().evict(sequence);
	}

	public void writeFeatures(Sample sample, List<Taxonomy> excludedTaxa, Writer output)  throws IOException, DataAccessException {
		// Preamble
		output.append("ANNOTATION TABLE\n");
		output.append("SAMPLE: " + sample.getName() + " " + sample.getDescription() + "\n");
		Project project = sample.getProject();
		output.append("PROJECT: " + project.getName() + " " + project.getDescription() + "\n");

		List<Sequence> seqList = getSequenceDao().findBySample(sample, true);
		
		// Header
		output.append("#SEQ_NAME\tFEATURE_ID\tLOCATION\tDECTECTOR\tPARTIAL\tFEATURE_conf");
		
		List<Annotator> usedAnnotators = getAnnotatorDao().findUsedBySample(sample); 
		writeAnnotatorsHeader(usedAnnotators, output);
		output.append('\n');
		
		for (Sequence seq : seqList) {
			if (excludedTaxa == null || !excludedTaxa.contains(seq.getTaxonomy())) {
				writeFeatures(seq,usedAnnotators,output);
			}
		}
	}

	public void writeFeatures(Sample sample, List<Taxonomy> excludedTaxa, File outputFile, boolean append) throws IOException, DataAccessException {
		BufferedWriter output = null;
		try {
			output = openWriter(outputFile,append);
			sample = getSampleDao().findByID(sample.getId());
			writeFeatures(sample,excludedTaxa,output);
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

	public AnnotatorDao getAnnotatorDao() {
		return annotatorDao;
	}
	public void setAnnotatorDao(AnnotatorDao annotatorDao) {
		this.annotatorDao = annotatorDao;
	}

	public AnnotationDao getAnnotationDao() {
		return annotationDao;
	}
	public void setAnnotationDao(AnnotationDao annotationDao) {
		this.annotationDao = annotationDao;
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
	
