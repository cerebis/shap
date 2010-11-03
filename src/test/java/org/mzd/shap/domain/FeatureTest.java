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
import org.mzd.shap.analysis.SimpleDetector;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.FeatureType;
import org.mzd.validation.AbstractValidationTest;

public class FeatureTest extends AbstractValidationTest<Feature> {

	public static Feature makeValidTarget() {
		Feature f = new Feature();
		f.setLocation(LocationTest.makeValidTarget());
		f.setData(LargeStringTest.makeValidTarget());
		f.setType(FeatureType.Undefined);
		f.setSequence(SequenceTest.makeValidTarget());
		f.setDetector(new SimpleDetector());
		return f; 
	}
	
	@Test
	public void locationNotNull() {
		Feature f = makeValidTarget();
		f.setLocation(null);
		
		Set<ConstraintViolation<Feature>> violations = getViolations(f);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}
	
	@Test
	public void sequenceNotNull() {
		Feature f = makeValidTarget();
		f.setSequence(null);
		
		Set<ConstraintViolation<Feature>> violations = getViolations(f);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}

	@Test
	public void detectorNotNull() {
		Feature f = makeValidTarget();
		f.setDetector(null);
		
		Set<ConstraintViolation<Feature>> violations = getViolations(f);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}
	
	@Test
	public void typeNotNull() {
		Feature f = makeValidTarget();
		f.setType(null);
		
		Set<ConstraintViolation<Feature>> violations = getViolations(f);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}

}
