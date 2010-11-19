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

import org.mzd.shap.spring.task.Task;

public interface Advice {

	/**
	 * Invoke advice on a task object. If the task requires access to persistence store,
	 * make sure it is attached and avoid lazy initialization exceptions.
	 * 
	 * @param task the task to handle
	 * @return the handled task, which will be attached to the session.
	 */
	Task invoke(Task task);
}
