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
package org.mzd.shap.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * Persist an instance of {@link java.util.Date} as the time in milliseconds 
 * since epoc. This is mapped as a SQL BIGINT.
 *
 */
public class EpocTimeUserType implements UserType {
	private final static int SQL_TYPE = Types.BIGINT;
	private final static int[] SQL_TYPES = {SQL_TYPE};
	private final static Class<?> RETURNED_CLASS = Date.class;

	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		 return (Serializable)value;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
	       if (x == y) { 
	            return true;
	        }
	        if (null == x || null == y) { 
	            return false; 
	        }
	        
	        Date dx = (Date)x;
	        Date dy = (Date)y;
	        
	        return dx.equals(dy); 
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public boolean isMutable() {
		return false;
	}

    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner) throws HibernateException, SQLException {
    	Long epoc = (Long)Hibernate.LONG.nullSafeGet(resultSet,names[0]);
    	if (epoc == null) {
    		return null;
    	}
    	return new Date(epoc);
    } 

    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index) throws HibernateException, SQLException {
    	if (value == null) {
    		Hibernate.LONG.nullSafeSet(preparedStatement, null, index);
    	}
    	else {
    		Date date = (Date)value;
    		Hibernate.LONG.nullSafeSet(preparedStatement, date.getTime(), index);
    	}
    } 

    public Object replace(Object original, Object target, Object owner) throws HibernateException { 
        return original; 
    } 

	public Class<?> returnedClass() {
		return RETURNED_CLASS;
	}

	public int[] sqlTypes() {
		return SQL_TYPES;
	}

}
