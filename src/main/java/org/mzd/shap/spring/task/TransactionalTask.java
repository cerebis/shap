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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Deprecated
public abstract class TransactionalTask extends BaseTask {
	private static Log LOGGER = LogFactory.getLog(TransactionalTask.class);
	private static final int MAX_RETRIES = 2;
	private static final int WAIT_TIME = 1000;
	private TransactionTemplate transactionTemplate;
	private TaskDao taskDao;

	/**
	 * Executes before the actual unit of work within a transaction to load
	 * target objects from store.

	 * @throws TaskException
	 */
	protected abstract void targetRead() throws TaskException;
	
	/**
	 * Executes after the actual unit of work within a transaction to write
	 * result objects to store.
	 * 
	 * @throws TaskException
	 */
	protected abstract void resultWrite() throws TaskException;

	/**
	 * Concrete subclasses implement this method to perform the particular
	 * unit of work. As this method executes outside of a transaction, arbitrary
	 * length run times are not a problem.
	 * 
	 * @throws TaskException
	 */
	protected abstract void runInternal() throws TaskException;

	/**
	 * Simple callback with a basic retry facility for updating the persistent
	 * instance of the superclass.
	 * 
	 */
	abstract class ShortTransactionCallback implements TransactionCallback<Object> {
		
		protected abstract void doInternal() throws TaskException;

		public Object doInTransaction(TransactionStatus status) {
			LOGGER.debug("executing doInTransaction for task.id=" + getId());
			int n = 0;
			while (n < MAX_RETRIES) {
				try {
					doInternal();
					break;
				}
				catch (Throwable t) {
					LOGGER.warn(this,t);
					synchronized (this) {
						try {
							LOGGER.warn("retry " + n + " waiting 1000ms");
							wait(WAIT_TIME);
						}
						catch (InterruptedException ex) {
							status.setRollbackOnly();
							break;
						}
					}
				}
				n++;
			}
			
			if (n == MAX_RETRIES) {
				status.setRollbackOnly();
			}
			
			return null;
		}
	}
	
	class ReadTransaction extends ShortTransactionCallback {
		protected void doInternal() throws TaskException {
			targetRead();
			markStart();
			getTaskDao().makePersistent(TransactionalTask.this);
		}
	}
	
	class WriteTransaction extends ShortTransactionCallback {
		protected void doInternal() throws TaskException {
			resultWrite();
			markFinish();
			getTaskDao().makePersistent(TransactionalTask.this);
		}
	}
	
	class ErrorTransaction extends ShortTransactionCallback {
		private Exception ex;
		public ErrorTransaction(Exception ex) {
			this.ex = ex;
		}
		protected void doInternal() throws TaskException {
			markError();
			setComment(ex.getClass().getName() + " " + ex.getMessage());
			getTaskDao().makePersistent(TransactionalTask.this);
		}
	}
	
	public void run() {
		try {
			// TX 1
			LOGGER.debug("Enter readTransaction task.id=" + this.getId());
			getTransactionTemplate().execute(new ReadTransaction());
			LOGGER.debug("Finished readTransaction task.id=" + this.getId());
			
			// Outside TX - unlimited run time.
			LOGGER.debug("Enter runInternal task.id=" + this.getId());
			runInternal();
			LOGGER.debug("Finished runInternal task.id=" + this.getId());

			// TX 2
			LOGGER.debug("Enter writeTansaction task.id=" + this.getId());
			getTransactionTemplate().execute(new WriteTransaction());
			LOGGER.debug("Finished writeTransaction task.id=" + this.getId());
		}
		catch (TaskException ex) {
			getTransactionTemplate().execute(new ErrorTransaction(ex));
		}
		finally {
			getTaskDao().evict(this);
		}
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public TaskDao getTaskDao() {
		return taskDao;
	}
	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}
	
}
