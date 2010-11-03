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
package org.mzd.shap.exec.drmaa;

import java.util.List;

import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.JobTemplate;
import org.ggf.drmaa.Session;
import org.ggf.drmaa.SessionFactory;

/**
 * A Drmaa session in the form of a bean for easy configuration in Spring. This
 * should be treated as a singleton, since only one session can be opened per
 * thread hierarchy.
 *
 * The init() and destroy() methods need to be called for lifecycle management.
 * (or perferably specified in a Spring bean file as init-method and destroy-method)
 * 
 */
public class SessionBean {
	private final static String DEFAULT_SPECIFICATION = "-w e -p 0 -b yes -V -shell yes";
	private String specification = DEFAULT_SPECIFICATION;
	private Session session;
	
	public SessionBean() {
		this.session = SessionFactory.getFactory().getSession();
	}
	
	public void init() throws GridException {
		try {
			getSession().init("");
		}
		catch (DrmaaException ex) {
			throw new GridException(ex);
		}
	}
	
	public void destroy() throws GridException {
		try {
			getSession().exit();
		}
		catch (DrmaaException ex) {
			throw new GridException(ex);
		}
	}
	
	public JobTemplate createJobTemplate(String cmd, List<String> args) throws GridException {
		try {
			JobTemplate jt = getSession().createJobTemplate();
			jt.setRemoteCommand(cmd);
			jt.setArgs(args);
			jt.setNativeSpecification(getSpecification());
			return jt;
		}
		catch (DrmaaException ex) {
			throw new GridException(ex);
		}
	}
	
	public void deleteJobTemplate(JobTemplate jt) throws GridException {
		try {
			getSession().deleteJobTemplate(jt);
		}
		catch (DrmaaException ex) {
			throw new GridException(ex);
		}
	}
	
	public String runJob(JobTemplate jt) throws GridException {
		try {
			return getSession().runJob(jt);
		}
		catch (DrmaaException ex) {
			throw new GridException(ex);
		}
	}
	
	public JobInfo waitForever(String id) throws GridException {
		try {
			return getSession().wait(id, Session.TIMEOUT_WAIT_FOREVER);
		}
		catch (DrmaaException ex) {
			throw new GridException(ex);
		}
	}
	
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}

	public String getSpecification() {
		return specification;
	}
	public void setSpecification(String specification) {
		this.specification = specification;
	}

}
