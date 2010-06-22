package org.redpin.android.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import org.redpin.android.core.Fingerprint;
import org.redpin.android.core.Location;
import org.redpin.android.core.Map;
import org.redpin.android.core.Measurement;
import org.redpin.android.core.Vector;
import org.redpin.android.core.measure.WiFiReading;
import org.redpin.android.net.ConnectionHandler;
import org.redpin.android.net.Request;
import org.redpin.android.net.RequestHandler;
import org.redpin.android.net.Response;
import org.redpin.android.net.Request.RequestType;
import org.redpin.android.net.Response.Status;

import android.test.InstrumentationTestCase;

import com.google.gson.Gson;

/**
 * Does need a running redpin standalone server on localhost:8000
 * 
 * 
 */
public class RequestHandlerTest extends InstrumentationTestCase {
	Map map, map2;
	private WiFiReading wfreading;
	private WiFiReading wfreading2;
	private WiFiReading wfreading3;
	private Vector<WiFiReading> vectorWifi;
	private Measurement measurement;
	private Fingerprint fingerprint, fingerprint2;
	private Location location;

	private Request<Fingerprint> setFingerprintRequest;
	private Request<Map> setMapRequest;

	private Gson gson;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		map = new Map();
		map.setRemoteId(null);
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

		try {
			Socket socket = new Socket();
			socket.bind(null);
			InetSocketAddress address = new InetSocketAddress(
					ConnectionHandler.host, ConnectionHandler.port);
			socket.connect(address, 10000);
			boolean online = socket.isConnected();
			socket.close();
			if (!online) {
				throw new IOException();
			}
		} catch (IOException e) {
			fail("redpin server is not started. this testcase needs an running repin server");
		}

	}

	public static String real_localhost = "10.0.2.2";
	public static int port = 8000;

	public void test_mapRequest() {

		ConnectionHandler.host = real_localhost;
		ConnectionHandler.port = port;
		Request<Map> request = new Request<Map>(RequestType.setMap, map);
		// assertEquals(new TypeToken<Response<Map>>() {}.getType(),
		// setMapRequest.getResponseType());
		Response<Map> response = (Response<Map>) RequestHandler
				.performRequest(request);
		assertNotNull(response);
		assertEquals(Response.Status.ok, response.getStatus());

		assertEquals(map.getMapName(), response.getData().getMapName());
		assertEquals(map.getMapURL(), response.getData().getMapURL());
		map.setRemoteId(response.getData().getRemoteId());
		request = new Request<Map>(RequestType.removeMap, map);

		Response<Void> response2 = (Response<Void>) RequestHandler
				.performRequest(request);
		assertNotNull(response2);
		assertEquals(Status.ok, response2.getStatus());

	}

	public void test_getMapListRequest() {

		ConnectionHandler.host = real_localhost;
		ConnectionHandler.port = port;
		Request<Map> request = new Request<Map>(RequestType.getMapList);
		// assertEquals(new TypeToken<Response<Map>>() {}.getType(),
		// setMapRequest.getResponseType());
		Response<List<Map>> response = (Response<List<Map>>) RequestHandler
				.performRequest(request);
		assertNotNull(response);
		assertEquals(Response.Status.ok, response.getStatus());

		List<Map> l = response.getData();
		assertNotNull(l);
		assertTrue(l instanceof List<?>);
		if (l.size() > 0) {
			assertTrue(l.get(0) instanceof Map);
		}

	}

	public void test_setFingerprintRequest() {

		ConnectionHandler.host = real_localhost;
		ConnectionHandler.port = port;

		Request<Map> mrequest = new Request<Map>(RequestType.setMap, map);
		Response<Map> mresponse = (Response<Map>) RequestHandler
				.performRequest(mrequest);
		assertEquals("setup failed", Status.ok, mresponse.getStatus());
		map.setRemoteId(mresponse.getData().getRemoteId());
		assertNotNull("setup failed", map.getRemoteId());

		Request<Fingerprint> request = new Request<Fingerprint>(
				RequestType.setFingerprint, fingerprint);
		Response<Fingerprint> response = (Response<Fingerprint>) RequestHandler
				.performRequest(request);

		fingerprint2 = response.getData();
		assertNotNull(fingerprint2);

		Measurement m1 = (Measurement) fingerprint.getMeasurement();
		Measurement m2 = (Measurement) fingerprint2.getMeasurement();

		assertEquals(m1.getTimestamp(), m2.getTimestamp());

		Location l1 = (Location) fingerprint.getLocation();
		Location l2 = (Location) fingerprint2.getLocation();

		assertEquals(l1.getMapXcord(), l2.getMapXcord());
		assertEquals(l1.getMapYcord(), l2.getMapYcord());
		assertEquals(l1.getSymbolicID(), l2.getSymbolicID());

		Map ma1 = (Map) l1.getMap();
		Map ma2 = (Map) l2.getMap();

		assertEquals(ma1.getMapName(), ma2.getMapName());
		assertEquals(ma1.getMapURL(), ma2.getMapURL());

		Vector<WiFiReading> w1 = m1.getWiFiReadings();
		Vector<WiFiReading> w2 = m2.getWiFiReadings();

		assertEquals(w1.size(), w2.size());

		for (int i = 0; i < w1.size(); i++) {
			WiFiReading wi1 = w1.get(i);
			WiFiReading wi2 = w2.get(i);

			assertEquals(wi1.getBssid(), wi2.getBssid());
			assertEquals(wi1.getRssi(), wi2.getRssi());
			assertEquals(wi1.getSsid(), wi2.getSsid());
		}

		assertEquals(m1.getBluetoothReadings().size(), m2
				.getBluetoothReadings().size());
		assertEquals(m2.getGsmReadings().size(), m2.getGsmReadings().size());

		Request<Map> mmrequest = new Request<Map>(RequestType.removeMap, map);
		Response<Void> mmresponse = (Response<Void>) RequestHandler
				.performRequest(mmrequest);
		assertEquals("cleanup failed", Status.ok, mmresponse.getStatus());
	}
}
