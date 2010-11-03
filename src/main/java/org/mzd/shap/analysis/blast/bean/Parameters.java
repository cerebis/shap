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
package org.mzd.shap.analysis.blast.bean;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Parameters")
public class Parameters {
	@XStreamAlias("Parameters_matrix")
	private String matrix;
	@XStreamAlias("Parameters_expect")
	private Double expectation;
	@XStreamAlias("Parameters_include")
	private Double include;
	@XStreamAlias("Parameters_sc-match")
	private Integer matchScore;
	@XStreamAlias("Parameters_sc-mismatch")
	private Integer mismatchScore;
	@XStreamAlias("Parameters_gap-open")
	private Integer gapOpenCost;
	@XStreamAlias("Parameters_gap-extend")
	private Integer gapExtensionCost;
	@XStreamAlias("Parameters_filter")
	private String filterOptions;
	@XStreamAlias("Parameters_pattern")
	private String phiBlastPattern;
	@XStreamAlias("Parameters_entrez-query")
	private String requestLimit;
	
	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("matrix",getMatrix())
			.append("expectation",getExpectation())
			.append("include",getInclude())
			.append("matchScore",getMatchScore())
			.append("mismatchScore",getMismatchScore())
			.append("gapOpenCost",getGapOpenCost())
			.append("gapExtensionCost",getGapExtensionCost())
			.append("filterOptions",getFilterOptions())
			.append("phiBlastPattern",getPhiBlastPattern())
			.append("requestLimit",getRequestLimit())
			.toString();
	}
	
	public String getMatrix() {
		return matrix;
	}
	public void setMatrix(String matrix) {
		this.matrix = matrix;
	}
	
	public Double getExpectation() {
		return expectation;
	}
	public void setExpectation(Double expectation) {
		this.expectation = expectation;
	}
	
	public Double getInclude() {
		return include;
	}
	public void setInclude(Double include) {
		this.include = include;
	}
	
	public Integer getMatchScore() {
		return matchScore;
	}
	public void setMatchScore(Integer matchScore) {
		this.matchScore = matchScore;
	}
	
	public Integer getMismatchScore() {
		return mismatchScore;
	}
	public void setMismatchScore(Integer mismatchScore) {
		this.mismatchScore = mismatchScore;
	}
	
	public Integer getGapOpenCost() {
		return gapOpenCost;
	}
	public void setGapOpenCost(Integer gapOpenCost) {
		this.gapOpenCost = gapOpenCost;
	}
	
	public Integer getGapExtensionCost() {
		return gapExtensionCost;
	}
	public void setGapExtensionCost(Integer gapExtensionCost) {
		this.gapExtensionCost = gapExtensionCost;
	}
	
	public String getFilterOptions() {
		return filterOptions;
	}
	public void setFilterOptions(String filterOptions) {
		this.filterOptions = filterOptions;
	}
	
	public String getPhiBlastPattern() {
		return phiBlastPattern;
	}
	public void setPhiBlastPattern(String phiBlastPattern) {
		this.phiBlastPattern = phiBlastPattern;
	}
	
	public String getRequestLimit() {
		return requestLimit;
	}
	public void setRequestLimit(String requestLimit) {
		this.requestLimit = requestLimit;
	}
	
}
