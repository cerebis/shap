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
package org.mzd.shap.io.genbank;

import java.io.IOException;
import java.io.Writer;

import org.mzd.shap.domain.DataAccessException;


public abstract class EntityWriter<ENTITY> {
	private final static String fwdFormat = "     %-15s %d..%d\n";
	private final static String revFormat = "     %-15s complement(%d..%d)\n";
	private String key;
	private Writer writer;
	
	public void write(ENTITY entity) throws IOException, DataAccessException {
		writeLocation(entity);
		writeAllQualifiers(entity);
	}
	
	protected abstract void writeAllQualifiers(ENTITY entity) throws IOException, DataAccessException;
	
	protected abstract void writeLocation(ENTITY entity) throws IOException, DataAccessException;

	protected void writeQualifier(String name, String format, Object... args) throws IOException {
		String s = String.format("/%s=\"%s\"", name, String.format(format,args));
		for (int i=0; i<s.length(); i+=60) {
			getWriter().append("                     ");
			int end = i+60 < s.length() ? i+60 : s.length();
			getWriter().append(s.substring(i,end) + "\n");
		}
	}

	protected String escapeQuotes(String unescaped) {
		return unescaped.replaceAll("\"", "\"\"");
	}

	protected EntityWriter(String key, Writer writer) {
		this.key = key;
		this.writer = writer;
	}

	protected static String getFwdFormat() {
		return fwdFormat;
	}

	protected static String getRevFormat() {
		return revFormat;
	}

	protected Writer getWriter() {
		return writer;
	}

	protected String getKey() {
		return key;
	}
	
}
