package org.redpin.android.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.redpin.android.ApplicationContext;
import org.redpin.android.core.Fingerprint;
import org.redpin.android.core.Location;
import org.redpin.android.core.Map;
import org.redpin.android.core.Measurement;
import org.redpin.android.core.Vector;
import org.redpin.android.core.measure.WiFiReading;
import org.redpin.android.db.EntityHomeFactory;
import org.redpin.android.db.LocationHome;
import org.redpin.android.db.MapHome;
import org.redpin.android.net.PerformRequestTask;
import org.redpin.android.net.Request;
import org.redpin.android.net.Response;
import org.redpin.android.net.Request.RequestType;
import org.redpin.android.net.Response.Status;
import org.redpin.android.net.home.RemoteEntityHome;
import org.redpin.android.net.home.RemoteEntityHomeCallback;
import org.redpin.android.provider.RedpinContentProvider;
import org.redpin.android.provider.RedpinContract;

import android.test.ProviderTestCase2;

/**
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class RemoteEntityHomeTest extends
		ProviderTestCase2<RedpinContentProvider> {

	public RemoteEntityHomeTest() {
		super(RedpinContentProvider.class, RedpinContract.AUTHORITY);

	}

	Map map, map2, map3, map4;
	List<Map> maplist;
	MapHome mh;

	WiFiReading wfreading, wfreading2, wfreading3;
	Vector<WiFiReading> vectorWifi;
	Measurement measurement;
	Fingerprint fingerprint, fingerprint2;

	Location loc, loc2, loc3, loc4;
	LocationHome lh;

	@Override
	protected void setUp() throws Exception {

		super.setUp();

		map = new Map();
		map.setMapName("Test Map Name");
		map.setMapURL("http://www.testmap.ch/map.gif");
		map.setRemoteId(1);

		map2 = new Map();
		map2.setMapName("Test Map Name 2");
		map2.setMapURL("http://www.testmap.ch/map2.gif");
		map2.setRemoteId(2);

		map3 = new Map();
		map3.setMapName("Test Map Name 3");
		map3.setMapURL("http://www.testmap.ch/map3.gif");
		map3.setRemoteId(3);

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
		loc.setRemoteId(10);

		loc2 = new Location();
		loc2.setSymbolicID("location 2");
		loc2.setMap(map);
		loc2.setMapXcord(15);
		loc2.setMapYcord(16);
		loc2.setAccuracy(100);
		loc2.setRemoteId(20);

		loc3 = new Location();
		loc3.setSymbolicID("location 3");
		loc3.setMap(map);
		loc3.setMapXcord(15);
		loc3.setMapYcord(16);
		loc3.setAccuracy(100);
		loc3.setRemoteId(30);

		loc4 = new Location();
		loc4.setSymbolicID("location 4");
		loc4.setMap(map2);
		loc4.setMapXcord(15);
		loc4.setMapYcord(16);
		loc4.setAccuracy(100);
		loc4.setRemoteId(40);

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

		fingerprint = new Fingerprint(loc, measurement);
		fingerprint2 = new Fingerprint(loc2, measurement);

		ApplicationContext.init(getMockContext());

		mh = EntityHomeFactory.getMapHome();
		lh = EntityHomeFactory.getLocationHome();

		mh.removeAll();
		lh.removeAll();

		assertEquals(0, mh.getAll().size());
		assertEquals(0, lh.getAll().size());

		RemoteEntityHome.clear();
	}

	@Override
	protected void tearDown() throws Exception {

		mh.removeAll();
		lh.removeAll();
		/*
		 * getMockContentResolver().delete(RedpinContract.Map.CONTENT_URI, null,
		 * null);
		 */
		assertEquals(0, mh.getAll().size());
		assertEquals(0, lh.getAll().size());
		super.tearDown();
	}

	private static void mockExecuteTask(PerformRequestTask task, Request<?> req) {
		RemoteEntityHome.getPending().put(task, req);
	}

	private static void mockExecuteTask(PerformRequestTask task,
			Request<?> req, RemoteEntityHomeCallback cb) {
		RemoteEntityHome.getPending().put(task, req);
		RemoteEntityHome.getCallbacks().put(req, cb);
	}

	private static void assertEquals(Map expected, Map actual) {
		assertEquals(expected.getRemoteId(), actual.getRemoteId());
		assertEquals(expected.getMapName(), actual.getMapName());
		assertEquals(expected.getMapURL(), actual.getMapURL());
	}

	public void test_setMap() {

		Request<Void> mockRequest = new Request<Void>(RequestType.setMap);
		Response<Map> mockResponse = new Response<Map>(Status.ok, map);
		map.setRemoteId(10);
		PerformRequestTask mockTask = new PerformRequestTask();

		mockExecuteTask(mockTask, mockRequest);

		RemoteEntityHome.getInstance().onPerformedBackground(mockRequest,
				mockResponse, mockTask);
		RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
				mockResponse, mockTask);

		assertTrue(map.getLocalId() > 0);

		List<Map> r = mh.getAll();
		assertNotNull(r);
		assertEquals(1, r.size());

		assertEquals(map, r.get(0));

	}

	public void test_getMapList() {
		assertNotNull(map.getRemoteId());
		assertNotNull(map2.getRemoteId());
		Request<Void> mockRequest = new Request<Void>(RequestType.getMapList);
		Response<List<?>> mockResponse = new Response<List<?>>(Status.ok,
				Arrays.asList(map, map2));
		PerformRequestTask mockTask = new PerformRequestTask();

		mockExecuteTask(mockTask, mockRequest);

		RemoteEntityHome.getInstance().onPerformedBackground(mockRequest,
				mockResponse, mockTask);
		RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
				mockResponse, mockTask);

		List<Map> r = mh.getAll();
		assertNotNull(r);
		assertEquals(2, r.size());
		assertTrue(map != r.get(0));
		assertTrue(map2 != r.get(1));

		assertEquals(map, r.get(0));
		assertEquals(map2, r.get(1));

		final String name = "mapName 2 updated";
		map2.setMapName(name);
		mockResponse = new Response<List<?>>(Status.ok, Arrays.asList(map,
				map2, map3));
		mockExecuteTask(mockTask, mockRequest);

		RemoteEntityHome.getInstance().onPerformedBackground(mockRequest,
				mockResponse, mockTask);
		RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
				mockResponse, mockTask);

		r = mh.getAll();
		assertNotNull(r);
		assertEquals(3, r.size());
		assertTrue(map != r.get(0));
		assertTrue(map2 != r.get(1));
		assertTrue(map3 != r.get(2));

		assertEquals(map, r.get(0));
		assertEquals(map2, r.get(1));
		assertEquals(map3, r.get(2));

	}

	public void test_removeMap() {
		map.setLocalId(-1);
		map.setRemoteId(10);

		mh.add(map);
		List<Map> list = mh.getAll();
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(map, list.get(0));

		Request<Map> mockRequest = new Request<Map>(RequestType.removeMap, map);
		Response<Void> mockResponse = new Response<Void>(Status.ok);

		PerformRequestTask mockTask = new PerformRequestTask();

		mockExecuteTask(mockTask, mockRequest);

		RemoteEntityHome.getInstance().onPerformedBackground(mockRequest,
				mockResponse, mockTask);
		RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
				mockResponse, mockTask);

		list = mh.getAll();
		assertNotNull(list);
		assertEquals(0, list.size());

	}

	public void test_getLocation() {

		Request<Measurement> mockRequest = new Request<Measurement>(
				RequestType.getLocation);
		loc.setRemoteId(10);
		Response<Location> mockResponse = new Response<Location>(Status.ok, loc);
		PerformRequestTask mockTask = new PerformRequestTask();

		mockExecuteTask(mockTask, mockRequest);

		RemoteEntityHome.getInstance().onPerformedBackground(mockRequest,
				mockResponse, mockTask);
		RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
				mockResponse, mockTask);

		List<Location> r = lh.getAll();
		assertNotNull(r);
		assertEquals(1, r.size());
		assertTrue(loc != r.get(0));
		assertEquals(loc, r.get(0));

	}

	public void test_updateLocation() {
		loc.setRemoteId(10);
		lh.add(loc);

		List<Location> list = lh.getAll();
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(loc, list.get(0));

		final String symbolicID = "new sombolic id";
		final int newX = 2000;
		final int newY = 4000;

		loc.setSymbolicID(symbolicID);
		loc.setMapXcord(newX);
		loc.setMapYcord(newY);

		Request<Location> mockRequest = new Request<Location>(
				RequestType.updateLocation, loc);
		Response<Void> mockResponse = new Response<Void>(Status.ok);

		PerformRequestTask mockTask = new PerformRequestTask();

		mockExecuteTask(mockTask, mockRequest);

		RemoteEntityHome.getInstance().onPerformedBackground(mockRequest,
				mockResponse, mockTask);
		RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
				mockResponse, mockTask);

		list = lh.getAll();
		assertNotNull(list);
		assertEquals(1, list.size());
		assertTrue(loc != list.get(0));
		assertEquals(loc, list.get(0));

	}

	public void test_removeLocation() {
		loc.setRemoteId(10);
		lh.add(loc);

		List<Location> list = lh.getAll();
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(loc, list.get(0));

		Request<Location> mockRequest = new Request<Location>(
				RequestType.removeLocation, loc);
		Response<Void> mockResponse = new Response<Void>(Status.ok);

		PerformRequestTask mockTask = new PerformRequestTask();

		mockExecuteTask(mockTask, mockRequest);

		RemoteEntityHome.getInstance().onPerformedBackground(mockRequest,
				mockResponse, mockTask);
		RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
				mockResponse, mockTask);

		list = lh.getAll();
		assertNotNull(list);
		assertEquals(0, list.size());

	}

	public void test_getLocationList() {
		assertNotNull(loc.getRemoteId());
		assertNotNull(loc2.getRemoteId());
		assertNotNull(loc3.getRemoteId());
		assertNotNull(loc4.getRemoteId());

		Request<Void> mockRequest = new Request<Void>(
				RequestType.getLocationList);
		Response<List<?>> mockResponse = new Response<List<?>>(Status.ok,
				Arrays.asList(loc, loc2));
		PerformRequestTask mockTask = new PerformRequestTask();

		mockExecuteTask(mockTask, mockRequest);

		RemoteEntityHome.getInstance().onPerformedBackground(mockRequest,
				mockResponse, mockTask);
		RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
				mockResponse, mockTask);

		List<Map> r = mh.getAll();
		assertNotNull(r);
		assertEquals(1, r.size());
		assertTrue(map != r.get(0));
		assertEquals(map, r.get(0));

		List<Location> rl = lh.getAll();
		assertNotNull(rl);
		assertEquals(2, rl.size());
		assertEquals(loc, rl.get(0));
		assertEquals(loc2, rl.get(1));

		final String symbolicID = "new sombolic id";
		final int newX = 2000;
		final int newY = 4000;

		loc.setSymbolicID(symbolicID);
		loc2.setMapXcord(newX);
		loc2.setMapYcord(newY);

		mockResponse = new Response<List<?>>(Status.ok, Arrays.asList(loc,
				loc2, loc3, loc4));
		mockExecuteTask(mockTask, mockRequest);

		RemoteEntityHome.getInstance().onPerformedBackground(mockRequest,
				mockResponse, mockTask);
		RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
				mockResponse, mockTask);

		r = mh.getAll();
		assertNotNull(r);
		assertEquals(2, r.size());
		assertEquals(map, r.get(0));
		assertEquals(map2, r.get(1));

		rl = lh.getAll();
		assertNotNull(rl);
		assertEquals(4, rl.size());
		assertEquals(loc, rl.get(0));
		assertEquals(loc2, rl.get(1));
		assertEquals(loc3, rl.get(2));
		assertEquals(loc4, rl.get(3));

	}

	public void test_setFingerprint() {
		loc.setRemoteId(-1);

		Location locCopy = new Location();
		locCopy.setRemoteId(10);
		locCopy.setMap(loc.getMap());
		locCopy.setSymbolicID(loc.getSymbolicID());
		locCopy.setMapXcord(loc.getMapXcord());
		locCopy.setMapYcord(loc.getMapYcord());

		Fingerprint fingerprintCopy = new Fingerprint(locCopy, measurement);

		Request<Fingerprint> mockRequest = new Request<Fingerprint>(
				RequestType.setFingerprint, fingerprint);
		Response<Fingerprint> mockResponse = new Response<Fingerprint>(
				Status.ok, fingerprintCopy);

		PerformRequestTask mockTask = new PerformRequestTask();

		mockExecuteTask(mockTask, mockRequest);

		RemoteEntityHome.getInstance().onPerformedBackground(mockRequest,
				mockResponse, mockTask);
		RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
				mockResponse, mockTask);

		List<Location> list = lh.getAll();
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(locCopy, list.get(0));

		mockRequest = new Request<Fingerprint>(RequestType.setFingerprint,
				fingerprint);
		mockResponse = new Response<Fingerprint>(Status.ok, fingerprintCopy);

		mockTask = new PerformRequestTask();

		mockExecuteTask(mockTask, mockRequest);

		RemoteEntityHome.getInstance().onPerformedBackground(mockRequest,
				mockResponse, mockTask);
		RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
				mockResponse, mockTask);

		list = lh.getAll();
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(locCopy, list.get(0));

	}

	public void test_onSuccess() {
		assertEquals(0, RemoteEntityHome.getPending().size());
		map.setRemoteId(20);
		Request<Map> mockRequest = new Request<Map>(RequestType.setMap, map);
		Response<Map> mockResponse = new Response<Map>(Status.ok, map);

		PerformRequestTask mockTask = new PerformRequestTask();

		mockExecuteTask(mockTask, mockRequest);

		RemoteEntityHome.getInstance().onPerformedBackground(mockRequest,
				mockResponse, mockTask);
		RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
				mockResponse, mockTask);

		assertEquals(0, RemoteEntityHome.getPending().size());

		mh.removeAll();

	}

	public void test_onCanceled() {
		assertEquals(0, RemoteEntityHome.getPending().size());
		Request<Map> mockRequest = new Request<Map>(RequestType.removeMap, map);

		PerformRequestTask mockTask = new PerformRequestTask();

		mockExecuteTask(mockTask, mockRequest);

		RemoteEntityHome.getInstance().onCanceledForeground(mockRequest,
				mockTask);

		assertEquals(1, RemoteEntityHome.getPending().size());
		assertTrue(RemoteEntityHome.getPending().values().contains(mockRequest));

	}

	Response<Map> mockResponse;

	public void test_CallbackOnSuccess() {
		assertEquals(0, RemoteEntityHome.getPending().size());
		map.setRemoteId(20);
		Request<Map> mockRequest = new Request<Map>(RequestType.setMap, map);
		mockResponse = new Response<Map>(Status.ok, map);

		PerformRequestTask mockTask = new PerformRequestTask();

		mockExecuteTask(mockTask, mockRequest, new RemoteEntityHomeCallback() {

			public void onResponse(Response<?> response) {
				assertTrue(response == mockResponse);
			}

			public void onFailure(Response<?> response) {
				fail();
			}
		});

		RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
				mockResponse, mockTask);

		assertEquals(0, RemoteEntityHome.getPending().size());
		assertEquals(0, RemoteEntityHome.getCallbacks().size());

	}

	/*
	 * 
	 * 
	 * public void test_CallbackOnFailure() { assertEquals(0,
	 * RemoteEntityHome.getPending().size()); map.setRemoteId(20); Request<Map>
	 * mockRequest = new Request<Map>(RequestType.setMap, map); mockResponse =
	 * new Response<Map>(Status.failed, map);
	 * 
	 * PerformRequestTask mockTask = new PerformRequestTask();
	 * 
	 * mockExecuteTask(mockTask, mockRequest, new RemoteEntityHomeCallback() {
	 * 
	 * public void onResponse(Response<?> response) { fail(); }
	 * 
	 * public void onFailure(Response<?> response) { assertTrue(response ==
	 * mockResponse);
	 * 
	 * } });
	 * 
	 * 
	 * RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
	 * mockResponse, mockTask);
	 * 
	 * 
	 * assertEquals(0, RemoteEntityHome.getPending().size()); assertEquals(0,
	 * RemoteEntityHome.getCallbacks().size());
	 * 
	 * }
	 * 
	 * 
	 * 
	 * public void test_onFailure() { assertEquals(0,
	 * RemoteEntityHome.getPending().size()); Request<Map> mockRequest = new
	 * Request<Map>(RequestType.removeMap, map); Response<Void> mockResponse =
	 * new Response<Void>(Status.failed);
	 * 
	 * PerformRequestTask mockTask = new PerformRequestTask();
	 * 
	 * mockExecuteTask(mockTask, mockRequest);
	 * 
	 * RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
	 * mockResponse, mockTask);
	 * 
	 * assertEquals(1, RemoteEntityHome.getPending().size());
	 * assertFalse(RemoteEntityHome.getPending().containsKey(mockTask));
	 * assertTrue(RemoteEntityHome.getPending().values().contains(mockRequest));
	 * assertTrue(RemoteEntityHome.getRetryCount().containsKey(mockRequest));
	 * assertEquals((Integer)1,
	 * RemoteEntityHome.getRetryCount().get(mockRequest));
	 * 
	 * 
	 * RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
	 * mockResponse, mockTask);
	 * 
	 * assertEquals(1, RemoteEntityHome.getPending().size());
	 * assertFalse(RemoteEntityHome.getPending().containsKey(mockTask));
	 * assertTrue(RemoteEntityHome.getPending().values().contains(mockRequest));
	 * assertTrue(RemoteEntityHome.getRetryCount().containsKey(mockRequest));
	 * assertEquals((Integer)2,
	 * RemoteEntityHome.getRetryCount().get(mockRequest));
	 * 
	 * RemoteEntityHome.getInstance().onPerformedForeground(mockRequest,
	 * mockResponse, mockTask); //given up assertEquals(0,
	 * RemoteEntityHome.getPending().size());
	 * assertFalse(RemoteEntityHome.getPending().containsKey(mockTask));
	 * assertFalse
	 * (RemoteEntityHome.getPending().values().contains(mockRequest));
	 * assertFalse(RemoteEntityHome.getRetryCount().containsKey(mockRequest));
	 * 
	 * 
	 * 
	 * }
	 */

}
