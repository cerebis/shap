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

import org.junit.Test;
import org.mzd.shap.domain.Sequence;
import org.mzd.validation.AbstractValidationTest;


public class SequenceTest extends AbstractValidationTest<Sequence> {

	public static Sequence makeValidTarget() {
		Sequence seq = new Sequence();
		seq.setName(generateRandomString(4));
		seq.setSample(SampleTest.makeValidTarget());
		return seq;
	}
	
	@Test
	public void nameNotNull() {
		Sequence s = makeValidTarget();
		s.setName(null);
		
		Set<ConstraintViolation<Sequence>> violations = getViolations(s);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}
	
	@Test
	public void nameSize() {
		Sequence s = makeValidTarget();
		
		s.setName("");
		
		Set<ConstraintViolation<Sequence>> violations = getViolations(s);

		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("size must be between 1 and 255", violations.iterator().next().getMessage());
		
		s.setName(generateRandomString(256));
		
		violations = getViolations(s);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("size must be between 1 and 255", violations.iterator().next().getMessage());
	}

	@Test
	public void descriptionSize() {
		Sequence s = makeValidTarget();
		
		s.setDescription("");
		
		Set<ConstraintViolation<Sequence>> violations = getViolations(s);

		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("size must be between 1 and 4095", violations.iterator().next().getMessage());
		
		s.setDescription(generateRandomString(4096));
		
		violations = getViolations(s);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("size must be between 1 and 4095", violations.iterator().next().getMessage());
	}
	
	@Test
	public void sampleNotNull() {
		Sequence s = makeValidTarget();
		s.setSample(null);
		
		Set<ConstraintViolation<Sequence>> violations = getViolations(s);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}
	
	@Test
	public void taxonomyNotNull() {
		Sequence s = makeValidTarget();
		s.setTaxonomy(null);
		
		Set<ConstraintViolation<Sequence>> violations = getViolations(s);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}
}
