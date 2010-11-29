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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mzd.shap.exec.SimpleExecutableFactoryBean;
import org.mzd.shap.spring.task.Task;
import org.mzd.shap.util.Notification;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class TaskAdvisorImpl implements TaskAdvisor {
	private static Log LOGGER = LogFactory.getLog(TaskAdvisor.class);
	private final static String BEFORE = "task.before";
	private final static String AFTER = "task.after";
	private final static String ERROR = "task.error";
	private TransactionTemplate transactionTemplate;
	private SimpleExecutableFactoryBean executableFactory;
	private Map<Class<? extends Task>, Advice> beforeAdvice;
	private Map<Class<? extends Task>, Advice> afterAdvice;
	private Advice errorAdvice;
	
	protected static Notification createNotification(String type, Object source) {
		return new Notification(type,source,System.currentTimeMillis());
	}
	
	public static Notification createBeforeNotification(Object source) {
		return createNotification(BEFORE, source);
	}
	
	public static Notification createAfterNotification(Object source) {
		return createNotification(AFTER, source);
	}

	public static Notification createErrorNotification(Object source, String msg) {
		return new Notification(ERROR, source, msg, System.currentTimeMillis());
	}

	class InvokeAdviceCallback implements TransactionCallback<Task> {
		private Advice advice;
		private Task task;

		public InvokeAdviceCallback(Advice advice, Task task) {
			this.advice = advice;
			this.task = task;
		}
		
		public Task doInTransaction(TransactionStatus status) {
			try {
				return advice.invoke(task);
			}
			catch (Throwable t) {
				LOGGER.error("Caught throwable while performing transaction for [" + 
						advice + "] on task [" + task + "]",t);
				status.setRollbackOnly();
				return null;
			}
		}
	}

	@Override
	public void update(final Notification notification) {
		Task t = (Task)notification.getSource();
		try {
			Advice a = null;
			t.setExecutable(executableFactory.getObject());
			
			String advType = notification.getType();
			LOGGER.debug("Doing " + advType  + " advise");
			
			if (advType.equals(BEFORE)) {
				a = getBeforeAdvice().get(t.getClass());
			}
			else if (advType.equals(AFTER)) {
				a = getAfterAdvice().get(t.getClass());
			}
			else if (advType.equals(ERROR)) {
				t.setComment(notification.getMessage());
				a = getErrorAdvice();
			}
			
			if (a == null) {
				throw new RuntimeException("No advise registered of type [" + 
						notification.getType() + "] for task [" + t + "]");
			}
			
			getTransactionTemplate().execute(new InvokeAdviceCallback(a, t));
		}
		catch (Exception ex) {
			LOGGER.error("An exception occured while invoking advice for task [" + t + 
					"]. Now trying to invoke error advice on task",ex);
			t.setComment(ex.getMessage());
			getTransactionTemplate().execute(new InvokeAdviceCallback(getErrorAdvice(), t));
		}
	}

	public SimpleExecutableFactoryBean getExecutableFactory() {
		return executableFactory;
	}
	public void setExecutableFactory(SimpleExecutableFactoryBean executableFactory) {
		this.executableFactory = executableFactory;
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public Map<Class<? extends Task>, Advice> getBeforeAdvice() {
		return beforeAdvice;
	}
	public void setBeforeAdvice(Map<Class<? extends Task>, Advice> beforeAdvice) {
		this.beforeAdvice = beforeAdvice;
	}

	public Map<Class<? extends Task>, Advice> getAfterAdvice() {
		return afterAdvice;
	}
	public void setAfterAdvice(Map<Class<? extends Task>, Advice> afterAdvice) {
		this.afterAdvice = afterAdvice;
	}

	public Advice getErrorAdvice() {
		return errorAdvice;
	}
	public void setErrorAdvice(Advice errorAdvice) {
		this.errorAdvice = errorAdvice;
	}

}
