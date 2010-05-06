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
		}

		return l;
	}	
	
	
	private Location getByLocationTag(String tag) {
		if (tag == null) return null;
		return HomeFactory.getLocationHome().getLocation(-1, tag);
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
