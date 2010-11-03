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
package org.mzd.shap.hibernate.search;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;

public abstract class FieldFilterFactory {
	private Object value;
	private String field;

	public FieldFilterFactory(String field) {
		this.field = field;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	@Key
	public FilterKey getFilterKey() {
		StandardFilterKey key = new StandardFilterKey();
		key.addParameter(value);
		return key;
	}
	
	@Factory
	public Filter getProjectUserFilter() {
		if (value == null) {
			throw new IllegalStateException("value cannot be null");
		}
		return new QueryWrapperFilter(new TermQuery(new Term(field,value.toString())));
	}
}