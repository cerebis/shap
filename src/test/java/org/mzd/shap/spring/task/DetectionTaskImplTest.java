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
package org.mzd.shap.spring.task;

import java.util.Set;

import javax.validation.ConstraintViolation;

import junit.framework.Assert;

import org.junit.Test;
import org.mzd.shap.analysis.SimpleDetectorTest;
import org.mzd.shap.spring.task.DetectionTask;
import org.mzd.validation.AbstractValidationTest;


public class DetectionTaskImplTest extends AbstractValidationTest<DetectionTask> {

	public static DetectionTask makeValidTarget() {
		DetectionTask d = new DetectionTask();
		d.setJob(JobTest.makeValidTarget());
		d.setDetector(SimpleDetectorTest.makeValidTarget());
		return d;
	}
	
	@Test
	public void jobNotNull() {
		DetectionTask d = makeValidTarget();
		d.setJob(null);

		Set<ConstraintViolation<DetectionTask>> violations = getViolations(d);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}

	@Test
	public void detectorNotNull() {
		DetectionTask d = makeValidTarget();
		d.setDetector(null);

		Set<ConstraintViolation<DetectionTask>> violations = getViolations(d);
		
		Assert.assertEquals(1, violations.size());
		Assert.assertEquals("may not be null", violations.iterator().next().getMessage());
	}
}
