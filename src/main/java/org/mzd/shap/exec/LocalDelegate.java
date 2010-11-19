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

import java.io.IOException;
import java.io.InputStream;

import org.mzd.shap.exec.ExecutableException;
import org.mzd.shap.exec.NonZeroReturnException;
import org.mzd.shap.exec.command.Command;

public class LocalDelegate extends BaseDelegate {

	/**
	 * Handles the reading of stdout and stderr streams associated with the running process.
	 * <p>
	 * Each handler runs as a separate thread, blocking on IO.
	 */
	protected class InputStreamHandler extends Thread {
		private InputStream stream;
		private StringBuffer captureBuffer;

		InputStreamHandler(StringBuffer captureBuffer, InputStream stream) {
			this.stream = stream;
			this.captureBuffer = captureBuffer;
			start();
		}
		
		public void run() {
			try {
				int nextChar;
				while ((nextChar = stream.read()) != -1) {
					this.captureBuffer.append((char) nextChar);
				}
			}
			catch (IOException ex) {
				getLogger().error("Error while reading stream",ex);
			}
		}
	}

	/**
	 * Runs the Command instance as a local process.
	 */
	public void run(Command command) throws ExecutableException {
		Process process = null;
		try {
			StringBuffer inBuffer = new StringBuffer();
			StringBuffer errBuffer = new StringBuffer();

			process = Runtime.getRuntime().exec(command.createCommand());
			if (process == null) {
				throw new ExecutableException("Process object was null");
			}
			
			new InputStreamHandler(inBuffer, process.getInputStream());
			new InputStreamHandler(errBuffer, process.getErrorStream());
			
			waitFor(process);
			
			if (getExitStatus() != ExitStatus.OK) {
				throw new NonZeroReturnException(
						"Process [" + process + "] returned non-zero [" + getExitStatus() + "] " +
						"Stdout [" + inBuffer.toString() + "] " +
						"Stderr [" + errBuffer.toString() + "]" );
			}
		}
		catch (IOException ex) {
			if (process != null) {
				process.destroy();
				process = null;
			}
			throw new ExecutableException("Exception while getting Process from Runtime", ex);
		}
	}
	
	private void waitFor(Process process) throws ExecutableException {
		synchronized (process) {
			try {
				if (process.waitFor() == 0) {
					setExitStatus(ExitStatus.OK);
				}
				else {
					setExitStatus(ExitStatus.PROBLEM);
					getExitStatus().setValue(process.exitValue());
				}
			}
			catch (InterruptedException ex) {
				getLogger().warn("Process was interrupted", ex);
				setExitStatus(ExitStatus.ABORTED);
				getExitStatus().setMessage("process was interrupted [" + ex.getMessage() + "]");
			}
		}
	}
}
