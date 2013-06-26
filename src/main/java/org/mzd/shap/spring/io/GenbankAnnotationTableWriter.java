package org.mzd.shap.spring.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

import org.mzd.shap.analysis.Annotator;
import org.mzd.shap.analysis.AnnotatorDao;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.AnnotationType;
import org.mzd.shap.domain.DataAccessException;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.SequenceException;
import org.mzd.shap.domain.Taxonomy;
import org.mzd.shap.domain.dao.AnnotationDao;
import org.mzd.shap.domain.dao.FeatureDao;
import org.mzd.shap.domain.dao.SequenceDao;
import org.mzd.shap.io.TableWriter;

public class GenbankAnnotationTableWriter implements TableWriter {
	private SequenceDao sequenceDao;
	private FeatureDao featureDao;
	private AnnotatorDao annotatorDao;
	private AnnotationDao annotationDao;
	private String proteinAnnotatorName; 
	private String trnaAnnotatorName;
	private String rrnaAnnotatorName;
	private DescriptionEditor proteinEditor;
	private DescriptionEditor trnaEditor;
	private DescriptionEditor rrnaEditor;
	
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
	
	@Override
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

	@Override
	public void writeFeatures(Sequence sequence, Writer output) throws IOException, DataAccessException {
		
		sequence = getSequenceDao().findByID(sequence.getId());

		Annotator proteinAnnotator = getAnnotatorDao().findByField("name", getProteinAnnotatorName());
		Annotator trnaAnnotator = getAnnotatorDao().findByField("name", getTrnaAnnotatorName());
		Annotator rrnaAnnotator = getAnnotatorDao().findByField("name", getRrnaAnnotatorName());

		output.append(String.format(">Features %s Table\n",sequence.getId()));
		
		for (Feature f :  getFeatureDao().findResolvedSet(sequence, null)) {
			
			String start = Integer.toString(f.getLocation().getStart() + 1);
			String end = Integer.toString(f.getLocation().getEnd() + 1);

			if (f.isPartial()) {
				// Gene continues off sequence to left
				if (f.getLocation().getStart() <= 2) {
					start = (f.getLocation().isReverseStrand() ? '>' : '<') + start;
				}
				// Gene continues off sequence to right
				else if (f.getLocation().getEnd() >= sequence.getDataLength()-2) {
					end = (f.getLocation().isReverseStrand() ? '<' : '>') + end;
				}
				// Internal partial gene 
				else {
					// we ignore internal partials as they straddle gaps and are very likely
					// to be garbage.
					continue;
				}
			}
			
			if (f.getLocation().isReverseStrand()) {
				String tmp = start;
				start = end;
				end = tmp;
			}

			output.append(String.format("%s\t%s\tgene\n",start,end));
			output.append(String.format("\t\t\tlocus_tag\t%d\n",f.getId()));
			
			switch (f.getType()) {
				case OpenReadingFrame: {
					output.append(String.format("%s\t%s\tCDS\n",start,end));
					Annotation a = getAnnotationDao()
						.findByAnnotatorAndFeature(proteinAnnotator, f, AnnotationType.Product);
					String product = "hypothetical protein";
					if (a != null) {
						product = getProteinEditor().getMinimalDescription(a.getDescription());
					}
					output.append(String.format("\t\t\tproduct\t%s\n",product));
					break; 
				}
				case TransferRNA: {
					output.append(String.format("%s\t%s\ttRNA\n",start,end));
					Annotation a = getAnnotationDao()
						.findByAnnotatorAndFeature(trnaAnnotator, f, AnnotationType.Product);
					if (a != null) {
						output.append(String.format("\t\t\tproduct\t%s\n",
								getTrnaEditor().getMinimalDescription(a.getDescription())));
					}
					break;
				}
				case RibosomalRNA: {
					output.append(String.format("%s\t%s\trRNA\n",start,end));
					Annotation a = getAnnotationDao()
						.findByAnnotatorAndFeature(rrnaAnnotator, f, AnnotationType.Product);
					if (a != null) {
						output.append(String.format("\t\t\tproduct\t%s\n",
								getTrnaEditor().getMinimalDescription(a.getDescription())));
					}
					break;
				}
			}
		}
	}

	/**
	 * Not implementing Sample and Collection<Feature> methods.
	 */
	@Override
	public void writeFeatures(Sample sample, List<Taxonomy> excludedTaxa, Writer output) throws IOException, DataAccessException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeFeatures(Sample sample, List<Taxonomy> excludedTaxa,
			File outputFile, boolean append) throws IOException,
			DataAccessException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeFeatures(Collection<Feature> features, Writer output)
			throws IOException, DataAccessException, SequenceException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeFeatures(Collection<Feature> features, File outputFile,
			boolean append) throws IOException, DataAccessException,
			SequenceException {
		throw new RuntimeException("Not implemented");
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

	public String getProteinAnnotatorName() {
		return proteinAnnotatorName;
	}
	public void setProteinAnnotatorName(String proteinAnnotatorName) {
		this.proteinAnnotatorName = proteinAnnotatorName;
	}

	public String getTrnaAnnotatorName() {
		return trnaAnnotatorName;
	}
	public void setTrnaAnnotatorName(String trnaAnnotatorName) {
		this.trnaAnnotatorName = trnaAnnotatorName;
	}

	public String getRrnaAnnotatorName() {
		return rrnaAnnotatorName;
	}
	public void setRrnaAnnotatorName(String rrnaAnnotatorName) {
		this.rrnaAnnotatorName = rrnaAnnotatorName;
	}

	public DescriptionEditor getProteinEditor() {
		return proteinEditor;
	}
	public void setProteinEditor(DescriptionEditor proteinEditor) {
		this.proteinEditor = proteinEditor;
	}

	public DescriptionEditor getTrnaEditor() {
		return trnaEditor;
	}
	public void setTrnaEditor(DescriptionEditor trnaEditor) {
		this.trnaEditor = trnaEditor;
	}

	public DescriptionEditor getRrnaEditor() {
		return rrnaEditor;
	}
	public void setRrnaEditor(DescriptionEditor rrnaEditor) {
		this.rrnaEditor = rrnaEditor;
	}
}
