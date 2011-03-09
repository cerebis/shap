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

import java.util.List;
import java.util.Map;

import org.mzd.shap.analysis.Annotator;
import org.mzd.shap.analysis.Detector;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Project;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.authentication.User;
import org.mzd.shap.domain.dao.FeatureDaoSpringHibernate.FeatureBreakdownDTO;
import org.mzd.shap.hibernate.search.SearchResult;
import org.mzd.shap.hibernate.search.view.Report;

public interface DataViewService {
	
	Object getObject(Integer id, User user);
	
	SearchResult<Report> getReports(String queryText, int firstResult, int maxResults);
	
	Map<String,List<?>> getFullTextResult(String queryText, User user);
	
	Detector getDetector(String detectorName) throws NotFoundException;
	Annotator getAnnotator(String annotatorName) throws NotFoundException;
	
	Project getProject(User user, Integer projectId) throws NotFoundException;
	Project getProject(Integer projectId) throws NotFoundException;
	Project getProject(String projectName) throws NotFoundException;
	List<Object[]> getProjectTable(int firstResult, int maxResults, int sortFieldIndex,  String sortDirection) throws NotFoundException;
	Long getProjectCount(Integer userId);
	
	Sample getSample(Project project, Integer sampleId) throws NotFoundException;
	Sample getSample(Integer sampleId) throws NotFoundException;
	Sample getSample(Project project, String sampleName) throws NotFoundException;
	Sample getSample(String projectName, String sampleName) throws NotFoundException;
	List<Object[]> getSampleTable(Integer projectId, int firstResult, int maxResults, int sortFieldIndex, String sortDirection) throws NotFoundException;
	Long getSampleCount(Integer projectId);

	Sequence getSequence(Sample sample, Integer sequenceId) throws NotFoundException;
	Sequence getSequence(Integer sequenceId) throws NotFoundException;
	Sequence getSequence(Sample sample, String sequenceName) throws NotFoundException;
	Sequence getSequence(String projectName, String sampleName, String sequenceName) throws NotFoundException;
	List<Object[]> getSequenceTable(Integer sampleId, int firstResult, int maxResults, int sortFieldIndex, String sortDirection) throws NotFoundException;
	Long getSequenceCount(Integer sampelId);
	List<Sequence> getSequences(List<Integer> sequenceIds) throws NotFoundException;
	
	Feature getFeature(Sequence sequence, Integer featureId) throws NotFoundException;
	Feature getFeature(Integer featureId) throws NotFoundException;
	List<Object[]> getFeatureTable(Integer sequenceId, int firstResult, int maxResults, int sortFieldIndex, String sortDirection) throws NotFoundException;
	Long getFeatureCount(Integer sequenceId);
	List<FeatureBreakdownDTO> getFeatureBreakdown(Integer featureId);
	List<Feature> getFeatures(List<Integer> featureIds) throws NotFoundException;
	
	List<Object[]> getAnnotationTable(Integer featureId, int firstResult, int maxResults, int sortFieldIndex, String sortDirection) throws NotFoundException;
	Long getAnnotationCount(Integer featureId);
}