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
