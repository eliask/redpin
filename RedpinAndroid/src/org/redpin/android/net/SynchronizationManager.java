/**
 *  Filename: SynchronizationManager.java (in org.repin.android.net)
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
package org.redpin.android.net;

import org.redpin.android.net.home.LocationRemoteHome;
import org.redpin.android.net.home.MapRemoteHome;
import org.redpin.android.net.home.RemoteEntityHomeCallback;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * {@link SynchronizationManager} synchronizes the local database with the
 * server
 *
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class SynchronizationManager extends Service {

	private static final String TAG = SynchronizationManager.class
			.getSimpleName();

	/**
	 * Registers an {@link InternetConnectionManager} broadcast receiver to get
	 * notified about connectivity changes and tries to synchronize
	 *
	 * @see Service#onCreate()
	 */
	@Override
	public void onCreate() {
		registerReceiver(bReceiver, new IntentFilter(
				InternetConnectionManager.CONNECTIVITY_ACTION));
		sync();
	}

	/**
	 * Unregisters the {@link InternetConnectionManager} broadcast receiver
	 *
	 * @see Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		unregisterReceiver(bReceiver);
		super.onDestroy();
	}

	/**
	 * {@link BroadcastReceiver} for {@link InternetConnectionManager}
	 * broadcasts
	 *
	 * @see BroadcastReceiver
	 */
	private BroadcastReceiver bReceiver = new BroadcastReceiver() {

		/**
		 * Tries to synchronize when an {@link InternetConnectionManager}
		 * broadcast was received that indicates that the redpin server is
		 * available
		 *
		 * @see BroadcastReceiver#onReceive(Context, Intent)
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			if ((intent.getFlags() & InternetConnectionManager.ONLINE_FLAG)== InternetConnectionManager.ONLINE_FLAG) {
				sync();
			}
		}
	};

	/**
	 * @see Binder
	 *
	 * @author Pascal Brogle (broglep@student.ethz.ch)
	 *
	 */
	public class LocalBinder extends Binder {
		/**
		 *
		 * @return {@link SynchronizationManager}
		 */
		SynchronizationManager getService() {
			return SynchronizationManager.this;
		}
	}

	private final LocalBinder mBinder = new LocalBinder();

	/**
	 * @see Service#onBind(Intent)
	 * @return {@link LocalBinder}
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private boolean isSynced = false;
	private boolean syncInProgress = false;

	/**
	 * Tries to synchronize the local database with the server. After successful
	 * synchronization the server is stopped
	 */
	private void sync() {

		if (isSynced) {
			stopSelf();
			return;
		}

		if (syncInProgress)
			return;

		syncInProgress = true;

		MapRemoteHome.getMapList(new RemoteEntityHomeCallback() {

			@Override
			public void onResponse(Response<?> response) {
				LocationRemoteHome
						.getLocationList(new RemoteEntityHomeCallback() {

							@Override
							public void onResponse(Response<?> response) {
								isSynced = true;
								Log.v(TAG, "database synchronized");
								syncInProgress = false;
							}

							@Override
							public void onFailure(Response<?> response) {
								Log.v(TAG,
										"database location synchronized failed: "
												+ response.getMessage());
								syncInProgress = false;
							}
						});
			}

			@Override
			public void onFailure(Response<?> response) {
				Log.v(TAG, "database map synchronized failed: "
						+ response.getMessage());
				syncInProgress = false;
			}
		});

	}

}
