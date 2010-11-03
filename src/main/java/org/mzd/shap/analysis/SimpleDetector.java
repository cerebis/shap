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
package org.mzd.shap.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.FeatureType;
import org.mzd.shap.domain.LargeString;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.SequenceException;
import org.mzd.shap.exec.ExecutableException;
import org.mzd.shap.io.ParserException;

@Entity
@DiscriminatorValue(value="SIMPLE_DETECTOR")
public class SimpleDetector extends SimpleAnalyzer<Sequence,Feature> implements Detector {
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(7,17)
			.append(getName())
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SimpleDetector == false) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		SimpleDetector other = (SimpleDetector)obj;
		return new EqualsBuilder()
			.append(getName(),other.getName())
			.isEquals();
	}
	
	@Override
	protected File stageInputFile(Sequence... target) throws ExecutableException {
		
		try {
			File input = super.stageInputFile(target);
			// TODO We probably need to check fasta header
			for (Sequence s : target) {
				s.toFastaFile(input, true);
			}
			return input;
		}
		catch (IOException ex) {
			throw new ExecutableException(ex);
		}
	}
	
	protected int countExistingFeatures(FeatureType resultType, Sequence target) {
		int typeCount = 0;
		for (Feature f : target.getFeatures()) {
			if (resultType.equals(f.getType())) {
				typeCount++;
			}
		}
		return typeCount;
	}
	
	@Override
	protected Feature[] parseOutput(File output, Sequence... target) throws ExecutableException {
		try {
			DetectorParser parser = (DetectorParser)createParser();
			
			if (target.length > 1 && !parser.supportsBatching()) {
				throw new ParserException("[" + parser + "] does not support batching");
			}
			
			Map<String,Collection<Feature>> resultMap = parser.parse(output);

			List<Feature> results = new ArrayList<Feature>();
			for (Sequence s : target) {
				
				Collection<Feature> featureList = resultMap.get(s.getQueryId());
				
				if (featureList == null) {
					getLogger().warn("No features found for sequence [" + s.getQueryId() 
							+ "] when parsed by [" + getParserClass().getName() + "]");
					continue;
				}
				
				for (Feature f : featureList) {
					f.setSequence(s);
					f.setDetector(this);
					if (f.getType().isTranslated()) {
						f.setData(new LargeString(s.translate(f.getLocation())));
					}
				}
				
				results.addAll(featureList);
			}
			
			return results.toArray(new Feature[0]);
		}
		catch (SequenceException ex) {
			throw new ExecutableException(ex);
		}
		catch (ParserException ex) {
			throw new ExecutableException(ex);
		}
	}

}
 