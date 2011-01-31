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
package org.mzd.shap.hibernate.search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.mzd.shap.domain.authentication.User;
import org.mzd.shap.hibernate.search.view.Report;
import org.mzd.shap.hibernate.search.view.ReportBuilderFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class FullTextSearchImpl extends HibernateDaoSupport implements FullTextSearch {
	private final static Version LUCENE_VERSION = Version.LUCENE_29;
	
	public Object findUnique(final String queryText, final User user) {
		return getHibernateTemplate().execute(new HibernateCallback<Object>() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				try {
					FullTextSession fts = Search.getFullTextSession(session);
					
					Query query = new QueryParser(
							LUCENE_VERSION,
							"id",
							new KeywordAnalyzer()).parse(queryText);
					
					FullTextQuery ftq = fts.createFullTextQuery(query);
					
					if (user != null) {
						ftq.enableFullTextFilter("featureUser").setParameter("value",user.getId());
						ftq.enableFullTextFilter("sequenceUser").setParameter("value", user.getId());
						ftq.enableFullTextFilter("sampleUser").setParameter("value", user.getId());
						ftq.enableFullTextFilter("projectUser").setParameter("value", user.getId());
					}
					
					return ftq.uniqueResult();
					
				} catch (ParseException ex) {
					throw new HibernateException("Error creating Lucene query for Hibernate Search",ex);
				}
			}
		});
	}
	
	public static class SearchResult<T> {
		private List<T> results;
		private Integer firstResult;
		private Integer maxResults;
		private Integer resultSize;
		
		public List<T> getResults() {
			return results;
		}
		public void setResults(List<T> results) {
			this.results = results;
		}
		public Integer getFirstResult() {
			return firstResult;
		}
		public void setFirstResult(Integer firstResult) {
			this.firstResult = firstResult;
		}
		public Integer getMaxResults() {
			return maxResults;
		}
		public void setMaxResults(Integer maxResults) {
			this.maxResults = maxResults;
		}
		public Integer getResultSize() {
			return resultSize;
		}
		public void setResultSize(Integer resultSize) {
			this.resultSize = resultSize;
		}
	}

	/**
	 * Create a MultiField query from a supplied query string, specifying a default operator between terms.
	 * 
	 * @param queryText - the query string to parse
	 * @param defaultOperator - default operator between terms
	 * @return MultiFieldQuery
	 * @throws ParseException
	 */
	protected Query createMultiFieldQuery(String queryText, String[] queryfields, Operator defaultOperator) throws ParseException {
		QueryParser qp = new MultiFieldQueryParser(LUCENE_VERSION, queryfields, new StandardAnalyzer(LUCENE_VERSION));
		qp.setDefaultOperator(defaultOperator);
		return qp.parse(queryText);
	}
	
	@Override
	public SearchResult<Report> find(final String queryText, final String[] queryFields, final Class<?>[] allowedClasses, 
			final int firstResult, final int maxResults) {
		
		return getHibernateTemplate().execute(new HibernateCallback<SearchResult<Report>>() {
			public SearchResult<Report> doInHibernate(Session session) throws HibernateException, SQLException {
				try {
					SearchResult<Report> result = new SearchResult<Report>();

					Query query = createMultiFieldQuery(queryText, queryFields, Operator.AND);
					
					FullTextQuery ftq = Search.getFullTextSession(session)
						.createFullTextQuery(query,allowedClasses)
						.setProjection(FullTextQuery.SCORE,FullTextQuery.THIS)
						.setFirstResult(firstResult)
						.setMaxResults(maxResults);
					
					List<?> resultSet = ftq.list();
					
					ReportBuilderFactory builderFactory = new ReportBuilderFactory("detail","=");
					
					// Convert, in a clumsy fashion, the object result-set into a list of reports.
					// In the future, this should use a projection and possible the conversion then
					// becomes a ResultTransformer.
					List<Report> reports = new ArrayList<Report>();
					for (Object obj : resultSet) {
						Object[] row = (Object[])obj;
						reports.add(builderFactory.buildReport((Float)row[0], row[1]));
					}
					
					result.setResults(reports);
					result.setFirstResult(firstResult);
					result.setMaxResults(reports.size());
					result.setResultSize(ftq.getResultSize());
					
					return result;
				} 
				catch (ParseException ex) {
					throw new HibernateException("Error creating Lucene query for Hibernate Search",ex);
				}
			}
		});
	}
}
