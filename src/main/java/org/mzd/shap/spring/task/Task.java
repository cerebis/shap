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

import java.util.Date;

import org.mzd.shap.exec.SimpleExecutable;
import org.mzd.shap.spring.task.aop.TaskAdvisor;


/**
 * A parcel of executable work, one or more of which comprises a Job.
 * 
 */
public interface Task extends Runnable {
	
	Integer getId();
	void setId(Integer id);

	Integer getVersion();
	void setVersion(Integer version);

	Date getStart();
	void setStart(Date start);
	
	Date getFinish();
	void setFinish(Date finish);

	Job getJob();
	void setJob(Job job);
	
	Status getStatus();
	void setStatus(Status status);
	
	String getComment();
	void setComment(String comment);
	
	void markStart();
	void markFinish();
	void markQueued();
	void markError();
	
	void setExecutable(SimpleExecutable executable);
	SimpleExecutable getExecutable();
	
	void setAdvisor(TaskAdvisor advisor);
	TaskAdvisor getAdvisor();
}
