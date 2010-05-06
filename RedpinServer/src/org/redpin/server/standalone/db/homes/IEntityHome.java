/**
 *  Filename: IEntityHome.java (in org.redpin.server.standalone.db.homes)
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
package org.redpin.server.standalone.db.homes;

import java.util.List;

/**
 * Interface for all Entity Homes. An Entity Home provides all necessary 
 * function to add, get, update and remove entities to/from the database 
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 * @param <T> Element type
 * @param <ID> Primary key type (depending on database)
 */

public interface IEntityHome<T, ID> {
		
	public T add(T e);
	public List<T> add(List<T> l);
	
	public T get(T e);
	public T getById(ID id);
	public List<T> get(List<T> list);
	public List<T> getById(List<ID> ids);
	public List<T> getAll();
	
	public boolean update(T e);
	public boolean update(List<T> list);
	
	public boolean remove(T e);
	public boolean remove(List<T> list);
	public boolean removeAll();
	
	
	

}
