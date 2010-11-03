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

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.mzd.shap.spring.orm.BaseDaoSpringHibernate;
import org.springframework.orm.hibernate3.HibernateCallback;


public class JobDaoSpringHibernate extends BaseDaoSpringHibernate<Job, Integer> implements JobDao {
	private static Conjunction endPointConjunction;
	static {
		endPointConjunction = new Conjunction();
		for (Status s : Status.values()) {
			if (s.isEndPointState()) {
				endPointConjunction.add(Restrictions.eq("status",s));
			}
		}
		
	}
	
	public JobDaoSpringHibernate() {
		super(Job.class);
	}
	
	public long countIncomplete() {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.add(Restrictions.disjunction()
							.add(Restrictions.eq("status", Status.NEW))
							.add(Restrictions.eq("status", Status.STARTED)))
					.setProjection(Projections.rowCount())
					.uniqueResult();
			}
		});
	}

	public long countIncompleteTasks(final Integer jobId) {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.add(Restrictions.idEq(jobId))
					.setProjection(Projections.rowCount())
					.createCriteria("tasks")
						.add(Restrictions.disjunction()
								.add(Restrictions.eq("status", Status.NEW))
								.add(Restrictions.eq("status", Status.QUEUED))
								.add(Restrictions.eq("status", Status.STARTED)))
					.uniqueResult();
			}
		});
	}
	
	public long countIncompleteTasks(final Job job) {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.add(Restrictions.idEq(job.getId()))
					.setProjection(Projections.rowCount())
					.createCriteria("tasks")
						.add(Restrictions.disjunction()
								.add(Restrictions.eq("status", Status.NEW))
								.add(Restrictions.eq("status", Status.QUEUED))
								.add(Restrictions.eq("status", Status.STARTED)))
					.uniqueResult();
			}
		});
	}
	
	public List<Job> findStarted() {
		return findByCriteria(Order.asc("id"), 
				Restrictions.eq("status", Status.STARTED));
	}
	
	public Job findNextNew() {
		return getHibernateTemplate().execute(new HibernateCallback<Job>() {
			public Job doInHibernate(Session session) throws HibernateException, SQLException {
				return (Job)session.createCriteria(getPersistentClass())
					.add(Restrictions.eq("status",Status.NEW))
					.addOrder(Order.asc("id"))
					.setMaxResults(1)
					.uniqueResult();
			}
		});
	}

	public Job findUnfinishedById(final Integer jobId) {
		return getHibernateTemplate().execute(new HibernateCallback<Job>() {
			public Job doInHibernate(Session session) throws HibernateException, SQLException {
				return (Job)session.createCriteria(getPersistentClass())
					.add(Restrictions.idEq(jobId))
					.add(Restrictions.conjunction()
						.add(Restrictions.ne("status", Status.DONE))
						.add(Restrictions.ne("status", Status.ERROR)))
					.uniqueResult();
			}
		});
	}
}
