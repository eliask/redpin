/**
 *  Filename: GsonFactory.java (in org.redpin.server.standalone.json)
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
package org.redpin.server.standalone.json;


import org.redpin.server.standalone.core.Measurement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Factory for {@link Gson} instances.
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class GsonFactory {
	
	private static Gson gson;
	
	/**
	 * Gets a configured {@link Gson} instance
	 *  
	 * @return {@link Gson} instance
	 */
	public synchronized static Gson getGsonInstance() {
		if(gson == null) {
			GsonBuilder builder = new GsonBuilder();
			
			//needed to get proper sub type after deserialization
			builder.registerTypeAdapter(org.redpin.base.core.Fingerprint.class, new BaseFingerprintTypeAdapter());
			builder.registerTypeAdapter(org.redpin.base.core.Location.class, new BaseLocationTypeAdapter());
			builder.registerTypeAdapter(org.redpin.base.core.Map.class, new BaseMapTypeAdapter());
			builder.registerTypeAdapter(org.redpin.base.core.Measurement.class, new BaseMeasurementTypeAdapter());
			
			//needed in order to deserialize proper the measurement vectors
			builder.registerTypeAdapter(Measurement.class, new MeasurementTypeAdapter());
	
			//builder.setPrettyPrinting();
			gson = builder.create();

		}
		
		return gson;
	}
	
}
