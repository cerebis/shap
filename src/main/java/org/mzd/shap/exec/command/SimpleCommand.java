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
package org.mzd.shap.exec.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mzd.shap.exec.command.Command;


public class SimpleCommand implements Command {
	private static final String SPLIT_PATTERN = "(?<!\\\\) +(?![^\"]+\")";
	private String argumentString;
	
	public String createCommand() {
		return getArgumentString(); 
	}
	
	public List<String> createCommandAsList() {
		List<String> argList = new ArrayList<String>();
		String[] args = createCommand().split(SPLIT_PATTERN);
		argList.addAll(Arrays.asList(args));
		return argList;
	}

	public String getArgumentString() {
		return argumentString;
	}
	public void setArgumentString(String argumentString) {
		this.argumentString = argumentString;
	}
	
}
