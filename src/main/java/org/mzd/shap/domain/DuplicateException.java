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

import org.mzd.shap.ApplicationException;

/**
 * Thrown when a collection association already contains an entity.
 * 
 * Most useful for bidirectional many-to-many.
 */
public class DuplicateException extends ApplicationException {
	static final long serialVersionUID = 8111274189406981015L;

	public DuplicateException() {
		// TODO Auto-generated constructor stub
	}

	public DuplicateException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public DuplicateException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public DuplicateException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
