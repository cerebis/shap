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
package org.mzd.shap.util;

import org.springframework.web.util.HtmlUtils;

public class StringUtils {

	/**
	 * Replace all occurrences of special characters in HTML entity references.
	 * 
	 * @param target - string to encode
	 * @return encoded version of string.
	 */
	static public String htmlEncode(String target) {
		return HtmlUtils.htmlEscape(target);
	}
	
	/**
	 * Strip all occurrences of those characters listed in the string badChars.
	 * 
	 * @param badChars - the list of characters to strip.
	 * @param target - the target string on which to operate.
	 * @return the target stripped of badChar occurrences.
	 */
	static public String stripCharacters(String badChars, String target) {
		return stripCharacters(badChars,"",target);
	}
	
	/**
	 * 
	 * @param badChars - the list of characters to strip.
	 * @param replacement - each occurrence will be replaced by the following string.
	 * @param target - the target string on which to operate.
	 * @return the target stripped of badChar occurrences.
	 */
	static public String stripCharacters(String badChars, String replacement, String target) {
		String result = "";
		for (int i = 0; i < target.length(); i++ ) {
			// keep those chars which aren't found in bad chars 
	        if (badChars.indexOf(target.charAt(i)) == -1) {
	           result += target.charAt(i);
	        }
	        else {
	        	result += replacement;
	        }
        }
	    return result;
	}
	
}
