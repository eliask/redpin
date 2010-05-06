/**
 *  Filename: DatabaseConnection.java (in org.redpin.server.standalone.db)
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
package org.redpin.server.standalone.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.redpin.server.standalone.util.Configuration;
import org.redpin.server.standalone.util.Log;

/**
 * Provides basic actions and access to a database.
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class DatabaseConnection {
	private static Connection connection = null;
	private static DatabaseConnection instance = null;
	private static Logger log;
	
	
	
	public DatabaseConnection() {
		log = Log.getLogger();
	}
	
	/**
	 * 
	 * @return {@link DatabaseConnection} instance 
	 */
	public synchronized static DatabaseConnection getInstance() {
		if(instance == null) {
			instance = new DatabaseConnection();
		}
		return instance;
	}
	
	/**
	 * 
	 * @return a database {@link Connection}
	 */
	public Connection getConnection() {
		if(!isConnected()) {
			// TODO: check success
			connect();
			
		} 
		return connection;
	}
	
	/**
	 * Checks if a connection exists
	 * 
	 * @return <code>true</code> if connection exists
	 */
	public boolean isConnected() {
		try {
			return (connection != null) && !connection.isClosed();
		} catch (SQLException e) {
			log.log(Level.WARNING, "DatabaseConnection: Connection check failed: "+e.getMessage(), e );
			return false;
		}
	}
	
	/**
	 * Establishes a connection to the database
	 * 
	 * @return <code>true</code> in case of success
	 */
	public synchronized boolean connect() {
		if(!isConnected()) {
			try {
				Class.forName(Configuration.DatabaseDriver);
				String url = "jdbc:"+Configuration.DatabaseType.name().toLowerCase()+":"+Configuration.DatabaseLocation;
				log.finer("DatabaseConnection: Connecting to "+url );
				connection = DriverManager.getConnection(url);
			} catch (SQLException e) {
				log.log(Level.SEVERE, "DatabaseConnection: Connection failed: "+e.getMessage(), e );
				return false;
			} catch (ClassNotFoundException e) {
				log.log(Level.SEVERE, "DatabaseConnection: Driver nod found: " +e.getMessage(), e );
				return false;
			}
		} else {
			log.finer("DatabaseConnection: already connected");
		}
		return true;
	}
	
	/**
	 * Closes a connection to database
	 */
	public synchronized void close() {
		if(isConnected()) {
			try {
				connection.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "DatabaseConnection: Connection closure failed: "+e.getMessage(), e );
				e.printStackTrace();
			}
		}
	}
	
	
}
