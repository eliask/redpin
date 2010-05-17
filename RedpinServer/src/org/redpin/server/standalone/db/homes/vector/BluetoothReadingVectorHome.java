/**
 *  Filename: BluetoothReadingVectorHome.java (in org.redpin.server.standalone.db.homes.vector)
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
package org.redpin.server.standalone.db.homes.vector;

import org.redpin.server.standalone.core.measure.BluetoothReading;
import org.redpin.server.standalone.db.HomeFactory;
import org.redpin.server.standalone.db.homes.EntityHome;

/**
 * @see EntityHome
 * @see VectorHome
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 *
 */
public class BluetoothReadingVectorHome extends VectorHome<BluetoothReading> {

	private static final String className = BluetoothReading.class.getSimpleName();
	
	/**
	 * @see VectorHome#getContainedObjectClassName()
	 */
	@Override
	public String getContainedObjectClassName() {
		return className;
	}

	/**
	 * @see VectorHome#getObjectHome()
	 */
	@Override
	public EntityHome<BluetoothReading> getObjectHome() {
		return HomeFactory.getBluetoothReadingHome();
	}

	
}
