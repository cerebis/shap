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

	public static void main(String[] args) {
		try {
			ApplicationContext ctx = new FileSystemXmlApplicationContext(
					"war/WEB-INF/spring/local-datasource-context.xml",
					"war/WEB-INF/spring/service-context.xml",
					"war/WEB-INF/spring/orm-context.xml");
			SessionFactory sessionFactory = (SessionFactory)ctx.getBean("sessionFactory");
			Session session = sessionFactory.openSession();
			
			// Index single-threaded. Appear to have problems with collections.
			Search.getFullTextSession(session)
				.createIndexer()
					.threadsToLoadObjects(1)
					.threadsForSubsequentFetching(1)
					.optimizeOnFinish(true)
					.startAndWait();
			System.exit(0);
		} catch (InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
}
