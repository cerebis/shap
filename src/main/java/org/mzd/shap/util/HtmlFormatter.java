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

import java.text.DecimalFormat;

import org.springframework.web.util.HtmlUtils;

/**
 * Convenience class for formatting property/value pairs for better styling control
 * in browsers.
 * <p>
 * Each property name, value and delimiter are wrapped by <span> tags, which are wrapped
 * again in another <span>. The 4 types of spans are then assigned classes, using the
 * supplied cssClass string as a prefix for the child spans.
 * <p>
 * Eg. For the following property/value pair and cssClass "test"
 * <p>
 * userName/bob
 * <p>
 * The output might be
 * <p>
 * {@code<span class="test">
 *    <span class="test_prop">userName</span>
 *    <span class="test_dlm">=</span>
 *    <span class="test_val">bob</span>
 * </span>}
 * <p>
 * It is expected that objects other than strings can be corrected converted to strings
 * by use of the toString() method. Therefore non-trivial types that have overridden
 * toString() might find this limitation awkward.
 * <p>
 * 
 */
public class HtmlFormatter {
	public final static String PROPERTY_SUFFIX = "_prop";
	public final static String DELIMITER_SUFFIX = "_dlm";
	public final static String VALUE_SUFFIX = "_val";
	public final static String DECIMAL_FORMAT = "0.###E0";
	private DecimalFormat decimalFormat;
	private String cssClass;
	private String delim;
	
	public HtmlFormatter(String cssClass, String delim) {
		this(cssClass,delim,new DecimalFormat(DECIMAL_FORMAT));
	}

	public HtmlFormatter(String cssClass, String delim, DecimalFormat decimalFormat) {
		this.cssClass = cssClass;
		this.delim = HtmlUtils.htmlEscapeDecimal(delim);
		this.decimalFormat = decimalFormat;
	}
	
	protected String parentClass() {
		return this.cssClass;
	}

	protected String childClass(String suffix) {
		return this.cssClass + suffix;
	}
	
	protected String valueToString(Object value) {
		String ret;
		if (value == null) {
			ret = "[nil]";
		}
		else if (value instanceof Number) {
			ret = decimalFormat.format(value);
		}
		else {
			ret = value.toString();
		}
		return HtmlUtils.htmlEscapeDecimal(ret);
	}

	/**
	 * Wrap a name/value pair by {@code <span>} tags.
	 * 
	 * @param name
	 * @param value
	 * @return HTML string.
	 */
	public String propertyToHtml(String name, Object value) {
		return new StringBuffer()
			.append("<span class=\"" + parentClass() + "\">")
				.append("<span class=\"" + childClass(PROPERTY_SUFFIX) + "\">")
					.append(HtmlUtils.htmlEscapeDecimal(name))
				.append("</span>")
				.append("<span class=\"" + childClass(DELIMITER_SUFFIX) + "\">")
					.append(delim)
				.append("</span>")
				.append("<span class=\"" + childClass(VALUE_SUFFIX) + "\">")
					.append(valueToString(value))
				.append("</span>")
			.append("</span>")
			.toString();
	}
}