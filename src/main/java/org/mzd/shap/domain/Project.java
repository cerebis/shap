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
package org.mzd.shap.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.Indexed;
import org.mzd.shap.domain.authentication.User;
import org.mzd.shap.hibernate.search.ProjectFilterFactory;

@Entity
@Table(name="Projects")
@Indexed(index="Projects")
@FullTextFilterDef(name="projectUser",impl=ProjectFilterFactory.class)
public class Project {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="PROJECT_ID")
	private Integer id;
	@Fields({
		@Field,
		@Field(name="name_full",analyzer=@Analyzer(impl=KeywordAnalyzer.class))
	})
	@Column(unique=true)
	@NotNull
	@Size(min=3,max=255)
	private String name;
	@Field
	@Type(type="text")
	@Size(min=1,max=4095)
	private String description;
	@Field
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date creation;
	@ManyToMany(mappedBy="projects",targetEntity=org.mzd.shap.domain.authentication.User.class,fetch=FetchType.LAZY)
	private Set<User> users = new HashSet<User>();
	@OneToMany(mappedBy="project",fetch=FetchType.LAZY)
	@ContainedIn
	@Valid
	private Set<Sample> samples = new HashSet<Sample>();
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,37)
			.append(getName())
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Project == false) {
			return false;
		}
		
		if (obj == this) {
			return true;
		}
		
		Project other = (Project)obj;
		
		return new EqualsBuilder()
			.append(getName(), other.getName())
			.isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id",getId())
			.append("name",getName())
			.append("description",getDescription())
			.toString();
	}
	
	public Project() {/*...*/}
	
	public Project(String name, String description, Date creation) {
		this.name = name;
		this.description = description;
		this.creation = creation;
	}
	
	public Date getCreation() {
		return creation;
	}
	public void setCreation(Date creation) {
		this.creation = creation;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getId() {
		return id;
	}
	protected void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Set<Sample> getSamples() {
		return samples;
	}
	public void setSamples(Set<Sample> samples) {
		this.samples = samples;
	}
	public void addSample(Sample sample) {
		sample.setProject(this);
		getSamples().add(sample);
	}

	public Set<User> getUsers() {
		return users;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	}
	public void addUser(User user) {
		getUsers().add(user);
		user.getProjects().add(this);
	}
}
