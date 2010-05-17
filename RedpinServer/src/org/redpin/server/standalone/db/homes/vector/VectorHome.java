/**
 *  Filename: VectorHome.java (in org.redpin.server.standalone.db.homes.vector)
 *  This file is part of the Redpin project.
 * 
 *  Redpin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  Redpin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Redpin. If not, see <http://www.gnu.org/licenses/>.
 *
 *  (c) Copyright ETH Zurich, Luba Rogoleva, Pascal Brogle, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */
package org.redpin.server.standalone.db.homes.vector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.redpin.server.standalone.core.ReadingInMeasurement;
import org.redpin.server.standalone.db.HomeFactory;
import org.redpin.server.standalone.db.IEntity;
import org.redpin.server.standalone.db.homes.EntityHome;
import org.redpin.server.standalone.util.Log;

/**
 * Abstract class which provides support for vector homes
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 *
 * @param <E> Entity which is contained by the vector
 */

abstract public class VectorHome<E extends IEntity<Integer>>  {
	
	private Logger log;

	public VectorHome() {
		this.log = Log.getLogger();
	}

	
	/**
	 * Gets the contained object class name. this is needed for backwards compability
	 * 
	 * @return The contained object class name
	 */
	abstract public String getContainedObjectClassName();
	
	/**
	 * Gets the contained object entity home
	 * @return Contained object entity home
	 */
	abstract public EntityHome<E> getObjectHome();
	

	

	/**
	 * @see EntityHome#parseResultRow(ResultSet)
	 */
	public Vector<E> parseResultRow(ResultSet rs, int fromIndex) throws SQLException {
		Vector<E> v = new Vector<E>();
		
		try {
			int measurementId = rs.getInt("measurementId");
			v.add(getObjectHome().parseResultRow(rs, fromIndex));
			String readingClassName = rs.getString("readingClassName");
			while (rs.next() && measurementId == rs.getInt("measurementId") && readingClassName.equals(rs.getString("readingClassName"))) {
				v.add(getObjectHome().parseResultRow(rs, fromIndex));
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, " parseResultRow failed: " + e.getMessage(), e);
			throw e;
		}
		
		return v;
	}
	
	/**
	 * execute an insert for all readings in the given vector
	 * 
	 * @param vps {@link PreparedStatement} {@link Vector}
	 * @param v entity {@link Vector}
	 * @param foreignKeyId foreign key
	 * @throws SQLException
	 */
	public void executeUpdate(Vector<PreparedStatement> vps, Vector<E> v, int foreignKeyId) throws SQLException {
		for (E reading : v) {
			int readingId = getObjectHome().executeInsertUpdate(vps, reading);			
			HomeFactory.getReadingInMeasurementHome().executeInsertUpdate(vps, new ReadingInMeasurement(foreignKeyId, readingId, getContainedObjectClassName()));
		}
	}
	
}
