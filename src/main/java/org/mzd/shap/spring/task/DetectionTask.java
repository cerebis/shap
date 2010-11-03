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
import org.mzd.shap.analysis.Detector;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.spring.task.aop.AdvisableTask;

@Entity
@DiscriminatorValue(value="DETECTION_TASK")
public class DetectionTask extends AdvisableTask { 
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="DetectionTargets",
			joinColumns=@JoinColumn(name="TASK_ID"),
			inverseJoinColumns=@JoinColumn(name="SEQUENCE_ID"))
	@OrderColumn(name="idx")
	private List<Sequence> target;
	@Transient
	private Feature[] result;
	@ManyToOne(targetEntity=org.mzd.shap.analysis.SimpleDetector.class,
			fetch=FetchType.EAGER)
	@JoinColumn(name="ANALYZER_ID",nullable=false)
	@NotNull
	private Detector detector;
	
	public void runInternal() throws TaskException {
		try {
			Feature[] results = getDetector()
				.analyze(getExecutable(), getTarget().toArray(new Sequence[0]));
			setResult(results);
		}
		catch (AnalyzerException ex) {
			throw new TaskException(ex);
		}
		catch (Throwable t) {
			throw new TaskException(t);
		}
	}
	
	public void setDetector(Detector detector) {
		this.detector = detector;
	}
	public Detector getDetector() {
		return detector;
	}

	public void setTarget(List<Sequence> target) {
		this.target = target;
	}
	public List<Sequence> getTarget() {
		return target;
	}

	public Feature[] getResult() {
		return result;
	}
	public void setResult(Feature[] result) {
		this.result = result;
	}

}
