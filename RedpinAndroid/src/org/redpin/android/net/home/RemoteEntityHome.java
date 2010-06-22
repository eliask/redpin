/**
 *  Filename: RemoteEntityHome.java (in org.repin.android.net.home)
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
package org.redpin.android.net.home;

import java.util.HashMap;

import org.redpin.android.net.PerformRequestTask;
import org.redpin.android.net.PerformRequestTaskCallback;
import org.redpin.android.net.Request;
import org.redpin.android.net.Response;
import org.redpin.android.net.Request.RequestType;
import org.redpin.android.net.Response.Status;

import android.os.AsyncTask;
import android.util.Log;

/**
 * {@link RemoteEntityHome} handles request to server and calls the responsible
 * Entity RemoteEntityHomes to synchronize the local database after the request
 * was performed
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class RemoteEntityHome implements PerformRequestTaskCallback {

	protected static RemoteEntityHome instance = new RemoteEntityHome();

	protected HashMap<PerformRequestTask, Request<?>> pending = new HashMap<PerformRequestTask, Request<?>>();
	protected HashMap<Request<?>, Integer> retryCount = new HashMap<Request<?>, Integer>();
	protected HashMap<Request<?>, RemoteEntityHomeCallback> callbacks = new HashMap<Request<?>, RemoteEntityHomeCallback>();

	private static final String TAG = RemoteEntityHome.class.getName();

	public static final Integer MAX_TRIES = 3;

	/**
	 * Performs an server request. This method must be invoked on the UI thread.
	 * 
	 * @param action
	 *            Action to be performed
	 * @param callback
	 *            {@link RemoteEntityHomeCallback}
	 */
	public static void performRequest(RequestType action,
			RemoteEntityHomeCallback callback) {
		performRequest(action, null, callback);
	}

	/**
	 * Performs an server request . This method must be invoked on the UI
	 * thread.
	 * 
	 * @param action
	 *            Action to be performed
	 */
	public static void performRequest(RequestType action) {
		performRequest(action, null);
	}

	/**
	 * Performs an server request. This method must be invoked on the UI thread.
	 * 
	 * @param <D>
	 *            Data type the request contains
	 * @param action
	 *            Action to be performed
	 * @param data
	 *            Data to be submitted with the request
	 */
	public static <D> void performRequest(RequestType action, D data) {
		performRequest(action, data, null);
	}

	/**
	 * Performs an server request. This method must be invoked on the UI thread
	 * (as it invokes {@link AsyncTask#execute(Object...)}
	 * 
	 * @param <D>
	 *            Data type the request contains
	 * @param action
	 *            Action to be performed
	 * @param data
	 *            Data to be submitted with the request
	 * @param callback
	 *            {@link RemoteEntityHomeCallback} to be called after the
	 *            request
	 */
	public static <D> void performRequest(RequestType action, D data,
			RemoteEntityHomeCallback callback) {

		PerformRequestTask task;
		Request<D> request = new Request<D>(action, data);
		task = new PerformRequestTask(instance);

		startTask(task, request, callback);

	}

	/**
	 * Starts an asynchronous server request and adds it to the pending list.
	 * 
	 * @param task
	 *            {@link PerformRequestTask} to be started
	 * @param request
	 *            {@link Request} to be performed
	 * @param callback
	 *            {@link RemoteEntityHomeCallback}
	 */
	protected static void startTask(PerformRequestTask task,
			Request<?> request, RemoteEntityHomeCallback callback) {
		instance.pending.put(task, request);
		if (callback != null) {
			instance.callbacks.put(request, callback);
		}
		task.execute(request);
	}

	/**
	 * Removes a task form the pending list
	 * 
	 * @param task
	 *            {@link PerformRequestTask} to be removed
	 */
	protected static void removeTask(PerformRequestTask task) {
		Request<?> r = instance.pending.remove(task);
		if (r == null) {
			Log.e(TAG, "removeTask tried to remove task which was not present");
			return;
		}

		instance.callbacks.remove(r);

	}

	/**
	 * Restarts a failed server request.
	 * 
	 * @param task
	 *            {@link PerformRequestTask} to be restarted
	 */
	protected static void restartTask(PerformRequestTask task) {
		Request<?> r = instance.pending.remove(task);
		if (r == null) {
			Log
					.e(TAG,
							"restartTask tried to remove task which was not present");
			return;
		}

		PerformRequestTask newTask = new PerformRequestTask(task);
		instance.pending.put(newTask, r);
		newTask.execute(r);

	}

	private static MapRemoteHome mapHome;
	private static LocationRemoteHome locationHome;
	private static FingerprintRemoteHome fingerprintHome;

	/**
	 * Returns the Entity RemoteEntityHome that is responsible for the request.
	 * 
	 * @param request
	 *            {@link Request}
	 * @return RemoteEntityHome for specific request
	 */
	public static IRemoteEntityHome getRemoteEntityHome(Request<?> request) {
		return getRemoteEntityHome(request.getAction());
	}

	/**
	 * Returns the Entity RemoteEntityHome that is responsible for an action.
	 * 
	 * @param type
	 *            Action
	 * @return RemoteEntityHome for specific request type
	 */
	public static IRemoteEntityHome getRemoteEntityHome(RequestType type) {
		switch (type) {
		case setMap:
		case getMapList:
		case removeMap:
			if (mapHome == null) {
				mapHome = new MapRemoteHome();
			}

			return mapHome;

		case getLocation:
		case getLocationList:
		case removeLocation:
		case updateLocation:

			if (locationHome == null) {
				locationHome = new LocationRemoteHome();
			}

			return locationHome;

		case setFingerprint:

			if (fingerprintHome == null) {
				fingerprintHome = new FingerprintRemoteHome();
			}

			return fingerprintHome;

		default:
			throw new IllegalArgumentException("No RemoteEntityHome for type "
					+ type);
		}
	}

	/**
	 * 
	 * @param response
	 *            {@link Response} received from the server
	 * @return <code>true</code> if request was successful
	 */
	private static boolean isSuccess(Response<?> response) {
		return response.getStatus() == Status.ok;
	}

	/**
	 * Hands over the response to the responsible Entity RemoteEntityHome.
	 * 
	 * @see PerformRequestTaskCallback#onPerformedBackground(Request, Response,
	 *      PerformRequestTask)
	 */
	public void onPerformedBackground(Request<?> request, Response<?> response,
			PerformRequestTask task) {
		if (isSuccess(response)) {
			getRemoteEntityHome(request.getAction()).onRequestPerformed(
					request, response, this);
		}
	}

	/**
	 * Calls the {@link RemoteEntityHomeCallback} if request was successful,
	 * otherwise retries to perform the request.
	 * 
	 * @see PerformRequestTaskCallback#onPerformedForeground(Request, Response,
	 *      PerformRequestTask)
	 */
	public void onPerformedForeground(Request<?> request, Response<?> response,
			PerformRequestTask task) {
		RemoteEntityHomeCallback cb = callbacks.get(request);

		if (isSuccess(response)) {
			if (cb != null) {
				cb.onResponse(response);
			}
		} else {

			Integer i = retryCount.get(request);
			if (i != null) {
				i++;
			} else {
				i = 1;
			}

			if (i < MAX_TRIES) {
				retryCount.put(request, i);
				restartTask(task);
				return;

			} else {
				retryCount.remove(request);
				if (cb != null) {
					cb.onFailure(response);
				}
			}

		}

		removeTask(task);

	}

	/**
	 * Retries to perform the canceled request
	 * 
	 * @see PerformRequestTaskCallback#onCanceledForeground(Request,
	 *      PerformRequestTask)
	 */
	public void onCanceledForeground(Request<?> request, PerformRequestTask task) {
		restartTask(task);
	}

	/*
	 * For testing only TODO: remove
	 */

	// TODO: remove
	public static HashMap<PerformRequestTask, Request<?>> getPending() {
		return instance.pending;
	}

	// TODO: remove
	public static HashMap<Request<?>, RemoteEntityHomeCallback> getCallbacks() {
		return instance.callbacks;
	}

	// TODO: remove
	public static HashMap<Request<?>, Integer> getRetryCount() {
		return instance.retryCount;
	}

	// TODO: remove
	public static void clear() {
		instance.pending.clear();
		instance.retryCount.clear();
		instance.callbacks.clear();
	}

	// TODO: remove
	public static RemoteEntityHome getInstance() {
		return instance;
	}

}
