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
package org.mzd.shap.exec.drmaa;

import org.mzd.shap.ApplicationException;

public class GridException extends ApplicationException {
	static final long serialVersionUID = -4949953711761273060L;

	public GridException() {
		// TODO Auto-generated constructor stub
	}

	public GridException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public GridException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public GridException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
