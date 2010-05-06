/**
 *  Filename: MapHome.java (in org.redpin.server.standalone.db.homes)
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
public class MapHome extends EntityHome<Map> {
	
	public static final String[] TableCols = {"mapName", "mapURL"};
	public static final String TableName = "map"; 
	private static final String TableIdCol = "mapId";
	


	public MapHome() {
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
	protected Object[] getColValues(Map e) {
		Object[] res = new Object[getTableCols().length];
		res[0] = e.getMapName();
		res[1] = e.getMapURL();
		return res;
	}
	
	/**
	 * @see EntityHome#parseResultRow(ResultSet)
	 */
	@Override
	protected Map parseResultRow(ResultSet rs) throws SQLException{
		Map map = new Map();
		
		try {
			map.setId(rs.getInt(1));
			map.setMapName(rs.getString(2));
			map.setMapURL(rs.getString(3));
		
		} catch (SQLException e) {
			log.log(Level.SEVERE, "parseResultRow failed: " + e.getMessage(), e);
			throw e;
		}
		
		return map;
	}
	
	/**
	 * Removes all map locations
	 * 
	 */
	
	private void removeLocations(Integer id) {
		LocationHome lh = HomeFactory.getLocationHome();
		List<Location> list = lh.getListByMapId(id);
		if(!lh.remove(list)) {
			log.log(Level.WARNING, "implicit deletion of map location failed");
		}
	}
	
	/**
	 * Also deletes the map's locations
	 * @see EntityHome#removeById(Integer)
	 */
	@Override
	public boolean removeById(Integer id) {
		removeLocations(id);
		return super.removeById(id);
	}
	
	/**
	 * Also deletes the map's locations
	 * @see EntityHome#remove(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	public boolean remove(Map m) {
		removeLocations(m.getId());
		return super.removeById(m.getId());
	}

}
