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
import org.mzd.shap.analysis.SimpleAnnotatorTest;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.AnnotationType;
import org.mzd.validation.AbstractValidationTest;

public class AnnotationTest extends AbstractValidationTest<Annotation> {

	public static Annotation makeValidTarget() {
		Annotation a = new Annotation();
		a.setAccession(RandomStringUtils.random(4));
		a.setDescription(RandomStringUtils.random(4));
		a.setFeature(FeatureTest.makeValidTarget());
		a.setAnnotator(SimpleAnnotatorTest.makeValidTarget());
		a.setRefersTo(AnnotationType.Gene);
		return a;
	}
	
	@Test
	public void accessionNotNull() {
		Annotation a = makeValidTarget();
		a.setAccession(null);
		
		Set<ConstraintViolation<Annotation>> violations = getViolations(a);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}

	@Test
	public void accessionRange() {
		Annotation a = makeValidTarget();
		a.setAccession("");
		
		Set<ConstraintViolation<Annotation>> violations = getViolations(a);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("size must be between 1 and 256", violations.iterator().next().getMessage());

		a.setAccession(RandomStringUtils.random(257));
		
		violations = getViolations(a);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("size must be between 1 and 256", violations.iterator().next().getMessage());
	}
	
	@Test
	public void descriptionNotNull() {
		Annotation a = makeValidTarget();
		a.setDescription(null);
		
		Set<ConstraintViolation<Annotation>> violations = getViolations(a);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}
	
	@Test
	public void featureNotNull() {
		Annotation a = makeValidTarget();
		a.setFeature(null);
		
		Set<ConstraintViolation<Annotation>> violations = getViolations(a);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}

	@Test
	public void annotatorNotNull() {
		Annotation a = makeValidTarget();
		a.setAnnotator(null);
		
		Set<ConstraintViolation<Annotation>> violations = getViolations(a);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}
	
	@Test
	public void refersToNotNull() {
		Annotation a = makeValidTarget();
		a.setRefersTo(null);
		
		Set<ConstraintViolation<Annotation>> violations = getViolations(a);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}


}
