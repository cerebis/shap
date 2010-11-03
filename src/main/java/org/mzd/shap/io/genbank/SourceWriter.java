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
import org.mzd.shap.domain.Sequence;


public class SourceWriter extends EntityWriter<Sequence> {
	
	public SourceWriter(Writer writer) {
		super("source",writer);
	}
	
	@Override
	public void writeAllQualifiers(Sequence entity) throws IOException, DataAccessException {
		writeQualifier("db_xref","sequence:%s",entity.getId());
	}
	
	@Override
	protected void writeLocation(Sequence entity) throws IOException, DataAccessException {
		getWriter().write(
			String.format(getFwdFormat(), getKey(), 1, entity.getDataLength()));	
	}

}
