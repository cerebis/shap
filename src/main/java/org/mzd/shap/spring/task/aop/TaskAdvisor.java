package org.mzd.shap.spring.task.aop;

import org.mzd.shap.util.Notification;
import org.mzd.shap.util.Observer;

public interface TaskAdvisor extends Observer {

	/**
	 * Invoke advice on a task based on the notification.
	 */
	void update(Notification notification);

}