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
package org.mzd.shap.spring.web.filter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

public class AjaxAwareAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
	private Log logger = LogFactory.getLog(AjaxAwareAuthenticationEntryPoint.class);
	private Pattern ajaxRequestPattern = Pattern.compile("ajax$");
	private Integer statusCode = 601;
	
	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {

		Matcher matcher = this.ajaxRequestPattern.matcher(request.getRequestURI());
		if (matcher.find()) {
			logger.debug("Identified unauthenticated AJAX request [" + request.getRequestURI() + 
					"] which will be handled as an error with status " + getStatusCode());
			response.sendError(getStatusCode(), "Unauthenticated AJAX request");
		}
		else {
			super.commence(request, response, authException);
		}
	}
	
	protected Log getLogger() {
		return logger;
	}

	public void setAjaxRequestPattern(String ajaxRequestPattern) {
		this.ajaxRequestPattern = Pattern.compile(ajaxRequestPattern);
	}
	public String getAjaxRequestPattern() {
		return this.ajaxRequestPattern.toString();
	}
	
	public Integer getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	
}