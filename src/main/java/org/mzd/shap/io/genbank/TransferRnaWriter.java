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
import java.util.Set;

import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.DataAccessException;
import org.mzd.shap.domain.Feature;


public class TransferRnaWriter extends FeatureWriter {
	
	public TransferRnaWriter(Writer writer) {
		super("tRNA",writer);
	}
	
	@Override
	protected void writeAllQualifiers(Feature entity) throws IOException, DataAccessException {
		
		writeQualifier("locus_tag", "feature:%d", entity.getId());

		Set<Annotation> annoSet = entity.getAnnotations();
		if (annoSet != null && annoSet.size() > 0) {
			Annotation a = annoSet.iterator().next();
			if (a.getDescription() != null) {
				writeQualifier("product","%s",a.getDescription());
			}
		}
	}

}
