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
import org.mzd.shap.analysis.SimpleAnnotator;
import org.mzd.shap.domain.FeatureType;
import org.mzd.shap.domain.MoleculeType;
import org.mzd.validation.AbstractValidationTest;


public class SimpleAnnotatorTest extends AbstractValidationTest<SimpleAnnotator> {

	public static SimpleAnnotator makeValidTarget() {
		SimpleAnnotator sa = new SimpleAnnotator();
		sa.setName(RandomStringUtils.random(4));
		sa.setBatchSize(2);
		sa.setArgumentString(RandomStringUtils.random(10));
		sa.setScratchPath(new File(RandomStringUtils.random(10)));
		sa.setParserClass(Object.class);
		sa.setSupportedFeatureType(FeatureType.Undefined);
		sa.setSupportedMoleculeType(MoleculeType.DNA);
		return sa;
	}
	
	@Test
	public void nameNotNull() {
		SimpleAnnotator sa = makeValidTarget();
		sa.setName(null);
		
		Set<ConstraintViolation<SimpleAnnotator>> constraintViolations = getViolations(sa);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void nameSize() {
		SimpleAnnotator sa = makeValidTarget();
		sa.setName("");
		
		Set<ConstraintViolation<SimpleAnnotator>> constraintViolations;
		
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
		SimpleAnnotator sa = makeValidTarget();
		sa.setBatchSize(null);
		
		Set<ConstraintViolation<SimpleAnnotator>> constraintViolations = getViolations(sa);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void batchSizeRange() {
		SimpleAnnotator sa = makeValidTarget();
		sa.setBatchSize(0);
		
		Set<ConstraintViolation<SimpleAnnotator>> constraintViolations;
		
		constraintViolations = getViolations(sa);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("must be greater than or equal to 1",constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void supportedFeatureTypeNotNull() {
		SimpleAnnotator sa = makeValidTarget();
		sa.setSupportedFeatureType(null);
		
		Set<ConstraintViolation<SimpleAnnotator>> constraintViolations = getViolations(sa);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void supportedMoleculeTypeNotNull() {
		SimpleAnnotator sa = makeValidTarget();
		sa.setSupportedMoleculeType(null);
		
		Set<ConstraintViolation<SimpleAnnotator>> constraintViolations = getViolations(sa);
		
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("may not be null",constraintViolations.iterator().next().getMessage());
	}

}
