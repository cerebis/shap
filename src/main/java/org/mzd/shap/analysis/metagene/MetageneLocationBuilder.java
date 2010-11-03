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
package org.mzd.shap.analysis.metagene;

import org.mzd.shap.domain.Strand;
import org.mzd.shap.domain.builders.LocationBuilder;

public class MetageneLocationBuilder extends LocationBuilder {

	@Override
	public LocationBuilder standardize(Integer start, Integer stop, Strand strand, Integer frame) {

		switch (strand) {
		case Forward:
			start += frame;
			frame = (start - 1) % 3;
			break;
		case Reverse:
			stop -= frame;
			frame = (stop + 1) % 3;
			break;
		}
		
		location.setStart(start-1);
		location.setEnd(stop-1);
		location.setStrand(strand);
		location.setFrame(frame);
		
		int extra3p = location.getExtraBases();
		if (extra3p > 0) {
			location.adjust3PrimeEnd(-extra3p);
		}
		
		return this;
	}
}
