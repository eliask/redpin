/**
 *  Filename: BluetoothReadingHome.java (in org.redpin.server.standalone.db.homes)
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
 *  (c) Copyright ETH Zurich, Pascal Brogle, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */
package org.redpin.server.standalone.db.homes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.redpin.server.standalone.core.measure.BluetoothReading;

/**
 * @see EntityHome
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class BluetoothReadingHome extends EntityHome<BluetoothReading> {
	
	private static final String[] TableCols = {"friendlyName", "bluetoothAddress", "majorDeviceClass", "minorDeviceClass"};
	private static final String TableName = "bluetoothreading"; 
	private static final String TableIdCol = "bluetoothReadingId";

	public BluetoothReadingHome() {
		super();
	}

	/**
	 * @see EntityHome#getTableName()
	 */
	@Override
	protected String getTableName() {
		return TableName;
	}
	
	/**
	 * @see EntityHome#getTableIdCol()
	 */
	@Override
	protected String getTableIdCol() {
		return TableIdCol;
	}
	
	/**
	 * @see EntityHome#getTableCols()
	 */
	@Override
	protected String[] getTableCols() {
		return TableCols;
	}

	/**
	 * @see EntityHome#getColValues(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	protected Object[] getColValues(BluetoothReading e) {
		Object[] res = new Object[getTableCols().length];
		res[0] = e.getFriendlyName();
		res[1] = e.getBluetoothAddress();
		res[2] = e.getMajorDeviceClass();
		res[3] = e.getMinorDeviceClass();
		return res;
	}
	
	/**
	 * @see EntityHome#parseResultRow(ResultSet)
	 */
	@Override
	protected BluetoothReading parseResultRow(ResultSet rs) throws SQLException{
		BluetoothReading reading = new BluetoothReading();
		
		try {
			reading.setId(rs.getInt(1));
			reading.setFriendlyName(rs.getString(2));
			reading.setBluetoothAddress(rs.getString(3));
			reading.setMajorDeviceClass(rs.getString(4));
			reading.setMinorDeviceClass(rs.getString(5));		
		
		} catch (SQLException e) {
			log.log(Level.SEVERE, "parseResultRow failed: " + e.getMessage(), e);
			throw e;
		}
		
		return reading;
	}

}
