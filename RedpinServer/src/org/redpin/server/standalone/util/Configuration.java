/**
 *  Filename: Configuration.java (in org.redpin.server.standalone.util)
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
package org.redpin.server.standalone.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;

import org.redpin.server.standalone.db.DatabaseConnection;
import org.redpin.server.standalone.svm.SVMSupport;
import org.redpin.server.standalone.svm.TrainSVMTimerTask;

/**
 * Configuration class which represents all configuration settings. 
 * Before the first access to a property, all properties are read from the properties file and are initialized accordingly.
 * If the database schema is not already set up, this is also done.
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class Configuration {
	
	/**
	 * Different {@link DatabaseTypes} which are supported by the server
	 * 
	 * @author Pascal Brogle (broglep@student.ethz.ch)
	 *
	 */
	public enum DatabaseTypes { SQLITE, MYSQL};
	public static final String ResourcesDir = "/resources/";
	private static final String SQLite_Schema = ResourcesDir + "redpin_sqlite.sql";
	
	public enum LoggerFormat {PLAIN, XML};
	
	// default settings
	public static Integer ServerPort = 8000;
	public static String ImageUploadPath = "mapuploads/";
	public static LoggerFormat LogFormat = LoggerFormat.PLAIN; //or xml
	public static Level LogLevel = Level.WARNING;
	public static String LogFile = "redpin.log";
	
	
	public static boolean LogRequests = false;
	public static String LogRequestPath = "requests/";
	
	public static DatabaseTypes DatabaseType = DatabaseTypes.SQLITE;
	public static String DatabaseLocation = "redpin.db";
	public static String DatabaseDriver = "org.sqlite.JDBC";
	
	public static String LibSVMDirectory = "libsvm-2.9";
	public static long SVMTrainRate = TrainSVMTimerTask.DEFAULT_TRAIN_RATE;
	
	
	private static String generateTrainScript(String dir) {
		return "#!/bin/sh \n"+
		dir +"/svm-scale -l -1 -u 1 -s " + SVMSupport.RANGE + " " + SVMSupport.TRAIN + " > " + SVMSupport.TRAIN_SCALE + "$1\n" +
		dir +"/svm-train -c 512 -t 0 -q " + SVMSupport.TRAIN_SCALE + "$1";
	}
	
	
	// initialization
	static {
		Properties p = new Properties();
		File f = new File("redpin.properties");
		if(f.exists()) {
			try {
				FileInputStream reader = new FileInputStream( f);
				p.load(reader);
				
				try {
					ServerPort = new Integer(p.getProperty("port", ServerPort.toString()));
				} catch (NumberFormatException e) {}
				
				ImageUploadPath = p.getProperty("image.upload.path", ImageUploadPath);
				if(ImageUploadPath.charAt(ImageUploadPath.length()-1) == '/') {
					ImageUploadPath = ImageUploadPath.substring(0, ImageUploadPath.length()-1);
				}
				LogFile = p.getProperty("log.file", LogFile);
				
				String format = "";
				try {
					format = p.getProperty("log.format", LogFormat.name());
					LogFormat = LoggerFormat.valueOf(format.toUpperCase());
					
				} catch(IllegalArgumentException e) {
					Log.getLogger().log(Level.CONFIG, "No such log format type " + format +": " + e.getMessage(), e);
				}
				
				String level = "";
				try {
					level = p.getProperty("log.level", LogLevel.getName());
					LogLevel = Level.parse(level);					
				} catch(IllegalArgumentException e) {
					Log.getLogger().log(Level.CONFIG, "No such log format type " + format +": " + e.getMessage(), e);
				}
				
				
				LogRequests = Boolean.valueOf(p.getProperty("requests.log", Boolean.valueOf(LogRequests).toString())).booleanValue();
				LogRequestPath = p.getProperty("requests.log.path", LogRequestPath);
				if(LogRequestPath.charAt(LogRequestPath.length()-1) == '/') {
					LogRequestPath = LogRequestPath.substring(0, LogRequestPath.length()-1);
				}
				
				if(LogRequests) {
					File lrp = new File(LogRequestPath);
					if(!lrp.exists()) {
						lrp.mkdirs();
					}
				}
				
				
				String type = "";
				try {
					type = p.getProperty("db.type", DatabaseType.name());
					DatabaseType = DatabaseTypes.valueOf(type.toUpperCase());
					
				} catch(IllegalArgumentException e) {
					Log.getLogger().log(Level.CONFIG, "No such database type " + type +": " + e.getMessage(), e);
				}
				
				if(DatabaseType == DatabaseTypes.SQLITE) {
					DatabaseLocation = p.getProperty("db.location", DatabaseLocation);
					DatabaseDriver = "org.sqlite.JDBC";
	
				}
				
				if(DatabaseType == DatabaseTypes.MYSQL) {
					DatabaseDriver = "com.mysql.jdbc.Driver";
					DatabaseLocation = p.getProperty("db.location", DatabaseLocation);
				}
				
				
				LibSVMDirectory = p.getProperty("svm.libdir", LibSVMDirectory);
				try {
					SVMTrainRate = Long.parseLong(p.getProperty("svm.trainrate", SVMTrainRate+""));
				} catch (NumberFormatException e) {}
				
				
				
			} catch (Exception e) {
				Log.getLogger().log(Level.SEVERE, "Config initialization failed: "+ e.getMessage(), e);
				e.printStackTrace();
			}
						
		}
		
		File dir = new File(ImageUploadPath);
		if(!dir.exists()) {
			if(!dir.mkdirs()) {
				Log.getLogger().log(Level.WARNING, "Image Upload Path could not be created");
			} else {						
				Log.getLogger().fine("No image upload dir found, now creating " + ImageUploadPath);
			}
		}
			
		
		if((DatabaseType == DatabaseTypes.SQLITE) && (!new File(DatabaseLocation).exists())) {
			Log.getLogger().fine("No database file found, now importing database schema");
			importSQLiteSchema();
		}
		
		dir = new File(LibSVMDirectory);
		if(dir.isDirectory() && dir.exists()) {
		
			File trainPl = new File(SVMSupport.TRAIN_SCRIPT);
			try {
				if(!trainPl.exists()) {
					Writer w = new FileWriter(trainPl);
					w.write(generateTrainScript(LibSVMDirectory));
					w.flush();
					w.close();				
					trainPl.setExecutable(true);
				}
			} catch (Exception e) {
				Log.getLogger().fine("could not create " + SVMSupport.TRAIN_SCRIPT);
			}
		}
		
	}
	
	/**
	 * Import the sqlite database schema
	 */
	private static void importSQLiteSchema() {
		

		InputStream is =  ClassLoader.class.getResourceAsStream(SQLite_Schema);
		if(is != null) {

			try {
				

				BufferedReader bf = new BufferedReader(new InputStreamReader(is));
				
				String sql = "";
				String line = "";
				while(true) {
					
					line = bf.readLine();
					
					if(line == null)
						break;
					
					line = line.trim();
					
					if(line.endsWith(";")) {
						sql += line;
						
						Connection conn = DatabaseConnection.getInstance().getConnection();
						Statement stat = conn.createStatement();
						Log.getLogger().finest("import table " + sql);
						stat.executeUpdate(sql);
						
						
						sql = "";
						line = "";
					} else {
						sql += line;
					}
					
					
					
				}
				
				
			} catch (FileNotFoundException e) {
				Log.getLogger().log(Level.WARNING, "schema file missing: " + e.getMessage(), e);
			} catch (SQLException e) {
				Log.getLogger().log(Level.WARNING, "schema file import failed: " + e.getMessage(), e);
			} catch (IOException e) {
				Log.getLogger().log(Level.WARNING, "schema file ioerror: " + e.getMessage(), e);
			}
		} else {
			Log.getLogger().log(Level.WARNING, "schema file missing");
		}
		
		
	}
}


