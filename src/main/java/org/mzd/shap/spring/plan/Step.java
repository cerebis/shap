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
package org.mzd.shap.spring.plan;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("step")
public class Step {
	@XStreamAlias("id")
	@XStreamAsAttribute
	private String id;
	@XStreamAlias("type")
	@XStreamAsAttribute
	private AnalysisType type;
	@XStreamImplicit(itemFieldName="analyzer")
	private List<String> analyzers = new ArrayList<String>();

	public void validate() throws PlanException {
		if (getType() == null) {
			throw new PlanException("A step must define a type: DETECTION or ANNOTATION");
		}
		if (getAnalyzers() == null || getAnalyzers().size() == 0) {
			throw new PlanException("A step must contain at least one analyzer");
		}
	}
	
	public void validate(Target ...targets) throws PlanException {
		validate();
		for (Target t : targets) {
			if (getType() == AnalysisType.ANNOTATION && t.isFeature()) {
				throw new PlanException("A step cannot apply detection to a feature");
			}
		}
		if (getType() == null) {
			throw new PlanException("A step must define a type: DETECTION or ANNOTATION");
		}
		if (getAnalyzers() == null || getAnalyzers().size() == 0) {
			throw new PlanException("A step must contain at least one analyzer");
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
			.append("id",getId())
			.append("type",getType())
			.append("analyzers",getAnalyzers())
			.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Step == false) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		Step other = (Step)obj;
		return new EqualsBuilder()
			.append(getId(), other.getId())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,37)
			.append(getId())
			.toHashCode();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public AnalysisType getType() {
		return type;
	}
	public void setType(AnalysisType type) {
		this.type = type;
	}
	
	public List<String> getAnalyzers() {
		return analyzers;
	}
	public void setAnalyzers(List<String> analyzers) {
		this.analyzers = analyzers;
	}
	public void addAnalyzer(String analyzerName) {
		if (!getAnalyzers().contains(analyzerName)) {
			getAnalyzers().add(analyzerName);
		}
	}
	
}
