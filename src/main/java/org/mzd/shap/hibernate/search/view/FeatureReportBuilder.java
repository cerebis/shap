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
package org.mzd.shap.hibernate.search.view;

import org.mzd.shap.domain.Feature;

public class FeatureReportBuilder extends ReportBuilder{
	
	public FeatureReportBuilder(String cssClass, String delim) {
		super(cssClass,delim);
	}
	
	@Override
	public Report build(Object obj) {
		Report report = super.build(obj);
		Feature target = (Feature)obj;
		report.setId(target.getId());
		report.setParentId(target.getSequence().getId());
		report.setDetail(
				getFormatter().propertyToHtml("type", target.getType()) + " " +
				getFormatter().propertyToHtml("contained-in", target.getSequence().getName()) + " " +
				getFormatter().propertyToHtml("loc",target.getLocation()) + " " +
				getFormatter().propertyToHtml("found-by", target.getDetector().getName()) + " " +
				getFormatter().propertyToHtml("conf", target.getConfidence()));
		return report;
	}
}