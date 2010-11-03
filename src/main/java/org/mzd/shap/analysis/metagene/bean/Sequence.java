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
package org.mzd.shap.analysis.metagene.bean;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("sequence")
public class Sequence {
	@XStreamAlias("id")
	private String identifier;
	@XStreamAlias("desc")
	private String description;
	@XStreamAlias("gc")
	private Double gcContent;
	@XStreamAlias("domain")
	private Domain domain;
	@XStreamAlias("rbs")
	private Double rbsContent;
	@XStreamImplicit
	private List<Orf> orfs = new ArrayList<Orf>();
	
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Double getGcContent() {
		return gcContent;
	}
	public void setGcContent(Double gcContent) {
		this.gcContent = gcContent;
	}
	
	public Double getRbsContent() {
		return rbsContent;
	}
	public void setRbsContent(Double rbsContent) {
		this.rbsContent = rbsContent;
	}
	
	public Domain getDomain() {
		return domain;
	}
	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	
	public List<Orf> getOrfs() {
		return orfs;
	}
	public void setOrfs(List<Orf> orfs) {
		this.orfs = orfs;
	}
	public void addOrf(Orf orf) {
		getOrfs().add(orf);
	}

}
