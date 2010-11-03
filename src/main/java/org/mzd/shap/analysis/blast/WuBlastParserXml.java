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
import org.mzd.shap.analysis.blast.bean.Hit;
import org.mzd.shap.analysis.blast.bean.Hsp;
import org.mzd.shap.analysis.blast.bean.Iteration;
import org.mzd.shap.analysis.blast.bean.MultiQueryWrapper;
import org.mzd.shap.analysis.blast.bean.MultiQueryWrapperIO;
import org.mzd.shap.analysis.blast.bean.MultiQueryWrapperIOBetwixt;
import org.mzd.shap.domain.Alignment;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.io.ParserException;
import org.mzd.shap.io.bean.BeanIOException;


public class WuBlastParserXml implements AnnotatorParser {
	private MultiQueryWrapperIO parser = new MultiQueryWrapperIOBetwixt();
	private Log logger = LogFactory.getLog(getClass());

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: [corrected blast xml file]");
			System.exit(1);
		}
		
		WuBlastParserXml p = new WuBlastParserXml();
		
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
		/*
		 * Although this parser does support batching, wu-blastall can generate invalid
		 * XML in situations where it crashes during execution. This can result in all
		 * results for the batch being lost. 
		 */
		return false;
	}
	
	public Map<String,Annotation> parse(File source) throws ParserException {
		try {
			Map<String,Annotation> resultMap = new HashMap<String,Annotation>();
			
			MultiQueryWrapper multiQuery = parser.read(source);
			
			for (BlastOutput blast : multiQuery.getResults()) {
				
				Annotation anno = Annotation.newProductAnnotation();
				
				if (resultMap.containsKey(blast.getQueryId())) {
					throw new ParserException("Parser cannot handle degenerate" +
							" query ids in blast output");
				}
				resultMap.put(blast.getQueryId(),anno);
	
				if (blast.getIterations().size() > 0) {
					Iteration iter = blast.getIterations().get(0);
					if (iter.getHits().size() > 0) {
						Hit topHit = iter.getHits().get(0);
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
