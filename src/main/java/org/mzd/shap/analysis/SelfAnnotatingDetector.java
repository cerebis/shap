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
package org.mzd.shap.analysis;

import java.io.File;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.exec.ExecutableException;

@Entity
@DiscriminatorValue("SELFANNOTATING_DETECTOR")
public class SelfAnnotatingDetector extends SimpleDetector {
	@OneToOne(targetEntity=org.mzd.shap.analysis.ImplicitAnnotator.class,
			cascade=CascadeType.ALL)
	@JoinColumn(name="IMPANNO_ID")
	private Annotator implicitAnnotator;

	@Override
	protected Feature[] parseOutput(File output, Sequence... target) throws ExecutableException {
		Feature[] results = super.parseOutput(output, target);
		// Set the annotator for each returned feature.
		for (Feature f : results) {
			for (Annotation an : f.getAnnotations()) {
				an.setAnnotator(getImplicitAnnotator());
			}
		}
		return results;
	}
	
	public Annotator getImplicitAnnotator() {
		return implicitAnnotator;
	}
	public void setImplicitAnnotator(Annotator implicitAnnotator) {
		this.implicitAnnotator = implicitAnnotator;
	}

}
