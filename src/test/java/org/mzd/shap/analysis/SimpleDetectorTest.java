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
package org.mzd.shap.analysis;

import java.io.File;
import java.util.Set;

import javax.validation.ConstraintViolation;

import junit.framework.Assert;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.mzd.shap.analysis.SimpleDetector;
import org.mzd.validation.AbstractValidationTest;

public class SimpleDetectorTest extends AbstractValidationTest<SimpleDetector> {

	public static SimpleDetector makeValidTarget() {
		SimpleDetector sd = new SimpleDetector();
		sd.setName(RandomStringUtils.random(4));
		sd.setBatchSize(2);
		sd.setArgumentString(RandomStringUtils.random(10));
		sd.setScratchPath(new File(RandomStringUtils.random(10)));
		sd.setParserClass(Object.class);
		return sd;
	}
	
	@Test
	public void nameNotNull() {
		SimpleDetector sa = makeValidTarget();
		sa.setName(null);
		
		Set<ConstraintViolation<SimpleDetector>> constraintViolations = getViolations(sa);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void nameSize() {
		SimpleDetector sa = makeValidTarget();
		sa.setName("");
		
		Set<ConstraintViolation<SimpleDetector>> constraintViolations;
		
		constraintViolations = getViolations(sa);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("size must be between 1 and 256",constraintViolations.iterator().next().getMessage());
		
		sa.setName(generateRandomString(257));
		
		constraintViolations = getViolations(sa);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("size must be between 1 and 256",constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void batchSizeNotNull() {
		SimpleDetector sa = makeValidTarget();
		sa.setBatchSize(null);
		
		Set<ConstraintViolation<SimpleDetector>> constraintViolations = getViolations(sa);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void batchSizeRange() {
		SimpleDetector sa = makeValidTarget();
		sa.setBatchSize(0);
		
		Set<ConstraintViolation<SimpleDetector>> constraintViolations;
		
		constraintViolations = getViolations(sa);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("must be greater than or equal to 1",constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void argumentStringNotNull() {
		SimpleDetector sa = makeValidTarget();
		sa.setArgumentString(null);
		
		Set<ConstraintViolation<SimpleDetector>> constraintViolations = getViolations(sa);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}
	
	
	@Test
	public void scratchPathNotNull() {
		SimpleDetector sa = makeValidTarget();
		sa.setScratchPath(null);
		
		Set<ConstraintViolation<SimpleDetector>> constraintViolations = getViolations(sa);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void parseClassNotNull() {
		SimpleDetector sa = makeValidTarget();
		sa.setParserClass(null);
		
		Set<ConstraintViolation<SimpleDetector>> constraintViolations = getViolations(sa);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}

}
