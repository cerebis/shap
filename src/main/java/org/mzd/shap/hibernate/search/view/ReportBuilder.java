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

import org.hibernate.proxy.HibernateProxyHelper;
import org.mzd.shap.util.HtmlFormatter;

public abstract class ReportBuilder {
	private HtmlFormatter formatter;

	public ReportBuilder(String cssClass, String delim) {
		formatter = new HtmlFormatter(cssClass,delim);
	}
	
	protected HtmlFormatter getFormatter() {
		return formatter;
	}
	
	protected static String getShortClassName(Object obj) {
		return HibernateProxyHelper
			.getClassWithoutInitializingProxy(obj)
				.getSimpleName();
	}
	
	public Report build(Float score, Object obj) {
		Report report = new Report();
		report.setScore(score);
		report.setLabel(getShortClassName(obj));
		return report;
	}
}