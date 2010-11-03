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


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mzd.shap.analysis.DetectorParser;
import org.mzd.shap.analysis.rnammer.bean.Entry;
import org.mzd.shap.analysis.rnammer.bean.Output;
import org.mzd.shap.analysis.rnammer.bean.RnammerIO;
import org.mzd.shap.analysis.rnammer.bean.RnammerIOXstream;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Location;
import org.mzd.shap.domain.LocationException;
import org.mzd.shap.domain.Strand;
import org.mzd.shap.domain.StrandException;
import org.mzd.shap.io.ParserException;
import org.mzd.shap.io.bean.BeanIOException;


public class RnammerParserXml implements DetectorParser {
	private RnammerIO parser = new RnammerIOXstream();
	private Log logger = LogFactory.getLog(getClass());
	
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: [rnammer xml file]");
			System.exit(1);
		}
		
		RnammerParserXml p = new RnammerParserXml();
		
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
			
			Output output = parser.read(source);
			
			for (Entry entry : output.getEntries()) {
				
				Collection<Feature> features = resultMap.get(entry.getSequenceEntry());
				if (features == null) {
					features = new ArrayList<Feature>();
					resultMap.put(entry.getSequenceEntry(),features);
				}
				
				Location loc = new Location(
						entry.getStart(), entry.getStop(),
						Strand.getInstance(entry.getDirection()),null);
					
				Feature f = Feature.newRibsomalRNAFeature();
				f.setLocation(loc);
				f.setConfidence(entry.getScore());
				f.setPartial(false);
				
				Annotation anno;
				
				// Gene annotation
				anno = Annotation.newGeneAnnotation();
				anno.setAccession("RNAmmer|" + entry.getFeature());
				anno.setDescription(NameMapper.productToGene(entry.getMolecule()));
				anno.setConfidence(entry.getScore());
				f.addAnnotation(anno);

				// Annotation for product
				anno = Annotation.newProductAnnotation();
				anno.setAccession("RNAmmer|" + entry.getFeature());
				anno.setDescription(entry.getMolecule());
				anno.setConfidence(entry.getScore());
				f.addAnnotation(anno);
				
				features.add(f);
			}
			
			return resultMap;
		}
		catch (DefinedMappingException ex) {
			getLogger().error(source,ex);
			throw new ParserException(ex);
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
