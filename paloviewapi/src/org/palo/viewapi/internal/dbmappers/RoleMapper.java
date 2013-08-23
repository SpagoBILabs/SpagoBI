/*
*
* @file RoleMapper.java
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
* @version $Id: RoleMapper.java,v 1.7 2009/12/17 16:14:08 PhilippBouillon Exp $
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
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.internal.DbService;
import org.palo.viewapi.internal.IGroupManagement;
import org.palo.viewapi.internal.IGroupRoleManagement;
import org.palo.viewapi.internal.IReportRoleManagement;
import org.palo.viewapi.internal.IRoleManagement;
import org.palo.viewapi.internal.IUserManagement;
import org.palo.viewapi.internal.IUserRoleManagement;
import org.palo.viewapi.internal.IViewRoleManagement;
import org.palo.viewapi.internal.RoleImpl;
import org.palo.viewapi.internal.RoleImpl.Builder;


/**
 * <code>RoleMapper</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: RoleMapper.java,v 1.7 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
final class RoleMapper extends AbstractMapper implements IRoleManagement {
	
	private static final String TABLE = DbService.getQuery("Roles.tableName");
	private static final String COLUMNS = DbService.getQuery("Roles.columns");
	private static final String CREATE_TABLE_STMT = DbService.getQuery("Roles.createTable", TABLE);
	private static final String FIND_ALL_STMT = DbService.getQuery("Roles.findAll", COLUMNS, TABLE);
	private static final String FIND_BY_ID_STMT = DbService.getQuery("Roles.findById", COLUMNS, TABLE);
	private static final String FIND_BY_NAME_STMT = DbService.getQuery("Roles.findByName", COLUMNS, TABLE);
	private static final String FIND_BY_USER_STMT = DbService.getQuery("Roles.findByUser", COLUMNS, TABLE);
	private static final String INSERT_STMT = DbService.getQuery("Roles.insert", TABLE);
	private static final String UPDATE_STMT = DbService.getQuery("Roles.update", TABLE);
	private static final String DELETE_STMT = DbService.getQuery("Roles.delete", TABLE);


	public final List<Role> findAll() throws SQLException {
		PreparedStatement stmt = null;
		ResultSet results = null;
		List<Role> roles = new ArrayList<Role>();
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(FIND_ALL_STMT);
			results = stmt.executeQuery();
			while(results.next()) { 
				Role role = (Role)load(results);
				if(role != null)
					roles.add(role);
			}
		} finally {
			cleanUp(stmt, results);
		}
		return roles;
	}

	public final void update(DomainObject obj) throws SQLException {
		Role role = (Role) obj;
		PreparedStatement stmt = null;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(UPDATE_STMT);
			stmt.setString(1, role.getName());
			stmt.setString(2, role.getDescription());			
			stmt.setString(3, role.getPermission().toString());
			stmt.setString(4, role.getId());
			stmt.execute();
//			handleAssociations(role);
		} finally {
			cleanUp(stmt);
		}
	}

	protected final void doInsert(DomainObject obj, PreparedStatement stmt)
			throws SQLException {
		Role role = (Role) obj;
		stmt.setString(1, role.getName());
		stmt.setString(2, role.getDescription());
		stmt.setString(3, role.getPermission().toString());		
//		handleAssociations(role);
	}

	protected final DomainObject doLoad(String id, ResultSet result)
			throws SQLException {
		Builder roleBuilder = new RoleImpl.Builder(id);
		roleBuilder = roleBuilder.name(result.getString(2)).description(
				result.getString(3)).permission(
				Right.fromString(result.getString(4)));

//		Role role = new Role(id, connection);
//		role.setName(result.getString(2));
//		role.setDescription(result.getString(3));
//		role.setPermission(Right.fromString(result.getString(4))); //TODO catch clause to handle if right string is invalid!
		
//		//LOAD ASSOCIATIONS:
//		MapperRegistry mapperReg = MapperRegistry.getInstance();
//		//groups
//		IGroupRoleManagement grAssoc = mapperReg.getGroupRoleAssociation();
//		roleBuilder.groups(grAssoc.getGroups(id)); //role));
//		//users
//		IUserRoleManagement urAssoc = mapperReg.getUserRoleAssociation();
//		roleBuilder.users(urAssoc.getUsers(id)); //role));

		return roleBuilder.build();
	}

	protected final void deleteAssociations(DomainObject obj) throws SQLException {
		Role role = (Role) obj;				
		//REMOVE ASSOCIATIONS:
		MapperRegistry mapperReg = MapperRegistry.getInstance();
		//group-role
		IGroupRoleManagement grAssoc = mapperReg.getGroupRoleAssociation();
		grAssoc.delete(role);
		//report-role
		IReportRoleManagement rrAssoc = mapperReg.getReportRoleAssociation();
		rrAssoc.delete(role);
		//user-role
		IUserRoleManagement urAssoc = mapperReg.getUserRoleAssociation();
		urAssoc.delete(role);
		//view-role
		IViewRoleManagement vrAssoc = mapperReg.getViewRoleAssociation();
		vrAssoc.delete(role);
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


//	private final void handleAssociations(Role role) throws SQLException {
//		if(role.getId() == null)
//			return; 	//role is about to be created!!
//		//HANDLE ASSOCIATIONS:
//		MapperRegistry mapperReg = MapperRegistry.getInstance();
//		//insert associated groups
//		IGroupManagement groupMgmt = mapperReg.getGroupManagement();
//		IGroupRoleManagement grAssoc = mapperReg.getGroupRoleAssociation();
//		List<String> groups = ((RoleImpl)role).getGroupIDs();	
//		List<String> savedGroups = grAssoc.getGroups(role);		
//		for(String id : groups) {
//			if(!savedGroups.contains(id))
//				grAssoc.insert(role, groupMgmt.find(id));
//		}
//		//remove any deleted ones:
//		savedGroups.removeAll(groups);		
//		for(String id : savedGroups)
//			grAssoc.delete(role, groupMgmt.find(id));
//
//		
//		//insert associated users
//		IUserManagement usrMgmt = mapperReg.getUserManagement();
//		IUserRoleManagement urAssoc = mapperReg.getUserRoleAssociation();
//		List<String> users = ((RoleImpl)role).getUserIDs();
//		List<String> savedUsers = urAssoc.getUsers(role);
//		for(String id : users) {
//			if(!savedUsers.contains(id))
//				urAssoc.insert(role, usrMgmt.find(id));
//		}
//		//remove any deleted ones:
//		savedUsers.removeAll(users);		
//		for(String id : savedUsers)
//			urAssoc.delete(role, usrMgmt.find(id));
//
//	}
}
