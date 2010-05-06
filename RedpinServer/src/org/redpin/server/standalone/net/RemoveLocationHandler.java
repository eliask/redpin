/**
 *  Filename: RemoveLocationHandler.java (in org.redpin.server.standalone.net)
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
package org.redpin.server.standalone.net;

import org.redpin.server.standalone.core.Location;
import org.redpin.server.standalone.core.Fingerprint;
import org.redpin.server.standalone.db.HomeFactory;
import org.redpin.server.standalone.db.homes.FingerprintHome;
import org.redpin.server.standalone.db.homes.LocationHome;
import org.redpin.server.standalone.json.GsonFactory;
import org.redpin.server.standalone.net.Response.Status;
import org.redpin.server.standalone.util.Log;

import com.google.gson.JsonElement;

/**
 * Does remove location and the corresponding fingerprint (+measurements)
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class RemoveLocationHandler implements IHandler {

	
	FingerprintHome fHome;
	LocationHome locHome;
	
	public RemoveLocationHandler() {
		fHome = HomeFactory.getFingerprintHome();
		locHome = HomeFactory.getLocationHome();
	}
	
	/**
	 * @see IHandler#handle(JsonElement)
	 */
	@Override
	public Response handle(JsonElement data) {
		
		Response res;
		
		Location loc = GsonFactory.getGsonInstance().fromJson(data, Location.class);
		Fingerprint fp = fHome.getByLocation(loc);
		
		if(fp != null) {
			
			boolean fpRemove = fHome.remove(fp);
			boolean locRemove = locHome.remove(loc);
			
			if(fpRemove && locRemove) {
				res = new Response(Status.ok, null, null);
				Log.getLogger().finer("removed location & fingerprint from database");
			} else {
				res = new Response(Status.failed, "could not remove from database", loc);
				Log.getLogger().fine("could not remove location / fingerprint from database:" + (!fpRemove ? " fingerprint not removed" : "" )+  (!locRemove ? " location not removed" : "" ));
			}
			
			
		} else {
			res = new Response(Status.failed, "could not remove from database, no fingerprint found", loc);
			Log.getLogger().fine("could not remove location from database, no fingerprint found");
		}
		
		
		
		
		
		return res;
	}

}
