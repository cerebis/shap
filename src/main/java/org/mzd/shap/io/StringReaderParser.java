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

import java.io.IOException;
import java.io.LineNumberReader;

public abstract class StringReaderParser<ENTITY> extends AbstractParser<ENTITY, String, LineNumberReader> {

	@Override
	public boolean hasMoreElements() {
		return getNextElem() != null;
	}
	
	@Override
	protected void next() throws ParserException {
		try {
			setNextElement(getSource().readLine());
		}
		catch (IOException ex) {
			throw new ParserException("Exception while getting next line from reader");
		}
	}
	
}
