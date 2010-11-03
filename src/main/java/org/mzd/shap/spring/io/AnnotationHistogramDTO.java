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
package org.mzd.shap.spring.io;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;

public class AnnotationHistogramDTO {
	private String accession;
	private String description;
	private BigDecimal frequency;
	private Double weightedFrequency;
	
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public BigDecimal getFrequency() {
		return frequency;
	}
	public void setFrequency(BigDecimal frequency) {
		this.frequency = frequency;
	}
	public Double getWeightedFrequency() {
		return weightedFrequency;
	}
	public void setWeightedFrequency(Double weightedFrequency) {
		this.weightedFrequency = weightedFrequency;
	}
	
	@Override
	public String toString() {
		if (getAccession() == null && getDescription() == null) {
			return "unassigned" + 
				"\t" + getFrequency() +
				"\t" + getWeightedFrequency() +
				"\t";
		}
		else {
			return getAccession() + 
				"\t" + getFrequency() + 
				"\t" + getWeightedFrequency() +
				"\t" + getDescription();
		}
	}
	
	public void write(OutputStream os) throws IOException {
		os.write((toString() + '\n').getBytes());
	}
}