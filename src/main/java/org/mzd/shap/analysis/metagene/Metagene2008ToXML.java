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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.mzd.shap.analysis.metagene.bean.Domain;
import org.mzd.shap.analysis.metagene.bean.Metagene;
import org.mzd.shap.analysis.metagene.bean.MetageneIO;
import org.mzd.shap.analysis.metagene.bean.MetageneIOXstream;
import org.mzd.shap.analysis.metagene.bean.Orf;
import org.mzd.shap.analysis.metagene.bean.Sequence;
import org.mzd.shap.io.bean.BeanIOException;

public class Metagene2008ToXML implements FormatConversion {
	private final static String COMMENT_MARKER = "#";
	
	static class Prediction {
		String id;
		int start;
		int end;
		String strand;
		int frame;
		boolean hasStart;
		boolean hasStop;
		double score;
		String model;
		Integer rbsStart;
		Integer rbsEnd;
		Double rbsScore;
		
		@Override
		public String toString() {
			return new ToStringBuilder(this)
				.append(id)
				.append(start)
				.append(end)
				.append(strand)
				.append(frame)
				.append(hasStart)
				.append(hasStop)
				.append(score)
				.append(model)
				.append(rbsStart)
				.append(rbsEnd)
				.append(rbsScore)
				.toString();
		}
	}
	
	static class Record {
		String id;
		String desc;
		Double gc;
		Double rbs;
		String self;
		
		List<Prediction> predictions = new ArrayList<Prediction>();
		
		void addPrediction(Prediction pred) {
			getPredictions().add(pred);
		}

		public List<Prediction> getPredictions() {
			return predictions;
		}
		public void setPredictions(List<Prediction> predictions) {
			this.predictions = predictions;
		}
		
		@Override
		public String toString() {
			return new ToStringBuilder(this)
				.append(id)
				.append(gc)
				.append(rbs)
				.append(self)
				.append(getPredictions())
				.toString();
		}
		
		public Sequence toBean() {
			Sequence seq = new Sequence();
			seq.setIdentifier(id);
			seq.setDescription(desc);
			seq.setGcContent(gc);
			seq.setDomain(getDomain(self));
			seq.setRbsContent(rbs);
			for (Prediction p : predictions) {
				Orf orf = new Orf();
				orf.setStart(p.start);
				orf.setStop(p.end);
				orf.setStrand(p.strand);
				orf.setFrame(p.frame);
				orf.setPartial(p.hasStart && p.hasStop);
				orf.setConfidence(p.score);
				orf.setRbsStart(p.rbsStart);
				orf.setRbsStop(p.rbsEnd);
				orf.setRbsScore(p.rbsScore);
				orf.setModel(getDomain(p.model));
				seq.addOrf(orf);
			}
			return seq;
		}
	}
	
	private static Domain getDomain(String str) {
		if (str.equals("b")) {
			return Domain.BACTERIA;
		}
		else if (str.equals("a")) {
			return Domain.ARCHAEA;
		}
		else if (str.equals("p")) {
			return Domain.PHAGE;
		}
		else if (str.equals("s")) {
			return Domain.SELF;
		}
		else {
			return Domain.UNDEFINED;
		}
	}
	
	public void convert(File inputFile, File outputFile) throws IOException, BeanIOException {
		convert(new BufferedReader(new FileReader(inputFile)), outputFile);
	}
	
	public void convert(String inputString, File outputFile) throws IOException, BeanIOException {
		convert(new BufferedReader(new StringReader(inputString)), outputFile);
	}
	
	private String readLine(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		if (line != null) {
			line = line.trim();
		}
		return line;
	}
	
	public void convert(BufferedReader reader, File outputFile) throws IOException, BeanIOException {
		Metagene mg = null;
		try {
			boolean readHeader = false;
			Record rec = null;
			mg = new Metagene();
			
			while (true) {
				
				String line = readLine(reader);
				if (line == null) {
					if (rec != null) {
						mg.addSequence(rec.toBean());
					}
					break;
				}
				if (line.length() == 0) {
					continue;
				}

				// Header
				if (line.startsWith(COMMENT_MARKER)) {
					if (readHeader || rec == null) {
						if (rec != null) {
							mg.addSequence(rec.toBean());
						}
						readHeader = false;
						rec = new Record();
						
						String[] field = line.split("\\s+");
						if (field.length < 2) {
							throw new IOException("Header appears invalid [" + line + "]");
						}
						rec.id = field[1];
						rec.desc = line.substring(line.indexOf(rec.id) + rec.id.length()).trim();
					}
					else {
						if (line.startsWith("gc", 2)) {
							String[] field = line.split("\\s");
							rec.gc = Double.parseDouble(field[3].substring(0, field[3].length()-1));
							rec.rbs = Double.parseDouble(field[6]);
						}
						else if (line.startsWith("self:",2)) {
							String[] field = line.split("\\s");
							rec.self = field[2];
							readHeader = true;
						}
					}
				}
				// Predictions
				else {
					String[] field = line.split("\\s");
					Prediction p = new Prediction();
					p.id = field[0];
					p.start = Integer.parseInt(field[1]);
					p.end = Integer.parseInt(field[2]);
					p.strand = field[3];
					p.frame = Integer.parseInt(field[4]);
					p.hasStart = field[5].charAt(0) == '1';
					p.hasStop = field[5].charAt(1) == '1';
					p.score = Double.parseDouble(field[6]);
					p.model = field[7];
					if (!field[8].equals("-")) {
						p.rbsStart = Integer.parseInt(field[8]);
					}
					if (!field[9].equals("-")) {
						p.rbsEnd = Integer.parseInt(field[9]);
					}
					if (!field[10].equals("-")) {
						p.rbsScore = Double.parseDouble(field[10]);
					}
					rec.addPrediction(p);
				}
			}
		}
		catch (Exception ex) {
			System.out.println("ERROR parsing output from Metagene. Potentially bad input file.");
			System.exit(1);
		}
		finally {
			if (reader != null) {
				reader.close();
				reader = null;
			}
		}

		if (mg != null) {
			MetageneIO io = new MetageneIOXstream();
			io.write(mg, outputFile);
		}
		
	}

	public static void main(String[] args) throws IOException, BeanIOException {
		if (args.length != 2) {
			System.out.println("[input sequence] [output orfs]");
			System.exit(1);
		}
		
		Process p = Runtime.getRuntime().exec("metagene " + args[0]);
		try {
			synchronized (p) {
				StringWriter sw = new StringWriter();
				InputStreamReader isr = new InputStreamReader(p.getInputStream());
				int retVal;
				while (true) {
					p.wait(100);
					while (isr.ready()) {
						sw.write(isr.read());
					}					
					try {
						retVal = p.exitValue();
						break;
					}
					catch (IllegalThreadStateException ex) {/*...*/}
				}
				
				// Just make sure stdout is completely empty.
				while (isr.ready()) {
					sw.write(isr.read());
				}
				
				if (retVal != 0) {
					System.out.println("Non-zero exist status [" + retVal + "]");
					InputStream is = null;
					try {
						is = p.getErrorStream();
						while (is.available() > 0) {
							System.out.write(is.read());
						}
					}
					finally {
						if (is != null) {
							is.close();
						}
					}
				}
				else {
					new Metagene2008ToXML().convert(sw.toString(), new File(args[1]));
				}
				
				System.exit(retVal);
			}
		}
		catch (InterruptedException ex) {/*...*/}
	}
}
