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

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.betwixt.expression.Context;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.strategy.DefaultObjectStringConverter;
import org.apache.commons.betwixt.strategy.TypeBindingStrategy;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

/**
 * Writing/reading beans of class BEAN to storage using the Betwixt library;
 * resulting files are in XML format.
 * 
 * Extend this class to perform IO on a concrete class of type BEAN.
 * 
 * The exceptions: SAXException, IntrospectionException and IOException are
 * wrapped by BeanIOException.
 * 
 * For each target class BEAN, the user can include a .betwixt configuration
 * file on the classpath to configure how the bean is written to XML.
 * 
 * eg. for a bean of class Person, one would include a file Person.betwixt.
 * 
 * @param <BEAN>
 */
public abstract class BeanIOBetwixt<BEAN> implements BeanIO<BEAN> {
	private Class<BEAN> readerClass;
	private boolean setMapIDs;
	private String[] localRegistrations;
	
	class EnumTypeBindingStrategy extends TypeBindingStrategy {
		@Override
		@SuppressWarnings("unchecked")
	    public TypeBindingStrategy.BindingType bindingType(Class type) {
	        TypeBindingStrategy.BindingType bindingType = null;
	        if(type.isEnum()) {
	            bindingType = TypeBindingStrategy.BindingType.PRIMITIVE;
	        } else {
	            bindingType = TypeBindingStrategy.DEFAULT.bindingType(type);
	        }
	        return bindingType;
	    }
	}

	class EnumObjectStringConverter extends DefaultObjectStringConverter {
		static final long serialVersionUID = -3089062213697155242L;
		
		@Override
		@SuppressWarnings("unchecked")
	    public String objectToString(Object object, Class type, Context context) {
	        String value = null;
	        if(object instanceof Enum) {
	            value = ((Enum)object).name();
	        } else {
	            value = super.objectToString(object, type, context);
	        }
	        return value;
	    }
	    
		@Override
		@SuppressWarnings("unchecked")
	    public Object stringToObject(String value, Class type, Context context) {
	        Object object = null;
	        if(type.isEnum()) {
	            object = Enum.valueOf(type, value);
	        } else {
	            object = super.stringToObject(value, type, context);
	        }
	        return object;
	    }
	}

	protected BeanIOBetwixt(Class<BEAN> readerClass) {
		this(readerClass,null,false);
	}
	
	protected BeanIOBetwixt(Class<BEAN> readerClass, String[] localRegistrations, boolean setMapIDs) {
		this.readerClass = readerClass;
		this.setMapIDs = setMapIDs;
		this.localRegistrations = localRegistrations;
	}
	
	/**
	 * Register a set of local DTDs in preference to leaving it up to the {@link EntityResolver} to
	 * find relevant references for us. The DTD tag in an XML document can result in resolution
	 * producing an HTTP request for every invocation of {@link #read(File)}.
	 * <p>
	 * Obviously the delay in an HTTP request will slow things down as well as possibly annoying
	 * the heck out of any servers which we're repeatingly hitting for the same document.
	 * 
	 * @param reader - the instance of reader which we're about to use.
	 */
	protected void registerLocalDtds(BeanReader reader) {
		if (getLocalRegistrations() != null) {
			for (int i=0; i<getLocalRegistrations().length; i+=2) {
				String url = getReaderClass().getResource(getLocalRegistrations()[i+1]).toString();
				if (url != null) {
					reader.register(getLocalRegistrations()[i], url);
				}
			}
		}
	}
	
	public BEAN read(Reader reader) throws BeanIOException {
		try {
			BeanReader beanReader = new BeanReader();
			registerLocalDtds(beanReader);
			beanReader.registerBeanClass(getReaderClass());
			beanReader.getBindingConfiguration().setMapIDs(isSetMapIDs());
			beanReader.getXMLIntrospector()
				.getConfiguration()
					.setTypeBindingStrategy(new EnumTypeBindingStrategy());
			beanReader.getBindingConfiguration()
				.setObjectStringConverter(new EnumObjectStringConverter());
			return getReaderClass().cast(beanReader.parse(reader));
		} 
		catch (IOException ex) {
			throw new BeanIOException(ex);
		}
		catch (SAXException ex) {
			throw new BeanIOException(ex);
		}
		catch (IntrospectionException ex) {
			throw new BeanIOException(ex);
		}
	}
	
	public BEAN read(File file) throws BeanIOException {
		try {
			BeanReader reader = new BeanReader();
			registerLocalDtds(reader);
			reader.registerBeanClass(getReaderClass());
			reader.getBindingConfiguration().setMapIDs(isSetMapIDs());
			reader.getXMLIntrospector()
				.getConfiguration()
					.setTypeBindingStrategy(new EnumTypeBindingStrategy());
			reader.getBindingConfiguration()
				.setObjectStringConverter(new EnumObjectStringConverter());
			return getReaderClass().cast(reader.parse(file));
		} 
		catch (IOException ex) {
			throw new BeanIOException(ex);
		}
		catch (SAXException ex) {
			throw new BeanIOException(ex);
		}
		catch (IntrospectionException ex) {
			throw new BeanIOException(ex);
		}
	}
	
	public void write(BEAN bean, File file) throws BeanIOException {
		
		BeanWriter writer = null;
		try {
			writer = new BeanWriter(new FileOutputStream(file));
			writer.getBindingConfiguration().setMapIDs(isSetMapIDs());
			writer.enablePrettyPrint();
			writer.getXMLIntrospector()
				.getConfiguration()
					.setTypeBindingStrategy(new EnumTypeBindingStrategy());
			writer.getBindingConfiguration()
				.setObjectStringConverter(new EnumObjectStringConverter());
			writer.write(bean);
			writer.close();
		}
		catch (IOException ex) {
			throw new BeanIOException(ex);
		}
		catch (SAXException ex) {
			throw new BeanIOException(ex);
		}
		catch (IntrospectionException ex) {
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
	
	protected Class<BEAN> getReaderClass() {
		return readerClass;
	}

	public boolean isSetMapIDs() {
		return setMapIDs;
	}

	protected String[] getLocalRegistrations() {
		return localRegistrations;
	}

	protected void setLocalRegistrations(String[] localRegistrations) {
		this.localRegistrations = localRegistrations;
	}
}
