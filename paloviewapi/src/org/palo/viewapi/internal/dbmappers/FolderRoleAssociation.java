/*
*
* @file FolderRoleAssociation.java
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
* @version $Id: FolderRoleAssociation.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
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
import java.util.HashSet;
import java.util.List;

import org.palo.viewapi.DomainObject;
import org.palo.viewapi.Role;
import org.palo.viewapi.View;
import org.palo.viewapi.internal.DbService;
import org.palo.viewapi.internal.ExplorerTreeNode;
import org.palo.viewapi.internal.IFolderRoleManagement;
import org.palo.viewapi.internal.IViewRoleManagement;
import org.palo.viewapi.services.ServiceProvider;


/**
 * <code>ViewRoleAssociation</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: FolderRoleAssociation.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
final class FolderRoleAssociation extends AssociationTableMapper implements IFolderRoleManagement {

	//the sql query constants:
	private static final String TABLE = DbService.getQuery("FoldersRolesAssociation.tableName");
	private static final String COLUMNS = DbService.getQuery("FoldersRolesAssociation.columns");
	private static final String CREATE_TABLE_STMT = DbService.getQuery("FoldersRolesAssociation.createTable", TABLE);
	private static final String INSERT_STMT = DbService.getQuery("FoldersRolesAssociation.insert", TABLE);
	private static final String FIND_BY_FOLDER_STMT = DbService.getQuery("FoldersRolesAssociation.findByFolder", COLUMNS, TABLE);
	private static final String FIND_BY_ROLE_STMT = DbService.getQuery("FoldersRolesAssociation.findByRole", COLUMNS, TABLE);
	private static final String UPDATE_STMT = DbService.getQuery("FoldersRolesAssociation.update", TABLE);
	private static final String DELETE_STMT = DbService.getQuery("FoldersRolesAssociation.delete", TABLE);
	private static final String DELETE_ROLE_STMT = DbService.getQuery("FoldersRolesAssociation.deleteRole", TABLE);
	private static final String DELETE_FOLDER_STMT = DbService.getQuery("FoldersRolesAssociation.deleteFolder", TABLE);

	
	public final List<String> getRoles(ExplorerTreeNode node) throws SQLException {
		return getRoles(node.getId());
	}
	
	public final List<String> getRoles(String nodeId) throws SQLException {
		Connection connection = DbService.getConnection();
		PreparedStatement stmt = null;
		ResultSet results = null;
		List<String> roles = new ArrayList<String>();
		try {
			stmt = connection.prepareStatement(FIND_BY_FOLDER_STMT);
			stmt.setString(1,nodeId);
			results = stmt.executeQuery();
			while(results.next()) {
				String id = results.getString(3);
				if(id != null && !roles.contains(id))
					roles.add(id);
			}
		} finally {
			cleanUp(stmt, results);
		}
		return roles;
	}

	public final List<String> getFolders(Role role) throws SQLException {
		Connection connection = DbService.getConnection();
		PreparedStatement stmt = null;
		ResultSet results = null;
		List<String> folders = new ArrayList<String>();
		try {
			stmt = connection.prepareStatement(FIND_BY_ROLE_STMT);
			stmt.setString(1,role.getId());
			results = stmt.executeQuery();
			while(results.next()) {
				String id = results.getString(2);
				if(id != null && !folders.contains(id)) {
					folders.add(id);
				}
			}
		} finally {
			cleanUp(stmt, results);
		}
		return folders;

	}

	public final void delete(Role role) throws SQLException  {
		delete(role, DELETE_ROLE_STMT);
	}

	public final void delete(ExplorerTreeNode node) throws SQLException {
		delete(node, DELETE_FOLDER_STMT);
	}

	protected final String deleteStatement() {
		return DELETE_STMT;
	}

	protected final String insertStatement() {
		return INSERT_STMT;
	}

	protected final String updateStatement() {
		return UPDATE_STMT;
	}
	
	protected final String createTableStatement() {
		return CREATE_TABLE_STMT;
	}
	
	protected final String getTableName() {
		return TABLE;
	}	
}
