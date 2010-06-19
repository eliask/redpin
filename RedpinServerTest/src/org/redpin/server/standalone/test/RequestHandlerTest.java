package org.redpin.server.standalone.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.redpin.server.standalone.core.Fingerprint;
import org.redpin.server.standalone.core.Location;
import org.redpin.server.standalone.core.Map;
import org.redpin.server.standalone.core.measure.WiFiReading;
import org.redpin.server.standalone.db.HomeFactory;
import org.redpin.server.standalone.db.homes.FingerprintHome;
import org.redpin.server.standalone.db.homes.LocationHome;
import org.redpin.server.standalone.db.homes.MapHome;
import org.redpin.server.standalone.json.GsonFactory;
import org.redpin.server.standalone.net.RequestHandler;
import org.redpin.server.standalone.net.Response.Status;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class RequestHandlerTest {

	Map map, map2, map3, map4;
	List<Map> maplist;
	MapHome mh;

	Location loc, loc2, loc3, loc4;
	List<Location> loclist;
	LocationHome lh;

	Gson gson;

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

		mh = HomeFactory.getMapHome();

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

		lh = HomeFactory.getLocationHome();

		gson = GsonFactory.getGsonInstance();

	}

	@Test
	public void test_setFingerprint() {

		// setup
		mh.add(map);

		String request = "{\"action\":\"setFingerprint\",\"data\":{\"location\":{\"symbolicID\":\"IFW A44\",\"map\":{\"id\":1,\"mapName\":\"Test Map Name\",\"mapURL\":\"http://www.testmap.ch/map.gif\"},\"mapXcord\":150,\"mapYcord\":250,\"accuracy\":0},\"measurement\":{\"timestamp\":1234567890,\"gsmReadings\":[],\"wifiReadings\":[{\"bssid\":\"BSSID\",\"ssid\":\"SSID\",\"rssi\":100,\"wepEnabled\":true,\"isInfrastructure\":false},{\"bssid\":\"BSSID 2\",\"ssid\":\"SSID 2\",\"rssi\":100,\"wepEnabled\":true,\"isInfrastructure\":false},{\"bssid\":\"BSSID 3\",\"ssid\":\"SSID 3\",\"rssi\":100,\"wepEnabled\":true,\"isInfrastructure\":false}],\"bluetoothReadings\":[]}}}";

		RequestHandler rh = new RequestHandler();
		String res = rh.request(request);

		FingerprintHome fph = HomeFactory.getFingerprintHome();

		JsonElement data = checkAndGetResponse(res);
		Fingerprint f = gson.fromJson(data, Fingerprint.class);
		assertNotNull("no id", f.getId());

		Fingerprint fp = null;
		fp = fph.get(f);

		assertEquals("Location not the same", ((Location) fp.getLocation())
				.getId(), ((Location) f.getLocation()).getId());
		DatabaseTest.assertMapEqual((Map) fp.getLocation().getMap(), (Map) f
				.getLocation().getMap(), true);
		for (int i = 0; i < 3; i++) {
			assertEquals(
					"Measurment (WiFi Reading " + i + ") not equal",
					((WiFiReading) fp.getMeasurement().getWiFiReadings().get(i))
							.getId(), ((WiFiReading) f.getMeasurement()
							.getWiFiReadings().get(i)).getId());
		}
	}

	private JsonElement checkAndGetResponse(String res) {
		JsonParser parser = new JsonParser();
		JsonElement root = parser.parse(res);
		if (root.isJsonObject()) {
			JsonObject obj = root.getAsJsonObject();
			JsonElement status = obj.get("status");

			Status stat = gson.fromJson(status, Status.class);

			if ((stat != Status.ok) && (stat != Status.warning))
				fail("response status: " + stat);

			JsonElement data = obj.get("data");

			return data;

		} else {
			fail("result ist no json object");
		}
		return null;
	}

	@Test
	public void test_setMap() {

		String request = "{\"action\":\"setMap\",\"data\":"
				+ gson.toJson(map, Map.class) + "}";

		RequestHandler rh = new RequestHandler();
		String res = rh.request(request);

		JsonElement data = checkAndGetResponse(res);
		Map m = gson.fromJson(data, Map.class);
		assertNotNull("no id", m.getId());

		map = null;
		map = mh.get(m);
		DatabaseTest.assertMapEqual(m, map, false);

	}

	@Test
	public void test_getMapList() {
		// setup
		mh.add(map);
		mh.add(map2);
		mh.add(map3);
		mh.add(map4);

		String request = "{\"action\":\"getMapList\"}";

		RequestHandler rh = new RequestHandler();
		String res = rh.request(request);

		JsonElement data = checkAndGetResponse(res);

		Type typeOfList = new TypeToken<List<Map>>() {
		}.getType();
		maplist = gson.fromJson(data, typeOfList);

		Assert.assertEquals(maplist.get(0).getId(), map.getId());
		Assert.assertEquals(maplist.get(1).getId(), map2.getId());
		Assert.assertEquals(maplist.get(2).getId(), map3.getId());
		Assert.assertEquals(maplist.get(3).getId(), map4.getId());

	}

	@Test
	public void test_removeMapHandler() {
		// setup
		mh.add(map);
		mh.add(map2);

		String request = "{\"action\":\"removeMap\",\"data\":"
				+ gson.toJson(map, Map.class) + "}";

		RequestHandler rh = new RequestHandler();
		String res = rh.request(request);

		checkAndGetResponse(res);

	}

	@Test
	public void test_getLocationList() {
		// setup
		mh.add(map);
		mh.add(map2);
		lh.add(loc);
		lh.add(loc2);
		lh.add(loc3);
		lh.add(loc4);

		String request = "{\"action\":\"getLocationList\"}";

		RequestHandler rh = new RequestHandler();
		String res = rh.request(request);

		JsonElement data = checkAndGetResponse(res);

		Type typeOfList = new TypeToken<List<Location>>() {
		}.getType();
		loclist = gson.fromJson(data, typeOfList);

		Assert.assertEquals(loclist.get(0).getId(), loc.getId());
		Assert.assertEquals(loclist.get(1).getId(), loc2.getId());
		Assert.assertEquals(loclist.get(2).getId(), loc3.getId());
		Assert.assertEquals(loclist.get(3).getId(), loc4.getId());

	}

	@After
	public void tearDown() throws Exception {
		mh.removeAll();
	}

}
