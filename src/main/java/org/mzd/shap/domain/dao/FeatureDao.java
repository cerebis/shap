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

import org.hibernate.criterion.Order;
import org.mzd.shap.analysis.Annotator;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.FeatureException;
import org.mzd.shap.domain.FeatureType;
import org.mzd.shap.domain.Project;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.Taxonomy;
import org.mzd.shap.domain.authentication.User;
import org.mzd.shap.domain.dao.FeatureDaoSpringHibernate.FeatureBreakdownDTO;
import org.mzd.shap.hibernate.BaseDao;

public interface FeatureDao extends BaseDao<Feature, Integer> {

	List<Feature> findByFullText(String queryText, User user);

	Long countBySample(Sample sample);
	
	Long countBySample(Sample sample, FeatureType type, Collection<Taxonomy> excludedTaxa);
	
	Long countBySampleAndType(Sample sample, FeatureType type);
	
	Long countBySequenceAndType(Sequence sequence, FeatureType type);
	
	Long countBySequence(Sequence sequence);
	
	Long countBySequence(Integer sequenceId);
	
	List<Feature> findBySequence(Sequence sequence, Order... order);
	
	List<Feature> findBySequenceAndType(Sequence sequence, FeatureType type, boolean minimal);
	
	Feature findBySequenceAndLocus(Sequence sequence, String locusTag, boolean minimal) throws FeatureException;
	
	List<Feature> findMarkers(final Order order, final String[] markerNames);

	Feature findByOldId(Integer oldId);
	
	List<Feature> findByProjectAndType(Project project, FeatureType type, boolean minimal);
	
	List<Feature> findByProjectAndType(Project project, FeatureType type);
	
	List<Feature> findBySampleAndType(Sample sample, FeatureType type, boolean minimal);
	
	Feature findBySequenceAndId(Sequence sequence, Integer id);
	
	List<Feature> findUnprocessed(Sequence sequence);
	
	Feature loadWithData(Integer id);
	
	List<Feature> loadWithData(List<Feature> features);
	
	List<Feature> pageBySample(int firstElement, int maxResults, Sample sample);
	
	List<Feature> pageBySample(int firstElement, int maxResults, Sample sample, FeatureType type);
	
	List<Feature> pageBySample(int firstElement, int maxResults, Sample sample, FeatureType type, Collection<Taxonomy> excludedTaxa);
	
	List<Feature> pageBySequence(int firstElement, int maxResults, Sequence sequence, Order... order);
	
	List<Feature> pageBySequenceAndType(int firstElement, int maxResults, Sequence sequence, FeatureType type);
	
	void saveIfNew(Sequence sequence, Feature feature);

	List<Feature> findBySampleAndAnnotator(Sample sample, Annotator annotator);
	
	List<Feature> findOnlyNew(Collection<Feature> features);
	
	List<Feature> findResolvedSet(Sequence sequence, FeatureType restrictedToType);
	
	List<Object[]> findPageRowsBySequence(int sequenceId, int firstResult, int maxResults, int sortFieldIndex, String sortDirection);
	
	List<FeatureBreakdownDTO> findTypeBreakdown(Integer sequenceId);
}
