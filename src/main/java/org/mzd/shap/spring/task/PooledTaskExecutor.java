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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mzd.shap.ApplicationException;
import org.mzd.shap.spring.task.aop.TaskAdvisor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Manages a configurable thread pool to which instances of Task can be submitted
 * for execution. The work queue is unbounded, but users should consider controlling
 * the number of tasks submitted at any one time for the sake of memory conservation.
 * <p>
 * The {@link #init()} and {@link #destroy()} methods are intended for lifecycle management and should
 * only be called once each. It is important to realize that on invoking {@link #destroy()}
 * that any tasks submitted at the time will be given only {@link #shutdownWaitTime}
 * seconds to complete, after which time they will be killed.
 * <p>
 * Methods {@link #addTask(Task)} and {@link #addTasks(Collection)} enable users of
 * this class to submit tasks for execution. The returned references to Future permit
 * inspection of status of submitted tasks.
 * 
 */
public class PooledTaskExecutor implements TaskExecutor, InitializingBean {
	private static Log LOGGER = LogFactory.getLog(PooledTaskExecutor.class);
	private int shutdownWaitTime = 300;
	private int corePoolSize = 1;
	private int maximumPoolSize = 1;
	private int queueCapacity = Integer.MAX_VALUE;
	private int keepAliveTime = 30;
	private ThreadPoolExecutor threadPool;
	private TaskAdvisor taskAdvisor;
	private Object poolSizeMonitor = new Object();

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(getTaskAdvisor(),"taskAdvisor is not set");
	}

	public TaskItem addTask(Task task) throws TaskException {
		LOGGER.debug("Submitting task [" + task + "] to queue");
		task.setAdvisor(getTaskAdvisor());
		return new TaskItem(getThreadPool().submit(task));
	}
	
	public List<TaskItem> addTasks(Collection<? extends Task> tasks) throws TaskException {
		List<TaskItem> futures = new ArrayList<TaskItem>();
		for (Task t : tasks) {
			futures.add(addTask(t));
		}
		return futures;
	}
	
	public boolean anyTasksRemaining() {
		return getCurrentScheduledTasks() > 0;
	}

	public long getLifetimeScheduledTasks() {
		return getThreadPool().getTaskCount();
	}
	
	public long getLifetimeCompletedTasks() {
		return getThreadPool().getCompletedTaskCount();
	}
	
	public int getCurrentActiveTasks() {
		return getThreadPool().getActiveCount();
	}
	
	public int getCurrentScheduledTasks() {
		long diff = getLifetimeScheduledTasks() - getLifetimeCompletedTasks();
		if (diff > Integer.MAX_VALUE) {
			throw new RuntimeException("Exceeded range of int");
		}
		return (int)diff;
	}
	
	/**
	 * Initialization method. This must be called before using the instance.
	 * 
	 * @throws ApplicationException
	 */
	public void init() throws ApplicationException {
		LOGGER.info("Initializing instance");
		if (getThreadPool() != null) {
			throw new ApplicationException("This instance has already been initialized");
		}
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(getQueueCapacity());
		setThreadPool(new ThreadPoolExecutor(getCorePoolSize(), getMaximumPoolSize(),
				getKeepAliveTime(), TimeUnit.SECONDS ,queue));
		getThreadPool().allowCoreThreadTimeOut(true);
	}

	/**
	 * Destroy method. This must be called when instance is garbage collected.
	 * <p>
	 * The method will attempt to cleanly shutdown the running thread pool,
	 * during such time new tasks cannot be submitted.
	 * <p>
	 * The maximum waiting time for a clean shutdown can be adjusted with
	 * {@link #setShutdownWaiT(int)}
	 */
	public void destroy() {
		LOGGER.info("Closing down instance");
		if (getThreadPool() == null) {
			LOGGER.debug("No threadPool instance needs to be shutdown");
		}
		else {
			shutdownPool();
		}
	}
	
	/**
	 * Restart a terminated threadpool.
	 * 
	 * @throws ApplicationException
	 */
	public void restartPool() throws ApplicationException {
		synchronized (poolSizeMonitor) {
			if (getThreadPool().isTerminated()) {
				setThreadPool(null);
				init();
			}
			else {
				LOGGER.warn("Cannot restart threadPool, not in a terminated state.");
			}
		}
	}

	/**
	 * Shutdown the internal threadPool.
	 * <p>
	 * The calling thread will wait for any submitted but uncompleted tasks to 
	 * finish before returning. If the time expires before the jobs have finished
	 * then the pool with be shutdown forcibly. This may not successfully close
	 * down running tasks.
	 * <p>
	 * The waiting period can be adjusted by {@link #setShutdownWaiT(int)}
	 */
	public void shutdownPool() {
		synchronized (poolSizeMonitor) {
			LOGGER.info("Initiating shutdown of threadPool, new tasks cannot be submitted");
			getThreadPool().shutdown();
			try {
				LOGGER.info("Waiting for submitted tasks to complete");
				if (getThreadPool().awaitTermination(
						getShutdownWaitTime(), TimeUnit.SECONDS) == false) {
					LOGGER.warn("Forcing shutdown: waiting period expired before all tasks completed.");
					List<Runnable> killed = getThreadPool().shutdownNow();
					for (Runnable r : killed) {
						LOGGER.warn("Attempted to forcibly kill task [" + r + "]" +
								", this may not have succeeded in a clustered environment");
					}
				}
				
				LOGGER.warn("There were " + getThreadPool().getQueue().size() + 
						" tasks left queued that need a status update");
			}
			catch (InterruptedException ex) {
				LOGGER.warn("Interrupted during shutdown " + ex);
			}
		}
	}
	
	public boolean isShutdown() {
		return getThreadPool().isShutdown();
	}
	
	/**
	 * The core number of threads in the pool.
	 * <p>
	 * This is the minimum number of threads that remain even when idle.
	 * 
	 * @param corePoolSize minimum number of threads not subject to idle disposal
	 */
	public void setCorePoolSize(int corePoolSize) {
		synchronized (poolSizeMonitor) {
			this.corePoolSize = corePoolSize;
			if (getThreadPool() != null) {
				getThreadPool().setCorePoolSize(corePoolSize);
			}
		}
	}
	public int getCorePoolSize() {
		synchronized (poolSizeMonitor) {
			return corePoolSize;
		}
	}

	/**
	 * The maximum number of threads for the pool.
	 * 
	 * @param maximumPoolSize maximum number of threads for pool
	 */
	public void setMaximumPoolSize(int maximumPoolSize) {
		synchronized (poolSizeMonitor) {
			this.maximumPoolSize = maximumPoolSize;
			if (getThreadPool() != null) {
				getThreadPool().setMaximumPoolSize(maximumPoolSize);
			}
		}
	}
	public int getMaximumPoolSize() {
		synchronized (poolSizeMonitor) {
			return maximumPoolSize;
		}
	}

	/**
	 * The time in seconds a thread in the pool should be kept alive after becoming idle.
	 * 
	 * @param keepAliveTime time in seconds to keep idle threads alive
	 */
	public void setKeepAliveTime(int keepAliveTime) {
		synchronized (poolSizeMonitor) {
			this.keepAliveTime = keepAliveTime;
			if (getThreadPool() != null) {
				getThreadPool().setKeepAliveTime(keepAliveTime, TimeUnit.SECONDS);
			}
		}
	}
	public int getKeepAliveTime() {
		synchronized (poolSizeMonitor) {
			return keepAliveTime;
		}
	}

	/**
	 * The time in seconds that the TaskManager should wait after invoking {@link #shutdownPool()} before
	 * any remaining running tasks are terminated.
	 * 
	 * @param shutdownWaitTime - waiting period in seconds
	 */
	public void setShutdownWaitTime(int shutdownWaitTime) {
		synchronized (poolSizeMonitor) {
			this.shutdownWaitTime = shutdownWaitTime;
		}
	}
	public int getShutdownWaitTime() {
		synchronized (poolSizeMonitor) {
			return shutdownWaitTime;
		}
	}

	/**
	 * Capacity of underlying queue.
	 * <p>
	 * The capacity cannot be changed after initialization. The default capacity is {@link Integer.MAX_SIZE}. A queue
	 * capacity less than the number of threads in the pool may not make much sense.
	 * 
	 * @param queueCapacity
	 */
	public void setQueueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}
	public int getQueueCapacity() {
		return this.queueCapacity;
	}
	
	protected ThreadPoolExecutor getThreadPool() {
		return threadPool;
	}
	protected void setThreadPool(ThreadPoolExecutor threadPool) {
		this.threadPool = threadPool;
	}

	public TaskAdvisor getTaskAdvisor() {
		return taskAdvisor;
	}
	public void setTaskAdvisor(TaskAdvisor taskAdvisor) {
		this.taskAdvisor = taskAdvisor;
	}

}
