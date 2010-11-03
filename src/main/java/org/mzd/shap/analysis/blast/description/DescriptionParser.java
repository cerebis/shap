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
package org.mzd.shap.analysis.blast.description;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse the description returned from a blast search of a given database.
 * <p>
 * Databases such as NR and SwissProt have their own conventions for how they
 * encode the details about the subject. We need to write a parser for each
 * database we wish to return results from.
 * <p>
 * Only the first capturing group of the specified {@link Pattern} will be used
 * as the description.
 * <p> 
 * Therefore there must be at least one capturing group.
 * 
 */
public abstract class DescriptionParser {
	private static final String propertyName =  "subjectDescription";
	private Pattern pattern;
	
	protected DescriptionParser(String regex) {
		this.pattern = Pattern.compile(regex);
	}
	
	protected DescriptionParser(Pattern pattern) {
		this.pattern = pattern;
	}
	

	/**
	 * Extract the description from a biojava annotation object.
	 * 
	 * @param annotation
	 * @return the description as a string.
	 */
	protected String getSubjectDescription(org.biojava.bio.Annotation annotation) {
		return (String)annotation.getProperty(propertyName);
	}
	
	/**
	 * Parse a description string for the relevant information as defined
	 * by the regular expression supplied at instantiation.
	 * 
	 * @param description - the description to parse
	 * @return a subset of the original description
	 * @throws EmptyDescriptionException
	 */
	public String parse(String description) throws EmptyDescriptionException {
		
		Matcher m = getPattern().matcher(description);
		if (m.matches()) {
			return m.group(1);
		}
		else {
			throw new EmptyDescriptionException("Using pattern [" + pattern + 
					"] no match was found to description [" + description + "]");
		}
	}
	
	/**
	 * Parse a biojava annotation object for the relevant information as defined
	 * by the regular expression supplied at instantiation.
	 * 
	 * @param annotation - the biojava annotation object to parse
	 * @return a subset of the original description
	 * @throws EmptyDescriptionException
	 */
	public String parse(org.biojava.bio.Annotation annotation) throws EmptyDescriptionException {
		return parse(getSubjectDescription(annotation));
	}

	protected Pattern getPattern() {
		return pattern;
	}
}
