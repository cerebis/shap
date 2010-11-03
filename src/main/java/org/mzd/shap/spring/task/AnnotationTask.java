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
package org.mzd.shap.spring.task;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.mzd.shap.analysis.AnalyzerException;
import org.mzd.shap.analysis.Annotator;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.spring.task.aop.AdvisableTask;

@Entity
@DiscriminatorValue(value="ANNOTATION_TASK")
public class AnnotationTask extends AdvisableTask {
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="AnnotationTargets",
			joinColumns=@JoinColumn(name="TASK_ID"),
			inverseJoinColumns=@JoinColumn(name="FEATURE_ID"))
	@OrderColumn(name="idx")
	private List<Feature> target;
	@Transient
	private Annotation[] result;
	@ManyToOne(targetEntity=org.mzd.shap.analysis.SimpleAnnotator.class,
			fetch=FetchType.EAGER)
	@JoinColumn(name="ANALYZER_ID",nullable=false)
	@NotNull
	private Annotator annotator;

	protected void runInternal() throws TaskException {
		try {
			Annotation[] result = getAnnotator()
				.analyze(getExecutable(), getTarget().toArray(new Feature[0]));
			setResult(result);
		}
		catch (AnalyzerException ex) {
			throw new TaskException(ex);
		}
		catch (Exception ex) {
			throw new TaskException(ex);
		}
	}
	
	public Annotator getAnnotator() {
		return annotator;
	}
	public void setAnnotator(Annotator annotator) {
		this.annotator = annotator;
	}
	
	public List<Feature> getTarget() {
		return target;
	}
	public void setTarget(List<Feature> target) {
		this.target = target;
	}

	public Annotation[] getResult() {
		return result;
	}
	public void setResult(Annotation[] result) {
		this.result = result;
	}
	
}
