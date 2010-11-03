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
package org.mzd.shap.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FastaReader {
	private final static int BUFFER_SIZE = 65536;
	private BufferedReader reader;
	private String lastLine = null;
	
	public FastaReader(String fileName) throws IOException {
		this.reader = new BufferedReader(new FileReader(fileName));
	}
	
	public FastaReader(File file) throws IOException {
		this.reader = new BufferedReader(new FileReader(file));
	}
	
	protected String getLine() throws IOException {
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				return null;
			}
			line = line.trim();
			if (line.length() > 0) {
				return line;
			}
		}
	}
	
	public void close() throws IOException {
		reader.close();
	}
	
	public Fasta readFasta() throws IOException {
		
		if (lastLine == null) {
			lastLine = getLine();
			if (lastLine == null) {
				return null;
			}
		}
		
		String header = null;
		StringBuffer sequence = new StringBuffer(BUFFER_SIZE);
		while (true) {
			
			if (lastLine.startsWith(">")) {
				header = lastLine.substring(1);
			}
			else {
				sequence.append(lastLine);
			}
			
			String nextLine = getLine();
			
			if (nextLine == null || nextLine.startsWith(">")) {
				lastLine = nextLine;
				return new Fasta(header,sequence.toString());
			}
			else {
				lastLine = nextLine;
			}
		}
	}
}
