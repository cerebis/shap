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
package org.mzd.shap.analysis.blast;

import java.io.File;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mzd.shap.analysis.AnnotatorParser;
import org.mzd.shap.analysis.blast.bean.BlastOutput;
import org.mzd.shap.analysis.blast.bean.BlastOutputIO;
import org.mzd.shap.analysis.blast.bean.BlastOutputIOXstream;
import org.mzd.shap.analysis.blast.bean.Hit;
import org.mzd.shap.analysis.blast.bean.Hsp;
import org.mzd.shap.analysis.blast.bean.Iteration;
import org.mzd.shap.domain.Alignment;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.io.ParserException;
import org.mzd.shap.io.bean.BeanIOException;

public class NcbiBlastParserXml implements AnnotatorParser {
	private BlastOutputIO beanReader = new BlastOutputIOXstream();
	private Log logger = LogFactory.getLog(getClass());

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: [ncbi blast xml file]");
			System.exit(1);
		}
		
		NcbiBlastParserXml p = new NcbiBlastParserXml();
		
		Map<String,Annotation> resultMap = p.parse(new File(args[0])); 
		for (String queryId : resultMap.keySet()) {
			System.out.println("Annotation for [" + queryId + "]");
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
	
	public Map<String,Annotation> parse(File source) throws ParserException {
		try {
			Map<String,Annotation> resultMap = new HashMap<String,Annotation>();
			
			BlastOutput blastOutput = beanReader.read(source);
			
			if (blastOutput.getIterations() == null) {
				getLogger().debug("Collection blastoutput.iterations was null [" + blastOutput + "]");
				return resultMap;
			}
			
			for (Iteration blIter : blastOutput.getIterations()) {
				
				if (blIter.getHits() == null) {
					getLogger().debug("Collection iteration.hits was null [" + blIter + "]");
					continue;
				}
				
				Annotation anno = Annotation.newProductAnnotation();
				
				if (blIter.getHits().size() > 0) {
				
					Hit topHit = blIter.getHits().get(0);
					
					if (topHit.getHsps() == null) {
						getLogger().debug("Collection hit.hsps was null [" + topHit.getHsps() + "]");
						continue;
					}

					anno.setAccession(topHit.getAccession());
					anno.setDescription(topHit.getDefinition());
					
					if (topHit.getHsps().size() > 0) {
						Hsp topHsp = topHit.getHsps().get(0);
						anno.setConfidence(topHsp.getEvalue());
						Alignment algn = new Alignment();
						anno.setAlignment(algn);
						algn.setQueryStart(topHsp.getQueryFrom());
						algn.setQueryEnd(topHsp.getQueryTo());
						algn.setSubjectStart(topHsp.getHitFrom());
						algn.setSubjectEnd(topHsp.getHitTo());
						algn.setQuerySeq(topHsp.getQuerySequence());
						algn.setSubjectSeq(topHsp.getHitSequence());
						algn.setConsensusSeq(topHsp.getMidline());
					}
				}
				
				if (resultMap.containsKey(blIter.getQueryDefinition())) {
					throw new ParserException("Parser cannot handle degenerate" +
							" query ids in blast output");
				}
				
				resultMap.put(blIter.getQueryDefinition(),anno);
			}
			
			return resultMap;
		}
		catch (BeanIOException ex) {
			getLogger().error(source,ex);
			throw new ParserException(ex);
		}
	}
}
