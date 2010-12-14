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

import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;

import junit.framework.Assert;

import org.junit.Test;
import org.mzd.shap.domain.Sample;
import org.mzd.validation.AbstractValidationTest;


public class SampleTest extends AbstractValidationTest<Sample> {

	public static Sample makeValidTarget() {
		Sample s = new Sample();
		s.setName(generateRandomString(4));
		s.setCreation(new Date());
		s.setProject(ProjectTest.makeValidTarget());
		return s;
	}
	
	@Test
	public void nameNotNull() {
		Sample s = makeValidTarget();
		s.setName(null);
		
		Set<ConstraintViolation<Sample>> violations = getViolations(s);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}

	@Test
	public void nameSize() {
		Sample s = makeValidTarget();
		s.setName(generateRandomString(2));
		
		Set<ConstraintViolation<Sample>> violations = getViolations(s);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("size must be between 3 and 255", violations.iterator().next().getMessage());
		
		s.setName(generateRandomString(256));
		
		violations = getViolations(s);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("size must be between 3 and 255", violations.iterator().next().getMessage());
	}
	
	@Test
	public void descriptionSize() {
		Sample s = makeValidTarget();
		
		s.setDescription("");
		
		Set<ConstraintViolation<Sample>> violations = getViolations(s);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("size must be between 1 and 4095", violations.iterator().next().getMessage());
		
		s.setDescription(generateRandomString(4096));
		
		violations = getViolations(s);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("size must be between 1 and 4095", violations.iterator().next().getMessage());
	}

	@Test
	public void creationNotNull() {
		Sample s = makeValidTarget();
		s.setCreation(null);
		
		Set<ConstraintViolation<Sample>> violations = getViolations(s);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}
	
	@Test
	public void projectNotNull() {
		Sample s = makeValidTarget();
		s.setProject(null);
		
		Set<ConstraintViolation<Sample>> violations = getViolations(s);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}
}
