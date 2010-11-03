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
package org.mzd.shap.analysis;

import java.io.File;
import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.mzd.shap.exec.Executable;
import org.mzd.shap.exec.ExecutableException;
import org.mzd.shap.exec.SimpleExecutable;
import org.mzd.shap.exec.command.CommandException;
import org.mzd.shap.exec.command.FileCommand;
import org.mzd.shap.util.TemporaryFileFactory;

/**
 * Provides the necessary functionality to carry out analyses via input and
 * output files. This is a common use case for bioinformatics tools.
 * <p>
 * Concrete implementations must implement the method {@link #parseOutput(File, Object...)}
 * <p>
 * Instances must reference a parser by its class {@link #parserClasse} which 
 * will used to parse the output file.
 * 
 * @param <TARGET>
 * @param <RESULT>
 */
@Entity
@TypeDef(name="file",typeClass=org.mzd.shap.hibernate.FileUserType.class)
public abstract class SimpleAnalyzer<TARGET,RESULT> extends AbstractAnalyzer<TARGET,RESULT> {
	private final static String INPUT_SUFFIX = ".in";
	private final static String OUTPUT_SUFFIX = ".out";
	private static TemporaryFileFactory fileFactory = new TemporaryFileFactory();
	@Type(type="text")
	@NotNull
	private String argumentString;
	@Type(type="file")
	@Column(length=4096)
	@NotNull
	private File scratchPath;
	@Column(length=4096)
	@NotNull
	private Class<?> parserClass;

	/**
	 * Parse the output file and return the result.
	 * @param output - the resulting output file from analysis
	 * @param target - the target of the analysis
	 * 
	 * @return the result as type {@link RESULT}
	 * @throws ExecutableException
	 */
	abstract protected RESULT[] parseOutput(File output, TARGET... target) throws ExecutableException;
	
	/**
	 * Instantiate the parser from its class.
	 * 
	 * @return an instance of the parser.
	 * @throws ExecutableException
	 */
	protected Object createParser() throws ExecutableException {
		try {
			Class<?> clazz = getParserClass();
			return clazz.newInstance();
		}
		catch (InstantiationException ex) {
			throw new ExecutableException(ex);
		}
		catch (IllegalAccessException ex) {
			throw new ExecutableException(ex);
		}
	}

	/**
	 * Prepare the input file prior to performing analysis. If additional
	 * steps are required (such as writing data to inputFile, concrete 
	 * class should override this method.
	 * <p>
	 * By default input files are randomly named temporary files. It is possible
	 * that a name class could occur, though quite unlikely.
	 *  
	 * @param target - the target of analysis
	 * @return the input file
	 * @throws ExecutableException
	 */
	protected File stageInputFile(TARGET... target) throws ExecutableException {
		try {
			return getFileFactory().createTemporaryFile(getName(), INPUT_SUFFIX, getScratchPath());
		}
		catch (IOException ex) {
			throw new ExecutableException(ex);
		}
	}
	
	/**
	 * Prepare the output file prior to performing analysis. If any additional
	 * steps are required, concrete class can override this method.
	 * <p>
	 * By default output files are randomly named temporary files. It is possible
	 * that a name class could occur, though quite unlikely.
	 * <p>
	 * The output file is created immediately so as to claim that file name
	 * until the completion of work. It can be the case that an external tool
	 * will refuse to overwrite a pre-existing file. In such cases a script could
	 * be used to delete the physical file just prior to invoking the tool. This,
	 * however, is a quite brittle solution. 
	 * 
	 * @param target - the target of analysis
	 * @return the input file
	 * @throws ExecutableException
	 */
	protected File stageOutputFile(TARGET... target) throws ExecutableException {
		try {
			return getFileFactory().createTemporaryFile(getName(), OUTPUT_SUFFIX, getScratchPath());
		}
		catch (IOException ex) {
			throw new ExecutableException(ex);
		}
	}
	
	/**
	 * Build the command object which will be used to invoke the analysis.
	 * 
	 * @param target - the target of analysis
	 * @return the command object.
	 * @throws ExecutableException
	 */
	protected FileCommand prepareCommand(TARGET... target) throws ExecutableException {
		FileCommand command = new FileCommand();
		command.setArgumentString(getArgumentString());
		command.setInput(stageInputFile(target));
		command.setOutput(stageOutputFile(target));
		return command;
	}
	
	/**
	 * Perform the analysis on the target object {@link TARGET}. This is carried
	 * out by the provided instance of {@link Executable} by way of the {@link BaseCommand}.
	 */
	public RESULT[] analyze(Executable exec, TARGET... target) throws AnalyzerException {
		
		// Can this analyzer handle the requested batched set
		if (target.length>1 && !supportsBatching()) {
			throw new AnalyzerException("[" + this + "] does not support batching");
		}
		
		FileCommand command = null;
		try {
			command = prepareCommand(target);

			// This is ugly
			((SimpleExecutable)exec).setCommand(command);
			exec.run();
			
			return parseOutput(command.getOutput(), target);
		}
		catch (ExecutableException ex) {
			getLogger().error("Exception while analyzing target", ex);
			throw new AnalyzerException(ex);
		}
		finally {
			if (command != null) {
				try {
					command.deleteFiles();
				}
				catch (CommandException ex) {
					getLogger().warn(ex);
				}
			}
		}
	}
	
	/**
	 * Utility for creating temporary files.
	 * @return an instance of File point to a temporary file.
	 */
	protected static TemporaryFileFactory getFileFactory() {
		return fileFactory;
	}
	
	// Persistent
	public String getArgumentString() {
		return argumentString;
	}
	public void setArgumentString(String argumentString) {
		this.argumentString = argumentString;
	}

	public File getScratchPath() {
		return scratchPath;
	}
	public void setScratchPath(File scratchPath) {
		this.scratchPath = scratchPath;
	}

	public Class<?> getParserClass() {
		return parserClass;
	}
	public void setParserClass(Class<?> parserClass) {
		this.parserClass = parserClass;
	}
	
}
