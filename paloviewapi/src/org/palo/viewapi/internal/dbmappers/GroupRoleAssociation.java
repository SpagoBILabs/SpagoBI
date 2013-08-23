/*
*
* @file GroupRoleAssociation.java
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
* @version $Id: GroupRoleAssociation.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
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

import org.palo.viewapi.Group;
import org.palo.viewapi.Role;
import org.palo.viewapi.internal.DbService;
import org.palo.viewapi.internal.IGroupRoleManagement;


/**
 * <code>GroupRoleAssociation</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: GroupRoleAssociation.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
final class GroupRoleAssociation extends AssociationTableMapper implements IGroupRoleManagement {

	private static final String TABLE = DbService.getQuery("GroupsRolesAssociation.tableName");
	private static final String COLUMNS = DbService.getQuery("GroupsRolesAssociation.columns");
	private static final String CREATE_TABLE_STMT = DbService.getQuery("GroupsRolesAssociation.createTable", TABLE);
	private static final String INSERT_STMT = DbService.getQuery("GroupsRolesAssociation.insert", TABLE);
	private static final String FIND_BY_GROUP_STMT = DbService.getQuery("GroupsRolesAssociation.findByGroup", COLUMNS, TABLE);
	private static final String FIND_BY_ROLE_STMT = DbService.getQuery("GroupsRolesAssociation.findByRole", COLUMNS, TABLE);
	private static final String UPDATE_STMT = DbService.getQuery("GroupsRolesAssociation.update", TABLE);
	private static final String DELETE_STMT = DbService.getQuery("GroupsRolesAssociation.delete", TABLE);	
	private static final String DELETE_GROUP_STMT = DbService.getQuery("GroupsRolesAssociation.deleteGroup", TABLE);
	private static final String DELETE_ROLE_STMT = DbService.getQuery("GroupsRolesAssociation.deleteRole", TABLE);

	

	public final List<String> getGroups(Role role) throws SQLException {
		return getGroups(role.getId());
	}
	public final List<String> getGroups(String roleId) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet results = null;
		List<String> groups = new ArrayList<String>();
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(FIND_BY_ROLE_STMT);
			stmt.setString(1,roleId);
			results = stmt.executeQuery();
			while(results.next()) { 
				String id = results.getString(2);
				if(id != null && !groups.contains(id))
					groups.add(id);
			}
		} finally {
			cleanUp(stmt);
		}
		return groups;
	}

	public final List<String> getRoles(Group group) throws SQLException {
		return getRoles(group.getId());
	}
	public final List<String> getRoles(String groupId) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet results = null;
		List<String> roles = new ArrayList<String>();
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(FIND_BY_GROUP_STMT);
			stmt.setString(1,groupId);
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

	/** deletes the given group and all of its association from this table */
	public final void delete(Group group) throws SQLException {
		delete(group, DELETE_GROUP_STMT);
	}
	
	/** deletes the given role and all of its association from this table */
	public final void delete(Role role) throws SQLException {
		delete(role, DELETE_ROLE_STMT);
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
	
//	private final IRole loadRole(ResultSet result) throws SQLException {
//		MapperRegistry mapperReg = MapperRegistry.getInstance(connection);
//		return (IRole)mapperReg.getRoleManagement().find(result.getString(3));
//	}
//
//	private final IGroup loadGroup(ResultSet result) throws SQLException {
//		MapperRegistry mapperReg = MapperRegistry.getInstance(connection);
//		return (IGroup)mapperReg.getGroupManagement().find(result.getString(3));
//	}
}
