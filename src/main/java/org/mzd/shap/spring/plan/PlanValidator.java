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

/**
 * Validates a {@link Plan} by checking if the supplied fields and requested analysis make sense.
 * 
 */
public class PlanValidator {
	
	static boolean nullSafeEmpty(String value) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		return false;
	}
	
	static boolean isSet(String value) {
		return !nullSafeEmpty(value);
	}
	
	static boolean isSet(Integer value) {
		return value != null;
	}
	
	public static void validate(Plan plan) throws PlanException {
		plan.validate();
	}
	
}
