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
package org.mzd.shap.analysis;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.spring.orm.BaseDaoSpringHibernate;
import org.springframework.orm.hibernate3.HibernateCallback;


public class AnnotatorDaoSpringHibernate extends BaseDaoSpringHibernate<Annotator, Integer> implements AnnotatorDao {

	public AnnotatorDaoSpringHibernate() {
		super(Annotator.class);
	}

	@SuppressWarnings("unchecked")
	public List<Annotator> findUsedBySequence(final Sequence seq) {
		Object result = getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(
							" FROM SimpleAnnotator AS ar " +
							" WHERE ar IN ( " +
								" SELECT anno.annotator " +
								" FROM Sequence as seq " +
									" INNER JOIN seq.features AS feat " +
									" INNER JOIN feat.annotations AS anno " +
									" WHERE seq = :seq " +
									" GROUP BY anno.annotator) " +
							" ORDER BY ar.name asc")
							.setEntity("seq", seq)
							.list();
			}
		});
		
		return (List<Annotator>)result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Annotator> findUsedBySample(final Sample sample) {
		Object result = getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(
							"from SimpleAnnotator as ar " +
							"where ar in ( " +
								"select anno.annotator " +
								"from " +
									"Sample as smp " +
									"inner join smp.sequences as seq " +
									"inner join seq.features as feat " +
									"inner join feat.annotations as anno " +
									"where smp = :sample " +
									"group by anno.annotator" +
							") " +
							"order by ar.name asc")
							.setEntity("sample", sample)
							.list();
			}
		});
		
		return (List<Annotator>)result;
	}
}
