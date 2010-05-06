/**
 *  Filename: ReadingInMeasurement.java (in org.repin.server.standalone.core)
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
 *  (c) Copyright ETH Zurich, Luba Rogoleva, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */
package org.redpin.server.standalone.core;

import org.redpin.server.standalone.db.IEntity;

public class ReadingInMeasurement implements IEntity<Integer> {

	private Integer id = -1;
	private int measurementId = -1;
	private int readingId = -1;
	private String readingClassName = "";
	
	public ReadingInMeasurement() {}
	
	public ReadingInMeasurement(int measurementId, int readingId, String readingClassName) {
		this.setMeasurementId(measurementId);
		this.setReadingId(readingId);
		this.setReadingClassName(readingClassName);		
	}
	/**
	 * @return the database id
	 */
	public Integer getId() {
		return id;
	}
	
	/**
	 * @param id
	 * 			the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	public void setMeasurementId(int measurementId) {
		this.measurementId = measurementId;
	}

	public int getMeasurementId() {
		return measurementId;
	}

	public void setReadingId(int readingId) {
		this.readingId = readingId;
	}

	public int getReadingId() {
		return readingId;
	}

	public void setReadingClassName(String readingClassName) {
		this.readingClassName = readingClassName;
	}

	public String getReadingClassName() {
		return readingClassName;
	}

}
