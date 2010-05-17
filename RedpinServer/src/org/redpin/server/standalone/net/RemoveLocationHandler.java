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
 *  (c) Copyright ETH Zurich, Luba Rogoleva, Pascal Brogle, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */
package org.redpin.server.standalone.net;

import org.redpin.server.standalone.core.Location;
import org.redpin.server.standalone.db.HomeFactory;
import org.redpin.server.standalone.db.homes.LocationHome;
import org.redpin.server.standalone.json.GsonFactory;
import org.redpin.server.standalone.net.Response.Status;
import org.redpin.server.standalone.util.Log;

import com.google.gson.JsonElement;

/**
 * Does remove location and the corresponding fingerprint (+measurements)
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 *
 */
public class RemoveLocationHandler implements IHandler {

	LocationHome locHome;
	
	public RemoveLocationHandler() {
		locHome = HomeFactory.getLocationHome();
	}
	
	/**
	 * @see IHandler#handle(JsonElement)
	 */
	@Override
	public Response handle(JsonElement data) {
		
		Response res;
		
		Location loc = GsonFactory.getGsonInstance().fromJson(data, Location.class);
		
		if(loc != null) {
			
			boolean locRemove = locHome.remove(loc);
			
			if(locRemove) { 
				res = new Response(Status.ok, null, null);
				Log.getLogger().finer("removed location from database");
			} else {
				res = new Response(Status.failed, "could not remove from database", loc);
				Log.getLogger().fine("could not remove location from database ");
			}
			
			
		} else {
			res = new Response(Status.ok, null, null);
			Log.getLogger().fine("fingerprint is not in the database");
		}
		
		
		
		
		
		return res;
	}

}
