/**
 *  Filename: CategorizerFactory.java (in org.redpin.server.standalone.svm)
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
package org.redpin.server.standalone.svm;

import java.util.List;

import org.redpin.server.standalone.core.Fingerprint;
import org.redpin.server.standalone.core.Location;
import org.redpin.server.standalone.core.Measurement;
import org.redpin.server.standalone.core.measure.WiFiReading;
import org.redpin.server.standalone.db.HomeFactory;

/**
 *  
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 *
 */
public class CategorizerFactory {

	private static Categorizer pLocationCategorizer; 
	private static Categorizer pBSSIDCategorizer;

	public synchronized static Categorizer LocationCategorizer() {
		if(pLocationCategorizer == null) {
			pLocationCategorizer = new Categorizer();
		}		
		return pLocationCategorizer;
	}
	
	public synchronized static Categorizer BSSIDCategorizer() {
		if(pBSSIDCategorizer == null) {
			pBSSIDCategorizer = new Categorizer();
		}		
		return pBSSIDCategorizer;
	}
	
	/**
	 * SVM requires all attributes as real numbers. This function 
	 * converts location tags and bssids into numeric data. 
	 * @param dataset
	 */
	public synchronized static void buildCategories(){
		List<Fingerprint> dataset = HomeFactory.getFingerprintHome().getAll();
		for (Fingerprint f : dataset) {
			if (f == null || f.getLocation() == null || f.getMeasurement() == null) continue;
			
			Integer id = ((Location)f.getLocation()).getId();
			if (id == null) continue;
			
			String locationTag = id.toString();
			LocationCategorizer().AddCategory(locationTag);
			Measurement m = (Measurement)f.getMeasurement();
			for (WiFiReading r : m.getWiFiReadings()) {
				if (r != null && r.getBssid() != null) {
					BSSIDCategorizer().AddCategory(r.getBssid());
				}
			}
		}
	}
}
