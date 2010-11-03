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
package org.mzd.shap.util;

import java.io.File;
import java.io.IOException;

import org.mzd.shap.util.RandomUtil;


public class TemporaryFileFactory {
	private static final int MIN_LENGTH = 3;
	private static final int PADDING_LENGTH = 4;
	private String padding;

	public TemporaryFileFactory() {
		this.padding = RandomUtil.getCapitalString(PADDING_LENGTH);
	}
	
	/**
	 * Temporary file prefixes and suffixes must be at least 3 characters long.
	 * This method simply adds a random padding to any short strings.
	 * 
	 * @param base - string to check for sufficient length.
	 * @return a sufficiently long string (could be the same as input).
	 */
	protected String sufficientString(String base) {
		if (base.length() < MIN_LENGTH) {
			return base + padding;
		}
		else {
			return base;
		}
	}

	/**
	 * Robust method for creating temporary files from any prefix and suffix.
	 * (Checks that these are >= 3 characters and if not lengthens them)
	 * <p>
	 * The method is synchronized to help prevent the possibility that concurrent
	 * threads would simultaneously create the same temporary file.
	 * 
	 * TODO: This is probably exceedingly unlikely and we would be better to add
	 * additional randomization to the names and retry on exceptions.
	 * 
	 * @param prefix - the prefix to use
	 * @param suffix - the suffix to use
	 * @param path - the path to use.
	 * @return a temporary files under 'path'
	 * @throws IOException
	 */
	public synchronized File createTemporaryFile(String prefix, String suffix, File path) throws IOException {
		return File.createTempFile(sufficientString(prefix), sufficientString(suffix), path);
	}
	
}
