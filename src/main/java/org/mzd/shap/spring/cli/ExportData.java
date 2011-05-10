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
package org.mzd.shap.spring.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.validation.EnumValidator;
import org.mzd.shap.ApplicationException;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.MoleculeType;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.Taxonomy;
import org.mzd.shap.domain.dao.FeatureDao;
import org.mzd.shap.domain.dao.SampleDao;
import org.mzd.shap.domain.dao.SequenceDao;
import org.mzd.shap.io.FastaWriter;
import org.mzd.shap.io.GenbankWriter;
//import org.mzd.shap.io.ReportWriter;
import org.mzd.shap.io.TableWriter;
import org.mzd.shap.spring.DataViewService;

public class ExportData extends BaseCommand {
	
	private final static Option FEATURE_TRANSLATE = CommandLineApplication.buildOption()
			.withLongName("translate")
			.withDescription("Translate features [coding features only]")
			.create();

	private final static Option FASTA_FEATURES = CommandLineApplication.buildOption()
			.withLongName("features")
			.withDescription("Sequence of derived features")
			.withChildren(CommandLineApplication.buildGroup()
					.withName("Options")
					.withOption(FEATURE_TRANSLATE)
					.create())
			.create();
	
	private final static Option FASTA_LONG_HEADER = CommandLineApplication.buildOption()
			.withLongName("long-header")
			.withDescription("Include additional information in headers")
			.create();
	
	private final static Option FASTA = CommandLineApplication.buildOption()
			.withLongName("fasta")
			.withDescription("Fasta format")
			.withChildren(CommandLineApplication.buildGroup()
					.withDescription("Fasta format options")
					.withName("Options")
					.withOption(FASTA_FEATURES)
					.withOption(FASTA_LONG_HEADER)
					.create())
			.create();
	
//	private final static Option HISTOGRAM_ANNOTATOR = CommandLineApplication.buildOption()
//			.withLongName("annotator")
//			.withDescription("Name of annotator")
//			.withRequired(true)
//			.withArgument(CommandLineApplication.buildArgument()
//					.withDescription("name")
//					.withMinimum(1)
//					.withMaximum(1)
//					.withName("name")
//					.create())
//			.create();
//	
//	private final static Double DEFAULT_CONFIDENCE = 1.0E-5; 
//	private final static Option HISTOGRAM_CONFIDENCE = CommandLineApplication.buildOption()
//			.withLongName("confidence")
//			.withDescription("Confidence threshold cut-off (default: " + DEFAULT_CONFIDENCE + ")")
//			.withArgument(CommandLineApplication.buildArgument()
//					.withName("float")
//					.withMinimum(1)
//					.withMaximum(1)
//					.withDefault(DEFAULT_CONFIDENCE)
//					.withValidator(CommandLineApplication.getFloatValidator())
//					.create())
//			.create();
//			
//	private final static Option HISTOGRAM = CommandLineApplication.buildOption()
//			.withLongName("histogram")
//			.withDescription("Histogram of hits for a given annotator")
//			.withChildren(CommandLineApplication.buildGroup()
//					.withDescription("Histogram options")
//					.withName("Options")
//					.withOption(HISTOGRAM_ANNOTATOR)
//					.withOption(HISTOGRAM_CONFIDENCE)
//					.create())
//			.create();
	
	private final static Option GENBANK = CommandLineApplication.buildOption()
			.withLongName("genbank")
			.withDescription("Genbank format")
			.create();
	
	private final static Option ANNOTATION = CommandLineApplication.buildOption()
			.withLongName("annotation")
			.withDescription("Annotation table")
			.create();
	
	private final static Option SAMPLE_ID = CommandLineApplication.buildOption()
			.withLongName("sample-id")
			.withDescription("Sample database ID")
			.create();
	
	private final static Option SEQUENCE_ID = CommandLineApplication.buildOption()
			.withLongName("sequence-id")
			.withDescription("Sequence database ID")
			.create();
	
	private final static Option FEATURE_ID = CommandLineApplication.buildOption()
			.withLongName("feature-id")
			.withDescription("Feature database ID")
			.create();
	
	private final static Option REF_BY_ASSOC = CommandLineApplication.buildOption()
			.withLongName("by-name")
			.withDescription("Reference by assocation [PROJECT_NAME,SAMPLE_NAME,SEQUENCE_NAME]")
			.create();

	private final static Set<String> TAXON_ARGS;
	static {
		TAXON_ARGS = new java.util.TreeSet<String>(); 
		for (Taxonomy t : Taxonomy.values()) {
			TAXON_ARGS.add(t.toString());
		}
	}
	private final static Option TAXON = CommandLineApplication.buildOption()
			.withLongName("exclude-taxons")
			.withDescription("A list of taxon names to exclude from all actions")
			.withRequired(false)
			.withArgument(CommandLineApplication.buildArgument()
					.withName("name")
					.withMinimum(1)
					.withValidator(new EnumValidator(TAXON_ARGS))
					.create())
			.create();
	
	private final static Option LIST = CommandLineApplication.buildOption()
			.withLongName("list")
			.withDescription("References from a list")
			.withArgument(CommandLineApplication.buildArgument()
					.withName("id")
					.withDescription("List of ids")
					.withMinimum(1)
					.create())
			.create();
	
	private final static Option INPUT_FILE = CommandLineApplication.buildOption()
			.withLongName("input-file")
			.withDescription("Reference IDs from a file")
			.withArgument(CommandLineApplication.buildArgument()
					.withName("filename")
					.withDescription("A file containg a list of reference ids")
					.withMinimum(1)
					.withMaximum(1)
					.withValidator(CommandLineApplication.getExitisngFileValidator(true, false))
					.create())
			.create();
	
	private final static Option OUTPUT_FILE = CommandLineApplication.buildOption()
			.withLongName("output-file")
			.withDescription("Output file name")
			.withArgument(CommandLineApplication.buildArgument()
					.withName("filename")
					.withDescription("A file to which any output will be written")
					.withMinimum(1)
					.withMaximum(1)
					.withValidator(CommandLineApplication.getNewFileValidator())
					.create())
			.create();

	public ExportData() {
		
		Group formatGroup = CommandLineApplication.buildGroup()
			.withName("Format")
//			.withRequired(true)
			.withMinimum(1)
			.withMaximum(1)
			.withOption(ANNOTATION)
			.withOption(FASTA)
			.withOption(GENBANK)
//			.withOption(HISTOGRAM)
			.create();
		
		Group referenceTypeGroup = CommandLineApplication.buildGroup()
			.withName("Reference Type")
			.withDescription("Reference type used for objects")
//			.withRequired(true)
			.withMinimum(1)
			.withMaximum(1)
			.withOption(SAMPLE_ID)
			.withOption(SEQUENCE_ID)
			.withOption(FEATURE_ID)
			.withOption(REF_BY_ASSOC)
			.create();
			
		Group sourceGroup = CommandLineApplication.buildGroup()
			.withName("Source")
			.withDescription("Reference source")
//			.withRequired(true)
			.withMinimum(1)
			.withMaximum(1)
			.withOption(INPUT_FILE)
			.withOption(LIST)
			.create();
		
		Group destinationGroup = CommandLineApplication.buildGroup()
			.withName("Output")
			.withDescription("Output destination")
//			.withRequired(true)
			.withMinimum(1)
			.withMaximum(1)
			.withOption(OUTPUT_FILE)
			.create();
		
		Group filterGroup = CommandLineApplication.buildGroup()
			.withName("Filters")
			.withDescription("Optional filters to limit output")
//			.withRequired(false)
			.withOption(TAXON)
			.create();
		
		setApp(new CommandLineApplication(
				CommandLineApplication.buildGroup()
					.withOption(referenceTypeGroup)
					.withOption(sourceGroup)
					.withOption(destinationGroup)
					.withOption(formatGroup)
					.withOption(filterGroup)
					.create()));
	}
	
	private List<String> readIdFile(File idFile) throws IOException {
		BufferedReader reader = null;
		List<String> idList = new ArrayList<String>();
		try {
			reader = new BufferedReader(new FileReader(idFile));
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				idList.add(line);
			}
			System.out.println("Read " + idList.size() + " id lines");
			return idList;
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	private void writeFasta(DomainTarget target, boolean isFeatures, boolean translate, boolean longHeader, 
			List<String> idList, List<Taxonomy> excludedTaxa, File outputFile) 
				throws ApplicationException, IOException {
		
		MoleculeType molType = translate ? MoleculeType.Protein : MoleculeType.DNA;
		
		FastaWriter writer = (FastaWriter)getApp()
			.getContext()
				.getBean("fastaWriter");

		SequenceDao sequenceDao = (SequenceDao)getApp()
			.getContext()
				.getBean("sequenceDao");

		SampleDao sampleDao = (SampleDao)getApp()
			.getContext()
				.getBean("sampleDao");

		DataViewService dataService = (DataViewService)getApp()
			.getContext()
				.getBean("dataAdminService");

		switch (target) {
		default:
			throw new ApplicationException("Unimplemented");
			
		case DEFERRED:
			
			for (String s : idList) {
				String[] fields = s.split(",");
				if (fields.length == 2) {
					// Project and sample
					Sample sample = dataService.getSample(fields[0], fields[1]);
					if (sample == null) {
						System.err.println("No object found for association [" + s + "]");
						continue;
					}
					if (isFeatures) {
						writer.writeFeatures(sample, molType, outputFile, longHeader, true, excludedTaxa);
					}
					else {
						writer.writeSequences(sample,outputFile,true,excludedTaxa);
					}
					sampleDao.evict(sample);
				}
				else if (fields.length == 3) {
					// Project, sample and sequence
					Sequence seq = dataService.getSequence(fields[0], fields[1], fields[2]);
					if (seq == null) {
						System.err.println("No object found for association [" + s + "]");
						continue;
					}
					if (!excludedTaxa.contains(seq.getTaxonomy())) {
						if (isFeatures) {
							writer.writeFeatures(seq, molType, outputFile, longHeader, true);
						}
						else {
							writer.writeSequence(seq, outputFile, true);
						}
					}
					sequenceDao.evict(seq);
				}
				else {
					System.err.println("Reference by association requires at least a project " +
						"and sample. It cannot be used to reference objects deeper than sequences.");
					continue;
				}
			}
			
			break;

		case SEQUENCE:

			for (String s : idList) {
				Integer id = Integer.parseInt(s);
				Sequence seq = sequenceDao.findById(id,true);
				if (seq == null) {
					System.err.println("Sequence [" + id + "] not found");
					continue;
				}
				if (!excludedTaxa.contains(seq.getTaxonomy())) {
					if (isFeatures) {
						writer.writeFeatures(seq, molType, outputFile, longHeader, true);
					}
					else {
						writer.writeSequence(seq, outputFile, true);
					}
				}
				sequenceDao.evict(seq);
			}
			
			break;
			
		case SAMPLE:
			
			for (String s : idList) {
				Sample sample = sampleDao.findByID(Integer.parseInt(s));
				if (sample == null) {
					System.err.println("Sample [" + s + "] not found");
					continue;
				}
				if (isFeatures) {
					writer.writeFeatures(sample, molType, outputFile, longHeader, true, excludedTaxa);
				}
				else {
					writer.writeSequences(sample,outputFile,true,excludedTaxa);
				}
				sampleDao.evict(sample);
			}
						
			break;
		}
	}
	
	private void writeAnnotation(DomainTarget target, List<String> idList,
			List<Taxonomy> excludedTaxa, File outputFile) throws ApplicationException, IOException {
		
		TableWriter writer = (TableWriter)getApp()
			.getContext()
				.getBean("tableWriter");
		
		FeatureDao featureDao = (FeatureDao)getApp()
			.getContext()
				.getBean("featureDao");

		SequenceDao sequenceDao = (SequenceDao)getApp()
			.getContext()
				.getBean("sequenceDao");

		SampleDao sampleDao = (SampleDao)getApp()
			.getContext()
				.getBean("sampleDao");

		DataViewService dataService = (DataViewService)getApp()
			.getContext()
				.getBean("dataAdminService");

		switch (target) {
		default:
			throw new ApplicationException("Unimplemented");
			
		case DEFERRED:
			
			for (String s : idList) {
				String[] fields = s.split(",");
				if (fields.length == 2) {
					// Project and sample
					Sample sample = dataService.getSample(fields[0], fields[1]);
					if (sample == null) {
						System.err.println("No object found for association [" + s + "]");
						continue;
					}
					writer.writeFeatures(sample, excludedTaxa, outputFile, true);
					sampleDao.evict(sample);
				}
				else if (fields.length == 3) {
					// Project, sample and sequence
					Sequence seq = dataService.getSequence(fields[0], fields[1], fields[2]);
					if (seq == null) {
						System.err.println("No object found for association [" + s + "]");
						continue;
					}
					if (!excludedTaxa.contains(seq.getTaxonomy())) {
						writer.writeFeatures(seq, outputFile, true);
					}
					sequenceDao.evict(seq);
				}
				else {
					System.err.println("Reference by association requires at least a project " +
						"and sample. It cannot be used to reference objects deeper than sequences.");
					continue;
				}
			}
			
			break;
		
		case FEATURE:

			List<Feature> featuresToWrite = new ArrayList<Feature>();
			for (String strId : idList) {
				Feature f = featureDao.findByID(Integer.parseInt(strId));
				if (f == null) {
					System.out.println("Feature [" + strId + "] was not found");
					continue;
				}
				featuresToWrite.add(f);
			}
			System.out.println("Writing annotations for " + featuresToWrite.size() + " features");
			writer.writeFeatures(featuresToWrite, outputFile, true);
			break;
			
		case SEQUENCE:
			
			for (String s : idList) {
				Integer id = Integer.parseInt(s);
				Sequence seq = sequenceDao.findById(id, true);
				if (seq == null) {
					System.err.println("Sequence [" + id + "] not found");
					continue;
				}
				if (!excludedTaxa.contains(seq.getTaxonomy())) {
					writer.writeFeatures(seq, outputFile, true);
				}
				sequenceDao.evict(seq);
			}
			
		case SAMPLE:
			
			for (String s: idList) {
				Sample sample = sampleDao.findByID(Integer.parseInt(s));
				if (sample == null) {
					System.err.println("Sample [" + s + "] not found");
					continue;
				}
				writer.writeFeatures(sample, excludedTaxa, outputFile, true);
				sampleDao.evict(sample);
			}
			break;
		}
	}
	
	private void writeGenbank(DomainTarget target, List<String> idList,
			List<Taxonomy> excludedTaxa, File outputFile) throws ApplicationException, IOException {
		
		GenbankWriter writer = (GenbankWriter)getApp()
			.getContext()
				.getBean("genbankWriter");
		
		SequenceDao sequenceDao = (SequenceDao)getApp()
				.getContext()
					.getBean("sequenceDao");
		
		SampleDao sampleDao = (SampleDao)getApp()
				.getContext()
					.getBean("sampleDao");

		DataViewService dataService = (DataViewService)getApp()
				.getContext()
					.getBean("dataAdminService");

		switch (target) {
		default:
			throw new ApplicationException("Unimplemented");
			
		case DEFERRED:
			
			for (String s : idList) {
				String[] fields = s.split(",");
				if (fields.length == 2) {
					// Project and sample
					Sample sample = dataService.getSample(fields[0], fields[1]);
					if (sample == null) {
						System.err.println("No object found for association [" + s + "]");
						continue;
					}
					List<Sequence> sequences = sequenceDao.findBySample(sample);
					for (Sequence seq : sequences) {
						if (!excludedTaxa.contains(seq.getTaxonomy())) {
							writer.writeFile(seq, outputFile);
						}
						sequenceDao.evict(seq);
					}
					sampleDao.evict(sample);
				}
				else if (fields.length == 3) {
					// Project, sample and sequence
					Sequence seq = dataService.getSequence(fields[0], fields[1], fields[2]);
					if (seq == null) {
						System.err.println("No object found for association [" + s + "]");
						continue;
					}
					if (!excludedTaxa.contains(seq.getTaxonomy())) {
						writer.writeFile(seq, outputFile);
					}
					sequenceDao.evict(seq);
				}
				else {
					System.err.println("Reference by association requires at least a project " +
						"and sample. It cannot be used to reference objects deeper than sequences.");
					continue;
				}
			}
			
			break;
			
		case SEQUENCE:
			for (String s : idList) {
				Integer id = Integer.parseInt(s);
				Sequence seq = sequenceDao.findById(id, true);
				if (seq == null) {
					System.err.println("Sequence [" + id + "] not found");
					continue;
				}
				if (!excludedTaxa.contains(seq.getTaxonomy())) {
					writer.writeFile(seq, outputFile);
				}
				sequenceDao.evict(seq);
			}
			
		case SAMPLE: 
			for (String s : idList) {
				Sample sample = sampleDao.findByID(Integer.parseInt(s));
				if (sample == null) {
					System.err.println("Sample [" + s + "] not found");
				}
				List<Sequence> sequences = sequenceDao.findBySample(sample);
				for (Sequence seq : sequences) {
					if (!excludedTaxa.contains(seq.getTaxonomy())) {
						writer.writeFile(seq, outputFile);
					}
					sequenceDao.evict(seq);
				}
				sampleDao.evict(sample);
			}
			break;
		}
	}

//	private void writeHistogram(DomainTarget target, String annotatorName, List<String> idList, 
//			Double confidence, List<Taxonomy> excludedTaxa, File outputFile)
//				throws ApplicationException, IOException {
//		
//		ReportWriter writer = (ReportWriter)getApp()
//			.getContext()
//				.getBean("annotatorHistographicReportWriter");
//		
//		switch (target) {
//		default:
//			throw new ApplicationException("Unimplemented");
//			
//		case SAMPLE:
//			SampleDao sampleDao = (SampleDao)getApp()
//				.getContext()
//					.getBean("sampleDao");
//			
//			for (String s: idList) {
//				Sample sample = sampleDao.findByID(Integer.parseInt(s));
//				writer.write(outputFile, sample, annotatorName, confidence, excludedTaxa);
//				sampleDao.evict(sample);
//			}
//			break;
//		}
//	}

	@Override
	@SuppressWarnings("unchecked")
	public void execute(String[] args) {
		try {
			CommandLine cl = getApp().parseArguments(args);
		
			String[] xmlPath = {
					"datasource-context.xml",
					"service-context.xml",
					"orm-context.xml"
			};
			
			getApp().startApplication(xmlPath, true);

			// Target
			DomainTarget target = null;
			if (cl.hasOption(SAMPLE_ID)) {
				target = DomainTarget.SAMPLE;
			}
			else if (cl.hasOption(SEQUENCE_ID)) {
				target = DomainTarget.SEQUENCE;
			}
			else if (cl.hasOption(FEATURE_ID)) {
				target = DomainTarget.FEATURE;
			}
			else if (cl.hasOption(REF_BY_ASSOC)) {
				target = DomainTarget.DEFERRED;
			}
			
			// Taxonomy filter
			List<Taxonomy> excludedTaxa = new ArrayList<Taxonomy>();
			if (cl.hasOption(TAXON)) {
				for (Object obj : cl.getValues(TAXON)) {
					try {
						excludedTaxa.add(
								Taxonomy.valueOf(((String)obj).toUpperCase()));
					}
					catch (IllegalArgumentException ex) {
						throw new ApplicationException("Unknown taxon name [" + obj + "]",ex);
					}
				}
			}
			
			// Source
			List<String> idList = null;
			if (cl.hasOption(LIST)) {
				idList = cl.getValues(LIST);
			}
			else if (cl.hasOption(INPUT_FILE)) {
				idList = readIdFile((File)cl.getValue(INPUT_FILE));
			}
			
			// Destination
			File outputFile = (File)cl.getValue(OUTPUT_FILE);
			if (outputFile.exists()) {
				throw new IOException("Output file [" + 
						outputFile.getPath() + "] already exists");
			}
			
			// Format
			if (cl.hasOption(ANNOTATION)) {
				writeAnnotation(
						target, 
						idList, 
						excludedTaxa, 
						outputFile);
			}
			else if (cl.hasOption(FASTA)) {
				writeFasta(
						target, 
						cl.hasOption(FASTA_FEATURES), 
						cl.hasOption(FEATURE_TRANSLATE), 
						cl.hasOption(FASTA_LONG_HEADER), 
						idList, 
						excludedTaxa, 
						outputFile);
			}
			else if (cl.hasOption(GENBANK)) {
				writeGenbank(target, idList, excludedTaxa, outputFile);
			}
//			else if (cl.hasOption(HISTOGRAM)) {
//				writeHistogram(
//						target, 
//						(String)cl.getValue(HISTOGRAM_ANNOTATOR), 
//						idList, 
//						(Double)cl.getValue(HISTOGRAM_CONFIDENCE), 
//						excludedTaxa, 
//						outputFile);
//			}
		}
		catch (Throwable t) {
			System.err.println(t.getMessage());
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		new ExportData().execute(args);
		System.exit(0);
	}

}
