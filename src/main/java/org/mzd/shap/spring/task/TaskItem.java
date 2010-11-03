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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TaskItem {
	private final static long SHORT_WAIT_TIME = 5; //milliseconds
	private Future<?> future;
	
	public TaskItem(Future<?> future) {
		if (future == null) {
			throw new NullPointerException("Future reference is null");
		}
		this.future = future;
	}
	
	public boolean isUnfinished() throws InterruptedException, ExecutionException {
		try {
			get(SHORT_WAIT_TIME);
			return false;
		}
		catch (TimeoutException ex) {
			return true;
		}
	}

	public Object get(long timeout_ms) throws InterruptedException, ExecutionException, TimeoutException  {
		return future.get(timeout_ms, TimeUnit.MILLISECONDS);
	}
	
	public boolean isCancelled() {
		return future.isCancelled();
	}
	
	public boolean isDone() {
		return future.isDone();
	}

}
