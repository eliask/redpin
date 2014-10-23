/**
 *  Filename: BaseLocationTypeAdapter.java (in org.redpin.android.json)
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

import org.redpin.android.core.Location;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * adapter for specific org.redpin.base.core.* type (it is needed to get always
 * a org.repin.server.standalone.core.* instance after deserialization
 *
 * @see JsonSerializer
 * @see JsonDeserializer
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class BaseLocationTypeAdapter implements
		JsonSerializer<org.redpin.base.core.Location>,
		JsonDeserializer<org.redpin.base.core.Location> {

	/**
	 * @see JsonSerializer#serialize(Object, Type, JsonSerializationContext)
	 */
	public JsonElement serialize(org.redpin.base.core.Location src,
			Type typeOfSrc, JsonSerializationContext context) {

		return context.serialize(src, Location.class);
	}

	/**
	 * @see JsonDeserializer#deserialize(JsonElement, Type,
	 *      JsonDeserializationContext)
	 */
	public Location deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {

		return context.deserialize(json, Location.class);
	}

}
