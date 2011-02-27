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

import org.mzd.shap.spring.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.dao.DataAccessException;

public class UserAdminServiceImpl implements UserAdminService {
	private UserDao userDao;
	private RoleDao roleDao;
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		User user = getUserDao().findByField("username", username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return user;
	}
	
	public User loadUser(String userName) throws NotFoundException {
		try {
			return (User)loadUserByUsername(userName);
		}
		catch (UsernameNotFoundException ex) {
			throw new NotFoundException(ex);
		}
	}
	
	public void updateUser(UserView modRequest) throws NotFoundException {
		User user = getUserDao().findByUsername(modRequest.getUsername());
		if (user == null) {
			throw new NotFoundException("User [" + modRequest.getUsername() + "] does not exist");
		}
		
		user.setName(modRequest.getName());
		user.setUsername(modRequest.getUsername());
		user.setPassword(modRequest.getPassword());
		
		user.getRoles().clear();
		for (String rn : modRequest.getRoles()) {
			Role reqRole = getRoleDao().findByField("name", rn);
			if (reqRole == null) {
				throw new NotFoundException("Requested role [" + rn + "] does not exist");
			}
			if (!user.getRoles().contains(reqRole)) {
				user.addRole(reqRole);
			}
		}
		
		getUserDao().saveOrUpdate(user);
	}
	
	public void createUser(UserView createRequest) throws UserAlreadyExistsException, NotFoundException {
		if (getUserDao().findByUsername(createRequest.getUsername()) != null) {
			throw new UserAlreadyExistsException("User [" + createRequest.getUsername() + "] already exists");
		}
		
		User user = new User();
		user.setName(createRequest.getName());
		user.setUsername(createRequest.getUsername());
		user.setPassword(createRequest.getPassword());
		
		for (String rn : createRequest.getRoles()) {
			Role reqRole = getRoleDao().findByField("name", rn);
			if (reqRole == null) {
				throw new NotFoundException("Requested role [" + rn + "] does not exist");
			}
			user.addRole(reqRole);
		}
		
		getUserDao().saveOrUpdate(user);
	}
	
	public void deleteUser(String userName) throws NotFoundException {
		User userToDelete = getUserDao().findByUsername(userName);
		if (userToDelete == null) {
			throw new NotFoundException("User [" + userName + "] does not exist");
		}
		getUserDao().delete(userToDelete);
	}

	public List<UserView> listExistingUsers() {
		return getUserDao().getUsers();
	}

	@Override
	public List<String> listRoles() {
		return getRoleDao().getRoleNames();
	}
	
	public UserDao getUserDao() {
		return userDao;
	}
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public RoleDao getRoleDao() {
		return roleDao;
	}
	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}
}
