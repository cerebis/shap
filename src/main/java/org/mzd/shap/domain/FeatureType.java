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
package org.mzd.shap.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("feature-type")
public enum FeatureType {
	OpenReadingFrame("ORF",true),
	TransferRNA("TRNA",false),
	RibosomalRNA("RRNA",false),
	NonCoding("NC",false),
	Undefined("UDEF",false);
	
	@XStreamOmitField
	private Boolean translated;
	@XStreamOmitField
	private String shortName;
	
	private FeatureType(String shortName, Boolean translated) {
		this.shortName = shortName;
		this.translated = translated;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public Boolean isTranslated() {
		return translated;
	}
}
