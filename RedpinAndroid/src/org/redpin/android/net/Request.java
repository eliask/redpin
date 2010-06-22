/**
 *  Filename: Request.java (in org.repin.android.net)
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

import java.lang.reflect.Type;
import java.util.List;

import org.redpin.android.core.Fingerprint;
import org.redpin.android.core.Location;
import org.redpin.android.core.Map;
import org.redpin.android.core.Measurement;

import com.google.gson.reflect.TypeToken;

/**
 * Class representing an server request
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class Request<D> {

	/**
	 * Supported redpin request types
	 * 
	 * @author Pascal Brogle (broglep@student.ethz.ch)
	 * 
	 */
	public enum RequestType {
		setFingerprint, getLocation, getMapList, setMap, removeMap, getLocationList, updateLocation, removeLocation;
	};

	private RequestType action;
	private D data;
	private transient Type requestType;
	private transient Type responseType;

	/**
	 * Empty constructor is need for deserialization
	 */
	protected Request() {
	}

	/**
	 * 
	 * @param action
	 *            Action to be performed
	 * @param data
	 *            Data to be submitted with the request
	 */
	public Request(RequestType action, D data) {
		this(action);
		this.data = data;
	}

	/**
	 * 
	 * @param action
	 *            Action to be performed
	 */
	public Request(RequestType action) {
		super();
		this.action = action;
		setTypes(action);
	}

	/**
	 * 
	 * @param action
	 *            Action to be performed
	 */
	public void setAction(RequestType action) {
		this.action = action;
		setTypes(action);
	}

	/**
	 * 
	 * @return Action to be performed
	 */
	public RequestType getAction() {
		return action;
	}

	/**
	 * 
	 * @param data
	 *            Data to be submitted with the request
	 */
	public void setData(D data) {
		this.data = data;
	}

	/**
	 * 
	 * @return Data to be submitted with the request
	 */
	public D getData() {
		return data;
	}

	/**
	 * This method is needed for JSON serialization/deserialization
	 * 
	 * @return {@link Type} of the request
	 */
	public Type getRequestType() {
		return requestType;
	}

	/**
	 * This method is needed for JSON serialization/deserialization
	 * 
	 * @return {@link Type} of the response
	 */
	public Type getResponseType() {
		return responseType;
	}

	private static Type fingerprintRequestType;
	private static Type fingerprintResponseType;
	private static Type measurementRequestType;
	private static Type locationResponseType;
	private static Type voidRequestType;
	private static Type voidResponseType;
	private static Type mapListResponseType;
	private static Type mapRequestType;
	private static Type mapResponseType;
	private static Type locationRequestType;
	private static Type locationListResponseType;

	/**
	 * Setups the proper types for the action to be performed
	 * 
	 * @param t
	 *            Action to be performed
	 */
	private void setTypes(RequestType t) {
		switch (t) {
		case setFingerprint:
			if (fingerprintRequestType == null) {
				fingerprintRequestType = new TypeToken<Request<Fingerprint>>() {
				}.getType();
			}
			requestType = fingerprintRequestType;

			if (fingerprintResponseType == null) {
				fingerprintResponseType = new TypeToken<Response<Fingerprint>>() {
				}.getType();
			}
			responseType = fingerprintResponseType;
			break;

		case getLocation:
			if (measurementRequestType == null) {
				measurementRequestType = new TypeToken<Request<Measurement>>() {
				}.getType();
			}
			requestType = measurementRequestType;

			if (locationResponseType == null) {
				locationResponseType = new TypeToken<Response<Location>>() {
				}.getType();
			}
			responseType = locationResponseType;
			break;
		case getMapList:
			if (voidRequestType == null) {
				voidRequestType = new TypeToken<Request<Void>>() {
				}.getType();
			}
			requestType = voidRequestType;

			if (mapListResponseType == null) {
				mapListResponseType = new TypeToken<Response<List<Map>>>() {
				}.getType();
			}
			responseType = mapListResponseType;
			break;
		case setMap:
			if (mapRequestType == null) {
				mapRequestType = new TypeToken<Request<Map>>() {
				}.getType();
			}
			requestType = mapRequestType;

			if (mapResponseType == null) {
				mapResponseType = new TypeToken<Response<Map>>() {
				}.getType();
			}
			responseType = mapResponseType;
			break;
		case removeMap:
			if (mapRequestType == null) {
				mapRequestType = new TypeToken<Request<Map>>() {
				}.getType();
			}
			requestType = mapRequestType;

			if (voidResponseType == null) {
				voidResponseType = new TypeToken<Response<Void>>() {
				}.getType();
			}
			responseType = voidResponseType;
			break;
		case getLocationList:
			if (voidRequestType == null) {
				voidRequestType = new TypeToken<Request<Void>>() {
				}.getType();
			}
			requestType = voidRequestType;

			if (locationListResponseType == null) {
				locationListResponseType = new TypeToken<Response<List<Location>>>() {
				}.getType();
			}
			responseType = locationListResponseType;
			break;
		case updateLocation:
			if (locationRequestType == null) {
				locationRequestType = new TypeToken<Request<Location>>() {
				}.getType();
			}
			requestType = locationRequestType;

			if (voidResponseType == null) {
				voidResponseType = new TypeToken<Response<Void>>() {
				}.getType();
			}
			responseType = voidResponseType;
			break;
		case removeLocation:
			if (locationRequestType == null) {
				locationRequestType = new TypeToken<Request<Location>>() {
				}.getType();
			}
			requestType = locationRequestType;

			if (voidResponseType == null) {
				voidResponseType = new TypeToken<Response<Void>>() {
				}.getType();
			}
			responseType = voidResponseType;
			break;

		default:
			throw new RuntimeException(
					"Need to implement Request#setTypes() for all request types");
		}
	}

}
