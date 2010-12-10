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
package org.mzd.shap.util;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.transcription.TranscriptionEngine;
import org.mzd.shap.ApplicationException;

/**
 * Wraps BioJava within a simple utility class.
 * 
 */
public class DnaTools {
	
	public static class DnaToolsException extends ApplicationException {
		private static final long serialVersionUID = 5968382584129179326L;
		
		public DnaToolsException(String message) {
			super(message);
		}
		
		public DnaToolsException(Throwable cause) {
			super(cause);
		}
		
		public DnaToolsException(String message, Throwable cause) {
			super(message,cause);
		}
	}
	
	/**
	 * Translate a DNA sequence to protein using the Universal table.
	 * 
	 * @param dnaSequence dna sequence to translate
	 * @return protein sequence
	 * @throws DnaToolsException
	 */
	public static String translate(String dnaSequence) throws DnaToolsException {
		return translate(1,dnaSequence);
	}
	
	/**
	 * Translate a DNA sequence to protein.
	 * 
	 * @param tableNumber translation table number
	 * @param dnaSequence dna sequence to translate
	 * @return protein sequence
	 * @throws DnaToolsException wraps underlying exceptions
	 */
	public static String translate(int tableNumber, String dnaSequence) throws DnaToolsException {
		try {
			return new TranscriptionEngine.Builder()
				.table(tableNumber)
				.build()
					.translate(new DNASequence(dnaSequence))
						.getSequenceAsString();
		} catch (Throwable t) {
			throw new DnaToolsException(t);
		}
	}
	
	/**
	 * Reverse and complement a dna sequence.
	 * 
	 * @param dnaSequence dna sequence to revcomp
	 * @return revcomp dna sequence
	 * @throws DnaToolsException
	 */
	public static String reverseComplement(String dnaSequence) throws DnaToolsException {
		try {
			return new DNASequence(dnaSequence).getReverseComplement().getSequenceAsString();
		}
		catch (Throwable t) {
			throw new DnaToolsException(t);
		}
	}
}
