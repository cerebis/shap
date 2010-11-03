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
package org.mzd.shap.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MinimalQueryValidator implements ConstraintValidator<MinimalQuery, String> {
	private int minLength;
	private boolean leadingWildcards;
	
	public boolean isValid(String value, ConstraintValidatorContext context) {

		if (value == null) {
			return false;
		}
		
		value = value.trim();
		if (!leadingWildcards && value.startsWith("*")) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(
					"{org.mzd.shap.constraints.MinimalQuery.leading}")
				.addConstraintViolation();
			return false;
		}
		if (value.length() < minLength) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(
					"{org.mzd.shap.constraints.MinimalQuery.minlength}")
				.addConstraintViolation();
			return false;
		}
		return true;
	}

	public void initialize(MinimalQuery constraintAnnotation) {
		this.minLength = constraintAnnotation.minLength();
		this.leadingWildcards = constraintAnnotation.leadingWildcards();
	}
}
