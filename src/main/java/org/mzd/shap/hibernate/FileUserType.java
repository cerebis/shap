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

import java.io.File;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * Store a {@link java.io.File} instance in the same manager as Strings are stored.
 * <p>
 * The string representing the instance of File is obtained from {@link java.io.File#getPath()}
 * <p>
 * A user can specify a length in the mapping document to use database types capable of
 * storing long path names.
 * 
 */
public class FileUserType implements UserType {
	private final static int SQL_TYPE = Types.VARCHAR;
	private final static int[] SQL_TYPES = {SQL_TYPE}; 
	private final static Class<?> RETURNED_CLASS = File.class;
	
	public FileUserType() {/*...*/}
	
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
        
        File fx = (File)x;
        File fy = (File)y;
        
        return fx.equals(fy); 
    } 

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public boolean isMutable() {
		return false;
	}

    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner) throws HibernateException, SQLException { 
        File result = null; 
        String fileAsStr = resultSet.getString(names[0]); 
        if (!resultSet.wasNull()) {
            result = new File(fileAsStr); 
        } 
        return result; 
    } 

    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index) throws HibernateException, SQLException { 
        if (value == null) { 
            preparedStatement.setNull(index, SQL_TYPE); 
        } else { 
        	String fileAsStr = ((File)value).getPath();
            preparedStatement.setString(index, fileAsStr); 
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
