/**
 *  Filename: InternetConnectionManager.java (in org.repin.android.net)
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
 *  (c) Copyright ETH Zurich, Arno Fiva, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 *
 *  www.redpin.org
 */
package org.redpin.android.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.redpin.android.R;
import org.redpin.android.ui.MapViewActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

/**
 * The {@link InternetConnectionManager} provides information about the
 * connectivity to the Redpin server. It does so by checking the devices current
 * network state but also contacting the server directly.
 *
 * @author Arno Fiva (fivaa@student.ethz.ch)
 */
public class InternetConnectionManager extends Service {

	public static String CONNECTIVITY_ACTION = "org.redpin.android.net.CONNECTIVITY_CHANGED";

	public static int ONLINE_FLAG = 1;

	public static int TIMEOUT = 30;

	private NotificationManager mNM;

	private ConnectivityChecker mChecker = null;
	private boolean isCheckRequested = false;
	private boolean hasNotifiedOnlineState = true; //prevent initial notification
	private boolean hasNotifiedOfflineState = false;

	private boolean isServerAvailable = false;

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		registerReceiver(mNotifier, new IntentFilter(CONNECTIVITY_ACTION));

		/* Hook ourselves up to ConnectivityManager to get network state changes */
		registerReceiver(mConnectivity, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));

		checkConnectivity();

	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mNotifier);
		unregisterReceiver(mConnectivity);

		mNM.cancel(R.string.app_name);

		super.onDestroy();
	}

	/**
	 * Tell {@link InternetConnectionManager} to check the connectivity now.
	 */
	public void checkConnectivity() {

		if (mChecker == null) {
			mChecker = new ConnectivityChecker();
			mChecker.execute((Void[]) null);
		} else {
			isCheckRequested = true;
		}

	}

	public boolean isOnline() {
		return isServerAvailable;
	}

	/**
	 * @see Service#onBind(Intent)
	 * @return {@link LocalBinder}
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/**
	 * @see Binder
	 *
	 */
	public class LocalBinder extends Binder {
		public InternetConnectionManager getService() {
			return InternetConnectionManager.this;
		}
	}

	private final LocalBinder mBinder = new LocalBinder();

	/**
	 * {@link BroadcastReceiver} for network status changes on the device
	 */
	private BroadcastReceiver mConnectivity = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			/*
			 * Only initiate a new check if we are not simply trying a different
			 * network
			 */
			if (intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER,
					false))
				return;
			checkConnectivity();

		}
	};

	/**
	 * {@link BroadcastReceiver} for our own {@link CONNECTIVITY_ACTION} action
	 * in order to display a notification in the UI.
	 */
	private BroadcastReceiver mNotifier = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if ((intent.getFlags() & InternetConnectionManager.ONLINE_FLAG)== InternetConnectionManager.ONLINE_FLAG) {
				if (!hasNotifiedOnlineState) {

					showNotification(R.string.connection_online);
					hasNotifiedOnlineState = true;

				}
				hasNotifiedOfflineState = false;
			} else {

				if (!hasNotifiedOfflineState) {
					showNotification(R.string.connection_offline);
					hasNotifiedOfflineState = true;
				}
				hasNotifiedOnlineState = false;
			}

			mChecker = null;
			if (isCheckRequested) {
				isCheckRequested = false;
				checkConnectivity();
			}
		}

		/**
		 * Display message with id {@link redId} in the notification manager.
		 *
		 * @param resId
		 */
		private void showNotification(int resId) {

			CharSequence text = getText(resId);
			Context context = getApplicationContext();

			Notification notification = new Notification(R.drawable.icon, text,
					System.currentTimeMillis());

			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					new Intent(context, MapViewActivity.class), 0);

			notification.setLatestEventInfo(context,
					getText(R.string.app_name), text, contentIntent);
			mNM.notify(R.string.app_name, notification);

		}

	};

	/**
	 * {@link AsyncTask} responsible for explicitly checking the connection to
	 * the Redpin server by simply opening a socket to it and closing it again.
	 *
	 */
	private class ConnectivityChecker extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Socket socket = null;
			boolean online = false;
			try {
				socket = new Socket();
				socket.bind(null);
				InetSocketAddress address = new InetSocketAddress(
						ConnectionHandler.host, ConnectionHandler.port);
				socket.connect(address, TIMEOUT * 1000);
				online = socket.isConnected();
				socket.close();
			} catch (IOException e) {
				// Nothing to do since we are just interested in the
				// availability of the server
			}

			Intent intent = new Intent(CONNECTIVITY_ACTION);
			if (online)
				intent.setFlags(ONLINE_FLAG);

			isServerAvailable = online;

			sendBroadcast(intent);

			return null;
		}

	};

}
