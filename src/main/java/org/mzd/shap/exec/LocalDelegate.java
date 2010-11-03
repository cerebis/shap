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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.mzd.shap.exec.ExecutableException;
import org.mzd.shap.exec.NonZeroReturnException;
import org.mzd.shap.exec.command.Command;


public class LocalDelegate extends BaseDelegate {
	private final static long WAIT_TIME = 200;
	private final static int IO_BUFFER_SIZE = 1024;
	private StringWriter stdErrWriter = new StringWriter();
	private StringWriter stdOutWriter = new StringWriter();

	/**
	 * Runs the Command instance as a local process.
	 */
	public void run(Command command) throws ExecutableException {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(command.createCommand());
			waitCycle(process);
		}
		catch (IOException ex) {
			if (process != null) {
				process.destroy();
				process = null;
			}
			throw new ExecutableException("Exception while getting Process from Runtime", ex);
		}
		finally {
			if (process != null) {
				cleanUp(process.getInputStream());
				cleanUp(process.getErrorStream());
				cleanUp(process.getOutputStream());
			}
		}
	}
	
	private void waitCycle(Process process) throws ExecutableException {
		
		if (process == null) {
			throw new ExecutableException("Process object was null");
		}
		
		synchronized (process) {
			while (true) {
				try {
					process.wait(WAIT_TIME);
					
					readStream(process.getErrorStream(), stdErrWriter);
					readStream(process.getInputStream(), stdOutWriter);
					
					if (isProcessEnded(process)) {
						break;
					}
				}
				catch (InterruptedException ex) {
					getLogger().warn("Process was interrupted", ex);
					break;
				}
			}
		}
		
		if (getExitStatus() != ExitStatus.OK) {
			throw new NonZeroReturnException(
					"Process [" + process + "] returned non-zero [" + getExitStatus() + "] " +
					"Stdout [" + getStdOut() + "] " +
					"Stderr [" + getStdErr() + "]" );
		}

	}
	
	private boolean isProcessEnded(Process process) {
		try {
			int ret = process.exitValue();
			if (ret == 0) {
				setExitStatus(ExitStatus.OK);
			}
			else {
				setExitStatus(ExitStatus.PROBLEM);
				getExitStatus().setValue(process.exitValue());
			}
			getLogger().debug("STDOUT: [" + getStdOut() + "]");
			getLogger().debug("STDERR: [" + getStdErr() + "]");
			return true;
		}
		catch (IllegalThreadStateException ex) {
			return false;
		}
	}
	
	private void cleanUp(InputStream stream) {
		try {
			stream.close();
		}
		catch (IOException ex) {
			getLogger().warn("Exception while cleaning up stream",ex);
		}
	}
	
	private void cleanUp(OutputStream stream) {
		try {
			stream.close();
		}
		catch (IOException ex) {
			getLogger().warn("Exception while cleaning up stream",ex);
		}
	}

	/**
	 * Reads all bytes of a supplied stream as was available
	 * at the time and writes them to the supplied writer.
	 * <p>
	 * This method is used to help insure that the Process does
	 * not block due to excessive output on stdout or stderr.
	 * 
	 * @param is - the input stream to read
	 * @param wr - the output writer
	 * @throws ExecutableException
	 */
	private void readStream(InputStream is, Writer wr) throws ExecutableException {
		InputStreamReader ir = null;
		try {
			/*
			 * Isolate the check for available bytes in its own 
			 * try/catch for cases when the stream has already been closed.
			 * 
			 * Logic below should not attempt to read from this stream.
			 */
			int nBytes = 0;
			try {
				nBytes = is.available();
			}
			catch (IOException ex) {
				getLogger().debug("Exception while checking available bytes",ex);
			}
			
			if (nBytes > 0) {
				ir = new InputStreamReader(is);
				char[] buffer = new char[IO_BUFFER_SIZE];
				while (nBytes > 0) {
					int nch = ir.read(buffer);
					if (nch == -1) {
						break;
					}
					wr.write(buffer, 0, nch);
					nBytes -= nch;
				}
			}
		} 
		catch (IOException ex) {
			throw new ExecutableException("Exception while reading from stream", ex);
		}	
		finally {
			if (ir != null) {
				try {
					ir.close();
				}
				catch (IOException ex) {
					throw new ExecutableException("Exception while closing stream", ex);
				}
			}
		}
	}

	public String getStdErr() {
		return stdErrWriter.toString();
	}

	public String getStdOut() {
		return stdOutWriter.toString();
	}

}
