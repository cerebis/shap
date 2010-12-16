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

import javax.persistence.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.mzd.shap.analysis.Annotator;

@Entity
@Table(name="Annotations")
public class Annotation {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ANNOTATION_ID")
	private Integer id;
	@Field
	@Analyzer(impl=KeywordAnalyzer.class)
	@Size(min=1,max=255)
	@NotNull
	private String accession;
	@Field
	@Type(type="text")
	@Size(min=1,max=4095)
	@NotNull
	private String description;
	@Field
	private Double confidence;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="FEATURE_ID")
	@Index(name="annotation_feature")
	@NotNull
	@ContainedIn
	private Feature feature;
	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name="ALIGNMENT_ID")
	@Valid
	private Alignment alignment;
	@ManyToOne(targetEntity=org.mzd.shap.analysis.SimpleAnnotator.class,fetch=FetchType.LAZY)
	@JoinColumn(name="ANNOTATOR_ID")
	@NotNull
	private Annotator annotator;
	@Enumerated(EnumType.STRING)
	@Index(name="annotation_refersto_index")
	@Field
	@NotNull
	private AnnotationType refersTo;
	
	public static Annotation newProductAnnotation() {
		Annotation anno = new Annotation();
		anno.setRefersTo(AnnotationType.Product);
		return anno;
	}
	
	public static Annotation newGeneAnnotation() {
		Annotation anno = new Annotation();
		anno.setRefersTo(AnnotationType.Gene);
		return anno;
	}

	public static Annotation newSiteAnnotation() {
		Annotation anno = new Annotation();
		anno.setRefersTo(AnnotationType.Site);
		return anno;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id",getId())
			.append("accession",getAccession())
			.append("description",getDescription())
			.append("confidence",getConfidence())
			.append("refersTo",getRefersTo())
			.append("alignment",getAlignment())
			.toString();
	}
	
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
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
	
	public Feature getFeature() {
		return feature;
	}
	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public Double getConfidence() {
		return confidence;
	}
	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}

	public Alignment getAlignment() {
		return alignment;
	}
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

	public Annotator getAnnotator() {
		return annotator;
	}
	public void setAnnotator(Annotator annotator) {
		this.annotator = annotator;
	}

	public AnnotationType getRefersTo() {
		return refersTo;
	}
	public void setRefersTo(AnnotationType refersTo) {
		this.refersTo = refersTo;
	}
		
}
