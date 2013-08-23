/*
*
* @file GroupMapper.java
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
* @version $Id: GroupMapper.java,v 1.7 2009/12/17 16:14:08 PhilippBouillon Exp $
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
import org.palo.viewapi.Group;
import org.palo.viewapi.Role;
import org.palo.viewapi.internal.DbService;
import org.palo.viewapi.internal.GroupImpl;
import org.palo.viewapi.internal.IGroupManagement;
import org.palo.viewapi.internal.IGroupRoleManagement;
import org.palo.viewapi.internal.IRoleManagement;
import org.palo.viewapi.internal.IUserGroupManagement;
import org.palo.viewapi.internal.IUserManagement;
import org.palo.viewapi.internal.GroupImpl.Builder;


/**
 * <code>GroupMapper</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: GroupMapper.java,v 1.7 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
final class GroupMapper extends AbstractMapper implements IGroupManagement {
	
	private static final String TABLE = DbService.getQuery("Groups.tableName");
	private static final String COLUMNS = DbService.getQuery("Groups.columns");
	private static final String CREATE_TABLE_STMT = DbService.getQuery("Groups.createTable", TABLE);
	private static final String FIND_ALL_STMT = DbService.getQuery("Groups.findAll", COLUMNS, TABLE);
	private static final String FIND_BY_ID_STMT = DbService.getQuery("Groups.findById", COLUMNS, TABLE);
	private static final String FIND_BY_NAME_STMT = DbService.getQuery("Groups.findByName", COLUMNS, TABLE);
//	private static final String FIND_BY_USER_STMT = DbService.getQuery("Groups.findByUser", COLUMNS, TABLE);
	private static final String INSERT_STMT = DbService.getQuery("Groups.insert", TABLE);
	private static final String UPDATE_STMT = DbService.getQuery("Groups.update", TABLE);
	private static final String DELETE_STMT = DbService.getQuery("Groups.delete", TABLE);


	public final List<Group> findAll() throws SQLException {
		PreparedStatement stmt = null;
		ResultSet results = null;
		List<Group> groups = new ArrayList<Group>();
		Connection connection = DbService.getConnection();		
		try {
			stmt = connection.prepareStatement(FIND_ALL_STMT);
			results = stmt.executeQuery();
			while(results.next()) { 
				Group group = (Group)load(results);
				if(group != null)
					groups.add(group);
			}
		} finally {
			cleanUp(stmt, results);
		}
		return groups;
	}
	
	public final void update(DomainObject obj) throws SQLException {
		Group group = (Group) obj;
		PreparedStatement stmt = null;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(UPDATE_STMT);
			stmt.setString(1, group.getName());
			stmt.setString(2, group.getDescription());
			stmt.setString(3, group.getId());
			stmt.execute();
			handleAssociations(group);
		} finally {
			cleanUp(stmt);
		}		
	}

	protected final void doInsert(DomainObject obj, PreparedStatement stmt)
			throws SQLException {
		Group group = (Group) obj;
		stmt.setString(1, group.getName());
		stmt.setString(2, group.getDescription());		
		handleAssociations(group);	
	}

	protected final DomainObject doLoad(String id, ResultSet result)
			throws SQLException {
		Builder groupBuilder = new GroupImpl.Builder(id);
		groupBuilder.name(result.getString(2));
		groupBuilder.description(result.getString(3));
		MapperRegistry mapperReg = MapperRegistry.getInstance();
		//load roles:
		IGroupRoleManagement grAssoc = mapperReg.getGroupRoleAssociation();
		groupBuilder.roles(grAssoc.getRoles(id));
		//load users:
		IUserGroupManagement ugAssoc = mapperReg.getUserGroupAssociation();
		groupBuilder.users(ugAssoc.getUsers(id));
		return groupBuilder.build();

//		Group group = new Group(id, connection);
//		group.setName(result.getString(2));
//		group.setDescription(result.getString(3));
//		MapperRegistry mapperReg = MapperRegistry.getInstance(connection);
//		//load roles:
//		IGroupRoleManagement grAssoc = mapperReg.getGroupRoleAssociation();
//		group.internalSetRoles(grAssoc.getRoles(group));
//		//load users:
//		IUserGroupManagement ugAssoc = mapperReg.getUserGroupAssociation();
//		group.internalSetUsers(ugAssoc.getUsers(group));
//		return group;
	}

	protected final void deleteAssociations(DomainObject obj) throws SQLException {
		Group group = (Group) obj;
		MapperRegistry mapperReg = MapperRegistry.getInstance();
		//remove associated users:
		IUserGroupManagement ugAssoc = mapperReg.getUserGroupAssociation();
		ugAssoc.delete(group);
		//remove associated roles:
		IGroupRoleManagement grAssoc = mapperReg.getGroupRoleAssociation();
		grAssoc.delete(group);
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


	private final void handleAssociations(Group group) throws SQLException {
		//HANDLE ASSOCIATIONS:
		MapperRegistry mapperReg = MapperRegistry.getInstance();
		// roles:
		IRoleManagement rolesMgmt = mapperReg.getRoleManagement();
		IGroupRoleManagement grAssoc = mapperReg.getGroupRoleAssociation();
		List<String> roles = ((GroupImpl)group).getRoleIDs();
		List<String> savedRoles = grAssoc.getRoles(group);
		for (String id : roles) {
			if (!savedRoles.contains(id))
				grAssoc.insert(group, rolesMgmt.find(id));
		}
		// remove any deleted ones:
		savedRoles.removeAll(roles);
		for (String id : savedRoles)
			grAssoc.delete(group, rolesMgmt.find(id));

		
		// users:
		IUserManagement usrMgmt = mapperReg.getUserManagement();
		IUserGroupManagement ugAssoc = mapperReg.getUserGroupAssociation();
		List<String> users = ((GroupImpl)group).getUserIDs();
		List<String> savedUsers = ugAssoc.getUsers(group);
		for (String id : users) {
			if (!savedUsers.contains(id))
				ugAssoc.insert(group, usrMgmt.find(id));
		}
		// remove any deleted ones:
		savedUsers.removeAll(users);
		for (String id : savedUsers)
			ugAssoc.delete(group, usrMgmt.find(id));
	}

	public List<Group> findAllGroupsFor(Role role) throws SQLException {
		MapperRegistry mapperReg = MapperRegistry.getInstance();
		IGroupManagement groupMgmt = mapperReg.getGroupManagement();
		IGroupRoleManagement grAssoc = mapperReg.getGroupRoleAssociation();
		List<String> groupIDs = grAssoc.getGroups(role);
		List<Group> groups = new ArrayList<Group>();
		for(String id : groupIDs) {
			Group group = (Group)groupMgmt.find(id);
			if(group != null)
				groups.add(group);
		}
		return groups;
	}
}
