/*
*
* @file Mapper.java
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
* @version $Id: Mapper.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.dbmappers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.palo.viewapi.internal.DbService;


/**
 * <code>Mapper</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: Mapper.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
abstract class Mapper {

//	protected final DbConnection connection;
	
	Mapper() { //DbConnection connection) {
//		this.connection = connection;
		createTable();
	}

	protected abstract String getTableName();
	protected abstract String createTableStatement();
	
	protected final void cleanUp(PreparedStatement stmt) {
		cleanUp(stmt,null);
	}
	protected final void cleanUp(PreparedStatement stmt, ResultSet rs) {
		try {
			if(stmt != null)
				stmt.close();
		}catch(SQLException e) { /* ignore */ }
		try {
			if(rs != null)
				rs.close();
		}catch(SQLException e) { /* ignore */ }
	}

	private final void createTable() {
		String table = getTableName();
		//first check if our table exists already:
		try {
			if(existsInMetadata(table))
				return;
		} catch (SQLException e) {
			// we fall back on simple select * from try:
			if (exists(table))
				return;
		}

		// if we get here we have to create the table...
		try {
			doCreateTable();
		} catch (SQLException ex) {
			throw new RuntimeException(
					"Cannot create required database table '" + table + "'!!",
					ex);
		}
	}
	
	private final void doCreateTable() throws SQLException {
		PreparedStatement stmt = null;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(createTableStatement());
			stmt.execute();
		} finally {
			cleanUp(stmt);
		}
	}

	private final boolean existsInMetadata(String table) throws SQLException {
		Connection connection = DbService.getConnection();
		DatabaseMetaData metaData = connection.getMetaData();
		ResultSet rs = metaData.getTables(null, null, "%", null);
		int colCount = rs.getMetaData().getColumnCount();
		while (rs.next()) {
			for (int i = 1; i <= colCount; i++) {
				String tbl = rs.getString(i);
				if (tbl != null && tbl.equalsIgnoreCase(table))
					return true;
			}
		}
		return false;
	}
	private final boolean exists(String table) {
		PreparedStatement stmt = null;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement("SELECT * FROM "+table);
			stmt.execute();
			return true;
		} catch(SQLException e) {
			/* ignore */
		} finally {
			cleanUp(stmt);
		}
		return false;
	}
}
