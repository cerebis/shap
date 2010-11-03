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

import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.mzd.shap.analysis.Annotator;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.AnnotationType;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.spring.orm.BaseDaoSpringHibernate;
import org.springframework.orm.hibernate3.HibernateCallback;

public class AnnotationDaoSpringHibernate extends BaseDaoSpringHibernate<Annotation, Integer> implements AnnotationDao {

	public AnnotationDaoSpringHibernate() {
		super(Annotation.class);
	}
	
	public List<Annotation> findByFullText(String queryText) {
		return null;
//		return findByFullText(queryText, new String[]{"id","accession","description","confidence","refersTo",
//				"alignment.queryStart","alignment.queryEnd","alignment.subjectStart","alignment.subjectEnd",
//				"alignment.querySeq","alignment.subjectSeq","alignment.consensusSeq"});
	}
	
	public List<Annotation> findByAnnotator(Annotator annotator) {
		return findByCriteria(Order.asc("id"), Restrictions.eq("annotator", annotator));
	}
	
	public Annotation findByAnnotatorAndFeature(Annotator annotator, Feature feature, AnnotationType type) {
		return findUniqueByCriteria(
				Restrictions.eq("annotator", annotator),
				Restrictions.eq("feature", feature),
				Restrictions.eq("refersTo", type));
	}
	
	@SuppressWarnings("unchecked")
	public List<Annotation> findByFeatureOrderedByAnnotator(final Feature feature) {
		
		Object result = getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(getPersistentClass())
					.add(Restrictions.eq("feature", feature))
					.setFetchMode("annotator", FetchMode.JOIN)
					.createCriteria("annotator")
						.addOrder(Order.asc("name"))
					.list();
			}
		});
		
		return (List<Annotation>)result;
	}
	
	public Long countByFeature(final Integer featureId) {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.createAlias("feature", "feat")
						.add(Restrictions.eq("feat.id", featureId))
					.setProjection(Projections.rowCount())
					.uniqueResult();
			}
		});
	}
	
	public List<Object[]> findPageRowsByFeature(final Integer featureId, final int firstResult, final int maxResults,
			final int sortFieldIndex, final String sortDirection) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Object[]>>() {
			@SuppressWarnings("unchecked")
			public List<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
				return (List<Object[]>)session.createQuery(
						" SELECT a.accession, a.description, a.confidence, an.name" +
						" FROM Annotation a " +
						"   INNER JOIN a.feature f " +
						"   INNER JOIN a.annotator an " +
						" WHERE " +
						"  f.id = :featureId " +
						" ORDER BY " + String.format("col_%d_0_ %s",sortFieldIndex,sortDirection))
					.setFirstResult(firstResult)
					.setMaxResults(maxResults)
					.setParameter("featureId", featureId)
					.list();
			}
		});
	}
}
