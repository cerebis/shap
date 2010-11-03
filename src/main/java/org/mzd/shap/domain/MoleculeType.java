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

public enum MoleculeType {
	DNA("nt"),
	Protein("aa");
	
	private String[] otherNames;
	MoleculeType(String... otherNames) {
		this.otherNames = otherNames;
	}
	
	public String[] getOtherNames() {
		return otherNames;
	}

	/**
	 * Get an enum constant by name. This name can be either
	 * the real name of the constant or another pseudonym
	 * listed in otherNames.
	 * 
	 * @param name - the name which refers to an specific enum constant.
	 * @throws IllegalArgumentException - when the name does not refer to any instance.
	 * @return an enum constant of the requested name.
	 */
	public static MoleculeType getType(String name) {
		try {
			// Assume name refers to element name
			return MoleculeType.valueOf(name);
		}
		catch (IllegalArgumentException ex) {
			// Now check if another name is being used.
			for (MoleculeType mt : MoleculeType.values()) {
				for (String s : mt.getOtherNames()) {
					if (s.equals(name)) {
						return mt;
					}
				}
			}
			// No name was found.
			throw ex;
		}
	}
}
