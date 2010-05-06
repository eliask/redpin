/**
 *  Filename: Location.java (in org.repin.base.core)
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

import org.redpin.base.core.Map;



/**
 * Describes a location with containing a label and map with corresponding pixel
 * coordinates
 * 
 * @author Philipp Bolliger (philipp@bolliger.name)
 * @author Simon Tobler (simon.p.tobler@gmx.ch)
 * @author Davide Spena (davide.spena@gmail.com)
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * @version 0.2
 */
public class Location {


	/*
	 * unique identifier, commonly the name of this location e.g. 'IFW D47.2'
	 */
	protected String symbolicID = "";

	/*
	 * the Map where this location resides. includes path to image and a name
	 */
	protected Map map;

	/*
	 * X and Y coordinates of the location in the image referenced by fileName
	 * in pixel format
	 */
	protected int mapXcord = 0;
	protected int mapYcord = 0;

	/*
	 * StaticResources.LOCATION_UNKNOWN = location totally unknown
	 * StaticResources.LOCATION_KNOWN = location known Numbers in between define
	 * level of accuracy
	 */
	protected int accuracy = 0;
	
	/* **************** Constructors **************** */

	public Location() {
		this("", new Map(), 0, 0, 0, -1);
	}

	public Location(String symbolicId, Map map, int mapXcord, int mapYcord,
			int accuracy, int reflocationId) {
		this.symbolicID = symbolicId;
		this.map = map;
		this.mapXcord = mapXcord;
		this.mapYcord = mapYcord;
		this.accuracy = accuracy;
	}

	/* **************** Getter and Setter Methods **************** */

	/**
	 * @return accuracy
	 */
	public int getAccuracy() {
		return accuracy;
	}

	/**
	 * @param accuracy
	 */
	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}

	/**
	 * @return the symbolicID
	 */
	public String getSymbolicID() {
		return symbolicID;
	}

	/**
	 * @param symbolicID
	 *            the symbolicID to set
	 */
	public void setSymbolicID(String symbolicID) {
		this.symbolicID = symbolicID;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public int getMapXcord() {
		return mapXcord;
	}

	public void setMapXcord(int mapXcord) {
		this.mapXcord = mapXcord;
	}

	public int getMapYcord() {
		return mapYcord;
	}

	public void setMapYcord(int mapYcord) {
		this.mapYcord = mapYcord;
	}
	
}