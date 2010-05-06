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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.redpin.server.standalone.core.Fingerprint;
import org.redpin.server.standalone.core.Location;
import org.redpin.server.standalone.core.Measurement;
import org.redpin.server.standalone.core.Vector;
import org.redpin.server.standalone.db.DatabaseConnection;
import org.redpin.server.standalone.db.HomeFactory;
import org.redpin.server.standalone.util.Log;

/**
 * @see EntityHome
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 *
 */
public class FingerprintHome extends EntityHome<Fingerprint> {

	
	private static final String[] TableCols = {"locationId", "measurementId"};
	private static final String TableName = "fingerprint"; 
	private static final String TableIdCol = "fingerprintId";
	private static final String selectFingerprints = " SELECT " + TableName + "." + TableIdCol + ", " + HomeFactory.getLocationHome().getTableColNames() + ", " +
	 												 HomeFactory.getMapHome().getTableColNames() + ", " + HomeFactory.getMeasurementHome().getTableColNames() + ", " +
	 												 " readinginmeasurement.readingClassName, " + HomeFactory.getWiFiReadingHome().getTableColNames() + ", " + 
	 												 HomeFactory.getGSMReadingHome().getTableColNames() + ", " + HomeFactory.getBluetoothReadingHome().getTableColNames()  +
	 												 " FROM " + TableName + " INNER JOIN location ON fingerprint.locationId = location.locationId " +
	 												 " INNER JOIN map ON location.mapId = map.mapId INNER JOIN measurement ON fingerprint.measurementId = measurement.measurementId " + 
	 												 " INNER JOIN readinginmeasurement ON readinginmeasurement.measurementId = measurement.measurementId " +
	 												 " LEFT OUTER JOIN wifireading ON wifireading.wifiReadingId = readinginmeasurement.readingId " +
	 												 " LEFT OUTER JOIN gsmreading ON gsmreading.gsmReadingId = readinginmeasurement.readingId " +
	 												 " LEFT OUTER JOIN bluetoothreading ON bluetoothreading.bluetoothReadingId = readinginmeasurement.readingId ";
	private static final String orderFingerprints = " fingerprint.fingerprintId, fingerprint.measurementId, readinginmeasurement.readingClassName ";
	
	

	public FingerprintHome() {
		super();
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
			f.setLocation(HomeFactory.getLocationHome().parseResultRow(rs, 2));
			f.setMeasurement(HomeFactory.getMeasurementHome().parseResultRow(rs, HomeFactory.getLocationHome().getTableCols().length + 2 + HomeFactory.getMapHome().getTableCols().length + 2));
		
		} catch (SQLException e) {
			log.log(Level.SEVERE, "parseResultRow failed: " + e.getMessage(), e);
			throw e;
		}
		
		return f;
	}
	
	
	/**
	 * @see EntityHome#getAll()
	 */
	@Override
	public List<Fingerprint> getAll() {
		return getFingerprints(-1, -1, -1);
	}
	
	private List<Fingerprint> getFingerprints(Integer fingerprintId, Integer locationId, Integer measurementId) { 
		String cnst = "";
		if (fingerprintId != -1) cnst += getTableName() + "." + getTableIdCol() + " = " + fingerprintId;
		else if (locationId != -1) cnst += getTableName() + "." + getTableCols()[0] + " = " + locationId;
		else if (measurementId != -1) cnst += getTableName() + "." + getTableCols()[1] + " = " + measurementId;
		return get(cnst);
	}
	
	
	
	@Override
	protected String getSelectSQL() {
		return selectFingerprints;
	}
	
	@Override
	protected String getOrder() {
		return orderFingerprints;
	}
	
	/*
	@Override
	protected List<Fingerprint> get(String constrain) { 
		
		List<Fingerprint> res = new ArrayList<Fingerprint>();
		
		String sql = selectFingerprints; 
		if (constrain != null && constrain.length() > 0) sql += " WHERE " + constrain;
		sql += orderFingerprints;
		
		log.finest(sql);
		ResultSet rs = null;
		Statement stat = null;
		try {
			stat = db.getConnection().createStatement();
			rs = stat.executeQuery(sql);
			do {
				if (rs.isAfterLast()) break;
				if (!rs.isBeforeFirst() || rs.next()) { 
					res.add(parseResultRow(rs));
				}
			} while(!rs.isAfterLast());
		} catch (SQLException e) {
			log.log(Level.SEVERE, "getFingerprints failed: " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (stat != null) stat.close();
			} catch (SQLException es) {
				log.log(Level.WARNING, "failed to close database resources: " + es.getMessage(), es);
			}
		}
		
		return res;
	}
	*/
	
	/**
	 * get the total number of Fingerprints
	 * 
	 * @return the number of Fingerprints
	 */
	public int getCount() {
		int res = 0;
		
		String sql = "SELECT COUNT(*) FROM " + TableName;
		Log.getLogger().finest(sql);
		ResultSet rs = null;
		Statement stat = null;
		try {
			stat = DatabaseConnection.getInstance().getConnection().createStatement();
			rs = stat.executeQuery(sql);
			res = rs.getInt(1);
		} catch (SQLException e) {
			Log.getLogger().log(Level.SEVERE, "getNum failed: " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (stat != null) stat.close();
			} catch (SQLException es) {
				Log.getLogger().log(Level.WARNING, "failed to close database resources: " + es.getMessage(), es);
			}
		}
		
		return res;
	}

	@Override
	public synchronized Fingerprint add(Fingerprint fprint) {
		Connection conn = db.getConnection();
		Vector<PreparedStatement> vps = new Vector<PreparedStatement>();
		ResultSet rs = null;
		
		try {

			conn.setAutoCommit(false);
			Measurement m = (Measurement)fprint.getMeasurement();
			int measurementId = HomeFactory.getMeasurementHome().executeInsertUpdate(vps, m);
			// wifi
			HomeFactory.getWiFiReadingVectorHome().executeUpdate(vps, m.getWiFiReadings(), measurementId);
			// gsm
			HomeFactory.getGSMReadingVectorHome().executeUpdate(vps, m.getGsmReadings(), measurementId);			
			// bluetooth
			HomeFactory.getBluetoothReadingVectorHome().executeUpdate(vps, m.getBluetoothReadings(), measurementId);
			
			Location l = (Location)fprint.getLocation();
			int locationId = l.getId() == null ? -1 : l.getId().intValue();
			if (locationId == -1) {
				locationId = HomeFactory.getLocationHome().executeInsertUpdate(vps, l); //.getPrimaryKeyId();
			}
			
			int fingerprintId = executeInsertUpdate(vps, fprint);
			conn.commit();
			
			return getById(fingerprintId);
		
		} catch (SQLException e) {
			log.log(Level.SEVERE, "add fingerprint failed: " + e.getMessage(), e);
		} finally {
			try {
				conn.setAutoCommit(true);
				if (rs != null) rs.close();
				for(PreparedStatement p : vps) {
					if (p != null) p.close();
				}
			} catch (SQLException es) {
				log.log(Level.WARNING, "failed to close statement: " + es.getMessage(), es);
			}
		}
		return null;
	}

	
	@Override
	public Fingerprint parseResultRow(ResultSet rs, int fromIndex)
			throws SQLException {
		return parseResultRow(rs, 1);
	}
	
	
	/**
	 * get the fingerprint by its {@link Fingerprint} primary key
	 * 
	 * @param id primary key
	 * @return {@link Fingerprint} 
	 */
	@Override
	public Fingerprint getById(Integer id) {
		if (id == null) return null;
		List<Fingerprint> res = getFingerprints(id, -1, -1);
		return res == null || res.size() == 0 ? null : res.get(0); 
	}
	
	/**
	 * get the fingerprint by its {@link Fingerprint} location id
	 * 
	 * @param id primary key
	 * @return {@link Fingerprint} 
	 */
	public Fingerprint getByLocationId(Integer id) {
		if (id == null) return null;
		List<Fingerprint> res = getFingerprints(-1, id, -1);
		return res == null || res.size() == 0 ? null : res.get(0);
	}

	/**
	 * get the fingerprint by its {@link Fingerprint} measurement id
	 * 
	 * @param id primary key
	 * @return {@link Fingerprint} 
	 */
	public Fingerprint getByMeasurementId(Integer id) {
		if (id == null) return null;
		List<Fingerprint> res = getFingerprints(-1, -1, id);
		return res == null || res.size() == 0 ? null : res.get(0);
	}
	
	/*
	@Override
	public boolean remove(Fingerprint fp) {
		
		String fingerprintsCnst = getTableIdCol() + " = " + fp.getId() + " OR " + getTableCols()[0] + " = " + ((Location)fp.getLocation()).getId();
		String measurementsCnst = HomeFactory.getMeasurementHome().getTableIdCol() + " = " + ((Measurement)fp.getMeasurement()).getId() + 
								  " OR " + HomeFactory.getMeasurementHome().getTableIdCol() + 
								  " IN (SELECT " + getTableCols()[1] + " FROM " + getTableName() + " WHERE (" + fingerprintsCnst + ")) ";
		String readingInMeasurementCnst = " IN (SELECT readingId FROM readinginmeasurement WHERE (" + measurementsCnst + ")) ";
		
		String sql_l = "DELETE FROM " + HomeFactory.getLocationHome().getTableName() + " WHERE " + HomeFactory.getLocationHome().getTableIdCol() + " = " + ((Location)fp.getLocation()).getId();
		String sql_m = " DELETE FROM " + HomeFactory.getMeasurementHome().getTableName() + " WHERE " + measurementsCnst;
		String sql_wifi = " DELETE FROM " + HomeFactory.getWiFiReadingHome().getTableName() + 
						  " WHERE " + HomeFactory.getWiFiReadingHome().getTableIdCol() + readingInMeasurementCnst;
		String sql_gsm = " DELETE FROM " + HomeFactory.getGSMReadingHome().getTableName() + 
		  				 " WHERE " + HomeFactory.getGSMReadingHome().getTableIdCol() + readingInMeasurementCnst;
		String sql_bluetooth = " DELETE FROM " + HomeFactory.getBluetoothReadingHome().getTableName() + 
		  					   " WHERE " + HomeFactory.getBluetoothReadingHome().getTableIdCol() + readingInMeasurementCnst;
		 
		String sql_rinm = "DELETE FROM readinginmeasurement WHERE " + measurementsCnst;
		String sql_fp = "DELETE FROM " + getTableName() + " WHERE " + fingerprintsCnst;
		Statement stat = null;
		int res = -1;
		try {
			db.getConnection().setAutoCommit(false);
			stat = db.getConnection().createStatement();
			if (db.getConnection().getMetaData().supportsBatchUpdates()) {
				stat.addBatch(sql_wifi);
				stat.addBatch(sql_gsm);
				stat.addBatch(sql_bluetooth);
				stat.addBatch(sql_rinm);
				stat.addBatch(sql_m);
				stat.addBatch(sql_l);
				stat.addBatch(sql_fp);
				int results[] = stat.executeBatch();
				if (results != null && results.length > 0) {
					res = results[results.length - 1];
				}
			} else {
				stat.executeUpdate(sql_wifi);
				stat.executeUpdate(sql_gsm);
				stat.executeUpdate(sql_bluetooth);
				stat.executeUpdate(sql_rinm);
				stat.executeUpdate(sql_m);
				stat.executeUpdate(sql_l);
				res = stat.executeUpdate(sql_fp);
			}
			db.getConnection().commit();
			return res > 0;
		} catch (SQLException e) {
			log.log(Level.SEVERE, "remove fingerprint failed: " + e.getMessage(), e);
		} finally {
			try {
				db.getConnection().setAutoCommit(true);
				if (stat != null) stat.close();
			} catch (SQLException es) {
				log.log(Level.WARNING, "failed to close statement: " + es.getMessage(), es);
			}
		}
		return false;
	}
	*/


	@Override
	public int fillInStatement(PreparedStatement ps, Fingerprint t, int fromIndex) throws SQLException {
		return fillInStatement(ps, new Object[] {((Location)t.getLocation()).getId(), ((Measurement)t.getMeasurement()).getId()}, 
				new int[]{Types.INTEGER, Types.INTEGER},
				fromIndex);
	}

	@Override
	protected boolean remove(String constrain) {

		String fingerprintsCnst = (constrain != null && constrain.length() > 0) ? constrain : "1=1";
		
		String measurementsCnst = HomeFactory.getMeasurementHome().getTableIdCol() + " IN (SELECT " + HomeFactory.getFingerprintHome().getTableCols()[1] + 
																					 " FROM " + HomeFactory.getFingerprintHome().getTableName() + 
																					 " WHERE (" + fingerprintsCnst + ")) ";
		String readingInMeasurementCnst = " IN (SELECT readingId FROM readinginmeasurement WHERE (" + measurementsCnst + ")) ";
		
		
		String sql_m = " DELETE FROM " + HomeFactory.getMeasurementHome().getTableName() + " WHERE " + measurementsCnst;
		String sql_wifi = " DELETE FROM " + HomeFactory.getWiFiReadingHome().getTableName() + 
						  " WHERE " + HomeFactory.getWiFiReadingHome().getTableIdCol() + readingInMeasurementCnst;
		String sql_gsm = " DELETE FROM " + HomeFactory.getGSMReadingHome().getTableName() + 
		  				 " WHERE " + HomeFactory.getGSMReadingHome().getTableIdCol() + readingInMeasurementCnst;
		String sql_bluetooth = " DELETE FROM " + HomeFactory.getBluetoothReadingHome().getTableName() + 
		  					   " WHERE " + HomeFactory.getBluetoothReadingHome().getTableIdCol() + readingInMeasurementCnst;
		 
		String sql_rinm = "DELETE FROM readinginmeasurement WHERE " + measurementsCnst;
		String sql_fp = "DELETE FROM " + HomeFactory.getFingerprintHome().getTableName() + " WHERE " + fingerprintsCnst;

		Statement stat = null;
		
		log.finest(sql_wifi);
		log.finest(sql_gsm);
		log.finest(sql_bluetooth);
		log.finest(sql_rinm);
		log.finest(sql_m);
		log.finest(sql_fp);
		try {
			int res = -1;
			db.getConnection().setAutoCommit(false);
			stat = db.getConnection().createStatement();
			if (db.getConnection().getMetaData().supportsBatchUpdates()) {
				stat.addBatch(sql_wifi);
				stat.addBatch(sql_gsm);
				stat.addBatch(sql_bluetooth);
				stat.addBatch(sql_rinm);
				stat.addBatch(sql_m);
				stat.addBatch(sql_fp);
				int results[] = stat.executeBatch();
				if (results != null && results.length > 0) {
					res = results[results.length - 1];
				}
			} else {
				stat.executeUpdate(sql_wifi);
				stat.executeUpdate(sql_gsm);
				stat.executeUpdate(sql_bluetooth);
				stat.executeUpdate(sql_rinm);
				stat.executeUpdate(sql_m);
				res = stat.executeUpdate(sql_fp);
			}
			db.getConnection().commit();
			return res > 0;
		} catch (SQLException e) {
			log.log(Level.SEVERE, "remove map failed: " + e.getMessage(), e);
		} finally {
			try {
				db.getConnection().setAutoCommit(true);
				if (stat != null) stat.close();
			} catch (SQLException es) {
				log.log(Level.WARNING, "failed to close statement: " + es.getMessage(), es);
			}
		}
		return false;
		
	}

	

	

	
}
