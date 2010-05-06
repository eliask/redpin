/**
 *  Filename: MeasurementComparator.java (in org.redpin.server.standalone.locator)
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
package org.redpin.server.standalone.locator;

import java.util.Comparator;

import org.redpin.server.standalone.core.Measurement;

/**
 * Comparator for different {@link Measurement}. On creation, there must be set
 * an basis {@link Measurement} based on which the comparisation is performed.
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class MeasurementComparator implements Comparator<Measurement> {

	
	Measurement basisMeasurement;
	
	/**
	 * Creates a {@link MeasurementComparator} whith basis {@link Measurement}
	 * @see MeasurementComparator
	 * @param m Basis Basis {@link Measurement}
	 */
	public MeasurementComparator(Measurement m) {
		basisMeasurement = m;
	}
	
	/**
	 * Gets the Basis {@link Measurement} to which the others are compared
	 * @return {@link Measurement}
	 */
	public Measurement getBasisMeasurement() {
		return basisMeasurement;
	}

	/**
	 * Sets the Basis {@link Measurement} to which the others are compared
	 * 
	 * @param basisMeasurement {@link Measurement}
	 */
	public void setBasisMeasurement(Measurement basisMeasurement) {
		this.basisMeasurement = basisMeasurement;
	}

	/**
	 * Compares two measurement to the basisMeasurement 
	 * and returns which one is more similar to the basisMeasurement
	 * 
	 * @param arg0 Measurement 1
	 * @param arg1 Measurement 2
	 * @return -1 if arg0 is more more similar to basisMeasurement than arg1 <br />
	 * 			0 if arg0 and arg1 are equal similar to basisMeasuremet <br />
	 * 			1 if arg1 is more similar to basisMeasurement than arg0
	 */
	@Override
	public int compare(Measurement arg0, Measurement arg1) {
		int a1 = basisMeasurement.similarityLevel(arg0);
		int a2 = basisMeasurement.similarityLevel(arg1);
		
		if (a1 == a2) {
			long t1 = arg0.getTimestamp();
			long t2 = arg1.getTimestamp();
			if (t1 == t2) {
				return 0;
			} else {
				if (t1 < t2) {
					return 1;
				} else {
					return -1;
				}
			}
		} else {
			if (a1 < a2) {
				return 1;
			} else {
				return -1;
			}
		}
		
		
	}
	
}
