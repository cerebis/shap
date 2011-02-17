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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Range;

/**
 * Models the request from jQuery DataTables.
 * 
 */
public class DataTableRequest {
	@NotNull
	@Min(0)
	private Integer	iDisplayStart;
	@NotNull
	@Range(min=0,max=1000)
	private Integer iDisplayLength;
	@NotNull
	@Range(min=0,max=100)
	private Integer iColumns;
	private String sSearchGlobal;
	@NotNull
	private String sColumns;
	private Boolean	bEscapeRegexGlobal;
	private List<Boolean> bSortable = new ArrayList<Boolean>();
	private List<Boolean> bSearchable = new ArrayList<Boolean>();
	private List<String> sSearch = new ArrayList<String>();
	private List<Boolean> bEscapeRegex = new ArrayList<Boolean>();
	@NotNull
	@Range(min=0,max=100)
	private Integer iSortingCols;
	private List<Integer> iSortCol = new ArrayList<Integer>();
	private List<String> sSortDir = new ArrayList<String>();
	@NotNull
	@Size(min=1)
	private String sEcho;
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("iDisplayStart",getIDisplayStart())
			.append("iDisplayLength",getIDisplayLength())
			.append("iColumns",getIColumns())
			.append("sSearchGlobal",getSSearchGlobal())
			.append("sColumns",getsColumns())
			.append("bEscapeRegexGlobal",getBEscapeRegexGlobal())
			.append("bSortable",getBSortable())
			.append("bSearchable",getBSearchable())
			.append("sSearch",getSSearch())
			.append("bEscapeRegex",getBEscapeRegex())
			.append("iSortingCols",getISortingCols())
			.append("iSortCol",getISortCol())
			.append("sSortDir",getSSortDir())
			.append("sEcho",getSEcho())
			.toString();
	}
	
	public String getsColumns() {
		return sColumns;
	}
	public void setsColumns(String sColumns) {
		this.sColumns = sColumns;
	}

	public String getSortingDirection() {
		for (String s : getSSortDir()) {
			if (s != null) {
				// assuming the first non-null element is correct. There should be only one is simple tables.
				return s;
			}
		}
		return null;
	}
	
	public String getSortedColumn() {
		String[] colNames = getsColumns().split(",");
		Integer idx = getSortedColumnIndex();
		if (idx > colNames.length-1 || idx < 0) {
			throw new RuntimeException("Requested sorting column index outside bounds of column list");
		}
		return colNames[idx];
	}
	
	public Integer getSortedColumnIndex() {
		for (Integer idx : getISortCol()) {
			if (idx != null) {
				// assuming the first non-null element is correct. There should be only one is simple tables.
				return idx;
			}
		}
		return null;
	}
	
	public Integer getIDisplayStart() {
		return iDisplayStart;
	}
	public void setIDisplayStart(Integer iDisplayStart) {
		this.iDisplayStart = iDisplayStart;
	}

	public Integer getIDisplayLength() {
		return iDisplayLength;
	}
	public void setIDisplayLength(Integer iDisplayLength) {
		this.iDisplayLength = iDisplayLength;
	}

	public Integer getIColumns() {
		return iColumns;
	}
	public void setIColumns(Integer iColumns) {
		this.iColumns = iColumns;
	}

	public String getSSearchGlobal() {
		return sSearchGlobal;
	}
	public void setSSearchGlobal(String sSearchGlobal) {
		this.sSearchGlobal = sSearchGlobal;
	}

	public Boolean getBEscapeRegexGlobal() {
		return bEscapeRegexGlobal;
	}
	public void setBEscapeRegexGlobal(Boolean bEscapeRegexGlobal) {
		this.bEscapeRegexGlobal = bEscapeRegexGlobal;
	}

	public List<Boolean> getBSortable() {
		return bSortable;
	}
	public void setBSortable(List<Boolean> bSortable) {
		this.bSortable = bSortable;
	}

	public List<Boolean> getBSearchable() {
		return bSearchable;
	}
	public void setBSearchable(List<Boolean> bSearchable) {
		this.bSearchable = bSearchable;
	}

	public List<String> getSSearch() {
		return sSearch;
	}
	public void setSSearch(List<String> sSearch) {
		this.sSearch = sSearch;
	}

	public List<Boolean> getBEscapeRegex() {
		return bEscapeRegex;
	}
	public void setBEscapeRegex(List<Boolean> bEscapeRegex) {
		this.bEscapeRegex = bEscapeRegex;
	}

	public Integer getISortingCols() {
		return iSortingCols;
	}
	public void setISortingCols(Integer iSortingCols) {
		this.iSortingCols = iSortingCols;
	}

	public List<Integer> getISortCol() {
		return iSortCol;
	}
	public void setISortCol(List<Integer> iSortCol) {
		this.iSortCol = iSortCol;
	}

	public List<String> getSSortDir() {
		return sSortDir;
	}
	public void setSSortDir(List<String> sSortDir) {
		this.sSortDir = sSortDir;
	}

	public String getSEcho() {
		return sEcho;
	}
	public void setSEcho(String sEcho) {
		this.sEcho = sEcho;
	}
}