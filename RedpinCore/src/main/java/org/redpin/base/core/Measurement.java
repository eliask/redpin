/**
 *  Filename: Measurement.java (in org.repin.base.core)
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

import java.util.Vector;

import org.redpin.base.core.measure.BluetoothReading;
import org.redpin.base.core.measure.GSMReading;
import org.redpin.base.core.measure.WiFiReading;

/**
 * Describes a measurement containing several readings from the bluetooth-,
 * wifi- and gsm device. 
 * When extending it, you have to implement IMeasurement<T>
 * 
 * @author Philipp Bolliger (philipp@bolliger.name)
 * @author Simon Tobler (simon.p.tobler@gmx.ch)
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * @version 0.2
 */
public abstract class Measurement {

	
	

	/* time of measurment */
	protected long timestamp = 0;

	/* set of GSM readings that where taken during the measurement */
	/* cant use generic type because of compability to j2me which does not have an generic vector type */
	protected Vector gsmReadings;

	/* set of WiFi readings that where taken during the measurement */
	protected Vector wifiReadings;

	/* set of Bluetooth readings that where taken during the measurement */
	protected Vector bluetoothReadings;

	/* constructor */
	public Measurement() {
		timestamp = System.currentTimeMillis();
		gsmReadings = new Vector();
		wifiReadings = new Vector();
		bluetoothReadings = new Vector();
		
	}
	
	public Measurement(Vector gsmReadings, Vector wifiReadings, Vector bluetoothReadings) {
		timestamp = System.currentTimeMillis();
		this.gsmReadings = gsmReadings;
		this.wifiReadings = wifiReadings;
		this.bluetoothReadings = bluetoothReadings;
	}


	/* ************ Getter and Setter Methods ************ */

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the gsm readings
	 */
	public Vector getGsmReadings() {
		return gsmReadings;
	}

	public void addGSMReading(GSMReading gsmReading) {
		this.gsmReadings.addElement(gsmReading);
	}

	/**
	 * @return the wifi readings
	 */
	public Vector getWiFiReadings() {
		return wifiReadings;
	}

	public void addWiFiReading(WiFiReading wiFiReading) {
		this.wifiReadings.addElement(wiFiReading);
	}

	/**
	 * @return the bluetooth readings
	 */
	public Vector getBluetoothReadings() {
		return bluetoothReadings;
	}

	public void addBluetoothReading(BluetoothReading bluetoothReading) {
		this.bluetoothReadings.addElement(bluetoothReading);
	}
	
}
