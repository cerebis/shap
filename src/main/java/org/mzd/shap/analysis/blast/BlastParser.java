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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.biojava.bio.BioException;
import org.biojava.bio.program.sax.blastxml.BlastXMLParserFacade;
import org.biojava.bio.program.ssbind.BlastLikeSearchBuilder;
import org.biojava.bio.program.ssbind.SeqSimilarityAdapter;
import org.biojava.bio.search.SearchContentHandler;
import org.biojava.bio.search.SeqSimilaritySearchHit;
import org.biojava.bio.search.SeqSimilaritySearchResult;
import org.biojava.bio.search.SeqSimilaritySearchSubHit;
import org.biojava.bio.seq.db.DummySequenceDB;
import org.biojava.bio.seq.db.DummySequenceDBInstallation;
import org.biojava.bio.symbol.SymbolList;
import org.mzd.shap.analysis.AnnotatorParser;
import org.mzd.shap.analysis.blast.description.DescriptionParser;
import org.mzd.shap.analysis.blast.description.EmptyDescriptionException;
import org.mzd.shap.analysis.blast.description.SimpleParser;
import org.mzd.shap.domain.Alignment;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.io.ParserException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class BlastParser implements AnnotatorParser {
	private DescriptionParser descParser  = new SimpleParser();
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
	
	public Map<String,Annotation> parse(File source) throws ParserException {
		FileInputStream fs = null;
		try {
			BlastXMLParserFacade bp = new BlastXMLParserFacade();
			SeqSimilarityAdapter adapter = new SeqSimilarityAdapter();
			bp.setContentHandler(adapter);
	
			Map<String,Annotation> resultMap = new HashMap<String,Annotation>();
			
			@SuppressWarnings("unchecked")
			List<?> blastResults = new ArrayList();
			
			SearchContentHandler builder = new BlastLikeSearchBuilder(blastResults,
			          new DummySequenceDB("queries"), new DummySequenceDBInstallation());
			adapter.setSearchContentHandler(builder);
			
			fs = new FileInputStream(source);
			bp.parse(new InputSource(fs));
			
			Annotation anno = Annotation.newProductAnnotation();
			resultMap.put("default",anno);
			
			if (blastResults.size() > 0) {
				List<?> hits = ((SeqSimilaritySearchResult)blastResults.get(0)).getHits();
				
				if (hits.size() > 0) {
					SeqSimilaritySearchHit topHit = (SeqSimilaritySearchHit)hits.get(0);
					anno.setConfidence(topHit.getEValue());
					
					if (topHit.getSubHits().size()>0) {
						Alignment algn = new Alignment();
						anno.setAlignment(algn);
						
						SeqSimilaritySearchSubHit subHit = (SeqSimilaritySearchSubHit)topHit.getSubHits().get(0);
						algn.setQueryStart(subHit.getQueryStart());
						algn.setQueryEnd(subHit.getQueryEnd());
						algn.setSubjectStart(subHit.getSubjectStart());
						algn.setSubjectEnd(subHit.getSubjectEnd());
						
						// Dig up the alignment strings. We don't appear to have access to the consensus in biojava!
						@SuppressWarnings("unchecked")
						Iterator<SymbolList> it = (Iterator<SymbolList>)subHit.getAlignment().symbolListIterator();
						
						algn.setQuerySeq(it.next().seqString());
						algn.setSubjectSeq(it.next().seqString());
						algn.setConsensusSeq(null);
					}
					
					try {
						anno.setAccession(topHit.getSubjectID());
						anno.setDescription(descParser.parse(topHit.getAnnotation()));
					}
					catch (EmptyDescriptionException ex) {
						getLogger().info(ex);
					}
				}
			}
			
			return resultMap;
		}
		catch (BioException ex) {
			getLogger().error(source,ex);
			throw new ParserException(ex);
		}
		catch (SAXException ex) {
			getLogger().error(source,ex);
			throw new ParserException(ex);
		}
		catch (IOException ex) {
			getLogger().error(source,ex);
			throw new ParserException(ex);
		}
		finally {
			if (fs != null) {
				try {
					fs.close();
				}
				catch (IOException ex) {
					getLogger().warn(ex);
				}
			}
		}
	}
	
}
