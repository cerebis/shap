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

import java.util.List;

import org.mzd.shap.spring.task.Job;
import org.mzd.shap.spring.task.Status;

public interface BatchViewService {

	/**
	 * Load job from store
	 */
	Job loadUnfinishedJob(Integer jobId) throws NotFoundException;

	/**
	 * Get the list of started jobs.
	 * <p>
	 * Incomplete jobs are those whose status is not at an end-point.
	 * Eg. {@link Status.NEW}, {@link Status.QUEUED}, {@link Status.STARTED}.
	 * 
	 * @return list of jobs.
	 */
	List<Job> getStartedJobs();

	/**
	 * Count the number of tasks with status {@link Status.NEW}
	 * 
	 * @return the number of NEW tasks.
	 */
	long countNewTasks();

	/**
	 * Count the number of incomplete tasks.
	 * <p>
	 * Incomplete tasks are those whose status is not at an end-point.
	 * Eg. {@link Status.NEW}, {@link Status.QUEUED}, {@link Status.STARTED}.
	 * 
	 * @return the number of incomplete tasks.
	 */
	long countIncompleteTasks();

	/**
	 * Count the number of incomplete tasks for the associated job.
	 * <p>
	 * Incomplete tasks are those whose status is not at an end-point.
	 * Eg. {@link Status.NEW}, {@link Status.QUEUED}, {@link Status.STARTED}.
	 * 
	 * @return the number of incomplete tasks.
	 */
	long countIncompleteTasks(Job job);
	
	/**
	 * Count the new of jobs with a non-endpoint status.
	 * 
	 * @return
	 */
	long countIncompleteJobs();

}