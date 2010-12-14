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
package org.mzd.shap.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

@Entity
@Table(name="Alignments")
public class Alignment {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ALIGNMENT_ID")
	private Integer id;
	@Min(1)
	@NotNull
	private Integer queryStart;
	@Min(1)
	@NotNull
	private Integer queryEnd;
	@Min(1)
	@NotNull
	private Integer subjectStart;
	@Min(1)
	@NotNull
	private Integer subjectEnd;
	@Type(type="text")
	@NotNull
	private String querySeq;
	@Type(type="text")
	@NotNull
	private String subjectSeq;
	@Type(type="text")
	private String consensusSeq;

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id",getId())
			.append("queryStart",getQueryStart())
			.append("queryEnd",getQueryEnd())
			.append("subjectStart",getSubjectStart())
			.append("subjectEnd",getSubjectEnd())
			.append("querySeq",getQuerySeq())
			.append("consensusSeq",getConsensusSeq())
			.append("subjectSeq",getSubjectSeq())
			.toString();
	}
	
	public Alignment() {/*...*/}
	
	public Integer getId() {
		return id;
	}
	protected void setId(Integer id) {
		this.id = id;
	}

	public String getConsensusSeq() {
		return consensusSeq;
	}

	public void setConsensusSeq(String consensusSeq) {
		this.consensusSeq = consensusSeq;
	}

	public Integer getQueryEnd() {
		return queryEnd;
	}

	public void setQueryEnd(Integer queryEnd) {
		this.queryEnd = queryEnd;
	}

	public String getQuerySeq() {
		return querySeq;
	}

	public void setQuerySeq(String querySeq) {
		this.querySeq = querySeq;
	}

	public Integer getQueryStart() {
		return queryStart;
	}

	public void setQueryStart(Integer queryStart) {
		this.queryStart = queryStart;
	}

	public Integer getSubjectEnd() {
		return subjectEnd;
	}

	public void setSubjectEnd(Integer subjectEnd) {
		this.subjectEnd = subjectEnd;
	}

	public String getSubjectSeq() {
		return subjectSeq;
	}

	public void setSubjectSeq(String subjectSeq) {
		this.subjectSeq = subjectSeq;
	}

	public Integer getSubjectStart() {
		return subjectStart;
	}

	public void setSubjectStart(Integer subjectStart) {
		this.subjectStart = subjectStart;
	}

}
