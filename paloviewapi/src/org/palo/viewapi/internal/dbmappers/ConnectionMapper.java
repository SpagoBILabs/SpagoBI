/*
*
* @file ConnectionMapper.java
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
* @version $Id: ConnectionMapper.java,v 1.5 2010/02/12 13:51:05 PhilippBouillon Exp $
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
import java.util.ArrayList;
import java.util.List;

import org.palo.viewapi.DomainObject;
import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.internal.DbService;
import org.palo.viewapi.internal.IConnectionManagement;
import org.palo.viewapi.internal.PaloConnectionImpl;
import org.palo.viewapi.internal.PaloConnectionImpl.Builder;


/**
 * <code>ConnectionMapper</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ConnectionMapper.java,v 1.5 2010/02/12 13:51:05 PhilippBouillon Exp $
 **/
final class ConnectionMapper extends AbstractMapper implements IConnectionManagement {

	private static final String TABLE = DbService.getQuery("Connections.tableName");
	private static final String COLUMNS = DbService.getQuery("Connections.columns");
	private static final String CREATE_TABLE_STMT = DbService.getQuery("Connections.createTable", TABLE);
	private static final String FIND_ALL_STMT = DbService.getQuery("Connections.findAll", COLUMNS, TABLE);
	private static final String FIND_BY_ID_STMT = DbService.getQuery("Connections.findById", COLUMNS, TABLE);
	private static final String FIND_BY_HOST_SERVICE_STMT = DbService.getQuery("Connections.findByHost", COLUMNS, TABLE);
	private static final String FIND_BY_NAME_STMT = DbService.getQuery("Connections.findByName", COLUMNS, TABLE);	
	private static final String INSERT_STMT = DbService.getQuery("Connections.insert", TABLE);
	private static final String UPDATE_STMT = DbService.getQuery("Connections.update", TABLE);
	private static final String DELETE_STMT = DbService.getQuery("Connections.delete", TABLE);


	public final PaloConnection findBy(String host, String service) {
		PreparedStatement stmt = null;
		ResultSet results = null;
		PaloConnection conn = null;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(FIND_BY_HOST_SERVICE_STMT);
			stmt.setString(1, host);
			stmt.setString(2, service);
			results = stmt.executeQuery();
			if (results.next())
				conn = (PaloConnection)load(results);
		} catch (SQLException e) {
			/* ignore */
		} finally {
			cleanUp(stmt, results);
		}
		return conn;
	}
	
	public final List<PaloConnection> findAll() throws SQLException {
		PreparedStatement stmt = null;
		ResultSet results = null;
		List<PaloConnection> connections = new ArrayList<PaloConnection>();
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(FIND_ALL_STMT);
			results = stmt.executeQuery();
			while(results.next()) { 
				PaloConnection paloConn = (PaloConnection)load(results);
				if(paloConn != null)
					connections.add(paloConn);
			}
		} finally {
			cleanUp(stmt, results);
		}
		return connections;
	}

	public final void update(DomainObject obj) throws SQLException {
		PaloConnection conn = (PaloConnection) obj;
		PreparedStatement stmt = null;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(UPDATE_STMT);
			stmt.setString(1, conn.getName());
			stmt.setString(2, conn.getHost());
			stmt.setString(3, conn.getService());
			stmt.setInt(4, conn.getType());
			stmt.setString(5, conn.getDescription());
			stmt.setString(6, conn.getId());
			stmt.execute();
		} finally {
			cleanUp(stmt);
		}

	}
	
	protected final void doInsert(DomainObject obj, PreparedStatement stmt)
			throws SQLException {
		PaloConnection conn = (PaloConnection) obj;
		stmt.setString(1, conn.getName());
		stmt.setString(2, conn.getHost());
		stmt.setString(3, conn.getService());
		stmt.setInt(4, conn.getType());
		stmt.setString(5, conn.getDescription());
	}

	protected final DomainObject doLoad(String id, ResultSet result)
			throws SQLException {
		Builder conBuilder = new PaloConnectionImpl.Builder(id);
		conBuilder.name(result.getString(2));	
		conBuilder.host(result.getString(3));
		conBuilder.service(result.getString(4));
		conBuilder.type(result.getInt(5));
		conBuilder.description(result.getString(6));
		return conBuilder.build();
	}

	protected final void deleteAssociations(DomainObject obj)
			throws SQLException {
		// delete from accounts
		PaloConnection conn = (PaloConnection) obj;
		MapperRegistry.getInstance().getAccountManagement().delete(conn);
	}

	protected final String deleteStatement() {
		return DELETE_STMT;
	}

	protected final String findStatement() {
		return FIND_BY_ID_STMT;
	}
	
	protected final String findByNameStatement() {
		return FIND_BY_NAME_STMT;
	}

	protected final String insertStatement() {
		return INSERT_STMT;
	}
	
	protected final String createTableStatement() {
		return CREATE_TABLE_STMT;
	}

	protected final String getTableName() {
		return TABLE;
	}

}
