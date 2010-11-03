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

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.transform.Transformers;
import org.mzd.shap.analysis.Annotator;
import org.mzd.shap.analysis.Detector;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.FeatureException;
import org.mzd.shap.domain.FeatureType;
import org.mzd.shap.domain.Location;
import org.mzd.shap.domain.Project;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.Taxonomy;
import org.mzd.shap.domain.authentication.User;
import org.mzd.shap.spring.orm.BaseDaoSpringHibernate;
import org.springframework.orm.hibernate3.HibernateCallback;

public class FeatureDaoSpringHibernate extends BaseDaoSpringHibernate<Feature, Integer> implements FeatureDao {

	public FeatureDaoSpringHibernate() {
		super(Feature.class);
	}

	public List<Feature> findByFullText(String queryText, User user) {
		FullTextQuery query = findByFullText(queryText, new String[]{"id","confidence","partial","type",
				"location.start","location.end","location.strand","location.frame",
				"annotations.accession","annotations.description","annotations.annotator"});
		query.enableFullTextFilter("featureUser")
			.setParameter("value", user.getId());
		return query.list();
	}
	
	public List<Feature> findUnprocessed(Sequence sequence) {
		return findByCriteria(null, 
				Restrictions.eq("sequence", sequence),
				Restrictions.isEmpty("annotations"));
	}
	
	protected void addOrders(Criteria crit, Order... orders) {
		if (orders != null) {
			for (Order o : orders) {
				crit.addOrder(o);
			}
		}
		else {
			crit.addOrder(Order.asc("id"));
		}
	}
	
	public List<Feature> findMarkers(final Order order, final String[] markerNames) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Feature>>() {
    		@SuppressWarnings("unchecked")
			public List<Feature> doInHibernate(Session session) {
    			// Or all specified marker names together
    			Disjunction disj = Restrictions.disjunction();
    			for (String mn : markerNames) {
    				disj.add(Restrictions.like("description","%" + mn + "%"));
    			}
    			
    	        Criteria crit = session.createCriteria(getPersistentClass())
    	        	.createCriteria("annotations")
    	        		.add(disj);
    	        
    	        if (order != null) {
    	        	crit.addOrder(order);
    	        }
    	        return crit.list();
    		}
    	});
	}
	
	public Feature findBySequenceAndId(Sequence sequence, Integer id) {
		return findUniqueByCriteria(
				Restrictions.eq("sequence", sequence),
				Restrictions.idEq(id));
	}

	public List<Feature> findByProjectAndType(final Project project, final FeatureType type, final boolean minimal) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Feature>>() {
			@SuppressWarnings("unchecked")
			public List<Feature> doInHibernate(Session session) throws HibernateException, SQLException {
				if (minimal) {
					return session.createCriteria(getPersistentClass())
						.setProjection(Projections.projectionList()
								.add(Projections.id(), "id")
								.add(Projections.property("version"), "version"))
						.add(Restrictions.eq("type", type))
						.createCriteria("sequence")
							.createCriteria("sample")
								.add(Restrictions.eq("project",project))
						.setResultTransformer(Transformers.aliasToBean(getPersistentClass()))
					.list();
					
				}
				else {
					return session.createCriteria(getPersistentClass())
						.add(Restrictions.eq("type", type))
						.createCriteria("sequence")
							.createCriteria("sample")
								.add(Restrictions.eq("project",project))
						.list();
				}
			}
		});
	}

	public List<Feature> findByProjectAndType(final Project project, final FeatureType type) {
		return findByProjectAndType(project,type,false);
	}
	
	public List<Feature> findBySampleAndType(final Sample sample, final FeatureType type, final boolean minimal) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Feature>>() {
			@SuppressWarnings("unchecked")
			public List<Feature> doInHibernate(Session session) throws HibernateException, SQLException {
				if (minimal) {
					return session.createCriteria(getPersistentClass())
						.setProjection(Projections.projectionList()
							.add(Projections.id(), "id")
							.add(Projections.property("version"),"version"))
						.add(Restrictions.eq("type", type))
						.createCriteria("sequence")
							.add(Restrictions.eq("sample",sample))
						.setResultTransformer(Transformers.aliasToBean(getPersistentClass()))
						.list();
				}
				else {
					return session.createCriteria(getPersistentClass())
						.add(Restrictions.eq("type", type))
						.createCriteria("sequence")
							.add(Restrictions.eq("sample",sample))
						.list();
				}
			}
		});
	}
	
	public List<Feature> pageBySample(final int firstElement, final int maxResults, final Sample sample,
			final FeatureType type, final Collection<Taxonomy> excludedTaxa) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Feature>>() {
			@SuppressWarnings("unchecked")
			public List<Feature> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria crit = session.createCriteria(getPersistentClass())
						.setFirstResult(firstElement)
						.setMaxResults(maxResults)
						.addOrder(Order.asc("id"))
						.add(Restrictions.eq("type", type))
						.createAlias("sequence","seq")
							.add(Restrictions.eq("seq.sample", sample));
				
				if (excludedTaxa != null && excludedTaxa.size() > 0) {
					crit.add(Restrictions.not(
						Restrictions.in("seq.taxonomy", excludedTaxa)));
				}
				return crit.list();
			}
		});
	}

	public List<Feature> pageBySample(final int firstElement, final int maxResults, final Sample sample, final FeatureType type) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Feature>>() {
			@SuppressWarnings("unchecked")
			public List<Feature> doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(getPersistentClass())
						.setFirstResult(firstElement)
						.setMaxResults(maxResults)
						.addOrder(Order.asc("id"))
						.add(Restrictions.eq("type", type))
						.createAlias("sequence","seq")
						.add(Restrictions.eq("seq.sample", sample))
						.list();
			}
		});
	}
	
	public List<Feature> pageBySample(final int firstElement, final int maxResults, final Sample sample) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Feature>>() {
			@SuppressWarnings("unchecked")
			public List<Feature> doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(getPersistentClass())
						.setFirstResult(firstElement)
						.setMaxResults(maxResults)
						.addOrder(Order.asc("id"))
						.createCriteria("sequence")
							.add(Restrictions.eq("sample", sample))
						.list();
			}
		});
	}
	
	public Long countBySample(final Sample sample) {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.setProjection(Projections.rowCount())
					.createCriteria("sequence")
						.add(Restrictions.eq("sample", sample))
					.uniqueResult();
			}
		});
	}
	
	public Long countBySample(final Sample sample, final FeatureType type, final Collection<Taxonomy> excludedTaxa) {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria crit = session.createCriteria(getPersistentClass())
					.setProjection(Projections.rowCount())
					.add(Restrictions.eq("type", type))
					.createCriteria("sequence")
						.add(Restrictions.eq("sample", sample));
				
				if (excludedTaxa != null && excludedTaxa.size() > 0) {
					crit.add(Restrictions.not(
							Restrictions.in("taxonomy", excludedTaxa)));
				}
				return (Long)crit.uniqueResult();
			}
		});
	}
	
	public Long countBySampleAndType(final Sample sample, final FeatureType type) {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.setProjection(Projections.rowCount())
					.add(Restrictions.eq("type", type))
					.createCriteria("sequence")
						.add(Restrictions.eq("sample", sample))
					.uniqueResult();
			}
		});
	}

	public List<Feature> pageBySequence(final int firstElement, final int maxResults, final Sequence sequence, final Order... order) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Feature>>() {
			@SuppressWarnings("unchecked")
			public List<Feature> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria crit =session.createCriteria(getPersistentClass())
					.add(Restrictions.eq("sequence", sequence))
					.setFirstResult(firstElement)
					.setMaxResults(maxResults);
				addOrders(crit,order);
				return crit.list();
			}
		});
	}

	public List<Feature> pageBySequenceAndType(final int firstElement, final int maxResults, 
			final Sequence sequence, final FeatureType type) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Feature>>() {
			@SuppressWarnings("unchecked")
			public List<Feature> doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(getPersistentClass())
					.add(Restrictions.eq("sequence", sequence))
					.add(Restrictions.eq("type", type))
					.setFirstResult(firstElement)
					.setMaxResults(maxResults)
					.addOrder(Order.asc("id"))
					.list();
			}
		});
	}

	public List<Feature> findBySequence(final Sequence sequence, final Order... order) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Feature>>() {
			@SuppressWarnings("unchecked")
			public List<Feature> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria crit = session.createCriteria(getPersistentClass())
					.add(Restrictions.eq("sequence", sequence));
				addOrders(crit,order);
				return crit.list();
			}
		});
	}
	
	public List<Feature> findBySequenceAndType(final Sequence sequence, final FeatureType type, final boolean minimal) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Feature>>() {
			@SuppressWarnings("unchecked")
			public List<Feature> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria crit = session.createCriteria(getPersistentClass())
								.add(Restrictions.eq("sequence", sequence))
								.add(Restrictions.eq("type",type));
				if (minimal) {
					crit.setProjection(Projections.projectionList()
							.add(Projections.id(),"id")
							.add(Projections.property("version"),"version"))
						.setResultTransformer(Transformers.aliasToBean(getPersistentClass()));
				}
				
				return crit.list();
			}
		});
	}
	
	public Feature findBySequenceAndLocus(final Sequence sequence, String locusTag, final boolean minimal) throws FeatureException {
		
		Pattern p = Pattern.compile("^\\D+(\\d+)$");
		Matcher m = p.matcher(locusTag);
		if (!m.matches()) {
			throw new FeatureException("LocusTag [" + locusTag + 
					"] does not conform to pattern [\\D+\\d+]");
		}
		
		final Integer rank = Integer.parseInt(m.group(1)) - 1;
		
		return getHibernateTemplate().execute(new HibernateCallback<Feature>() {
			public Feature doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria crit = session.createCriteria(getPersistentClass())
							.add(Restrictions.eq("sequence", sequence))
							.add(Restrictions.eq("rank",rank));
				
				if (minimal) {
					crit.setProjection(Projections.projectionList()
							.add(Projections.id(),"id")
							.add(Projections.property("version"),"version"))
						.setResultTransformer(Transformers.aliasToBean(getPersistentClass()));
				
				}
				
				return (Feature)crit.uniqueResult();
			}
		});
	}

	public Long countBySequence(final Sequence sequence) {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.add(Restrictions.eq("sequence", sequence))
					.setProjection(Projections.rowCount())
					.uniqueResult();
			}
		});
	}
	
	public Long countBySequence(final Integer sequenceId) {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.createAlias("sequence","seq")
						.add(Restrictions.eq("seq.id", sequenceId))
					.setProjection(Projections.rowCount())
					.uniqueResult();
			}
		});
	}

	public Long countBySequenceAndType(final Sequence sequence, final FeatureType type) {
		Object count =	getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				return (Long)session.createCriteria(getPersistentClass())
					.add(Restrictions.eq("sequence", sequence))
					.add(Restrictions.eq("type", type))
					.setProjection(Projections.rowCount())
					.uniqueResult();
			}
		});
		
		return (Long)count;
	}
	
	public Feature findByOldId(Integer oldId) {
		return findUniqueByCriteria(Restrictions.eq("oldId", oldId));
	}
	
	public void saveIfNew(final Sequence sequence, final Feature feature) {
		getHibernateTemplate().execute(new HibernateCallback<Object>() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				Location l = feature.getLocation();
				
				Long count = (Long)session.createCriteria(getPersistentClass())
					.add(Restrictions.eq("sequence", sequence))
					.add(Restrictions.eq("location.start", l.getStart()))
					.add(Restrictions.eq("location.end", l.getEnd()))
					.add(Restrictions.eq("location.frame", l.getFrame()))
					.add(Restrictions.eq("location.strand", l.getStrand()))
					.setProjection(Projections.rowCount())
					.uniqueResult();
				
				if (count == 0) {
					feature.setSequence(sequence);
					makePersistent(feature);
				}
				
				return null;
			}
		});
	}
	
	public Feature loadWithData(final Integer id) {
		return getHibernateTemplate().execute(new HibernateCallback<Feature>() {
			public Feature doInHibernate(Session session) throws HibernateException, SQLException {
				return (Feature)session.createCriteria(getPersistentClass())
					.setFetchMode("data", FetchMode.JOIN)
					.add(Restrictions.idEq(id))
					.uniqueResult();
			}
		});
	}
	
	public List<Feature> loadWithData(final List<Feature> features) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Feature>>() {
			@SuppressWarnings("unchecked")
			public List<Feature> doInHibernate(Session session) throws HibernateException, SQLException {
				List<Integer> ids = new ArrayList<Integer>();
				for (Feature f : features) {
					ids.add(f.getId());
				}
				return session.createCriteria(getPersistentClass())
					.setFetchMode("data", FetchMode.JOIN)
					.add(Restrictions.in("id", ids))
					.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<Feature> findBySampleAndAnnotator(final Sample sample, final Annotator annotator) {
		Object result = getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				return session.createCriteria(getPersistentClass())
					.createAlias("annotations", "an")
					.createAlias("sequence","seq")
					.add(Restrictions.eq("seq.sample",sample))
					.add(Restrictions.eq("an.annotator",annotator))
					.add(Restrictions.isNotNull("an.description"))
					.list();
			}
		});
		return (List<Feature>)result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Feature> findOnlyNew(final Collection<Feature> features) {
		Object result = getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<Feature> newFeatures = new ArrayList<Feature>();
				for (Feature f : features) {
					Location loc = f.getLocation();
					Long count = (Long)session.createCriteria(getPersistentClass())
						.add(Restrictions.eq("sequence", f.getSequence()))
						.add(Restrictions.eq("location.start", loc.getStart()))
						.add(Restrictions.eq("location.end", loc.getEnd()))
						.add(Restrictions.eq("location.strand", loc.getStrand()))
						.add(Restrictions.eq("location.frame", loc.getFrame()))
						.setProjection(Projections.rowCount())
						.uniqueResult();
					if (count == 0) {
						newFeatures.add(f);
					}
				}
				return newFeatures;
			}
		});
		return (List<Feature>)result;
	}

	/**
	 * Build a list of Features for each FeatureType where only independent features
	 * are reported. Here, independent features of a given type are those from different 
	 * detectors which do not possess any overlap in genomeic coordinates. Conflicts
	 * are resolved by consideration of the assigned rank of each detector for
	 * a given FeatureType.
	 */
	@SuppressWarnings("unchecked")
	public List<Feature> findResolvedSet(final Sequence sequence, final FeatureType restrictedToType) {
		Object result = getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				List<Feature> features = new ArrayList<Feature>();
				
				List<FeatureType> types = (List<FeatureType>)session
					.createCriteria(getPersistentClass())
					.add(Restrictions.eq("sequence",sequence))
					.setProjection(Projections.projectionList()
						.add(Projections.groupProperty("type")))
					.list();
				
				for (FeatureType t : types) {
					// skip other types if we've got a non-null restriction
					if (restrictedToType != null && t != restrictedToType) {
						continue;
					}
					
					List<Object[]> detectors = (List<Object[]>)session
						.createCriteria(getPersistentClass())
						.createAlias("detector", "det")
						.setFetchMode("detector", FetchMode.JOIN)
						.add(Restrictions.eq("sequence",sequence))
						.add(Restrictions.eq("type", t))
						.addOrder(Order.desc("det.rank"))
						.setProjection(Projections.projectionList()
							.add(Projections.groupProperty("detector"))
							.add(Projections.groupProperty("det.rank")))
						.list();
					
					List<Feature> independentFeaturesOfType = new ArrayList<Feature>();
					
					for (Object[] row : detectors) {
						
						Detector detector = (Detector)row[0];
						List<Feature> featuresOfDetector = (List<Feature>)session
							.createCriteria(getPersistentClass())
							.add(Restrictions.eq("sequence", sequence))
							.add(Restrictions.eq("detector", detector))
							.list();
						
						for (Feature df : featuresOfDetector) {
							boolean independent = true;
							for (Feature ndf : independentFeaturesOfType) {
								if (!df.getDetector().equals(ndf.getDetector()) &&
										!df.getLocation().independent(ndf.getLocation())) {
									independent = false;
									break;
								}
							}
							
							if (independent) {
								independentFeaturesOfType.add(df);
							}
						}
					}
					features.addAll(independentFeaturesOfType);
				}
				
				return Feature.sortAscendingStart(features);
			}
		});
		return (List<Feature>)result;
	}
	
	public List<Object[]> findPageRowsBySequence(final int sequenceId, final int firstResult, final int maxResults, 
			final int sortFieldIndex, final String sortDirection) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Object[]>>() {
			@SuppressWarnings("unchecked")
			public List<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(
						" SELECT f.id, f.location.start, f.location.end, f.location.strand, f.location.frame, f.partial, f.confidence, f.type, " +
						"  f.location.end - f.location.start + 1, (SELECT count(*) from f.annotations) " +
						" FROM Feature f " +
						" WHERE " +
						"   f.sequence.id = :sequenceId" + 
						" ORDER BY " + String.format("col_%d_0_ %s",sortFieldIndex,sortDirection))
						.setFirstResult(firstResult)
						.setMaxResults(maxResults)
						.setParameter("sequenceId", sequenceId)
						.list();
			}
		});
	}

	public static class FeatureBreakdownDTO implements Serializable {
		private static final long serialVersionUID = -26450120355021044L;
		private FeatureType type;
		private Long count;

		public FeatureType getType() {
			return type;
		}
		public void setType(FeatureType type) {
			this.type = type;
		}
		public Long getCount() {
			return count;
		}
		public void setCount(Long count) {
			this.count = count;
		}
	}
	
	public List<FeatureBreakdownDTO> findTypeBreakdown(final Integer sequenceId) {
		return getHibernateTemplate().execute(new HibernateCallback<List<FeatureBreakdownDTO>>() {
			@SuppressWarnings("unchecked")
			public List<FeatureBreakdownDTO> doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(getPersistentClass())
					.createAlias("sequence","seq")
						.add(Restrictions.eq("seq.id",sequenceId))
					.setProjection(Projections.projectionList()
							.add(Projections.groupProperty("type"),"type")
							.add(Projections.rowCount(),"count"))
					.addOrder(Order.asc("type"))
					.setResultTransformer(Transformers.aliasToBean(FeatureBreakdownDTO.class))
					.list();
			}
		});
	}
}