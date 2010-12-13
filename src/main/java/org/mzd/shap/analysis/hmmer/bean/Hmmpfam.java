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

@XStreamAlias("hmmpfam")
public class Hmmpfam {
	@XStreamAlias("database-file")
	private String databaseFile;
	@XStreamAlias("sequence-file")
	private String sequenceFile;
	@XStreamAlias("results")
	private List<Result> results;
	
	public Hmmpfam() {
		this(null,null,new ArrayList<Result>());
	}
	
	public Hmmpfam(String databaseFile, String sequenceFile) {
		this(databaseFile,sequenceFile,new ArrayList<Result>());
	}
	
	public Hmmpfam(String databaseFile, String sequenceFile, List<Result> results) {
		this.databaseFile = databaseFile;
		this.sequenceFile = sequenceFile;
		this.results = results;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("databaseFile",getDatabaseFile())
			.append("sequenceFile",getSequenceFile())
			.append("results",getResults()).toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Hmmpfam == false) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		Hmmpfam rhs = (Hmmpfam)obj;
		return new EqualsBuilder()
			.append(getDatabaseFile(),rhs.getDatabaseFile())
			.append(getSequenceFile(),rhs.getSequenceFile())
			.append(getResults(),rhs.getResults())
			.isEquals();
	}
	
	public String getDatabaseFile() {
		return databaseFile;
	}

	public void setDatabaseFile(String databaseFile) {
		this.databaseFile = databaseFile;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}
	
	public void addResult(Result result) {
		getResults().add(result);
	}

	public String getSequenceFile() {
		return sequenceFile;
	}

	public void setSequenceFile(String sequenceFile) {
		this.sequenceFile = sequenceFile;
	}
	
}
