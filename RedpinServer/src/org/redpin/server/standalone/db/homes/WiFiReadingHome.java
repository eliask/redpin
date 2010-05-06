/**
 *  Filename: WiFiReadingHome.java (in org.redpin.server.standalone.db.homes)
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

import org.redpin.server.standalone.core.measure.WiFiReading;

/**
 * @see EntityHome
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class WiFiReadingHome extends EntityHome<WiFiReading> {

	
	private static final String[] TableCols = {"bssid", "ssid", "rssi", "wepEnabled", "isInfrastructure"};
	private static final String TableName = "wifireading"; 
	private static final String TableIdCol = "wifiReadingId";
	
	public WiFiReadingHome() {
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
	protected Object[] getColValues(WiFiReading e) {
		Object[] res = new Object[getTableCols().length];
		res[0] = e.getBssid();
		res[1] = e.getSsid();
		res[2] = e.getRssi();
		res[3] = e.isWepEnabled();
		res[4] = e.isInfrastructure();
		return res;
	}
	
	/**
	 * @see EntityHome#parseResultRow(ResultSet)
	 */
	@Override
	protected WiFiReading parseResultRow(ResultSet rs) throws SQLException{
		WiFiReading reading = new WiFiReading();
		
		try {
			reading.setId(rs.getInt(1));
			reading.setBssid(rs.getString(2));
			reading.setSsid(rs.getString(3));
			reading.setRssi(rs.getInt(4));
			reading.setWepEnabled(rs.getBoolean(5));
			reading.setInfrastructure(rs.getBoolean(6));
			
		
		} catch (SQLException e) {
			log.log(Level.SEVERE, "parseResultRow failed: " + e.getMessage(), e);
			throw e;
		}
		
		return reading;
	}
	
	

}
