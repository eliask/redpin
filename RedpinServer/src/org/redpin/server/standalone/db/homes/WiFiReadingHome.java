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
 *  (c) Copyright ETH Zurich, Luba Rogoleva, Pascal Brogle, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */
package org.redpin.server.standalone.db.homes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;

import org.redpin.server.standalone.core.measure.WiFiReading;

/**
 * @see EntityHome
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * @author Luba Rogoleva (lubar@student.ethz.ch)
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
	 * @see EntityHome#parseResultRow(ResultSet)
	 */
	@Override
	public WiFiReading parseResultRow(final ResultSet rs, int fromIndex) throws SQLException{
		WiFiReading reading = new WiFiReading();
		
		try {
			reading.setId(rs.getInt(fromIndex));
			reading.setBssid(rs.getString(fromIndex + 1));
			reading.setSsid(rs.getString(fromIndex + 2));
			reading.setRssi(rs.getInt(fromIndex + 3));
			reading.setWepEnabled(rs.getBoolean(fromIndex + 4));
			reading.setInfrastructure(rs.getBoolean(fromIndex + 5));
		
		} catch (SQLException e) {
			log.log(Level.SEVERE, "parseResultRow failed: " + e.getMessage(), e);
			throw e;
		}
		
		return reading;
	}
	/**
	 * @see EntityHome#fillInStatement(PreparedStatement, org.redpin.server.standalone.db.IEntity, int)
	 */
	@Override
	public int fillInStatement(PreparedStatement ps, WiFiReading t, int fromIndex)
			throws SQLException {
		return fillInStatement(ps, new Object[] {t.getBssid(), t.getSsid(), t.getRssi(), t.isWepEnabled(), t.isInfrastructure()},   
				new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.TINYINT, Types.TINYINT}, fromIndex);
	}

	
	
	

}
