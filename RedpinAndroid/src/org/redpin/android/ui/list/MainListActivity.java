/**
 *  Filename: MainListActivity.java (in org.repin.android.ui.list)
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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * {@link ListActivity} that allows a user to select whether the
 * {@link Location}s or {@link Map}s should be shown.
 * 
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class MainListActivity extends ListActivity implements
		OnItemClickListener {

	private static final int ITEM_MAPS = 0;
	private static final int ITEM_LOCATIONS = 1;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_row, R.id.list_row_label, getResources()
						.getStringArray(R.array.main_list_items));
		setListAdapter(adapter);

		ListView lv = getListView();

		lv.setClickable(true);
		lv.setOnItemClickListener(this);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent i;
		switch (position) {
		case ITEM_MAPS:
			i = new Intent(MainListActivity.this, MapListActivity.class);
			startActivity(i);
			break;
		case ITEM_LOCATIONS:
			i = new Intent(MainListActivity.this, LocationListActivity.class);
			startActivity(i);
			break;
		}

	}

}
