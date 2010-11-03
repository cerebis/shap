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

import static junit.framework.Assert.*;

import org.junit.Test;
import org.mzd.shap.domain.Location;
import org.mzd.shap.domain.LocationException;
import org.mzd.shap.domain.Strand;
import org.mzd.validation.AbstractValidationTest;


public class LocationTest extends AbstractValidationTest<Location> {
	
	public static Location makeValidTarget() {
		Location l = new Location();
		l.setStart(0);
		l.setEnd(10);
		l.setStrand(Strand.Forward);
		l.setFrame(0);
		return l;
	}
	
	@Test
	public void reverseStrandCheck() {
		Location l = makeValidTarget();
		
		l.setStrand(Strand.Forward);
		assertFalse("strand is forward", l.isReverseStrand());

		l.setStrand(Strand.Reverse);
		assertTrue("strand is reverse", l.isReverseStrand());
	}
	
	@Test
	public void independenceCheck() {
		Location a = makeValidTarget();
		Location b = makeValidTarget();
		
		a.setStart(1);
		a.setEnd(10);
		b.setStart(11);
		b.setEnd(20);
		
		assertTrue("a is independent of b",a.independent(b));
		assertFalse("a is not independent of itself",a.independent(a));
	}
	
	@Test(expected=LocationException.class)
	public void createInValid() throws Exception {
		new Location(1,1,Strand.Forward,1);
	}
	
	@Test(expected=LocationException.class)
	public void forwardCoordsAndReverseStrand() throws LocationException {
		Location.createForwardLocation(1, 10, Strand.Reverse, 0);
	}

	@Test(expected=LocationException.class)
	public void reverseCoordsAndForwardStrand() throws LocationException {
		Location.createForwardLocation(10, 1, Strand.Forward, 0);
	}

	@Test(expected=LocationException.class)
	public void createForwardZeroExtext() throws LocationException {
		Location.createForwardLocation(1, 1, Strand.Forward, 0);
	}

	@Test
	public void startNotNull() {
		Location l = makeValidTarget();
		l.setStart(null);
		
		Set<ConstraintViolation<Location>> violations = getViolations(l);
		
		assertEquals(1, violations.size());
		assertEquals("may not be null", violations.iterator().next().getMessage());
	}
	
	@Test
	public void startRange() {
		Location l = makeValidTarget();
		
		l.setStart(-1);
		
		Set<ConstraintViolation<Location>> violations = getViolations(l);

		assertEquals(1, violations.size());
		assertEquals("must be greater than or equal to 0", violations.iterator().next().getMessage());
	}

	@Test
	public void endNotNull() {
		Location l = makeValidTarget();
		l.setEnd(null);
		
		Set<ConstraintViolation<Location>> violations = getViolations(l);
		
		assertEquals(1, violations.size());
		assertEquals("may not be null", violations.iterator().next().getMessage());
	}
	
	@Test
	public void endRange() {
		Location l = makeValidTarget();
		
		l.setEnd(-1);
		
		Set<ConstraintViolation<Location>> violations = getViolations(l);

		assertEquals(1, violations.size());
		assertEquals("must be greater than or equal to 0", violations.iterator().next().getMessage());
	}
	
	@Test
	public void strandNotNull() {
		Location l = makeValidTarget();
		l.setStrand(null);
		
		Set<ConstraintViolation<Location>> violations = getViolations(l);
		
		assertEquals(1, violations.size());
		assertEquals("may not be null", violations.iterator().next().getMessage());
	}

	@Test
	public void frameRange() {
		Location l = makeValidTarget();
		
		l.setFrame(-1);
		
		Set<ConstraintViolation<Location>> violations = getViolations(l);
		
		assertEquals(1, violations.size());
		assertEquals("must be between 0 and 2", violations.iterator().next().getMessage());

		l.setFrame(3);
		
		violations = getViolations(l);
		
		assertEquals(1, violations.size());
		assertEquals("must be between 0 and 2", violations.iterator().next().getMessage());
}
}
