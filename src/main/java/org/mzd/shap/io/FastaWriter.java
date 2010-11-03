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
package org.mzd.shap.io;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import org.mzd.shap.domain.DataAccessException;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.FeatureException;
import org.mzd.shap.domain.MoleculeType;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.SequenceException;
import org.mzd.shap.domain.Taxonomy;

public interface FastaWriter {
	
	/**
	 * Write a Feature to Fasta format.
	 * 
	 * @param feature
	 * @param moleculeType
	 * @param output
	 * @param verbose
	 * @throws IOException
	 * @throws FeatureException
	 * @throws SequenceException
	 */
	void write(Feature feature, MoleculeType moleculeType, Writer output, boolean verbose) 
		throws IOException, FeatureException, SequenceException;
	
	/**
	 * Write a Feature to Fasta format.
	 * 
	 * @param feature - the feature to write.
	 * @param outputPath - the path to write the fasta file to.
	 * @param verbose - should the fasta header be verbose (creates additional hits on database).
	 * @param append - should the fasta files append to a single file.
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws FeatureException
	 * @throws SequenceException
	 */
	void write(Feature feature, MoleculeType moleculeType, File outputPath, boolean verbose, boolean append) 
		throws IOException, FeatureException, SequenceException;
	
	/**
	 * Write the collection of Features to Fasta format.
	 * 
	 * @param features
	 * @param moleculeType
	 * @param output
	 * @param verbose
	 * @throws IOException
	 * @throws FeatureException
	 * @throws SequenceException
	 */
	void writeFeatures(Collection<Feature> features, MoleculeType moleculeType, Writer output, boolean verbose) 
		throws IOException, FeatureException, SequenceException;

	/**
	 * Write the collection of Features to Fasta format.
	 * 
	 * @param features
	 * @param outputPath
	 * @param verbose
	 * @param append
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws FeatureException
	 * @throws SequenceException
	 */
	void writeFeatures(Collection<Feature> features, MoleculeType moleculeType, File outputPath, boolean verbose, boolean append) 
		throws IOException, FeatureException, SequenceException;
	
	/**
	 * Write all Features contained in a Sequence to Fasta format.
	 * 
	 * @param sequence
	 * @param moleculeType
	 * @param output
	 * @param verbose
	 * @param append
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws FeatureException
	 * @throws SequenceException
	 */
	void writeFeatures(Sequence sequence, MoleculeType moleculeType, Writer output, boolean verbose)
		throws IOException, DataAccessException, FeatureException, SequenceException;

	/**
	 * Write all features contained in a Sequence to Fasta format.
	 * 
	 * @param sequence
	 * @param moleculeType
	 * @param outputPath
	 * @param verbose
	 * @param append
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws FeatureException
	 * @throws SequenceException
	 */
	void writeFeatures(Sequence sequence, MoleculeType moleculeType, File outputPath, boolean verbose, boolean append)
		throws IOException, DataAccessException, FeatureException, SequenceException;
	
	/**
	 * Write all Features contained in a sample to Fasta format.
	 * 
	 * @param sample
	 * @param moleculeType
	 * @param outputPath
	 * @param verbose
	 * @param append
	 * @param excludedTaxa
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws FeatureException
	 * @throws SequenceException
	 */
	void writeFeatures(Sample sample, MoleculeType moleculeType, Writer output, 
			boolean verbose, Collection<Taxonomy> excludedTaxa)
		throws IOException, DataAccessException, FeatureException, SequenceException;

	/**
	 * Write all Features contained in a sample to Fasta format.
	 * 
	 * @param sample
	 * @param moleculeType
	 * @param outputPath
	 * @param verbose
	 * @param append
	 * @param excludedTaxa
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws FeatureException
	 * @throws SequenceException
	 */
	void writeFeatures(Sample sample, MoleculeType moleculeType, File outputPath, 
			boolean verbose, boolean append, Collection<Taxonomy> excludedTaxa)
		throws IOException, DataAccessException, FeatureException, SequenceException;
	
	/**
	 * Write a Sequence to Fasta format.
	 * 
	 * @param sequence
	 * @param output
	 * @throws IOException
	 */
	void writeSequence(Sequence sequence, Writer output) throws IOException;

	/**
	 * Write a Sequence to Fasta format.
	 * 
	 * @param sequence
	 * @param outputPath
	 * @param append
	 * @param excludedTaxa
	 * @throws IOException
	 */
	void writeSequence(Sequence sequence, File outputPath, boolean append) throws IOException;

	/**
	 * Write all Sequences contained in a Sample to Fasta format.
	 * 
	 * @param sample
	 * @param output
	 * @param excludedTaxa
	 * @throws IOException
	 */
	void writeSequences(Sample sample, Writer output, Collection<Taxonomy> excludedTaxa) throws IOException;

	/**
	 * Write all Sequences contained in a Sample to Fasta format.
	 * 
	 * @param sample
	 * @param outputPath
	 * @param append
	 * @param excludedTaxa
	 * @throws IOException
	 */
	void writeSequences(Sample sample, File outputPath, boolean append, 
			Collection<Taxonomy> excludedTaxa) throws IOException;
}
