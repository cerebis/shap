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

import java.util.Set;

import javax.validation.ConstraintViolation;

import junit.framework.Assert;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.mzd.shap.domain.LargeString;
import org.mzd.validation.AbstractValidationTest;


public class LargeStringTest extends AbstractValidationTest<LargeString> {

	public static LargeString makeValidTarget() {
		LargeString l = new LargeString();
		l.setValue(RandomStringUtils.random(5));
		return l;
	}
	
	@Test
	public void valueNotNull() {
		LargeString l = makeValidTarget();
		
		l.setValue(null);
		
		Set<ConstraintViolation<LargeString>> violations = getViolations(l);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}
}
