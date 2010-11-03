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
import org.mzd.shap.domain.Alignment;
import org.mzd.validation.AbstractValidationTest;


public class AlignmentTest extends AbstractValidationTest<Alignment> {

	public static Alignment makeValidTarget() {
		Alignment a = new Alignment();
		a.setQueryStart(1);
		a.setQueryEnd(10);
		a.setSubjectStart(11);
		a.setSubjectEnd(20);
		a.setQuerySeq(RandomStringUtils.random(10));
		a.setSubjectSeq(RandomStringUtils.random(10));
		a.setConsensusSeq(RandomStringUtils.random(10));
		return a;
	}
	
	@Test
	public void queryStartNotNull() {
		Alignment a = makeValidTarget();
		a.setQueryStart(null);
		
		Set<ConstraintViolation<Alignment>> constraintViolations = getViolations(a);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void queryStartRange() {
		Alignment a = makeValidTarget();
		
		a.setQueryStart(0);
		
		Set<ConstraintViolation<Alignment>> violations = getViolations(a);

		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("must be greater than or equal to 1", violations.iterator().next().getMessage());
	}

	@Test
	public void queryEndNotNull() {
		Alignment a = makeValidTarget();
		a.setQueryEnd(null);
		
		Set<ConstraintViolation<Alignment>> constraintViolations = getViolations(a);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void queryEndRange() {
		Alignment a = makeValidTarget();
		
		a.setQueryEnd(0);
		
		Set<ConstraintViolation<Alignment>> violations = getViolations(a);

		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("must be greater than or equal to 1", violations.iterator().next().getMessage());
	}
	
	@Test
	public void subjectStartNotNull() {
		Alignment a = makeValidTarget();
		a.setQueryStart(null);
		
		Set<ConstraintViolation<Alignment>> constraintViolations = getViolations(a);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void subjectStartRange() {
		Alignment a = makeValidTarget();
		
		a.setSubjectStart(0);
		
		Set<ConstraintViolation<Alignment>> violations = getViolations(a);

		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("must be greater than or equal to 1", violations.iterator().next().getMessage());
	}
	
	@Test
	public void subjectEndNotNull() {
		Alignment a = makeValidTarget();
		a.setSubjectEnd(null);
		
		Set<ConstraintViolation<Alignment>> constraintViolations = getViolations(a);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void subjectEndRange() {
		Alignment a = makeValidTarget();
		
		a.setSubjectEnd(0);
		
		Set<ConstraintViolation<Alignment>> violations = getViolations(a);

		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("must be greater than or equal to 1", violations.iterator().next().getMessage());
	}

	@Test
	public void querySeqNotNull() {
		Alignment a = makeValidTarget();
		a.setQuerySeq(null);
		
		Set<ConstraintViolation<Alignment>> constraintViolations = getViolations(a);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void subjectSeqNull() {
		Alignment a = makeValidTarget();
		a.setSubjectSeq(null);
		
		Set<ConstraintViolation<Alignment>> constraintViolations = getViolations(a);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}
	
}
