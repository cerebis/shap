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
package org.mzd.shap.util;

public class Notification {
	private String type;
	private Object source;
	private String message;
	private long timeStamp;
	
	public Notification(String type, Object source, String message, long timeStamp) {
		this.type = type;
		this.source = source;
		this.message = message;
		this.timeStamp = timeStamp;
	}
	
	public Notification(String type, Object source, String message) {
		this.type = type;
		this.source = source;
		this.message = message;
	}
	
	public Notification(String type, Object source, long timeStamp) {
		this.type = type;
		this.source = source;
		this.timeStamp = timeStamp;
	}
	
	public Notification(String type, Object source) {
		this.type = type;
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

}
