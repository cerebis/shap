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
package org.mzd.shap.domain.io;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.FeatureType;
import org.mzd.shap.domain.LargeString;
import org.mzd.shap.domain.Location;
import org.mzd.shap.domain.LocationException;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.Strand;
import org.mzd.shap.domain.io.SequenceIOXstream;
import org.mzd.shap.io.bean.BeanIOException;

public class SequenceIOXstreamTest {
	private SequenceIOXstream marshaller;

	@Before
	public void setUp() throws Exception {
		marshaller = new SequenceIOXstream();
	}

	@Test
	public void testRead() throws BeanIOException {
		marshaller.read(new File("sequence-read.xml"));
	}

	@Test
	public void testWrite() throws LocationException, BeanIOException {
		Feature f = new Feature();
		f.setLocation(new Location(1,12,Strand.Forward,1));
		f.setData(new LargeString("MAR*"));
		f.setType(FeatureType.OpenReadingFrame);
		Sequence s = new Sequence();
		s.setName("myseq");
		s.addFeature(f);
		s.setData(new LargeString("acgt"));
		marshaller.write(s, new File("sequence-write.xml"));
	}

}
