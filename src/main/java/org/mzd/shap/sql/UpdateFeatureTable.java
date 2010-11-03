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
package org.mzd.shap.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * The sole purpose of this class is to update the Features table to accomodate the change made
 * to the domain structure. It is done using the plain Java SQL objects.
 * <p>
 * Sequences not store their respective Features as a List rather than a Set. This allows for more
 * sensible means of assigning locus tags to the related Features (IE. using the list index).
 * <p>
 * In making the change between Set and List, Hibernate adds an index column to the Features table.
 * We must then add the indexes after the fact, so that Hibernate can successfully populate the list
 * from persistent storage.
 * <p>
 * The list order is in ascending Feature start position.
 *
 */
public class UpdateFeatureTable {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws SQLException {

		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://localhost/BBay01a");
		ds.setUsername("test");
		ds.setPassword("xeno12");
		
		Connection conn = ds.getConnection();

		// Get the Sequence ID list.
		PreparedStatement select = null;
		PreparedStatement update = null;
		ResultSet result = null;
		
		select = conn.prepareStatement("SELECT DISTINCT SEQUENCE_ID FROM Features");
		result = select.executeQuery();
		List<Integer> sequenceIds = new ArrayList<Integer>();
		result.beforeFirst();
		while (result.next()) {
			sequenceIds.add(result.getInt(1));
		}
		result.close();
		select.close();
		
		
		// Get the list of Feature IDs for this Sequence ID
		select = conn.prepareStatement("SELECT FEATURE_ID FROM Features WHERE SEQUENCE_ID=? ORDER BY start");
		
		// Update the idx column for this feature id
		update = conn.prepareStatement("UPDATE Features SET featureOrder=? where FEATURE_ID=?");
		
		for (Integer seqId : sequenceIds) {
			select.setInt(1, seqId);
			result = select.executeQuery();
			
			int n = 0;
			result.beforeFirst();
			while (result.next()) {
				int featId = result.getInt(1);
				update.setInt(1, n++);
				update.setInt(2, featId);
				if (update.executeUpdate() != 1) {
					System.out.println("Failed update [" + update.toString() + "]");
					throw new SQLException();
				}
			}
			result.close();
		}

		update.close();
		select.close();
		
		conn.close();
		ds.close();
	}

}
