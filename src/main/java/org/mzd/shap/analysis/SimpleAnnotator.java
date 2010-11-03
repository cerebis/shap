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
import java.util.List;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.FeatureException;
import org.mzd.shap.domain.FeatureType;
import org.mzd.shap.domain.MoleculeType;
import org.mzd.shap.domain.SequenceException;
import org.mzd.shap.exec.ExecutableException;
import org.mzd.shap.io.ParserException;

/**
 * A simple {@link Annotator} where the target class type is {@link Feature} and the result
 * is a collection of type {@link Annotation}.
 * <p>
 * Features are written to the input file in Fasta format.
 * <p>
 * The resulting output file is handled by implementations of {@link AnnotatorParser}
 * pertinent to the tool used for analysis.
 *
 */
@Entity
@DiscriminatorValue(value="SIMPLE_ANNOTATOR")
public class SimpleAnnotator extends SimpleAnalyzer<Feature,Annotation> implements Annotator {
	@Enumerated(EnumType.STRING)
	@NotNull
	private FeatureType supportedFeatureType;
	@Enumerated(EnumType.STRING)
	@NotNull
	private MoleculeType supportedMoleculeType;
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(7,17)
			.append(getName())
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SimpleAnnotator == false) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		SimpleAnnotator other = (SimpleAnnotator)obj;
		return new EqualsBuilder()
			.append(getName(),other.getName())
			.isEquals();
	}

	/**
	 * Write the Feature to the input file in Fasta format.
	 * <p>
	 * TODO the format should probably be retargetable since not every tool will
	 * necessarily accept Fasta format. This could be done in a similar way
	 * to Parser.
	 */
	@Override
	protected File stageInputFile(Feature... target) throws ExecutableException {
		try {
			File input = super.stageInputFile(target);
			for (Feature f : target) {
				if (f.getType() != getSupportedFeatureType()) {
					String msg = "Target feature [" + target + "] is not of supported " +
					"featureType [" + getSupportedFeatureType() + "]";
					getLogger().error(msg);
					throw new ExecutableException(msg);
				}
	
				f.toFastaFile(
						input, 
						getSupportedMoleculeType(), 
						false, true);
				
			}
			return input;
		}
		catch (IOException ex) {
			throw new ExecutableException(ex);
		}
		catch (FeatureException ex) {
			throw new ExecutableException(ex);
		}
		catch (SequenceException ex) {
			throw new ExecutableException(ex);
		}
	}

	/**
	 * Instantiate the parser and parse the output file, returning the result.
	 */
	@Override
	protected Annotation[] parseOutput(File output, Feature... target) throws ExecutableException {
		try {
			AnnotatorParser parser = (AnnotatorParser)createParser();
			
			if (target.length > 1 && !parser.supportsBatching()) {
				throw new ParserException("[" + parser + "] does not support batching");
			}
			
			Map<String,Annotation> resultMap = parser.parse(output);
			
			List<Annotation> results = new ArrayList<Annotation>();
			for (Feature f : target) {
				Annotation a = resultMap.get(f.getQueryId());
				if (a == null) {
					getLogger().debug("No annotation found for feature [" + f.getQueryId() 
							+ "] when parsed by [" + getParserClass().getName() + "]");
					continue;
				}
				a.setAnnotator(this);
				a.setFeature(f);
				results.add(a);
			}
			return results.toArray(new Annotation[0]);
		}
		catch (ParserException ex) {
			throw new ExecutableException(ex);
		}
	}

	public FeatureType getSupportedFeatureType() {
		return supportedFeatureType;
	}
	public void setSupportedFeatureType(FeatureType supportedFeatureType) {
		this.supportedFeatureType = supportedFeatureType;
	}

	public MoleculeType getSupportedMoleculeType() {
		return supportedMoleculeType;
	}
	public void setSupportedMoleculeType(MoleculeType supportedMoleculeType) {
		this.supportedMoleculeType = supportedMoleculeType;
	}

}
