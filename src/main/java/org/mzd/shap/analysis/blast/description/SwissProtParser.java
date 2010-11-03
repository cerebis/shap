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

import java.util.regex.Pattern;

/**
 * Parsers the description syntax as used in the SwissProt database, as released
 * by the Swiss-Prot group and not NCBI who modifies the description field.
 * <p>
 * Attempts to return the minimal detail that tells us something about the protein,
 * and excludes detail about the species.
 * 
 */
public class SwissProtParser extends DescriptionParser {
	// this is kept here for possible future reference.
	// private final static String NCBI_PATTERN = "^gi.*?\\s+(.*?)\\s?((&gt;|\\>).*)*(?:\\(.*)*$";
	
	// this is the pattern if using the original DB from SP.
	private final static Pattern SWISSPROT_PATTERN = Pattern.compile("^(.*)\\s+-\\s+.*?$");
	
	public SwissProtParser() {
		super(SWISSPROT_PATTERN);
	}
}
