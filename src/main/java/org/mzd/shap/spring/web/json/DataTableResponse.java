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
package org.mzd.shap.spring.web.json;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Models a generic response to jQuery DataTables queries.
 * 
 * Instances are expected to be converted to JSON by Jackson.
 * 
 */
public class DataTableResponse {
	private Long iTotalRecords;
	private Long iTotalDisplayRecords;
	private String sEcho;
	private List<Object[]> aaData = new ArrayList<Object[]>();
	private String[] columnNames;
	
	public DataTableResponse(String... columnNames) {
		this.columnNames = columnNames;
	}
	
	@JsonIgnore
	public void addAll(List<Object[]> rows) {
		getAaData().addAll(rows);
	}
	
	@JsonIgnore
	protected String columnNamesToString() {
		StringBuffer buffer = new StringBuffer();
		for (String cn : this.columnNames) {
			buffer.append(cn + ",");
		}
		buffer.deleteCharAt(buffer.length()-1);
		return buffer.toString();
	}
	
	@JsonIgnore
	public String getColumnName(int index) {
		return this.columnNames[index];
	}
	
	public String getsColumns() {
		return columnNamesToString();
	}
	public List<Object[]>getAaData() {
		return aaData;
	}
	public void setAaData(List<Object[]> aaData) {
		this.aaData = aaData;
	}
	public Long getiTotalRecords() {
		return iTotalRecords;
	}
	public void setiTotalRecords(Long iTotalRecords) {
		this.iTotalRecords = iTotalRecords;
	}
	public Long getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}
	public void setiTotalDisplayRecords(Long iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}
	public String getsEcho() {
		return sEcho;
	}
	public void setsEcho(String sEcho) {
		this.sEcho = sEcho;
	}
}