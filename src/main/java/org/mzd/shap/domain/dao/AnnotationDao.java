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

import java.util.List;

import org.mzd.shap.analysis.Annotator;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.AnnotationType;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.hibernate.BaseDao;

public interface AnnotationDao extends BaseDao<Annotation, Integer> {

	/**
	 * Find all annotations created by a specific Annotator.
	 * 
	 * @param annotator - the annotator responsible for this annotation.
	 * @return the list of annotations for the annotator.
	 */
	List<Annotation> findByAnnotator(Annotator annotator);
	
	List<Annotation> findByFullText(String queryText);
	
	Annotation findByAnnotatorAndFeature(Annotator annotator, Feature feature, AnnotationType type);

	List<Annotation> findByFeatureOrderedByAnnotator(Feature feature);

	List<Object[]> findPageRowsByFeature(Integer featureId, int firstResult, int maxResults, int sortFieldIndex, String sortDirection);

	Long countByFeature(Integer featureId);
	
}
