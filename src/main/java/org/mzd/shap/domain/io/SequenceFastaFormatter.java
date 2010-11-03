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
package org.mzd.shap.domain.io;

import java.io.IOException;
import java.io.Writer;
import java.io.Reader;

import org.mzd.shap.domain.Sequence;
import org.mzd.shap.io.TextFormatter;


public class SequenceFastaFormatter extends TextFormatter<Sequence> {
	private final static int COLUMN_WIDTH = 80;
	private final static char HEADER_SYMBOL = '>';
	
	/**
	 * Write the supplied {@link Sequence} to the output {@link Writer} in Fasta format.
	 * <p>
	 * This method should be thread safe for concurrent access on the output Writer.
	 * 
	 * @param output - the output {@link Writer}
	 * @param input - the input instance of {@link Sequence}
	 * @throws IOException
	 */
	public void write(Writer output, Sequence input) throws IOException {
		
		synchronized (output) {

			StringBuffer header = new StringBuffer();
			header.append(input.getQueryId());
			if (input.getDescription() != null) {
				header.append(" " + input.getDescription());
			}
			output.write(HEADER_SYMBOL + header.toString() + '\n');
			
			Reader dataReader = null; 
			try {
				dataReader = input.getDataStream();
			
				char[] buff = new char[COLUMN_WIDTH];
				while (true) {
					int nch = dataReader.read(buff);
					if (nch == -1) {
						break;
					}
					output.write(buff, 0, nch);
					output.write('\n');
				}
			}
			finally {
				if (dataReader != null) {
					dataReader.close();
				}
			}
		}
	}
}
