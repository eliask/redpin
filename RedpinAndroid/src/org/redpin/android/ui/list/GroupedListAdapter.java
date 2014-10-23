/**
 *  Filename: GroupedListAdapter.java (in org.repin.android.ui.list)
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.redpin.android.R;
import org.redpin.android.provider.RedpinContract;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Extended {@link Adapter} that is the bridge between a grouped
 * {@link ListView} and the data that backs the list
 *
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class GroupedListAdapter extends BaseAdapter {

	private final LinkedHashMap<String, SimpleCursorAdapter> sections;
	private final SimpleCursorAdapter maps;
	private ListActivity activity;
	public final static int TYPE_SECTION_HEADER = 0;

	public GroupedListAdapter(ListActivity activity, String filterOnMap,
			String filterOnLocation) {
		super();
		this.activity = activity;
		Uri uri = RedpinContract.Map.CONTENT_URI;

		List<Long> additionalMaps = getMapsToConsider(filterOnMap,
				filterOnLocation);
		int additionalMapsNum = additionalMaps.size();

		if (additionalMapsNum != 0) {
			String listAsString = getListAsString(additionalMaps, ",");
			String where = "(" + filterOnMap + ") OR " + RedpinContract.Map._ID
					+ " IN (" + listAsString + ")";

			maps = new SimpleCursorAdapter(activity, R.layout.list_row_header,
					activity.managedQuery(uri, null, where, null, null),
					new String[] { RedpinContract.Map.NAME },
					new int[] { R.id.list_row_label });
		} else {
			maps = new SimpleCursorAdapter(activity, R.layout.list_row_header,
					activity.managedQuery(uri, null, filterOnMap, null, null),
					new String[] { RedpinContract.Map.NAME },
					new int[] { R.id.list_row_label });
		}

		Cursor c = maps.getCursor();

		sections = new LinkedHashMap<String, SimpleCursorAdapter>(c.getCount());

		while (c.moveToNext()) {
			System.out.println(c.getString(c
					.getColumnIndex(RedpinContract.Map.NAME)));
			long mapId = c.getLong(c.getColumnIndex(RedpinContract.Map._ID));
			String where = null;
			if (additionalMaps.contains(mapId)) { // only one or more locations
													// match
				where = filterOnLocation;
			}
			SimpleCursorAdapter a = new SimpleCursorAdapter(activity,
					R.layout.list_row, activity.managedQuery(
							RedpinContract.Location.buildFilterUri(mapId),
							null, where, null, null),
					new String[] { RedpinContract.Location.SYMBOLIC_ID },
					new int[] { R.id.list_row_label });
			System.out.println(c.getString(c
					.getColumnIndex(RedpinContract.Map.NAME))
					+ ": " + a.getCount());
			sections.put(
					c.getString(c.getColumnIndex(RedpinContract.Map.NAME)), a);
		}

	}

	// for all maps that do not match the query look for all locations that
	// match
	private List<Long> getMapsToConsider(String filterOnMap,
			String filterOnLocation) {
		List<Long> mapsToConsider = new ArrayList<Long>();
		SimpleCursorAdapter notmatchmaps = null;
		if (filterOnMap != null && filterOnLocation != null) {
			notmatchmaps = new SimpleCursorAdapter(activity,
					R.layout.list_row_header, activity.managedQuery(
							RedpinContract.Map.CONTENT_URI, null, " NOT ("
									+ filterOnMap + ")", null, null),
					new String[] { RedpinContract.Map.NAME },
					new int[] { R.id.list_row_label });
			Cursor cursor = notmatchmaps.getCursor();
			while (cursor.moveToNext()) {
				long mapId = cursor.getLong(cursor
						.getColumnIndex(RedpinContract.Map._ID));
				SimpleCursorAdapter a = new SimpleCursorAdapter(activity,
						R.layout.list_row, activity.managedQuery(
								RedpinContract.Location.buildFilterUri(mapId),
								null, filterOnLocation, null, null),
						new String[] { RedpinContract.Location.SYMBOLIC_ID },
						new int[] { R.id.list_row_label });
				if (a.getCount() > 0)
					mapsToConsider.add(mapId);
			}
		}
		return mapsToConsider;
	}

	private String getListAsString(List<Long> list, String separator) {
		String listAsString = "";
		for (Long item : list) {
			if (listAsString.length() != 0)
				listAsString += ",";
			listAsString += item;
		}
		return listAsString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCount() {
		int total = 0;
		for (Adapter adapter : this.sections.values()) {
			total += adapter.getCount() + 1;
		}
		return total;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getViewTypeCount() {
		int total = 1;
		for (Adapter adapter : this.sections.values()) {
			total += adapter.getViewTypeCount();
		}
		return total;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getItemViewType(int position) {
		int type = 1;
		for (Object section : this.sections.keySet()) {
			SimpleCursorAdapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if (position == 0)
				return TYPE_SECTION_HEADER;
			if (position < size)
				return type + adapter.getItemViewType(position - 1);

			// otherwise jump into next section
			position -= size;
			type += adapter.getViewTypeCount();
		}
		return -1;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getItem(int position) {
		for (String section : this.sections.keySet()) {
			SimpleCursorAdapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if (position == 0)
				return section;
			if (position < size)
				return adapter.getItem(position - 1);

			// otherwise jump into next section
			position -= size;
		}
		return null;

	}

	public boolean areAllItemsSelectable() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled(int position) {
		return (getItemViewType(position) != TYPE_SECTION_HEADER);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getItemId(int position) {

		for (String section : this.sections.keySet()) {
			SimpleCursorAdapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if (position == 0)
				return 0;
			if (position < size)
				return adapter.getItemId(position - 1);

			// otherwise jump into next section
			position -= size;
		}
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int sectionnum = 0;
		for (Object section : this.sections.keySet()) {
			SimpleCursorAdapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if (position == 0)
				return maps.getView(sectionnum, convertView, parent);
			if (position < size)
				return adapter.getView(position - 1, convertView, parent);

			// otherwise jump into next section
			position -= size;
			sectionnum++;
		}
		return null;

	}

}
