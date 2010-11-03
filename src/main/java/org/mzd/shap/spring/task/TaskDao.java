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

import org.mzd.shap.hibernate.BaseDao;

public interface TaskDao extends BaseDao<Task, Integer> {

	/**
	 * Find NEW tasks contained in a specific job.
	 * 
	 * @param job the containing job
	 * @param maxResults the maximum number of tasks to return
	 * @return a list of tasks, possibly empty.
	 */
	List<Task> findNewByJob(Job job, int maxResults);
	
	/**
	 * Find NEW tasks in any job with a specific status.
	 * 
	 * @param jobStatus the job status that should be matched
	 * @param maxResults the maximum number of tasks to return 
	 * @return a list of tasks, possibly empty.
	 */
	List<Task> findNew(Status jobStatus, int maxResults);
	
	/**
	 * Find NEW tasks in any jobs which have been started.
	 * 
	 * @param maxResults the maximum number of tasks to return
	 * @return a list of tasks, possibly empty.
	 */
	List<Task> findNewInStartedJobs(int maxResults);
	
	/**
	 * Count NEW tasks in all jobs of any status.
	 * 
	 * @return the number of NEW tasks
	 */
	long countNew();
	
	/**
	 * Count NEW tasks in all started jobs.
	 * 
	 * @return the number of NEW tasks
	 */
	long countNewInStartedJobs();
		
	/**
	 * Count incomplete tasks in all jobs of any status.
	 * <p>
	 * Here incomplete means a tasks whose status is not an endpoint status.
	 *  
	 * @return the number of incomplete tasks.
	 */
	long countIncomplete();
	
	/**
	 * Count incomplete tasks in all started jobs.
	 * 
	 * @return the number of incomplete tasks
	 */
	long countIncompleteInStartedJobs();
}
