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

import org.mzd.shap.domain.Annotation;

public class AnnotationReportBuilder extends ReportBuilder {
	
	public AnnotationReportBuilder(String cssClass, String delim) {
		super(cssClass,delim);
	}
	
	@Override
	public Report build(Object obj) {
		Report report = super.build(obj);
		Annotation target = (Annotation)obj;
		report.setId(target.getId());
		report.setParentId(target.getFeature().getId());
		report.setDetail(
				getFormatter().propertyToHtml("acc", target.getAccession()) + " " +
				getFormatter().propertyToHtml("feature", target.getFeature().getId()) + " " +
				getFormatter().propertyToHtml("found-by", target.getAnnotator().getName()) + " " +
				getFormatter().propertyToHtml("conf", target.getConfidence()) + " " +
				getFormatter().propertyToHtml("desc", target.getDescription()));		
		return report;
	}
}