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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mzd.shap.domain.Project;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name="Users")
public class User implements UserDetails {
	private static final long serialVersionUID = 5714036306798316556L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="USER_ID")
	private Integer id;
	@Size(min=0,max=255)
	private String name;
	@Column(unique=true)
	@NotNull
	@Size(min=3,max=255)
	private String username;
	@NotNull
	@Size(min=6,max=8)
	private String password;
	private boolean credentialsNonExpired = true;
	private boolean accountNonExpired = true;
	private boolean accountNonLocked = true;
	private boolean enabled = true;
	@ManyToMany(targetEntity=Role.class,fetch=FetchType.EAGER)
	@JoinTable(name="UserRoles",
	        joinColumns=@JoinColumn(name="USER_ID"),
	        inverseJoinColumns=@JoinColumn(name="ROLE_ID"))
	@Valid
	private Set<Role> roles= new HashSet<Role>();
	@ManyToMany(targetEntity=org.mzd.shap.domain.Project.class)
	@JoinTable(name="UserProjects",
	        joinColumns=@JoinColumn(name="USER_ID"),
	        inverseJoinColumns=@JoinColumn(name="PROJECT_ID"))
	@Valid
	private Set<Project> projects = new HashSet<Project>();
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("name",getName())
			.append("username",getUsername())
			.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(11,17)
			.append(getUsername())
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User == false) {
			return false;
		}
		
		if (obj == this) {
			return true;
		}
		
		User other = (User)obj;
		return new EqualsBuilder()
			.append(getUsername(),other.getUsername())
			.isEquals();
	}
	
	public Collection<GrantedAuthority> getAuthorities() {
		return new HashSet<GrantedAuthority>(getRoles());
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}
	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}
	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}
	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	public void addRole(Role role) {
		getRoles().add(role);
	}
	
	public Set<Project> getProjects() {
		return projects;
	}
	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

}
