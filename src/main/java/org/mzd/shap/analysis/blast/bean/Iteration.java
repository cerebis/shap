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

@XStreamAlias("Iteration")
public class Iteration {
	@XStreamAlias("Iteration_iter-num")
    private Integer number; 
	@XStreamAlias("Iteration_query-ID")
    private String queryId; 
	@XStreamAlias("Iteration_query-def")
    private String queryDefinition; 
	@XStreamAlias("Iteration_query-len")
    private Integer queryLength; 
	@XStreamAlias("Iteration_hits")
    private List<Hit> hits = new ArrayList<Hit>(); 
	@XStreamAlias("Iteration_stat")
    private Statistics statistics; 
	@XStreamAlias("Iteration_message")
    private String message;
    
    @Override
    public String toString() {
    	return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
    		.append("number",getNumber())
    		.append("queryId",getQueryId())
    		.append("queryDefinition",getQueryDefinition())
    		.append("queryLength",getQueryLength())
    		.append("hits",getHits())
    		.append("statistics",getStatistics())
    		.append("message",getMessage())
    		.toString();
    }
    
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	public String getQueryId() {
		return queryId;
	}
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}
	
	public String getQueryDefinition() {
		return queryDefinition;
	}
	public void setQueryDefinition(String queryDefinition) {
		this.queryDefinition = queryDefinition;
	}
	
	public Integer getQueryLength() {
		return queryLength;
	}
	public void setQueryLength(Integer queryLength) {
		this.queryLength = queryLength;
	}
	
	public List<Hit> getHits() {
		return hits;
	}
	public void setHits(List<Hit> hits) {
		this.hits = hits;
	}
	public void addHit(Hit hit) {
		getHits().add(hit);
	}
	
	public Statistics getStatistics() {
		return statistics;
	}
	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}
