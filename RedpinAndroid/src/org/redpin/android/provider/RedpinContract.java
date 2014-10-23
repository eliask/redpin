/**
 *  Filename: RedpinContract.java (in org.repin.android.provider)
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
package org.redpin.android.provider;

import android.content.ContentUris;
import android.net.Uri;

/**
 * {@link RedpinContract} is the contract between redpin provider and
 * applications. It contains definitions for the supported URIs and columns
 *
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class RedpinContract {

	/**
	 * The authority for the redpin provider
	 */
	public static final String AUTHORITY = "org.redpin.android.provider";

	/**
	 * An optional parameter used in URIs for the redpin provider to specify
	 * that the used id is an remote id instead of a local id
	 */
	public static final String REMOTE_PARAMETER = "remote";

	/**
	 * Contract for {@link org.redpin.android.core.Map}s
	 *
	 * @author Pascal Brogle (broglep@student.ethz.ch)
	 *
	 */
	public static final class Map implements RemoteBaseColumns {
		/**
		 * Path segment for {@link Map}s
		 */
		public static final String PATH_SEGMENT = "maps";

		/**
		 * {@link Uri} to retrieve all {@link Map}s
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + PATH_SEGMENT);

		/**
		 * Type for {@link Map} items
		 */
		public static final String ITEM_TYPE = "vnd.android.cursor.item/vnd.redpin.maps";

		/**
		 * Type for lists of {@link Map} items
		 */
		public static final String LIST_TYPE = "vnd.android.cursor.dir/vnd.redpin.maps";

		public static final String NAME = "mapName";
		public static final String URL = "mapURL";

		/**
		 *
		 *
		 * @param id
		 *            Local map id
		 * @return {@link Uri} for {@link Map} with specified id
		 */
		public static Uri buildQueryUri(long id) {
			return ContentUris.appendId(CONTENT_URI.buildUpon(), id).build();
		}

	}

	/**
	 * Contract for {@link org.redpin.android.core.Location}s
	 *
	 * @author Pascal Brogle (broglep@student.ethz.ch)
	 *
	 */
	public static final class Location implements RemoteBaseColumns {
		/**
		 * Path segment for {@link Location}s
		 */
		public static final String PATH_SEGMENT = "locations";

		/**
		 * {@link Uri} to retrieve all {@link org.redpin.android.core.Location}s
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + PATH_SEGMENT);

		/**
		 * Type for {@link Location} items
		 */
		public static final String ITEM_TYPE = "vnd.android.cursor.item/vnd.redpin.locations";

		/**
		 * Type for lists of {@link Location} items
		 */
		public static final String LIST_TYPE = "vnd.android.cursor.dir/vnd.redpin.locations";

		public static final String _MAP_ID = "_map_id";
		public static final String SYMBOLIC_ID = "symbolicID";

		public static final String X = "mapXcord";
		public static final String Y = "mapYcord";

		/**
		 *
		 * @param id
		 *            Location id
		 * @return {@link Uri} for {@link Location} with specified id
		 */
		public static Uri buildQueryUri(long id) {
			return ContentUris.appendId(CONTENT_URI.buildUpon(), id).build();
		}

		/**
		 *
		 * @param mapId
		 *            {@link Map} local id
		 * @return {@link Uri} to add a new {@link Location} to the database for
		 *         {@link Map} with specified id
		 */
		public static Uri buildInsertUri(long mapId) {
			return ContentUris.appendId(Map.CONTENT_URI.buildUpon(), mapId)
					.appendPath(PATH_SEGMENT).build();
		}

		/**
		 *
		 * @param mapUri
		 *            {@link Map} {@link Uri}
		 * @return {@link Uri} to add a new {@link Location} to the database for
		 *         {@link Map} with specified {@link Uri}
		 */
		public static Uri buildInsertUri(Uri mapUri) {
			return mapUri.buildUpon().appendPath(PATH_SEGMENT).build();
		}

		/**
		 *
		 * @param mapId
		 *            {@link Map} local id
		 * @return {@link Uri} for retrieving all {@link Location}s for
		 *         {@link Map} with specified id
		 */
		public static Uri buildFilterUri(long mapId) {
			return buildInsertUri(mapId);
		}

		/**
		 *
		 * @param mapUri
		 *            {@link Map} {@link Uri}
		 * @return {@link Uri} for retrieving all {@link Location}s for
		 *         {@link Map} with specified {@link Uri}
		 */
		public static Uri buildFilterUri(Uri mapUri) {
			return buildInsertUri(mapUri);
		}
	}
}
