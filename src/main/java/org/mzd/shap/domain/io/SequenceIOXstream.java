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

import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.LargeString;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.io.bean.BeanIOXstream;

public class SequenceIOXstream extends BeanIOXstream<Sequence> {

	public SequenceIOXstream() {
		super(Sequence.class,Feature.class);
		getDelegate().registerLocalConverter(Sequence.class, "data", new LargeString.LargeStringConverter());
		getDelegate().registerLocalConverter(Feature.class, "data", new LargeString.LargeStringConverter());
	}

}
