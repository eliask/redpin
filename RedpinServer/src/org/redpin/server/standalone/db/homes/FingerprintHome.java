/**
 *  Filename: FingerprintHome.java (in org.redpin.server.standalone.db.homes)
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

import org.redpin.server.standalone.core.Fingerprint;
import org.redpin.server.standalone.core.Location;
import org.redpin.server.standalone.core.Measurement;
import org.redpin.server.standalone.core.Vector;
import org.redpin.server.standalone.core.measure.BluetoothReading;
import org.redpin.server.standalone.core.measure.GSMReading;
import org.redpin.server.standalone.core.measure.WiFiReading;
import org.redpin.server.standalone.db.HomeFactory;

/**
 * @see EntityHome
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class FingerprintHome extends EntityHome<Fingerprint> {

	
	private static final String[] TableCols = {"locationId", "measurementId"};
	private static final String TableName = "fingerprint"; 
	private static final String TableIdCol = "fingerprintId";
	
	public FingerprintHome() {
		super();
	}

	/**
	 * @see EntityHome#getColValues(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	protected Object[] getColValues(Fingerprint e) {
		Object[] res = new Object[getTableCols().length];
		res[0] = ((Location) e.getLocation()).getId();
		res[1] = ((Measurement) e.getMeasurement()).getId();
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
	protected Fingerprint parseResultRow(ResultSet rs) throws SQLException {
		Fingerprint f = new Fingerprint();
		
		try {
			
			f.setId(rs.getInt(1));
			f.setLocation(HomeFactory.getLocationHome().getById(rs.getInt(2)));
			f.setMeasurement(HomeFactory.getMeasurementHome().getById(rs.getInt(3)));
			
		
		} catch (SQLException e) {
			log.log(Level.SEVERE, "parseResultRow failed: " + e.getMessage(), e);
			throw e;
		}
		
		return f;
	}
	
	/**
	 * Checks if the fingerprint's location is already stored in the database. if not, the location is saved
	 * @param e {@link Fingerprint}
	 */
	private void checkLocation(Fingerprint e) {
		Location l = (Location) e.getLocation();
		if(l.getId() == null) {
			log.finer("location not yet saved, now saving it");
			l = HomeFactory.getLocationHome().add(l);
		}
	}
	
	/**
	 * Checks if the fingerprint's measurement is already stored in the database. if not, the location is saved
	 * @param e {@link Fingerprint}
	 */
	private void checkMeasurement(Fingerprint e) {
		Measurement m = (Measurement) e.getMeasurement();
		if(m.getId() == null) {
			log.finer("measurement not yet saved, now saving it");
			m = HomeFactory.getMeasurementHome().add(m);
		}
	}
	
	/**
	 * @see EntityHome#add(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	public Fingerprint add(Fingerprint e) {
		checkLocation(e);
		checkMeasurement(e);
		return super.add(e);
	}
	
	/**
	 * @see EntityHome#update(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	public boolean update(Fingerprint e) {
		checkLocation(e);
		checkMeasurement(e);
		return super.update(e);
	}
	
	
	/**
	 * Removes measurements if they are not already deleted
	 * 
	 * @param f {@link Fingerprint}
	 */
	private void removeMeasurements(Fingerprint f) {
		MeasurementHome mh = HomeFactory.getMeasurementHome();
		if(f.getMeasurement() != null) {
			log.finer("implicit deletion of measurement");
			if(!mh.remove((Measurement)f.getMeasurement())) {
				log.log(Level.WARNING, "implicit deletion of measurement failed");
			}
		}
	}
	
	/**
	 * Also deletes the fingerprint's measurement, but not its location
	 * @see EntityHome#removeById(Integer)
	 */
	@Override
	public boolean removeById(Integer id) {
		Fingerprint f = getById(id);
		removeMeasurements(f);
		return super.removeById(id);
	}
	
	/**
	 * Also deletes the fingerprint's measurement, but not its location
	 * @see EntityHome#remove(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	public boolean remove(Fingerprint f) {
		removeMeasurements(f);
		return super.removeById(f.getId());
	}
	
	/**
	 * get the location by its {@link Measurement}
	 * 
	 * @param m {@link Measurement}
	 * @return {@link Fingerprint} containing the {@link Measurement} m
	 */
	public Fingerprint getByMeasurement(Measurement m) {
		return getByMeasurementId(m.getId());
	}
	
	/**
	 * get the location by its {@link Measurement} primary key
	 * 
	 * @param id primary key
	 * @return {@link Fingerprint} containing the measurement m
	 */
	public Fingerprint getByMeasurementId(Integer id) {
		Fingerprint res = null;
		
		String sql = "SELECT * FROM " + getTableName() + " WHERE "	+ getTableCols()[1] + "=" + id;
		log.finest(sql);

		try {
			ResultSet rs = executeQuery(sql);
			if (rs.next()) {
				res = parseResultRow(rs);
			} else {
				log.log(Level.WARNING, "getByMeasurementId returned no result");
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "getByMeasurementId failed: " + e.getMessage(), e);
		}
			
	
		
		return res;
	}
	
	/**
	 * get the location by its {@link Fingerprint}
	 * 
	 * @param l {@link Fingerprint}
	 * @return {@link Fingerprint} containing the {@link Fingerprint} f
	 */
	public Fingerprint getByLocation(Location l) {
		return getByFingerprintId(l.getId());
	}
	
	/**
	 * get the location by its {@link Fingerprint} primary key
	 * 
	 * @param id primary key
	 * @return {@link Fingerprint} containing the measurement m
	 */
	public Fingerprint getByFingerprintId(Integer id) {
		Fingerprint res = null;
		
		String sql = "SELECT * FROM " + getTableName() + " WHERE "	+ getTableCols()[0] + "=" + id;
		log.finest(sql);

		try {
			ResultSet rs = executeQuery(sql);
			if (rs.next()) {
				res = parseResultRow(rs);
			} else {
				log.log(Level.WARNING, "getByMeasurementId returned no result");
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "getByMeasurementId failed: " + e.getMessage(), e);
		}	
		
		return res;
	}
	

}
