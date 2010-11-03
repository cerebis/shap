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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Hit")
public class Hit {
	@XStreamAlias("Hit_num")
	private Integer number; 
	@XStreamAlias("Hit_id")
	private String id; 
	@XStreamAlias("Hit_def")
	private String definition; 
	@XStreamAlias("Hit_accession")
    private String accession; 
	@XStreamAlias("Hit_len")
    private Integer length; 
	@XStreamAlias("Hit_hsps")
    private List<Hsp> hsps = new ArrayList<Hsp>();
    
    @Override
    public String toString() {
    	return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
    		.append("number",getNumber())
    		.append("id",getId())
    		.append("definition",getDefinition())
    		.append("accession",getAccession())
    		.append("length",getLength())
    		.append("hsps",getHsps())
    		.toString();
    }
    
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}
	
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	
	public List<Hsp> getHsps() {
		return hsps;
	}
	public void setHsps(List<Hsp> hsps) {
		this.hsps = hsps;
	}
	public void addHsp(Hsp hsp) {
		getHsps().add(hsp);
	}

}
