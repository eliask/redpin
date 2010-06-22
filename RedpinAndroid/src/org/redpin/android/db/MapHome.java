/**
 *  Filename: MapHome.java (in org.repin.android.db)
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
package org.redpin.android.db;

import java.util.List;

import org.redpin.android.core.Map;
import org.redpin.android.provider.RedpinContract;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * {@link EntityHome} for {@link Map}s
 * @see EntityHome
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class MapHome extends EntityHome<Map> {

	/**
	 * @see EntityHome#EntityHome()
	 */
	public MapHome() {
		super();
	}

	/**
	 * @see EntityHome#EntityHome(ContentResolver)
	 */
	public MapHome(ContentResolver resolver) {
		super(resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Uri contentUri() {
		return RedpinContract.Map.CONTENT_URI;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map fromCursorRow(Cursor cursor) {
		Map res = new Map();
		res.setLocalId(cursor.getLong(cursor
				.getColumnIndex(RedpinContract.Map._ID)));
		res.setRemoteId(cursor.getInt(cursor
				.getColumnIndex(RedpinContract.Map.REMOTE_ID)));
		res.setMapName(cursor.getString(cursor
				.getColumnIndex(RedpinContract.Map.NAME)));
		res.setMapURL(cursor.getString(cursor
				.getColumnIndex(RedpinContract.Map.URL)));

		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ContentValues toContentValues(Map e) {
		ContentValues v = new ContentValues();
		if (e == null) {
			return v;
		}
		v.put(RedpinContract.Map.REMOTE_ID, e.getRemoteId());
		v.put(RedpinContract.Map.NAME, e.getMapName());
		v.put(RedpinContract.Map.URL, e.getMapURL());

		return v;
	}
	
	/**
	 * Gets the {@link Map} by its remote id
	 * 
	 * @param id remote id of entity
	 * @return {@link Map} for specified remote id if available, otherwise <code>null</code>
	 */
	public Map getByRemoteId(Integer id) {
		Uri uri = ContentUris.appendId(contentUri().buildUpon(), id)
				.appendQueryParameter(RedpinContract.REMOTE_PARAMETER, "1")
				.build();

		List<Map> res = fromCursor(resolver.query(uri, null, null, null, null));
		if (res.size() < 1) {
			return null;
		}
		return res.get(0);
	}

}
