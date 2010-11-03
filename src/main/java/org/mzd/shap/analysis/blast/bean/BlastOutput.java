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

@XStreamAlias("BlastOutput")
public class BlastOutput {
	@XStreamAlias("BlastOutput_program")
   	private String program;
	@XStreamAlias("BlastOutput_version")
	private String version;
	@XStreamAlias("BlastOutput_reference")
	private String reference;
	@XStreamAlias("BlastOutput_db")
	private String database;
	@XStreamAlias("BlastOutput_query-ID")
	private String queryId; 
	@XStreamAlias("BlastOutput_query-def")
	private String queryDefinition;
	@XStreamAlias("BlastOutput_query-len")
	private Integer queryLength;
	@XStreamAlias("BlastOutput_query-seq")
	private String querySequence;
	@XStreamAlias("BlastOutput_param")
	private Parameters parameters;
	@XStreamAlias("BlastOutput_iterations")
	private List<Iteration> iterations = new ArrayList<Iteration>();
	@XStreamAlias("BlastOutput_mbstat")
	private Statistics megablastStatistics; 
	
	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("program",getProgram())
			.append("version",getVersion())
			.append("reference",getReference())
			.append("database",getDatabase())
			.append("queryId",getQueryId())
			.append("queryDefinition",getQueryDefinition())
			.append("queryLength",getQueryLength())
			.append("querySequence",getQuerySequence())
			.append("parameters",getParameters())
			.append("iterations",getIterations())
			.append("megablastStatistics",getMegablastStatistics())
			.toString();
	}
	
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
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
	
	public Parameters getParameters() {
		return parameters;
	}
	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}
	
	public List<Iteration> getIterations() {
		return iterations;
	}
	public void setIterations(List<Iteration> iterations) {
		this.iterations = iterations;
	}
	public void addIteration(Iteration iteration) {
		getIterations().add(iteration);
	}
	
	public String getQuerySequence() {
		return querySequence;
	}
	public void setQuerySequence(String querySequence) {
		this.querySequence = querySequence;
	}
	
	public Statistics getMegablastStatistics() {
		return megablastStatistics;
	}
	public void setMegablastStatistics(Statistics megablastStatistics) {
		this.megablastStatistics = megablastStatistics;
	}
		
}
