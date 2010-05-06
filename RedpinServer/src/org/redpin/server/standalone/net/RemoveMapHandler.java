/**
 *  Filename: RemoveMapHandler.java (in org.redpin.server.standalone.net)
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

import org.redpin.server.standalone.core.Map;
import org.redpin.server.standalone.db.HomeFactory;
import org.redpin.server.standalone.db.homes.MapHome;
import org.redpin.server.standalone.json.GsonFactory;
import org.redpin.server.standalone.net.Response.Status;
import org.redpin.server.standalone.util.Log;

import com.google.gson.JsonElement;

public class RemoveMapHandler implements IHandler {

	
	MapHome mapHome;
	
	public RemoveMapHandler() {
		mapHome = HomeFactory.getMapHome();
	}
	
	/**
	 * @see IHandler#handle(JsonElement)
	 */
	@Override
	public Response handle(JsonElement data) {
		
		Response res;
		
		Map map = GsonFactory.getGsonInstance().fromJson(data, Map.class);
		if(mapHome.remove(map)) {
			res = new Response(Status.ok, null, null);
			Log.getLogger().finer("removed map from database");
		} else {
			res = new Response(Status.failed, "could not remove from database", map);
			Log.getLogger().fine("could not remove map from database");
		}
		
		
		
		return res;
	}

}
