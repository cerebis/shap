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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

public class Fasta {
	private final static char NEW_LINE = '\n';
	private final static char HEADER_MARKER = '>';
	private final static int LINE_LENGTH = 70;
	private final static int DEFAULT_BUFFER_SIZE = 4096;
	private String header;
	private String sequence;
	
	/**
	 * A convenience method for standard formatting of header attributes.
	 * @param tag - attribute name
	 * @param value - attribute value
	 * @return standardized formatting of attribute.
	 */
	public static String formatAttribute(String tag, String value) {
		return String.format("/%s=\"%s\"", tag, value);
	}
	
	public void write(BufferedOutputStream os) throws IOException {
		// write the header
		os.write(HEADER_MARKER);
		os.write(header.getBytes());
		os.write(NEW_LINE);
		
		// write the body
		byte[] buff = sequence.getBytes();
		int n,m;
		for (n=0,m=0; n<buff.length; n+=m) {
			for (m=0; m<LINE_LENGTH && m+n<buff.length; m++) {
				os.write(buff[n+m]);
			}
			os.write(NEW_LINE);
		}
	}
	
	public void write(Writer output) throws IOException {
		output.write(HEADER_MARKER + header + NEW_LINE);
		StringReader reader = new StringReader(getSequence());
		char[] buffer = new char[LINE_LENGTH];
		int n = 0;
		while ((n = reader.read(buffer)) != -1) {
			output.write(buffer,0,n);
			output.write(NEW_LINE);
		}
	}
		
	/**
	 * Write the instance in standard format to the specified file.
	 * @param file - the destination file
	 * @param append - append to existing file (makes concatenated fasta files)
	 * @throws IOException
	 */
	@Deprecated
	public void write(File file, boolean append) throws IOException {
		BufferedOutputStream bs = null;
		try {
			bs = new BufferedOutputStream(
					new FileOutputStream(file, append),DEFAULT_BUFFER_SIZE);
			write(bs);
		}
		finally {
			if (bs != null) {
				bs.close();
			}
		}
	}
	
	@Override
	public String toString() {
		BufferedOutputStream bs = null;
		try {
			bs = new BufferedOutputStream(
					new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE),
					DEFAULT_BUFFER_SIZE);
			write(bs);
			return bs.toString();
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		finally {
			if (bs != null) {
				try {
					bs.close();
				}
				catch (IOException ex) {/*...*/}
			}
		}
	}
	
	public Fasta() {
		header = null;
		sequence = null;
	}
	
	public Fasta(String header, String sequence) {
		this.header = header;
		this.sequence = new String(sequence);
	}
	
	public String getSequence() {
		return sequence;
	}
	
	public String getHeader() {
		return header;
	}
}
