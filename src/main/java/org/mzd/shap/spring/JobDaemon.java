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

/**
 * JobDaemon provides applications with a means of submitting jobs for analysis.
 * <p>
 * Implementations should support concurrent access.
 * 
 */
public interface JobDaemon {

	/**
	 * Check if any jobs have completed. If so, update their status to reflect this.
	 */
	void checkAndUpdateJobStatus();

	/**
	 * As jobs can be large in size, they are processed in blocks. Calling this
	 * method will queue a portion of submitted jobs for execution.
	 * <p>
	 * It is by repeated calls to this method that implementing classes will complete 
	 * submitted jobs. It is envisioned that either an observer pattern or periodic
	 * task will invoke this method. 
	 */
	void analyzeMore();

	/**
	 * Test whether an instance of JobDaemon currently has any pending work to complete.
	 * 
	 * @return true - there is pending work, false - all work as been completed.
	 */
	boolean pendingWork();

}