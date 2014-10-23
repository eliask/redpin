/**
 *  Filename: FingerprintRemoteHome.java (in org.repin.android.net.home)
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

import org.redpin.android.core.Fingerprint;
import org.redpin.android.core.Location;
import org.redpin.android.db.EntityHomeFactory;
import org.redpin.android.db.LocationHome;
import org.redpin.android.net.Request;
import org.redpin.android.net.Response;
import org.redpin.android.net.Request.RequestType;

/**
 * RemoteEntityHome for {@link Fingerprint}s
 *
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class FingerprintRemoteHome implements IRemoteEntityHome {

	protected LocationHome locHome = EntityHomeFactory.getLocationHome();

	/**
	 * Performs an setFingerprint request without callback
	 *
	 * @param fingerprint
	 *            {@link Fingerprint}
	 */
	public static void setFingerprint(Fingerprint fingerprint) {
		RemoteEntityHome
				.performRequest(RequestType.setFingerprint, fingerprint);
	}

	/**
	 * Performs an setFingerprint request with callback
	 *
	 * @param fingerprint
	 *            {@link Fingerprint}
	 * @param callback
	 *            {@link RemoteEntityHomeCallback}
	 */
	public static void setFingerprint(Fingerprint fingerprint,
			RemoteEntityHomeCallback callback) {
		RemoteEntityHome.performRequest(RequestType.setFingerprint,
				fingerprint, callback);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void onRequestPerformed(Request<?> request, Response<?> response,
			RemoteEntityHome rHome) {
		switch (request.getAction()) {
		case setFingerprint:
			setFingerprintPerformed((Request<Fingerprint>) request,
					(Response<Fingerprint>) response);
			break;

		default:
			throw new IllegalArgumentException(getClass().getName()
					+ " can't handle action " + request.getAction());
		}

	}

	/**
	 * Updates the location database if fingerprint's location is not yet in the
	 * database and sets local and remote id's accordingly
	 *
	 * @param request
	 *            Performed {@link Request}
	 * @param response
	 *            Received {@link Response}
	 */
	private void setFingerprintPerformed(Request<Fingerprint> request,
			Response<Fingerprint> response) {
		Fingerprint resFp = response.getData();
		if (resFp == null) {
			return;
		}
		Location resLoc = (Location) resFp.getLocation();

		if (resLoc != null) {
			Location l = locHome.getByRemoteId(resLoc.getRemoteId());
			if (l == null) {
				locHome.add(resLoc);
			}
		}

		Fingerprint f = request.getData();
		if (f == null)
			return;

		f.setRemoteId(resFp.getRemoteId());
		Location l = (Location) f.getLocation();

		if (l == null)
			return;

		l.setRemoteId(resLoc.getRemoteId());
		l.setLocalId(resLoc.getLocalId());

	}

}
