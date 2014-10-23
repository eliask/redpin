/**
 *  Filename: Fingerprint.java (in org.repin.base.core)
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


import org.redpin.base.core.Location;
import org.redpin.base.core.Measurement;



/**
 * Describes a fingerprint containing a location and a corresponding measurement
 *
 * @author Philipp Bolliger (philipp@bolliger.name)
 * @author Simon Tobler (simon.p.tobler@gmx.ch)
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * @version 0.2
 */
public class Fingerprint {


	/* the measurement that and the location which are associated hereby */
	protected Location location;
	protected Measurement measurement;

	/* **************** Constructors **************** */

	public Fingerprint(Location location, Measurement measurement) {
		this.location = location;
		this.measurement = measurement;
	}

	/* ************ Getter / Setter Methods ************ */

	/**
	 * @return the reading
	 */
	public Measurement getMeasurement() {
		return measurement;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @param measurement
	 *            the measurement to set
	 */
	public void setMeasurement(Measurement measurement) {
		this.measurement = measurement;
	}



}

