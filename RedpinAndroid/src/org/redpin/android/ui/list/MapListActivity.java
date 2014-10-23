/**
 *  Filename: MapListActivity.java (in org.repin.android.ui.list)
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
import org.redpin.android.core.Map;
import org.redpin.android.db.EntityHomeFactory;
import org.redpin.android.db.MapHome;
import org.redpin.android.net.InternetConnectionManager;
import org.redpin.android.net.home.MapRemoteHome;
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
 * {@link ListActivity} that displays {@link Map}s.
 *
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class MapListActivity extends ListActivity implements
		OnItemClickListener, OnCreateContextMenuListener, TextWatcher {

	private MapHome mapHome;
	private String TAG = MapListActivity.class.getSimpleName();

	private boolean isOnline = false;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bindService(new Intent(this, InternetConnectionManager.class), mConnection, Context.BIND_AUTO_CREATE);

		registerReceiver(connectionChangeReceiver, new IntentFilter(
				InternetConnectionManager.CONNECTIVITY_ACTION));

		setContentView(R.layout.list_view);

		Intent intent = getIntent();
		if (intent.getData() == null) {
			intent.setData(RedpinContract.Map.CONTENT_URI);
		}

		View v = findViewById(R.id.list_view_topbar_text);
		if (v instanceof TextView) {
			((TextView) v).setText(R.string.list_view_topbar_maplist);
		}

		setListAdapter(new MapCursorAdapter(this, getIntent().getData()));
		ListView lv = getListView();

		/*
		 * Does pose problems with own search box. after user presses search, focus is transfered to the listview and the keyboard appears again
		 * Possible fix is to use lv.setFocusableInTouchMode(true)
		 */
		//lv.setTextFilterEnabled(true);

		registerForContextMenu(lv);
		lv.setClickable(true);
		lv.setOnItemClickListener(this);

		View searchView = (View) findViewById(R.id.filter_layout);
		searchView.setVisibility(View.VISIBLE);
		EditText filter = (EditText) findViewById(R.id.filter);
		filter.addTextChangedListener(this);


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
	 * Gets the clicked {@link Map}
	 *
	 * @param parent
	 *            {@link AdapterView}
	 * @param position
	 *            Position of the click
	 * @return {@link Map} that was clicked
	 */
	public Map getClickedItem(AdapterView<?> parent, int position) {
		Map m = null;

		if (position >= 0) {
			Cursor c = (Cursor) parent.getItemAtPosition(position);
			if (mapHome == null) {
				mapHome = EntityHomeFactory.getMapHome();
			}
			m = mapHome.fromCursorRow(c);
		}

		return m;
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
		i.setData(RedpinContract.Map.buildQueryUri(id));
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
				.getColumnIndex(RedpinContract.Map.NAME)));

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_map_list, menu);

		MenuItem deleteItem = menu.getItem(1);
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
		case R.id.menu_delete: {
			System.out.println("Delete item with id:" + info.id);

			Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
			if (cursor == null) {
				return true;
			}

			if (mapHome == null) {
				mapHome = EntityHomeFactory.getMapHome();
			}
			Map m = mapHome.fromCursorRow(cursor);
			// mapHome.remove(m);
			Log.i(TAG, "deleting map: " + m);
			MapRemoteHome.removeMap(m);

			return true;
		}

		case R.id.menu_show_locations: {

			Intent i = new Intent(MapListActivity.this,
					LocationListActivity.class);
			i.setData(RedpinContract.Location.buildFilterUri(info.id));
			startActivity(i);

			return true;
		}
		}
		return false;
	}

	/**
	 * {@link InternetConnectionManager} {@link BroadcastReceiver} for
	 * retrieving Internet connection changes.
	 */
	private BroadcastReceiver connectionChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			isOnline = (intent.getFlags() & InternetConnectionManager.ONLINE_FLAG)== InternetConnectionManager.ONLINE_FLAG;
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
