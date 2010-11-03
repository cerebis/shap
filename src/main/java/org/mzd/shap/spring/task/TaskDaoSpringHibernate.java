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
package org.mzd.shap.spring.task;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.mzd.shap.spring.orm.BaseDaoSpringHibernate;
import org.springframework.orm.hibernate3.HibernateCallback;

public class TaskDaoSpringHibernate 
	extends BaseDaoSpringHibernate<Task, Integer> implements TaskDao {
	
	public TaskDaoSpringHibernate() {
		super(Task.class);
	}

	public List<Task> findNewByJob(final Job job, final int maxResults) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Task>>() {
			@SuppressWarnings("unchecked")
			public List<Task> doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(getPersistentClass())
					.add(Restrictions.eq("status",Status.NEW))
					.add(Restrictions.eq("job",job))
					.setMaxResults(maxResults)
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.list();
			}
		});
	}

	/**
	 * Advice class to pull specific concrete implementations of Task from the store.
	 * 
	 * @param derivedClass
	 * @param jobStatus
	 * @param maxResults
	 * @return
	 */
	protected List<Task> findNew(final Class<? extends Task> derivedClass, final Status jobStatus, final int maxResults) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Task>>() {
			@SuppressWarnings("unchecked")
			public List<Task> doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(derivedClass)
					.add(Restrictions.eq("status", Status.NEW))
					.createAlias("job", "job")
						.add(Restrictions.eq("job.status", jobStatus))
					.setMaxResults(maxResults)
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.addOrder(Order.asc("id"))
					.list();
			}
		});
	}
	
	public List<Task> findNew(Status jobStatus, final int maxResults) {
		List<Task> tasks = findNew(DetectionTask.class, jobStatus, maxResults);
		if (tasks.size() < maxResults) {
			tasks.addAll(findNew(AnnotationTask.class, jobStatus, maxResults - tasks.size()));
		}
		return tasks;
	}
	
	public List<Task> findNewInStartedJobs(int maxResults) {
		return findNew(Status.STARTED,maxResults);
	}
	
	protected long countNew(final Status jobStatus) {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.add(Restrictions.eq("status", Status.NEW))
					.createAlias("job","job")
						.add(Restrictions.eq("job.status", jobStatus))
					.setProjection(Projections.rowCount())
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.uniqueResult();
			}
		});
	}
	
	public long countNewInStartedJobs() {
		return countNew(Status.STARTED);
	}
	
	public long countNew() {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.add(Restrictions.eq("status", Status.NEW))
					.setProjection(Projections.rowCount())
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.uniqueResult();
			}
		});
	}
	
	protected long countIncomplete(final Status jobStatus) {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.add(Restrictions.disjunction()
							.add(Restrictions.eq("status", Status.NEW))
							.add(Restrictions.eq("status", Status.STARTED))
							.add(Restrictions.eq("status", Status.QUEUED)))
					.createAlias("job","job")
						.add(Restrictions.eq("job.status", jobStatus))
					.setProjection(Projections.rowCount())
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.uniqueResult();
			}
		});
	}

	public long countIncompleteInStartedJobs() {
		return countIncomplete(Status.STARTED);
	}
	
	public long countIncomplete() {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.add(Restrictions.disjunction()
							.add(Restrictions.eq("status", Status.NEW))
							.add(Restrictions.eq("status", Status.STARTED))
							.add(Restrictions.eq("status", Status.QUEUED)))
					.setProjection(Projections.rowCount())
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.uniqueResult();
			}
		});
	}

}
