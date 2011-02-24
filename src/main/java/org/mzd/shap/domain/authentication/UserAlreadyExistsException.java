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
package org.mzd.shap.domain.authentication;

public class UserAlreadyExistsException extends Exception {
	private static final long serialVersionUID = -5004548278757313155L;

	public UserAlreadyExistsException() {
		// TODO Auto-generated constructor stub
	}

	public UserAlreadyExistsException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public UserAlreadyExistsException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public UserAlreadyExistsException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
