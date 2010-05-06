/**
 *  Filename: HomeFactory.java (in org.redpin.server.standalone.db)
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
package org.redpin.server.standalone.db;

import org.redpin.server.standalone.db.homes.BluetoothReadingHome;
import org.redpin.server.standalone.db.homes.EntityHome;
import org.redpin.server.standalone.db.homes.FingerprintHome;
import org.redpin.server.standalone.db.homes.GSMReadingHome;
import org.redpin.server.standalone.db.homes.LocationHome;
import org.redpin.server.standalone.db.homes.MapHome;
import org.redpin.server.standalone.db.homes.MeasurementHome;
import org.redpin.server.standalone.db.homes.ReadingInMeasurementHome;
import org.redpin.server.standalone.db.homes.WiFiReadingHome;
import org.redpin.server.standalone.db.homes.vector.BluetoothReadingVectorHome;
import org.redpin.server.standalone.db.homes.vector.GSMReadingVectorHome;
import org.redpin.server.standalone.db.homes.vector.WiFiReadingVectorHome;

/**
 * Factory for all {@link EntityHome}s
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 *
 */
public class HomeFactory {
	
	private static MapHome mapHome = null;
	public synchronized static MapHome getMapHome() {
		if(mapHome == null) {
			mapHome = new MapHome();
		}		
		return mapHome;
	}
	
	private static LocationHome locHome = null;
	public synchronized static LocationHome getLocationHome() {
		if(locHome == null) {
			locHome = new LocationHome();
		}		
		return locHome;
	}
	
	private static FingerprintHome fpHome = null;
	public synchronized static FingerprintHome getFingerprintHome() {
		if(fpHome == null) {
			fpHome = new FingerprintHome();
		}		
		return fpHome;
	}
	
	private static MeasurementHome mHome = null;
	public synchronized static MeasurementHome getMeasurementHome() {
		if(mHome == null) {
			mHome = new MeasurementHome();
		}		
		return mHome;
	}
	
	private static ReadingInMeasurementHome rinmHome = null;
	public synchronized static ReadingInMeasurementHome getReadingInMeasurementHome() {
		if(rinmHome == null) {
			rinmHome = new ReadingInMeasurementHome();
		}		
		return rinmHome;
	}
	
	private static WiFiReadingHome wrHome = null;
	public synchronized static WiFiReadingHome getWiFiReadingHome() {
		if(wrHome == null) {
			wrHome = new WiFiReadingHome();
		}		
		return wrHome;
	}
	
	private static WiFiReadingVectorHome wrvHome = null;
	public synchronized static WiFiReadingVectorHome getWiFiReadingVectorHome() {
		if(wrvHome == null) {
			wrvHome = new WiFiReadingVectorHome();
		}		
		return wrvHome;
	}
	
	private static GSMReadingHome grHome = null;
	public synchronized static GSMReadingHome getGSMReadingHome() {
		if(grHome == null) {
			grHome = new GSMReadingHome();
		}		
		return grHome;
	}
	
	private static GSMReadingVectorHome grvHome = null;
	public synchronized static GSMReadingVectorHome getGSMReadingVectorHome() {
		if(grvHome == null) {
			grvHome = new GSMReadingVectorHome();
		}		
		return grvHome;
	}
	
	private static BluetoothReadingHome brHome = null;
	public synchronized static BluetoothReadingHome getBluetoothReadingHome() {
		if(brHome == null) {
			brHome = new BluetoothReadingHome();
		}		
		return brHome;
	}
	
	private static BluetoothReadingVectorHome brvHome = null;
	public synchronized static BluetoothReadingVectorHome getBluetoothReadingVectorHome() {
		if(brvHome == null) {
			brvHome = new BluetoothReadingVectorHome();
		}		
		return brvHome;
	}
}
