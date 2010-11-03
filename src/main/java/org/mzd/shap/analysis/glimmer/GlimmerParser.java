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
package org.mzd.shap.analysis.glimmer;

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
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Location;
import org.mzd.shap.domain.LocationException;
import org.mzd.shap.domain.Strand;
import org.mzd.shap.domain.StrandException;
import org.mzd.shap.io.ParserException;


public class GlimmerParser implements DetectorParser {
	private Log logger = LogFactory.getLog(getClass());

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: [input glimmer file]");
			System.exit(1);
		}
		
		Map<String,Collection<Feature>> resultMap = 
			new GlimmerParser().parse(new File(args[0]));
		
		for (String queryId : resultMap.keySet()) {
			for (Feature f : resultMap.get(queryId)) {
				System.out.println(queryId + " -> " + f);
			}
		}
	}
	
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
			Collection<Feature> features = null;
			
			reader = new BufferedReader(new FileReader(source));
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				// Prepare for features from the given sequence.
				// This should cope with multifasta results, but it is untested.
				if (line.startsWith(">")) {
					String[] comment = line.split("\\s+");
					if (comment[0].length() < 2) {
						throw new ParserException("Comment line appears to be empty");
					}
					features = new ArrayList<Feature>();
					resultMap.put(comment[0].substring(1),features);
					continue;
				}
				
				String[] field = line.split("\\s+");
				if (field.length != 5) {
					throw new ParserException("Wrong number of expected fields [" + line + "]");
				}
				
				Integer start = new Integer(field[1]) - 1;
				Integer end = new Integer(field[2]) - 1;
				
				Strand strand = Strand.getInstance(field[3].substring(0, 1));
				// Glimmer frames are 1..3
				Integer frame = new Integer(field[3].substring(1)) - 1;
				Double conf = new Double(field[4]);
				
				Feature f = Feature.newOpenReadingFrameFeature();
				f.setLocation(Location
						.createForwardLocation(start, end, strand, frame));
				f.setConfidence(conf);
				f.setPartial(false);
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
	
}
