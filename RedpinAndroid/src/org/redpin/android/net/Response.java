/**
 *  Filename: Response.java (in org.repin.android.net)
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

/**
 * Class representing a response from the server.
 *
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class Response<D> {

	/**
	 * Different status the server reports back to the client
	 *
	 * @author Pascal Brogle (broglep@student.ethz.ch)
	 *
	 */
	public enum Status {
		ok, failed, warning, jsonError;
	}

	private Status status;
	private String message;
	private D data;

	/**
	 * Empty constructor is need for deserialization
	 */
	protected Response() {
	}

	/**
	 *
	 * @param status
	 *            Response {@link Status}
	 * @param message
	 *            Message from the server
	 */
	public Response(Status status, String message) {
		this.status = status;
		this.message = message;
	}

	/**
	 *
	 * @param status
	 *            Response {@link Status}
	 * @param message
	 *            Message from the server
	 * @param data
	 *            Data returned by the server
	 */
	public Response(Status status, String message, D data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}

	/**
	 *
	 * @param status
	 *            Response {@link Status}
	 * @param data
	 *            Data returned by the server
	 */
	public Response(Status status, D data) {
		this.status = status;
		this.data = data;
	}

	/**
	 *
	 * @param status
	 *            Response {@link Status}
	 */
	public Response(Status status) {
		this.status = status;
	}

	/**
	 *
	 * @return {@link Status}
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 *
	 * @return Message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 *
	 * @return Data
	 */
	public D getData() {
		return data;
	}

}
