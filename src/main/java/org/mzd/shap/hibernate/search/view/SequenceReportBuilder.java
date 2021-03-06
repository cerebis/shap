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

import org.mzd.shap.domain.Sequence;

public class SequenceReportBuilder extends ReportBuilder {
	
	public SequenceReportBuilder(String cssClass, String delim) {
		super(cssClass,delim);
	}
	
	@Override
	public Report build(Float score, Object obj) {
		Report report = super.build(score, obj);
		Sequence target = (Sequence)obj;
		report.setId(target.getId());
		report.setParentId(target.getSample().getId());
		report.appendDetail("<div>")
			.appendDetail("<em>Name</em> " + target.getName() + " <em>Desc</em>" + target.getDescription())
			.appendDetail("</div>")
			.appendDetail("<div>")
			.appendDetail("<em>Taxonomy</em> " + target.getTaxonomy() + " <em>Coverage</em> " + target.getCoverage())
			.appendDetail("</div>");
		return report;
	}
}