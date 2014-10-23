/**
 *  Filename: EntityHome.java (in org.repin.android.db)
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.redpin.android.ApplicationContext;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Abstract class which provides an partial implementation of all function
 * needed to add, get, update and remove entities to/from database.
 * EntityHomes are used to encapsulate androids {@link ContentProvider}
 *
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 * @param <T>
 *            entity which implements the interface {@link LocalEntity}
 */

public abstract class EntityHome<T extends LocalEntity> implements
		IEntityHome<T, Long> {

	protected ContentResolver resolver;

	/**
	 * Transforms an entity to a set of values
	 *
	 * @param e
	 *            Entity
	 * @return Set of entity values
	 */
	abstract protected ContentValues toContentValues(T e);

	/**
	 * Transforms an database row to an entity
	 *
	 * @param cursor
	 * @return Entity
	 */
	abstract public T fromCursorRow(Cursor cursor);

	/**
	 *
	 * @return {@link ContentProvider} URI of entities handled by this
	 *         {@link EntityHome}
	 */
	abstract protected Uri contentUri();

	/**
	 * Creates an {@link EntityHome} with the {@link ContentResolver} obtained
	 * from {@link ApplicationContext}
	 */
	public EntityHome() {
		this.resolver = ApplicationContext.get().getContentResolver();
	}

	/**
	 *
	 * @param resolver
	 *            {@link ContentResolver}
	 */
	public EntityHome(ContentResolver resolver) {
		this.resolver = resolver;
	}

	/* add */

	/**
	 * Add an entity to the database.
	 *
	 * @param e
	 *            Entity
	 * @return Entity with its generated primary key
	 */

	public T add(T e) {
		ContentValues values = toContentValues(e);
		Uri res = resolver.insert(contentUri(), values);
		e.setLocalId(getId(res));
		return e;
	}

	/**
	 * Add a list of entities
	 *
	 * @param list
	 *            {@link List} of entities
	 * @return {@link List} of entities with their generated primary keys
	 */

	public List<T> add(List<T> list) {

		T el;
		List<T> res = new ArrayList<T>(list.size());

		for (T e : list) {
			el = add(e);
			if (el == null) {
				break;
			} else {
				res.add(el);
			}

		}

		return res;
	}

	/* get */

	/**
	 * Get an entity by it's primary key
	 *
	 * @param id
	 *            Primary Key
	 * @return Entity
	 */
	public T getById(Long id) {
		Uri uri = ContentUris.withAppendedId(contentUri(), id);
		List<T> res = fromCursor(resolver.query(uri, null, null, null, null));
		if (res.size() < 1) {
			return null;
		}
		return res.get(0);
	}

	/**
	 * Get all entities
	 *
	 * @return {@link List} of all entities
	 */

	public List<T> getAll() {
		return fromCursor(resolver.query(contentUri(), null, null, null, null));
	}

	/**
	 * Get an entity
	 *
	 * @param e
	 *            Entity
	 * @return Entity with all fields filled from database
	 */
	public T get(T e) {
		return getById(e.getLocalId());
	}

	/**
	 * Get a {@link List} of entities
	 *
	 * @param list
	 *            {@link List} of primary keys
	 * @return {@link List} of entities
	 */
	public List<T> get(List<T> list) {
		List<T> res = new ArrayList<T>(list.size());
		for (T e : list) {
			res.add(get(e));
		}
		return res;
	}

	/**
	 * Get a {@link List} of entities by their primary keys
	 *
	 * @param ids
	 *            {@link List} of primary keys
	 * @return {@link List} of entities
	 */
	public List<T> getById(List<Long> ids) {
		List<T> res = new ArrayList<T>(ids.size());
		for (Long id : ids) {
			res.add(getById(id));
		}
		return res;
	}

	/* remove */

	/**
	 * Removes an entity by it's primary key
	 *
	 * @param id
	 *            Primary Key
	 * @return True if successful
	 */

	public boolean removeById(Long id) {
		Uri uri = ContentUris.withAppendedId(contentUri(), id);
		return resolver.delete(uri, null, null) == 1;
	}

	/**
	 * Removes an entity
	 *
	 * @param e
	 *            Entity
	 * @return True if successful
	 */

	public boolean remove(T e) {
		return removeById(e.getLocalId());
	}

	/**
	 * Removes a {@link List} of entities
	 *
	 * @param list
	 *            {@link List} of entities
	 * @return True if removal of all entities was successful
	 */

	public boolean remove(List<T> list) {
		boolean ok = true;

		for (T e : list) {
			if (!remove(e))
				break;
		}

		return ok;
	}

	/**
	 * Removes a list of entities by their primary keys
	 *
	 * @param ids
	 *            List of primary keys
	 * @return True if removal of all entities was successful
	 */

	public boolean removeById(List<Long> ids) {
		boolean ok = true;

		for (Long id : ids) {
			if (!removeById(id)) {
				ok = false;
				break;
			}
		}

		return ok;
	}

	/**
	 * Removes all entities from database
	 *
	 * @return True if removal was successful
	 */

	public boolean removeAll() {
		resolver.delete(contentUri(), null, null);
		return true;
	}

	/* update */

	/**
	 * Updates an entity
	 *
	 * @param e
	 *            Entity
	 * @return <code>true</code> if successful
	 */
	public boolean update(T e) {
		if (e.getLocalId() < 0) {
			return false;
		}
		Uri uri = ContentUris.withAppendedId(contentUri(), e.getLocalId());
		return resolver.update(uri, toContentValues(e), null, null) == 1;
	}

	/**
	 * Updates a {@link List} of entities
	 *
	 * @param list
	 *            {@link List} of entities to be updated
	 * @return <code>true</code> if all entities were updated successfully
	 */

	public boolean update(List<T> list) {
		boolean ok = true;

		for (T e : list) {
			if (!update(e)) {
				ok = false;
				break;
			}
		}

		return ok;
	}

	private static final String[] idProjection = new String[] { BaseColumns._ID };

	/**
	 * Gets the database id of an entity-URI
	 *
	 * @param uri
	 *            URI of an entity
	 * @return Database id for entity with specified URI
	 */
	protected long getId(Uri uri) {
		Cursor cursor = resolver.query(uri, idProjection, null, null, null);

		if (cursor.moveToFirst()) {
			long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
			cursor.close();
			return id;
		}
		cursor.close();
		throw new SQLException("no first row");
	}

	/**
	 * Creates entities from cursor
	 *
	 * @param cursor
	 * @return {@link List} of entities
	 */
	protected List<T> fromCursor(Cursor cursor) {
		List<T> res = new LinkedList<T>();

		if (cursor == null) {
			return res;
		}

		while (cursor.moveToNext()) {
			int pos = cursor.getPosition();

			res.add(fromCursorRow(cursor));

			int new_pos = cursor.getPosition();
			if (pos != new_pos) {
				cursor.moveToPosition(pos);
			}

		}

		cursor.close();

		return res;
	}

}
