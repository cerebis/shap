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

import java.util.Random;

public class RandomUtil {
	private static ThreadLocal<Random> localRandom = new ThreadLocal<Random>() {
		@Override
		protected Random initialValue() {
			return new Random(System.currentTimeMillis());
		};
	};
	
	/**
	 * Get a random capital character (ASCII Code).
	 * 
	 * @return random char
	 */
	public static char getCapitalChar() {
		return (char)(localRandom.get().nextInt(26) + 65);
	}
	
	/**
	 * Get a random string of capital characters (ASCII Code).
	 * 
	 * @param length - the length of string to create.
	 * @return random string
	 */
	public static String getCapitalString(int length) {
		char[] ch = new char[length];
		for (int i=0; i<length; i++) {
			ch[i] = getCapitalChar();
		}
		return new String(ch);
	}
	
}
