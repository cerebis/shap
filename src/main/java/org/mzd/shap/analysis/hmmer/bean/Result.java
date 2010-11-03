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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("result")
public class Result {
	@XStreamAlias("query")
	private Query query;
	@XStreamAlias("global-hits")
	private List<GlobalHit> globalHits;
	@XStreamAlias("domain-hits")
	private List<DomainHit> domainHits;
	
	public Result() {
		this(null,new ArrayList<GlobalHit>(),new ArrayList<DomainHit>());
	}
	
	public Result(String database, Query query) {
		this(query,new ArrayList<GlobalHit>(),new ArrayList<DomainHit>());
	}
	
	public Result(Query query, List<GlobalHit> globalHits, List<DomainHit> domainHits) {
		this.query = query;
		this.globalHits = globalHits;
		this.domainHits = domainHits;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("query",getQuery())
			.append("globalHits",getGlobalHits())
			.append("domainHits",getDomainHits()).toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Result == false) {
			return false;
		}
		
		if (obj == this) {
			return true;
		}
		
		Result rhs = (Result)obj;
		return new EqualsBuilder()
			.append(getQuery(),rhs.getQuery())
			.append(getGlobalHits(),rhs.getGlobalHits())
			.append(getDomainHits(),rhs.getDomainHits())
			.isEquals();
	}
	
	public List<GlobalHit> getGlobalHits() {
		return globalHits;
	}

	public void setGlobalHits(List<GlobalHit> globalHits) {
		this.globalHits = globalHits;
	}
	
	public void addGlobalHits(GlobalHit globalHit) {
		getGlobalHits().add(globalHit);
	}

	public List<DomainHit> getDomainHits() {
		return domainHits;
	}

	public void setDomainHits(List<DomainHit> domainHits) {
		this.domainHits = domainHits;
	}
	
	public void addDomainHits(DomainHit domainHits) {
		getDomainHits().add(domainHits);
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}
}
