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

public class SimpleExecutableFactoryBean {
	// Default delegate.
	private static final Class<? extends Delegate> DEFAULT_DELEGATE = LocalDelegate.class;
	
	/**
	 * Create a new instance of {@link SimpleExecutable} with a properly initialized
	 * {@link Delegate}.
	 * 
	 * @return an initialized {@link SimpleExecutable}
	 * @throws Exception
	 */
	public SimpleExecutable getObject() throws ExecutableException {
		try {
			SimpleExecutable exec = new SimpleExecutable();
			exec.setDelegate(createDelegate());
			return exec;
		}
		catch (Exception ex) {
			throw new ExecutableException(ex);
		}
	}

	/**
	 * Create a new instance of the {@link Delegate} dependency.
	 * <p>
	 * Users of this factory can use method injection to override this call to
	 * supply alternative delegates.
	 * 
	 * @return an initialized {@link Delegate}.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Delegate createDelegate() throws InstantiationException, IllegalAccessException {
		return DEFAULT_DELEGATE.newInstance();
	}
	
}
