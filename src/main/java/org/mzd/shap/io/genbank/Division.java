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

package org.mzd.shap.io.genbank;

public enum Division {
	Primate("PRI"),
	Rodent("ROD"),
	OtherMammalian("MAM"),
	OtherVertebrate("VRT"),
	Invertebrate("INV"),
	PlantFungalAlgal("PLN"),
	Bacterial("BCT"),
	Viral("VRL"),
	Bacteriophage("PHG"),
	Synthetic("SYN"),
	Unannotated("UNA"),
	ExpressedSequenceTags("EST"),
	Patent("PAT"),
	SequenceTaggedSites("STS"),
	GenomeSurvey("GSS"),
	HighThroughputGenomic("HTG"),
	UnfinishedHighThroughputCdna("HTC"),
	EnvironmentalSampling("ENV");
	
	private String tag;
	private Division(String tag) {
		this.tag = tag;
	}
	
	public String getTag() {
		return tag;
	}
	
	@Override
	public String toString() {
		return getTag();
	}
}