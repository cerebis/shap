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


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mzd.shap.analysis.DetectorParser;
import org.mzd.shap.analysis.metagene.bean.Metagene;
import org.mzd.shap.analysis.metagene.bean.MetageneIO;
import org.mzd.shap.analysis.metagene.bean.MetageneIOXstream;
import org.mzd.shap.analysis.metagene.bean.Orf;
import org.mzd.shap.analysis.metagene.bean.Sequence;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.LocationException;
import org.mzd.shap.domain.StrandException;
import org.mzd.shap.io.ParserException;
import org.mzd.shap.io.bean.BeanIOException;


public class MetageneParserXml implements DetectorParser {
	private MetageneIO parser = new MetageneIOXstream();
	private Log logger = LogFactory.getLog(getClass());

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: [metagene xml file]");
			System.exit(1);
		}
		
		MetageneParserXml p = new MetageneParserXml();
		
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
			
			Metagene metagene = parser.read(source);
			
			if (metagene.getSequences() == null) {
				getLogger().debug("Collection metagene.sequences was null [" + metagene + "]");
				return resultMap;
			}
			
			for (Sequence seq : metagene.getSequences()) {
				getLogger().debug("Retrieving predictions for sequence [" + seq.getIdentifier() + "]");
			
				if (resultMap.containsKey(seq.getIdentifier())) {
					throw new ParserException("Parser cannot handle degenerate" +
							" query ids in output");
				}
				
				Collection<Feature> features = new ArrayList<Feature>();			
				resultMap.put(seq.getIdentifier(),features);
			
				if (seq.getOrfs() == null) {
					getLogger().debug("Collection sequence.orfs was null [" + seq + "]");
					continue;
				}
				
				for (Orf orf : seq.getOrfs()) {
					getLogger().debug("Creating feature from orf [" + orf + "]");
					Feature f = Feature.newOpenReadingFrameFeature();
					f.setLocation(orf.createLocation());
					f.setConfidence(orf.getConfidence());
					f.setPartial(orf.getPartial());
					features.add(f);
					getLogger().debug("Created feature [" + f + "]");
				}
				
				getLogger().debug("Feature count: " + features.size());
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
