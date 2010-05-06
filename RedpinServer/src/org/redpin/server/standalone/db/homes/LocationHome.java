/**
 *  Filename: LocationHome.java (in org.redpin.server.standalone.db.homes)
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.redpin.server.standalone.core.Fingerprint;
import org.redpin.server.standalone.core.Location;
import org.redpin.server.standalone.core.Map;
import org.redpin.server.standalone.db.HomeFactory;


/**
 * @see EntityHome
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class LocationHome extends EntityHome<Location> {
	
	
	
	private static final String[] TableCols = {"symbolicId", "mapId", "mapXCord", "mapYCord", "accuracy"};
	private static final String TableName = "location"; 
	private static final String TableIdCol = "locationId";
	
	
	public LocationHome() {
		super();
	}

	/**
	 * @see EntityHome#getColValues(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	protected Object[] getColValues(Location e) {
		Object[] res = new Object[getTableCols().length];
		res[0] = e.getSymbolicID();
		res[1] = ((Map)e.getMap()).getId();
		res[2] = e.getMapXcord();
		res[3] = e.getMapYcord();
		res[4] = e.getAccuracy();
		return res;
		
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
	 * @see EntityHome#getTableName()
	 */
	@Override
	protected String getTableName() {
		return TableName;
	}

	/**
	 * @see EntityHome#parseResultRow(ResultSet)
	 */
	@Override
	protected Location parseResultRow(ResultSet rs) throws SQLException {
		Location loc = new Location();
		
		try {
			loc.setId(rs.getInt(1));
			loc.setSymbolicID(rs.getString(2));
			loc.setMap(HomeFactory.getMapHome().getById(rs.getInt(3)));
			loc.setMapXcord(rs.getInt(4));
			loc.setMapYcord(rs.getInt(5));
			loc.setAccuracy(rs.getInt(6));
		
		} catch (SQLException e) {
			log.log(Level.SEVERE, "parseResultRow failed: " + e.getMessage(), e);
			throw e;
		}
		
		return loc;
	}
	
	/**
	 * Checks if the {@link Location}'s {@link Map} is already stored in the database. if not, the {@link Map} is saved
	 * @param e {@link Location}
	 */
	private void checkMap(Location e) {
		Map m = (Map) e.getMap();
		if(m.getId() == null) {
			log.finer("map not yet saved, now saving it");
			m = HomeFactory.getMapHome().add(m);
		}
	}
	
	/**
	 * @see EntityHome#add(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	public Location add(Location e) {
		//checkMap(e);
		return super.add(e);
	}
	
	/**
	 * @see EntityHome#update(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	public boolean update(Location e) {
		//checkMap(e);
		return super.update(e);
	}

	
	public List<Location> getListByMapId(Integer id) {
		List<Location> res = new ArrayList<Location>();
		
		String sql = "SELECT * FROM " + getTableName() + " WHERE "	+ getTableCols()[1] + "=" + id;
		log.finest(sql);

		try {
			ResultSet rs = executeQuery(sql);
			while(rs.next()) {
				res.add(parseResultRow(rs));
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "getListByMapId failed: " + e.getMessage(), e);
		}
		
		return res;
	}
	
	public List<Location> getListByMap(Map m) {
		return getListByMapId(m.getId());
	}
	
	

}
