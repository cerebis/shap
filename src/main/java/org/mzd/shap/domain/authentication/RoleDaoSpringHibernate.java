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
package org.mzd.shap.domain.authentication;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.mzd.shap.spring.orm.BaseDaoSpringHibernate;
import org.springframework.orm.hibernate3.HibernateCallback;

public class RoleDaoSpringHibernate extends BaseDaoSpringHibernate<Role, Integer> implements RoleDao {

	public RoleDaoSpringHibernate() {
		super(Role.class);
	}
	
	public List<String> getRoleNames() {
		return getHibernateTemplate().execute(new HibernateCallback<List<String>>() {
			@Override
			@SuppressWarnings("unchecked")
			public List<String> doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(getPersistentClass())
					.addOrder(Order.asc("name"))
					.setProjection(Projections.projectionList()
						.add(Projections.property("name")))
					.list();
			}
		});
	}

}
