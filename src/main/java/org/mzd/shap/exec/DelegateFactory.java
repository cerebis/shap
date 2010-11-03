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
package org.mzd.shap.exec;

/**
 * An interface for creating new instances of an implementation of {@link Delegate}.
 * <p>
 * Generally these should be treated as non-reuseable objects.
 * <p>
 * Using method injection via Spring, implementing this interface provides
 * a simple means of configuring a factory object using IoC. 
 *   
 */
public interface DelegateFactory {

	/**
	 * Create a new instance of an implementation of Delegate.
	 * 
	 * @return a new {@link Delegate}
	 */
	Delegate newDelegate();
}
