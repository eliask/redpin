/**
 *  Filename: LocationHome.java (in org.repin.android.db)
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.redpin.android.core.Location;
import org.redpin.android.core.Map;
import org.redpin.android.provider.RedpinContract;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * {@link EntityHome} for {@link Location}s
 * @see EntityHome
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class LocationHome extends EntityHome<Location> {

	private MapHome mapHome = null;
	
	
	/**
	 * @see EntityHome#EntityHome()
	 */
	public LocationHome() {
		super();
	}

	/**
	 * @see EntityHome#EntityHome(ContentResolver)
	 */
	public LocationHome(ContentResolver resolver) {
		super(resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Uri contentUri() {
		return RedpinContract.Location.CONTENT_URI;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Location> fromCursor(Cursor cursor) {
		List<Location> res = new LinkedList<Location>();

		if (cursor == null) {
			return res;
		}

		HashMap<Long, Map> hashmap = new HashMap<Long, Map>();
		if (cursor.getCount() > 0) {
			if (mapHome == null) {
				mapHome = new MapHome(resolver);
			}
			List<Map> maps = mapHome.getAll();
			for (Map m : maps) {
				hashmap.put(m.getLocalId(), m);
			}

		}

		while (cursor.moveToNext()) {

			Location loc = new Location();
			loc.setLocalId(cursor.getLong(cursor
					.getColumnIndex(RedpinContract.Location._ID)));
			loc.setRemoteId(cursor.getInt(cursor
					.getColumnIndex(RedpinContract.Location.REMOTE_ID)));
			loc.setSymbolicID(cursor.getString(cursor
					.getColumnIndex(RedpinContract.Location.SYMBOLIC_ID)));
			loc.setMapXcord(cursor.getInt(cursor
					.getColumnIndex(RedpinContract.Location.X)));
			loc.setMapYcord(cursor.getInt(cursor
					.getColumnIndex(RedpinContract.Location.Y)));

			long mapId = cursor.getLong(cursor
					.getColumnIndex(RedpinContract.Location._MAP_ID));

			loc.setMap(hashmap.get(mapId));

			res.add(loc);

		}
		
		cursor.close();

		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Location fromCursorRow(Cursor cursor) {

		Location loc = new Location();
		loc.setLocalId(cursor.getLong(cursor
				.getColumnIndex(RedpinContract.Location._ID)));
		loc.setRemoteId(cursor.getInt(cursor
				.getColumnIndex(RedpinContract.Location.REMOTE_ID)));
		loc.setSymbolicID(cursor.getString(cursor
				.getColumnIndex(RedpinContract.Location.SYMBOLIC_ID)));
		loc.setMapXcord(cursor.getInt(cursor
				.getColumnIndex(RedpinContract.Location.X)));
		loc.setMapYcord(cursor.getInt(cursor
				.getColumnIndex(RedpinContract.Location.Y)));

		long mapId = cursor.getLong(cursor
				.getColumnIndex(RedpinContract.Location._MAP_ID));

		if (mapHome == null) {
			mapHome = new MapHome(resolver);
		}
		loc.setMap(mapHome.getById(mapId));

		return loc;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ContentValues toContentValues(Location e) {
		ContentValues v = new ContentValues();

		if (e == null) {
			return v;
		}

		v.put(RedpinContract.Location.REMOTE_ID, e.getRemoteId());
		v.put(RedpinContract.Location.SYMBOLIC_ID, e.getSymbolicID());
		v.put(RedpinContract.Location.X, e.getMapXcord());
		v.put(RedpinContract.Location.Y, e.getMapYcord());
		Map m = (Map) e.getMap();
		if (m != null) {
			v.put(RedpinContract.Location._MAP_ID, m.getLocalId());
		}

		return v;
	}

	/**
	 * Gets the {@link Location} by its remote id
	 * 
	 * @param id remote id of entity
	 * @return {@link Location} for specified remote id if available, otherwise <code>null</code>
	 */
	public Location getByRemoteId(Integer id) {
		Uri uri = ContentUris.appendId(contentUri().buildUpon(), id)
				.appendQueryParameter(RedpinContract.REMOTE_PARAMETER, "1")
				.build();

		List<Location> res = fromCursor(resolver.query(uri, null, null, null,
				null));
		if (res.size() < 1) {
			return null;
		}
		return res.get(0);
	}

	/**
	 * Gets all locations for a specific map
	 * 
	 * @param map {@link Map}
	 * @return {@link List} of {@link Location} that are on the {@link Map}
	 */
	public List<Location> getListByMap(Map map) {
		Uri uri = RedpinContract.Location.buildFilterUri(map.getLocalId());

		return fromCursor(resolver.query(uri, null, null, null, null));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean update(Location e) {
		Map m = (Map) e.getMap();
		if (m == null) {
			return false;
		}
		checkImplicitAddMap(m);
		return super.update(e);
	}

	/**
	 * Checks if the {@link Map} is not yet stored in the database.
	 * If it is not, the {@link Map} is added
	 * 
	 * @param m {@link Map}
	 */
	private void checkImplicitAddMap(Map m) {
		if (m.getLocalId() < 0) {			
			if (mapHome == null) {
				mapHome = new MapHome(resolver);
			}
			
			if (m.getRemoteId() != null && m.getRemoteId() > -1) {
				Map t = mapHome.getByRemoteId(m.getRemoteId());
				if (t != null) {
					m.setLocalId(t.getLocalId());
				} else {
					t = mapHome.add(m);
					m.setLocalId(t.getLocalId());
				}
			}			
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Location add(Location e) {
		Map m = (Map) e.getMap();
		if (m == null) {
			return null;
		}

		checkImplicitAddMap(m);

		ContentValues values = toContentValues(e);
		Uri res = resolver.insert(RedpinContract.Location.buildInsertUri(m
				.getLocalId()), values);
		e.setLocalId(getId(res));
		return e;
	}

}
