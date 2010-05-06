/**
 *  Filename: BluetoothReading.java (in org.repin.base.core.measure)
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
package org.redpin.base.core.measure;


import org.redpin.base.core.Types;

/**
 * Describes a bluetooth reading
 * 
 * @author Philipp Bolliger (philipp@bolliger.name)
 * @author Simon Tobler (simon.p.tobler@gmx.ch)
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * @version 0.2
 */
public class BluetoothReading {


	/* attributes */
	protected String friendlyName = "";
	protected String bluetoothAddress = "";
	protected String majorDeviceClass = ""; // see
	// http://www.jasonlam604.com/articles_introduction_to_bluetooth_and_j2me_part2.php
	protected String minorDeviceClass = "";

	/* **************** Getter and Setter Methods **************** */

	/**
	 * @return the friendlyName
	 */
	public String getFriendlyName() {
		return friendlyName;
	}

	/**
	 * @param friendlyName
	 *            the friendlyName to set
	 */
	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	/**
	 * @return the bluetoothAddress
	 */
	public String getBluetoothAddress() {
		return bluetoothAddress;
	}

	/**
	 * @param bluetoothAddress
	 *            the bluetoothAddress to set
	 */
	public void setBluetoothAddress(String bluetoothAddress) {
		this.bluetoothAddress = bluetoothAddress;
	}

	/**
	 * @return the majorDeviceClass
	 */
	public String getMajorDeviceClass() {
		return majorDeviceClass;
	}

	/**
	 * @param majorDeviceClass
	 *            the majorDeviceClass to set
	 */
	public void setMajorDeviceClass(String majorDeviceClass) {
		this.majorDeviceClass = majorDeviceClass;
	}

	/**
	 * @return the minorDeviceClass
	 */
	public String getMinorDeviceClass() {
		return minorDeviceClass;
	}

	/**
	 * @param minorDeviceClass
	 *            the minorDeviceClass to set
	 */
	public void setMinorDeviceClass(String minorDeviceClass) {
		this.minorDeviceClass = minorDeviceClass;
	}

	public String getType() {
		return Types.BLUETOOTH;
	}
	
	/*
	 * removed due to conflicts: what is id needed for?
	 *
	public String getId() {
		return bluetoothAddress;
	}
	*/
	
	
	/**
	 * Returns Bluetooth Friendly Name
	 */
	public String getHumanReadableName() {
		return friendlyName;
	}

	public String toString() {
		return super.toString() + ": " + Types.FRIENDLY_NAME + "=" + friendlyName
				+ "|" + Types.BLUETOOTH_ADDRESS + "=" + bluetoothAddress + "|"
				+ Types.MAJOR_DEVICE_CLASS + "=" + majorDeviceClass + "|"
				+ Types.MINOR_DEVICE_CLASS + "=" + minorDeviceClass;
	}

	
}
