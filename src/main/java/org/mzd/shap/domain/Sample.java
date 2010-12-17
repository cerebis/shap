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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.mzd.shap.hibernate.search.SampleFilterFactory;

@Entity
@Table(name="Samples",
		uniqueConstraints={@UniqueConstraint(columnNames={"PROJECT_ID","name"})})
@Indexed(index="Samples")
@FullTextFilterDef(name="sampleUser",impl=SampleFilterFactory.class)
public class Sample {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="SAMPLE_ID")
	private Integer id;
	@Fields({
		@Field,
		@Field(name="name_full",analyzer=@Analyzer(impl=KeywordAnalyzer.class))
	})
	@Column(nullable=false)
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
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PROJECT_ID")
	@IndexedEmbedded
	@NotNull
	private Project project;
	@OneToMany(mappedBy="sample",fetch=FetchType.LAZY)
	@ContainedIn
	@Valid
	private Set<Sequence> sequences = new HashSet<Sequence>();
	
	public Sample() {/*...*/}
	
	public Sample(String name, String description, Date creation) {
		this.name = name;
		this.description = description;
		this.creation = creation;
	}
	
	/**
	 * Sample identity is determined solely on its name.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Sample == false) {
			return false;
		}
		
		if (obj == this) {
			return true;
		}
		
		Sample other = (Sample)obj;
		return new EqualsBuilder()
			.append(getName(), other.getName())
			.isEquals();		
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

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreation() {
		return creation;
	}
	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}

	public Set<Sequence> getSequences() {
		return sequences;
	}
	public void setSequences(Set<Sequence> sequences) {
		this.sequences = sequences;
	}
	public void addSequence(Sequence sequence) {
		getSequences().add(sequence);
		sequence.setSample(this);
	}

}
