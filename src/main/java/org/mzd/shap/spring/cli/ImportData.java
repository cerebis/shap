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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.mzd.shap.ApplicationException;
import org.mzd.shap.spring.DataAdminService;
import org.mzd.shap.spring.NotFoundException;

public class ImportData extends BaseCommand {
	private DataAdminService dataAdminService;
	
	private final static Option PROJECT = CommandLineApplication.buildOption()
		.withLongName("project")
		.withDescription("Project name")
		.withRequired(true)
		.withArgument(CommandLineApplication.buildArgument()
				.withName("name")
				.withMinimum(1)
				.withMaximum(1)
				.create())
			.create();
	
	private final static Option DESCRIPTION = CommandLineApplication.buildOption()
		.withLongName("description")
		.withRequired(true)
		.withArgument(CommandLineApplication.buildArgument()
				.withName("text")
				.withMinimum(1)
				.create())
			.create();

	private final static Option SAMPLE = CommandLineApplication.buildOption()
		.withLongName("sample")
		.withDescription("Sample name")
		.withRequired(true)
		.withArgument(CommandLineApplication.buildArgument()
				.withName("name")
				.withMinimum(1)
				.withMaximum(1)
				.create())
			.create();
	
	private final static Option SEQUENCE = CommandLineApplication.buildOption()
		.withLongName("sequence")
		.withDescription("Sequence name")
		.withRequired(true)
		.withArgument(CommandLineApplication.buildArgument()
				.withName("name")
				.withMinimum(1)
				.withMaximum(1)
				.create())
			.create();
	
	private final static Option ADD_PROJECT = CommandLineApplication.buildOption()
		.withLongName("add-project")
		.withDescription("Add a new project to system")
		.withChildren(CommandLineApplication.buildGroup()
				.withRequired(true)
				.withOption(PROJECT)
				.withOption(DESCRIPTION)
				.create())
		.create();
	
	private final static Option ADD_SAMPLE = CommandLineApplication.buildOption()
		.withLongName("add-sample")
		.withDescription("Add a new sample to an existing project")
		.withChildren(CommandLineApplication.buildGroup()
				.withRequired(true)
				.withOption(PROJECT)
				.withOption(SAMPLE)
				.withOption(DESCRIPTION)
				.create())
		.create();

	private final static Option ADD_SEQUENCE = CommandLineApplication.buildOption()
		.withLongName("add-sequence")
		.withDescription("Add an empty container sequence to a sample")
		.withChildren(CommandLineApplication.buildGroup()
				.withRequired(true)
				.withOption(PROJECT)
				.withOption(SAMPLE)
				.withOption(SEQUENCE)
				.withOption(DESCRIPTION)
				.create())
		.create();

	private final static Option IMPORT_SEQUENCE = CommandLineApplication.buildOption()
		.withLongName("import-sequence")
		.withDescription("Import FASTA sequence data into a sample")
		.withArgument(CommandLineApplication.buildArgument()
				.withName("filename")
				.withDescription("Fasta DNA Sequence")
				.withMinimum(1)
				.withMaximum(1)
				.withValidator(CommandLineApplication.getExitisngFileValidator(true, false))
				.create())
		.withChildren(CommandLineApplication.buildGroup()
				.withRequired(true)
				.withOption(PROJECT)
				.withOption(SAMPLE)
				.create())
		.create(); 
	
	private final static Option IMPORT_FEATURE = CommandLineApplication.buildOption()
		.withLongName("import-feature")
		.withDescription("Import features from XML sequence definition")
		.withArgument(CommandLineApplication.buildArgument()
				.withName("filename")
				.withDescription("Sequence XML file")
				.withMinimum(1)
				.withMaximum(1)
				.withValidator(CommandLineApplication.getExitisngFileValidator(true, false))
				.create())
		.withChildren(CommandLineApplication.buildGroup()
				.withRequired(true)
				.withOption(PROJECT)
				.withOption(SAMPLE)
				.create())
		.create();
	
	private final static Option SET_COVERAGE = CommandLineApplication.buildOption()
		.withLongName("set-coverage")
		.withDescription("Set coverage data for existing sequences")
		.withArgument(CommandLineApplication.buildArgument()
				.withName("filename")
				.withDescription("Coverage CSV file")
				.withMinimum(1)
				.withMaximum(1)
				.withValidator(CommandLineApplication.getExitisngFileValidator(true, false))
				.create())
		.withChildren(CommandLineApplication.buildGroup()
				.withRequired(true)
				.withOption(PROJECT)
				.withOption(SAMPLE)
				.create())
		.create();

	private final static Option REMOVE_SEQUENCE = CommandLineApplication.buildOption()
		.withLongName("remove-sequence")
		.withDescription("Remove a list of sequences from the system")
		.withArgument(CommandLineApplication.buildArgument()
				.withName("filename")
				.withDescription("SequenceID list")
				.withMinimum(1)
				.withMaximum(1)
				.withValidator(CommandLineApplication.getExitisngFileValidator(true, false))
				.create())
		.create();

	private final static Option REMOVE_FEATURE = CommandLineApplication.buildOption()
		.withLongName("remove-feature")
		.withDescription("Remove a list of features from the system")
		.withArgument(CommandLineApplication.buildArgument()
				.withName("filename")
				.withDescription("FeatureID list")
				.withMinimum(1)
				.withMaximum(1)
				.withValidator(CommandLineApplication.getExitisngFileValidator(true, false))
				.create())
		.create();
	
	public ImportData() throws ApplicationException {
		
		Group actionGroup = CommandLineApplication.buildGroup()
			.withName("Action")
			.withDescription("Action to perform")
			.withRequired(true)
			.withMinimum(1)
			.withMaximum(1)
			.withOption(ADD_PROJECT)
			.withOption(ADD_SAMPLE)
			.withOption(ADD_SEQUENCE)
			.withOption(IMPORT_SEQUENCE)
			.withOption(IMPORT_FEATURE)
			.withOption(SET_COVERAGE)
			.withOption(REMOVE_SEQUENCE)
			.withOption(REMOVE_FEATURE)
			.create();
		
		setApp(new CommandLineApplication(actionGroup));
	}
	
	private void setCoverage(String projectName, String sampleName, File inputFile) throws IOException, NotFoundException {
		BufferedReader reader = null;
		Map<String,Double> coverageMap = new HashMap<String, Double>(); 
		try {
			System.out.println("Reading coverage table...");
			reader = new BufferedReader(new FileReader(inputFile));
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				String[] fields = line.split("\t");
				if (fields.length != 2) {
					throw new IOException("Invalid line in coverage table." +
							" Format: {seq_name}{tab}{coverage}");
				}
				coverageMap.put(fields[0], Double.parseDouble(fields[1]));
			}
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
		// Set the coverage values
		getDataAdminService().setCoverage(projectName, sampleName, coverageMap);
	}
	
	private List<Integer> readIdFile(File inputFile) throws IOException {
		List<Integer> idList = new ArrayList<Integer>();
		BufferedReader reader = null;
		try {
			System.out.println("Reading id list...");
			reader = new BufferedReader(new FileReader(inputFile));
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				idList.add(Integer.parseInt(line));
			}
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
		return idList;
	}
	
	private void removeFeature(File inputFile) throws IOException, NotFoundException {
		List<Integer> toDelete = readIdFile(inputFile);
		// Remove the features
		if (toDelete.size() > 0) {
			System.out.print(
					"Dangerous!!\n" +
					"Really remove " + toDelete.size() + " features from system forever?\n" +
					"This will cascade to child annotations\n" +
					"[y/n] ");
			char ans = (char)System.in.read();
			if (ans == 'y' || ans == 'Y') {
				System.out.println("Removing " + toDelete.size() + " from system");
				getDataAdminService().removeFeatures(toDelete);
			}
		}
	}

	private void removeSequence(File inputFile) throws IOException, NotFoundException {
		List<Integer> toDelete = readIdFile(inputFile);
		// Remove the sequences
		if (toDelete.size() > 0) {
			System.out.print(
					"Dangerous!!\n" +
					"Really remove " + toDelete.size() + " sequences from system forever?\n" +
					"This will cascade to child features and annotations\n" +
					"[y/n] ");
			char ans = (char)System.in.read();
			if (ans == 'y' || ans == 'Y') {
				System.out.println("Removing " + toDelete.size() + " from system");
				getDataAdminService().removeSequences(toDelete);
			}
		}
	}

	@Override
	public void execute(String[] args) {
		try {
			CommandLine cl = getApp().parseArguments(args);
			
			String[] xmlPath = {
					"war/WEB-INF/spring/local-datasource-context.xml",
					"war/WEB-INF/spring/service-context.xml",
					"war/WEB-INF/spring/orm-context.xml"
			};
			
			getApp().startApplication(xmlPath, true);
			
			// Add an observer to show simply progress activity.
			this.dataAdminService = (DataAdminService)getApp().getContext().getBean("dataAdminService");
			
			if (cl.hasOption(SET_COVERAGE)) {
				String projectName = (String)cl.getValue(PROJECT);
				String sampleName = (String)cl.getValue(SAMPLE);
				File inputFile = (File)cl.getValue(SET_COVERAGE);
				setCoverage(projectName,sampleName,inputFile);
			}
			else if (cl.hasOption(IMPORT_SEQUENCE)) {
				String projectName = (String)cl.getValue(PROJECT);
				String sampleName = (String)cl.getValue(SAMPLE);
				File inputFile = (File)cl.getValue(IMPORT_SEQUENCE);
				getDataAdminService().addSequences(projectName, sampleName, inputFile);
			}
			else if (cl.hasOption(ADD_PROJECT)) {
				String projectName = (String)cl.getValue(PROJECT);
				String description = CommandLineApplication.concatenateValues(cl.getValues(DESCRIPTION));
				System.out.println("Adding project [" + projectName + "] [" + description + "]");
				getDataAdminService().addProject(projectName, description);
			}
			else if (cl.hasOption(ADD_SAMPLE)) {
				String sampleName = (String)cl.getValue(SAMPLE);
				String projectName = (String)cl.getValue(PROJECT);
				String description = CommandLineApplication.concatenateValues(cl.getValues(DESCRIPTION));
				System.out.println("Adding sample [" + sampleName + "] [" + description + 
						"] to [" + projectName + "]");
				getDataAdminService().addSample(projectName, sampleName, description);
			}
			else if (cl.hasOption(ADD_SEQUENCE)) {
				String sampleName = (String)cl.getValue(SAMPLE);
				String projectName = (String)cl.getValue(PROJECT);
				String sequenceName = (String)cl.getValue(SEQUENCE);
				String description = CommandLineApplication.concatenateValues(cl.getValues(DESCRIPTION));
				System.out.println("Adding container sequence [" + sequenceName + "] [" + description + 
						"] to [" + projectName + "," + sampleName + "]");
				getDataAdminService().addSequence(projectName, sampleName, sequenceName, description);
			}
			else if (cl.hasOption(IMPORT_FEATURE)) {
				String sampleName = (String)cl.getValue(SAMPLE);
				String projectName = (String)cl.getValue(PROJECT);
				File inputFile = (File)cl.getValue(IMPORT_FEATURE);
				System.out.println("Importing features from XML to [" + projectName + "," + sampleName + "]");
				getDataAdminService().addFeatures(projectName, sampleName, inputFile);
			}
			else if (cl.hasOption(REMOVE_SEQUENCE)) {
				File inputFile = (File)cl.getValue(REMOVE_SEQUENCE);
				removeSequence(inputFile);
			}
			else if (cl.hasOption(REMOVE_FEATURE)) {
				File inputFile = (File)cl.getValue(REMOVE_FEATURE);
				removeFeature(inputFile);
			}
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	protected DataAdminService getDataAdminService() {
		return dataAdminService;
	}
	
	public static void main(String[] args) throws ApplicationException {
		new ImportData().execute(args);
		System.exit(0);
	}

}
