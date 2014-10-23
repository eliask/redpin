/**
 *  Filename: RequestHandler.java (in org.repin.android.net)
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
package org.redpin.android.net;

import java.io.IOException;

import org.redpin.android.ApplicationContext;
import org.redpin.android.json.GsonFactory;
import org.redpin.android.net.Response.Status;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

/**
 * The {@link RequestHandler} is responsible for serializing and deserializing
 * the server request
 *
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class RequestHandler {

	private static Gson gson = GsonFactory.getGsonInstance();

	/**
	 * Performs a server request
	 *
	 * @param request
	 *            {@link Request} to be performed
	 * @return {@link Response} from the server
	 */
	public static Response<?> performRequest(Request<?> request) {
		String json = gson.toJson(request, request.getRequestType());

		String str;
		try {
			str = ConnectionHandler.performRequest(json);
		} catch (IOException e1) {

			/*
			 * Notify InternetConnectionManager that connectivity might have
			 * changed
			 */
			try {
				Context context = ApplicationContext.get();
				context.bindService(new Intent(context,
						InternetConnectionManager.class), mConnection,
						Context.BIND_AUTO_CREATE);
				// mManager.checkConnectivity();
				//TODO: check if possible to move the unbinding to the mConnection
				context.unbindService(mConnection);
			} catch (IllegalArgumentException e) {
			}

			return new Response<Void>(Status.failed, e1.getMessage());
		}

		Response<?> response = null;
		try {
			response = gson.fromJson(str, request.getResponseType());
		} catch (JsonParseException e) {
			return new Response<Void>(Status.jsonError, e.getMessage());
		}

		return response;
	}

	/**
	 * {@link RequestHandler.mManager} stores the currently bound
	 * {@link InternetConnectionManager}.
	 */
	private static InternetConnectionManager mManager;

	/**
	 * The {@link RequestHandler.mConnection} is responsible for connecting to
	 * the {@link InternetConnectionManager} service.
	 */
	private static ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mManager = ((InternetConnectionManager.LocalBinder) service)
					.getService();
			mManager.checkConnectivity();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mManager = null;
		}

	};

}
