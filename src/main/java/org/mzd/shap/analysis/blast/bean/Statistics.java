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

@XStreamAlias("Statistics")
public class Statistics {
	@XStreamAlias("Statistics_db-num")
	private Integer numberSequences;
	@XStreamAlias("Statistics_db-len")
	private Integer databaseLength; 
	@XStreamAlias("Statistics_hsp-len")
	private Integer hspLength; 
	@XStreamAlias("Statistics_eff-space")
	private Double effectiveSearchSpace; 
	@XStreamAlias("Statistics_kappa")
	private Double kappa; 
	@XStreamAlias("Statistics_lambda")
	private Double lambda; 
	@XStreamAlias("Statistics_entropy")
	private Double entropy;
	
	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("numberSequences",getNumberSequences())
			.append("databaseLength",getDatabaseLength())
			.append("hspLength",getHspLength())
			.append("effectiveSearchSapce",getEffectiveSearchSpace())
			.append("kappa",getKappa())
			.append("lambda",getLambda())
			.append("entropy",getEntropy())
			.toString();
	}
	
	public Integer getNumberSequences() {
		return numberSequences;
	}
	public void setNumberSequences(Integer numberSequences) {
		this.numberSequences = numberSequences;
	}
	
	public Integer getDatabaseLength() {
		return databaseLength;
	}
	public void setDatabaseLength(Integer databaseLength) {
		this.databaseLength = databaseLength;
	}
	
	public Integer getHspLength() {
		return hspLength;
	}
	public void setHspLength(Integer hspLength) {
		this.hspLength = hspLength;
	}
	
	public Double getEffectiveSearchSpace() {
		return effectiveSearchSpace;
	}
	public void setEffectiveSearchSpace(Double effectiveSearchSpace) {
		this.effectiveSearchSpace = effectiveSearchSpace;
	}
	
	public Double getKappa() {
		return kappa;
	}
	public void setKappa(Double kappa) {
		this.kappa = kappa;
	}
	
	public Double getLambda() {
		return lambda;
	}
	public void setLambda(Double lambda) {
		this.lambda = lambda;
	}
	
	public Double getEntropy() {
		return entropy;
	}
	public void setEntropy(Double entropy) {
		this.entropy = entropy;
	}

}
