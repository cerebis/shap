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

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.hibernate.search.annotations.Field;
import org.hibernate.validator.constraints.Range;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * A class representing a location along a contiguous piece of DNA. Normally this would
 * be a genome, but any stretch of DNA with identifiable features would suffice. 
 * <p>
 * TODO
 * <p>
 * We should consider subclassing Location to support:
 * 1) position
 * 2) position + strand
 * 3) position + strand + frame
 * <p>
 * Currently we cludge the lack of strand or frame information.
 * 
 */
@Embeddable
public class Location {
	private static Logger logger = Logger.getLogger(Location.class);
	@Field
	@XStreamAsAttribute
	@Min(0)
	@NotNull
	private Integer start;
	@Field
	@Column(name="end_")
	@XStreamAsAttribute
	@Min(0)
	@NotNull
	private Integer end;
	@Field
	@Enumerated(EnumType.STRING)
	@XStreamAsAttribute
	@NotNull
	@Valid
	private Strand strand;
	@Field
	@XStreamAsAttribute
	@Range(min=0,max=2)
	@NotNull
	private Integer frame;
	
	public String getExtent(String sequence) {
		if (sequence.length() == getEnd()) {
			getLogger().warn("Likely off-by-one error. Requested a substring one greater than string length");
			return sequence.substring(getStart(), getEnd());
		}
		else {
			return sequence.substring(getStart(), getEnd()+1);
		}
	}
	
	public boolean independent(Location other) {
		return getEnd() < other.getStart() || getStart() > other.getEnd();
	}
	
	public boolean isReverseStrand() {
		return getStrand() == Strand.Reverse;
	}
	
	/**
	 * @return The length of this location
	 */
	public int length() {
		return getEnd() - getStart() + 1;
	}
	
	/**
	 * Adjust the 3-prime end of the location by the specified
	 * value.
	 * 
	 * @param change - the relative change of the 3-prime end.
	 */
	public void adjust3PrimeEnd(int change) {
		getStrand().adjust3PrimeEnd(this, change);
	}
	
	/**
	 * Adjust the 5-prime end of the location by the specified
	 * value.
	 * 
	 * @param change - the relative change of the 5-prime end.
	 */
	public void adjust5PrimeEnd(int change) {
		getStrand().adjust5PrimeEnd(this, change);
	}
	
	/**
	 * Bases left over when the length is taken mod 3.
	 * Effectively, the number of bases left over after
	 * the extent is grouped into codons.
	 * <p>
	 * For valid open reading frames, this should return 0.
	 *  
	 * @return the number of "extra" bases.
	 */
	public int getExtraBases() {
		return length() % 3;
	}
	
	/**
	 * Return the maximally translatable extent of this location.
	 * <p>
	 * This is the longest extent which is divisible by 3 and
	 * maintains the original frame. This is usually only called
	 * for in cases where predicted ORFs are incomplete (Metagene). 
	 * 
	 * @return the maximally translatable extent.
	 * @throws LocationException
	 */
	public Location maximalTranslatableExtent() throws LocationException {
		Location mte = new Location(getStart(),getEnd(),getStrand(),getFrame());
		if (getExtraBases() > 0) {
			switch (getStrand()) {
			case Forward:
				mte.adjust5PrimeEnd(mte.getExtraBases());
				break;
			case Reverse:
				mte.adjust3PrimeEnd(-mte.getExtraBases());
				break;
			}
		}
		return mte;
	}
	
	@Override
	public String toString() {
		return 
			getStart() + ".." + getEnd() + " " + 
			getStrand().toString() + 
			(getFrame() == null ? "" : " " + getFrame());
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,37)
			.append(getStart())
			.append(getEnd())
			.append(getStrand())
			.append(getFrame())
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Location == false) {
			return false;
		}
		
		if (obj == this) {
			return true;
		}
		
		Location other = (Location)obj;
		
		return new EqualsBuilder()
			.append(getStart(), other.getStart())
			.append(getEnd(), other.getEnd())
			.append(getStrand(), other.getStrand())
			.append(getFrame(), other.getFrame())
			.isEquals();
			
	}
	
	public Location() {/*...*/}
	
	/**
	 * Constructor. A location cannot have zero extent (IE start != end).
	 * 
	 * @param start - start relative to genomic forward strand
	 * @param end - end relative to genomic forward strand
	 * @param strand - the location's strand
	 * @param frame - the location's frame
	 * @throws LocationException - thrown when start==end.
	 */
	public Location(Integer start, Integer end, Strand strand, Integer frame) throws LocationException {
		this.start = start;
		this.end = end;
		this.strand = strand;
		this.frame = frame;
		validate();
	}
	
	public void validate() throws LocationException {
		if (start.equals(end)) {
			throw new LocationException("Location has zero extent");
		}
	}
	
	/**
	 * Create an instance of Location which is relative to the forward stranded
	 * genomic origin.
	 * <p>
	 * External tools tend to disagree on whether genomic coordinates should
	 * always be specified relative to the forward strand or relative to the
	 * strand on which the feature exists. This method is here to provide a
	 * convenient means of creating a consistent representation.
	 * <p>
	 * The strand parameter is technically not necessary to create an instance
	 * of Location in this situation (implied by values of 5' and 3'), but it is 
	 * included for sanity checking and cannot be null.  
	 * 
	 * @param fivePrime - the 5-prime end of the feature.
	 * @param threePrime - the 3-prime end of the feature.
	 * @param strand - the strand of the feature.
	 * @param frame - the frame of the feature
	 * @return instance of Location relative to the genomic forward strand.
	 * @throws LocationException
	 */
	public static Location createForwardLocation(
			Integer fivePrime, 
			Integer threePrime, 
			Strand strand, 
			Integer frame) throws LocationException {
		
		// Forward strand
		if (fivePrime < threePrime) {
			if (!strand.equals(Strand.Forward)) {
				throw new LocationException("Implied strandedness conflicts with strand parameter");
			}
			return new Location(fivePrime,threePrime,strand,frame);
		}
		// Reverse
		else if (fivePrime > threePrime) {
			if (!strand.equals(Strand.Reverse)) {
				throw new LocationException("Implied strandedness conflicts with strand parameter");
			}
			return new Location(threePrime,fivePrime,strand,frame);
		}
		// Empty range
		else {
			throw new LocationException("Location has zero extent");
		}
	}
	
	protected Logger getLogger() {
		return logger;
	}
	
	public Integer getEnd() {
		return end;
	}
	public void setEnd(Integer end) {
		this.end = end;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Strand getStrand() {
		return strand;
	}
	public void setStrand(Strand strand) {
		this.strand = strand;
	}
	public Integer getFrame() {
		return frame;
	}
	public void setFrame(Integer frame) {
		this.frame = frame;
	}
}
