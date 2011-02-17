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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Models a generic response to jQuery DataTables queries.
 * 
 * Instances are expected to be converted to JSON by Jackson.
 * 
 */
public class DataTableResponse {
	private static Log logger = LogFactory.getLog(DataTableResponse.class);
	private Long iTotalRecords;
	private Long iTotalDisplayRecords;
	private String sEcho;
	private List<Object[]> aaData = new ArrayList<Object[]>();
	private String[] columnNames;
	private final DateFormat dateFormat;
	private final DecimalFormat sciFormat;
	private final DecimalFormat deciFormat;

	public DataTableResponse(String... columnNames) {
		dateFormat = DateFormat.getDateInstance();
		sciFormat = new DecimalFormat("0.000E0");
		deciFormat = new DecimalFormat();
		deciFormat.setMaximumFractionDigits(2);
		this.columnNames = columnNames;
	}
	
	public String safeFormat(Format fmt, Object obj) {
		try {
			return fmt.format(obj);
		}
		catch (IllegalArgumentException ex) {
			logger.debug("failed to format object [" + obj + "] using [" + fmt + "]",ex);
			return "";
		}
	}
	
	public String deciFormat(Object obj) {
		return safeFormat(deciFormat,obj);
	}
	
	public String sciFormat(Object obj) {
		return safeFormat(sciFormat,obj);
	}
	
	public String dateFormat(Object obj) {
		return safeFormat(dateFormat,obj);
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
	
	@JsonIgnore
	public Integer getColumnIndex(String name) {
		for (int i=0; i<columnNames.length; i++) {
			if (columnNames[i].equals(name)) {
				return i;
			}
		}
		return -1;
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