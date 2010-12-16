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

import java.util.Collection;
import java.util.Iterator;

import org.hibernate.search.bridge.StringBridge;

/**
 * Converts a value-collection to a comma separated list of all elements in collection.
 */
public class ValueCollectionBridge implements StringBridge {
	public String objectToString(Object field) {
		if (field == null) {
			return null;
		}
		if (field instanceof Collection<?>) {
			StringBuffer buffer = new StringBuffer();
			Collection<?> collection = (Collection<?>)field;
			Iterator<?> it = collection.iterator();
			while (true) {
				Object value = it.next();
				buffer.append(value.toString());
				if (!it.hasNext()) {
					break;
				}
			}
			return buffer.toString();
		}
		else {
			throw new IllegalArgumentException(ValueCollectionBridge.class +
					" used on a non-collection: " + 
					field.getClass());
		}
	}
}