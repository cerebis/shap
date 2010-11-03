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


import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.AnnotationType;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Location;
import org.mzd.shap.domain.StrandException;

public abstract class FeatureWriter extends EntityWriter<Feature> {
	private List<String> annotatorPrecedence;

	protected FeatureWriter(String key, Writer writer) {
		super(key, writer);
	}
	
	protected FeatureWriter(String key, Writer writer, List<String> annotatorPrecedence) {
		super(key, writer);
		this.annotatorPrecedence = annotatorPrecedence;
	}
	
	class RankComparator implements Comparator<Annotation> {
		public int compare(Annotation a, Annotation b) {
			String aName = a.getAnnotator().getName();
			String bName = b.getAnnotator().getName();
			Integer aIdx = getAnnotatorPrecedence().indexOf(aName);
			Integer bIdx = getAnnotatorPrecedence().indexOf(bName);
			return aIdx.compareTo(bIdx);
		}
	}
	
	protected SortedSet<Annotation> rankAnnotations(Feature entity, AnnotationType refersTo) {
		SortedSet<Annotation> ranked = new TreeSet<Annotation>(new RankComparator());
		for (Annotation a : entity.getAnnotations()) {
			if (a.getRefersTo().equals(refersTo)) {
				ranked.add(a);
			}
		}
		return ranked;
	}
		
	@Override
	protected void writeLocation(Feature entity) throws IOException {
		Location loc = entity.getLocation(); 
		switch (loc.getStrand()) {
		case Forward:
			getWriter().write(
					String.format(getFwdFormat(), getKey(), loc.getStart()+1, loc.getEnd()+1));
			break;
		case Reverse:
			getWriter().write(
					String.format(getRevFormat(), getKey(), loc.getStart()+1, loc.getEnd()+1));
			break;
		default:
			throw new RuntimeException(new StrandException("Unknown strand type"));
		}
	}
	
	public List<String> getAnnotatorPrecedence() {
		return annotatorPrecedence;
	}
	public void setAnnotatorPrecedence(List<String> annotatorPrecedence) {
		this.annotatorPrecedence = annotatorPrecedence;
	}

}
