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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("target")
public class Target {
	@XStreamAlias("feature-id")
	@XStreamAsAttribute
	private Integer featureId;
	@XStreamAlias("sequence-id")
	@XStreamAsAttribute
	private Integer sequenceId;
	@XStreamAlias("sample-name")
	@XStreamAsAttribute
	private String sampleName;
	@XStreamAlias("project-name")
	@XStreamAsAttribute
	private String projectName;
	
	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
			.append("featureId",getFeatureId())
			.append("sequenceId",getSequenceId())
			.append("sampleName",getSampleName())
			.append("projectName",getProjectName())
			.toString();
	}
	
	public boolean isFeature() {
		return getFeatureId() != null;
	}
	
	public boolean isSequence() {
		return getSequenceId() != null;
	}
	
	public boolean isSample() {
		return getSampleName() != null;
	}

	public void validate() throws PlanException {
		if (getFeatureId() != null && 
				(getSequenceId() != null || getSampleName() != null || getProjectName() != null)) {
			throw new PlanException("Valid targets can be only featureId, sequenceId or (sampleName and projectName)");
		}
		else if (getSequenceId() != null && 
				(getSampleName() != null || getProjectName() != null)) {
			throw new PlanException("Valid targets can be only featureId, sequenceId or (sampleName and projectName)");
		}
		else if ((getSampleName() != null && getProjectName() == null) || 
				(getSampleName() == null && getProjectName() != null)) {
			throw new PlanException("Valid targets can be only featureId, sequenceId or (sampleName and projectName)");
		}
	}

	public Integer getFeatureId() {
		return featureId;
	}
	public void setFeatureId(Integer featureId) {
		this.featureId = featureId;
	}
	
	public Integer getSequenceId() {
		return sequenceId;
	}
	public void setSequenceId(Integer sequenceId) {
		this.sequenceId = sequenceId;
	}
	
	public String getSampleName() {
		return sampleName;
	}
	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
