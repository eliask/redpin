package org.redpin.android.test;

import org.redpin.android.provider.RedpinContentProvider;
import org.redpin.android.provider.RedpinContract;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class ContentProviderTest extends
		android.test.ProviderTestCase2<RedpinContentProvider> {

	public ContentProviderTest() {
		super(RedpinContentProvider.class, RedpinContract.AUTHORITY);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		getMockContentResolver().delete(RedpinContract.Map.CONTENT_URI, null,
				null);
		super.tearDown();
	}

	private Uri insertMap(int rId, String name, String url) {
		ContentValues values = new ContentValues();
		values.put(RedpinContract.Map.REMOTE_ID, rId);
		values.put(RedpinContract.Map.NAME, name);
		values.put(RedpinContract.Map.URL, url);

		return getMockContentResolver().insert(RedpinContract.Map.CONTENT_URI,
				values);
	}

	private int updateMap(Uri uri, int rId, String name, String url) {
		ContentValues values = new ContentValues();

		values.put(RedpinContract.Map.REMOTE_ID, rId);
		values.put(RedpinContract.Map.NAME, name);
		values.put(RedpinContract.Map.URL, url);

		return getMockContentResolver().update(uri, values, null, null);
	}

	private void queryMap(Uri uri, String prefix, int rId, String name,
			String url) {
		Cursor c = getMockContentResolver().query(uri, null, null, null, null);

		assertTrue(prefix + "cursor is empty", c.moveToFirst());
		assertTrue(prefix + "not only result", c.isLast());

		assertEquals(prefix + "rId wrong", rId, c.getInt(c
				.getColumnIndex(RedpinContract.Map.REMOTE_ID)));
		assertEquals(prefix + "name wrong", name, c.getString(c
				.getColumnIndex(RedpinContract.Map.NAME)));
		assertEquals(prefix + "url wrong", url, c.getString(c
				.getColumnIndex(RedpinContract.Map.URL)));

	}

	private int deleteMap(Uri uri) {
		return getMockContentResolver().delete(uri, null, null);
	}

	private Uri insertLocation(Uri uri, int rId, String sID, int x, int y) {
		ContentValues values = new ContentValues();

		values.put(RedpinContract.Location.REMOTE_ID, rId);
		values.put(RedpinContract.Location.SYMBOLIC_ID, sID);
		values.put(RedpinContract.Location.X, x);
		values.put(RedpinContract.Location.Y, y);

		return getMockContentResolver().insert(uri, values);
	}

	private int updateLocation(Uri uri, int rId, String sID, int x, int y) {
		ContentValues values = new ContentValues();

		values.put(RedpinContract.Location.REMOTE_ID, rId);
		values.put(RedpinContract.Location.SYMBOLIC_ID, sID);
		values.put(RedpinContract.Location.X, x);
		values.put(RedpinContract.Location.Y, y);

		return getMockContentResolver().update(uri, values, null, null);
	}

	private void queryLocation(Uri uri, String prefix, int rId, String sID,
			int x, int y, long mapId) {
		Cursor c = getMockContentResolver().query(uri, null, null, null, null);

		assertTrue(prefix + "cursor is empty", c.moveToFirst());
		assertTrue(prefix + "not only result", c.isLast());

		assertEquals(prefix + "rId wrong", rId, c.getInt(c
				.getColumnIndex(RedpinContract.Location.REMOTE_ID)));
		assertEquals(prefix + "symbolic id wrong", sID, c.getString(c
				.getColumnIndex(RedpinContract.Location.SYMBOLIC_ID)));
		assertEquals(prefix + "x wrong", x, c.getInt(c
				.getColumnIndex(RedpinContract.Location.X)));
		assertEquals(prefix + "y wrong", y, c.getInt(c
				.getColumnIndex(RedpinContract.Location.Y)));
		assertEquals(prefix + "map id wrong", mapId, c.getLong(c
				.getColumnIndex(RedpinContract.Location._MAP_ID)));

	}

	private int deleteLocation(Uri uri) {
		return getMockContentResolver().delete(uri, null, null);
	}

	public void test_Locations() {

		int mapRId = 10;
		String mapName = "IFW A";
		String mapURL = "URL";
		Uri map = insertMap(mapRId, mapName, mapURL);
		long mapId = ContentUris.parseId(map);

		int locRid = 100;
		String sID = "symbolicID";
		int x = 100;
		int y = 200;

		Uri newLoc = RedpinContract.Location.buildInsertUri(map);
		assertEquals(newLoc, RedpinContract.Location.buildInsertUri(mapId));
		Uri location = insertLocation(newLoc, locRid, sID, x, y);
		long locId = ContentUris.parseId(location);
		assertNotNull(location);

		queryLocation(location, "maps/#/locations/#: ", locRid, sID, x, y,
				mapId);
		queryLocation(RedpinContract.Location.CONTENT_URI, "locations: ",
				locRid, sID, x, y, mapId);
		queryLocation(RedpinContract.Location.CONTENT_URI.buildUpon()
				.appendPath(location.getLastPathSegment()).build(),
				"locations/# ", locRid, sID, x, y, mapId);

		locRid = 101;
		sID = "symbolicID up";
		x = 101;
		y = 201;

		assertEquals("maps/#/locations/#: update not successful", 1,
				updateLocation(location, locRid, sID, x, y));
		queryLocation(location, "maps/#/locations/# update: ", locRid, sID, x,
				y, mapId);

		locRid = 102;
		sID = "symbolicID up2";
		x = 102;
		y = 202;

		assertEquals("locations/#: update not successful", 1, updateLocation(
				RedpinContract.Location.CONTENT_URI.buildUpon().appendPath(
						location.getLastPathSegment()).build(), locRid, sID, x,
				y));
		queryLocation(RedpinContract.Location.CONTENT_URI.buildUpon()
				.appendPath(location.getLastPathSegment()).build(),
				"locations/# update: ", locRid, sID, x, y, mapId);

		assertEquals("maps/#/locations/#: delete not successful", 1,
				deleteLocation(location));

		location = insertLocation(newLoc, locRid, sID, x, y);
		locId = ContentUris.parseId(location);
		assertEquals("locations/#: delete not sucessfull", 1,
				deleteLocation(RedpinContract.Location.CONTENT_URI.buildUpon()
						.appendPath(location.getLastPathSegment()).build()));

	}

	public void test_RemoteParameter() {
		int rId = 10;
		String name = "IFW A";
		String url = "URL";

		Uri map = insertMap(rId, name, url);
		Uri testUri = ContentUris.withAppendedId(
				RedpinContract.Map.CONTENT_URI, rId).buildUpon()
				.appendQueryParameter(RedpinContract.REMOTE_PARAMETER, "1")
				.build();

		queryMap(testUri, "remote map test: ", rId, name, url);

		name = "IFW A up";
		url = "URL up";
		rId = 100;

		assertEquals("nothing updated", 1, updateMap(testUri, rId, name, url));

		// missing remote parameter
		assertEquals(0, updateMap(ContentUris.withAppendedId(
				RedpinContract.Map.CONTENT_URI, rId), rId, name, url));

		// test invalid usage of remote parameter

		// query map/#rId/locations/#
		testUri = RedpinContract.Location.buildFilterUri(testUri);
		Exception ex = null;
		try {
			getMockContentResolver().query(testUri, null, null, null, null);
			fail("execption not thrown");
		} catch (IllegalArgumentException e) {
			ex = e;
		}

		assertTrue("execption not thrown",
				ex instanceof IllegalArgumentException);

		// update map/#rId/locations/#
		ex = null;
		try {
			getMockContentResolver().update(testUri, null, null, null);
			fail("execption not thrown");
		} catch (IllegalArgumentException e) {
			ex = e;
		}

		assertTrue("execption not thrown",
				ex instanceof IllegalArgumentException);

		int locRid = 100;
		String sID = "symbolicID";
		int x = 100;
		int y = 200;

		// insert map/#rId/locations/#
		ex = null;
		try {
			getMockContentResolver().insert(testUri, null);
			fail("execption not thrown");
		} catch (IllegalArgumentException e) {
			ex = e;
		}
		assertTrue("execption not thrown",
				ex instanceof IllegalArgumentException);

		insertLocation(RedpinContract.Location.buildInsertUri(map), locRid,
				sID, x, y);

		testUri = ContentUris.withAppendedId(testUri, locRid);
		sID = "symbolicID up2";
		x = 102;
		y = 202;

		assertEquals(1, updateLocation(testUri, locRid, sID, x, y));

	}

	public void test_Map() {
		int rId = 10;
		String name = "IFW A";
		String url = "URL";

		Uri uri = insertMap(rId, name, url);

		queryMap(uri, "", rId, name, url);

		name = "IFW A up";
		url = "URL up";
		rId = 100;

		assertEquals("nothing updated", 1, updateMap(uri, rId, name, url));
		queryMap(uri, "update: ", rId, name, url);

		assertEquals("not deleted", 1, deleteMap(uri));

	}

}
