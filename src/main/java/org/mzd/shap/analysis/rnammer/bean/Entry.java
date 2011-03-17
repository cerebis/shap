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
package org.mzd.shap.analysis.rnammer.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("entry")
public class Entry {
	@XStreamAlias("mol")
	private String molecule;
	@XStreamAlias("feature")
	private String feature;
	@XStreamAlias("start")
	private Integer start;
	@XStreamAlias("stop")
	private Integer stop;
	@XStreamAlias("direction")
	private String direction;
	@XStreamAlias("score")
	private Double score;
	@XStreamAlias("evalue")
	private Double evalue;
	@XStreamAlias("sequenceEntry")
	private String sequenceEntry;
	@XStreamAlias("sequenceEntryLength")
	private Integer sequenceEntryLength;
	@XStreamAlias("sequence")
	private String sequence;
	@XStreamAlias("model_string")
	private String modelString;
	@XStreamAlias("match_string")
	private String matchString;
	@XStreamAlias("query_string")
	private String queryString;
	
	public String getMolecule() {
		return molecule;
	}
	public void setMolecule(String molecule) {
		this.molecule = molecule;
	}
	public String getFeature() {
		return feature;
	}
	public void setFeature(String feature) {
		this.feature = feature;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getStop() {
		return stop;
	}
	public void setStop(Integer stop) {
		this.stop = stop;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
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
	public String getSequenceEntry() {
		return sequenceEntry;
	}
	public void setSequenceEntry(String sequenceEntry) {
		this.sequenceEntry = sequenceEntry;
	}
	public Integer getSequenceEntryLength() {
		return sequenceEntryLength;
	}
	public void setSequenceEntryLength(Integer sequenceEntryLength) {
		this.sequenceEntryLength = sequenceEntryLength;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public String getModelString() {
		return modelString;
	}
	public void setModelString(String modelString) {
		this.modelString = modelString;
	}
	public String getMatchString() {
		return matchString;
	}
	public void setMatchString(String matchString) {
		this.matchString = matchString;
	}
	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
}
