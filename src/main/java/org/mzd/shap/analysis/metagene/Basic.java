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
package org.mzd.shap.analysis.metagene;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.mzd.shap.analysis.AnalyzerException;
import org.mzd.shap.analysis.SimpleDetector;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.FeatureException;
import org.mzd.shap.domain.MoleculeType;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.SequenceException;
import org.mzd.shap.exec.LocalDelegate;
import org.mzd.shap.exec.SimpleExecutable;
import org.mzd.shap.io.Fasta;
import org.mzd.shap.io.FastaReader;


public class Basic {
	
	public static void printUsageAndExit(int exitValue) {
		System.out.println(
				"Usage: [OPTIONS] <input DNA fasta> <output file>\n" +
				"Options:\n" +
				"	--dna         Write DNA sequence rather than protein");
		System.exit(exitValue);
	}

	public static void main(String[] args) {
		
		SimpleDetector detector = new SimpleDetector();
		detector.setParserClass(MetageneParserXml.class);
		detector.setScratchPath(new File("/tmp"));
		detector.setId(0);
		detector.setName("Metagene");
		detector.setArgumentString("metageneXML.sh %s %s");
		
		if (!(args.length == 2 || args.length == 3)) {
			printUsageAndExit(1);
		}
		
		File inputFile;
		File outputFile;
		MoleculeType molType = MoleculeType.Protein;
		if (args.length == 3) {
			if (args[0].equals("--dna")) {
				molType = MoleculeType.DNA;
			}
			else {
				printUsageAndExit(1);
			}
			inputFile = new File(args[1]);
			outputFile = new File(args[2]);
		}
		else {
			inputFile = new File(args[0]);
			outputFile = new File(args[1]);
		}
		
		if (!inputFile.exists() || !inputFile.isFile()) {
			System.out.println(inputFile.getPath() + " is not a file or does not exist");
			System.exit(1);
		}
		
		if (outputFile.exists()) { 
			System.out.println(outputFile.getPath() + " already exists");
			System.exit(1);
		}
		
		FastaReader reader = null;
		BufferedOutputStream output = null;
		try {
			reader = new FastaReader(inputFile);
			output = new BufferedOutputStream(new FileOutputStream(outputFile));
			
			int uniqId = 0;
			while (true) {
				Fasta fasta = reader.readFasta();
				if (fasta == null) {
					break;
				}
				Sequence dnaSeq = Sequence.fromFasta(fasta);
				
				SimpleExecutable exec = new SimpleExecutable();
				exec.setDelegate(new LocalDelegate());
				
				Feature[] features = detector.analyze(exec, dnaSeq);
				for (Feature feat : features) {
					feat.setId(uniqId++);
					feat.toFastaFile(output, molType, true, true);
				}
				
				// Force a flush after each sequence as users get 
				// confused if nothing is written.
				output.flush();
			}
		}
		catch (FeatureException ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}
		catch (SequenceException ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}
		catch (AnalyzerException ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}
		catch (IOException ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception ex) {/*...*/}
			}
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {/*...*/}
			}
		}
		
		
	}
}
