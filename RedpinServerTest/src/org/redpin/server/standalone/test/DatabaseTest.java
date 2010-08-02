package org.redpin.server.standalone.test;



import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.redpin.server.standalone.core.Fingerprint;
import org.redpin.server.standalone.core.Location;
import org.redpin.server.standalone.core.Map;
import org.redpin.server.standalone.core.Measurement;
import org.redpin.server.standalone.core.Vector;
import org.redpin.server.standalone.core.measure.BluetoothReading;
import org.redpin.server.standalone.core.measure.GSMReading;
import org.redpin.server.standalone.core.measure.WiFiReading;
import org.redpin.server.standalone.db.HomeFactory;
import org.redpin.server.standalone.db.homes.BluetoothReadingHome;
import org.redpin.server.standalone.db.homes.FingerprintHome;
import org.redpin.server.standalone.db.homes.GSMReadingHome;
import org.redpin.server.standalone.db.homes.LocationHome;
import org.redpin.server.standalone.db.homes.MapHome;
import org.redpin.server.standalone.db.homes.MeasurementHome;
import org.redpin.server.standalone.db.homes.WiFiReadingHome;
import org.redpin.server.standalone.db.homes.vector.BluetoothReadingVectorHome;
import org.redpin.server.standalone.db.homes.vector.GSMReadingVectorHome;
import org.redpin.server.standalone.db.homes.vector.WiFiReadingVectorHome;

public class DatabaseTest extends TestCase {

	Map map, map2, map3, map4;
	List<Map> maplist;
	MapHome mh;
	
	Location loc, loc2, loc3, loc4;
	LocationHome lh;
	
	WiFiReading wfreading, wfreading2, wfreading3;
	Vector<WiFiReading> vectorWifi;
	WiFiReadingHome wrh;
	WiFiReadingVectorHome wrvh;
	
	
	BluetoothReading breading, breading2;
	Vector<BluetoothReading> vectorBlue;
	BluetoothReadingVectorHome brvh;
	
	GSMReading greading;
	Vector<GSMReading> vectorGSM;
	GSMReadingVectorHome grvh;
	
	Measurement measurement;
	MeasurementHome measurementHome;
	private FingerprintHome fph;
	private BluetoothReadingHome brh;
	private GSMReadingHome grh;
	
	
	
	
	
	
	@Before
	public void setUp() throws Exception {
		
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
		
		wfreading = new WiFiReading();
		wfreading.setBssid("BSSID");
		wfreading.setRssi(100);
		wfreading.setSsid("SSID");
		wfreading.setWepEnabled(true);
		wfreading.setInfrastructure(false);
		
		wfreading2 = new WiFiReading();
		wfreading2.setBssid("BSSID 2");
		wfreading2.setRssi(100);
		wfreading2.setSsid("SSID 2");
		wfreading2.setWepEnabled(true);
		wfreading2.setInfrastructure(false);
		
		wfreading3 = new WiFiReading();
		wfreading3.setBssid("BSSID 3");
		wfreading3.setRssi(100);
		wfreading3.setSsid("SSID 3");
		wfreading3.setWepEnabled(true);
		wfreading3.setInfrastructure(false);
		
		vectorWifi = new Vector<WiFiReading>();
		vectorWifi.add(wfreading);
		vectorWifi.add(wfreading2);
		vectorWifi.add(wfreading3);
		
		
		
		breading = new BluetoothReading();
		breading.setBluetoothAddress("bluetooth adress");
		breading.setFriendlyName("friendly name");
		breading.setMajorDeviceClass("Major");
		breading.setMinorDeviceClass("minor");
		
		breading2 = new BluetoothReading();
		breading2.setBluetoothAddress("bluetooth adress 2");
		breading2.setFriendlyName("friendly name 2");
		breading2.setMajorDeviceClass("Major 2");
		breading2.setMinorDeviceClass("minor 2");
		
		vectorBlue = new Vector<BluetoothReading>();
		vectorBlue.add(breading);
		vectorBlue.add(breading2);
		
		greading = new GSMReading();
		greading.setAreaId("Area ID");
		greading.setCellId("Cell ID");
		greading.setMCC("MCC");
		greading.setMNC("MNC");
		greading.setNetworkName("Network Name");
		greading.setSignalStrength("Singal Strengh");
		
		vectorGSM = new Vector<GSMReading>();
		vectorGSM.add(greading);
		
		measurement = new Measurement();
		measurement.setWiFiReadings(vectorWifi);
		measurement.setBluetoothReadings(vectorBlue);
		measurement.setGSMReadings(vectorGSM);
		
		mh = HomeFactory.getMapHome();
		lh = HomeFactory.getLocationHome();
		wrh = HomeFactory.getWiFiReadingHome();
		wrvh = HomeFactory.getWiFiReadingVectorHome();
		brh = HomeFactory.getBluetoothReadingHome();
		brvh = HomeFactory.getBluetoothReadingVectorHome();
		grh = HomeFactory.getGSMReadingHome();
		grvh = HomeFactory.getGSMReadingVectorHome();	
		measurementHome = HomeFactory.getMeasurementHome();
		fph = HomeFactory.getFingerprintHome();
		
		mh.removeAll();
	}
	
	private void setupMap(Map m) {
		m = mh.add(m);
		assertNotNull("setup failed", m );
		assertNotNull("setup failed", m.getId() );
	}
	
	
	
	private void cleanupMap(Map m) {
		assertTrue("clean up failed", mh.remove(m));
	}
	
	
	@Test
	public void test_MapHomeAdd() {
		map = mh.add(map);	
		assertNotNull("add failed", map );
		assertNotNull("add failed (no id set)", map.getId() );

		//clean up
		cleanupMap(map);
	}
	
	@Test
	public void test_MapHomeGet() {
		//setup
		setupMap(map);
		
		//get
		Map new_map;
		new_map = mh.getById(map.getId());
		
		assertNotNull("get failed", new_map);
		assertMapEqual(map, new_map, true);
		
		//clean up
		cleanupMap(map);
		
	}
	
	public void test_MapHomeUpdate() {
		//setup
		setupMap(map);
		
		//update
		
		final String name = "Test Map Updated";
		final String url = "http://www.testmap.ch/map_updated.gif";
		
		map.setMapName(name);
		map.setMapURL(url);
		
		assertTrue("update failed", mh.update(map));
		
		Map new_map = mh.getById(map.getId());
		assertNotNull("retrieval of updated map failed", new_map);
		assertMapEqual(map, new_map, true);
		
		//clean up
		cleanupMap(map);
	}
	
	@Test
	public void test_MapHomeRemove() {
		//setup
		setupMap(map);
		
		//delete		
		assertTrue("remove failed", mh.remove(map));
		

	}
		
	private List<Integer> setupMapList(List<Map> ml) {
		ml = mh.add(ml);
		
		List<Integer> idlist = new ArrayList<Integer>(ml.size());
		for(Map m: ml) {
			assertNotNull("setup failed", m.getId() );
			idlist.add(m.getId());
		}
		
		return idlist;
	}
	
	private void cleanupMapList(List<Map> ml) {
		assertTrue("clean up failed", mh.remove(ml));
	}
	
	@Test
	public void test_MapHomeListAdd() {
		//add
		maplist = mh.add(maplist);
		
		List<Integer> idlist = new ArrayList<Integer>(maplist.size());
		for(Map m: maplist) {
			assertNotNull("add: failed (no id set)", m.getId() );
			idlist.add(m.getId());
		}
		
		//clean up	
		cleanupMapList(maplist);
	}
	
	@Test
	public void test_MapHomeListGet() {
		//setup
		List<Integer> idlist = setupMapList(maplist);
		
		//get
		List<Map> new_map_list;
		new_map_list = mh.getById(idlist);
		
		
		int i=0;
		for(Map nm: new_map_list) {
			assertNotNull("get failed", nm);
			assertMapEqual(maplist.get(i++), nm, true);
		}
				
		//clean up	
		cleanupMapList(maplist);
	}
	
	public void test_MapHomeListUpdate() {
		//setup
		List<Integer> idlist = setupMapList(maplist);
		
		//update
		final String name = " Updated";
		final String url = "-updated";
		
		int i=0;
		int j;
		for(Map m: maplist) {
			j = i++;
			m.setMapName(maplist.get(j).getMapName() + name);
			m.setMapURL(maplist.get(j).getMapURL() + url);
		}
		
		assertTrue("update failed", mh.update(maplist));
		
		List<Map> new_map_list = mh.getById(idlist);
		assertNotNull("retrieval of updated map failed", new_map_list);

		i=0;
		
		for(Map nm: new_map_list) {
			j = i++;
			assertMapEqual(maplist.get(j), nm, true);			
		}
		
		//clean up	
		cleanupMapList(maplist);
	}
	
	@Test
	public void test_MapHomeListRemove() {
		
		//setup
		setupMapList(maplist);
		
		//remove		
		assertTrue("remove failed", mh.remove(maplist));
	}
	
	
	public static void assertMapEqual(Map map, Map new_map, boolean id) {
		assertNotNull("map not null", map);
		assertNotNull("new_map not null", new_map);
		assertEquals("map name not the same", map.getMapName(), new_map.getMapName());
		assertEquals("map url not the same", map.getMapURL(), new_map.getMapURL());
		if(id) {
			assertEquals("map id not the same", map.getId(), new_map.getId());
		}
	}
	
	private void setupLoc(Location l) {
		l = lh.add(l);
		assertNotNull("setup failed ", l );
		assertNotNull("setup failed (no id set)", l.getId());
	}
	
	private void cleanupLoc(Location l) {
		assertTrue("clean up failed", lh.remove(l));
	}
	
	private void locationAdd() {

		loc = lh.add(loc);
		assertNotNull("add failed ", loc );
		assertNotNull("add failed (no id set)", loc.getId());
		
		map = mh.get((Map) loc.getMap());
		assertMapEqual( (Map) loc.getMap(), map, true);
		
	}
	
	@Test
	public void test_LocationHomeAdd() {
		//setup
		setupMap(map);
		
		
		locationAdd();
		
		//clean up
		cleanupLoc(loc);
		cleanupMap(map);
		
	}

		
	@Test
	public void test_LocationHomeGet() {
		//setup
		setupMap(map);
		setupLoc(loc);
		
		//fetch test
		int locid = loc.getId();
		loc = null;
		loc = lh.getById(locid);
		assertMapEqual( (Map) loc.getMap(), map, true);
		
		//clean up
		cleanupLoc(loc);
		cleanupMap(map);
		
	}
	
	@Test
	public void test_LocationHomeGetByMap() {
		//setup
		setupMap(map);
		setupMap(map2);
		setupLoc(loc);
		setupLoc(loc2);
		setupLoc(loc3);
		setupLoc(loc4);
		
		//test 1
		
		List<Location> res = lh.getListByMap(map);
		assertEquals("test 1: not all location fetched", 3, res.size());
		assertEquals("test 1: first location not the same", loc.getId(), res.get(0).getId());
		assertEquals("test 1: second location not the same", loc2.getId(), res.get(1).getId());
		assertEquals("test 1: third location not the same", loc3.getId(), res.get(2).getId());
		
		//test 2
		res = lh.getListByMap(map2);
		assertEquals("test 2: not all location fetched", 1, res.size());
		assertEquals("test 2: first location not the same", loc4.getId(), res.get(0).getId());
		
		
		
		//cleanup
		cleanupLoc(loc);
		cleanupLoc(loc2);
		cleanupLoc(loc3);
		cleanupLoc(loc4);
		cleanupMap(map);
		cleanupMap(map2);
		
		
	}
	
	private void locationUpdate() {
		//update test
		loc.setMap(map2);
		int locid = loc.getId();
		assertTrue("update failed", lh.update(loc));
		assertNotNull("update did not add map", ((Map) loc.getMap()).getId());
		loc = null;
		
		//test fetch
		loc = lh.getById(locid);
		assertNotNull("get failed after update", loc);
		assertMapEqual(map2, (Map) loc.getMap(), true);
	}
	
	@Test
	public void test_LocationHomeUpdate() {
		//setup
		setupMap(map);
		setupMap(map2);
		setupLoc(loc);
		
		locationUpdate();
		
		
		//clean up
		cleanupLoc(loc);
		cleanupMap(map);
		cleanupMap(map2);
		
	}
	
	@Test
	public void test_LocationHomeRemove() {
		//setup
		setupMap(map);
		setupLoc(loc);
		
		assertTrue("removal of loc failed", lh.remove(loc));
		
		//clean up
		cleanupMap(map);
		
	}
	
	@Test
	public void test_FingerprintHomeImplicitLocationAdd() {
		mh.add((Map)loc.getMap());
		Fingerprint fp = new Fingerprint(loc, measurement);
		fp = fph.add(fp);
		
		assertNotNull(fp);
		assertNotNull(fp.getLocation());
		assertNotNull(fp.getMeasurement());
		
		assertNotNull(fp.getId());
		assertNotNull(((Location)fp.getLocation()).getId());
		assertNotNull(((Measurement)fp.getMeasurement()).getId());
	}
	
	@Test
	public void test_FingerprintHomeGetEmpty() {
		List<Fingerprint> list = fph.getAll();
		assertNotNull(list);
		assertEquals(0, list.size());
	}
	
	@Test
	public void test_FingerprintHomeGetEmptyMeasurement() {
		//setup
		mh.add((Map)loc.getMap());
		lh.add(loc);
		
		Fingerprint fp = null;
		int count = 4;
		for(int i=0; i < count; i++) {
			Measurement m = new Measurement();
			m.setTimestamp(measurement.getTimestamp());
			m.setWiFiReadings(vectorWifi);
			fp = new Fingerprint(loc, m);
			fp = fph.add(fp);
		}
		
		
		
		List<Fingerprint> list = fph.getAll();
		assertNotNull(list);
		assertEquals(count, list.size());
		
		for(int i=0; i < count; i++) {
			fp = list.get(i);
			String msg = "round "+i;			
			
			assertNotNull(msg,fp);
			assertNotNull(msg,fp.getLocation());
			assertNotNull(msg,fp.getMeasurement());
			
			assertNotNull(msg,fp.getId());
			assertNotNull(msg,((Location)fp.getLocation()).getId());
			Measurement m = (Measurement)fp.getMeasurement();
			assertNotNull(msg,m.getId());
			
				
			assertNotNull(msg,m.getWiFiReadings());
			assertEquals(msg,3, m.getWiFiReadings().size());
			
			assertNotNull(msg,m.getBluetoothReadings());
			assertEquals(msg,0, m.getBluetoothReadings().size());
			
			assertNotNull(msg, m.getGsmReadings());
			assertEquals(msg,0, m.getGsmReadings().size());
		}
		
		
	}
	
	
	
	@Test
	public void test_FingerprintHomeGet() {
		//setup
		mh.add((Map)loc.getMap());
		lh.add(loc);
		Fingerprint fp = null;
		for(int i=0; i < 4; i++) {
			fp = new Fingerprint(loc, measurement);
			fp = fph.add(fp);
		}
		
		
		List<Fingerprint> list = fph.getAll();
		assertNotNull(list);
		assertEquals(4, list.size());
		
		
		for(int i=0; i < 4; i++) {
			fp = list.get(i);
			String msg = "round "+i;			
			
			assertNotNull(msg,fp);
			assertNotNull(msg,fp.getLocation());
			assertNotNull(msg,fp.getMeasurement());
			
			assertNotNull(msg,fp.getId());
			assertNotNull(msg,((Location)fp.getLocation()).getId());
			Measurement m = (Measurement)fp.getMeasurement();
			assertNotNull(msg,m.getId());
			
				
			assertNotNull(msg,m.getWiFiReadings());
			assertEquals(msg,3, m.getWiFiReadings().size());
			
			assertNotNull(msg,m.getBluetoothReadings());
			assertEquals(msg,2, m.getBluetoothReadings().size());
			
			assertNotNull(msg, m.getGsmReadings());
			assertEquals(msg,1, m.getGsmReadings().size());
			
		}
		
	}
	
	
	
	@Test
	public void test_FingerprintHomeAdd() {
		mh.add((Map)loc.getMap());
		lh.add(loc);
		Fingerprint fp = new Fingerprint(loc, measurement);
		fp = fph.add(fp);
		
		assertNotNull(fp);
		assertNotNull(fp.getLocation());
		assertNotNull(fp.getMeasurement());
		
		assertNotNull(fp.getId());
		assertNotNull(((Location)fp.getLocation()).getId());
		assertNotNull(((Measurement)fp.getMeasurement()).getId());
		
		List<Measurement> ms = measurementHome.getAll();
		
		assertNotNull(ms);
		assertEquals(1, ms.size());
		
		List<WiFiReading> ws = wrh.getAll();
		assertNotNull(ws);
		assertEquals(3, ws.size());
		
		List<BluetoothReading> bs = brh.getAll();
		assertNotNull(bs);
		assertEquals(2, bs.size());
		
		List<GSMReading> gs = grh.getAll();
		assertNotNull(gs);
		assertEquals(1, gs.size());
	}
	
	@Test
	public void test_FingerprintHomeRemove() {
		//setup
		mh.add((Map)loc.getMap());
		lh.add(loc);
		Fingerprint fp = new Fingerprint(loc, measurement);
		fp = fph.add(fp);
		
		
		assertTrue(fph.remove(fp));
		
				
		List<Measurement> ms = measurementHome.getAll();
		
		assertNotNull(ms);
		assertEquals(0, ms.size());
		
		List<WiFiReading> ws = wrh.getAll();
		assertNotNull(ws);
		assertEquals(0, ws.size());
		
		List<BluetoothReading> bs = brh.getAll();
		assertNotNull(bs);
		assertEquals(0, bs.size());
		
		List<GSMReading> gs = grh.getAll();
		assertNotNull(gs);
		assertEquals(0, gs.size());
	}
	
	@Test
	public void test_FinterprintHomeCount() {
		//setup
		mh.add((Map)loc.getMap());
		lh.add(loc);
		lh.add(loc2);
		int i = 1;
		int j = 1;
		for (; i < 5; i++) {
			measurement.setId(null);
			Fingerprint fp = new Fingerprint(loc, measurement);
			fp = fph.add(fp);
			assertNotNull("round: "+i,fp);
			assertEquals("round: "+i, i,fph.getCount());
		}
		i--;
		
		for (; j < 3; j++) {
			measurement.setId(null);
			Fingerprint fp = new Fingerprint(loc2, measurement);
			fp = fph.add(fp);
			assertNotNull("round: "+j,fp);
			assertEquals("round: "+j, j,fph.getCount(loc2));
			assertEquals("round: "+j, i+j,fph.getCount());
			
		}
	}
	
	
	
	
	private void setupWiFiReading(WiFiReading wr) {
		wr = wrh.add(wr);
		assertNotNull("setup failed ", wr );
		assertNotNull("setup failed (no id set)", wr.getId());
	}
	
	private void cleanupWiFiReading(WiFiReading wr) {
		assertTrue("clean up failed", wrh.remove(wr));
	}
	
	@Test
	public void test_WifiHomeAdd() {
		wfreading = wrh.add(wfreading);	
		assertNotNull("add failed", wfreading );
		assertNotNull("add failed (no id set)", wfreading.getId() );

		//clean up
		cleanupWiFiReading(wfreading);
	}
	
	@Test
	public void test_WifiHomeGet() {
		//setup
		setupWiFiReading(wfreading);
		
		//get
		WiFiReading new_wr;
		new_wr = wrh.getById(wfreading.getId());
		
		assertNotNull("get failed", new_wr);
		assertEquals("wifireading not equal", wfreading.getId(), new_wr.getId());
		
		
		//clean up
		cleanupWiFiReading(wfreading);
		
	}
	
	public void test_wifiHomeUpdate() {
		//setup
		setupWiFiReading(wfreading);
		
		//update
		
		final String bssid = "New BSSID";
		final String ssid = "New SSID";
		final int rssi = 150;
		final boolean wep = false;
		final boolean infra = true;
		
		
		
		wfreading.setBssid(bssid);
		wfreading.setRssi(rssi);
		wfreading.setSsid(ssid);
		wfreading.setWepEnabled(wep);
		wfreading.setInfrastructure(infra);
		
		assertTrue("update failed", wrh.update(wfreading));
		
		WiFiReading new_wr = wrh.getById(wfreading.getId());
		assertNotNull("retrieval of updated reading failed", new_wr);
		assertEquals("bssid not the same", wfreading.getBssid(), new_wr.getBssid());
		assertEquals("ssid not the same", wfreading.getSsid(), new_wr.getSsid());
		assertEquals("rssi not the same", wfreading.getRssi(), new_wr.getRssi());
		assertEquals("wep enabled not the same", wfreading.isWepEnabled(), new_wr.isWepEnabled());
		assertEquals("infrastructure not the same", wfreading.isInfrastructure(), new_wr.isInfrastructure());
		
		//clean up
		cleanupWiFiReading(wfreading);
	}
	
	@Test
	public void test_wifiHomeRemove() {
		//setup
		setupWiFiReading(wfreading);
		
		//delete		
		assertTrue("remove failed", wrh.remove(wfreading));
		

	}
	
	

	@After
	public void tearDown() throws Exception {
		fph.removeAll();
		lh.removeAll();
		mh.removeAll();
	}

}
