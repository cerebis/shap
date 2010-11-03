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
import java.util.List;

import org.mzd.shap.domain.DataAccessException;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.SequenceException;
import org.mzd.shap.domain.Taxonomy;

public interface TableWriter {

	/**
	 * Write out all {@link Feature}S in collection as a table.
	 * 
	 * @param features - the source features
	 * @param outputFile - the destination file
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws SequenceException
	 */
	void writeFeatures(Collection<Feature> features, File outputFile, boolean append) 
		throws IOException, DataAccessException, SequenceException;
	
	/**
	 * Write out all {@link Feature}S associated with the given {@link Sequence}.
	 * 
	 * @param sequence - the sequence source
	 * @param outputFile - the destination file
	 * @param append - write out in append mode
	 * @throws IOException
	 * @throws DataAccessException
	 */
	void writeFeatures(Sequence sequence, File outputFile, boolean append) 
		throws IOException, DataAccessException;
	
	/**
	 * Write out all {@link Features}S for an entire sample as a single table.
	 * 
	 * @param sample - the sample source
	 * @param excludedTaxa - exclude these taxa from table
	 * @param outputFile - the destination file
	 * @param append - write in append mode
	 * @throws IOException
	 * @throws DataAccessException
	 */
	void writeFeatures(Sample sample, List<Taxonomy> excludedTaxa, File outputFile, boolean append) 
		throws IOException, DataAccessException;
	
	/**
	 * Write out all {@link Feature}S associated with a given {@link Sequence}
	 * 
	 * @param sequence
	 * @param output
	 * @throws IOException
	 * @throws DataAccessException
	 */
	void writeFeatures(Sequence sequence, Writer output) throws IOException, DataAccessException;
	
	void writeFeatures(Collection<Feature> features, Writer output) throws IOException, DataAccessException, SequenceException;
	
	void writeFeatures(Sample sample, List<Taxonomy> excludedTaxa, Writer output)  throws IOException, DataAccessException;
}
