/**
 *  Filename: LocationRemoteHome.java (in org.repin.android.net.home)
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

import org.redpin.android.core.Location;
import org.redpin.android.core.Map;
import org.redpin.android.core.Measurement;
import org.redpin.android.db.EntityHomeFactory;
import org.redpin.android.db.LocationHome;
import org.redpin.android.db.MapHome;
import org.redpin.android.net.Request;
import org.redpin.android.net.Response;
import org.redpin.android.net.Request.RequestType;

import android.util.Log;

/**
 * RemoteEntityHome for {@link Location}s
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class LocationRemoteHome implements IRemoteEntityHome {

	protected LocationHome locHome = EntityHomeFactory.getLocationHome();
	protected MapHome mapHome = EntityHomeFactory.getMapHome();

	private static final String TAG = LocationRemoteHome.class.getName();

	/**
	 * Performs an getLocationList request without callback
	 */
	public static void getLocationList() {
		RemoteEntityHome.performRequest(RequestType.getLocationList);
	}

	/**
	 * Performs an getLocationList request with callback
	 * 
	 * @param callback
	 *            {@link RemoteEntityHomeCallback}
	 */
	public static void getLocationList(RemoteEntityHomeCallback callback) {
		RemoteEntityHome.performRequest(RequestType.getLocationList, callback);
	}

	/**
	 * Performs an getLocation request without callback to estimate the current
	 * location
	 * 
	 * @param measurement
	 *            {@link Measurement}
	 */
	public static void getLocation(Measurement measurement) {
		RemoteEntityHome.performRequest(RequestType.getLocation, measurement);
	}

	/**
	 * Performs an getLocation request with callback to estimate the current
	 * location
	 * 
	 * @param measurement
	 *            {@link Measurement}
	 * @param callback
	 *            {@link RemoteEntityHomeCallback}
	 */
	public static void getLocation(Measurement measurement,
			RemoteEntityHomeCallback callback) {
		RemoteEntityHome.performRequest(RequestType.getLocation, measurement,
				callback);
	}

	/**
	 * Performs an updateLocation request without callback
	 * 
	 * @param loc
	 *            {@link Location} to be updated
	 * @return <code>true</code> if request can be performed, <code>false</code>
	 *         if the {@link Location} has no remote id
	 */
	public static boolean updateLocation(Location loc) {
		return updateLocation(loc, null);
	}

	/**
	 * Performs an updateLocation request with callback
	 * 
	 * @param loc
	 *            {@link Location} to be updated
	 * @param callback
	 *            {@link RemoteEntityHomeCallback}
	 * @return <code>true</code> if request can be performed, <code>false</code>
	 *         if the {@link Location} has no remote id
	 */
	public static boolean updateLocation(Location loc,
			RemoteEntityHomeCallback callback) {
		if (loc.getRemoteId() == null || loc.getRemoteId() < 0) {
			Log
					.i(TAG,
							"location can't be updated because no remote id is present");
			return false;
		}
		RemoteEntityHome.performRequest(RequestType.updateLocation, loc,
				callback);
		return true;
	}

	/**
	 * Performs a removeLocation request without callback
	 * 
	 * @param loc
	 *            {@link Location} to be removed
	 * @return <code>true</code> if request can be performed, <code>false</code>
	 *         if the {@link Location} has no remote id
	 */
	public static boolean removeLocation(Location loc) {
		return removeLocation(loc, null);
	}

	/**
	 * Performs a removeLocation request with callback
	 * 
	 * @param loc
	 *            {@link Location} to be removed
	 * @param callback
	 *            {@link RemoteEntityHomeCallback}
	 * @return <code>true</code> if request can be performed, <code>false</code>
	 *         if the {@link Location} has no remote id
	 */
	public static boolean removeLocation(Location loc,
			RemoteEntityHomeCallback callback) {
		if (loc.getRemoteId() == null || loc.getRemoteId() < 0) {
			Log
					.i(TAG,
							"location can't be removed because no remote id is present");
			return false;
		}
		RemoteEntityHome.performRequest(RequestType.removeLocation, loc,
				callback);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void onRequestPerformed(Request<?> request, Response<?> response,
			RemoteEntityHome rHome) {

		switch (request.getAction()) {
		case getLocation:
			getLocationPerformed((Request<Measurement>) request,
					(Response<Location>) response);
			break;
		case getLocationList:
			getLocationListPerformed((Request<Void>) request,
					(Response<List<Location>>) response);
			break;
		case removeLocation:
			removeLocationPerformed((Request<Location>) request,
					(Response<Void>) response);
			break;
		case updateLocation:
			updateLocationPerformed((Request<Location>) request,
					(Response<Void>) response);
			break;

		default:
			throw new IllegalArgumentException(getClass().getName()
					+ " can't handle action " + request.getAction());
		}

	}

	/**
	 * Updates the local database after the location was updated on the server
	 * 
	 * @param request
	 *            {@link Request} performed
	 * @param response
	 *            {@link Response} received
	 */
	private void updateLocationPerformed(Request<Location> request,
			Response<Void> response) {
		Location l = request.getData();

		if (l == null) {
			return;
		}

		Location dbLoc = locHome.get(l);

		if (!l.equals(dbLoc)) {
			boolean success = locHome.update(l);
			if (!success) {
				Log.i(TAG, "update of location " + l + " failed");
			}
		}

	}

	/**
	 * Removes the {@link Location} from the local database after it was removed
	 * on the server
	 * 
	 * @param request
	 *            {@link Request} performed
	 * @param response
	 *            {@link Response} received
	 */
	private void removeLocationPerformed(Request<Location> request,
			Response<Void> response) {
		boolean success = locHome.remove(request.getData());

		if (!success) {
			Log.i(TAG, "removal of location " + request.getData() + " failed");
		}

	}

	/**
	 * Synchronizes the local {@link Location} database
	 * 
	 * @param request
	 *            {@link Request} performed
	 * @param response
	 *            {@link Response} received
	 */
	private void getLocationListPerformed(Request<Void> request,
			Response<List<Location>> response) {
		List<Location> dbList = locHome.getAll();
		HashMap<Integer, Location> loc = new HashMap<Integer, Location>();
		for (Location l : dbList) {
			loc.put(l.getRemoteId(), l);
		}

		List<Location> remote = response.getData();

		for (Location l : remote) {
			if (loc.containsKey(l.getRemoteId())) {
				Location dbLoc = loc.get(l.getRemoteId());
				l.setLocalId(dbLoc.getLocalId());
				if (dbLoc.getMap() != null && l.getMap() != null) {
					((Map) l.getMap()).setLocalId(((Map) dbLoc.getMap())
							.getLocalId());
				}

				if (!l.equals(dbLoc)) {
					if (!locHome.update(l)) {
						Log.w(TAG, "update of location " + l + " failed");
					}
				}
			} else {
				if (l.getMap() == null) {
					continue;
				}
				Map m = mapHome.getByRemoteId(((Map) l.getMap()).getRemoteId());

				if (m != null) {
					l.setMap(m);
				}

				locHome.add(l);
			}
		}

		loc.clear();
		for (Location l : remote) {
			loc.put(l.getRemoteId(), l);
		}

		for (Location l : dbList) {
			if (!loc.containsKey(l.getRemoteId())) {
				locHome.remove(l);
			}
		}

	}

	/**
	 * Adds {@link Location} if not yet in local database
	 * 
	 * @param request
	 *            {@link Request} performed
	 * @param response
	 *            {@link Response} received
	 */
	private void getLocationPerformed(Request<Measurement> request,
			Response<Location> response) {
		Location l = response.getData();
		if (l == null) {
			return;
		}

		Location dbLoc = locHome.getByRemoteId(l.getRemoteId());
		if (dbLoc != null) {
			l.setLocalId(dbLoc.getLocalId());

			if (!l.equals(dbLoc)) {
				if (!locHome.update(l)) {
					Log.w(TAG, "update of location " + l + " failed");
				}
			}

		} else {
			locHome.add(l);
		}

	}
}
