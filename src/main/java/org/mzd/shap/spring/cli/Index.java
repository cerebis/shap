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
package org.mzd.shap.spring.cli;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.Search;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Simple class to manually instigate Hibernate Search (Lucene) indexing of an existing database.
 * 
 */
public class Index {
	private static Log LOGGER = LogFactory.getLog(Index.class);
	private static final String BEGIN_MSG = "Indexing the database, this can take some time.";
	private static final String FINISH_MSG = "Completed indexing and optimization.";
	private static final String ERROR_MSG = "An exception occured while indexing.";
	private static final String USAGE_MSG = "Mass indexing command takes no arguments.";

	public static void main(String[] args) {
		if (args.length != 0) {
			System.out.println(USAGE_MSG);
			System.exit(1);
		}

		try {
			ApplicationContext ctx = new FileSystemXmlApplicationContext(
					"war/WEB-INF/spring/datasource-context.xml",
					"war/WEB-INF/spring/orm-massindex-context.xml");

			SessionFactory sessionFactory = (SessionFactory)ctx.getBean("sessionFactory");
			Session session = sessionFactory.openSession();

			System.out.println(BEGIN_MSG);
			LOGGER.info(BEGIN_MSG);
			
			// Index single-threaded. Appear to have problems with collections.
			Search.getFullTextSession(session)
				.createIndexer()
					.batchSizeToLoadObjects(30)
					.threadsForSubsequentFetching(4)
	   				.threadsToLoadObjects(2)
					.cacheMode(CacheMode.NORMAL)
					.optimizeOnFinish(true)
					.startAndWait();

			System.out.println(FINISH_MSG);
			LOGGER.info(FINISH_MSG);
					
			System.exit(0);
		} 
		catch (Exception ex) {
			System.err.println(ERROR_MSG + " [" + ex.getMessage() + "]");
			LOGGER.debug(ERROR_MSG, ex);
		}
	}
}
