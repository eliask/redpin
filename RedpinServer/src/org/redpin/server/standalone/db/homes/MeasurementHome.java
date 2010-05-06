/**
 *  Filename: MeasurementHome.java (in org.redpin.server.standalone.db.homes)
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
package org.redpin.server.standalone.db.homes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.redpin.server.standalone.core.Measurement;
import org.redpin.server.standalone.core.Vector;
import org.redpin.server.standalone.core.measure.BluetoothReading;
import org.redpin.server.standalone.core.measure.GSMReading;
import org.redpin.server.standalone.core.measure.WiFiReading;
import org.redpin.server.standalone.db.HomeFactory;
import org.redpin.server.standalone.db.homes.vector.BluetoothReadingVectorHome;
import org.redpin.server.standalone.db.homes.vector.GSMReadingVectorHome;
import org.redpin.server.standalone.db.homes.vector.WiFiReadingVectorHome;

/**
 * @see EntityHome
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class MeasurementHome extends EntityHome<Measurement> {


	private static final String[] TableCols = {"timestamp", "wifiReadingsvectorId","gsmReadingsvectorId","bluetoothReadingsvectorId"};
	private static final String TableName = "measurement"; 
	private static final String TableIdCol = "measurementId";
	
	private WiFiReadingVectorHome wrvh = HomeFactory.getWiFiReadingVectorHome();
	private GSMReadingVectorHome grvh = HomeFactory.getGSMReadingVectorHome();
	private BluetoothReadingVectorHome brvh = HomeFactory.getBluetoothReadingVectorHome();
	

	public MeasurementHome() {
		super();
	}
	
	/**
	 * @see EntityHome#getColValues(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	protected Object[] getColValues(Measurement e) {
		Object[] res = new Object[getTableCols().length];
		res[0] = e.getTimestamp();
		res[1] = e.getWiFiReadings().getId();
		res[2] = e.getGsmReadings().getId();
		res[3] = e.getBluetoothReadings().getId();
		
		return res;
	}

	/**
	 * @see EntityHome#getTableIdCol()
	 */
	@Override
	protected String getTableIdCol() {
		return TableIdCol;
	}

	/**
	 * @see EntityHome#getTableCols()
	 */
	@Override
	protected String[] getTableCols() {
		return TableCols;
	}

	/**
	 * @see EntityHome#getTableName()
	 */
	@Override
	protected String getTableName() {
		return TableName;
	}
	
	/**
	 * @see EntityHome#parseResultRow(ResultSet)
	 */
	@Override
	protected Measurement parseResultRow(ResultSet rs) throws SQLException {
		Measurement m = new Measurement();
		
		try {
			
			m.setId(rs.getInt(1));
			m.setTimestamp(rs.getLong(2));
			m.setWiFiReadings(wrvh.getById(rs.getInt(3)));
			m.setGSMReadings(grvh.getById(rs.getInt(4)));
			m.setBluetoothReadings(brvh.getById(rs.getInt(5)));
		
		} catch (SQLException e) {
			log.log(Level.SEVERE, "parseResultRow failed: " + e.getMessage(), e);
			throw e;
		}
		
		return m;
	}
	
	private void checkReadings(Measurement e) {
		Vector<WiFiReading> wv = e.getWiFiReadings();
		if(wv.getId() == null) {
			log.finer("wifireading vector not yet saved, now saving it");
			wv = wrvh.add(wv);
		}
		
		Vector<GSMReading> gv = e.getGsmReadings();
		if(gv.getId() == null) {
			log.finer("gsmreading vector not yet saved, now saving it");
			gv = grvh.add(gv);
		}
		
		Vector<BluetoothReading> bv = e.getBluetoothReadings();
		if(bv.getId() == null) {
			log.finer("bluetoothreading vector not yet saved, now saving it");
			bv = brvh.add(bv);
		}
	}
	
	
	/**
	 * @see EntityHome#add(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	public Measurement add(Measurement e) {
		checkReadings(e);
		
		return super.add(e);
	}
	
	
	/**
	 * @see EntityHome#update(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	public boolean update(Measurement e) {
		checkReadings(e);
		
		return super.update(e);
	}
	
	/**
	 * Removes vectors if they are not already deleted
	 * 
	 * @param m {@link Measurement}
	 */
	private void removeVectors(Measurement m) {
		Vector<WiFiReading> wrv = m.getWiFiReadings();
		Vector<GSMReading> grv = m.getGsmReadings();
		Vector<BluetoothReading> brv = m.getBluetoothReadings();
		
		if( (wrv != null) && (wrv.getId() != null)) {
			log.finer("implicit deletion of wifireading vector");
			wrvh.remove(wrv);
		}
		
		if( (grv != null) && (grv.getId() != null)) {
			log.finer("implicit deletion of gsmreading vector");
			grvh.remove(grv);
		}
		
		if( (brv != null) && (brv.getId() != null)) {
			log.finer("implicit deletion of bluetoothreading vector");
			brvh.remove(brv);
		}
		
			
	}
	
	/**
	 * Also removes the measurment's vectors
	 * @see EntityHome#removeById(Integer)
	 */
	@Override
	public boolean removeById(Integer id) {
		Measurement m = getById(id);
		removeVectors(m);
		return super.removeById(id);
	}
	
	/**
	 * Also removes the measurment's vectors
	 * @see EntityHome#remove(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	public boolean remove(Measurement e) {
		removeVectors(e);
		return super.removeById(e.getId());
	}
	
	
	/**
	 * Helper Function to remove old {@link Vector} and if successful assign the new one to the {@link Measurement}
	 * (on update there is no automatic removal of the old vector when a new {@link Vector} is set)
	 *  
	 * @param m {@link Measurement} 
	 * @param v New {@link Vector} which will be assigned to the {@link Measurement} m
	 * @return <code>true</code> if removal of the old {@link Vector} was successful
	 */
	public boolean setWiFiReadingVector(Measurement m, Vector<WiFiReading> v) {
		boolean res = false;
		log.finer("try to remove old vector");
		if(wrvh.remove(m.getWiFiReadings())) {
			m.setWiFiReadings(v);
			res = true;
		}		
		return res;
	}
	
	/**
	 * @see MeasurementHome#setWiFiReadingVector(Measurement, Vector)
	 */
	public boolean setGSMReadingVector(Measurement m, Vector<GSMReading> v) {
		boolean res = false;
		log.finer("try to remove old vector");
		if(grvh.remove(m.getGsmReadings())) {
			m.setGSMReadings(v);
			res = true;
		}		
		return res;
	}
	
	/**
	 * @see MeasurementHome#setWiFiReadingVector(Measurement, Vector)
	 */
	public boolean setBluetoothReadingVector(Measurement m, Vector<BluetoothReading> v) {
		boolean res = false;
		log.finer("try to remove old vector");
		if(brvh.remove(m.getBluetoothReadings())) {
			m.setBluetoothReadings(v);
			res = true;
		}		
		return res;
	}

}
