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
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.AnnotationType;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.spring.io.DescriptionEditor;

public class CodingSequenceWriter extends FeatureWriter {
	private final static String HYPOTHETICAL_NAME = "hypothetical protein";
	private static Pattern ecPattern = Pattern.compile("^.*\\[EC\\:(.*?)\\].*$");
	private Map<String,DescriptionEditor> editors;
	private DescriptionEditor defaultEditor;
	
	public CodingSequenceWriter(Writer writer, 
			List<String> annotatorPrecedence,
			DescriptionEditor defaultEditor,
			Map<String, DescriptionEditor> editors) {
		super("CDS",writer,annotatorPrecedence);
		this.editors = editors;
		this.defaultEditor = defaultEditor;
	}

	protected void checkAndWriteEcQualifier(Annotation a) throws IOException {
		if (a.getDescription().contains("[EC:")) {
			Matcher m = ecPattern.matcher(a.getDescription());
			if (m.find()) {
				writeQualifier("EC_number","%s",m.group(1));
			}
		}
	}
	
	@Override
	public void writeAllQualifiers(Feature entity) throws IOException {
		writeQualifier("locus_tag", "feature:%d", entity.getId());
		writeQualifier("db_xref", "feature:%d", entity.getId());
		
		if (entity.isPartial()) {
			writeQualifier("note","partial");
		}
		
		if (entity.getAnnotations() != null && entity.getAnnotations().size() > 0) {
			
			SortedSet<Annotation> ranked = rankAnnotations(entity,AnnotationType.Product);
			
			// Write out qualifiers for the winning and subordinate annotators.
			boolean topWritten = false;
			for (Annotation a : ranked) {
				if (a.getDescription() != null) {
					
					if (!topWritten) {
						String brief = getMinimalDescription(a);
						writeQualifier("product","%s",escapeQuotes(brief));
						writeQualifier("function","%s",escapeQuotes(brief));
						topWritten = true;
					}
					
					String note = String.format(
							"source='%s' conf='%6.2e' desc='%s'",
							a.getAnnotator().getName(),
							a.getConfidence(),
							a.getDescription());
						
					writeQualifier("note","%s",escapeQuotes(note));
					checkAndWriteEcQualifier(a);
				}
			}
			
			// If there was no informative annotation, then it is a hypothetical.
			if (!topWritten) {
				writeQualifier("product", "%s", HYPOTHETICAL_NAME);
			}
			
		}
		
		writeQualifier("translation","%s",entity.getData().getValue());
	}

	/**
	 * Return a minimal description for the given annotation.
	 * 
	 * @param annotation
	 * @return a minimal description
	 */
	protected String getMinimalDescription(Annotation annotation) {
		String annotatorName = annotation.getAnnotator().getName();
		DescriptionEditor editor = !editors.containsKey(annotatorName) ?
				defaultEditor : editors.get(annotatorName);
		
		return editor.getMinimalDescription(annotation.getDescription());
	}

}
