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
import org.apache.commons.lang.builder.ToStringBuilder;

public class Query {
	private String name;
	private String accession;
	private String description;
	
	public Query() {/*...*/}
	
	public Query(String name, String accession, String description) {
		super();
		this.name = name;
		this.accession = accession;
		this.description = description;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("name",getName())
			.append("accession",getAccession())
			.append("description",getDescription()).toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Query == false) {
			return false;
		}
		
		if (obj == this) {
			return true;
		}
		
		Query rhs = (Query)obj;
		return new EqualsBuilder()
			.append(getName(),rhs.getName())
			.append(getAccession(), rhs.getAccession())
			.append(getDescription(), rhs.getDescription())
			.isEquals();
	}
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
