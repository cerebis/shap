package org.mzd.shap.spring.orm;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

public class Reporting {

	public static String reportQueryCacheStatistics(SessionFactory sessionFactory) {
		StringBuffer buffer = new StringBuffer();
		Statistics stats = sessionFactory.getStatistics();
		
		long queryHitCount = stats.getQueryCacheHitCount();
		long queryMissCount = stats.getQueryCacheMissCount();
		
		buffer.append("Query Cache Hits: " + queryHitCount + "\n");
		buffer.append("Query Cache Misses: " + queryMissCount + "\n");
		if (queryHitCount + queryMissCount > 0) {
			buffer.append("Query Cache Hit Ratio: " + 
					(double)queryHitCount / (double)(queryHitCount + queryMissCount) + "\n");
		}
		
		return buffer.toString();
	}
		
	public static String reportSecondLevleCacheStatistics(SessionFactory sessionFactory) {
		StringBuffer buffer = new StringBuffer();
		Statistics stats = sessionFactory.getStatistics();
		
		long cacheHitCount = stats.getSecondLevelCacheHitCount();
		long cacheMissCount = stats.getSecondLevelCacheMissCount();
		
		System.out.println("Second Level Cache Hits: " + cacheHitCount);
		System.out.println("Second Level Cache Misses: " + cacheMissCount);
		if (cacheHitCount + cacheMissCount > 0) {
			System.out.println("Second Level Cache Hit Ratio: " + (double)cacheHitCount / (double)(cacheHitCount + cacheMissCount));
		}
		
		return buffer.toString();
	}
	
}
