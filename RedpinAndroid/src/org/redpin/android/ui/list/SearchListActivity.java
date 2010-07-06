/**
 *  Filename: SearchListActivity.java (in org.repin.android.ui.list)
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
import org.redpin.android.core.Map;
import org.redpin.android.db.EntityHomeFactory;
import org.redpin.android.db.LocationHome;
import org.redpin.android.provider.RedpinContract;
import org.redpin.android.ui.MapViewActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * {@link ListActivity} that displays {@link Location}s grouped by {@link Map}s an lets the user search them.
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class SearchListActivity extends ListActivity implements
		OnItemClickListener, TextWatcher {

	private LocationHome locHome;
	@SuppressWarnings("unused")
	private String TAG = SearchListActivity.class.getSimpleName();
	private static final String COLUMN_PLACEHOLDER = "_COLUMN_";
	private static final String TEXT_PLACEHOLDER = "_TEXT_";
	private static final String FILTER_QUERY = COLUMN_PLACEHOLDER + " LIKE '%"
			+ TEXT_PLACEHOLDER + "%' ";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);

		View v = findViewById(R.id.list_view_topbar_text);
		if (v instanceof TextView) {
			((TextView) v).setText(R.string.list_view_topbar_searchlist);
		}

		setListAdapter(new GroupedListAdapter(this, null, null));
		ListView lv = getListView();
		// lv.setTextFilterEnabled(true);
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
	public void afterTextChanged(Editable s) {
		// do nothing here
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// do nothing here
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String text = s.toString();
		setListAdapterWithFilter(text);
	}

	/**
	 * Sets the filtered {@link GroupedListAdapter}
	 * 
	 * @param text
	 *            The text that is searched
	 */
	private void setListAdapterWithFilter(String text) {
		String filterOnMap = FILTER_QUERY.replace(COLUMN_PLACEHOLDER,
				RedpinContract.Map.NAME).replace(TEXT_PLACEHOLDER, text);
		String filterOnLocation = FILTER_QUERY.replace(COLUMN_PLACEHOLDER,
				RedpinContract.Location.SYMBOLIC_ID).replace(TEXT_PLACEHOLDER,
				text);
		setListAdapter(new GroupedListAdapter(this, filterOnMap,
				filterOnLocation));
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
		Location l = getClickedItem(parent, position);
		if (l == null)
			return;

		Intent i = new Intent(this, MapViewActivity.class);
		i.setData(RedpinContract.Location.buildQueryUri(id));
		startActivity(i);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onSearchRequested() {
		// do nothing here
		return true;

	}

}
