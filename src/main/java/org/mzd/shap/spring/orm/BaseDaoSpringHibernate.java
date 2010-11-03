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
package org.mzd.shap.spring.orm;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.util.Version;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.mzd.shap.hibernate.BaseDao;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class BaseDaoSpringHibernate<ENTITY, ID extends Serializable>
		extends HibernateDaoSupport implements BaseDao<ENTITY, ID> {
	
	private Class<ENTITY> persistentClass;

	protected BaseDaoSpringHibernate(Class<ENTITY> persistentClass) {
		this.persistentClass = persistentClass;
	}
	
	public Class<ENTITY> getPersistentClass() {
		return this.persistentClass;
	}
	
	protected FullTextQuery findByFullText(final String queryText, final String[] fields) {
		return getHibernateTemplate().execute(new HibernateCallback<FullTextQuery>() {
			public FullTextQuery doInHibernate(Session session) throws HibernateException, SQLException {
				FullTextSession fts = Search.getFullTextSession(session);
				try {
					return fts.createFullTextQuery(
							new MultiFieldQueryParser(
								Version.LUCENE_29, 
								fields,	
								fts.getSearchFactory().getAnalyzer(getPersistentClass()))
									.parse(queryText), 
							getPersistentClass());
				} catch (ParseException ex) {
					throw new HibernateException(ex);
				}
			}
		});
	}

	public Long countAll() {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.setProjection(Projections.rowCount())
					.uniqueResult();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<ENTITY> pageAll(final Order order, final int pageNumber, final int pageSize) {
		return (List<ENTITY>) getHibernateTemplate().execute(new HibernateCallback<List<?>>() {
			public List<?> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria crit = session.createCriteria(getPersistentClass())
					.setFirstResult(pageSize*pageNumber)
					.setMaxResults(pageSize);
				
				if (order != null) {
					crit.addOrder(order);
				}
				
				return crit.list();
			}
		});
	}

	public ENTITY findByID(ID id) {
		ENTITY entity = (ENTITY)getHibernateTemplate().get(getPersistentClass(), id);
		if (entity == null) {
			throw new ObjectNotFoundException(id,"id not found");
		}
		return entity;
	}

	public ENTITY findByField(String field, Object value) {
		return findUniqueByCriteria(Restrictions.eq(field,value));
	}
	
	public List<ENTITY> findAll(Order order) {
		return findByCriteria(order);
	}
	
	public List<ENTITY> findAllByField(String field, Object value, Order order) {
		return findByCriteria(order,Restrictions.eq(field,value));
	}
	
	public ENTITY makePersistent(ENTITY entity) {
		getHibernateTemplate().saveOrUpdate(entity);
		return entity;
	}
	
	public void saveOrUpdateAll(Collection<?> entities) {
		getHibernateTemplate().saveOrUpdateAll(entities);
	}
	
	public void makeTransient(ENTITY entity) {
		getHibernateTemplate().delete(entity);
	}
	
	public ENTITY reattach(ENTITY entity) {
		return (ENTITY)getHibernateTemplate().merge(entity);
	}
	
	public void flush() {
		getHibernateTemplate().flush();
	}
	
	public void evict(ENTITY entity) {
		getHibernateTemplate().evict(entity);
	}
	
	public void refresh(ENTITY entity) {
		getHibernateTemplate().refresh(entity, LockMode.UPGRADE);
	}

	@SuppressWarnings("unchecked")
	protected List<ENTITY> findByCriteria(final Order order, final Criterion[] criterion, final Map<String,Criterion[]> assocCriterion) {
		return (List<ENTITY>)getHibernateTemplate().execute(new HibernateCallback<List<?>>() {
    		public List<?> doInHibernate(Session session) {
    			Criteria crit = session.createCriteria(getPersistentClass());
    			for (Criterion c : criterion) {
    				crit.add(c);
    			}
    			if (order != null) {
    				crit.addOrder(order);
    			}
    			for (String assocProperty : assocCriterion.keySet()) {
    				Criteria acrit = crit.createCriteria(assocProperty);
    				for (Criterion c : assocCriterion.get(assocProperty)) {
    					acrit.add(c);
    				}
    			}
    			return crit.list();
    		}
       	});
    }
	
	/**
     * Use these inside subclasses as a convenience method.
     */
	
	/**
	 * Find lists of objects by way of criterion filtering. This is
	 * meant to be used internally by extending classes, who should
	 * be providing more specific functions.
	 * 
	 * @param order the order in which to return the list of objects.
	 * @param criterion a vararg array of criterions on which to filter the result.
	 * @return the list of entities.
	 */
    @SuppressWarnings("unchecked")
	protected List<ENTITY> findByCriteria(final Order order, final Criterion... criterion) {
    	return (List<ENTITY>) getHibernateTemplate().execute(new HibernateCallback<List<?>>() {
    		public List<?> doInHibernate(Session session) {
    	        Criteria crit = session.createCriteria(getPersistentClass());
    	        for (Criterion c : criterion) {
    	            crit.add(c);
    	        }
    	        if (order != null) {
    	        	crit.addOrder(order);
    	        }
    	        return crit.list();
    		}
    	});
    }
    
    /**
     * Find a single object by way of criterion filtering. This effectively
     * load(id,class) but with additional constraints possible. Provided
     * mainly so association constraints can easily be applied. 
     * 
     * This is meant to be used internally by extending classes, who should
	 * be providing more specific functions.
	 * 
     * @param criterion a vararg array of criterions on which to filter the result.
     * @return the entity in question
     * @throws IncorrectResultSizeDataAccessException (unchecked) when there is more than one result found.
     */
    protected ENTITY findUniqueByCriteria(final Criterion... criterion) {
    	return getHibernateTemplate().execute(new HibernateCallback<ENTITY>() {
    		@SuppressWarnings("unchecked")
			public ENTITY doInHibernate(Session session) {
   				Criteria crit = session.createCriteria(getPersistentClass());
   	        	for (Criterion c : criterion) {
   	            	crit.add(c);
   	        	}
   	        	return (ENTITY)crit.uniqueResult();
    		}
    	});
    }
    
    protected List<?> projectionByCriteria(final Order order, final Projection projection, final Criterion... criterion) {
    	return getHibernateTemplate().executeFind(new HibernateCallback<List<?>>() {
    		public List<?> doInHibernate(Session session) throws HibernateException, SQLException {
    			Criteria crit = session.createCriteria(getPersistentClass());
    			for (Criterion c : criterion) {
    				crit.add(c);
    			}
    			crit.setProjection(projection);
    			if (order != null) {
    				crit.addOrder(order);
    			}
    			return crit.list();
    		}
    	});
    }
    
	@SuppressWarnings("unchecked")
	public List<ENTITY> findPage(final Order order, final int pageNumber, final int pageSize, final Criterion... filters) {
		return (List<ENTITY>) getHibernateTemplate().executeFind(new HibernateCallback<List<?>>() {
			public List<?> doInHibernate(Session session) {
				Criteria crit = session.createCriteria(getPersistentClass())
					.setFirstResult(pageNumber * pageSize)
					.setMaxResults(pageSize);
				if (order != null) {
					crit.addOrder(order);
				}
    			for (Criterion c : filters) {
    				crit.add(c);
    			}
				return crit.list();
			};
		});
	}

}
