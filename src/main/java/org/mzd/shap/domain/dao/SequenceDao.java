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
import org.mzd.shap.domain.Project;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.authentication.User;
import org.mzd.shap.hibernate.BaseDao;


public interface SequenceDao extends BaseDao<Sequence, Integer> {

	/**
	 * Find all sequences in project each of which have no associated list of Features.
	 * This indicates that the sequence has never been run against a detector.
	 * 
	 * @param project - the project to search.
	 * @return the list of unprocessed sequences.
	 */
	List<Sequence> findUnprocessed(Project project);
	
	List<Sequence> minimalFindAll();
	
	Sequence findById(Integer id, boolean minimal);
	
	List<Sequence> pageBySample(int firstElement, int maxResults, Order order, Sample sample);
	
	List<Object[]> searchPageRowsBySample(int sampleId, int firstResult, int maxResults, 
			String searchText, String sortField, String sortDirection);
	
	List<Object[]> findPageRowsBySample(int sampleId, int firstResult, int maxResults, int sortFieldIndex, String sortDirection);
	
	Long countBySample(Sample sample);

	Sequence findBySampleAndId(Sample sample, Integer id);
	
	Sequence findBySampleAndName(Sample sample, String name, boolean minimal);

	List<Sequence> findBySample(Sample sample);
	
	List<Sequence> findBySample(Sample sample, boolean minimal);
	
	List<Sequence> findByProject(Project project);
	
	List<Sequence> findByProject(Project project, boolean minimal);
	
	Sequence loadWithData(Integer id);
	
	List<Sequence> loadWithData(List<Sequence> sequences);
	
	void batchSave(Collection<Sequence> sequences);
	
	List<Sequence> findByFullText(String queryText, User user);

}
