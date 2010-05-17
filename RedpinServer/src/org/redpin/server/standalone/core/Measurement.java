/**
 *  Filename: Measurement.java (in org.repin.server.standalone.core)
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
package org.redpin.server.standalone.core;

import org.redpin.base.core.IMeasurement;

import org.redpin.server.standalone.core.measure.BluetoothReading;
import org.redpin.server.standalone.core.measure.GSMReading;
import org.redpin.server.standalone.core.measure.WiFiReading;
import org.redpin.server.standalone.db.IEntity;
import org.redpin.server.standalone.locator.LocatorHome;
import java.util.Vector;


/**
 * @see org.redpin.base.core.Measurement
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class Measurement extends org.redpin.base.core.Measurement implements IMeasurement, IEntity<Integer> {
	
	
	public Measurement() {
		super(new Vector<GSMReading>(), new Vector<WiFiReading>(), new Vector<BluetoothReading>());
		
	}

	public Measurement(Vector<GSMReading> gsmReadings, Vector<WiFiReading> wifiReadings, Vector<BluetoothReading> bluetoothReadings) {
		super(gsmReadings, wifiReadings, bluetoothReadings);
	}

	private Integer id;

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
	
	/*
	protected Vector<GSMReading> gsmReadings;

	
	protected Vector<WiFiReading> wifiReadings;

	
	protected Vector<BluetoothReading> bluetoothReadings;
	*/
	
	/**
	 * @return Bluetooth readings vector
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Vector<BluetoothReading> getBluetoothReadings() {
		return (Vector<BluetoothReading>) super.getBluetoothReadings();
	}

	/**
	 * @return GSM readings vector
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Vector<GSMReading> getGsmReadings() {
		return (Vector<GSMReading>) super.getGsmReadings();
	}

	/**
	 * @return WiFi readings vector
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Vector<WiFiReading> getWiFiReadings() {
		return (Vector<WiFiReading>) super.getWiFiReadings();
	}
	
	/**
	 * 
	 * @param br Bluetooth readings vector
	 */
	public void setBluetoothReadings(Vector<BluetoothReading> br) {
		bluetoothReadings = br;
	}
	
	/**
	 * 
	 * @param wr WiFi readings vector
	 */
	public void setWiFiReadings(Vector<WiFiReading> wr) {
		wifiReadings = wr;
	}
	
	/**
	 * 
	 * @param gr GSMreadings vector
	 */
	public void setGSMReadings(Vector<GSMReading> gr) {
		gsmReadings = gr;
	}

	@Override
	public boolean isSimilar(org.redpin.base.core.Measurement m) {
		return LocatorHome.getLocator().measurmentAreSimilar(this, m);
	}

	@Override
	public int similarityLevel(org.redpin.base.core.Measurement m) {
		return LocatorHome.getLocator().measurementSimilarityLevel(this, m);
	}
	
	
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) || (this.getTimestamp() == ((Measurement)obj).getTimestamp());
	}
	
	

}
