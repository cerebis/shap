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
package org.mzd.shap.spring;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.mzd.shap.domain.FeatureException;
import org.mzd.shap.domain.FeatureType;
import org.mzd.shap.spring.plan.Step;
import org.mzd.shap.spring.plan.Target;
import org.mzd.shap.spring.task.Job;
import org.mzd.shap.spring.task.Status;
import org.mzd.shap.spring.task.Task;

public interface BatchAdminService extends BatchViewService {

	/**
	 * Job Creation
	 */
	Job createDetectionJobForProject(String projectName, String detectorName)
			throws NotFoundException;

	Job createDetectionJobForSample(String projectName, String sampleName,
			String detectorName) throws NotFoundException;

	Job createDetectionJobForSequence(Integer sequenceId, String detectorName)
			throws NotFoundException;

	Job createDetectionJob(File sequenceList, String detectorName)
			throws IOException, NotFoundException;

	Job createAnnotationJobForFeature(FeatureType type, Integer featureId,
			String annotatorName) throws NotFoundException, FeatureException;

	Job createAnnotationJobForSequence(FeatureType type, Integer sequenceId,
			String annotatorName) throws NotFoundException;

	Job createAnnotationJobForSample(FeatureType type, String projectName,
			String sampleName, String annotatorName) throws NotFoundException;

	/**
	 * Step creation
	 */
	Job newDetectionStep(Target target, Step step) throws NotFoundException;
	
	Job newAnnotationStep(Target target, Step step) throws NotFoundException, FeatureException;

	/**
	 * Get some tasks whose status is {@link Status.NEW} and mark them as {@link Status.QUEUED}.
	 * 
	 * @param maxResults
	 * @return a list of tasks.
	 */
	List<Task> prepareNewTasks(int maxResults);
	
	/**
	 * Prepare a new job {@link Status.NEW} for processing. This sets the jobs status to {@link Status.STARTED}.
	 * 
	 * @return prepared job or null if there were no new jobs to prepare. 
	 */
	Job prepareNewJob();

	/**
	 * Save a given job to persistent store.
	 * @param job
	 */
	Job saveNewJob(Job job);

	/**
	 * Mark a job finished.
	 * 
	 * @param job
	 */
	Job finishJob(Job job);

}