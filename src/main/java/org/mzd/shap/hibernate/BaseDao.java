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
package org.mzd.shap.hibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Order;

/**
 * A generic DAO interface for the storage and retrieval
 * of persist objects. This should be the fundamental
 * basis of all DAOs in this project.
 * 
 */
public interface BaseDao<ENTITY, ID extends Serializable> {

	Long countAll();
	
	List<ENTITY> pageAll(Order order, int pageNumber, int pageSize);
	
	/**
	 * Find a given persistent object by its unique identifier.
	 * 
	 * @param id the objects identitier.
	 * @throws ObjectNotFoundException (unchecked) when entity not found.
	 * @return the entity in question.
	 */
	ENTITY findByID(ID id);
	
	/**
	 * Find by a given field
	 * @param the field and field value with which to build a query.
	 * @throws Exception when entity not found or non-unique.
	 * @return the entity in question.
	 */
	ENTITY findByField(String field, Object value);
	
	/**
	 * Retrieve a list of all entities of a given type from
	 * the database.
	 * 
	 * @param order the ordering of the retrieved list of entities.
	 * @return the list of retrieved entities.
	 */
	List<ENTITY> findAll(Order order);
	
	/**
	 * Retrieve a list of all entities of a give type from
	 * the database, constained by the value of a particular field.
	 * 
	 * @param field - the field which will form the constraint
	 * @param value - the value of the constraint
	 * @param order - the order of the returned list
	 * @return
	 */
	List<ENTITY> findAllByField(String field, Object value, Order order);
	
	/**
	 * Persist a given entity to the database.
	 * 
	 * @param entity
	 */
	ENTITY makePersistent(ENTITY entity);
	
	void saveOrUpdateAll(Collection<?> entities);
	
	/**
	 * Remove an entities persisted state from the datastore. This
	 * does not effect the object its, except for the possibility
	 * of its identifier.
	 * 
	 * @param entity the object to delete.
	 */
	void makeTransient(ENTITY entity);
	
	/**
	 * Reattach an instance to the persistence layer
	 * Use the returned entity.
	 * 
	 * @param entity - the object to reattach
	 * @return a reattached reference
	 */
	ENTITY reattach(ENTITY entity);
	
	/**
	 * Flush the session cache.
	 */
	void flush();
	
	/**
	 * Evict the object from the session cache.
	 */
	void evict(ENTITY entity);
	
	/**
	 * Refresh the object state from the underlying store.
	 * 
	 * @param entity - the object to refresh
	 */
	void refresh(ENTITY entity);
}
