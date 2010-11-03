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
package org.mzd.shap.analysis.trnascan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mzd.shap.analysis.DetectorParser;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Location;
import org.mzd.shap.domain.LocationException;
import org.mzd.shap.domain.Strand;
import org.mzd.shap.io.ParserException;


public class TRnaScanParser implements DetectorParser {
	private Log logger = LogFactory.getLog(getClass());
	private final static int COLUMN_NUMBER = 9;

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

			// Skip-over the first 3 non-data lines.
			for (int i=0; i<3; i++) {
				String line = reader.readLine();
				// If we've reached the end of the file already, 
				// we've got nothing to parse. Return an empty list.
				if (line == null) {
					return resultMap;
				}
			}
			
			
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (line.length() <= 0) {
					continue;
				}
				
				String[] token = line.split("\\s+");
				if (token.length < COLUMN_NUMBER) {
					throw new ParserException("Line did not contain " + 
							COLUMN_NUMBER + " columns [" + line + "]");
				}
				
				//String seqName = token[0];
				//Integer number = Integer.parseInt(token[1]);
				Integer start = Integer.parseInt(token[2]);
				Integer end = Integer.parseInt(token[3]);
				String type = token[4];
				String antiCodon = token[5];
				Double coveScore = Double.parseDouble(token[8]);

				Location loc;
				Integer intronPosition, intronLength;
				if (start < end) {
					loc = new Location(start-1,end-1,Strand.Forward,null);
					intronPosition = Integer.parseInt(token[6]);
					intronLength = Integer.parseInt(token[7]) - intronPosition + 1;
				}
				else {
					loc = new Location(end-1,start-1,Strand.Reverse,null);
					intronPosition = Integer.parseInt(token[7]);
					intronLength = Integer.parseInt(token[6]) - intronPosition + 1;
				}
				
				
				Feature f = Feature.newTransferRNAFeature();
				f.setConfidence(coveScore);
				f.setLocation(loc);
				f.setPartial(false);
				
				Annotation anno = Annotation.newProductAnnotation();
				String description = "tRNA-" + type + " /anticodon=" + antiCodon;
				if (intronPosition != 0) {
					anno.setAccession("tRNAscan|TI");
					description += " /intronPosition=" + intronPosition + " /intronLength=" + intronLength;
				}
				else {
					anno.setAccession("tRNAscan|T");
				}
				anno.setDescription(description);
				anno.setConfidence(null);
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

	protected Log getLogger() {
		return logger;
	}

}
