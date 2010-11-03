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

import org.mzd.shap.domain.Project;
import org.mzd.shap.domain.authentication.User;
import org.mzd.shap.hibernate.BaseDao;

public interface ProjectDao extends BaseDao<Project, Integer> {

	Project findByUserAndId(User user, Integer id);
	
	List<Object[]> findPageRowsByUser(int userId, int firstResult, int maxResults, int sortFieldIndex, String sortDirection);
	
	List<Object[]> findAnyPageRows(int firstResult, int maxResults, int sortFieldIndex, String sortDirection);
	
	Long countByUser(Integer userId);

	List<Project> findByFullText(String queryText, User user);
	
}
