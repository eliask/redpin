/**
 *  Filename: Map.java (in org.repin.base.core)
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
package org.redpin.base.core;




/**
 * Describes a map
 * 
 * @author Philipp Bolliger (philipp@bolliger.name)
 * @author Simon Tobler (simon.p.tobler@gmx.ch)
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * @version 0.2
 */
public class Map {

	
	/*
	 * unique identifier, commonly the name of this map e.g. 'IFW floor A'
	 */
	protected String mapName = "";

	/*
	 * the URL of the corresponding map (image) where this location resides e.g.
	 * http://www.redpin.org/maps/layout_IFW_D.gif
	 */
	protected String mapURL = "";

	/* **************** Constructors **************** */

	public Map() {
		mapName = "";
		mapURL = "";
	}

	public Map(String mapName, String mapURL) {
		super();
		this.mapName = mapName;
		this.mapURL = mapURL;
	}

	/* **************** Getter and Setter Methods **************** */

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getMapURL() {
		return mapURL;
	}

	public void setMapURL(String mapURL) {
		this.mapURL = mapURL;
	}
	
	public String toString() {
		return super.toString() + ": " + mapName + "; mapURL = " + mapURL + ";";
	}
}