/**
 *  Filename: MapRemoteHome.java (in org.repin.android.net.home)
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
package org.redpin.android.net.home;

import java.util.HashMap;
import java.util.List;

import org.redpin.android.core.Map;
import org.redpin.android.db.EntityHomeFactory;
import org.redpin.android.db.MapHome;
import org.redpin.android.net.Request;
import org.redpin.android.net.Response;
import org.redpin.android.net.Request.RequestType;

import android.util.Log;

/**
 * RemoteEntityHome for {@link Map}s
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class MapRemoteHome implements IRemoteEntityHome {

	protected MapHome mapHome = EntityHomeFactory.getMapHome();

	private static final String TAG = MapRemoteHome.class.getName();

	/**
	 * Performs an getMapList request without callback
	 */
	public static void getMapList() {
		RemoteEntityHome.performRequest(RequestType.getMapList);
	}

	/**
	 * Performs an getMapList request with callback
	 * 
	 * @param callback
	 *            {@link RemoteEntityHomeCallback}
	 */
	public static void getMapList(RemoteEntityHomeCallback callback) {
		RemoteEntityHome.performRequest(RequestType.getMapList, callback);
	}

	/**
	 * Performs an setMap request without callback
	 * 
	 * @param map
	 *            {@link Map} to be added
	 */
	public static void setMap(Map map) {
		RemoteEntityHome.performRequest(RequestType.setMap, map);
	}

	/**
	 * Performs an setMap request with callback
	 * 
	 * @param map
	 *            {@link Map} to be added
	 * @param callback
	 *            {@link RemoteEntityHomeCallback}
	 */
	public static void setMap(Map map, RemoteEntityHomeCallback callback) {
		RemoteEntityHome.performRequest(RequestType.setMap, map, callback);
	}

	/**
	 * Performs an removeMap request without callback
	 * 
	 * @param map
	 *            {@link Map} to be removed
	 * @return <code>true</code> if request can be performed, <code>false</code>
	 *         if the {@link Map} has no remote id
	 */
	public static boolean removeMap(Map map) {
		return removeMap(map, null);
	}

	/**
	 * Performs an removeMap request with callback
	 * 
	 * @param map
	 *            {@link Map} to be removed
	 * @param callback
	 *            {@link RemoteEntityHomeCallback}
	 * @return <code>true</code> if request can be performed, <code>false</code>
	 *         if the {@link Map} has no remote id
	 */
	public static boolean removeMap(Map map, RemoteEntityHomeCallback callback) {
		if (map.getRemoteId() == null || map.getRemoteId() < 0) {
			Log.i(TAG, "map can't be removed because no remote id is present");
			return false;
		}
		RemoteEntityHome.performRequest(RequestType.removeMap, map, callback);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void onRequestPerformed(Request<?> request, Response<?> response,
			RemoteEntityHome rHome) {

		switch (request.getAction()) {
		case getMapList:
			getMapListPerformed((Request<Void>) request,
					(Response<List<Map>>) response);
			break;

		case setMap:
			setMapPerformed((Request<Map>) request, (Response<Map>) response);
			break;

		case removeMap:
			removeMapPerformed((Request<Map>) request,
					(Response<Void>) response);
			break;

		default:
			throw new IllegalArgumentException(getClass().getName()
					+ " can't handle action " + request.getAction());
		}

	}

	/**
	 * Removes the {@link Map} from the local database after it was removed on
	 * the server
	 * 
	 * @param request
	 *            {@link Request} performed
	 * @param response
	 *            {@link Response} received
	 */
	private void removeMapPerformed(Request<Map> request,
			Response<Void> response) {

		boolean success = mapHome.remove(request.getData());

		if (!success) {
			Log.i(TAG, "removal of map " + request.getData() + " failed");
		}

	}

	/**
	 * Synchronizes the local {@link Map} database
	 * 
	 * @param request
	 *            {@link Request} performed
	 * @param response
	 *            {@link Response} received
	 */
	private void getMapListPerformed(Request<Void> request,
			Response<List<Map>> response) {
		List<Map> dbList = mapHome.getAll();
		HashMap<Integer, Map> map = new HashMap<Integer, Map>();
		for (Map m : dbList) {
			map.put(m.getRemoteId(), m);
		}

		List<Map> remote = response.getData();

		for (Map m : remote) {
			if (map.containsKey(m.getRemoteId())) {
				Map dbMap = map.get(m.getRemoteId());
				m.setLocalId(dbMap.getLocalId());

				if (!m.equals(dbMap)) {
					if (!mapHome.update(m)) {
						Log.w(TAG, "update of map " + m + " failed");
					}
				}
			} else {
				mapHome.add(m);
			}
		}

		map.clear();
		for (Map m : remote) {
			map.put(m.getRemoteId(), m);
		}

		for (Map m : dbList) {
			if (!map.containsKey(m.getRemoteId())) {
				mapHome.remove(m);
			}
		}

	}

	/**
	 * Adds the {@link Map} to the local database after it was added on the
	 * server
	 * 
	 * @param request
	 *            {@link Request} performed
	 * @param response
	 *            {@link Response} received
	 */
	private void setMapPerformed(Request<Map> request, Response<Map> response) {
		Map m = response.getData();
		mapHome.add(m);

	}

}
