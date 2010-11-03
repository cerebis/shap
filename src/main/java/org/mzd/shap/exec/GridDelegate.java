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
package org.mzd.shap.exec;

import java.util.List;


import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.JobTemplate;
import org.mzd.shap.exec.ExecutableException;
import org.mzd.shap.exec.NonZeroReturnException;
import org.mzd.shap.exec.command.Command;
import org.mzd.shap.exec.drmaa.GridException;
import org.mzd.shap.exec.drmaa.SessionBean;

public class GridDelegate extends BaseDelegate {
	private SessionBean session;
	
	/**
	 * Submits the Command instance as a job to the cluster's queue.
	 */
	public void run(Command command) throws ExecutableException {
		try {
			List<String> args = command.createCommandAsList();
			JobTemplate jt = getSession().createJobTemplate(args.remove(0),args);
			
			String id = getSession().runJob(jt);
			
			getLogger().debug("Submitted GridJob id[" + id + "]");
			getSession().deleteJobTemplate(jt);
			
			JobInfo info = getSession().waitForever(id);
			try {
				// aborted termination
				if (info.wasAborted()) {
					setExitStatus(ExitStatus.ABORTED);
					throw new NonZeroReturnException("GridJob id[" + id + "] aborted, never ran");
				}
				// normal termination
				else if (info.hasExited()) {
					if (info.getExitStatus() == 0) {
						setExitStatus(ExitStatus.OK);
					}
					else {
						setExitStatus(ExitStatus.PROBLEM);
						getExitStatus().setValue(info.getExitStatus());
					}
					String msg = "GridJob id[" + id + "] ended normally [" + getExitStatus() + "]";
					if (getExitStatus() == ExitStatus.PROBLEM) {
						throw new NonZeroReturnException(msg);
					}
					getLogger().debug(msg);
				}
				// signaled termination
				else if (info.hasSignaled()) {
					setExitStatus(ExitStatus.SIGNALED);
					getExitStatus().setMessage(info.getTerminatingSignal());
					throw new NonZeroReturnException("GridJob id[" + id + 
							"] ended from signal [" + getExitStatus() + "]");
				}
				// Unknown
				else {
					setExitStatus(ExitStatus.UNKNOWN);
					throw new NonZeroReturnException("GridJob id[" + id + 
							"] ended with unclear conditions");
				}
			}
			catch (DrmaaException ex) {
				throw new GridException(ex);
			}
		}
		catch (GridException ex) {
			getLogger().error(ex);
			throw new ExecutableException(ex);
		}
	}

	public SessionBean getSession() {
		return session;
	}
	public void setSessionBean(SessionBean session) {
		this.session = session;
	}
}
