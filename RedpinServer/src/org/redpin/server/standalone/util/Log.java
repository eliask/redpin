/**
 *  Filename: Log.java (in org.redpin.server.standalone.util)
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

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import org.redpin.server.standalone.util.Configuration.LoggerFormat;

/**
 * Log class which provides access to the logger
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class Log {
	
	private static Logger logger;
	
	/**
	 * Get an instance of a {@link Logger} configured according to the settings
	 * 
	 * @return {@link Logger}
	 */
	public synchronized static Logger getLogger() {
		
		if(logger == null) {
			try {
				logger = Logger.getLogger("RedpinLogger");
				FileHandler fh = new FileHandler(Configuration.LogFile);
				
				if(Configuration.LogFormat == LoggerFormat.PLAIN) {
					fh.setFormatter(new SimpleFormatter());
				} else if(Configuration.LogFormat == LoggerFormat.XML) {
					fh.setFormatter(new XMLFormatter());
				} else {
					//default
					fh.setFormatter(new SimpleFormatter());					
				}
				
				logger.addHandler(fh);
				logger.setLevel(Configuration.LogLevel);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				System.err.println(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		} 
		
		return logger;
		
	}
	

	
}
