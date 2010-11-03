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
package org.mzd.shap.analysis.metagene.bean;

import org.mzd.shap.domain.Location;
import org.mzd.shap.domain.LocationException;
import org.mzd.shap.domain.Strand;
import org.mzd.shap.domain.StrandException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("orf")
public class Orf {
	@XStreamAlias("start")
	@XStreamAsAttribute
	private Integer start;
	
	@XStreamAlias("stop")
	@XStreamAsAttribute
	private Integer stop;
	
	@XStreamAlias("strand")
	@XStreamAsAttribute
	private String strand;
	
	@XStreamAlias("frame")
	@XStreamAsAttribute
	private Integer frame;
	
	@XStreamAlias("conf")
	@XStreamAsAttribute
	private Double confidence;
	
	@XStreamAlias("partial")
	@XStreamAsAttribute
	private Boolean partial;
	
	@XStreamAlias("model")
	@XStreamAsAttribute
	private Domain model;
	
	@XStreamOmitField
	private Integer rbsStart;
	@XStreamOmitField
	private Integer rbsStop;
	@XStreamOmitField
	private Double rbsScore;
	
	/**
	 * Create an instance of {@link Location} from this instance of {@link Orf}.
	 * <p>
	 * This method encapsulates the details needed to standardize the genomic
	 * location of this feature from Metagene output.
	 * 
	 * @return a {@link Location}
	 * @throws LocationException
	 * @throws StrandException
	 */
	public Location createLocation() throws LocationException, StrandException {
		
		Integer start = getStart();
		Integer stop = getStop();
		Strand strand = Strand.getInstance(getStrand());
		
		/*
		 * Convert frame to a more standard definition.
		 */
		Integer frame = null;
		switch (strand) {
		case Forward:
			start += getFrame();
			frame = (start - 1) % 3;
			break;
		case Reverse:
			stop -= getFrame();
			frame = (stop + 1) % 3;
			break;
		}
		
		Location standardizedLocation = new Location(start-1,stop-1,strand,frame);

		int extra3p = standardizedLocation.getExtraBases();
		if (extra3p > 0) {
			standardizedLocation.adjust3PrimeEnd(-extra3p);
		}
		
		return standardizedLocation;
	}
	
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getStop() {
		return stop;
	}
	public void setStop(Integer stop) {
		this.stop = stop;
	}

	public String getStrand() {
		return strand;
	}
	public void setStrand(String strand) {
		this.strand = strand;
	}

	public Integer getFrame() {
		return frame;
	}
	public void setFrame(Integer frame) {
		this.frame = frame;
	}

	public Double getConfidence() {
		return confidence;
	}
	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}

	public Boolean getPartial() {
		return partial;
	}
	public void setPartial(Boolean partial) {
		this.partial = partial;
	}

	public Integer getRbsStart() {
		return rbsStart;
	}
	public void setRbsStart(Integer rbsStart) {
		this.rbsStart = rbsStart;
	}

	public Integer getRbsStop() {
		return rbsStop;
	}
	public void setRbsStop(Integer rbsStop) {
		this.rbsStop = rbsStop;
	}

	public Double getRbsScore() {
		return rbsScore;
	}
	public void setRbsScore(Double rbsScore) {
		this.rbsScore = rbsScore;
	}

	public Domain getModel() {
		return model;
	}
	public void setModel(Domain model) {
		this.model = model;
	}
}
