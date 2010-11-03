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
package org.mzd.shap.analysis.aragorn.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("gene")
public class Gene {
	@XStreamAsAttribute
	private Integer start;
	@XStreamAsAttribute
	private Integer stop;
	@XStreamAsAttribute
	private String strand;
	@XStreamAsAttribute
	private String species;
	@XStreamAsAttribute
	private String anticodon;
	@XStreamAlias("anticodon-position")
	@XStreamAsAttribute
	private String anticodonPosition;
	@XStreamAlias("intron-position")
	@XStreamAsAttribute
	private String intronPosition;
	@XStreamAlias("intron-length")
	@XStreamAsAttribute
	private String intronLength;
	
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
	public String getStrand() {
		return strand;
	}
	public void setStrand(String strand) {
		this.strand = strand;
	}
	public String getSpecies() {
		return species;
	}
	public void setSpecies(String species) {
		this.species = species;
	}
	public String getAnticodon() {
		return anticodon;
	}
	public void setAnticodon(String anticodon) {
		this.anticodon = anticodon;
	}
	public String getAnticodonPosition() {
		return anticodonPosition;
	}
	public void setAnticodonPosition(String anticodonPosition) {
		this.anticodonPosition = anticodonPosition;
	}
	public String getIntronPosition() {
		return intronPosition;
	}
	public void setIntronPosition(String intronPosition) {
		this.intronPosition = intronPosition;
	}
	public String getIntronLength() {
		return intronLength;
	}
	public void setIntronLength(String intronLength) {
		this.intronLength = intronLength;
	}
	
	
}
