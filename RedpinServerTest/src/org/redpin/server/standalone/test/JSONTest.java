package org.redpin.server.standalone.test;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.redpin.server.standalone.core.Fingerprint;
import org.redpin.server.standalone.core.Location;
import org.redpin.server.standalone.core.Map;
import org.redpin.server.standalone.core.Measurement;
import org.redpin.server.standalone.core.Vector;
import org.redpin.server.standalone.core.measure.WiFiReading;
import org.redpin.server.standalone.json.GsonFactory;

import com.google.gson.Gson;

public class JSONTest extends TestCase {

	Map map, map2;
	private WiFiReading wfreading;
	private WiFiReading wfreading2;
	private WiFiReading wfreading3;
	private Vector<WiFiReading> vectorWifi;
	private Measurement measurement;
	private Fingerprint fingerprint, fingerprint2;
	private Location location;
	
	@Before
	public void setUp() throws Exception {
		map = new Map();
		map.setId(50);
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
	}
	
	@Test
	public void test_gsonFingerprint() {
		Gson gson = GsonFactory.getGsonInstance();
		String json = gson.toJson(fingerprint);
		System.out.println(json);
		fingerprint2 = gson.fromJson(json, Fingerprint.class );
		
		assertEquals("fingerprint class not equal", fingerprint2.getClass(), fingerprint.getClass());
		assertEquals("location class not the same", fingerprint2.getLocation().getClass(), Location.class);
		assertEquals("map class not the same", fingerprint2.getLocation().getMap().getClass(), Map.class);
		assertEquals("measurement class not the same", fingerprint2.getMeasurement().getClass(), Measurement.class);
		assertEquals("measurement class not the same", fingerprint2.getMeasurement().getWiFiReadings().getClass(), new Vector<WiFiReading>().getClass());
		
		System.out.println(fingerprint2.getMeasurement().getWiFiReadings());
		

	}
	
	@After
	public void tearDown() throws Exception {
		
	}
}
