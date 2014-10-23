/**
 *  Filename: SVMLocator.java (in org.redpin.server.standalone.locator)
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
 *  (c) Copyright ETH Zurich, Luba Rogoleva, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */
package org.redpin.server.standalone.locator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.redpin.server.standalone.core.Location;
import org.redpin.server.standalone.core.Measurement;
import org.redpin.server.standalone.db.HomeFactory;
import org.redpin.server.standalone.svm.CategorizerFactory;
import org.redpin.server.standalone.svm.SVMSupport;
import org.redpin.server.standalone.util.Log;

/**
 * Locator that uses support vector machines (SVM) to estimate location
 *  
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 *
 */
public class SVMLocator implements ILocator {
	
	Logger log;
	
	public SVMLocator() {
		log = Log.getLogger();
	}
	
	@Override
	public Location locate(Measurement currentMeasurement) 
	{
		Location l = null;
		
		try {
			
			String outputfile = SVMSupport.predict(currentMeasurement);		
			
			BufferedReader outputreader = new BufferedReader(new InputStreamReader(new FileInputStream(outputfile)));
			String outputline = outputreader.readLine();
			if (outputline != null) {
				double locationTagIDScaled = Double.parseDouble(outputline);
				int locationTagID = (int)locationTagIDScaled;
				l = getByLocationTag(CategorizerFactory.LocationCategorizer().GetCategory(locationTagID));
			}		
			
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, "locate failed due to FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			log.log(Level.SEVERE, "locate failed due to IOException: " + e.getMessage());
		} catch (NumberFormatException e) {
			log.log(Level.SEVERE, "locate failed due to NumberFormatException: " + e.getMessage());
		}


		return l;
	}	
	
	
	private Location getByLocationTag(String tag) {
		if (tag == null) return null;
		try {
			int id = Integer.parseInt(tag);
			
			return HomeFactory.getLocationHome().getById(id);
			
		} catch (NumberFormatException e) {
			log.log(Level.WARNING, "getByLocationTag failed: " +e.getMessage());
		}
		return null;
		
	}
	
	@Override
	public int measurementSimilarityLevel(org.redpin.base.core.Measurement t,
			org.redpin.base.core.Measurement o) {
		return 0;
	}

	@Override
	public Boolean measurmentAreSimilar(org.redpin.base.core.Measurement t,
			org.redpin.base.core.Measurement o) {
		return null;
	}

}
