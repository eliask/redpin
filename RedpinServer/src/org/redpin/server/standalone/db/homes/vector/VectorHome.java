/**
 *  Filename: VectorHome.java (in org.redpin.server.standalone.db.homes.vector)
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
package org.redpin.server.standalone.db.homes.vector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.redpin.server.standalone.core.Vector;
import org.redpin.server.standalone.db.IEntity;
import org.redpin.server.standalone.db.homes.EntityHome;

/**
 * Abstract class which provides support for vector homes
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 * @param <E> Entity which is contained by the vector
 */

abstract public class VectorHome<E extends IEntity<Integer>> extends EntityHome<Vector<E>> {
	
	
	
	
	public VectorHome() {
		super();
	}

	private static final String[] TableCols = {"size", "containedObjectsClassName","containedObjectIds"};
	private static final String TableName = "serializablevector"; 
	private static final String TableIdCol = "vectorId";
	
	
	/**
	 * Gets the contained object class name. this is needed for backwards compability
	 * 
	 * @return The contained object class name
	 */
	abstract protected String getContainedObjectClassName();
	
	/**
	 * Gets the contained object entity home
	 * @return Contained object entity home
	 */
	abstract protected EntityHome<E> getObjectHome();
	

	/**
	 * @see EntityHome#getColValues(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	protected Object[] getColValues(Vector<E> e) {
		Object[] res = new Object[getTableCols().length];
		res[0] = e.size();
		res[1] = getContainedObjectClassName();
		res[2] = implodeObjectIds(e);
		
		return res;
	}

	/**
	 * @see EntityHome#getTableIdCol()
	 */
	@Override
	protected String getTableIdCol() {
		return TableIdCol;
	}

	/**
	 * @see EntityHome#getTableCols()
	 */
	@Override
	protected String[] getTableCols() {
		return TableCols;
	}

	/**
	 * @see EntityHome#getTableName()
	 */
	@Override
	protected String getTableName() {
		return TableName;
	}

	/**
	 * @see EntityHome#parseResultRow(ResultSet)
	 */
	@Override
	protected Vector<E> parseResultRow(ResultSet rs) throws SQLException {
		Vector<E> v = new Vector<E>();
		
		try {
			
			v.setId(rs.getInt(1));
			
			int size = rs.getInt(2);
			
			String[] objIds = rs.getString(4).split(";");
			
			for(int i=0; i < size; i++) {
				v.add(getObjectHome().getById(new Integer(objIds[i])));
			}
			
			
		
		} catch (SQLException e) {
			log.log(Level.SEVERE, "parseResultRow failed: " + e.getMessage(), e);
			throw e;
		}
		
		return v;
	}
	
	/**
	 * Checks if each element of the {@link Vector} is present in the database. 
	 * if it is not present, the element is added to the database
	 * 
	 * @param e {@link Vector}
	 */
	private void checkVector(Vector<E> e) {
		for(E el: e) {
			if(el.getId() == null) {
				log.finer("vector element not yet saved, now saving it");
				el = getObjectHome().add(el);
			}
		}
	}
	
	/**
	 * Adds a {@link Vector} and its elements (if not already added)
	 * 
	 * @see EntityHome#add(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	public Vector<E> add(Vector<E> e) {
		checkVector(e);
		return super.add(e);
	}
	
	/**
	 * Updates a {@link Vector} and adds new elements (if not already added).
	 * Does not remove old elements of the {@link Vector}. In order to this, use {@link VectorHome#removeObjectFromVector(Vector, IEntity)}
	 * 
	 * @see EntityHome#update(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	public boolean update(Vector<E> e) {
		checkVector(e);
		return super.update(e);
	}
	
	/**
	 * Removes all element of the {@link Vector} from the database. 
	 * This function is used for implicit deletion all the {@link Vector}'s elements when the {@link Vector} is deleted
	 *   
	 * @param v Vector
	 */
	private void removeObjects(Vector<E> v) {
		for(E e: v) {
			if(e == null) {
				log.log(Level.WARNING, "implicit deletion of vector element failed: element is null");
			} else {
				if(!getObjectHome().remove(e)) {
					log.log(Level.WARNING, "implicit deletion of vector element failed (already deleted): " + e );
				}
			}
		}
	}
	
	/**
	 * Removes a {@link Vector} by its id. Also removes containing elements (if not already removed)
	 * 
	 * @see EntityHome#removeById(Integer)
	 */
	@Override
	public boolean removeById(Integer id) {
		Vector<E> v = getById(id);
		removeObjects(v);
		return super.removeById(id);
	}
	
	/**
	 * Removes a {@link Vector}. Also removes containing elements (if not already removed)
	 * 
	 * @see EntityHome#remove(org.redpin.server.standalone.db.IEntity)
	 */
	@Override
	public boolean remove(Vector<E> e) {
		removeObjects(e);
		return super.remove(e);
	}
	
	
	/**
	 * Helper Function in order to remove an element from {@link Vector} and database 
	 * (there is no automatic removal of removed elements in the {@link Vector} on an update) 
	 * 
	 * @param v {@link Vector} of which a element wants to be removed
	 * @param e Element to be removed (in database and {@link Vector})
	 * @return <code>true</code> if {@link Vector} contains element and element is removed from database
	 */
	public boolean removeObjectFromVector(Vector<E> v, E e) {
		
		boolean res = false;
		
		if(v.contains(e)) {
			res = getObjectHome().remove(e) && v.remove(e);
		} 
		
		return res;
	}
	
	
	
	/**
	 * Creates a list of object id's like bandy (for backward compability)
	 * 
	 * @param e {@link Vector} which element ids to be imploded
	 * @return String containing all element id's, each followed with a semicolon
	 */
	protected String implodeObjectIds(Vector<E> e) {
		String res = "";
		for(E el: e) {
			res += el.getId() + ";";
		}
		return res;
		
	}
}
