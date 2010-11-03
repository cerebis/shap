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
package org.mzd.shap.spring.plan;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("plan")
public class Plan {
	@XStreamAlias("id")
	@XStreamAsAttribute
	private String id;
	@XStreamAlias("targets")
	private List<Target> targets;
	@XStreamAlias("steps")
	private List<Step> steps = new ArrayList<Step>();

	public void validate() throws PlanException {
		// Plans need an ID
		if (getId() == null) {
			throw new PlanException("A plan must define an id");
		}

		// Validate targets
		if (getTargets() == null || getTargets().size() == 0) {
			throw new PlanException("A plan must contain at least one target");
		}
		for (Target t : getTargets()) {
			t.validate();
		}
		
		// Validate steps
		if (getSteps() == null || getSteps().size() == 0) {
			throw new PlanException("A plan must contain at least one step");
		}
		for (Step s : getSteps()) {
			s.validate(getTargets().toArray(new Target[]{}));
		}
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
			.append("id",getId())
			.append("targets",getTargets())
			.append("steps",getSteps())
			.toString();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public List<Target> getTargets() {
		return targets;
	}
	public void setTargets(List<Target> targets) {
		this.targets = targets;
	}
	public void addTarget(Target target) {
		if (!getTargets().contains(target)) {
			getTargets().add(target);
		}
	}
		
	public List<Step> getSteps() {
		return steps;
	}
	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}
	public void addStep(Step step) {
		if (!getSteps().contains(step)) {
			getSteps().add(step);
		}
	}

}
