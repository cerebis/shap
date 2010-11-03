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
package org.mzd.shap.analysis.rfamscan;

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
import org.mzd.shap.io.ParserException;


public class RfamscanParser implements DetectorParser {
	private Log logger = LogFactory.getLog(getClass());
	
	protected Log getLogger() {
		return logger;
	}
	protected void setLogger(Log logger) {
		this.logger = logger;
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
				
				StringTokenizer st = new StringTokenizer(line,",");
				
				st.nextToken(); // seqId
				Integer start = Integer.parseInt(st.nextToken());
				Integer end = Integer.parseInt(st.nextToken());
				String modelAccession = st.nextToken();
				st.nextToken(); // modelStart
				st.nextToken(); // modelEnd 
				Double confidence = Double.parseDouble(st.nextToken());
				st.nextToken(); // modelName
				String modelDesc = st.nextToken();
				
				Location loc = null;
				if (start < end) {
					Strand strand = Strand.Forward;
					loc = new Location(start-1, end-1, strand, null);
				}
				else {
					Strand strand = Strand.Reverse;
					loc = new Location(end-1, start-1, strand, null);
				}
				
				Feature f = Feature.newRibsomalRNAFeature();
				f.setConfidence(confidence);
				f.setLocation(loc);
				f.setPartial(false);
				
				Annotation anno = Annotation.newProductAnnotation();
				anno.setAccession("Rfam|" + modelAccession);
				anno.setDescription(modelDesc);
				anno.setConfidence(confidence);
				f.addAnnotation(anno);
				
				features.add(f);
			}
			return resultMap;
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

}
