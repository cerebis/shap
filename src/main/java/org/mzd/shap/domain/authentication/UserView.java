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

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserView {
	@Size(min=3,max=255)
	private String name;
	@NotNull
	@Size(min=3,max=255)
	private String username;
	@NotNull
	@Size(min=6,max=8)
	private String password;
	@NotNull
	@Size(min=1)
	private List<String> roles = new ArrayList<String>();
	
	public UserView() {/*...*/}

	public UserView(String name, String username, String password) {
		this.name = name;
		this.username = username;
		this.password = password;
		addRole("user");
	}

	public UserView(User user) {
		this.name = user.getName();
		this.username = user.getUsername();
		this.password = user.getPassword();
		for (Role r : user.getRoles()) {
			addRole(r.getName());
		}
	}
	
	/**
	 * Create a regular user.
	 * 
	 * @param name
	 * @param username
	 * @param password
	 * @return
	 */
	public static UserView createUser(String name, String username, String password) {
		return new UserView(name,username,password);
	}
	
	/**
	 * Create a user with administator role.
	 * 
	 * @param name
	 * @param username
	 * @param password
	 * @return
	 */
	public static UserView createAdminUser(String name, String username, String password) {
		UserView admin = createUser(name, username, password);
		admin.addRole("admin");
		return admin;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public void addRole(String role) {
		getRoles().add(role);
	}
}