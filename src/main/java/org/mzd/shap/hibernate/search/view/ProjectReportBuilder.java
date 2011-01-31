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

import org.mzd.shap.domain.Project;

public class ProjectReportBuilder extends ReportBuilder {
	
	public ProjectReportBuilder(String cssClass, String delim) {
		super(cssClass,delim);
	}
	
	@Override
	public Report build(Float score, Object obj) {
		Report report = super.build(score, obj);
		Project target = (Project)obj;
		report.setId(target.getId());
		report.setParentId(null);
		report.setDetail(
				getFormatter().propertyToHtml("name",target.getName()) + " " +
				getFormatter().propertyToHtml("desc",target.getDescription()));
		return report;
	}
}