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
package org.mzd.shap.analysis;

import java.io.File;
import java.util.Map;

import org.mzd.shap.io.Parser;


public interface AnalyzerParser<RESULT> extends Parser<Map<String,RESULT>, File> {

	/**
	 * Announces whether an implementation supports batching.
	 * <p>
	 * In this sense, batching means multiple input objects are correctly handled
	 * by the underlying {@link Analyzer}.
	 * 
	 * @return true if the implementation supports batching.
	 */
	public boolean supportsBatching();
}
