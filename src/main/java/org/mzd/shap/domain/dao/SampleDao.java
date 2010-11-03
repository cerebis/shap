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
package org.mzd.shap.domain.dao;

import java.util.Collection;
import java.util.List;

import org.mzd.shap.analysis.Annotator;
import org.mzd.shap.domain.Project;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Taxonomy;
import org.mzd.shap.domain.authentication.User;
import org.mzd.shap.hibernate.BaseDao;
import org.mzd.shap.spring.io.AnnotationHistogramDTO;

public interface SampleDao extends BaseDao<Sample, Integer> {

	Sample findByProjectAndName(Project project, String name);
	
	Sample findByProjectAndId(Project project, Integer id);
	
	List<AnnotationHistogramDTO> annotationHistogram(
			Sample sample, Annotator annotator, Double confidence, Collection<Taxonomy> excludedTaxons);
	
	List<Sample> findAllByProject(Project project);
	
	List<Object[]> findPageRowsByProject(int projectId, int firstResult, int maxResults, int sortFieldIndex, String sortDirection);
	
	Long countByProject(Integer projectId);
	
	List<Sample> findByFullText(String queryText, User user);
}
