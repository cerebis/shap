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
package org.mzd.shap.io.bean;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * An explicit {@link CollectionConverter} for use in disambiguating alias to class conversions.
 * 
 * XStream expects all aliases to be unique to XML instance. This is regardless of hierarchical level
 * within the XML.
 * 
 */
public class NamedCollectionConverter extends CollectionConverter {
	private final String name;
	private final Class<?> type;

	public NamedCollectionConverter(final Mapper mapper, final String name, final Class<?> type) {
		super(mapper);
		this.name = name;
		this.type = type;
	}

	protected Object readItem(final HierarchicalStreamReader reader, final UnmarshallingContext context, final Object current) {
		return context.convertAnother(current, type);
	}

	protected void writeItem(final Object item, final MarshallingContext context, final HierarchicalStreamWriter writer) {
		if (item == null) {
			super.writeItem(item, context, writer);
		} 
		else {
			ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, item.getClass());
			context.convertAnother(item);
			writer.endNode();
		}
	}
}