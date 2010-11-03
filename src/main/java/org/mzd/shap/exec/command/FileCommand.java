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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class FileCommand extends SimpleCommand {
	private Set<File> deletionRegistry = new HashSet<File>();
	private File input;
	private File output;
	
	@Override
	public String createCommand() {
		return String.format(getArgumentString(), 
				getInput().getPath(), getOutput().getPath());
	}
	
	public void setInput(File input) {
		removeForDeletion(getInput());
		this.input = input;
		addForDeletion(input);
	}
	public File getInput() {
		return input;
	}
	
	public void setOutput(File output) {
		removeForDeletion(getOutput());
		this.output = output;
		addForDeletion(output);
	}
	public File getOutput() {
		return output;
	}
	
	protected Set<File> getDeletionRegistry() {
		return deletionRegistry;
	}

	public boolean removeForDeletion(File file) {
		return getDeletionRegistry().remove(file);
	}
	
	public boolean addForDeletion(File file) {
		return getDeletionRegistry().add(file);
	}
	
	public void deleteFiles() throws CommandException {
		for (File f : getDeletionRegistry()) {
			if (f.exists()) {
				if (!f.delete()) {
					throw new CommandException("Failed to delete [" + f.getPath() + "]");
				}
			}
		}
	}
	
}
