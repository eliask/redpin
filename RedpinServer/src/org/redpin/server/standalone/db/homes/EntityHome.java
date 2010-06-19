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
 *  (c) Copyright ETH Zurich, Luba Rogoleva, Pascal Brogle, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */
package org.redpin.server.standalone.db.homes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
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
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 *
 * @param <T> entity which implements the interface IEntity<Integer>
 */

public abstract class EntityHome<T extends IEntity<Integer>> implements IEntityHome<T, Integer> {
	
	protected int primaryKeyId = -1;
	protected DatabaseConnection db;	
	protected Logger log;
	protected String TableColNames = null;
	protected String insertSQL = null;
	protected String updateSQL = null;
	protected String selectSQL = null;
	protected String deleteSQL = null;
	
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
	 * @return columns names of entity for a query
	 */
	protected String getTableColNames() {
		if (TableColNames == null) {
			TableColNames = getTableName() + "." + getTableIdCol();
			for (String colName : getTableCols()) {
				TableColNames += ", " + getTableName() + "." + colName;
			}
		}
		return TableColNames;
	}
		
	/**
	 * 
	 * @return next primary key for the entry
	 */
	protected synchronized int getPrimaryKeyId() {
		if (primaryKeyId == -1) {
			ResultSet rs = null;
			Statement stat = null;
			try {
				stat = db.getConnection().createStatement();
				rs = stat.executeQuery("SELECT MAX(" + getTableIdCol() + ") FROM " + getTableName());
				primaryKeyId = rs.next() ? rs.getInt(1) : 0;
			} catch (SQLException e) {
				primaryKeyId = -1;
				log.log(Level.SEVERE, "getPrimaryKeyId failed: " + e.getMessage(), e);
			} finally {
				try {
					if (rs != null) rs.close();
					if (stat != null) stat.close();
				} catch (SQLException es) {
					log.log(Level.WARNING, "failed to close db resources: " + es.getMessage(), es);
				}
			}
		}
		primaryKeyId++;
		return primaryKeyId;
	}
	
	/**
	 * 
	 * @return prepared INSERT sql string
	 */
	protected String getInsertSQL() {
		if (insertSQL == null) {
			insertSQL = " INSERT INTO " + getTableName() + " (" + getTableIdCol() + ", " + implode(getTableCols()) + ") VALUES (? " + repeat(",?", getTableCols().length) + ");";
		}
		return insertSQL;
	}
	
	/**
	 * 
	 * @return prepared UPDATE sql string
	 */	
	protected String getUpdateSQL() {

		if (updateSQL == null) {
			String[] cols = getTableCols();
			updateSQL = " UPDATE " + getTableName() + " SET ";
			boolean first = true;
			for(int i=0; i < getTableCols().length; i++) {
				if (!first) {
					updateSQL += " , ";
				} 
				updateSQL += cols[i] + " = ?";
				first = false;
			}
			
			updateSQL += " WHERE " + getTableIdCol() + " = ? ;";
		}
		return updateSQL;
	}
	
	/**
	 * 
	 * @return prepared SELECT sql string
	 */
	protected String getSelectSQL() {

		if (selectSQL == null) {
			selectSQL = "SELECT " + getTableColNames() + " FROM " + getTableName();
		}
		return selectSQL;
	}
	
	/**
	 * 
	 * @return SQL ORDER BY string
	 */
	protected String getOrder() {
		return "";
	}
	
	/**
	 * 
	 * @return prepared DELETE sql string
	 */
	protected String getDeleteSQL() {

		if (deleteSQL == null) {
			deleteSQL = "DELETE FROM " + getTableName();
		}
		return deleteSQL;
	}
	
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
	 * @see EntityHome#parseResultRow(ResultSet, int)
	 * 
	 * @param rs {@link ResultSet}
	 * @return Restored entity from database row
	 * @throws SQLException
	 */
	protected T parseResultRow(final ResultSet rs) throws SQLException {
		return parseResultRow(rs, 1);
	}
		
	/**
	 * This function restores an entity from a database row
	 * 
	 * @param rs {@link ResultSet}
	 * @param fromIndex index from where the parsing starts 
	 * @return Restored entity from database row
	 * @throws SQLException
	 */
	public abstract T parseResultRow(final ResultSet rs, int fromIndex) throws SQLException;
	
	/**
	 * @see EntityHome#fillInStatement(PreparedStatement, Object[], int[], int)
	 * 
	 * @param ps {@link PreparedStatement}
	 * @param values entity values 
	 * @param sqlTypes corresponding SQL {@link Types} for values
	 * @return number of filled in values
	 * @throws SQLException
	 */
	protected int fillInStatement(PreparedStatement ps, Object[] values, int[] sqlTypes) throws SQLException {
		return fillInStatement(ps, values, sqlTypes, 1);
	}
	
	/**
	 * Fills values into the {@link PreparedStatement} starting at fromIndex
	 * 
	 * @param ps {@link PreparedStatement}
	 * @param values entity values 
	 * @param sqlTypes corresponding SQL {@link Types} for values
	 * @param fromIndex index from where the filling in starts
	 * @return number of filled in values
	 * @throws SQLException
	 */
	protected int fillInStatement(PreparedStatement ps, Object[] values, int[] sqlTypes, int fromIndex) throws SQLException {
		for (int i = 0; i < values.length; i++) {
			ps.setObject(fromIndex + i, values[i], sqlTypes[i]);
		}
		
		return values.length;
	}
	
	/**
	 * @see EntityHome#fillInStatement(PreparedStatement, IEntity, int)
	 * 
	 * @param ps {@link PreparedStatement}
	 * @param e Entity
	 * @return number of filled in values
	 * @throws SQLException
	 */
	protected int fillInStatement(PreparedStatement ps, T e) throws SQLException {
		return fillInStatement(ps, e, 1);
	}
	
	/**
	 * Fills entity values into the {@link PreparedStatement} starting at fromIndex  
	 * @param ps {@link PreparedStatement}
	 * @param e Entity
	 * @return number of filled in values
	 * @throws SQLException
	 */
	protected abstract int fillInStatement(PreparedStatement ps, T e, int fromIndex) throws SQLException;
	
	
	/**
	 * 
	 * @param vps Vector<{@link PreparedStatement}>
	 * @param e entity
	 * @return primary key for the new entry
	 * @throws SQLException
	 */
	public int executeInsertUpdate(Vector<PreparedStatement> vps, T e) throws SQLException {
		int id =  getPrimaryKeyId();

		PreparedStatement ps  = getPreparedStatement(getInsertSQL());
		ps.setInt(1, id);
		fillInStatement(ps, e, 2);
		vps.add(ps);
		ps.executeUpdate();
		return id;
	}
	
	/**
	 * Add an entity to the database. 
	 * 
	 * @param e Entity
	 * @return Entity with its generated primary key
	 */
	public T add(T e) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps =  getPreparedStatement(getInsertSQL());
			e.setId(getPrimaryKeyId());
			ps.setInt(1, e.getId());
			fillInStatement(ps, e, 2);
			ps.executeUpdate();
			rs = getGeneratedKey(ps);
			if(rs != null && rs.next()) {
				e.setId(rs.getInt(1));
			}
			
		} catch (SQLException ex) {
			log.log(Level.SEVERE, "add failed: " + ex.getMessage(), ex);
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
			} catch (SQLException es) {
				log.log(Level.WARNING, "failed to close db resources: " + es.getMessage(), es);
			}
		}
		
		return e;
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
	 * Gets entities from database matching a constraint
	 * 
	 * @param constraint SQL WHERE constraint
	 * @return {@link List} of entities matching the constraint
	 */
	protected List<T> get(String constraint) {
		List<T> res = new ArrayList<T>();
		
		String sql = getSelectSQL();
		if (constraint != null && constraint.length() > 0) sql += " WHERE " + constraint;
		String order = getOrder();
		if (order != null && order.length() > 0) sql += " ORDER BY " + order;
		
		
		log.finest(sql);
		ResultSet rs = null;
		Statement stat = null;
		try {
			stat = db.getConnection().createStatement();
			rs = stat.executeQuery(sql);
			while(rs.next()) {
				res.add(parseResultRow(rs));
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "get failed: " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (stat != null) stat.close();
			} catch (SQLException es) {
				log.log(Level.WARNING, "failed to close ResultSet: " + es.getMessage(), es);
			}
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
		if (e == null || e.getId() == null || e.getId() == -1) {
			return null;
		}
		return getById(e.getId());
	}
	
	/**
	 * Get an entity by it's primary key
	 * 
	 * @param id Primary Key
	 * @return Entity
	 */
	@Override
	public T getById(Integer id) {
		if (id == null || id == -1) {
			return null;
		}
		String constraint = getTableIdCol() + " = " + id;
		List<T> list = get(constraint);
		if (list.size() == 0) {
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * Get a {@link List} of entities
	 * 
	 * @param list {@link List} of primary keys
	 * @return {@link List} of entities
	 */
	@Override
	public List<T> get(List<T> list) {
		
		if (list == null) {
			return new ArrayList<T>();
		}
		List<Integer> ids = new ArrayList<Integer>(list.size());
		for(T entity: list) {
			if (entity.getId() != null && entity.getId() != -1) {
				ids.add(entity.getId());
			}
		}	
		
		if (ids.isEmpty()) {
			return new ArrayList<T>();
		}
		
		
		
		return getById(ids);
	}
	
	/**
	 * Get a {@link List} of entities by their primary keys
	 * 
	 * @param ids {@link List} of primary keys
	 * @return {@link List} of entities
	 */
	@Override
	public List<T> getById(List<Integer> ids) {
		if (ids == null || ids.isEmpty()) {
			return new ArrayList<T>();
		}
		String constraint = getTableIdCol() + " IN  (" + implode(ids.toArray()) + ")";
		return get(constraint);
	}	
	
	/**
	 * Get all entities
	 * 
	 * @return {@link List} of all entities
	 */
	@Override
	public List<T> getAll() {
		return get("");
	}

	
	/* remove */
	
	/**
	 * Removes entities from database matching a constraint
	 * 
	 * @param constraint SQL WHERE constraint
	 * @return True if successful
	 */
	protected boolean remove(String constraint) {
		String sql = getDeleteSQL();
		if (constraint != null && constraint.length() > 0) sql += " WHERE " + constraint;
		
		log.finest(sql);
		
		Statement stat = null;
		try {
			int res = -1;

			stat = db.getConnection().createStatement();			
			res = stat.executeUpdate(sql);	
			
			return res > 0;
		} catch (SQLException e) {
			log.log(Level.SEVERE, "remove failed: " + e.getMessage(), e);
		} finally {
			try {
				db.getConnection().setAutoCommit(true);
				if (stat != null) stat.close();
			} catch (SQLException es) {
				log.log(Level.WARNING, "failed to close statement: " + es.getMessage(), es);
			}
		}
		return false;
	}
		
	/**
	 * Removes an entity
	 * 
	 * @param e Entity
	 * @return True if successful
	 */
	@Override
	public boolean remove(T e) {
		if (e == null || e.getId() == null || e.getId()  == -1) {
			return true;
		}
		String constraint = getTableIdCol() + " = " + e.getId();
		return remove(constraint);
	}	
	
	/**
	 * Removes a {@link List} of entities
	 * 
	 * @param list {@link List} of entities
	 * @return True if removal of all entities was successful 
	 */
	@Override
	public boolean remove(List<T> list) {
		if (list == null) {
			return true;
		}
		List<Integer> ids = new ArrayList<Integer>(list.size());
		for(T entity: list) {
			if (entity.getId() != null && entity.getId() != -1) {
				ids.add(entity.getId());
			}
		}	
		
		if (ids.isEmpty()) {
			return true;
		}
		
		String constraint = getTableIdCol() + " IN  (" + implode(ids.toArray()) + ")";
		
		return remove(constraint);
	}
	
	
	/**
	 * Removes all entities from database
	 * 
	 * @return True if removal was successful
	 */
	@Override
	public boolean removeAll() {		
		return remove("");
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
		PreparedStatement ps = null;
		boolean res = false;
		try {
			ps = db.getConnection().prepareStatement(getUpdateSQL());
			int c = fillInStatement(ps, e);
			ps.setInt(c + 1, e.getId());
			int numcol = ps.executeUpdate();
			res = numcol == 1;		
			
		} catch (SQLException ex) {
			log.log(Level.SEVERE, "update location failed: " + ex.getMessage(), ex);
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (SQLException es) {
				log.log(Level.WARNING, "failed to close ResultSet: " + es.getMessage(), es);
			}
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
	static protected String implode(Object[] obj) {
		
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


	private String repeat(String str, int repeatCount) {
		if (str == null) return null;		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < repeatCount; i ++) {
			sb.append(str);
		}
		
		return sb.toString();
	}

}
