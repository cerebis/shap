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

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.dao.DataAccessException;

public class UserAdminServiceImpl implements UserAdminService {
	private UserDao userDao;
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		User user = getUserDao().findByField("username", username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return user;
	}
	
	public User loadUser(String userName) throws UserNotFoundException {
		try {
			return (User)loadUserByUsername(userName);
		}
		catch (UsernameNotFoundException ex) {
			throw new UserNotFoundException(ex);
		}
	}
	
	public void updateUser(User user) throws UserNotFoundException {
		User existingUser = getUserDao().findByUsername(user.getUsername());
		if (existingUser == null) {
			throw new UserNotFoundException("User [" + user.getUsername() + "] does not exist");
		}
		user.setId(existingUser.getId());
		getUserDao().saveOrUpdate(user);
	}
	
	public void createUser(User user) throws UserAlreadyExistsException {
		User oldUser = getUserDao().findByUsername(user.getUsername());
		if (oldUser != null) {
			throw new UserAlreadyExistsException("User [" + user.getUsername() + "] already exists");
		}
		getUserDao().saveOrUpdate(user);
	}
	
	public void deleteUser(String userName) throws UserNotFoundException {
		User userToDelete = getUserDao().findByUsername(userName);
		if (userToDelete == null) {
			throw new UserNotFoundException("User [" + userName + "] does not exist");
		}
		getUserDao().delete(userToDelete);
	}

	public List<UserView> listExistingUsers() {
		return getUserDao().listUsers();
	}
	
	public UserDao getUserDao() {
		return userDao;
	}
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
}
