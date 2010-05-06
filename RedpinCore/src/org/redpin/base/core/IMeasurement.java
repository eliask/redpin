/**
 *  Filename: IMeasurement.java (in org.repin.base.core)
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

import org.redpin.base.core.Measurement;

/**
 * Interface for measurement
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public interface IMeasurement {
	
	/**
	 * computes the achieved accuracy level of a measurement compared to the
	 * current one
	 * 
	 * @param m Measurement
	 * @return computed similarity level
	 */
	public int similarityLevel(Measurement m);
	
	/**
	 * returns a boolean whether a measurement is considered to be similar or not
	 * to the current measurement
	 *
	 * @param m Measurement
	 * @return true if considered similar
	 */
	public boolean isSimilar(Measurement m);
}
