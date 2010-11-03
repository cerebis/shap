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
package org.mzd.shap.analysis.hmmer;

import java.io.File;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mzd.shap.analysis.AnnotatorParser;
import org.mzd.shap.analysis.hmmer.bean.DomainHit;
import org.mzd.shap.analysis.hmmer.bean.GlobalHit;
import org.mzd.shap.analysis.hmmer.bean.Hmmpfam;
import org.mzd.shap.analysis.hmmer.bean.HmmpfamIO;
import org.mzd.shap.analysis.hmmer.bean.HmmpfamIOXstream;
import org.mzd.shap.analysis.hmmer.bean.Query;
import org.mzd.shap.analysis.hmmer.bean.Result;
import org.mzd.shap.domain.Alignment;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.io.ParserException;
import org.mzd.shap.io.bean.BeanIOException;


public class HmmpfamParserXml implements AnnotatorParser {
	private HmmpfamIO beanReader = new HmmpfamIOXstream();
	private Log logger = LogFactory.getLog(getClass());
	
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: [hmmer xml output]");
			System.exit(1);
		}
		
		File input = new File(args[0]);
		if (!input.exists() || !input.isFile()) {
			System.out.println("'" + input.getName() + "' does not exist or is not a file.");
			System.exit(1);
		}
		
		HmmpfamParserXml p = new HmmpfamParserXml();
		try {
			Map<String,Annotation> resultMap = p.parse(input);
			for (String key : resultMap.keySet()) {
				System.out.println(key + " => " + resultMap.get(key));
			}
		}
		catch (ParserException ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
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
	
	public Map<String,Annotation> parse(File source) throws ParserException {
		try {
			Map<String,Annotation> resultMap = new HashMap<String,Annotation>();
			
			Hmmpfam hmmpfam = beanReader.read(source);
			
			if (hmmpfam.getResults() == null) {
				getLogger().debug("Collection hmmpfam.results was null [" + hmmpfam + "]");
				return resultMap;
			}
			
			for (Result result : hmmpfam.getResults()) {
				Annotation anno = Annotation.newProductAnnotation();
				
				if (result.getGlobalHits() == null) {
					getLogger().debug("Collection result.globalhits was null [" + result + "]");
					continue;
				}
				
				// Take just the top hit
				List<GlobalHit> globalHits = result.getGlobalHits();
				if (globalHits.size() > 0) {
					GlobalHit hit = globalHits.get(0);
					
					anno.setAccession(hit.getModel());
					anno.setDescription(hit.getDescription());
					anno.setConfidence(hit.getEvalue());
					Alignment algn = new Alignment();
					anno.setAlignment(algn);
				
					if (result.getDomainHits() == null) {
						getLogger().debug("Collection result.domainhits was null [" + result + "]");
						continue;
					}
					
					// Get the relevant alignment details
					for (DomainHit dh : result.getDomainHits()) {
						if (dh.getModel().equals(hit.getModel())) {
							algn.setQueryStart(dh.getSeqFrom());
							algn.setQueryEnd(dh.getSeqTo());
							algn.setSubjectStart(dh.getHmmFrom());
							algn.setSubjectEnd(dh.getHmmTo());
							algn.setQuerySeq(dh.getQuerySeq());
							algn.setSubjectSeq(dh.getSubjectSeq());
							algn.setConsensusSeq(dh.getConsensusSeq());
							break;
						}
					}
					
					Query query = result.getQuery();
					if (query == null || query.getName() == null) {
						throw new ParserException("Hmmpfam result did not contain neccessary query info");
					}
					if (resultMap.containsKey(query.getName())) {
						throw new ParserException("Parser cannot handle degenerate" +
								" query ids in hmmpfam output");
					}
					
					resultMap.put(query.getName(), anno);
				}
			}
			
			return resultMap;
		}
		catch (BeanIOException ex) {
			getLogger().error(source,ex);
			throw new ParserException(ex);
		}
	}
	
}
