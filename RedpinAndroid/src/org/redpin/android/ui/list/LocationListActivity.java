/**
 *  Filename: LocationListActivity.java (in org.repin.android.ui.list)
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
package org.redpin.android.ui.list;

import org.redpin.android.R;
import org.redpin.android.core.Location;
import org.redpin.android.db.EntityHomeFactory;
import org.redpin.android.db.LocationHome;
import org.redpin.android.net.InternetConnectionManager;
import org.redpin.android.net.home.LocationRemoteHome;
import org.redpin.android.provider.RedpinContract;
import org.redpin.android.ui.MapViewActivity;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * {@link ListActivity} that displays {@link Location}s for a specific
 * {@link Uri} passed with the {@link Intent}.
 * 
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class LocationListActivity extends ListActivity implements
		OnItemClickListener, OnCreateContextMenuListener, TextWatcher {

	private LocationHome locHome;
	private String TAG = LocationListActivity.class.getSimpleName();
	private boolean isOnline = false;

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		registerReceiver(connectionChangeReceiver, new IntentFilter(
				InternetConnectionManager.CONNECTIVITY_ACTION));
		
		bindService(new Intent(this, InternetConnectionManager.class), mConnection, Context.BIND_AUTO_CREATE);
		

		setContentView(R.layout.list_view);
		View v = findViewById(R.id.list_view_topbar_text);
		if (v instanceof TextView) {
			((TextView) v).setText(R.string.list_view_topbar_locationlist);
		}

		ListView lv = getListView();
		//lv.setTextFilterEnabled(true);
		registerForContextMenu(lv);
		lv.setClickable(true);
		lv.setOnItemClickListener(this);
		
		View searchView = (View) findViewById(R.id.filter_layout);
		searchView.setVisibility(View.VISIBLE);
		EditText filter = (EditText) findViewById(R.id.filter);
		filter.addTextChangedListener(this);
		
		show();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDestroy() {
		unbindService(mConnection);
		unregisterReceiver(connectionChangeReceiver);
		super.onDestroy();
	}

	/**
	 * Shows the {@link Location} list for the {@link Uri} contained in the
	 * {@link Intent}. If there is no {@link Uri} in the {@link Intent} then all
	 * {@link Location}s are displayed.
	 */
	private void show() {

		Intent intent = getIntent();
		if (intent.getData() == null) {
			intent.setData(RedpinContract.Location.CONTENT_URI);
		}

		setListAdapter(new LocationCursorAdapter(this, intent.getData()));

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
	 * Gets the clicked {@link Location}
	 * 
	 * @param parent
	 *            {@link AdapterView}
	 * @param position
	 *            Position of the click
	 * @return {@link Location} that was clicked
	 */
	public Location getClickedItem(AdapterView<?> parent, int position) {
		Location l = null;

		if (position >= 0) {
			Cursor c = (Cursor) parent.getItemAtPosition(position);
			if (locHome == null) {
				locHome = EntityHomeFactory.getLocationHome();
			}
			l = locHome.fromCursorRow(c);
		}

		return l;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if (position < 0)
			return;

		Intent i = new Intent(this, MapViewActivity.class);
		i.setData(RedpinContract.Location.buildQueryUri(id));
		startActivity(i);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		if (getListAdapter().isEmpty())
			return false;

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
		if (cursor == null) {
			return;
		}

		menu.setHeaderTitle(cursor.getString(cursor
				.getColumnIndex(RedpinContract.Location.SYMBOLIC_ID)));

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_location_list, menu);

		MenuItem deleteItem = menu.getItem(0);
		deleteItem.setEnabled(isOnline);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.menu_delete:

			Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
			if (cursor == null) {
				return true;
			}

			if (locHome == null) {
				locHome = EntityHomeFactory.getLocationHome();
			}
			Location l = locHome.fromCursorRow(cursor);
			// mapHome.remove(m);
			Log.i(TAG, "deleting location: " + l);
			LocationRemoteHome.removeLocation(l);

			return true;

		}
		return false;
	}

	/**
	 * {@link InternetConnectionManager} {@link BroadcastReceiver} for
	 * retrieving Internet connection changes to enable or disable deletion of
	 * {@link Location}s.
	 */
	private BroadcastReceiver connectionChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			isOnline = intent.getFlags() == InternetConnectionManager.ONLINE_FLAG;
		}
	};
	
	/**
	 * {@link InternetConnectionManager} {@link ServiceConnection} to check current online state
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			InternetConnectionManager mManager = ((InternetConnectionManager.LocalBinder)service).getService();
			isOnline = mManager.isOnline();
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
		
	};
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterTextChanged(Editable s) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		CursorAdapter adapter = (CursorAdapter) getListAdapter();
		adapter.getFilter().filter(s);		
	}
}
