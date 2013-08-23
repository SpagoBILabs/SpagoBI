/*
*
* @file UserMapper.java
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
* @version $Id: UserMapper.java,v 1.13 2009/12/17 16:14:08 PhilippBouillon Exp $
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

import org.palo.viewapi.Account;
import org.palo.viewapi.DomainObject;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.internal.DbService;
import org.palo.viewapi.internal.ExplorerTreeNode;
import org.palo.viewapi.internal.IAccountManagement;
import org.palo.viewapi.internal.IFolderManagement;
import org.palo.viewapi.internal.IGroupManagement;
import org.palo.viewapi.internal.IRoleManagement;
import org.palo.viewapi.internal.IUserGroupManagement;
import org.palo.viewapi.internal.IUserManagement;
import org.palo.viewapi.internal.IUserRoleManagement;
import org.palo.viewapi.internal.UserImpl;
import org.palo.viewapi.internal.UserImpl.Builder;

/**
 * <code>UserMapper</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: UserMapper.java,v 1.13 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
final class UserMapper extends AbstractMapper implements IUserManagement {

//	Users.findByCompleteName = SELECT ''{0}'' FROM ''{1}'' WHERE FIRSTNAME = ? AND LASTNAME = ?
	private static final String TABLE = DbService.getQuery("Users.tableName");
	private static final String COLUMNS = DbService.getQuery("Users.columns");
	private static final String CREATE_TABLE_STMT = DbService.getQuery("Users.createTable", TABLE);
	private static final String FIND_ALL_STMT = DbService.getQuery("Users.findAll", COLUMNS, TABLE);
	private static final String FIND_BY_ID_STMT = DbService.getQuery("Users.findById", COLUMNS, TABLE);
	private static final String FIND_BY_LOGIN_STMT = DbService.getQuery("Users.findByLogin", COLUMNS, TABLE);
	private static final String INSERT_STMT = DbService.getQuery("Users.insert", TABLE);
	private static final String UPDATE_STMT = DbService.getQuery("Users.update", TABLE);
	private static final String DELETE_STMT = DbService.getQuery("Users.delete", TABLE);
	
	
	public final List<User> findAll() throws SQLException {
		PreparedStatement stmt = null;
		ResultSet results = null;
		List<User> users = new ArrayList<User>();
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(FIND_ALL_STMT);
			results = stmt.executeQuery();
			while(results.next()) { 
				User user = (User)load(results);
				if(user != null)
					users.add(user);
			}
		} finally {
			cleanUp(stmt, results);
		}
		return users;
	}
	
	public final void update(DomainObject obj) throws SQLException {
		User user = (User) obj;
		PreparedStatement stmt = null;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(UPDATE_STMT);
			stmt.setString(1, user.getFirstname());
			stmt.setString(2, user.getLastname());
			stmt.setString(3, user.getLoginName());			
			stmt.setString(4, user.getPassword());
			stmt.setString(5, user.getId());
			stmt.execute();
			
			handleAssociations(user);
		} finally {
			cleanUp(stmt);
		}
	}

	protected DomainObject doLoad(String id, ResultSet rs) throws SQLException {
		Builder usrBuilder = new UserImpl.Builder(id);
		usrBuilder.firstname(rs.getString(2));
		usrBuilder.lastname(rs.getString(3));
		usrBuilder.login(rs.getString(4));
		String pass = rs.getString(5);
		usrBuilder.password(pass);
		//LOAD ASSOCIATIONS:
		MapperRegistry mapperReg = MapperRegistry.getInstance();
		//groups
		IUserGroupManagement ugAssoc = mapperReg.getUserGroupAssociation();
		List<String> groups = ugAssoc.getGroups(id);
		usrBuilder.groups(groups);
		//roles
		IUserRoleManagement urAssoc = mapperReg.getUserRoleAssociation();
		List<String> roles = urAssoc.getRoles(id);
		usrBuilder.roles(roles);
		//accounts
		IAccountManagement accMgmt = mapperReg.getAccountManagement();
		List<Account> accounts = accMgmt.getAccounts(id);
		usrBuilder.accounts(accounts);

		return usrBuilder.build();
	}

	protected final void doInsert(DomainObject obj, PreparedStatement stmt)
			throws SQLException {
		User user = (User) obj;
		stmt.setString(1, user.getFirstname());
		stmt.setString(2, user.getLastname());
		stmt.setString(3, user.getLoginName());
		stmt.setString(4, user.getPassword());
		
		handleAssociations(user);
	}

	protected final void deleteAssociations(DomainObject obj) throws SQLException {
		User user = (User) obj;
		MapperRegistry mapperReg = MapperRegistry.getInstance();
		
		//delete associated groups
		IUserGroupManagement ugAssoc = mapperReg.getUserGroupAssociation();
		if (ugAssoc != null) {
			ugAssoc.delete(user);
		}
		
		//delete associated roles
		IUserRoleManagement urAssoc = mapperReg.getUserRoleAssociation();
		if (urAssoc != null) {
			urAssoc.delete(user);
		}
		
		//delete accounts
		IAccountManagement accMgmt = mapperReg.getAccountManagement();		
		List<Account> accounts = accMgmt.getAccounts(user);
		if (accounts != null) {
			for(Account account : accounts)
				accMgmt.delete(account);
		}		
	}

	protected final String deleteStatement() {
		return DELETE_STMT;
	}
	protected final String findStatement() {
		return FIND_BY_ID_STMT;
	}
	protected final String findByNameStatement() {
		return FIND_BY_LOGIN_STMT;
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

	private final void handleAssociations(User user) throws SQLException {
		if(user.getId() == null)
			return;
				
		UserImpl usr = (UserImpl) user;
		//HANDLE ASSOCIATIONS:
		MapperRegistry mapperReg = MapperRegistry.getInstance();
		//insert associated groups
		IGroupManagement groupMgmt = mapperReg.getGroupManagement();
		IUserGroupManagement ugAssoc = mapperReg.getUserGroupAssociation();		
		List<String> groups = usr.getGroupIDs();
		List<String> savedGroups = ugAssoc.getGroups(user);
		for(String id : groups) {
			if(!savedGroups.contains(id))
				ugAssoc.insert(user, groupMgmt.find(id));
		}
		//remove any deleted ones:
		savedGroups.removeAll(groups);
		for(String id : savedGroups)
			ugAssoc.delete(user, groupMgmt.find(id));

		
		//insert associated roles:
		IRoleManagement roleMgmt = mapperReg.getRoleManagement();
		IUserRoleManagement urAssoc = mapperReg.getUserRoleAssociation();
		List<String> roles = usr.getRoleIDs();
		List<String> savedRoles = urAssoc.getRoles(user);
		for(String id : roles) {
			if(!savedRoles.contains(id))
				urAssoc.insert(user, roleMgmt.find(id));
		}
		//remove any deleted ones:
		savedRoles.removeAll(roles);
		for(String id : savedRoles)
			urAssoc.delete(user, roleMgmt.find(id));
		
		
//		//insert associated accounts:
//		IAccountManagement accMgmt = mapperReg.getAccountManagement();
//		List<Account> accounts = usr.getAccounts();
//		List<Account> savedAccounts = accMgmt.getAccounts(user);		
//		for(Account account : accounts) {
//			if(!savedAccounts.contains(account))
//				accMgmt.insert(account);
//		}
//		//remove any deleted accounts:
//		savedAccounts.removeAll(accounts);
//		for(Account account : savedAccounts)
//			accMgmt.delete(account);
	}

	public List<User> findAllUsersFor(Role role) throws SQLException {
		MapperRegistry mapperReg = MapperRegistry.getInstance();
		IUserManagement usrMgmt = mapperReg.getUserManagement();
		IUserRoleManagement urAssoc = mapperReg.getUserRoleAssociation();
		List<String> userIDs = urAssoc.getUsers(role);
		List<User> users = new ArrayList<User>();
		for(String id : userIDs) {
			User user = (User)usrMgmt.find(id);
			if(user != null)
				users.add(user);
		}
		return users;
	}
}
