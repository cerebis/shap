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

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration representing the strandedness of a DNA Feature.
 */
public enum Strand {
	Forward {
		@Override
		public void adjust3PrimeEnd(Location loc, int change) {
			loc.setEnd(loc.getEnd() + change);
		}
		
		@Override
		public void adjust5PrimeEnd(Location loc, int change) {
			loc.setStart(loc.getStart() + change);
		}
	},
	
	Reverse {
		@Override
		public void adjust3PrimeEnd(Location loc, int change) {
			loc.setStart(loc.getStart() - change);
		}
		
		@Override
		public void adjust5PrimeEnd(Location loc, int change) {
			loc.setEnd(loc.getEnd() - change);
		}
	};
	
	/**
	 * Adjust the 3-prime end of an instance of Location. 
	 * <p>
	 * As LocationS are always relative to the genomic forward strand, 
	 * which property is changed (start|end) depends on the instance
	 * of Strand. 
	 * 
	 * @param loc - the location to modify
	 * @param change - the relative change to an end
	 */
	public abstract void adjust3PrimeEnd(Location loc, int change);
	
	/**
	 * Adjust the 5-prime end of an instance of Location.
	 * <p>
	 * As LocationS are always relative to the genomic forward strand, 
	 * which property is changed (start|end) depends on the instance
	 * of Strand. 
	 * 
	 * @param loc - the location to modify
	 * @param change - the relative change to an end
	 */
	public abstract void adjust5PrimeEnd(Location loc, int change);
	
	private static Map<String,Strand> registry = new HashMap<String, Strand>();
	static {
		// Various symbols used to designation forward and reverse.
		registry.put("U", Forward);
		registry.put("C", Reverse);
		registry.put("+", Forward);
		registry.put("-", Reverse);
	}
	
	/**
	 * Get an instance of Strand which correlates with the specified symbolic
	 * string.
	 *  
	 * @param symbol - the string which symbolically represents strandedness.
	 * @return an instance of Strand.
	 * @throws UnknownSymbolException
	 */
	public static Strand getInstance(String symbol) throws StrandException {
		Strand s = registry.get(symbol);
		if (s == null) {
			throw new StrandException("The symbol [" + symbol + "] was not found in the registry");
		}
		return s;
	}
}
