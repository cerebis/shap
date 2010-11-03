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

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("output")
public class Output {
	@XStreamAlias("predictionTitle")
	private String predictionTitle;
	@XStreamAlias("predictor")
	private String predictor;
	@XStreamAlias("reference")
	private String reference;
	@XStreamAlias("predictionDate")
	private String predictionDate;
	@XStreamAlias("entries")
	private List<Entry> entries = new ArrayList<Entry>();
	
	public String getPredictionTitle() {
		return predictionTitle;
	}
	public void setPredictionTitle(String predictionTitle) {
		this.predictionTitle = predictionTitle;
	}
	
	public String getPredictor() {
		return predictor;
	}
	public void setPredictor(String predictor) {
		this.predictor = predictor;
	}
	
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public String getPredictionDate() {
		return predictionDate;
	}
	public void setPredictionDate(String predictionDate) {
		this.predictionDate = predictionDate;
	}
	
	public List<Entry> getEntries() {
		return entries;
	}
	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}
	public void addEntry(Entry entry) {
		getEntries().add(entry);
	}
	
	
}
