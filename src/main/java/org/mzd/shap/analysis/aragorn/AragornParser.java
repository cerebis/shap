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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mzd.shap.analysis.DetectorParser;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Location;
import org.mzd.shap.domain.LocationException;
import org.mzd.shap.domain.Strand;
import org.mzd.shap.io.ParserException;


public class AragornParser implements DetectorParser {
	private Log logger = LogFactory.getLog(getClass());
	private Pattern linePattern = Pattern.compile("^(c?)\\[(\\d+),(\\d+)\\]$");
	private final static int COLUMN_NUMBER = 4;

	public boolean supportsBatching() {
		return false;
	}
	
	public Map<String, Collection<Feature>> parse(File source) throws ParserException {
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
				if (line.length() <= 0 || line.startsWith(">")) {
					continue;
				}
				
				String[] token = line.split("\\s+");
				if (token.length < COLUMN_NUMBER) {
					throw new ParserException("Line did not contain " + 
							COLUMN_NUMBER + " or more columns [" + line + "]");
				}
				
				String type = token[0];
				String location = token[1];
				Integer acceptorSpecies = Integer.parseInt(token[2]);
				String antiCodon = token[3];
				
				Integer intronPosition = null;
				Integer intronLength = null; 
				if (type.equals("TI") && token.length >= 6) {
					intronPosition = Integer.parseInt(token[4]);
					intronLength = Integer.parseInt(token[5]);
				}
				
				Matcher m = linePattern.matcher(location);
				Location loc = null;
				if (m.matches()) {
					if (m.groupCount() != 3) {
						throw new ParserException("Location string [" + location + "] didn't correctly match pattern");
					}
					Integer start = Integer.parseInt(m.group(2));
					Integer end = Integer.parseInt(m.group(3));
					Strand strand = m.group(1).equals("c") ? Strand.Reverse : Strand.Forward;
					
					loc = new Location(start,end,strand,null);
				}
				
				Feature f = Feature.newTransferRNAFeature();
				f.setConfidence(null);
				f.setLocation(loc);
				f.setPartial(false);
				
				Annotation anno = Annotation.newProductAnnotation();
				anno.setAccession("Aragorn|" + type);
				token = antiCodon.split("[\\(\\)]");
				if (token.length < 2) {
					throw new ParserException("Anticodon field did not contain " + 
							2 + " or more columns [" + line + "]");
				}
				String description = "tRNA-" + token[0] + " /anticodon=" + token[1].toUpperCase() + 
					" /iso-acceptorSpecies=" + acceptorSpecies;
				if (type.equals("TI")) {
					description += " /intronPosition=" + intronPosition + " /intronLength=" + intronLength;
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
