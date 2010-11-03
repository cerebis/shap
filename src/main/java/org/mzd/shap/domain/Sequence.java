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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.mzd.shap.hibernate.search.SequenceFilterFactory;
import org.mzd.shap.io.Fasta;
import org.mzd.shap.util.DnaTools;
import org.mzd.shap.util.DnaTools.DnaToolsException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@Entity
@Table(name="Sequences",
		uniqueConstraints={@UniqueConstraint(columnNames={"SAMPLE_ID","name"})})
@Indexed(index="Sequences")
@FullTextFilterDef(name="sequenceUser",impl=SequenceFilterFactory.class)
@XStreamAlias("sequence")
public class Sequence {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="SEQUENCE_ID")
	@XStreamAsAttribute
	private Integer id;
	@Version
	@XStreamOmitField
	private Integer version;
	@Field(store=Store.YES)
	@Analyzer(impl=KeywordAnalyzer.class)
	@XStreamAsAttribute
	@NotNull
	@Size(min=1,max=256)
	private String name;
	@Fields({
		@Field,
		@Field(name="description_full",index=Index.UN_TOKENIZED,store=Store.YES)
	})
	@Type(type="text")
	@XStreamAsAttribute
	@XStreamAlias("desc")
	@Size(min=1,max=1024)
	private String description;
	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name="LRGSTR_ID")
	@Valid
	private LargeString data;
	@OneToMany(mappedBy="sequence",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.LAZY)
	@ContainedIn
	@XStreamImplicit
	@Valid
	private Set<Feature> features = new HashSet<Feature>();
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SAMPLE_ID")
	@IndexedEmbedded
	@XStreamOmitField
	@NotNull
	private Sample sample;
	@Enumerated(EnumType.STRING)
	@Field(store=Store.YES)
	@Analyzer(impl=KeywordAnalyzer.class)
	@XStreamAsAttribute
	@NotNull
	private Taxonomy taxonomy = Taxonomy.UNCLASSIFIED;
	@Field(store=Store.YES)
	@XStreamAsAttribute
	private Double coverage;
	
	/**
	 * Standardized reference ID used in output.
	 * 
	 * This should be unique and include more than simply a surrogate key.
	 *
	 * Eg. "lcl|seqId|name"
	 * 
	 * @return
	 */
	public String getQueryId() {
		return "lcl|sequenceId|" + getId() + "|name|" + getName();
	}
	
	/**
	 * Simple method for creating an instance from a fasta object.
	 * 
	 * The name is composed of the leading non-whitespace string
	 * found within the header. 
	 * 
	 * @param fasta
	 * @return
	 */
	public static Sequence fromFasta(Fasta fasta) {
		Sequence seq = new Sequence();
		seq.setData(new LargeString(fasta.getSequence()));
		String[] fields = fasta.getHeader().split("\\s+");
		seq.setName(fields[0]);
		// Everything after the first space is considered the description.
		String desc = null;
		if (fields.length > 1) {
			desc = fasta.getHeader().substring(fields[0].length()).trim();
		}
		seq.setDescription(desc);
		return seq;
	}
	
	/**
	 * Write to a Fasta file.
	 * 
	 * @param outputFile
	 * @param append
	 * @throws IOException
	 */
	public void toFastaFile(File outputFile, boolean append) throws IOException {
		Writer output = null;
		try {
			output = new BufferedWriter(new FileWriter(outputFile,append));
			writeFasta(output);
		}
		finally {
			if (output != null) {
				output.close();
			}
		}
	}
	
	/**
	 * Output this instance in {@link Fasta} format to the output {@link Writer}.
	 * 
	 * @param output
	 * @param append TODO
	 * @throws IOException
	 */
	public void writeFasta(Writer output) throws IOException {
		// Prepare a header string and write it.
		String header = getQueryId();
		
		if (getDescription() != null) {
			header += " " + getDescription() + " /taxonomy=" + getTaxonomy();
		}
		output.write('>' + header + '\n');
		
		Reader dataReader = null; 
		try {
			dataReader = getDataStream();
		
			char[] buff = new char[80];
			while (true) {
				int nch = dataReader.read(buff);
				if (nch == -1) {
					break;
				}
				output.write(buff, 0, nch);
				output.write('\n');
			}
		}
		finally {
			if (dataReader != null) {
				dataReader.close();
			}
		}
	}

	/**
	 * Translate to protein sequence.
	 * <p>
	 * Since biojava throws an exception if the supplied string is not
	 * evenly divisible by 3, we drop extraneous bases prior to 
	 * translation. This can occur if locations corresponding to ORFs 
	 * are only partial (not containing either a start or stop site).
	 *
	 * @param location
	 * @return amino acid sequence as a string
	 * @throws SequenceException
	 */
	public String translate(Location location) throws SequenceException {
		try {
			Location mteLoc = location.maximalTranslatableExtent();
			return DnaTools.translate(subSequence(mteLoc));
		}
		catch (LocationException ex) {
			throw new SequenceException("Exception translating sequence", ex);
		}
		catch (DnaToolsException ex) {
			throw new SequenceException("Exception translating sequence", ex);
		}
	}
	
	/**
	 * Get the sequence for specified the location.
	 * 
	 * @param location - the location from which to extract the sequence.
	 * @return the subsequence.
	 * @throws SequenceException
	 */
	public String subSequence(Location location) throws SequenceException {
		try {
			String subseq = location.getExtent(getData().getValue());
			return location.isReverseStrand() ? DnaTools.reverseComplement(subseq) : subseq;
		}
		catch (DnaToolsException ex) {
			throw new SequenceException("Exception getting subsequence", ex);
		}
	}
	
	public LargeString getData() {
		return data;
	}
	public void setData(LargeString data) {
		this.data = data;
	}
	
	public Reader getDataStream() {
		return new StringReader(getData().getValue());
	}
	
	public long getDataLength() {
		return getData().length();
	}
	
	public Set<Feature> getFeatures() {
		return features;
	}
	public void setFeatures(Set<Feature> features) {
		this.features = features;
	}
	public void addFeature(Feature feature) {
		getFeatures().add(feature);
		feature.setSequence(this);
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

	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}

	public Sample getSample() {
		return sample;
	}
	public void setSample(Sample sample) {
		this.sample = sample;
	}
	
	public Taxonomy getTaxonomy() {
		return taxonomy;
	}
	public void setTaxonomy(Taxonomy taxonomy) {
		this.taxonomy = taxonomy;
	}

	public Double getCoverage() {
		return coverage;
	}
	public void setCoverage(Double coverage) {
		this.coverage = coverage;
	}

}
