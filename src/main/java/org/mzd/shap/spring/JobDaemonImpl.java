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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mzd.shap.ApplicationException;
import org.mzd.shap.spring.task.Job;
import org.mzd.shap.spring.task.PooledTaskExecutor;
import org.mzd.shap.spring.task.Task;
import org.mzd.shap.spring.task.TaskException;
import org.mzd.shap.util.BaseObservable;
import org.mzd.shap.util.Notification;
import org.mzd.shap.util.Observable;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(objectName="bean:name=jobDaemon",description="Job Processing Daemon")
public class JobDaemonImpl extends BaseObservable implements JobDaemon, Observable {
	private static Log LOGGER = LogFactory.getLog(JobDaemonImpl.class);
	private int maxQueued;
	private BatchAdminService batchAdminService;
	private PooledTaskExecutor taskExecutor;
	
	public synchronized void checkAndUpdateJobStatus() {
		List<Job> runningJobs = getBatchAdminService().getStartedJobs();
		for (Job rj : runningJobs) {
			// Count number of incomplete tasks in each running job.
			// Update status to done if no tasks left to process.
			if (getBatchAdminService().countIncompleteTasks(rj) == 0) {
				String msg = String.format("all tasks in job %d have completed", rj.getId());
				notifyObservers(new Notification("shap.jobdaemon",this,msg));
				LOGGER.debug(msg);
				getBatchAdminService().finishJob(rj);
			}
		}
	}
	
	/**
	 * Schedule a block of tasks based on how many free queue slots exist, where
	 * the aim is to keep the queue at filled.
	 */
	public synchronized void analyzeMore() {
		// Is processing active and are there incomplete jobs
		if (!getTaskExecutor().isShutdown() && 
				getBatchAdminService().countIncompleteJobs() > 0) {
			
			// Is the queue full
			int availSlots = getAvailableSlots();
			LOGGER.debug("Available slots: " + availSlots);
			if (availSlots > 0) {
				
				List<Task> queuedTasks = getBatchAdminService()
					.prepareNewTasks(availSlots);
				
				LOGGER.debug("New tasks to queue: " + queuedTasks.size());
				
				// Schedule prepared tasks if we found some
				if (queuedTasks.size() > 0) {
					try {
						getTaskExecutor().addTasks(queuedTasks);
					}
					catch (TaskException ex) {
						LOGGER.error(ex);
					}
				}
				// We finished the work. Tidy up, and perhaps begin another job.
				else {
					checkAndUpdateJobStatus();
					if (getBatchAdminService().prepareNewJob() == null) {
						LOGGER.debug("no outstanding jobs left to process");
					}
				}
			}
		}
	}

	public boolean pendingWork() {
		long numTasks = getBatchAdminService().countIncompleteTasks();
		long numJobs = getBatchAdminService().countIncompleteJobs();
		String msg = String.format("there are %3d jobs and %6d tasks pending", numJobs, numTasks);
		notifyObservers(new Notification("shap.jobdaemon", this, msg));
		LOGGER.debug(msg);
		return numTasks > 0 || numJobs > 0;
	}
	
	protected int getAvailableSlots() {
		return getMaxQueued() - getTaskExecutor().getCurrentScheduledTasks();
	}

	/**
	 * Check if the PooledTaskExecutor has less than the maximum allowable number of active
	 * tasks.
	 * 
	 * @return true - the taskExecutor has less than the minimum number of active tasks.
	 */
	protected boolean isAvailable() {
		return getTaskExecutor().getCurrentScheduledTasks() < getMaxQueued();
	}

	// -- JMX controls --
	
	@ManagedOperation(description="cleanly shutdown executor")
	public void shutdown() {
		getTaskExecutor().shutdownPool();
	}
	
	@ManagedOperation(description="restart a shutdown executor")
	public void restart() throws ApplicationException {
		getTaskExecutor().restartPool();
	}
	
	@ManagedAttribute(description="core number of concurrent execution threads")
	public void setCorePoolSize(int poolSize) {
		getTaskExecutor().setCorePoolSize(poolSize);
	}
	@ManagedAttribute
	public int getCorePoolSize() {
		return getTaskExecutor().getCorePoolSize();
	}

	@ManagedAttribute(description="maximum number of concurrent execution threads")
	public void setMaxPoolSize(int poolSize) {
		getTaskExecutor().setMaximumPoolSize(poolSize);
	}
	@ManagedAttribute
	public int getMaxPoolSize() {
		return getTaskExecutor().getMaximumPoolSize();
	}
	
	@ManagedAttribute(description="seconds to wait for running threads after shutdown invoked")
	public void setShutdownWaitTime(int seconds) {
		getTaskExecutor().setShutdownWaitTime(seconds);
	}
	@ManagedAttribute
	public int getShutdownWaitTime() {
		return getTaskExecutor().getShutdownWaitTime();
	}

	@ManagedAttribute(description="maximum number of queued tasks awaiting execution")
	public void setMaxQueued(int maxQueued) {
		this.maxQueued = maxQueued;
	}
	@ManagedAttribute
	public int getMaxQueued() {
		return maxQueued;
	}

	// -- set/get --]
	
	public PooledTaskExecutor getTaskExecutor() {
		return taskExecutor;
	}
	public void setTaskExecutor(PooledTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public BatchAdminService getBatchAdminService() {
		return batchAdminService;
	}
	public void setBatchAdminService(BatchAdminService batchAdminService) {
		this.batchAdminService = batchAdminService;
	}

}
