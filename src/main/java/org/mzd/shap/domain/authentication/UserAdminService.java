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

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserAdminService extends UserDetailsService {

	/**
	 * Load a user instance by username.
	 * 
	 * @param username
	 * @return
	 * @throws UserNotFoundException
	 */
	public User loadUser(String username) throws UserNotFoundException;
	
	/**
	 * Update an existing user.
	 * 
	 * @param user
	 * @throws UserNotFoundException 
	 */
	public void updateUser(User user) throws UserNotFoundException;
	
	/**
	 * Create a new user.
	 * 
	 * @param user
	 * @throws UserAlreadyExistsException
	 */
	public void createUser(User user) throws UserAlreadyExistsException;
	
	/**
	 * Delete an existing user.
	 * 
	 * @param userName
	 * @throws UserNotFoundException
	 */
	public void deleteUser(String userName) throws UserNotFoundException;
	
	/**
	 * List existing users.
	 * 
	 * @return
	 */
	public List<UserView> listExistingUsers();
}
