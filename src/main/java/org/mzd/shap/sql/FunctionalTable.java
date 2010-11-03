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

import java.io.FileReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.commons.dbcp.BasicDataSource;

public class FunctionalTable {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		if (args.length != 2) {
			throw new Exception("Usage: [func_cat.txt] [cog-to-cat.txt]");
		}
		
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://localhost/Dummy");
		ds.setUsername("test");
		ds.setPassword("xeno12");
		
		Connection conn = ds.getConnection();

		// Get the Sequence ID list.
		PreparedStatement insert = null;
		LineNumberReader reader = null;

		try {
			reader = new LineNumberReader(new FileReader(args[0]));
			
			insert = conn.prepareStatement("INSERT INTO CogCategories (code,function,class) values (?,?,?)");
			
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				
				String[] fields = line.split("\t");
				if (fields.length != 3) {
					throw new Exception("Bad number of fields [" + fields.length 
							+ "] for line [" + line + "]");
				}
				
				insert.setString(1, fields[0]);
				insert.setString(2, fields[1]);
				insert.setString(3, fields[2]);
				insert.executeUpdate();
			}
			
			insert.close();
			
			reader.close();
			
			reader = new LineNumberReader(new FileReader(args[1]));
			
			insert = conn.prepareStatement("INSERT INTO CogFunctions (accession,code) values (?,?)");
			
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				
				String[] fields = line.split("\\s+");
				if (fields.length != 2) {
					throw new Exception("Bad number of fields [" + fields.length 
							+ "] for line [" + line + "]");
				}
				
				for (char code : fields[1].toCharArray()) {
					insert.setString(1, fields[0]);
					insert.setString(2, String.valueOf(code));
					insert.executeUpdate();
				}
			}
			
			insert.close();
			
		}
		finally {
			if (reader != null) {
				reader.close();
			}

			conn.close();
			ds.close();
		}
	}

}
