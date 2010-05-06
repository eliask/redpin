/**
 *  Filename: Response.java (in org.redpin.server.standalone.net)
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
package org.redpin.server.standalone.net;
/**
 * Class representing a response from the server. 
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class Response {
	
	/**
	 * Different status the server reports back to the client
	 * 
	 * @author Pascal Brogle (broglep@student.ethz.ch)
	 *
	 */
	public enum Status {
		ok,
		failed,
		warning,
		jsonError;
	}
	
	
	private Status status;
	private String message;
	private Object data;
	
	
	public Response() {}
	
	
	public Response(Status status, String message, Object data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}
	
	/**
	 * 
	 * @param status {@link Status} to be sent to the client
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
	
	/**
	 * 
	 * @return {@link Status} to be sent to the client
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * 
	 * @param message Message to be sent to the client
	 */
	public void setMessage(String message) {
		this.message = message;
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
	 * @param data Data to be sent to the client
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
	/**
	 * 
	 * @return Data
	 */
	public Object getData() {
		return data;
	}
	
}
