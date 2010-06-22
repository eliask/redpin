package org.redpin.android.test;

import java.util.ArrayList;
import java.util.List;

import org.redpin.android.core.Location;
import org.redpin.android.core.Map;
import org.redpin.android.db.LocationHome;
import org.redpin.android.db.MapHome;
import org.redpin.android.provider.RedpinContentProvider;
import org.redpin.android.provider.RedpinContract;

/**
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class EntityHomeTest extends
		android.test.ProviderTestCase2<RedpinContentProvider> {

	public EntityHomeTest() {
		super(RedpinContentProvider.class, RedpinContract.AUTHORITY);
	}

	Map map, map2, map3, map4;
	List<Map> maplist;
	MapHome mh;

	Location loc, loc2, loc3, loc4;
	LocationHome lh;

	@Override
	protected void setUp() throws Exception {

		super.setUp();

		map = new Map();
		map.setMapName("Test Map Name");
		map.setMapURL("http://www.testmap.ch/map.gif");

		map2 = new Map();
		map2.setMapName("Test Map Name 2");
		map2.setMapURL("http://www.testmap.ch/map2.gif");

		map3 = new Map();
		map3.setMapName("Test Map Name 3");
		map3.setMapURL("http://www.testmap.ch/map3.gif");

		map4 = new Map();
		map4.setMapName("Test Map Name 4");
		map4.setMapURL("http://www.testmap.ch/map4.gif");

		maplist = new ArrayList<Map>(4);
		maplist.add(map);
		maplist.add(map2);
		maplist.add(map3);
		maplist.add(map4);

		loc = new Location();
		loc.setSymbolicID("location 1");
		loc.setMap(map);
		loc.setMapXcord(15);
		loc.setMapYcord(16);
		loc.setAccuracy(100);

		loc2 = new Location();
		loc2.setSymbolicID("location 1");
		loc2.setMap(map);
		loc2.setMapXcord(15);
		loc2.setMapYcord(16);
		loc2.setAccuracy(100);

		loc3 = new Location();
		loc3.setSymbolicID("location 1");
		loc3.setMap(map);
		loc3.setMapXcord(15);
		loc3.setMapYcord(16);
		loc3.setAccuracy(100);

		loc4 = new Location();
		loc4.setSymbolicID("location 1");
		loc4.setMap(map2);
		loc4.setMapXcord(15);
		loc4.setMapYcord(16);
		loc4.setAccuracy(100);

		mh = new MapHome(getMockContentResolver());
		lh = new LocationHome(getMockContentResolver());

	}

	@Override
	protected void tearDown() throws Exception {
		getMockContentResolver().delete(RedpinContract.Map.CONTENT_URI, null,
				null);
		super.tearDown();
	}

	private void setupMap(Map m) {
		m = mh.add(m);
		assertNotNull("setup failed", m);
		assertTrue("setup failed", m.getLocalId() > -1);
	}

	private void cleanupMap(Map m) {
		mh.remove(m);
		// assertTrue("clean up failed", mh.remove(m));
	}

	public void test_mapHomeAdd() {
		map = mh.add(map);
		assertNotNull("add failed", map);
		assertTrue("add failed (no id set)", map.getLocalId() > -1);

		// clean up
		cleanupMap(map);
	}

	public void test_mapHomeGet() {
		// setup
		setupMap(map);

		// get
		Map new_map;
		new_map = mh.getById(map.getLocalId());

		assertNotNull("get failed", new_map);
		assertMapEqual(map, new_map, true);

		// clean up
		cleanupMap(map);

	}

	public void test_mapHomeUpdate() {
		// setup
		setupMap(map);

		// update

		final String name = "Test Map Updated";
		final String url = "http://www.testmap.ch/map_updated.gif";

		map.setMapName(name);
		map.setMapURL(url);

		assertTrue("update failed", mh.update(map));

		Map new_map = mh.getById(map.getLocalId());
		assertNotNull("retrieval of updated map failed", new_map);
		assertMapEqual(map, new_map, true);

		// clean up
		cleanupMap(map);
	}

	public void test_mapHomeRemove() {
		// setup
		setupMap(map);

		// delete
		assertTrue("remove failed", mh.remove(map));

	}

	private List<Long> setupMapList(List<Map> ml) {
		ml = mh.add(ml);

		List<Long> idlist = new ArrayList<Long>(ml.size());
		for (Map m : ml) {
			assertTrue("setup failed", m.getLocalId() > -1);
			idlist.add(m.getLocalId());
		}

		return idlist;
	}

	private void cleanupMapList(List<Map> ml) {
		mh.remove(ml);
		// assertTrue("clean up failed", mh.remove(ml));
	}

	public void test_mapHomeListAdd() {
		// add
		maplist = mh.add(maplist);

		List<Long> idlist = new ArrayList<Long>(maplist.size());
		for (Map m : maplist) {
			assertTrue("add: failed (no id set)", m.getLocalId() > -1);
			idlist.add(m.getLocalId());
		}

		// clean up
		cleanupMapList(maplist);
	}

	public void test_mapHomeListGet() {
		// setup
		List<Long> idlist = setupMapList(maplist);

		// get
		List<Map> new_map_list;
		new_map_list = mh.getById(idlist);

		int i = 0;
		for (Map nm : new_map_list) {
			assertNotNull("get failed", nm);
			assertMapEqual(maplist.get(i++), nm, true);
		}

		// clean up
		cleanupMapList(maplist);
	}

	public void test_mapHomeListUpdate() {
		// setup
		List<Long> idlist = setupMapList(maplist);

		// update
		final String name = " Updated";
		final String url = "-updated";

		int i = 0;
		int j;
		for (Map m : maplist) {
			j = i++;
			m.setMapName(maplist.get(j).getMapName() + name);
			m.setMapURL(maplist.get(j).getMapURL() + url);
		}

		assertTrue("update failed", mh.update(maplist));

		List<Map> new_map_list = mh.getById(idlist);
		assertNotNull("retrieval of updated map failed", new_map_list);

		i = 0;

		for (Map nm : new_map_list) {
			j = i++;
			assertMapEqual(maplist.get(j), nm, true);
		}

		// clean up
		cleanupMapList(maplist);
	}

	public void test_mapHomeListRemove() {

		// setup
		setupMapList(maplist);

		// remove
		assertTrue("remove failed", mh.remove(maplist));
	}

	public static void assertMapEqual(Map map, Map new_map, boolean id) {
		assertNotNull("map not null", map);
		assertNotNull("new_map not null", new_map);
		assertEquals("map name not the same", map.getMapName(), new_map
				.getMapName());
		assertEquals("map url not the same", map.getMapURL(), new_map
				.getMapURL());
		if (id) {
			assertTrue("map id not the same", map.getLocalId() == new_map
					.getLocalId());
		}
	}

	private void setupLoc(Location l) {
		l = lh.add(l);
		assertNotNull("setup failed ", l);
		assertTrue("add failed (no id set)", loc.getLocalId() > -1);
	}

	private void cleanupLoc(Location l) {
		lh.remove(l);
		// assertTrue("clean up failed", lh.remove(l));
	}

	private void locationAdd() {

		loc = lh.add(loc);
		assertNotNull("add failed ", loc);
		assertTrue("add failed (no id set)", loc.getLocalId() > -1);

		map = mh.get((Map) loc.getMap());
		assertMapEqual((Map) loc.getMap(), map, true);

	}

	public void test_LocationHomeAdd() {
		// setup
		setupMap(map);

		locationAdd();

		// clean up
		cleanupMap(map);
		cleanupLoc(loc);

	}

	public void test_LocationHomeGet() {
		// setup
		setupMap(map);
		setupLoc(loc);

		// fetch test
		long locid = loc.getLocalId();
		loc = null;
		loc = lh.getById(locid);
		assertMapEqual((Map) loc.getMap(), map, true);

		// clean up
		cleanupMap(map);
		cleanupLoc(loc);
	}

	public void test_LocationHomeGetByMap() {
		// setup
		setupMap(map);
		setupMap(map2);
		setupLoc(loc);
		setupLoc(loc2);
		setupLoc(loc3);
		setupLoc(loc4);

		// test 1

		List<Location> res = lh.getListByMap(map);
		assertEquals("test 1: not all location fetched", 3, res.size());
		assertEquals("test 1: first location not the same", loc.getLocalId(),
				res.get(0).getLocalId());
		assertEquals("test 1: second location not the same", loc2.getLocalId(),
				res.get(1).getLocalId());
		assertEquals("test 1: third location not the same", loc3.getLocalId(),
				res.get(2).getLocalId());

		// test 2
		res = lh.getListByMap(map2);
		assertEquals("test 2: not all location fetched", 1, res.size());
		assertEquals("test 2: first location not the same", loc4.getLocalId(),
				res.get(0).getLocalId());

		// cleanup
		cleanupMap(map);
		cleanupMap(map2);
		cleanupLoc(loc);
		cleanupLoc(loc2);
		cleanupLoc(loc3);
		cleanupLoc(loc4);

	}

	private void locationUpdate() {
		// update test
		loc.setMap(map2);
		long locid = loc.getLocalId();
		assertTrue("update failed", lh.update(loc));
		assertTrue("update did not add map",
				((Map) loc.getMap()).getLocalId() > -1);
		loc = null;

		// test fetch
		loc = lh.getById(locid);
		assertNotNull("get failed after update", loc);
		assertMapEqual(map2, (Map) loc.getMap(), true);
	}

	public void test_LocationHomeUpdate() {
		// setup
		setupMap(map);
		setupMap(map2);
		setupLoc(loc);

		locationUpdate();

		// clean up
		cleanupMap(map);
		cleanupMap(map2);
		cleanupLoc(loc);
	}

	public void test_LocationHomeRemove() {
		// setup
		setupMap(map);
		setupLoc(loc);

		assertTrue("removal of loc failed", lh.remove(loc));

		// clean up
		cleanupMap(map);

	}

}
