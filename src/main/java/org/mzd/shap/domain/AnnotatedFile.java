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

import java.io.File;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Allows a File to have a small amount of metadata associated with it.
 * <p>
 * The purpose is to better support the definition of properties
 * associated with filesystem objects where changes at the filesystem level 
 * may have gnobe unnoticed at the application level while still having a 
 * significant effect.
 * <p>
 * Eg. A new version of an executable or reference datafile.
 * <p>
 * Initially it was intended that an md5sum would be recorded rather than
 * a potentially manually set version property, however outside of a perfectly
 * homogeneous compute environment, this would lead to an unnecessary and
 * potentially problematic degree of descrimination. 
 */
public class AnnotatedFile {
	String name;
	String version;
	File path;

	/**
	 * Convience override to return the filesystem path.
	 */
	@Override
	public String toString() {
		return path.getPath();
	}
	
	/**
	 * Instances are descriminated on {@link #getName()} and {@link #getVersion()}
	 * only by default, not the filesystem path.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AnnotatedFile == false) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		AnnotatedFile other = (AnnotatedFile)obj;
		return new EqualsBuilder()
			.append(getName(),other.getName())
			.append(getVersion(),other.getVersion())
			.isEquals();
	}
	
	/**
	 * Instances are descriminated on {@link #getName()} and {@link #getVersion()}
	 * only by default, not the filesystem path.
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,31)
			.append(getName())
			.append(getVersion())
			.toHashCode();
	}

	/**
	 * A reference to the actual file on the filesystem.
	 * @return
	 */
	public File getPath() {
		return path;
	}
	public void setPath(File path) {
		this.path = path;
	}

	/**
	 * An abstract name, not necessarily associated with the associated File instance.
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * An abstract version string. This could be the version of an executable or not.
	 */
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
}
