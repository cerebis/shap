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

@XStreamAlias("Hsp")
public class Hsp {
	@XStreamAlias("Hsp_num")
	private Integer number; 
	@XStreamAlias("Hsp_bit-score")
	private Double bitScore; 
	@XStreamAlias("Hsp_score")
	private Double score; 
	@XStreamAlias("Hsp_evalue")
	private Double evalue; 
	@XStreamAlias("Hsp_query-from")
	private Integer queryFrom; 
	@XStreamAlias("Hsp_query-to")
	private Integer queryTo; 
	@XStreamAlias("Hsp_hit-from")
	private Integer hitFrom; 
	@XStreamAlias("Hsp_hit-to")
	private Integer hitTo; 
	@XStreamAlias("Hsp_pattern-from")
	private Integer patternFrom; 
	@XStreamAlias("Hsp_pattern-to")
	private Integer patternTo; 
	@XStreamAlias("Hsp_query-frame")
	private Integer queryFrame; 
	@XStreamAlias("Hsp_hit-frame")
	private Integer hitFrame; 
	@XStreamAlias("Hsp_identity")
	private Integer identities; 
	@XStreamAlias("Hsp_positive")
	private Integer positives; 
	@XStreamAlias("Hsp_gaps")
	private Integer gaps; 
	@XStreamAlias("Hsp_align-len")
	private Integer alignLength;
	@XStreamAlias("Hsp_density")
	private Integer scoreDensity; 
	@XStreamAlias("Hsp_qseq")
	private String querySequence;  
	@XStreamAlias("Hsp_hseq")
	private String hitSequence; 
	@XStreamAlias("Hsp_midline")
	private String midline;
	
	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("number",getNumber())
			.append("bitScore",getBitScore())
			.append("score",getScore())
			.append("evalue",getEvalue())
			.append("queryFrom",getQueryFrom())
			.append("queryTo",getQueryTo())
			.append("hitFrom",getHitFrom())
			.append("hitTo",getHitTo())
			.append("patternFrom",getPatternFrom())
			.append("patternTo",getPatternTo())
			.append("queryFrame",getQueryFrame())
			.append("hitFrame",getHitFrame())
			.append("identities",getIdentities())
			.append("positives",getPositives())
			.append("gaps",getGaps())
			.append("alignLength",getAlignLength())
			.append("scoreDensity",getScoreDensity())
			.append("querySequence",getQuerySequence())
			.append("hitSequence",getHitSequence())
			.append("midline",getMidline())
			.toString();
	}
	
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	public Double getBitScore() {
		return bitScore;
	}
	public void setBitScore(Double bitScore) {
		this.bitScore = bitScore;
	}
	
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	
	public Double getEvalue() {
		return evalue;
	}
	public void setEvalue(Double evalue) {
		this.evalue = evalue;
	}
	
	public Integer getQueryFrom() {
		return queryFrom;
	}
	public void setQueryFrom(Integer queryFrom) {
		this.queryFrom = queryFrom;
	}
	
	public Integer getQueryTo() {
		return queryTo;
	}
	public void setQueryTo(Integer queryTo) {
		this.queryTo = queryTo;
	}
	
	public Integer getHitFrom() {
		return hitFrom;
	}
	public void setHitFrom(Integer hitFrom) {
		this.hitFrom = hitFrom;
	}
	
	public Integer getHitTo() {
		return hitTo;
	}
	public void setHitTo(Integer hitTo) {
		this.hitTo = hitTo;
	}
	
	public Integer getPatternFrom() {
		return patternFrom;
	}
	public void setPatternFrom(Integer patternFrom) {
		this.patternFrom = patternFrom;
	}
	
	public Integer getPatternTo() {
		return patternTo;
	}
	public void setPatternTo(Integer patternTo) {
		this.patternTo = patternTo;
	}
	
	public Integer getQueryFrame() {
		return queryFrame;
	}
	public void setQueryFrame(Integer queryFrame) {
		this.queryFrame = queryFrame;
	}
	
	public Integer getHitFrame() {
		return hitFrame;
	}
	public void setHitFrame(Integer hitFrame) {
		this.hitFrame = hitFrame;
	}
	
	public Integer getIdentities() {
		return identities;
	}
	public void setIdentities(Integer identities) {
		this.identities = identities;
	}
	
	public Integer getPositives() {
		return positives;
	}
	public void setPositives(Integer positives) {
		this.positives = positives;
	}
	
	public Integer getGaps() {
		return gaps;
	}
	public void setGaps(Integer gaps) {
		this.gaps = gaps;
	}
	
	public Integer getAlignLength() {
		return alignLength;
	}
	public void setAlignLength(Integer alignLength) {
		this.alignLength = alignLength;
	}
	
	public Integer getScoreDensity() {
		return scoreDensity;
	}
	public void setScoreDensity(Integer scoreDensity) {
		this.scoreDensity = scoreDensity;
	}
	
	public String getQuerySequence() {
		return querySequence;
	}
	public void setQuerySequence(String querySequence) {
		this.querySequence = querySequence;
	}
	
	public String getHitSequence() {
		return hitSequence;
	}
	public void setHitSequence(String hitSequence) {
		this.hitSequence = hitSequence;
	}
	
	public String getMidline() {
		return midline;
	}
	public void setMidline(String midline) {
		this.midline = midline;
	}

}
