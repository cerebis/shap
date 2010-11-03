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
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("hit")
public class DomainHit {
	@XStreamAlias("model")
	private String model;
	@XStreamAlias("index")
	private Integer index;
	@XStreamAlias("ndom")
	private Integer ndom;
	@XStreamAlias("seq-from")
	private Integer seqFrom;
	@XStreamAlias("seq-to")
	private Integer seqTo;
	@XStreamAlias("seq-start")
	private Boolean seqStart;
	@XStreamAlias("seq-end")
	private Boolean seqEnd;
	@XStreamAlias("hmm-from")
	private Integer hmmFrom;
	@XStreamAlias("hmm-to")
	private Integer hmmTo;
	@XStreamAlias("hmm-start")
	private Boolean hmmStart;
	@XStreamAlias("hmm-end")
	private Boolean hmmEnd;
	@XStreamAlias("score")
	private Double score;
	@XStreamAlias("evalue")
	private Double evalue;
	@XStreamAlias("query-seq")
	private String querySeq;
	@XStreamAlias("subject-seq")
	private String subjectSeq;
	@XStreamAlias("consensus-seq")
	private String consensusSeq;
	
	public DomainHit() {/*...*/}
	
	public DomainHit(
			String model, 
			Integer index, Integer ndom,
			Integer seqFrom, Integer seqTo,	Boolean seqStart, Boolean seqEnd, 
			Integer hmmFrom, Integer hmmTo,	Boolean hmmStart, Boolean hmmEnd, 
			Double score, Double evalue) {
		
		this.model = model;
		this.index = index;
		this.ndom = ndom;
		this.seqFrom = seqFrom;
		this.seqTo = seqTo;
		this.seqStart = seqStart;
		this.seqEnd = seqEnd;
		this.hmmFrom = hmmFrom;
		this.hmmTo = hmmTo;
		this.hmmStart = hmmStart;
		this.hmmEnd = hmmEnd;
		this.score = score;
		this.evalue = evalue;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("model",getModel())
			.append("index",getIndex())
			.append("ndom",getNdom())
			.append("seqFrom",getSeqFrom())
			.append("seqTo",getSeqTo())
			.append("seqStart",getSeqStart())
			.append("seqEnd",getSeqEnd())
			.append("hmmFrom",getHmmFrom())
			.append("hmmTo",getHmmTo())
			.append("hmmStart",getHmmStart())
			.append("hmmEnd",getHmmEnd())
			.append("score",getScore())
			.append("evalue",getEvalue())
			.append("querySeq",getQuerySeq())
			.append("subjectSeq",getSubjectSeq())
			.append("consensusSeq",getConsensusSeq())
			.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,31)
			.append(getModel())
			.append(getIndex())
			.append(getNdom())
			.append(getSeqFrom())
			.append(getSeqTo())
			.append(getSeqStart())
			.append(getSeqEnd())
			.append(getHmmFrom())
			.append(getHmmTo())
			.append(getHmmStart())
			.append(getHmmEnd())
			.append(getScore())
			.append(getEvalue())
			.append(getQuerySeq())
			.append(getSubjectSeq())
			.append(getConsensusSeq())
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DomainHit == false) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		DomainHit rhs = (DomainHit)obj;
		return new EqualsBuilder()
			.append(getModel(),rhs.getModel())
			.append(getIndex(),rhs.getIndex())
			.append(getNdom(),rhs.getNdom())
			.append(getSeqFrom(),rhs.getSeqFrom())
			.append(getSeqTo(),rhs.getSeqTo())
			.append(getSeqStart(),rhs.getSeqStart())
			.append(getSeqEnd(),rhs.getSeqEnd())
			.append(getHmmFrom(),rhs.getHmmFrom())
			.append(getHmmTo(),rhs.getHmmTo())
			.append(getHmmStart(),rhs.getHmmStart())
			.append(getHmmEnd(),rhs.getHmmEnd())
			.append(getScore(),rhs.getScore())
			.append(getEvalue(),rhs.getEvalue())
			.append(getQuerySeq(),rhs.getQuerySeq())
			.append(getSubjectSeq(),rhs.getSubjectSeq())
			.append(getConsensusSeq(),rhs.getConsensusSeq())
			.isEquals();
	}

	public Double getEvalue() {
		return evalue;
	}

	public void setEvalue(Double evalue) {
		this.evalue = evalue;
	}

	public Integer getHmmFrom() {
		return hmmFrom;
	}

	public void setHmmFrom(Integer hmmFrom) {
		this.hmmFrom = hmmFrom;
	}

	public Integer getHmmTo() {
		return hmmTo;
	}

	public void setHmmTo(Integer hmmTo) {
		this.hmmTo = hmmTo;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
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

	public Integer getSeqFrom() {
		return seqFrom;
	}

	public void setSeqFrom(Integer seqFrom) {
		this.seqFrom = seqFrom;
	}

	public Integer getSeqTo() {
		return seqTo;
	}

	public void setSeqTo(Integer seqTo) {
		this.seqTo = seqTo;
	}

	public Boolean getHmmEnd() {
		return hmmEnd;
	}

	public void setHmmEnd(Boolean hmmEnd) {
		this.hmmEnd = hmmEnd;
	}

	public Boolean getHmmStart() {
		return hmmStart;
	}

	public void setHmmStart(Boolean hmmStart) {
		this.hmmStart = hmmStart;
	}

	public Boolean getSeqEnd() {
		return seqEnd;
	}

	public void setSeqEnd(Boolean seqEnd) {
		this.seqEnd = seqEnd;
	}

	public Boolean getSeqStart() {
		return seqStart;
	}

	public void setSeqStart(Boolean seqStart) {
		this.seqStart = seqStart;
	}

	public String getConsensusSeq() {
		return consensusSeq;
	}

	public void setConsensusSeq(String consensusSeq) {
		this.consensusSeq = consensusSeq;
	}

	public String getQuerySeq() {
		return querySeq;
	}

	public void setQuerySeq(String querySeq) {
		this.querySeq = querySeq;
	}

	public String getSubjectSeq() {
		return subjectSeq;
	}

	public void setSubjectSeq(String subjectSeq) {
		this.subjectSeq = subjectSeq;
	}
}
