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

import org.mzd.shap.spring.task.BaseTask;
import org.mzd.shap.spring.task.TaskException;
import org.mzd.shap.util.Notification;
import org.mzd.shap.util.Observable;
import org.mzd.shap.util.Observer;

public abstract class AdvisableTask extends BaseTask implements Observable {
	private TaskAdvisor advisor;
	
	public int countObservers() {
		return this.advisor == null ? 0 : 1;
	}

	public void notifyObservers(Notification notification) {
		this.advisor.update(notification);
	}

	public void registerObserver(Observer obs) {
		this.advisor = (TaskAdvisor)obs;
	}

	public void removeAllObservers() {
		this.advisor = null;
	}

	public void removeObserver(Observer obs) {
		this.advisor = null;
	}

	public TaskAdvisor getAdvisor() {
		return advisor;
	}
	public void setAdvisor(TaskAdvisor advisor) {
		registerObserver(advisor);
	}
	
	protected abstract void runInternal() throws TaskException;
	
	@Override
	public void run() {
		try {
			notifyObservers(TaskAdvisor.createBeforeNotification(this));
			runInternal();
			notifyObservers(TaskAdvisor.createAfterNotification(this));
		}
		catch (TaskException ex) {
			notifyObservers(TaskAdvisor
					.createErrorNotification(this,ex.getMessage()));
		}
	}

}
