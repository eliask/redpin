/**
 *  Filename: MapCursorAdapter.java (in org.repin.android.ui.list)
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
package org.redpin.android.ui.list;

import org.redpin.android.R;
import org.redpin.android.core.Map;
import org.redpin.android.provider.RedpinContract;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.widget.SimpleCursorAdapter;

/**
 * {@link SimpleCursorAdapter} for {@link Map}s that supports filtering.
 *
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class MapCursorAdapter extends SimpleCursorAdapter {

	private ListActivity activity;
	private Uri uri;

	/**
	 *
	 * @param activity
	 *            {@link ListActivity} that uses this CursorAdapter
	 * @param uri
	 *            {@link Uri} of the {@link Map}s to be displayed
	 */
	public MapCursorAdapter(ListActivity activity, Uri uri) {
		super(activity, R.layout.list_row, activity.managedQuery(uri, null,
				null, null, null), new String[] { RedpinContract.Map.NAME },
				new int[] { R.id.list_row_label });

		this.activity = activity;
		this.uri = uri;

		setStringConversionColumn(getCursor().getColumnIndex(
				RedpinContract.Map.NAME));

	}

	/**
	 * Filters the {@link Map} by its name.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		if (constraint == null || constraint.length() == 0) {
			return activity.managedQuery(uri, null, null, null, null);
		}

		return activity.managedQuery(uri, null, RedpinContract.Map.NAME
				+ " LIKE '%" + constraint.toString().replace("'", "") + "%'",
				null, null);
	}

}
