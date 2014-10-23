/**
 *  Filename: ConnectionHandler.java (in org.repin.android.net)
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;

/**
 * The {@link ConnectionHandler} handles a connection to the redpin server. It
 * sends a request and waits for the response.
 *
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class ConnectionHandler {

	public static int port = 8000;
	public static String host = "10.0.2.2";// "10.0.2.2" is the host that runs the emulator
	public static String httpProtocol = "http://";

	/**
	 * Sends an request to the server
	 *
	 * @param str
	 *            JSON serialized request
	 * @return JSON serialized response
	 * @throws IOException
	 */
	public static String performRequest(String str) throws IOException {

		InetAddress serverAdr = null;
		serverAdr = Inet4Address.getByName(host);
		Socket socket = null;
		String response = null;
		try {

			socket = new Socket(serverAdr, port);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));

			out.write(str + "\n");
			out.flush();

			response = in.readLine();

			if (response == null) {
				throw new IOException("no response recieved");
			}
		} finally {
			if (socket != null) {
				socket.close();
			}
		}

		return response;

	}

	public static String getServerURL() {
		return httpProtocol + host;
	}

	public static int getServerPort() {
		return port;
	}
}
