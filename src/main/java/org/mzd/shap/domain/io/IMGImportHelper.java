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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mzd.shap.domain.Location;
import org.mzd.shap.domain.LocationException;
import org.mzd.shap.domain.Strand;
import org.mzd.shap.domain.StrandException;
import org.mzd.shap.io.Fasta;
import org.mzd.shap.io.FastaReader;

/**
 * Import IMG data into SHAP.
 * <p>
 * A user must obtain the IMG exported scaffold (NT) and predicted protein (AA) sequences, as well as the gene table.
 * <p>
 * The resulting XML output can be fed into the system.
 * <p>
 * It is quite possible that IMG will alter its unspecified file formats in the future and break this class.
 */
public class IMGImportHelper {
	private File scfFasta;
	private File orfFasta;
	private File geneTable;
	
	public IMGImportHelper(File scfFasta, File orfFasta, File geneTable) throws IOException {
		assertFileOk(scfFasta);
		this.scfFasta = scfFasta;
		assertFileOk(orfFasta);
		this.orfFasta = orfFasta;
		assertFileOk(geneTable);
		this.geneTable = geneTable;
	}
	
	private void assertFileOk(File input) throws IOException{
		if (!scfFasta.exists() || !scfFasta.isFile()) {
			throw new IOException("Input file [" + input.getPath() + "] does not exist or is not a file");
		}
	}
	
	/**
	 * Represents a row from an IMG gene table.
	 */
	protected class Gene {
		private String oid;
		private Location loc;
		private double gc;
		private String locusTag;
		private String geneSymbol;
		private String description;
		private String scaffold;
		
		public Gene(String tableRow) throws IOException, NumberFormatException, StrandException, LocationException {
			String[] fields = tableRow.split("\t");
			if (fields.length < 9) {
				throw new IOException("Less than 8 fields in table row [" + tableRow + "]");
			}
			this.oid = fields[0];
			this.loc = new Location(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]), 
					Strand.getInstance(fields[3]), 0);
			this.gc = Double.parseDouble(fields[4]);
			this.locusTag = fields[5];
			this.geneSymbol = fields[6];
			this.description = fields[7];
			
			String[] scfFields = fields[8].split(":");
			if (scfFields.length != 2) {
				throw new IOException("Scaffold_Name field format error, two colon-delimited fields not found [" + fields[8] + "]");
			}
					
			this.scaffold = scfFields[1].trim();
		}

		public String getOID() {
			return oid;
		}

		public Location getLoc() {
			return loc;
		}

		public double getGc() {
			return gc;
		}

		public String getLocusTag() {
			return locusTag;
		}

		public String getGeneSymbol() {
			return geneSymbol;
		}

		public String getDescription() {
			return description;
		}

		public String getScaffold() {
			return scaffold;
		}
	}

	/**
	 * Read multi-fasta of sequences, return a Map which uses the IMG object ID (OID)
	 * as the map key. Both DNA sequences and predicted features are assigned their own
	 * OID. They are synthetic database IDs.
	 * 
	 * @param fasta
	 * @return
	 * @throws IOException
	 */
	public Map<String,Fasta> readFasta(File fasta) throws IOException {
		FastaReader reader = null;
		try {
			Map<String,Fasta> fastaMap = new HashMap<String,Fasta>();
			reader = new FastaReader(fasta);
			while (true) {
				Fasta f = reader.readFasta();
				if (f == null) {
					break;
				}
				String[] fields = f.getHeader().split(" ");
				fastaMap.put(fields[0].trim(),f);
			}
			return fastaMap;
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Read the gene table exported from IMG and return a Map whose keys are scaffold name
	 * and the values are the collection of Genes that belong to that scaffold.
	 * <p>
	 * This could easily break if IMG changes their formatting of the scaffold name field.
	 * 
	 * @param table
	 * @return
	 * @throws IOException
	 */
	public Map<String,List<Gene>> readGeneTable(File table) throws IOException {
		BufferedReader reader = null;
		try {
			Map<String,List<Gene>> geneMap = new HashMap<String,List<Gene>>();
			reader = new BufferedReader(new FileReader(table));
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (line.startsWith("gene_oid")) {
					continue;
				}
				
				try {
					Gene g = new Gene(line);
					List<Gene> genes = geneMap.get(g.getScaffold());
					if (genes == null) {
						genes = new ArrayList<Gene>();
						geneMap.put(g.getScaffold(), genes);
					}
					genes.add(g);
				}
				catch (Exception ex) {
					throw new IOException(ex);
				}
			}
			return geneMap;
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Write the XML conversion of the IMG data to the supplied writer. This can be read by
	 * {@link SequenceIOXstream}.
	 * 
	 * @param xmlWriter
	 * @throws IOException
	 */
	public void toXML(Writer xmlWriter) throws IOException {
		Map<String,Fasta> scfMap = readFasta(this.scfFasta);
		Map<String,Fasta> orfMap = readFasta(this.orfFasta);
		Map<String,List<Gene>> geneMap = readGeneTable(this.geneTable);
		
		if (scfMap.size() > 0) {
			xmlWriter.write("<object-stream>\n");
			for (String sOID : scfMap.keySet()) {
				try {
					Fasta scf = scfMap.get(sOID);
					String[] fields = scf.getHeader().split(" ");
					String scfName = fields[1];
					// take everything after the fasta id
					String scfDesc = scf.getHeader().substring(scf.getHeader().indexOf(" ") + 1);
					
					xmlWriter.write(
							String.format("<sequence name=\"%s\" desc=\"%s\" coverage=\"1.0\" taxonomy=\"UNCLASSIFIED\">\n",
									fields[0],scfDesc));
					xmlWriter.write("\t<data>" + scf.getSequence() + "</data>\n");
					
					List<Gene> gl = geneMap.get(scfName);
					if (gl == null) {
						throw new RuntimeException("No genes exist for the scaffold [" + scfName + "]");
					}
					
					for (Gene g : gl) {
						Fasta orf = orfMap.get(g.getOID());
						if (orf != null) {
							xmlWriter
								.append("\t<feature partial=\"false\" type=\"OpenReadingFrame\" conf=\"1.0\">\n")
								.append("\t\t<data>" + orf.getSequence() + "</data>\n");
						}
						else {
							xmlWriter.write("\t<feature partial=\"false\" type=\"NonCoding\" conf=\"1.0\">\n");
						}
						Location l = g.getLoc();
						xmlWriter
							.append(String.format("\t\t<location start=\"%d\" end=\"%d\" strand=\"%s\" frame=\"0\" conf=\"1.0\"/>\n",
								l.getStart(),l.getEnd(),l.getStrand().toString()))
							.append("\t\t<alias>" + g.getLocusTag() + "</alias>")
							.append("\t</feature>");
					}
				}
				catch (RuntimeException ex) {
					System.err.println(ex.getMessage());
				}
				finally {
					xmlWriter.write("</sequence>\n");
				}
			}
			xmlWriter.write("</object-stream>\n");
		}
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			System.out.println("Usage: [scaffolf fasta] [orf fasta] [gene table]");
			System.exit(1);
		}
		
		new IMGImportHelper(new File(args[0]), new File(args[1]), new File(args[2]))
			.toXML(new PrintWriter(System.out));
	}
}
