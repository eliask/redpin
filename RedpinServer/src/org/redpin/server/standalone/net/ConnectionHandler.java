/**
 *  Filename: ConnectionHandler.java (in org.redpin.server.standalone.net)
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.redpin.server.standalone.util.Log;

/**
 * Handler for a connection.
 * 
 * It does handle an incoming connection and reads requests and pass each of them to the request handler
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */

public class ConnectionHandler implements Runnable {

	private Socket socket;	
	private DataInputStream in;
	private DataOutputStream out;
	
	private Logger log = Log.getLogger();
	
	public ConnectionHandler(Socket s) throws IOException {
		socket = s;		
		in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
		out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
	}
	
	/**
	 * reads each request and passes it to the request handler. 
	 * closes a connection if requested
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		
		log.fine("Connection Handler " + Thread.currentThread().getId() +": Started Connection " + socket.getInetAddress().getHostName() + ":" + socket.getPort());
		try {
			
			
			RequestHandler rhandler = new RequestHandler();
			ImageHandler imgHandler = new ImageHandler(in, out);
			String line = in.readLine();
			if(line != null) {
				if((line.indexOf("GET") == 0) || (line.indexOf("POST") == 0)) {
					imgHandler.handle(line);
				} else {
					out.write((rhandler.request(line) + "\n").getBytes("US-ASCII"));
				}
			}

			out.flush();

			socket.close();

			
		} catch (IOException e) {
			log.log(Level.SEVERE, "Connection Handler: ", e);
			
			e.printStackTrace();
		}
		
		log.fine("Connection Handler " + Thread.currentThread().getId() +": Closed Connection " + socket.getInetAddress().getHostName() + ":" + socket.getPort());
		
		
		
	}

}
