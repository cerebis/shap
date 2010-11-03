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
package org.mzd.shap.spring.web.command;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

public class FindByIdQuery {
	@NotNull
	private DomainTarget target;
	private String textIds;
	private MultipartFile fileIds;

	public static enum DomainTarget {
		FEATURE,
		SEQUENCE;
	}
	
	public DomainTarget getTarget() {
		return target;
	}
	public void setTarget(DomainTarget target) {
		this.target = target;
	}
	public String getTextIds() {
		return textIds;
	}
	public void setTextIds(String textIds) {
		this.textIds = textIds;
	}
	public MultipartFile getFileIds() {
		return fileIds;
	}
	public void setFileIds(MultipartFile fileIds) {
		this.fileIds = fileIds;
	}
}
