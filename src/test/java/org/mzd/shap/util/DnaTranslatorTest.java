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

import junit.framework.Assert;

import org.junit.Test;
import org.mzd.shap.util.DnaTools;
import org.mzd.shap.util.DnaTools.DnaToolsException;

public class DnaTranslatorTest {
	
	private static String protein = "MFEKMDQNDLEDYAEQIITGSEVSDENIRAMLQISDPDDMEKLHYVARHIRDNFFGNKVFMYSFVYFSTH" +
						"CKNNCAFCYYNRENDIERYRLTLEDIKKICQVLKTEEIHMVDLTMGEDPYFHNNPERLAELVRTVKEEVG" +
						"KPIMISPGVVDNETLMLLKENGANFLALYQETYDKELFGKLRVEQSFEERINSRNHAKRIGYLVEDGILT" +
						"AVEPDIESTLISLRGLGTSNPDMVRVMTFLPQKGTPLEGKDVEGSEAELRMISILRLMYPNLLIPASLDL" +
						"EGIDGMVHRLNSGANVVTSIISSNSALEGVVNYDREHAERDRDVKSVIYRLKTMGMEPAKQSDFEKLLGQ*";

	private static String gene = "ATGTTTGAGAAAATGGACCAAAATGATCTGGAAGACTATGCTGAGCAGATAATTACCGGATCAGAGGTTTCCG" +
			"ATGAGAACATCAGGGCAATGCTTCAGATAAGCGATCCTGATGATATGGAAAAACTGCATTATGTGGCACGCCA" +
			"CATACGGGATAATTTCTTTGGGAACAAAGTGTTCATGTACAGTTTCGTGTACTTTTCCACACACTGCAAGAAC" +
			"AATTGCGCTTTCTGTTATTACAACCGTGAGAATGACATCGAGAGGTATCGCCTGACACTTGAGGACATAAAGA" +
			"AGATATGCCAGGTCCTCAAGACAGAAGAGATACACATGGTAGACCTCACCATGGGAGAGGACCCCTATTTCCA" +
			"TAACAATCCTGAACGTCTGGCAGAACTTGTCAGAACAGTGAAGGAAGAAGTGGGAAAACCAATAATGATATCC" +
			"CCGGGAGTTGTTGATAATGAGACCCTTATGCTGTTGAAGGAGAACGGGGCAAACTTCCTTGCACTATACCAGG" +
			"AGACCTATGACAAGGAGCTTTTTGGAAAGCTGAGGGTTGAACAATCCTTTGAGGAAAGGATCAACTCCAGGAA" +
			"CCATGCAAAAAGGATAGGTTATCTTGTTGAAGATGGGATCCTAACAGCAGTGGAACCGGATATAGAATCCACA" +
			"CTTATCTCACTGAGAGGACTTGGAACATCCAATCCCGATATGGTAAGGGTCATGACATTTTTGCCTCAAAAAG" +
			"GCACACCTCTGGAAGGAAAAGATGTTGAGGGAAGTGAAGCGGAACTCAGGATGATCTCAATACTTCGATTGAT" +
			"GTATCCAAACCTACTCATTCCGGCATCACTTGACCTTGAAGGTATTGACGGAATGGTGCACAGGCTAAACTCC" +
			"GGTGCCAATGTGGTCACCTCCATAATCTCATCCAATTCAGCACTTGAAGGTGTTGTGAACTATGACAGAGAAC" +
			"ATGCTGAGAGGGACAGGGATGTTAAGAGCGTTATCTACCGACTGAAGACAATGGGAATGGAACCTGCTAAACA" +
			"AAGCGATTTCGAGAAGTTGTTGGGGCAATAG";

	private static String rcgene = "CTATTGCCCCAACAACTTCTCGAAATCGCTTTGTTTAGCAGGTTCCATTCCCATTGTCTTCAGTCGGTAGATAACGCTCT" + 
			"TAACATCCCTGTCCCTCTCAGCATGTTCTCTGTCATAGTTCACAACACCTTCAAGTGCTGAATTGGATGAGATTATGGAG" + 
			"GTGACCACATTGGCACCGGAGTTTAGCCTGTGCACCATTCCGTCAATACCTTCAAGGTCAAGTGATGCCGGAATGAGTAG" + 
			"GTTTGGATACATCAATCGAAGTATTGAGATCATCCTGAGTTCCGCTTCACTTCCCTCAACATCTTTTCCTTCCAGAGGTG" + 
			"TGCCTTTTTGAGGCAAAAATGTCATGACCCTTACCATATCGGGATTGGATGTTCCAAGTCCTCTCAGTGAGATAAGTGTG" + 
			"GATTCTATATCCGGTTCCACTGCTGTTAGGATCCCATCTTCAACAAGATAACCTATCCTTTTTGCATGGTTCCTGGAGTT" + 
			"GATCCTTTCCTCAAAGGATTGTTCAACCCTCAGCTTTCCAAAAAGCTCCTTGTCATAGGTCTCCTGGTATAGTGCAAGGA" + 
			"AGTTTGCCCCGTTCTCCTTCAACAGCATAAGGGTCTCATTATCAACAACTCCCGGGGATATCATTATTGGTTTTCCCACT" + 
			"TCTTCCTTCACTGTTCTGACAAGTTCTGCCAGACGTTCAGGATTGTTATGGAAATAGGGGTCCTCTCCCATGGTGAGGTC" + 
			"TACCATGTGTATCTCTTCTGTCTTGAGGACCTGGCATATCTTCTTTATGTCCTCAAGTGTCAGGCGATACCTCTCGATGT" + 
			"CATTCTCACGGTTGTAATAACAGAAAGCGCAATTGTTCTTGCAGTGTGTGGAAAAGTACACGAAACTGTACATGAACACT" + 
			"TTGTTCCCAAAGAAATTATCCCGTATGTGGCGTGCCACATAATGCAGTTTTTCCATATCATCAGGATCGCTTATCTGAAG" + 
			"CATTGCCCTGATGTTCTCATCGGAAACCTCTGATCCGGTAATTATCTGCTCAGCATAGTCTTCCAGATCATTTTGGTCCA" + 
			"TTTTCTCAAACAT";
	
	@Test
	public void testTranslateEmpty() throws DnaToolsException {
		Assert.assertEquals("empty string", "", DnaTools.translate(""));
	}

	@Test(expected=java.lang.NullPointerException.class)
	public void testTranslateNull() throws DnaToolsException {
		DnaTools.translate(null);
	}
	
	@Test
	public void startTranslation() throws DnaToolsException {
		Assert.assertEquals("translate start codon", "M", DnaTools.translate("ATG"));
	}
	
	@Test
	public void geneToProteinUsingTable() throws DnaToolsException {
		Assert.assertEquals("gene translation should equal protein",
				protein,DnaTools.translate(11,gene));
	}

	@Test
	public void geneToProteinUniversal() throws DnaToolsException {
		Assert.assertEquals("gene translation should equal protein",
				protein,DnaTools.translate(gene));
	}
	
	@Test
	public void testRevcompEmpty() throws DnaToolsException {
		Assert.assertEquals("empty string", "", DnaTools.reverseComplement(""));
	}
	
	@Test(expected=java.lang.NullPointerException.class)
	public void testRevcompNull() throws DnaToolsException {
		DnaTools.reverseComplement(null);
	}
	
	@Test
	public void revcompGene() throws DnaToolsException {
		Assert.assertEquals("revcomp should match",rcgene,DnaTools.reverseComplement(gene).toUpperCase());
	}
}
