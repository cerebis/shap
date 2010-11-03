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

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.mzd.shap.domain.Project;
import org.mzd.shap.domain.authentication.User;
import org.mzd.shap.spring.orm.BaseDaoSpringHibernate;
import org.springframework.orm.hibernate3.HibernateCallback;


public class ProjectDaoSpringHibernate extends BaseDaoSpringHibernate<Project, Integer> implements ProjectDao {

	public ProjectDaoSpringHibernate() {
		super(Project.class);
	}
	
	public List<Project> findByFullText(String queryText, User user) {
		FullTextQuery query = findByFullText(queryText, new String[]{"id","name","description"});
		query.enableFullTextFilter("projectUser")
			.setParameter("value", user.getId());
		return query.list();
	}
	
	public Project findByUserAndId(final User user, final Integer id) {
		return getHibernateTemplate().execute(new HibernateCallback<Project>() {
			public Project doInHibernate(Session session) throws HibernateException, SQLException {
				return (Project)session.createCriteria(getPersistentClass())
					.addOrder(Order.asc("id"))
					.add(Restrictions.idEq(id))
					.createCriteria("users")
						.add(Restrictions.idEq(user.getId()))
					.uniqueResult();
			}
		});
	}

	public List<Object[]> findPageRowsByUser(final int userId, final int firstResult,
			final int maxResults, final int sortFieldIndex, final String sortDirection) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Object[]>>() {
			@SuppressWarnings("unchecked")
			public List<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(
						" SELECT p.id, p.name, p.description, p.creation, " +
						"   (SELECT count(*) from p.samples) " +
						" FROM Project p " +
						"  INNER JOIN p.users as user " +
						" WHERE " +
						"   user.id = :userId" + 
						" ORDER BY " + String.format("col_%d_0_ %s",sortFieldIndex,sortDirection))
						.setFirstResult(firstResult)
						.setMaxResults(maxResults)
						.setParameter("userId", userId)
						.list();
			}
		});
	}
	
	public List<Object[]> findAnyPageRows(final int firstResult, final int maxResults, 
			final int sortFieldIndex, final String sortDirection) {
		
		return getHibernateTemplate().execute(new HibernateCallback<List<Object[]>>() {
			@SuppressWarnings("unchecked")
			public List<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(
						" SELECT p.id, p.name, p.description, p.creation, " +
						"   (SELECT count(*) from p.samples) " +
						" FROM Project p " +
						" ORDER BY " + String.format("col_%d_0_ %s",sortFieldIndex,sortDirection))
						.setFirstResult(firstResult)
						.setMaxResults(maxResults)
						.list();
			}
		});
	}

	public Long countByUser(final Integer userId) {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.createAlias("users", "user")
						.add(Restrictions.eq("user.id", userId))
					.setProjection(Projections.rowCount())
					.uniqueResult();
			}
		});
	}
	
}
