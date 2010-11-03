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

/**
 * Representing a status which can be either transient (not an endpoint, composing objects can/will change
 * state further) and final (an endpoint has been reached, no further actions should be taken on the composing
 * object or its children).
 *  
 */
public enum Status {
	/**
	 * Transient states
	 */
	NEW(false),
	QUEUED(false),
	STARTED(false),
	PAUSED(false),
	
	/**
	 * Final states
	 */
	CANCEL(true),
	DONE(true),
	ERROR(true);

	/**
	 * Composing objects should be regarded as having reached a final state any time 
	 * endPointState=true. No further actions should be taken on the object or any of its 
	 * children. The state should first changed to a non-EndPoint if further actions
	 * are to be taken.
	 * <p>
	 * This contract will be respected by collaborating objects.
	 * 
	 * @return
	 */
	public boolean isEndPointState() {
		return endPointState;
	}
	
	private Boolean endPointState;
	private Status(Boolean endPointState) {
		this.endPointState = endPointState;
	}
	
}
