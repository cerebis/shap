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
package org.mzd.shap.spring.io;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.InitializingBean;

public class DescriptionEditor implements InitializingBean {
	private String editPattern;
	private Pattern pattern = null;
	
	public DescriptionEditor() {/*...*/}
	
	public DescriptionEditor(String editPattern) {
		this.editPattern = editPattern;
		compilePattern();
	}
	
	public void afterPropertiesSet() throws Exception {
		compilePattern();
	}
	
	public void compilePattern() {
		setPattern(Pattern.compile(getEditPattern()));
	}
	
	public String getMinimalDescription(String fullDescription) {
		Matcher m = getPattern().matcher(fullDescription);
		
		if (m.groupCount() <= 0 || !m.matches()) {
			return fullDescription;
		}
		
		return m.group(1);
	}
	
	protected Pattern getPattern() {
		return pattern;
	}
	protected void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public String getEditPattern() {
		return editPattern;
	}
	public void setEditPattern(String editPattern) {
		this.editPattern = editPattern;
	}
	
}