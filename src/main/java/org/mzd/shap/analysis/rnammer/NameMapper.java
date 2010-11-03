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
package org.mzd.shap.analysis.rnammer;

public class NameMapper {
	private final static String[] prokProducts = {"5s_rRNA","16s_rRNA","23s_rRNA"};
	private final static String[] eukProducts = {"8s_rRNA","18s_rRNA","28s_rRNA"};
	private final static String[] genes = {"TSU","SSU","LSU"};

	protected static String findMapped(String name, String[] sourceNames, String[] mappedNames) 
			throws DefinedMappingException {
		
		if (sourceNames.length != mappedNames.length) {
			throw new DefinedMappingException("source and mapped name arrays are not of equal length");
		}
		
		for (int n=0; n<sourceNames.length; n++) {
			if (sourceNames[n].equalsIgnoreCase(name)) {
				return mappedNames[n];
			}
		}
		
		throw new DefinedMappingException("Name ["+ name + "] not found in mapping");
	}
	
	public static String geneToProkProduct(String geneName) throws DefinedMappingException {
		return findMapped(geneName,genes,prokProducts);
	}

	public static String geneToEukProduct(String geneName) throws DefinedMappingException {
		return findMapped(geneName,genes,eukProducts);
	}
	
	public static String prokProductToGene(String productName) throws DefinedMappingException {
		return findMapped(productName,prokProducts,genes);
	}
	
	public static String eukProductToGene(String productName) throws DefinedMappingException {
		return findMapped(productName,eukProducts,genes);
	}
	
	public static String productToGene(String productName) throws DefinedMappingException {
		try {
			return prokProductToGene(productName);
		}
		catch (DefinedMappingException ex) {
			return eukProductToGene(productName);
		}
	}
}
