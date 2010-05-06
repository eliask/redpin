/**
 *  Filename: EntityHome.java (in org.redpin.server.standalone.db.homes)
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.redpin.server.standalone.db.DatabaseConnection;
import org.redpin.server.standalone.db.IEntity;
import org.redpin.server.standalone.util.Configuration;
import org.redpin.server.standalone.util.Log;
import org.redpin.server.standalone.util.Configuration.DatabaseTypes;

/**
 * Abstract class which provides an partial implementation of all function 
 * needed to add, get, update and remove entities to/from database
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 * @param <T> entity which implements the interface IEntity<Integer>
 */

public abstract class EntityHome<T extends IEntity<Integer>> implements IEntityHome<T, Integer> {
	
	protected DatabaseConnection db;	
	protected Logger log;
	

	public EntityHome() {
		this.db = DatabaseConnection.getInstance();
		this.log = Log.getLogger();
	}
	
	
	
	/**
	 * 
	 * @return Table name of entity
	 */
	abstract protected String getTableName();
	
	/**
	 * 
	 * @return Primary key column name 
	 */
	abstract protected String getTableIdCol();
	
	/**
	 * 
	 * @return All table columns excluding the primary key column
	 */
	abstract protected String[] getTableCols();
	
	/**
	 * 
	 * @param e Entity
	 * @return All column values (including primary key) in order getTableIdCol(), getTableCols() 
	 */
	abstract protected Object[] getColValues(T e);	
	
	/**
	 * This function restores an entity from a database row
	 * 
	 * @param rs {@link ResultSet}
	 * @return Restored entity from database row
	 * @throws SQLException
	 */
	abstract protected T parseResultRow(ResultSet rs) throws SQLException;
		
	
	/* add */
	
	/**
	 * Add an entity to the database. 
	 * 
	 * @param e Entity
	 * @return Entity with its generated primary key
	 */
	@Override
	public T add(T e) {
		String[] cols = getTableCols();
		String[] questionmarks = new String[cols.length];
		for(int i=0; i < cols.length; i++) {
			questionmarks[i] = "?";
		}
		String sql = "INSERT INTO " + getTableName() + " (" + implode(getTableCols()) + ") VALUES (" + implode(questionmarks)  + ")";
		log.finest(sql);
		
		T res = null;
		
		try {
			ResultSet rs = executeUpdate(sql, getColValues(e));
			if(rs != null) {
				
				if(rs.next()) {
					int i = rs.getInt(1);
					e.setId(new Integer(i));
					res = e;
				}
				
			} 
		
		} catch (SQLException ex) {
			log.log(Level.SEVERE, "add failed: " + ex.getMessage(), ex);
		}
		
		return res;
	}
	
	/**
	 * Add a list of entities
	 * 
	 * @param list {@link List} of entities
	 * @return {@link List} of entities with their generated primary keys
	 */
	@Override	
	public List<T> add(List<T> list) {
		
		T el;
		List<T> res = new ArrayList<T>(list.size());
		
		for(T e : list) {
			el = add(e);
			if(el == null) {
				break;
			} else {
				res.add(el);
			}
			
		}		

		return res;
	}

	
	/* get */
	
	/**
	 * Get an entity by it's primary key
	 * 
	 * @param id Primary Key
	 * @return Entity
	 */
	@Override
	public T getById(Integer id) {
		String sql = "SELECT * FROM " + getTableName() + " WHERE " + getTableIdCol() + "=" + id;
		log.finest(sql);
		
		T res = null;

		try {
			ResultSet rs = executeQuery(sql);
			if(rs.next()) {
				res = parseResultRow(rs);
			} else {
				log.log(Level.WARNING, "getById returned no result");
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "getById failed: " + e.getMessage(), e);
		}
		
		return res;
	}
	
	/**
	 * Get all entities
	 * 
	 * @return {@link List} of all entities
	 */
	@Override
	public List<T> getAll() {
		String sql = "SELECT * FROM " + getTableName() ;
		log.finest(sql);
		
		List<T> res = new ArrayList<T>();
		
		try {
			ResultSet rs = executeQuery(sql);
			while(rs.next()) {
				res.add(parseResultRow(rs));
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "getAll failed: " + e.getMessage(), e);
		}
		
		return res;
	}
	
	/**
	 * Get an entity
	 * 
	 * @param e Entity
	 * @return Entity with all fields filled from database
	 */
	@Override
	public T get(T e) {
		return getById(e.getId());
	}
	
	/**
	 * Get a {@link List} of entities
	 * 
	 * @param list {@link List} of primary keys
	 * @return {@link List} of entities
	 */
	@Override
	public List<T> get(List<T> list) {
		List<T> res = new ArrayList<T>(list.size());
		for(T e : list) {
			res.add(get(e));
		}
		return res;
	}
	
	/**
	 * Get a {@link List} of entities by their primary keys
	 * 
	 * @param ids {@link List} of primary keys
	 * @return {@link List} of entities
	 */
	@Override
	public List<T> getById(List<Integer> ids) {
		List<T> res = new ArrayList<T>(ids.size());
		for(Integer id : ids) {
			res.add(getById(id));
		}
		return res;
	}

	
	
	

	
	/* remove */
	
	/**
	 * Removes an entity by it's primary key
	 * 
	 * @param id Primary Key
	 * @return True if successful
	 */
	@Override
	public boolean removeById(Integer id) {
		String sql = "DELETE FROM " + getTableName() + " WHERE " + getTableIdCol() + "=" + id;
		log.finest(sql);
		
		boolean res = false;
				
		try {
			
			res = executeUpdate(sql);
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, "getById failed: " + e.getMessage(), e);
		}
		
		return res;
		
		
	}
	
	/**
	 * Removes an entity
	 * 
	 * @param e Entity
	 * @return True if successful
	 */
	@Override
	public boolean remove(T e) {
		return removeById(e.getId());
	}	
	
	/**
	 * Removes a {@link List} of entities
	 * 
	 * @param list {@link List} of entities
	 * @return True if removal of all entities was successful 
	 */
	@Override
	public boolean remove(List<T> list) {
		boolean ok = true;		
		
		for(T e : list) {			
			if(!remove(e))
				break;			
		}		

		return ok;
	}
	
	/**
	 * Removes a list of entities by their primary keys
	 * 
	 * @param ids List of primary keys
	 * @return True if removal of all entities was successful
	 */
	@Override
	public boolean removeById(List<Integer> ids) {
		boolean ok = true;		
		
		for(Integer id : ids) {			
			if(!removeById(id)) {
				ok = false;
				break;
			}			
		}		

		return ok;
	}


	
	/**
	 * Removes all entities from database
	 * 
	 * @return True if removal was successful
	 */
	@Override
	public boolean removeAll() {		
		return remove(getAll());
	}

	/* update */
	
	/**
	 * Updates an entity
	 * 
	 * @param e Entity
	 * @return True if successful
	 */
	@Override
	public boolean update(T e) {
		
		String[] cols = getTableCols();
		cols = Arrays.copyOf(cols, cols.length);
		
		for(int i=0; i < cols.length; i++) {
			cols[i] = cols[i] + "=?";
		}
		
		
		String sql = "UPDATE " + getTableName() + " SET " + implode(cols) + " WHERE " + getTableIdCol() + "=" + e.getId();
		log.finest(sql);
		
		boolean res = false;

		try {
			
			ResultSet rs = executeUpdate(sql, getColValues(e));
			if(rs != null) {
				
				if(Configuration.DatabaseType == DatabaseTypes.SQLITE) {
					if(rs.next()) {
						res = true;
					}
				} 
				
				if(Configuration.DatabaseType == DatabaseTypes.MYSQL) {
					res = true;
				}
				
			} 
			
			
		} catch (SQLException ex) {
			log.log(Level.SEVERE, "getById failed: " + ex.getMessage(), ex);
		}
		
		return res;
	}
	
	
	/**
	 * Updates a {@link List} of entities
	 * 
	 * @param list {@link List} of entities to be updated
	 * @return <code>true</code> if all entities were updated successfully
	 */
	@Override
	public boolean update(List<T> list) {
		boolean ok = true;		
		
		for(T e : list) {			
			if(!update(e)) {
				ok = false;
				break;
			}			
		}		

		return ok;
	}

		
	/**
	 * 
	 * @param sql SQL query to be executed
	 * @return {@link ResultSet} in case of success, otherwise <code>null</code>
	 * @throws SQLException
	 */
	protected ResultSet executeQuery(String sql) throws SQLException {
		 
		
		Statement stat;
		ResultSet res = null;

		try {
			stat = db.getConnection().createStatement();
			res = stat.executeQuery(sql);

		} catch (SQLException e) {
			log.log(Level.SEVERE, "executeQuery failed: " + e.getMessage(), e);
			throw e;
		}
		
		
		return res;
		
		
	}
	
	
	/**
	 * This function is intended to be used for DELETE commands, because
	 * it does not return the generated key
	 * 
	 * @param sql SQL command (DELETE) to be executed
	 * @return <code>true</code> if at least one row has been changed
	 * @throws SQLException
	 */
	protected boolean executeUpdate(String sql) throws SQLException {
		
		Connection conn = db.getConnection();
		Statement stat = null;
		boolean res;
		
		
		try {
			stat = conn.createStatement();
			int uc = stat.executeUpdate(sql);
			log.finest("executeUpdate(sql) Update Count: " + uc);
			res = (uc > 0);
		} catch (SQLException e) {
			/*TODO: perhaps retry executeUpdate (often encountered a 
			 * database is locked error from sqlite jdbc when database file is accessed outsite
			 */
			log.log(Level.SEVERE, "executeUpdate(sql) failed: " + e.getMessage(), e);
			throw e;
		}
		
		return res;
		
	}
	
	/**
	 * This function is intended to be used for INSERT and UPDATE commands
	 * where the generated key is needed
	 * 
	 * @param sql SQL command (INSERT, UPDATE) to be executed
	 * @param data Array of data do be used in the SQL command
	 * @return {@link ResultSet} with generated key
	 * @throws SQLException
	 */
	protected ResultSet executeUpdate(String sql, Object[] data) throws SQLException {
		
		PreparedStatement ps;
		ResultSet res = null;

		try {
			ps = getPreparedStatement(sql);
			
			int len = data.length;
			for(int i=0; i < len; i++) {
				ps.setObject(i+1, data[i]);
			}
			
			int uc = ps.executeUpdate();
			log.finest("executeUpdate(sql,data) Update Count: " + uc);			
			res = getGeneratedKey(ps);
			
			
			
			

		} catch (SQLException e) {
			log.log(Level.SEVERE, "executeUpdate(sql,data) failed: " + e.getMessage(), e);
			throw e;
		}
		
		
		return res;
		
	}
	
	
	/**
	 * Helper function to get a prepared statement.
	 * Does consider different methods of retrieving the generated keys depending on the database
	 * 
	 * @param sql SQL command
	 * @return {@link PreparedStatement}
	 * @throws SQLException
	 */
	protected PreparedStatement getPreparedStatement(String sql) throws SQLException {
		Connection conn = db.getConnection();
		PreparedStatement ps = null;
		try {
			if(conn.getMetaData().supportsGetGeneratedKeys()) {
				
					ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				
			} else {
				ps = conn.prepareStatement(sql);
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "getPreparedStatement failed: " + e.getMessage(), e);
			throw e;
		}
		
		return ps;
			
	}
	
	/**
	 * Helper function to get generated key
	 * Does consider different methods of retrieving the generated keys depending on the database
	 * 
	 * @param ps {@link PreparedStatement}
	 * @return {@link ResultSet} with generated key
	 * @throws SQLException
	 */
	protected ResultSet getGeneratedKey(PreparedStatement ps) throws SQLException {
		
		ResultSet rs = null;
		
		try {
		
			if(db.getConnection().getMetaData().supportsGetGeneratedKeys()) {
				rs = ps.getGeneratedKeys();
			} else {
				
				if(Configuration.DatabaseType == DatabaseTypes.SQLITE) {
					// workaround: retrieve keys with 'select last_insert_rowid();'
					rs = ps.getGeneratedKeys();
				} else {
					throw new SQLException("driver does not support retrieving generated keys.");
				}
				
			}
		
		} catch (SQLException e) {
			log.log(Level.SEVERE, "getGeneratedKey failed: " + e.getMessage(), e);
			throw e;
		}
		
		
		return rs;
		
	}
	
	/**
	 * 
	 * @param obj Objects to be imploded
	 * @return String with each object separated by a colon
	 */
	protected String implode(Object[] obj) {
		
		String res;
		
		if(obj.length == 0) {
			res = "";
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(obj[0]);
			for(int i=1; i < obj.length; i++) {
				sb.append(", ");
				sb.append(obj[i]);
			}
			res = sb.toString();
		}
		
		return res;
	}

}
