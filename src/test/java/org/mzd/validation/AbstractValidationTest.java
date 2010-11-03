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
package org.mzd.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.BeforeClass;

public abstract class AbstractValidationTest<T> {
    private static Validator validator;

	@BeforeClass
	public static void setUp() throws Exception {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	protected Set<ConstraintViolation<T>> getViolations(T target) {
		return validator.validate(target); 
	}
	
	public static String generateRandomString(int length) {
		return RandomStringUtils.random(length);
	}
	
}
