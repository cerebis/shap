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
package org.mzd.shap.io.genbank;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.SortedSet;

import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.AnnotationType;
import org.mzd.shap.domain.Feature;


public class GeneWriter extends FeatureWriter {
	
	public GeneWriter(Writer writer, List<String> annotatorPrecedence) {
		super("gene", writer, annotatorPrecedence);
	}

	@Override
	public void writeAllQualifiers(Feature entity) throws IOException {
		writeQualifier("locus_tag", "feature:%d", entity.getId());
		writeQualifier("db_xref","feature:%d", entity.getId());
		
		SortedSet<Annotation> ranked = rankAnnotations(entity,AnnotationType.Gene);
		
		// Write out qualifiers for the winning and subordinate annotators.
		for (Annotation a : ranked) {
			if (a.getDescription() != null) {
				switch (entity.getType()) {
				case RibosomalRNA:
					writeQualifier("gene","%s",escapeQuotes(a.getDescription()));
					break;
				case OpenReadingFrame:
					writeQualifier("function","%s",escapeQuotes(a.getDescription()));
					break;
				default:
				}
				
				break;
			}
		}
	}

}
