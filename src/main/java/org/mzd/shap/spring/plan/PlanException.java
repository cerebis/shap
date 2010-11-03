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
package org.mzd.shap.spring.plan;

import org.mzd.shap.ApplicationException;

public class PlanException extends ApplicationException {
	private static final long serialVersionUID = -6609412912153101972L;

	public PlanException() {
		// TODO Auto-generated constructor stub
	}

	public PlanException(String message) {
		super(message);
	}

	public PlanException(Throwable cause) {
		super(cause);
	}

	public PlanException(String message, Throwable cause) {
		super(message, cause);
	}

}
