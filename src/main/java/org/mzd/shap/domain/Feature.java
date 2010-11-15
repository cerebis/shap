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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.hibernate.annotations.Index;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.mzd.shap.analysis.Detector;
import org.mzd.shap.hibernate.search.AnalyzerNameBridge;
import org.mzd.shap.hibernate.search.FeatureFilterFactory;
import org.mzd.shap.io.Fasta;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@Entity
@Table(name="Features")
@XStreamAlias("feature")
@Indexed(index="Features")
@FullTextFilterDef(name="featureUser",impl=FeatureFilterFactory.class)
public class Feature {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="FEATURE_ID")
	@XStreamOmitField
	private Integer id;
	@Version
	@XStreamOmitField
	private Integer version;
	@ElementCollection
	@CollectionTable(name="FeatureAliases",joinColumns=@JoinColumn(name="FEATURE_ID"),
			uniqueConstraints=@UniqueConstraint(columnNames={"FEATURE_ID","alias"}))
	@Column(name="alias")
	@IndexedEmbedded
	@XStreamImplicit(itemFieldName="alias")
	private Set<String> aliases = new HashSet<String>();
	@Embedded
	@IndexedEmbedded
	@NotNull
	@Valid
	private Location location = new Location();
	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name="LRGSTR_ID")
	@Valid
	private LargeString data;
	@Field(store=Store.YES)
	@XStreamAsAttribute
	@XStreamAlias("conf")
	private Double confidence;
	@Field(store=Store.YES)
	@XStreamAsAttribute
	private Boolean partial = false;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SEQUENCE_ID")
	@IndexedEmbedded
	@XStreamOmitField
	@NotNull
	private Sequence sequence;
	@OneToMany(mappedBy="feature",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.LAZY)
	@IndexedEmbedded
	@XStreamOmitField
	@Valid
	private Set<Annotation> annotations = new HashSet<Annotation>();
	@ManyToOne(targetEntity=org.mzd.shap.analysis.SimpleDetector.class,fetch=FetchType.LAZY)
	@JoinColumn(name="DETECTOR_ID")
	@Field(store=Store.YES)
	@FieldBridge(impl=AnalyzerNameBridge.class)
	@Analyzer(impl=KeywordAnalyzer.class)
	@XStreamOmitField
	@NotNull
	private Detector detector;
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	@Index(name="feature_type_index")
	@Field(store=Store.YES)
	@Analyzer(impl=KeywordAnalyzer.class)
	@XStreamAsAttribute
	@NotNull
	private FeatureType type = FeatureType.Undefined;
	
	public static Feature newTransferRNAFeature() {
		Feature feat = new Feature();
		feat.setType(FeatureType.TransferRNA);
		return feat;
	}
	
	public static Feature newRibsomalRNAFeature() {
		Feature feat = new Feature();
		feat.setType(FeatureType.RibosomalRNA);
		return feat;
	}
	
	public static Feature newOpenReadingFrameFeature() {
		Feature feat = new Feature();
		feat.setType(FeatureType.OpenReadingFrame);
		return feat;
	}
	
	public static Feature newNonCodingFeature() {
		Feature feat = new Feature();
		feat.setType(FeatureType.NonCoding);
		return feat;
	}

	public static Feature newUndefinedFeature() {
		Feature feat = new Feature();
		feat.setType(FeatureType.Undefined);
		return feat;
	}
	
	public static class AscendingStartComparator implements Comparator<Feature> {
		public int compare(Feature f1, Feature f2) {
			return f1.getLocation().getStart().compareTo(f2.getLocation().getStart());
		}
	}
	
	public static List<Feature> sortAscendingStart(Collection<Feature> unsorted) {
		Feature[] sorted = unsorted.toArray(new Feature[]{});
		Arrays.sort(sorted, new Feature.AscendingStartComparator());
		return Arrays.asList(sorted);
	}

	/**
	 * A verbose header for fasta output. 
	 * 
	 * When a persistence layer is involved (eg. Hibernate), this method should 
	 * only be used when extra detail is necessary. It navigates associations 
	 * which will result in additional queries.
	 * 
	 * @return a verbose header for fasta output.
	 */
	public String verboseFastaHeader() {
		// Create a header from the available fields
		StringBuffer header = new StringBuffer();
		
		header.append(getQueryId());
		
		// A verbose description
		header.append(" " + Fasta.formatAttribute("coords", getLocation().getStart() + ".." + getLocation().getEnd()));
		header.append(" " + Fasta.formatAttribute("strand", getLocation().getStrand().toString()));
		header.append(" " + Fasta.formatAttribute("orf_conf", getConfidence().toString()));
		header.append(" " + Fasta.formatAttribute("partial", isPartial().toString()));

		int n = 0;
		for (Annotation anno : getAnnotations()) {
			if (anno.getDescription() != null) {
				String prefix = String.format("anno%d",++n);
				header.append(" " + Fasta.formatAttribute(prefix, anno.getDescription() + "," 
						+ anno.getConfidence().toString()));
			}
		}
		
		return header.toString();
	}
	
	/**
	 * A very brief fasta header, composed of just the Id or "no_id" if 
	 * the property has not been set. (Eg. a transient instance)
	 * 
	 * @return a brief fasta header.
	 */
	public String getQueryId() {
		StringBuffer header = new StringBuffer();
		header.append("lcl|featureId|" + (getId() == null ? "no_id" : getId()) );
		return header.toString();
	}
	
	/**
	 * Write the the feature to a fasta file. This can be either in the form of DNA or Protein sequence.
	 * <p>
	 * DNA sequences require that a Sequence has been associated with the feature and will cause a database
	 * hit in case of being backed by persistent storage. No attempt to reattach the object to the persistence
	 * mechanism and is left up to the user.
	 * 
	 * @param outputFile - the file to write to
	 * @param moleculeType - whether DNA or protein
	 * @param verbose - whether the header should be verbose or not
	 * @param append - whether the output should be appended to a previously existing file.
	 * @throws FeatureException
	 * @throws SequenceException
	 * @throws IOException
	 */
	public void toFastaFile(
			File outputFile, 
			MoleculeType moleculeType, 
			boolean verbose, 
			boolean append) throws FeatureException, SequenceException, IOException {
		
		toFasta(moleculeType, verbose).write(outputFile, append);
	}
	
	public void toFastaFile(
			BufferedOutputStream outputStream, 
			MoleculeType moleculeType, 
			boolean verbose, 
			boolean append) throws FeatureException, SequenceException, IOException {
		
		toFasta(moleculeType, verbose).write(outputStream);
	}
	
	/**
	 * Create a {@link Fasta} object from the Feature. The object is fully realized in memory
	 * which could result in large memory resource requirements.
	 * 
	 * @param moleculeType
	 * @param verbose
	 * @return the fasta object.
	 * @throws FeatureException
	 * @throws SequenceException
	 */
	public Fasta toFasta(MoleculeType moleculeType, boolean verbose) throws FeatureException, SequenceException {
		
		String header = verbose ? verboseFastaHeader() : getQueryId();

		String data = null;
		switch (moleculeType) {
		case DNA:
			data = getSequence().subSequence(getLocation());
			break;
		case Protein:
			if (!getType().isTranslated()) {
				throw new FeatureException("Attempted to translate non-coding feature");
			}
			data = getData().getValue();
			break;
		}
		
		return new Fasta(header, data);
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id",getId())
			.append("aliases",getAliases())
			.append("location",getLocation())
			.append("data",getData())
			.append("confidence",getConfidence())
			.append("partial", isPartial())
			.append("type", getType())
			.append("sequence",getSequence())
			.append("annotations",getAnnotations())
			.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,37)
			.append(getLocation())
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Feature == false) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		Feature other = (Feature)obj;
		
		return new EqualsBuilder()
			.append(getLocation(), other.getLocation())
			.isEquals();
	}
	
	/*
	 *  Setters/getters
	 */
	
	public Set<Annotation> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}
	public void addAnnotation(Annotation annotation) {
		getAnnotations().add(annotation);
		annotation.setFeature(this);
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}

	public Set<String> getAliases() {
		return aliases;
	}
	public void setAliases(Set<String> aliases) {
		this.aliases = aliases;
	}

	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Sequence getSequence() {
		return sequence;
	}
	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}

	public Double getConfidence() {
		return confidence;
	}
	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}

	public LargeString getData() {
		return data;
	}
	public void setData(LargeString data) {
		this.data = data;
	}

	public Boolean isPartial() {
		return partial;
	}
	public Boolean getPartial() {
		return partial;
	}
	public void setPartial(Boolean partial) {
		this.partial = partial;
	}

	public Detector getDetector() {
		return detector;
	}
	public void setDetector(Detector detector) {
		this.detector = detector;
	}

	public FeatureType getType() {
		return type;
	}
	public void setType(FeatureType type) {
		this.type = type;
	}

}
