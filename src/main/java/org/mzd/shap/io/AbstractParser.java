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
package org.mzd.shap.io;

public abstract class AbstractParser<ENTITY, ELEMENT, SOURCE> {
	private SOURCE source;
	private ELEMENT nextElem;
	
	abstract protected void next() throws ParserException;
	abstract public boolean hasMoreElements();
	
	abstract protected ENTITY parseInternal() throws Exception;
	
	protected ENTITY parse() throws ParserException {
		try {
			return parseInternal();
		}
		catch (Exception ex) {
			if (ex instanceof ParserException) {
				throw (ParserException)ex;
			}
			else {
				throw new ParserException(ex);
			}
		}
	}
	
	public ENTITY nextElement() throws ParserException {
		ENTITY entity = parse();
		next();
		return entity;
	}
	
	protected ELEMENT getNextElem() {
		return nextElem;
	}
	
	protected void setNextElement(ELEMENT nextElem) {
		this.nextElem = nextElem;
	}
	
	protected SOURCE getSource() {
		return source;
	}
	
	/**
	 * Set the source to the supplied instance. In doing so, the
	 * first element will be parsed from the source.
	 *  
	 * @param source - the source to parse.
	 * @throws ParserException - thrown when there is a problem parsing the first element.
	 */
	public void setSource(SOURCE source) throws ParserException {
		this.source = source;
		next();
	}
}
