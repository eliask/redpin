/**
 *  Filename: RequestHandler.java (in org.redpin.server.standalone.net)
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



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.redpin.server.standalone.json.GsonFactory;
import org.redpin.server.standalone.net.Response.Status;
import org.redpin.server.standalone.util.Configuration;
import org.redpin.server.standalone.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Handler for a request. Each request read by a connection handler is passed to this handler 
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class RequestHandler {
	
	
	public static final String ACTION_TOKEN = "action";
	public static final String DATA_TOKEN = "data";
	public static final String NO_ACTION = "no action specified";
	
	/**
	 * Different request type supported by the server
	 * 
	 * @author Pascal Brogle (broglep@student.ethz.ch)
	 *
	 */
	public enum RequestType { 
		setFingerprint,
		getLocation,
		getMapList,
		setMap,
		removeMap,
		getLocationList,
		updateLocation,
		removeLocation;
	};
	
	
	
	/**
	 * Does handle an request
	 * 
	 * @param request Request
	 * @return response as string
	 */
	public String request(String request) {
		
		Response response = new Response(Status.failed, null, null);
		
		Gson gson = GsonFactory.getGsonInstance();
		JsonParser parser = new JsonParser();
		
		IHandler handler = null;
		
		try {
			
			JsonElement root = parser.parse(request);
			
			if(root.isJsonObject()) {
				JsonObject rootobj = root.getAsJsonObject();
				JsonElement action = rootobj.get(ACTION_TOKEN);
				JsonElement data = rootobj.get(DATA_TOKEN);
				
				if(action == null) {
					throw new Exception(NO_ACTION);
				}
				
				RequestType type = gson.fromJson(action, RequestType.class);				
				handler = HandlerFactory.findHandler(type);
				response = handler.handle(data);
				
			}
	
		} catch (JsonParseException e) {
			response = new Response(Status.jsonError, e.getMessage(), null);
			Log.getLogger().fine("json parse error: " + e.getMessage());
		} catch (Exception e) {
			response = new Response(Status.failed, e.getMessage(), null);
			Log.getLogger().fine("error: " + e.getMessage());
		}
		
		
		String response_str = "";
		try {
			response_str = gson.toJson(response);
		} catch (Exception e) {
			response_str = "{\"status\":\"" +Status.jsonError +"\",\"message\":\""+ e + ": "+e.getMessage()+"\"}";
			Log.getLogger().fine("json serializaion error: " + e.getMessage());
		}
		
		if(Configuration.LogRequests) {
			try {
				File f,r;
				int i = 0;
				while(true) {
					f = new File(Configuration.LogRequestPath + "/" + handler.getClass().getSimpleName() + "_" +i);
					r = new File(Configuration.LogRequestPath + "/" + handler.getClass().getSimpleName() + "_" +i + "_response");
					if(!f.exists()) {
						BufferedWriter bw = new BufferedWriter(new FileWriter(f));
						bw.write(request);
						bw.close();
						
						bw = new BufferedWriter(new FileWriter(r));
						bw.write(response_str);
						bw.close();
						break;
					} else {
						i++;
					}
				}
			} catch (IOException e) {
				
			}
		}

		return response_str;		
	}
	
	
	

}
