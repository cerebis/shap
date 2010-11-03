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

import java.util.Collection;
import java.util.List;

public interface TaskExecutor {

	/**
	 * Add a single task to the queue.
	 * 
	 * @param task - the task to add.
	 * @return Future reference for this task.
	 */
	public abstract TaskItem addTask(Task task) throws TaskException;

	/**
	 * Add a collection of tasks to the queue.
	 * 
	 * @param tasks - the tasks to add.
	 * @return the list of Future references for the submitted tasks.
	 */
	public abstract List<TaskItem> addTasks(Collection<? extends Task> tasks)  throws TaskException;

	/**
	 * Test if there are any tasks remaining to be processed.
	 * <p>
	 * This should be treated as only an estimate.
	 * 
	 * @return true - there are tasks remaining to be processed.
	 */
	public abstract boolean anyTasksRemaining();

	/**
	 * Get the number of scheduled tasks for the lifetime of
	 * this TaskManager.
	 * 
	 * @return the lifetime number of scheduled tasks.
	 */
	public abstract long getLifetimeScheduledTasks();

	/**
	 * Get the number of completed tasks for the lifetime of
	 * this TaskManager.
	 * 
	 * @return the lifetime number of completed tasks.
	 */
	public abstract long getLifetimeCompletedTasks();

	/**
	 * Get the number of currently active tasks for this
	 * TaskManager.
	 * 
	 * @return the number of active tasks.
	 */
	public abstract int getCurrentActiveTasks();

	/**
	 * Get the number of scheduled tasks waiting to be processed and also
	 * those which are currently active but unfinished.	 * 
	 * 
	 * @return the number of remaining tasks.
	 */
	public abstract int getCurrentScheduledTasks();

}