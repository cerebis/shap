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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.mzd.shap.ApplicationException;

/**
 * Basic utilities for DNA sequences represented as Strings.
 * 
 * Originally this class simply wrapped calls to Biojava, but I have found that the
 * Biojava dependency is more trouble than it is worth. Concurrency issues with
 * TranscriptionEngine have forced me to eliminate it from the stack.
 * 
 * Translation now uses a hard-coded instance of the IUPAC Universal transcription table.
 * 
 */
public class DnaTools {
	
	//  IUPAC UNIVERSAL TABLE
	//	AAs    = FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG
	//	Starts = ---M---------------M---------------M----------------------------
	//	Base1  = TTTTTTTTTTTTTTTTCCCCCCCCCCCCCCCCAAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGG
	//	Base2  = TTTTCCCCAAAAGGGGTTTTCCCCAAAAGGGGTTTTCCCCAAAAGGGGTTTTCCCCAAAAGGGG
	//	Base3  = TCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAG
	private final static Map<String,String> DNA_TO_PROTEIN = 
			Collections.unmodifiableMap(new HashMap<String,String>() {
				{
					put("TTT", "F");
					put("TTC", "F");
					put("TTA", "L");
					put("TTG", "L");
					put("TCT", "S");
					put("TCC", "S");
					put("TCA", "S");
					put("TCG", "S");
					put("TAT", "Y");
					put("TAC", "Y");
					put("TAA", "*");
					put("TAG", "*");
					put("TGT", "C");
					put("TGC", "C");
					put("TGA", "*");
					put("TGG", "W");
					put("CTT", "L");
					put("CTC", "L");
					put("CTA", "L");
					put("CTG", "L");
					put("CCT", "P");
					put("CCC", "P");
					put("CCA", "P");
					put("CCG", "P");
					put("CAT", "H");
					put("CAC", "H");
					put("CAA", "Q");
					put("CAG", "Q");
					put("CGT", "R");
					put("CGC", "R");
					put("CGA", "R");
					put("CGG", "R");
					put("ATT", "I");
					put("ATC", "I");
					put("ATA", "I");
					put("ATG", "M");
					put("ACT", "T");
					put("ACC", "T");
					put("ACA", "T");
					put("ACG", "T");
					put("AAT", "N");
					put("AAC", "N");
					put("AAA", "K");
					put("AAG", "K");
					put("AGT", "S");
					put("AGC", "S");
					put("AGA", "R");
					put("AGG", "R");
					put("GTT", "V");
					put("GTC", "V");
					put("GTA", "V");
					put("GTG", "V");
					put("GCT", "A");
					put("GCC", "A");
					put("GCA", "A");
					put("GCG", "A");
					put("GAT", "D");
					put("GAC", "D");
					put("GAA", "E");
					put("GAG", "E");
					put("GGT", "G");
					put("GGC", "G");
					put("GGA", "G");
					put("GGG", "G");
			}});

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
	 * @param dnaSequence DNA sequence to translate
	 * @return protein sequence
	 * @throws DnaToolsException wraps underlying exceptions
	 */
	public static String translate(String dnaSequence) throws DnaToolsException {
		if (dnaSequence.equals("")) {
			return ""; 
		}
		
		int i = 0;
		int seqlen = dnaSequence.length();
		StringBuffer protein = new StringBuffer(seqlen/3+1);
		while (true) {
			if (i+3 > seqlen) {
				break;
			}
			String codon = dnaSequence.substring(i, i+3).toUpperCase();
			String aa;
			if (codon.contains("N")) {
				aa = "X";
			}
			else {
				aa = DNA_TO_PROTEIN.get(codon);
				if (aa == null) {
					throw new DnaToolsException("Unrecognized codon [" + codon + "] at position [" + i + "]");
				}
			}
			protein.append(aa);
			i+=3;
		}
		return protein.toString();
	}
	
	/**
	 * Reverse and complement a dna sequence.
	 * 
	 * @param dnaSequence dna sequence to revcomp
	 * @return revcomp dna sequence
	 */
	public static String reverseComplement(String dnaSequence) {
		if (dnaSequence.length() == 0 || dnaSequence.equals("")) {
			return dnaSequence;
		}
		char[] seq = dnaSequence.toUpperCase().toCharArray();
		char[] rc = new char[seq.length];
		for (int i=seq.length - 1, j=0; i>=0; i--, j++) {
			switch (seq[i]) {
			case 'A' : rc[j] = 'T'; break;
			case 'C' : rc[j] = 'G'; break;
			case 'G' : rc[j] = 'C'; break;
			case 'T' : rc[j] = 'A'; break;
			default: rc[j] = seq[i];
			}
		}
		return new String(rc); 
	}
}
