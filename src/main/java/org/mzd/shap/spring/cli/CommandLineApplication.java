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

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.List;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.util.HelpFormatter;
import org.apache.commons.cli2.validation.FileValidator;
import org.apache.commons.cli2.validation.NumberValidator;
import org.mzd.shap.ApplicationException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class CommandLineApplication {
	private AbstractApplicationContext context = null;
	private Parser clParser;
	
	protected CommandLineApplication(Group optionGroup) {
		this.clParser = new Parser();
		this.clParser.setHelpFormatter(new HelpFormatter());
		this.clParser.setHelpTrigger("--help");
		this.clParser.setGroup(optionGroup);
	}
	
	public CommandLine parseArguments(String[] args) {
		CommandLine cl = clParser.parseAndHelp(args);
		if (cl == null) {
			System.exit(1);
		}
		return cl;
	}
	
	public void startApplication(String[] xmlPaths, boolean registerShutdownHook) throws ApplicationException {
		if (context == null) {
			System.out.println("Starting application");
			context = new FileSystemXmlApplicationContext(xmlPaths);
			if (registerShutdownHook) {
				context.registerShutdownHook();
			}
		}
		else {
			throw new ApplicationException("Application has already been started");
		}
	}
	
	public void shutdownApplication() throws ApplicationException {
		if (context == null) {
			throw new ApplicationException("ApplicationContext is null -- start application first");
		}
		context.stop();
	}
	
	public AbstractApplicationContext getContext() throws ApplicationException {
		if (context == null) {
			throw new ApplicationException("ApplicationContext is null -- start application first");
		}
		return context;
	}
	
	public static DefaultOptionBuilder buildOption() {
		return new DefaultOptionBuilder();
	}
	
	public static ArgumentBuilder buildArgument() {
		return new ArgumentBuilder();
	}
	
	public static GroupBuilder buildGroup() {
		return new GroupBuilder();
	}

	@SuppressWarnings("unchecked")
	public static String concatenateValues(List values) {
		String first = (String)values.get(0);
		for (int i=1; i<values.size(); i++) {
			first += " " + values.get(i);
		}
		return first;
	}
	
	static class IntegerNumberFormat extends NumberFormat {
		static final long serialVersionUID = -194597105611788096L;
		
		@Override
		public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
			return getIntegerInstance().format(number, toAppendTo, pos);
		}
		
		@Override
		public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
			return getIntegerInstance().format(number, toAppendTo, pos);
		}
		
		@Override
		public Number parse(String source, ParsePosition parsePosition) {
			Number num = getIntegerInstance().parse(source, parsePosition);
			if (num == null) {
				throw new RuntimeException("Source [" + source + "] was not an integer when parsed");
			}
			return new Integer(num.intValue());
		}
	}

	public static NumberValidator getIntegerValidator() {
		return new NumberValidator(new IntegerNumberFormat());
	}
	
	static class DoubleNumberFormat extends DecimalFormat {
		static final long serialVersionUID = 2252708271723915070L;
		
		@Override
		public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
			return getInstance().format(number, toAppendTo, pos);
		}
		
		@Override
		public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
			return getInstance().format(number, toAppendTo, pos);
		}
		
		@Override
		public Number parse(String source, ParsePosition parsePosition) {
			Number num = getNumberInstance().parse(source, parsePosition);
			if (num == null) {
				throw new RuntimeException("Source [" + source + "] was not a floating-point number when parsed");
			}
			return num.doubleValue();
		}
	}

	public static NumberValidator getFloatValidator() {
		return new NumberValidator(new DoubleNumberFormat());
	}
	
	public static FileValidator getExitisngFileValidator(boolean isReadable, boolean isWritable) {
		FileValidator fv = FileValidator.getExistingFileInstance();
		fv.setReadable(isReadable);
		fv.setWritable(isWritable);
		return fv;
	}
	
	public static FileValidator getExistingDirectoryValidator(boolean isReadable, boolean isWritable) {
		FileValidator fv = FileValidator.getExistingDirectoryInstance();
		fv.setReadable(isReadable);
		fv.setWritable(isWritable);
		return fv;
	}
	
	public static FileValidator getNewFileValidator() {
		return getFileValidator(false,false,false,false,false,false);
	}
	
	public static FileValidator getFileValidator(boolean isDirectory, boolean isFile, boolean isExisting,
			boolean isHidden, boolean isReadable, boolean isWritable) {
		if (isDirectory && isFile) {
			throw new RuntimeException("Objects cannot simultaneously be a directory and a file");
		}
		FileValidator fv = new FileValidator();
		fv.setDirectory(isDirectory);
		fv.setExisting(isExisting);
		fv.setFile(isFile);
		fv.setHidden(isHidden);
		fv.setReadable(isReadable);
		fv.setWritable(isWritable);
		return fv;
	}
	
}
