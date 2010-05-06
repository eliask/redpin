/**
 *  Filename: ILocator.java (in org.redpin.server.standalone.locator)
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


import org.redpin.server.standalone.core.Location;
import org.redpin.server.standalone.core.Measurement;

/**
 * Interface for a locator algorithm
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public interface ILocator {
	/**
	 * Tries to find a location which fingerprint measurement matches the {@link Measurement} m
	 * @param m {@link Measurement}
	 * @return {@link Location} or null if no location could be found
	 */
	public Location locate(Measurement m);
	
	/**
	 * Returns a similarity level between to measurement.
	 * This function is called by {@link Measurement#similarityLevel(org.redpin.base.core.Measurement)}
	 * 
	 * @see Measurement#similarityLevel(org.redpin.base.core.Measurement)
	 * @param t {@link org.redpin.base.core.Measurement} 
	 * @param o {@link org.redpin.base.core.Measurement}
	 * @return Similarity level
	 */
	public int measurementSimilarityLevel(org.redpin.base.core.Measurement t, org.redpin.base.core.Measurement o);
	
	/**
	 * Decides whether to measurements are similar.
	 * This function is called by {@link Measurement#isSimilar(org.redpin.base.core.Measurement)}
	 * 
	 * @see Measurement#isSimilar(org.redpin.base.core.Measurement)
	 * @param t {@link org.redpin.base.core.Measurement} 
	 * @param o {@link org.redpin.base.core.Measurement}
	 * @return Similarity level
	 */
	public Boolean measurmentAreSimilar(org.redpin.base.core.Measurement t, org.redpin.base.core.Measurement o);

}
