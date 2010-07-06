/**
 *  Filename: ServerPreferences.java (in org.repin.android.ui)
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
 *  (c) Copyright ETH Zurich, Luba Rogoleva, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */
package org.redpin.android.ui;

import org.redpin.android.R;
import org.redpin.android.net.ConnectionHandler;
import org.redpin.android.net.InternetConnectionManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

/**
 * Class represents an activity responsible for Preferences screen through which
 * the user can change shared preferences.
 * 
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 * 
 */
public class ServerPreferences extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	/**
	 * Key ID for the shared preference host.
	 */
	public final static String HOST_PREFERENCE_KEY = "host_text_preference";
	/**
	 * Key ID for the shared preference port.
	 */
	public final static String PORT_PREFERENCE_KEY = "port_text_preference";

	private String TAG = ServerPreferences.class.getSimpleName();
	private static boolean initialized = false;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.preferences);
		PreferenceScreen preferenceScreen = getPreferenceScreen();
		preferenceScreen.getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
		initPreferences();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(HOST_PREFERENCE_KEY)) {
			ConnectionHandler.host = sharedPreferences.getString(
					HOST_PREFERENCE_KEY, "");
		} else if (key.equals(PORT_PREFERENCE_KEY)) {
			String portStr = sharedPreferences.getString(PORT_PREFERENCE_KEY,
					"");
			try {
				int port = Integer.parseInt(portStr);
				ConnectionHandler.port = port;
			} catch (NumberFormatException ex) {
				Log.v(TAG, "Invalid value input '" + portStr + "'");
			}
		}
		bindService(new Intent(this, InternetConnectionManager.class),
				mConnection, Context.BIND_AUTO_CREATE);
		unbindService(mConnection);

	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			InternetConnectionManager mManager = ((InternetConnectionManager.LocalBinder) service)
					.getService();
			mManager.checkConnectivity();
			Log.i(TAG, "checking connectivity");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

	};

	// initialize preferences
	private void initPreferences() {
		if (!initialized) {
			initHostPreference();
			initPortPreference();
			initialized = true;
		}
	}

	private void initHostPreference() {
		EditTextPreference hostPreference = (EditTextPreference) findPreference(HOST_PREFERENCE_KEY);
		hostPreference.setText(ConnectionHandler.host);
	}

	private void initPortPreference() {
		EditTextPreference portPreference = (EditTextPreference) findPreference(PORT_PREFERENCE_KEY);
		portPreference.setText(ConnectionHandler.port + "");
	}
}
