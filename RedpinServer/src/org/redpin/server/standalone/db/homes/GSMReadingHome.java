/**
 *  Filename: GSMReadingHome.java (in org.redpin.server.standalone.db.homes)
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

import org.redpin.server.standalone.core.measure.GSMReading;

/**
 * @see EntityHome
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 *
 */
public class GSMReadingHome extends EntityHome<GSMReading> {
	
	private static final String[] TableCols = {"cellId", "areaId", "signalStrength", "MCC", "MNC","networkName"};
	private static final String TableName = "gsmreading"; 
	private static final String TableIdCol = "gsmReadingId";
	
	
	public GSMReadingHome() {
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
	public GSMReading parseResultRow(final ResultSet rs, int fromIndex) throws SQLException{
		GSMReading reading = new GSMReading();
		
		try {
			reading.setId(rs.getInt(fromIndex));
			reading.setCellId(rs.getString(fromIndex + 1));
			reading.setAreaId(rs.getString(fromIndex + 2));
			reading.setSignalStrength(rs.getString(fromIndex + 3));
			reading.setMCC(rs.getString(fromIndex + 4));
			reading.setMNC(rs.getString(fromIndex + 5));
			reading.setNetworkName(rs.getString(fromIndex + 6));
			
		
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
	public int fillInStatement(PreparedStatement ps, GSMReading t, int fromIndex)
			throws SQLException {
		return fillInStatement(ps, new Object[] {t.getCellId(), t.getAreaId(), t.getSignalStrength(), t.getMCC(), t.getMNC(), t.getNetworkName()},   
				new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR},
				fromIndex);
	}


	
	
}
