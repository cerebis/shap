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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;

public class BeanIOXstream<BEAN> implements BeanIO<BEAN> {
	private Class<?>[] readerClasses;
	private XStream delegate;
	
	public BeanIOXstream(Class<?> ...readerClasses) {
		this.readerClasses = readerClasses;
		this.delegate = new XStream();
		this.delegate.processAnnotations(getReaderClasses());
	}

	protected synchronized XStream getDelegate() {
		return delegate;
	}
	
	@SuppressWarnings("unchecked")
	public BEAN read(File file) throws BeanIOException {
		try {
			return (BEAN)(getDelegate().fromXML(new FileReader(file)));
		} 
		catch (FileNotFoundException ex) {
			throw new BeanIOException(ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public BEAN read(Reader reader) throws BeanIOException {
		return (BEAN)(getDelegate().fromXML(reader));
	}
	
	public void write(BEAN bean, File file) throws BeanIOException {
		Writer writer = null;
		try {
			writer = new FileWriter(file);
			getDelegate().toXML(bean, writer);
		} 
		catch (IOException ex) {
			throw new BeanIOException(ex);
		}
		finally {
			try {
				if (writer != null) {
					writer.close();
				}
			}
			catch (IOException ex) {
				throw new BeanIOException(ex);
			}
		}
	}
	
	protected Class<?>[] getReaderClasses() {
		return readerClasses;
	}
}
