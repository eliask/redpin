/**
 *  Filename: MapViewActivity.java (in org.repin.android.ui)
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
package org.redpin.android.ui;

import org.redpin.android.ApplicationContext;
import org.redpin.android.R;
import org.redpin.android.core.Fingerprint;
import org.redpin.android.core.Location;
import org.redpin.android.core.Map;
import org.redpin.android.core.Measurement;
import org.redpin.android.core.Vector;
import org.redpin.android.core.measure.WiFiReading;
import org.redpin.android.net.InternetConnectionManager;
import org.redpin.android.net.Response;
import org.redpin.android.net.SynchronizationManager;
import org.redpin.android.net.home.FingerprintRemoteHome;
import org.redpin.android.net.home.LocationRemoteHome;
import org.redpin.android.net.home.RemoteEntityHomeCallback;
import org.redpin.android.net.wifi.WifiSniffer;
import org.redpin.android.ui.list.MainListActivity;
import org.redpin.android.ui.list.SearchListActivity;
import org.redpin.android.ui.mapview.MapView;
import org.redpin.android.util.ExceptionReporter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Main activity of the client that displays maps and locations
 *
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class MapViewActivity extends Activity {
	private static final String TAG = MapViewActivity.class.getSimpleName();
	MapView mapView;
	ImageButton locateButton;
	ImageButton addLocationButton;
	TextView mapName;
	ProgressDialog progressDialog;

	WifiSniffer mWifiService;
	Location mLocation;

	private RelativeLayout mapTopBar;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationContext.init(getApplicationContext());
		ExceptionReporter.register(this);

		registerReceiver(connectionChangeReceiver, new IntentFilter(
				InternetConnectionManager.CONNECTIVITY_ACTION));
		startService(new Intent(MapViewActivity.this,
				SynchronizationManager.class));
		bindService(new Intent(this, InternetConnectionManager.class),
				mICMConnection, Context.BIND_AUTO_CREATE);

		 startService(new Intent(MapViewActivity.this,InternetConnectionManager.class));


		startWifiSniffer();
		/*
		 * bindService(new Intent(this, WifiSniffer.class), mWifiConnection,
		 * Context.BIND_AUTO_CREATE); registerReceiver(wifiReceiver, new
		 * IntentFilter( WifiSniffer.WIFI_ACTION));
		 */

		setContentView(R.layout.map_view);
		mapView = (MapView) findViewById(R.id.map_view_component);
		mapName = (TextView) findViewById(R.id.map_name);
		mapTopBar = (RelativeLayout) findViewById(R.id.map_topbar);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mapTopBar.setVisibility(View.GONE);
		}

		addLocationButton = (ImageButton) findViewById(R.id.add_location_button);
		addLocationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addNewLocation();
			}
		});

		locateButton = (ImageButton) findViewById(R.id.locate_button);
		locateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				locate();
			}
		});
		progressDialog = new ProgressDialog(this);
		//progressDialog.setCancelable(false);
		progressDialog.setCancelable(true);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(getText(R.string.taking_measurement));

		setOnlineMode(false);


		restoreState();

		show();

	}

	/**
	 * Starts the setting screen
	 *
	 * @param target {@link View} that called this method
	 */
	public void button_Settings(View target) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	private static final String pref_url = "url";
	private static final String pref_scrollX = "x";
	private static final String pref_scrollY = "y";

	/**
	 * Saves some of the {@link MapView} state
	 */
	private void saveState() {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putString(pref_url, mapView.getUrl());
		int[] scroll = mapView.getScrollXY();
		edit.putInt(pref_scrollX, scroll[0]);
		edit.putInt(pref_scrollY, scroll[1]);
		edit.commit();
	}

	/**
	 * Restores the {@link MapView} to show the last shown map
	 */
	private void restoreState() {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		String mapUrl = preferences.getString(pref_url, null);
		int scrollX = preferences.getInt(pref_scrollX, 0);
		int scrollY = preferences.getInt(pref_scrollY, 0);

		if (getIntent().getData() == null && mapUrl != null) {
			getIntent().setData(Uri.parse(mapUrl));
			preferences.edit().clear().commit();
			mapView.requestScroll(scrollX, scrollY, true);
		}

		preferences.edit().clear().commit();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDestroy() {
		saveState();

		unregisterReceiver(connectionChangeReceiver);

		stopWifiSniffer();

		stopService(new Intent(MapViewActivity.this,
				SynchronizationManager.class));

		 stopService(new Intent(MapViewActivity.this,InternetConnectionManager.class));

		unbindService(mICMConnection);

		super.onDestroy();
	}

	/**
	 * Shows the {@link MapView}
	 */
	protected void show() {
		getIntent().resolveType(this);
		mapView.show(getIntent().getData());
		Map m = mapView.getCurrentMap();

		if (m != null) {
			mapName.setText(m.getMapName());
		}
	}

	/**
	 * Displays the current location on the map
	 * @param loc The current estimated location
	 */
	protected void showLocation(Location loc) {
		if (loc == null)
			return;

		Map m = (Map) loc.getMap();

		if (m != null) {
			mapName.setText(m.getMapName());
		}

		mapView.showLocation(loc, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		show();
	}

	/**
	 * Initiates a scan for a new measurement, creates a location and afterwards displays it on the map
	 */
	private void addNewLocation() {

		Map currentMap = mapView.getCurrentMap();

		if (currentMap == null) {
			new AlertDialog.Builder(this).setPositiveButton(
					android.R.string.ok, null)
					.setTitle(R.string.map_view_title).setMessage(
							R.string.map_view_no_map_selected).create().show();
			Log.w(TAG, "addNewLocation: no current map shown");
			return;
		}

		progressDialog.show();

		Location location = new Location();

		location.setMap(currentMap);

		firstMeasurement = true;
		mLocation = location;
		mWifiService.forceMeasurement();

	}

	/**
	 * Locates the client
	 */
	private void locate() {
		progressDialog.show();

		mLocation = null;
		mWifiService.forceMeasurement();

	}

	/**
	 * Sets the connectivity mode of the view
	 *
	 * @param isOnline <code>True</code> if the client can connect to the server, <code>false</code> otherwise
	 */
	private void setOnlineMode(boolean isOnline) {
		mapView.setModifiable(isOnline);
		locateButton.setEnabled(isOnline);
		addLocationButton.setEnabled(isOnline);

	}

	/**
	 * Starts the sniffer and registers the receiver
	 */
	private void startWifiSniffer() {
		bindService(new Intent(this, WifiSniffer.class), mWifiConnection,
				Context.BIND_AUTO_CREATE);
		registerReceiver(wifiReceiver,
				new IntentFilter(WifiSniffer.WIFI_ACTION));
		Log.i(TAG, "Started WifiSniffer");
	}

	/**
	 * Stops the sniffer and unregisters the receiver
	 */
	private void stopWifiSniffer() {
		if (mWifiService != null) {
			mWifiService.stopMeasuring();
		}
		unbindService(mWifiConnection);
		unregisterReceiver(wifiReceiver);
		Log.i(TAG, "Stopped WifiSniffer");
	}

	/**
	 * {@link InternetConnectionManager} {@link ServiceConnection} to check
	 * current online state
	 */
	private ServiceConnection mICMConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			InternetConnectionManager mManager = ((InternetConnectionManager.LocalBinder) service)
					.getService();
			setOnlineMode(mManager.isOnline());
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

	};

	/**
	 * Receives notifications about connectivity changes
	 */
	private BroadcastReceiver connectionChangeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			setOnlineMode((intent.getFlags() & InternetConnectionManager.ONLINE_FLAG)== InternetConnectionManager.ONLINE_FLAG);
		}

	};

	private boolean firstMeasurement = false;
	/**
	 * Receives notifications about new available measurements
	 */
	private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			Measurement m = mWifiService.retrieveLastMeasurement();

			if (m == null)
				return;

			if (mLocation != null) {
				// Interval Scanning
				Fingerprint fp = new Fingerprint(mLocation, m);
				FingerprintRemoteHome.setFingerprint(fp,
						new RemoteEntityHomeCallback() {

							@Override
							public void onResponse(Response<?> response) {

								if (firstMeasurement) {
									progressDialog.hide();
									mapView.addNewLocation(mLocation);
									firstMeasurement = false;
								}

								Log
										.i(TAG,
												"addNewLocation: setFingerprint successfull");
							}

							@Override
							public void onFailure(Response<?> response) {
								progressDialog.hide();
								Log.i(TAG,
										"addNewLocation: setFingerprint failed: "
												+ response.getStatus() + ", "
												+ response.getMessage());
							}
						});

			} else {
				// Localization
				LocationRemoteHome.getLocation(m,
						new RemoteEntityHomeCallback() {

							@Override
							public void onFailure(Response<?> response) {
								progressDialog.hide();

								new AlertDialog.Builder(MapViewActivity.this).setMessage(response.getMessage()).setPositiveButton(android.R.string.ok, null).create().show();

							}

							@Override
							public void onResponse(Response<?> response) {
								progressDialog.hide();
								Location l = (Location) response.getData();
								showLocation(l);

							}

						});
				mWifiService.stopMeasuring();
			}

		}
	};
	/**
	 * {@link ServiceConnection} for the {@link WifiSniffer}
	 */
	private ServiceConnection mWifiConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mWifiService = ((WifiSniffer.LocalBinder) service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mWifiService = null;
		}

	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, R.id.options_menu_search, 0,
				R.string.options_menu_search_text).setIcon(
				R.drawable.menu_search);
		menu.add(0, R.id.options_menu_listview, 0,
				R.string.options_menu_listview_text).setIcon(
				R.drawable.menu_list_black);
		menu.add(0, R.id.options_menu_add_map, 0,
				R.string.options_menu_add_map_text).setIcon(
				R.drawable.menu_addmap_black);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.options_menu_add_map:
			Intent newmap = new Intent(this, NewMapActivity.class);
			startActivity(newmap);
			return true;
		case R.id.options_menu_listview:
			Intent mainlist = new Intent(this, MainListActivity.class);
			startActivity(mainlist);
			return true;
		case R.id.options_menu_search:
			Intent search = new Intent(this, SearchListActivity.class);
			startActivity(search);
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onSearchRequested() {
		Intent search = new Intent(this, SearchListActivity.class);
		startActivity(search);
		return false;
	}

}
