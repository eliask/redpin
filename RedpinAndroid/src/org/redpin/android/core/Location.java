/**
 *  Filename: Location.java (in org.repin.android.core)
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
package org.redpin.android.core;

import org.redpin.android.db.LocalEntity;
import org.redpin.android.db.RemoteEntity;

/**
 * @see org.redpin.base.core.Location
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class Location extends org.redpin.base.core.Location implements
		RemoteEntity<Integer>, LocalEntity {

	protected Integer id;

	public Integer getRemoteId() {
		return id;
	}

	public void setRemoteId(Integer id) {
		this.id = id;
	}

	private transient long _id = -1;

	public long getLocalId() {
		return _id;
	}

	public void setLocalId(long id) {
		_id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (_id ^ (_id >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		result = prime * result + mapXcord;
		result = prime * result + mapYcord;
		result = prime * result
				+ ((symbolicID == null) ? 0 : symbolicID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (_id != other._id)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		if (mapXcord != other.mapXcord)
			return false;
		if (mapYcord != other.mapYcord)
			return false;
		if (symbolicID == null) {
			if (other.symbolicID != null)
				return false;
		} else if (!symbolicID.equals(other.symbolicID))
			return false;
		return true;
	}



}
