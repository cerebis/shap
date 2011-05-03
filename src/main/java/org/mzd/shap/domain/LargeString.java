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
package org.mzd.shap.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@Entity
@Table(name="LargeStrings")
public class LargeString {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="LRGSTR_ID")
	private Integer id;
	@Column(length=2147483647)
	@Lob
	@NotNull
	private String value;
	
	public LargeString() {/*...*/}
	
	public LargeString(String value) {
		this.value = value;
	}
	
	public int length() {
		return getValue().length();
	}
	
	public Integer getId() {
		return id;
	}
	protected void setId(Integer id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Custom converter for marshalling to/from XML using XStream. 
	 *<p>
	 * This converter is in preference to using XStream annotations as the desired 
	 * result could not be achieved otherwise.
	 *<p> 
	 * Nested so as to associate the conversion closely to the class implementation.
	 */
	public static class LargeStringConverter implements Converter {
		@SuppressWarnings("unchecked")
		public boolean canConvert(Class type) {
			return type.equals(LargeString.class);
		}

		public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
			LargeString l = (LargeString)source;
			if (l.getValue() != null) {
				writer.setValue(l.getValue());
			}
		}
		
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			LargeString l = new LargeString();
			l.setValue(reader.getValue());
			return l;
		}
	}
}
