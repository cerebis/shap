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
package org.mzd.shap.spring.task.aop;

import java.util.Arrays;

import org.mzd.shap.domain.dao.FeatureDao;
import org.mzd.shap.spring.task.DetectionTask;
import org.mzd.shap.spring.task.Task;

public class DetectionAfterAdvice extends AfterAdvice {
	private FeatureDao featureDao;

	public Task invoke(Task task) {
		task = super.invoke(task);
		DetectionTask t = (DetectionTask)task;
		getFeatureDao().saveOrUpdateAll(Arrays.asList(t.getResult()));
		return task;
	}

	public FeatureDao getFeatureDao() {
		return featureDao;
	}
	public void setFeatureDao(FeatureDao featureDao) {
		this.featureDao = featureDao;
	}

}
