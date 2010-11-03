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
import org.mzd.shap.domain.Project;
import org.mzd.validation.AbstractValidationTest;

public class ProjectTest extends AbstractValidationTest<Project> {
	
	public static Project makeValidTarget() {
		Project p = new Project();
		p.setName(generateRandomString(4));
		p.setCreation(new Date());
		p.setDescription(generateRandomString(4));
		return p;
	}
	
	@Test
	public void nameSize() {
		Project p = makeValidTarget();
		p.setName(generateRandomString(2));
		
		Set<ConstraintViolation<Project>> constraintViolations;
		
		constraintViolations = getViolations(p);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("size must be between 3 and 20",constraintViolations.iterator().next().getMessage());
		
		p.setName(generateRandomString(21));
		
		constraintViolations = getViolations(p);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("size must be between 3 and 20",constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void nameNotNull() {
		Project p = makeValidTarget();
		p.setName(null);
		
		Set<ConstraintViolation<Project>> constraintViolations = getViolations(p);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void descriptionSize() {
		Project p = makeValidTarget();
		
		p.setDescription("");
		
		Set<ConstraintViolation<Project>> violations = getViolations(p);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("size must be between 1 and 1024", violations.iterator().next().getMessage());
		
		p.setDescription(generateRandomString(1025));
		
		violations = getViolations(p);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("size must be between 1 and 1024", violations.iterator().next().getMessage());
	}

	@Test
	public void creationNotNull() {
		Project p = makeValidTarget();
		p.setCreation(null);

		Set<ConstraintViolation<Project>> constraintViolations = getViolations(p);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}
}
