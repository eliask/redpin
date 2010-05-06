/**
 *  Filename: UpdateLocationHandler.java (in org.redpin.server.standalone.net)
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
import org.redpin.server.standalone.db.HomeFactory;
import org.redpin.server.standalone.db.homes.LocationHome;
import org.redpin.server.standalone.json.GsonFactory;
import org.redpin.server.standalone.net.Response.Status;
import org.redpin.server.standalone.util.Log;

import com.google.gson.JsonElement;

/**
 * @see IHandler
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class UpdateLocationHandler implements IHandler {
	
	LocationHome locHome;
	
	public UpdateLocationHandler() {
		locHome = HomeFactory.getLocationHome();
	}
	
	
	/**
	 * @see IHandler#handle(JsonElement)
	 */
	@Override
	public Response handle(JsonElement data) {
		Response res;
		
		Location loc = GsonFactory.getGsonInstance().fromJson(data, Location.class);
		
		
		if(locHome.update(loc)) {
			res = new Response(Status.ok, null, null);
			Log.getLogger().finer("location updated: " + loc);
			
		} else {
			res = new Response(Status.failed, "could not update to database", null);
			Log.getLogger().fine("location could not be updated to the database");
		}
		
		return res;
	}

}
