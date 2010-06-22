package org.redpin.android.test;

import java.lang.reflect.Type;
import java.util.List;

import org.redpin.android.core.Fingerprint;
import org.redpin.android.core.Location;
import org.redpin.android.core.Map;
import org.redpin.android.core.Measurement;
import org.redpin.android.core.Vector;
import org.redpin.android.core.measure.WiFiReading;
import org.redpin.android.json.GsonFactory;
import org.redpin.android.net.Request;
import org.redpin.android.net.Response;
import org.redpin.android.net.Request.RequestType;
import org.redpin.android.net.Response.Status;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class JSONTest extends InstrumentationTestCase {

	Map map, map2;
	private WiFiReading wfreading;
	private WiFiReading wfreading2;
	private WiFiReading wfreading3;
	private Vector<WiFiReading> vectorWifi;
	private Measurement measurement;
	private Fingerprint fingerprint, fingerprint2;
	private Location location;

	private Request<Fingerprint> request;

	private Gson gson;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		map = new Map();
		map.setRemoteId(50);
		map.setMapName("Test Map Name");
		map.setMapURL("http://www.testmap.ch/map.gif");

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

		measurement = new Measurement();
		measurement.setTimestamp(1234567890);
		measurement.setWiFiReadings(vectorWifi);

		location = new Location();
		location.setMap(map);
		location.setAccuracy(0);
		location.setMapXcord(150);
		location.setMapYcord(250);
		location.setSymbolicID("IFW A44");

		fingerprint = new Fingerprint(location, measurement);

		request = new Request(RequestType.setFingerprint, fingerprint);

		gson = GsonFactory.getGsonInstance();
	}

	private static final String request_str = "{\"action\":\"setFingerprint\",\"data\":{\"location\":{\"symbolicID\":\"IFW A44\",\"map\":{\"id\":50,\"mapName\":\"Test Map Name\",\"mapURL\":\"http://www.testmap.ch/map.gif\"},\"mapXcord\":150,\"mapYcord\":250,\"accuracy\":0},\"measurement\":{\"bluetoothReadings\":[],\"gsmReadings\":[],\"wifiReadings\":[{\"bssid\":\"BSSID\",\"ssid\":\"SSID\",\"rssi\":100,\"isInfrastructure\":false,\"wepEnabled\":true},{\"bssid\":\"BSSID 2\",\"ssid\":\"SSID 2\",\"rssi\":100,\"isInfrastructure\":false,\"wepEnabled\":true},{\"bssid\":\"BSSID 3\",\"ssid\":\"SSID 3\",\"rssi\":100,\"isInfrastructure\":false,\"wepEnabled\":true}],\"timestamp\":1234567890}}}";

	public void test_request() {
		Type type = new TypeToken<Request<Fingerprint>>() {
		}.getType();
		String json = gson.toJson(request, type);
		Log.d(getName(), json);
		assertEquals("generated json does not math", request_str, json);
	}

	private static final String test_message = "this is a test message";
	private static final Status test_status = Status.ok;
	private static final String getMapList_response = "{\"status\":\""
			+ test_status.name()
			+ "\",\"message\":\""
			+ test_message
			+ "\",\"data\":[{\"id\":1,\"mapName\":\"IFW A\",\"mapURL\":\"http://www.rauminfo.ethz.ch/Rauminfo/grundrissplan.gif?region=Z&areal=Z&gebaeude=IFW&geschoss=A&\"},{\"id\":2,\"mapName\":\"IFW B\",\"mapURL\":\"http://www.rauminfo.ethz.ch/Rauminfo/grundrissplan.gif?region=Z&areal=Z&gebaeude=IFW&geschoss=B&\"},{\"id\":3,\"mapName\":\"IFW C\",\"mapURL\":\"http://www.rauminfo.ethz.ch/Rauminfo/grundrissplan.gif?region=Z&areal=Z&gebaeude=IFW&geschoss=C&\"},{\"id\":4,\"mapName\":\"IFW D\",\"mapURL\":\"http://www.rauminfo.ethz.ch/Rauminfo/grundrissplan.gif?region=Z&areal=Z&gebaeude=IFW&geschoss=D&\"}]}";

	public void test_response() {

		Type type = new TypeToken<Response<List<Map>>>() {
		}.getType();
		Response<List<Map>> res = gson.fromJson(getMapList_response, type);

		// Response res = gson.fromJson(getMapList_response, Response.class);

		assertEquals(Response.Status.ok, res.getStatus());
		assertEquals(test_message, res.getMessage());

		List<Map> list = res.getData();
		assertEquals(4, list.size());

		Map m = list.get(0);
		assertEquals(Map.class, list.get(0).getClass());
		assertEquals("IFW A", m.getMapName());
		assertEquals(1, m.getRemoteId().intValue());
		assertEquals(
				"http://www.rauminfo.ethz.ch/Rauminfo/grundrissplan.gif?region=Z&areal=Z&gebaeude=IFW&geschoss=A&",
				m.getMapURL());

	}

	private final static String setFingerprintResponse_str = "{\"status\":\"ok\",\"data\":{\"id\":1,\"location\":{\"id\":1,\"symbolicID\":\"IFW A44\",\"map\":{\"id\":50,\"mapName\":\"Test Map Name\",\"mapURL\":\"http://www.testmap.ch/map.gif\"},\"mapXcord\":150,\"mapYcord\":250,\"accuracy\":0},\"measurement\":{\"id\":1,\"timestamp\":1234567890,\"gsmReadings\":[],\"wifiReadings\":[{\"id\":1,\"bssid\":\"BSSID\",\"ssid\":\"SSID\",\"rssi\":100,\"wepEnabled\":true,\"isInfrastructure\":false},{\"id\":2,\"bssid\":\"BSSID 2\",\"ssid\":\"SSID 2\",\"rssi\":100,\"wepEnabled\":true,\"isInfrastructure\":false},{\"id\":3,\"bssid\":\"BSSID 3\",\"ssid\":\"SSID 3\",\"rssi\":100,\"wepEnabled\":true,\"isInfrastructure\":false}],\"bluetoothReadings\":[]}}}";

	public void test_setFingerprintResponse() {
		Type type = new TypeToken<Response<Fingerprint>>() {
		}.getType();
		Response<Fingerprint> res = gson.fromJson(setFingerprintResponse_str,
				type);

		assertEquals(Response.Status.ok, res.getStatus());
		fingerprint2 = res.getData();
		assertNotNull(fingerprint2);

		assertEquals("fingerprint class not equal", fingerprint2.getClass(),
				fingerprint.getClass());
		assertEquals("location class not the same", fingerprint2.getLocation()
				.getClass(), Location.class);
		assertEquals("map class not the same", fingerprint2.getLocation()
				.getMap().getClass(), Map.class);
		assertEquals("measurement class not the same", fingerprint2
				.getMeasurement().getClass(), Measurement.class);
		assertEquals("measurement class not the same", fingerprint2
				.getMeasurement().getWiFiReadings().getClass(),
				new Vector<WiFiReading>().getClass());

		assertEquals(1, fingerprint2.getRemoteId().intValue());
		Measurement m1 = (Measurement) fingerprint.getMeasurement();
		Measurement m2 = (Measurement) fingerprint2.getMeasurement();

		assertEquals(m1.getTimestamp(), m2.getTimestamp());

		Location l1 = (Location) fingerprint.getLocation();
		Location l2 = (Location) fingerprint2.getLocation();

		assertEquals(1, l2.getRemoteId().intValue());
		assertEquals(l1.getMapXcord(), l2.getMapXcord());
		assertEquals(l1.getMapYcord(), l2.getMapYcord());
		assertEquals(l1.getSymbolicID(), l2.getSymbolicID());

		Map ma1 = (Map) l1.getMap();
		Map ma2 = (Map) l2.getMap();
		assertEquals(50, ma2.getRemoteId().intValue());
		assertEquals(ma1.getMapName(), ma2.getMapName());
		assertEquals(ma1.getMapURL(), ma2.getMapURL());

		Vector<WiFiReading> w1 = m1.getWiFiReadings();
		Vector<WiFiReading> w2 = m2.getWiFiReadings();

		assertEquals(w1.size(), w2.size());

		for (int i = 0; i < w1.size(); i++) {
			WiFiReading wi1 = w1.get(i);
			WiFiReading wi2 = w2.get(i);

			assertEquals(i + 1, wi2.getRemoteId().intValue());
			assertEquals(wi1.getBssid(), wi2.getBssid());
			assertEquals(wi1.getRssi(), wi2.getRssi());
			assertEquals(wi1.getSsid(), wi2.getSsid());
		}

		assertEquals(m1.getBluetoothReadings().size(), m2
				.getBluetoothReadings().size());
		assertEquals(m2.getGsmReadings().size(), m2.getGsmReadings().size());
	}

	public void test_gsonFingerprint() {
		String json = gson.toJson(fingerprint);
		Log.d(getName(), json);
		fingerprint2 = gson.fromJson(json, Fingerprint.class);

		assertEquals("fingerprint class not equal", fingerprint2.getClass(),
				fingerprint.getClass());
		assertEquals("location class not the same", fingerprint2.getLocation()
				.getClass(), Location.class);
		assertEquals("map class not the same", fingerprint2.getLocation()
				.getMap().getClass(), Map.class);
		assertEquals("measurement class not the same", fingerprint2
				.getMeasurement().getClass(), Measurement.class);
		assertEquals("measurement class not the same", fingerprint2
				.getMeasurement().getWiFiReadings().getClass(),
				new Vector<WiFiReading>().getClass());

		assertEquals(fingerprint.getRemoteId(), fingerprint2.getRemoteId());
		Measurement m1 = (Measurement) fingerprint.getMeasurement();
		Measurement m2 = (Measurement) fingerprint2.getMeasurement();

		assertEquals(m1.getTimestamp(), m2.getTimestamp());

		Location l1 = (Location) fingerprint.getLocation();
		Location l2 = (Location) fingerprint2.getLocation();

		assertEquals(l1.getRemoteId(), l2.getRemoteId());
		assertEquals(l1.getMapXcord(), l2.getMapXcord());
		assertEquals(l1.getMapYcord(), l2.getMapYcord());
		assertEquals(l1.getSymbolicID(), l2.getSymbolicID());

		Map ma1 = (Map) l1.getMap();
		Map ma2 = (Map) l2.getMap();
		assertEquals(ma1.getRemoteId(), ma2.getRemoteId());
		assertEquals(ma1.getMapName(), ma2.getMapName());
		assertEquals(ma1.getMapURL(), ma2.getMapURL());

		Vector<WiFiReading> w1 = m1.getWiFiReadings();
		Vector<WiFiReading> w2 = m2.getWiFiReadings();

		assertEquals(w1.size(), w2.size());

		for (int i = 0; i < w1.size(); i++) {
			WiFiReading wi1 = w1.get(i);
			WiFiReading wi2 = w2.get(i);

			assertEquals(wi1.getRemoteId(), wi2.getRemoteId());
			assertEquals(wi1.getBssid(), wi2.getBssid());
			assertEquals(wi1.getRssi(), wi2.getRssi());
			assertEquals(wi1.getSsid(), wi2.getSsid());
		}

		assertEquals(m1.getBluetoothReadings().size(), m2
				.getBluetoothReadings().size());
		assertEquals(m2.getGsmReadings().size(), m2.getGsmReadings().size());

	}

}
