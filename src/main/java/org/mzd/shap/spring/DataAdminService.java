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
package org.mzd.shap.spring;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.mzd.shap.domain.DuplicateException;
import org.mzd.shap.domain.Project;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.io.bean.BeanIOException;

public interface DataAdminService extends DataViewService {

	/**
	 * Add a new project.
	 * 
	 * @param projectName - the unique project name
	 * @param description - a description for the project
	 * @return persistent entity
	 * @throws DuplicateException
	 */
	Project addProject(String projectName, String description) throws DuplicateException;
	
	/**
	 * Add a new sample to an existing project.
	 * 
	 * @param projectName
	 * @param sampleName
	 * @param description
	 * @return
	 * @throws DuplicateException
	 * @throws NotFoundException
	 */
	Sample addSample(String projectName, String sampleName, String description) throws DuplicateException, NotFoundException;
	
	/**
	 * 
	 * @param projectName
	 * @param sampleName
	 * @param sequenceName
	 * @param description
	 * @return
	 * @throws NotFoundException
	 * @throws DuplicateException 
	 * @throws IOException
	 */
	Sequence addSequence(String projectName, String sampleName, String sequenceName, String description) throws NotFoundException, DuplicateException;

	/**
	 * For a given project and sample, add more DNA sequences from a fasta file.
	 * 
	 * @param projectName
	 * @param sampleName
	 * @param fastaFile
	 * @throws NotFoundException
	 * @throws IOException
	 */
	void addSequences(String projectName, String sampleName, File fastaFile) throws NotFoundException, IOException;

	/**
	 * Add features defined by XML serialized Sequence object.
	 * 
	 * @param projectName
	 * @param sampleName
	 * @param xmlFile
	 * @throws NotFoundException
	 * @throws IOException
	 * @throws BeanIOException 
	 */
	void addFeatures(String projectName, String sampleName, File xmlFile) throws NotFoundException, IOException, BeanIOException;
	
	/**
	 * Remove a list of features referenced by featureId.
	 * 
	 * @param featureIds
	 * @throws NotFoundException
	 */
	void removeFeatures(List<Integer> featureIds) throws NotFoundException;

	/**
	 * Remove a list of sequences referenced by sequenceId.
	 * 
	 * @param sequenceIds
	 * @throws NotFoundException
	 */
	void removeSequences(List<Integer> sequenceIds) throws NotFoundException;

	/**
 	 * Set coverage values for existing sequences.
 	 * 
	 * @param projectName
	 * @param sampleName
	 * @param coverageMap
	 * @throws IOException
	 * @throws NotFoundException
	 */
	void setCoverage(String projectName, String sampleName, Map<String,Double> coverageMap) throws IOException, NotFoundException;
	
}