/*
*
* @file AbstractMapper.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: AbstractMapper.java,v 1.8 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.dbmappers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.palo.viewapi.DomainObject;
import org.palo.viewapi.internal.DbService;
import org.palo.viewapi.internal.DomainObjectImpl;
import org.palo.viewapi.internal.IDomainObjectManagement;

/**
 * <code>AbstractMapper</code> TODO DOCUMENT ME
 * 
 * @version $Id: AbstractMapper.java,v 1.8 2009/12/17 16:14:08 PhilippBouillon Exp $
 */
abstract class AbstractMapper extends Mapper implements IDomainObjectManagement {

	private final String ID_STMT = DbService.getQuery("identity"); // "CALL
																	// IDENTITY()";
	protected final DomainObjectCache cache = new DomainObjectCache();

	AbstractMapper() {
	}

	protected abstract String findStatement();
	protected abstract String findByNameStatement();
	protected abstract String insertStatement();
	protected abstract String deleteStatement();
	protected abstract void deleteAssociations(DomainObject obj)
			throws SQLException;
	protected abstract DomainObject doLoad(String id, ResultSet result)
			throws SQLException;
	protected abstract void doInsert(DomainObject obj, PreparedStatement stmt)
			throws SQLException;

	
	public final void reset() {
		cache.clear();
	}

	public final DomainObject find(String id) throws SQLException {
		DomainObject obj = cache.get(id);
		if (obj != null)
			return obj;

		PreparedStatement stmt = null;
		ResultSet results = null;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(findStatement());
			stmt.setString(1, id);
			results = stmt.executeQuery();
			if (results.next())
				obj = load(results);
			return obj;
		} finally {
			cleanUp(stmt, results);
		}
	}

	public final DomainObject findByName(String name) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet results = null;
		DomainObject obj = null;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(findByNameStatement());
			stmt.setString(1, name);
			results = stmt.executeQuery();
			if (results.next())
				obj = load(results);
		} finally {
			cleanUp(stmt, results);
		}
		return obj;
	}

	public synchronized void insert(DomainObject obj) throws SQLException {
		PreparedStatement stmt = null;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(insertStatement());
			doInsert(obj, stmt);
			stmt.execute();
			((DomainObjectImpl) obj).setId(getId(connection));
			cache.add(obj);
		} finally {
			cleanUp(stmt);
		}
	}

	public final void delete(DomainObject obj) throws SQLException {
		if (obj == null) {
			return;
		}
		PreparedStatement stmt = null;
		Connection connection = DbService.getConnection();
		try {
			// we first must delete associations to preserve db integrity
			deleteAssociations(obj);
			// now the object itself...
			stmt = connection.prepareStatement(deleteStatement());
			stmt.setString(1, obj.getId());
			stmt.execute();
			cache.remove(obj);
		} finally {
			cleanUp(stmt);
		}
	}

	protected DomainObject load(ResultSet rs) throws SQLException {
		String id = rs.getString(1);
		if(cache.contains(id))
			return cache.get(id);
		DomainObject obj = doLoad(id, rs);
		cache.add(obj);
		return obj;
	}

	private final String getId(Connection connection) throws SQLException {
		Long id = -1l;
		PreparedStatement idStmt = connection.prepareStatement(ID_STMT);
		ResultSet rs = idStmt.executeQuery();
		try {
			if (rs.next())
				id = rs.getLong(1);
			if (id == -1)
				throw new SQLException("Failed to receive id!");
			return Long.toString(id);
		} finally {
			cleanUp(idStmt, rs);
		}
	}
}
