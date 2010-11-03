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
package org.mzd.shap.analysis.rnammer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mzd.shap.analysis.DetectorParser;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Location;
import org.mzd.shap.domain.LocationException;
import org.mzd.shap.domain.Strand;
import org.mzd.shap.domain.StrandException;
import org.mzd.shap.io.ParserException;


/**
 * 
 * Rnammer script mappings of product name to gene name.
 * <p>
 * The HMMs used in Rnammer are named after the products (TSU, SSU and LSU)
 * but when returning results the gene names are given. This conversion
 * is represented in the script as a simple associative array for the 3 kingdoms.
 * <p>
 * They are reproduced here. All are append with _rRNA as per the script's behaviour.
 * <p>
 * Bac
 * <p>
 * "TSU","5s"
 * "SSU","16s"
 * "LSU","23s"
 * <p>
 * Arc
 * <p>
 * "TSU","5s"
 * "SSU","16s"
 * "LSU","23s"
 * <p>
 * Euk
 * <p>
 * "TSU","8s"
 * "SSU","18s"
 * "LSU","28s"
 */
public class RnammerParser implements DetectorParser {
	private Log logger = LogFactory.getLog(getClass());
	private final static Map<String,String> productToGene;
	
	static {
		productToGene = new HashMap<String,String>();
		productToGene.put("5s_rRNA","TSU");
		productToGene.put("8s_rRNA","TSU");
		productToGene.put("16s_rRNA","SSU");
		productToGene.put("18s_rRNA","SSU");
		productToGene.put("23s_rRNA","LSU");
		productToGene.put("28s_rRNA","LSU");
	}
	
	protected String getGeneFromProduct(String productName) {
		return productToGene.get(productName);
	}
	
	public boolean supportsBatching() {
		return false;
	}
	
	public Map<String,Collection<Feature>> parse(File source) throws ParserException {
		BufferedReader reader = null;
		try {
			Map<String,Collection<Feature>> resultMap = new HashMap<String, Collection<Feature>>();
			Collection<Feature> features = new ArrayList<Feature>();
			resultMap.put("default", features);
			
			reader = new BufferedReader(new FileReader(source));
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (line.length() <= 0 || line.startsWith("#")) {
					continue;
				}
				
				StringTokenizer st = new StringTokenizer(line);

				st.nextToken(); // sequence name
				st.nextToken(); // program name
				String feature = st.nextToken();
				Integer start = Integer.parseInt(st.nextToken());
				Integer end = Integer.parseInt(st.nextToken());
				Double confidence = Double.parseDouble(st.nextToken());
				Strand strand = Strand.getInstance(st.nextToken());
				st.nextToken(); // frame
				String productName = st.nextToken(); 
				
				Location loc = new Location(start-1, end-1, strand, null);
				Feature f = Feature.newRibsomalRNAFeature();
				f.setConfidence(confidence);
				f.setLocation(loc);
				f.setPartial(false);
				
				Annotation anno;
				
				// Annotation for gene
				anno = Annotation.newGeneAnnotation();
				anno.setAccession("RNAmmer|" + feature);
				anno.setDescription(getGeneFromProduct(productName));
				anno.setConfidence(confidence);
				f.addAnnotation(anno);

				// Annotation for product
				anno = Annotation.newProductAnnotation();
				anno.setAccession("RNAmmer|" + feature);
				anno.setDescription(productName);
				anno.setConfidence(confidence);
				f.addAnnotation(anno);
				
				features.add(f);
			}
			return resultMap;
		}
		catch (StrandException ex) {
			getLogger().error(source,ex);
			throw new ParserException(ex);
		}
		catch (LocationException ex) {
			getLogger().error(source,ex);
			throw new ParserException(ex);
		}
		catch (IOException ex) {
			getLogger().error(source,ex);
			throw new ParserException(ex);
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				}
				catch (IOException ex) {
					getLogger().warn(ex);
				}
			}
		}
		
	}

	protected Log getLogger() {
		return logger;
	}
	protected void setLogger(Log logger) {
		this.logger = logger;
	}

}
