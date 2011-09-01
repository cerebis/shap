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
package org.mzd.shap.analysis.aragorn;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mzd.shap.analysis.DetectorParser;
import org.mzd.shap.analysis.aragorn.bean.Aragorn;
import org.mzd.shap.analysis.aragorn.bean.AragornIO;
import org.mzd.shap.analysis.aragorn.bean.AragornIOXstream;
import org.mzd.shap.analysis.aragorn.bean.Gene;
import org.mzd.shap.analysis.aragorn.bean.Sequence;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Location;
import org.mzd.shap.domain.LocationException;
import org.mzd.shap.domain.Strand;
import org.mzd.shap.domain.StrandException;
import org.mzd.shap.io.ParserException;
import org.mzd.shap.io.bean.BeanIOException;


public class AragornParserXml implements DetectorParser {
	private AragornIO parser = new AragornIOXstream();
	private Log logger = LogFactory.getLog(getClass());

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: [aragorn xml file]");
			System.exit(1);
		}
		
		AragornParserXml p = new AragornParserXml();
		
		Map<String,Collection<Feature>> resultMap = p.parse(new File(args[0])); 
		for (String queryId : resultMap.keySet()) {
			System.out.println("Detection for [" + queryId + "]");
			System.out.println(resultMap.get(queryId));
		}
	}
	
	protected Log getLogger() {
		return logger;
	}
	protected void setLogger(Log logger) {
		this.logger = logger;
	}
	
	public boolean supportsBatching() {
		return true;
	}
	
	public Map<String,Collection<Feature>> parse(File source) throws ParserException {
		try {
			Map<String,Collection<Feature>> resultMap = new HashMap<String, Collection<Feature>>();
			
			Aragorn aragorn = parser.read(source);
	
			if (aragorn.getSequences() == null) {
				getLogger().debug("Collection aragorn.sequences was null [" + aragorn + "]");
				return resultMap;
			}
			
			for (Sequence seq : aragorn.getSequences()) {
				
				if (resultMap.containsKey(seq.getIdentifier())) {
					throw new ParserException("Parser cannot handle degenerate" +
							" query ids in output");
				}
				
				Collection<Feature> features = new ArrayList<Feature>();			
				resultMap.put(seq.getIdentifier(),features);
				
				// In case of null collections (null != empty) in all cases
				if (seq.getGenes() == null) {
					getLogger().debug("Collection aragorn.sequences.genes was null [" + seq + "]");
					continue;
				}
				
				for (Gene gene : seq.getGenes()) {
					Location loc = new Location(
							gene.getStart(), gene.getStop(),
							Strand.getInstance(gene.getStrand()),null);
					
					Feature f = Feature.newTransferRNAFeature();
					f.setLocation(loc);
					f.setConfidence(null);
					if (f.getLocation().getStart() < 0) {
						f.getLocation().setStart(0);
						f.setPartial(true);
					}
					else {
						f.setPartial(false);
					}
					
					Annotation anno = Annotation.newProductAnnotation();
					if (gene.getIntronPosition() == null) {
						anno.setAccession("Aragorn|T");
					}
					else {
						anno.setAccession("Aragorn|TI");
					}
					
					String antiCodon;
					if (gene.getAnticodon() != null) {
						antiCodon = gene.getAnticodon().toUpperCase();
					}
					else {
						getLogger().warn("Property [antiCodon] was empty for sequence [" + 
								seq.getIdentifier() + "]");
						antiCodon = "";
					}
					
					String description = gene.getSpecies() + " /anticodon=" + antiCodon + 
						" /anticodonPosition=" + gene.getAnticodonPosition();
					if (gene.getIntronPosition() != null) {
						description += " /intronPosition=" + gene.getIntronPosition() 
							+ " /intronLength=" + gene.getIntronLength();
					}
					anno.setDescription(description);
					anno.setConfidence(null);
					f.addAnnotation(anno);
					
					features.add(f);
				}
				
			}
			
			return resultMap;
		}
		catch (LocationException ex) {
			getLogger().error(source,ex);
			throw new ParserException(ex);
		}
		catch (StrandException ex) {
			getLogger().error(source,ex);
			throw new ParserException(ex);
		}
		catch (BeanIOException ex) {
			getLogger().error(source,ex);
			throw new ParserException(ex);
		}
	}

}
