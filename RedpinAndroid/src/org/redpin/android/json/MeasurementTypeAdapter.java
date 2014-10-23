/**
 *  Filename: MeasurementTypeAdapter.java (in org.redpin.android.json)
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
package org.redpin.android.json;

import java.lang.reflect.Type;
import java.util.Collection;

import org.redpin.android.core.Measurement;
import org.redpin.android.core.Vector;
import org.redpin.android.core.measure.BluetoothReading;
import org.redpin.android.core.measure.GSMReading;
import org.redpin.android.core.measure.WiFiReading;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 * Custom adapter for {@link Measurement}s in order to deserialize the
 * {@link Vector}s
 *
 * @see JsonDeserializer
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class MeasurementTypeAdapter implements JsonDeserializer<Measurement> {

	/**
	 * @see JsonDeserializer#deserialize(JsonElement, Type,
	 *      JsonDeserializationContext)
	 */
	public Measurement deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {

		// get all json elements in order to deserialize them separately
		JsonObject obj = json.getAsJsonObject();
		JsonElement json_timestamp = obj.get("timestamp");
		JsonElement json_wifi = obj.get("wifiReadings");
		JsonElement json_gsm = obj.get("gsmReadings");
		JsonElement json_bluetooth = obj.get("bluetoothReadings");

		// init vectors
		Vector<WiFiReading> wifi = new Vector<WiFiReading>();
		Vector<GSMReading> gsm = new Vector<GSMReading>();
		Vector<BluetoothReading> bluetooth = new Vector<BluetoothReading>();

		// deserialize reading vectors
		Type listType;
		if (json_wifi != null) {
			listType = new TypeToken<Vector<WiFiReading>>() {
			}.getType();
			Collection<WiFiReading> wificol = context.deserialize(json_wifi,
					listType);
			wifi.addAll(wificol);
		}

		if (json_gsm != null) {
			listType = new TypeToken<Vector<GSMReading>>() {
			}.getType();
			Collection<GSMReading> gsmcol = context.deserialize(json_gsm,
					listType);
			gsm.addAll(gsmcol);
		}

		if (json_bluetooth != null) {
			listType = new TypeToken<Vector<BluetoothReading>>() {
			}.getType();
			Collection<BluetoothReading> bluetoothcol = context.deserialize(
					json_bluetooth, listType);
			bluetooth.addAll(bluetoothcol);
		}
		// create deserialized measurement
		Measurement m = new Measurement(gsm, wifi, bluetooth);
		if (json_timestamp != null) {
			m.setTimestamp((Long) context.deserialize(json_timestamp,
					Long.class));
		}

		return m;
	}

}
