package org.redpin.server.standalone.svm;

import java.util.List;

import org.redpin.server.standalone.core.Fingerprint;
import org.redpin.server.standalone.core.Measurement;
import org.redpin.server.standalone.core.measure.WiFiReading;
import org.redpin.server.standalone.db.HomeFactory;

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
			String locationTag = f.getLocation().getSymbolicID();
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
