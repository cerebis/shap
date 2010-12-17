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
package org.mzd.shap.spring.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mzd.shap.domain.Project;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.authentication.User;
import org.mzd.shap.spring.DataViewService;
import org.mzd.shap.spring.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

public class AbstractControllerSupport {
	private Log logger = LogFactory.getLog(this.getClass());
	private DataViewService dataAdmin;

	/**
	 * Get the authenticated user from the SecurityContext.
	 * 
	 * @return user
	 * @throws NotFoundException
	 */
	protected User getSessionUser() throws NotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			User user = (User)auth.getPrincipal();
			if (user == null) {
				throw new NotFoundException("Authentication principle was null");
			}
			logger.debug(user);
			return user;
		}
		throw new NotFoundException("SecurityContext contained no authentication instance");
	}

	protected void addSessionUser(Model model) throws NotFoundException {
		model.addAttribute("user", getSessionUser());
	}
	
	/**
	 * Check stepwise down the object graph that each child is owned by its parent. The check
	 * goes from top to bottom and stops at the first occurrence of null in the supplied IDs.
	 * <p>
	 * This is done by multiple DB calls and could definitely be made into a single query. The quest
	 * is whether this would be efficient when joining 5 tables each time, with Sequence and Feature
	 * tables being very large.
	 * 
	 * @param projectId
	 * @param sampleId
	 * @param sequenceId
	 * @param featureId
	 * @throws NotFoundException thrown if the object graph does not contain path 
	 */
	protected void checkOwnership(Integer projectId, Integer sampleId, Integer sequenceId, Integer featureId) throws NotFoundException {
		if (projectId == null) {
			throw new NotFoundException("requested data was not found");
		}
		
		Project project = getDataAdmin().getProject(getSessionUser(),projectId);
		if (sampleId == null) {
			// Check stops here if supplied sampleId was null
			return;
		}
			
		Sample sample = getDataAdmin().getSample(project, sampleId);
		if (sequenceId == null) {
			// Check stops here if supplied sequenceId was null
			return;
		}
		
		Sequence sequence = getDataAdmin().getSequence(sample, sequenceId);
		if (featureId == null) {
			// Check stops here if supplied featureId was null
			return;
		}
		
		getDataAdmin().getFeature(sequence, featureId);
		
		// Checked all the way down to feature
		return;
	}
	
	@Autowired
	public void setDataAdmin(DataViewService dataAdmin) {
		this.dataAdmin = dataAdmin;
	}
	public DataViewService getDataAdmin() {
		return dataAdmin;
	}

	public Log getLogger() {
		return logger;
	}
}