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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.transform.Transformers;
import org.mzd.shap.domain.Project;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.authentication.User;
import org.mzd.shap.spring.orm.BaseDaoSpringHibernate;
import org.springframework.orm.hibernate3.HibernateCallback;


public class SequenceDaoSpringHibernate extends BaseDaoSpringHibernate<Sequence, Integer> implements SequenceDao {

	public SequenceDaoSpringHibernate() {
		super(Sequence.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<Sequence> findByFullText(String queryText, User user) {
		FullTextQuery query = findByFullText(queryText, new String[]{"id","name","description","taxonomy","coverage"});
		query.enableFullTextFilter("sequenceUser")
			.setParameter("value", user.getId());
		return query.list();
	}
	
	public List<Sequence> findUnprocessed(final Project project) {
		return findByCriteria(null,
				Restrictions.eq("project", project), 
				Restrictions.isEmpty("features"));
	}
	
	public List<Sequence> minimalFindAll() {
		return getHibernateTemplate().execute(new HibernateCallback<List<Sequence>>() {
			@SuppressWarnings("unchecked")
			public List<Sequence> doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(getPersistentClass())
					.setProjection(Projections.id())
					.setResultTransformer(Transformers.aliasToBean(getPersistentClass()))
					.list();
			}
		});
	}

	public Sequence findById(final Integer id, boolean minimal) {
		if (!minimal) {
			return getHibernateTemplate().execute(new HibernateCallback<Sequence>() {
				public Sequence doInHibernate(Session session) throws HibernateException, SQLException {
					return (Sequence)session.createCriteria(getPersistentClass())
						.add(Restrictions.idEq(id))
						.uniqueResult();
				}
			});
		}
		else {
			return getHibernateTemplate().execute(new HibernateCallback<Sequence>() {
				public Sequence doInHibernate(Session session) throws HibernateException, SQLException {
					return (Sequence)session.createQuery(
							"SELECT seq.id AS id, seq.version AS version " +
							"FROM Sequence AS seq " +
							"WHERE seq.id=:id")
							.setParameter("id", id)
							.setResultTransformer(Transformers.aliasToBean(Sequence.class))
							.uniqueResult();
				}
			});
		}
	}

	public List<Sequence> pageBySample(final int firstElement, final int maxResults, final Order order, final Sample sample) {
		
		return getHibernateTemplate().execute(new HibernateCallback<List<Sequence>>() {
			@SuppressWarnings("unchecked")
			public List<Sequence> doInHibernate(Session session) throws HibernateException, SQLException {

				Criteria crit =session.createCriteria(getPersistentClass())
					.add(Restrictions.eq("sample", sample))
					.setFirstResult(firstElement)
					.setMaxResults(maxResults);
				
				if (order != null) {
					crit.addOrder(order);
				}
				else {
					crit.addOrder(Order.asc("id"));
				}
				
				return crit.list();
			}
		});
	}
	
	public List<Object[]> searchPageRowsBySample(final int sampleId, final int firstResult, final int maxResults, 
			final String searchText, final String sortField, final String sortDirection) {
		
		throw new RuntimeException();
		
//		return getHibernateTemplate().executeFind(new HibernateCallback<List<Object[]>>() {
//			public List<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
//				try {
//					FullTextSession fts = Search.getFullTextSession(session);
//					fts.getSearchFactory().getAnalyzer(getPersistentClass());
//					
//					String[] fields = new String[]{"id","name","description_forSort","coverage","taxonomy"};
//					MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_29, fields, 
//							fts.getSearchFactory().getAnalyzer(getPersistentClass()));
//	
//					Query assocQ = new TermQuery(new Term("sample.id",Integer.toString(sampleId)));
//					Query mfQ = parser.parse(searchText);
//					
//					BooleanQuery bq = new BooleanQuery();
//					bq.add(assocQ, BooleanClause.Occur.MUST);
//					bq.add(mfQ, BooleanClause.Occur.MUST);
//					
//					List<Object[]> result = fts.createFullTextQuery(bq,getPersistentClass())
//											.setProjection(fields)
//											.list();
//					
//					org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(this.getClass());
//					int n=0;
//					for (Object o : result) {
//						logger.debug(++n + " " + o);
//					}
//					return result;
//				}
//				catch (ParseException ex) {
//					throw new HibernateException(ex);
//				}
//			}
//		});
	}
	
	public List<Object[]> findPageRowsBySample(final int sampleId, final int firstResult, final int maxResults, 
			final int sortFieldIndex, final String sortDirection) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Object[]>>() {
			@SuppressWarnings("unchecked")
			public List<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(
						" SELECT s.id AS id, s.name AS name, s.description AS desc, s.coverage AS cov, s.taxonomy AS tax, " +
						"   length(data.value) as seqlen, (SELECT count(*) from s.features) as featcount " +
						" FROM Sequence s " +
						"   INNER JOIN s.data as data " +
						" WHERE " +
						"   s.sample.id = :sampleId" + 
						" ORDER BY " + String.format("col_%d_0_ %s",sortFieldIndex,sortDirection))
						.setFirstResult(firstResult)
						.setMaxResults(maxResults)
						.setParameter("sampleId", sampleId)
						.list();
			}
		});
	}
	
	public Long countBySample(final Sample sample) {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.add(Restrictions.eq("sample", sample))
					.setProjection(Projections.rowCount())
					.uniqueResult();
			}
		});
	}

	public Sequence findBySampleAndId(Sample sample, Integer id) {
		return findUniqueByCriteria(
				Restrictions.eq("sample", sample),
				Restrictions.idEq(id));
	}
	
	public Sequence findBySampleAndName(final Sample sample, final String name, boolean minimal) {
		if (minimal) {
			return getHibernateTemplate().execute(new HibernateCallback<Sequence>() {
				public Sequence doInHibernate(Session session) throws HibernateException, SQLException {
					return (Sequence)session.createQuery(
							"SELECT seq.id AS id, seq.version AS version " +
							"FROM Sequence AS seq " +
							"  INNER JOIN seq.sample as smp " +
							"WHERE smp.id=:smpId " +
							"  AND seq.name=:name")
							.setParameter("smpId", sample.getId())
							.setParameter("name", name)
							.setResultTransformer(Transformers.aliasToBean(Sequence.class))
							.uniqueResult();
				}
			});
		}
		else {
			return findUniqueByCriteria(
					Restrictions.eq("sample", sample),
					Restrictions.eq("name", name));
		}
	}

	public List<Sequence> findBySample(Sample sample) {
		return findBySample(sample,false);
	}
	
	@SuppressWarnings("unchecked")
	public List<Sequence> findBySample(final Sample sample, boolean minimal) {
		if (minimal) {
			Object result =	getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					
					// Get a projection of just identifier and version stamp
					// We're resorting to HQL because the criteria API appears
					// broken for this use case. (Comments below)
					List<?> resultSet = session.createQuery(
							"SELECT id, version " +
							"FROM Sequence " +
							"WHERE sample=:sample")
							.setParameter("sample", sample)
							.list();
					
					// Now convert the result set into Sequence objects where
					// only the identifier and version stamp are initialized.
					List<Sequence> seqList = new ArrayList<Sequence>();
					for (Object obj : resultSet) {
						Object[] columns = (Object[])obj;
						Sequence seq = new Sequence();
						seq.setId((Integer)columns[0]);
						seq.setVersion((Integer)columns[1]);
						seqList.add(seq);
					}
					
					return seqList;
				}
			});
			return (List<Sequence>)result;
		}
		else {
			return findByCriteria(Order.asc("id"),Restrictions.eq("sample", sample));
		}
	}

	public List<Sequence> findByProject(final Project project) {
		return findByProject(project, false);
	}
	
	@SuppressWarnings("unchecked")
	public List<Sequence> findByProject(final Project project, final boolean minimal) {
		Object result =	getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				if (minimal) {

					List<?> resultSet = session.createQuery(
						"SELECT seq.id, seq.version " +
						"FROM Sequence as seq LEFT JOIN seq.sample as sample " +
						"WHERE sample.project=:project")
						.setParameter("project", project)
						.list();
					
					List<Sequence> seqList = new ArrayList<Sequence>();
					for (Object obj : resultSet) {
						Object[] columns = (Object[])obj;
						Sequence seq = new Sequence();
						seq.setId((Integer)columns[0]);
						seq.setVersion((Integer)columns[1]);
						seqList.add(seq);
					}
					
					return seqList;
				}
				else {
					return session.createCriteria(getPersistentClass())
						.createCriteria("sample")
							.add(Restrictions.eq("project", project))
						.list();
				}
			}
		});

		return (List<Sequence>)result;
	}
	
	public Sequence loadWithData(final Integer id) {
		return getHibernateTemplate().execute(new HibernateCallback<Sequence>() {
			public Sequence doInHibernate(Session session) throws HibernateException, SQLException {
				return (Sequence)session.createCriteria(getPersistentClass())
					.setFetchMode("data", FetchMode.JOIN)
					.add(Restrictions.idEq(id))
					.uniqueResult();
			}
		});
	}

	public List<Sequence> loadWithData(final List<Sequence> sequences) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Sequence>>() {
			@SuppressWarnings("unchecked")
			public List<Sequence> doInHibernate(Session session) throws HibernateException, SQLException {
				List<Integer> ids = new ArrayList<Integer>();
				for (Sequence s : sequences) {
					ids.add(s.getId());
				}
				return session.createCriteria(getPersistentClass())
					.setFetchMode("data", FetchMode.JOIN)
					.add(Restrictions.in("id", ids))
					.list();
			}
		});
	}

	/**
	 * Saves, flushes and clears session.
	 */
	public void batchSave(Collection<Sequence> sequences) {
		final int batchSize = 20;
		int n = 0;
		for (Sequence seq : sequences) {
			getHibernateTemplate().save(seq);
			if (n % batchSize == 0) {
				getHibernateTemplate().flush();
				getHibernateTemplate().clear();
			}
			n++;
		}
	}
	
//	public void updateNames(Sample sample, Map<String, String> nameMap) throws ApplicationException {
//		for (String oldName : nameMap.keySet()) {
//			Sequence seq = findByID(new SequenceId(sample.getId(),oldName));
//			if (seq == null) {
//				throw new ApplicationException("For sample [" + sample.getName() + 
//						"] no sequence found with name [" + oldName + "]");
//			}
//			String newName = nameMap.get(oldName);
//			//System.out.println("Would have updated: " + seq.getId() + " to " + newName);
//			seq.getId().setName(newName);
//			getHibernateTemplate().update(seq);
//		}
//	}
}
