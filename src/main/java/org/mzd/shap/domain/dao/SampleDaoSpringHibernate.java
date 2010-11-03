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
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.CustomType;
import org.mzd.shap.analysis.Annotator;
import org.mzd.shap.domain.Project;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Taxonomy;
import org.mzd.shap.domain.authentication.User;
import org.mzd.shap.hibernate.TaxonomyUserType;
import org.mzd.shap.spring.io.AnnotationHistogramDTO;
import org.mzd.shap.spring.orm.BaseDaoSpringHibernate;
import org.springframework.orm.hibernate3.HibernateCallback;


public class SampleDaoSpringHibernate 
	extends BaseDaoSpringHibernate<Sample, Integer> implements SampleDao {

	public SampleDaoSpringHibernate() {
		super(Sample.class);
	}
	
	public List<Sample> findByFullText(String queryText, User user) {
		FullTextQuery query = findByFullText(queryText, new String[]{"id","name","description"});
		query.enableFullTextFilter("sampleUser")
			.setParameter("value", user.getId());
		return query.list();
	}
	
	public Sample findByProjectAndName(Project project, String name) {
		return findUniqueByCriteria(
				Restrictions.eq("project", project),
				Restrictions.eq("name", name));
	}

	public Sample findByProjectAndId(Project project, Integer id) {
		return findUniqueByCriteria(
				Restrictions.eq("project", project),
				Restrictions.idEq(id));
	}
	
	public List<Sample> findAllByProject(Project project) {
		return findAllByField("project", project, Order.asc("id"));
	}
		
	private final static String HISTO_QUERY_NOTAX = 					
		"select sum(sq1.freq) as frequency, sum(sq1.wfreq) as \"weightedFrequency\", sq1.accession, sq1.description " +
		"from (" +
		"select seq.seq_name, count(*) as freq, count(*) * seq.coverage as wfreq, an.accession, an.description " +
		"from sequences seq " +
			"inner join features f on seq.sample_id=f.sample_id and seq.seq_name=f.seq_name " +
			"inner join annotations an on an.feature_id=f.feature_id " +
			"where seq.sample_id = :sampleId " +
				"and an.annotator_id = :annotatorId " +
				"and an.confidence = :confidence " +
			"group by seq.seq_name, seq.coverage, an.accession, an.description) sq1 " +
		"group by sq1.accession, sq1.description " +
		"order by sq1.accession";
	
	private final static String HISTO_QUERY_WITHTAX = 
		"select sum(sq1.freq) as frequency, sum(sq1.wfreq) as \"weightedFrequency\", sq1.accession, sq1.description " +
		"from (" +
		"select seq.seq_name, count(*) as freq, count(*) * seq.coverage as wfreq, an.accession, an.description " +
		"from sequences seq " +
			"inner join features f on seq.sample_id=f.sample_id and seq.seq_name=f.seq_name " +
			"inner join annotations an on an.feature_id=f.feature_id " +
			"where seq.sample_id = :sampleId " +
				"and an.annotator_id = :annotatorId " +
				"and an.confidence <= :confidence " +
				"and seq.taxonomy not in (:excludedTaxons) " +
			"group by seq.seq_name, seq.coverage, an.accession, an.description) sq1 " +
		"group by sq1.accession, sq1.description " +
		"order by sq1.accession";

	@SuppressWarnings("unchecked")
	public List<AnnotationHistogramDTO> annotationHistogram(final Sample sample,
			final Annotator annotator, final Double confidence, final Collection<Taxonomy> excludedTaxons) {
		
		Object result = getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query;
				if (excludedTaxons == null || excludedTaxons.size() == 0) {
					query = session.createSQLQuery(HISTO_QUERY_NOTAX);
				}
				else {
					query = session.createSQLQuery(HISTO_QUERY_WITHTAX)
						.setParameterList("excludedTaxons", excludedTaxons, 
							new CustomType(TaxonomyUserType.class,null));
				}
				query.setResultTransformer(Transformers.aliasToBean(AnnotationHistogramDTO.class));
				query.setInteger("sampleId", sample.getId());
				query.setDouble("confidence", confidence);
				query.setInteger("annotatorId", annotator.getId());
				return query.list();
			}
		});
		return (List<AnnotationHistogramDTO>)result;
	}

	public List<Object[]> findPageRowsByProject(final int projectId, final int firstResult,
			final int maxResults, final int sortFieldIndex, final String sortDirection) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Object[]>>() {
			@SuppressWarnings("unchecked")
			public List<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(
						" SELECT s.id, s.name, s.description, s.creation, " +
						" (SELECT count(*) from s.sequences) " +
						" FROM Sample s " +
						" WHERE " +
						"   s.project.id = :projectId" + 
						" ORDER BY " + String.format("col_%d_0_ %s",sortFieldIndex,sortDirection))
						.setFirstResult(firstResult)
						.setMaxResults(maxResults)
						.setParameter("projectId", projectId)
						.list();
			}
		});
	}
	
	public Long countByProject(final Integer projectId) {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.createAlias("project", "proj")
						.add(Restrictions.eq("proj.id",projectId))
					.setProjection(Projections.rowCount())
					.uniqueResult();
			}
		});
	}
}
