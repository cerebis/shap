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

import org.apache.commons.lang.StringUtils;
import org.mzd.shap.domain.Feature;

public class FeatureReportBuilder extends ReportBuilder{
	
	public FeatureReportBuilder(String cssClass, String delim) {
		super(cssClass,delim);
	}
	
	@Override
	public Report build(Float score, Object obj) {
		Report report = super.build(score, obj);
		Feature target = (Feature)obj;
		report.setId(target.getId());
		report.setParentId(target.getSequence().getId());

		// Location
		report.appendDetail("<div>")
			.appendDetail("<em>Type</em> " + target.getType() + " <em>Found By</em> " + target.getDetector().getName())
			.appendDetail("</div>")
			.appendDetail("<div>")
			.appendDetail("<em>Seq</em> " + target.getSequence().getName() + " <em>Loc</em> " + target.getLocation())
			.appendDetail("</div>");

		// Aliases
		if (target.getAliases().size() > 0) {
			report.appendDetail("<div><em>Aliases</em> " + StringUtils.join(target.getAliases().toArray(), ", ") + "</div>");
		}
		
		return report;
	}
}