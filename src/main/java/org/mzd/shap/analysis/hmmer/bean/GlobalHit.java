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
package org.mzd.shap.analysis.hmmer.bean;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("hit")
public class GlobalHit {
	@XStreamAlias("model")
	private String model;
	@XStreamAlias("description")
	private String description;
	@XStreamAlias("score")
	private Double score;
	@XStreamAlias("evalue")
	private Double evalue;
	@XStreamAlias("ndom")
	private Integer ndom;
	
	public GlobalHit() {/*...*/}
	
	public GlobalHit(String model, String description, Double score, Double evalue, Integer ndom) {
		this.model = model;
		this.description = description;
		this.score = score;
		this.evalue = evalue;
		this.ndom = ndom;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("model",getModel())
			.append("description",getDescription())
			.append("evalue",getEvalue())
			.append("ndom",getNdom())
			.append("score",getScore()).toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GlobalHit == false) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		GlobalHit rhs = (GlobalHit)obj;
		return new EqualsBuilder()
			.append(getModel(),rhs.getModel())
			.append(getDescription(),rhs.getDescription())
			.append(getScore(),rhs.getScore())
			.append(getEvalue(),rhs.getEvalue())
			.append(getNdom(),rhs.getNdom())
			.isEquals();
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getEvalue() {
		return evalue;
	}

	public void setEvalue(Double evalue) {
		this.evalue = evalue;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Integer getNdom() {
		return ndom;
	}

	public void setNdom(Integer ndom) {
		this.ndom = ndom;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}
}
