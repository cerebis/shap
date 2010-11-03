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
package org.mzd.shap.io.bean;

import java.io.File;
import java.io.Reader;

/**
 * Bean IO interface.
 * 
 * Writing/reading a bean of class BEAN to and from storage. 
 * 
 * @param <BEAN>
 * 
 */
public interface BeanIO<BEAN> {

	/**
	 * Instantiate a bean of class BEAN using the state read from a file.
	 * 
	 * @param file - the file from which to read the state of the bean.
	 * @return the instance of the bean.
	 * @throws BeanIOException - wraps exceptions thrown internally by the implementation.
	 */
	BEAN read(File file) throws BeanIOException;
	
	/**
	 * Read a bean from a open {@link Reader}
	 * 
	 * @param reader
	 * @return
	 * @throws BeanIOException
	 */
	BEAN read(Reader reader) throws BeanIOException;
	
	/**
	 * Write an instance of a bean of class BEAN to a file.
	 * 
	 * @param bean - the bean instance which to write to the file.
	 * @param file - the destination file. 
	 * @throws BeanIOException - wraps exceptions thrown internally by the implementation.
	 */
	void write(BEAN bean, File file) throws BeanIOException;
}
