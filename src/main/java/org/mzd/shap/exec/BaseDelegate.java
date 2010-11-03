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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class BaseDelegate implements Delegate {
	private Log logger = LogFactory.getLog(this.getClass());
	private ExitStatus exitStatus;

	enum ExitStatus {
		OK,
		PROBLEM {
			@Override
			public String toString() {
				return super.toString() + " value [" + getValue() + "] " +
						"message [" + getMessage() + "]";
			}		
		},
		SIGNALED {
			@Override
			public String toString() {
				return super.toString() + " message [" + getMessage() + "] " +
						"message [" + getMessage() + "]";
			}		
		},
		ABORTED,
		UNKNOWN;
		
		private int value = 0;
		private String message;
		
		public void setValue(int value) {
			this.value = value;
		}
		public int getValue() {
			return value;
		}
		
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
	}

	public ExitStatus getExitStatus() {
		return exitStatus;
	}
	protected void setExitStatus(ExitStatus exitStatus) {
		this.exitStatus = exitStatus;
	}

	public Log getLogger() {
		return logger;
	}
	public void setLogger(Log logger) {
		this.logger = logger;
	}
}