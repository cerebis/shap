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

import org.mzd.shap.exec.Executable;

/**
 * Base interface for performing analyses on an object of type {@link TARGET},
 * the result of which is returned as type {@link RESULT}.
 *  
 */
public interface Analyzer<TARGET,RESULT> {
	
	/**
	 * Analyze a given target object. The supplied {@link Executable} provides
	 * the means to execute the analysis as an external thread. This is most
	 * often the case with binary tools like Blastall, Hmmpfam, etc. 
	 * 
	 * @param exec a concrete instance of {@link Executable}
	 * @param target an instance of the target class type {@link TARGET}
	 * @return the result of analysis as the class type {@link RESULT}
	 * @throws AnalyzerException other checked exceptions are wrapped.
	 */
	RESULT[] analyze(Executable exec, TARGET... target) throws AnalyzerException;
	
	/**
	 * Identifier used in persistent storage.
	 */
	Integer getId();
	void setId(Integer id);

	/**
	 * Unique name for this Analyzer.
	 */
	String getName();
	void setName(String name);

	String getDescription();
	void setDescription(String description);
	
	/**
	 * Batching Support - Analyzer can operate on multiple objects in a single call.
	 * @return
	 */
	boolean supportsBatching();
	Integer getBatchSize();
	void setBatchSize(Integer batchSize);

}
