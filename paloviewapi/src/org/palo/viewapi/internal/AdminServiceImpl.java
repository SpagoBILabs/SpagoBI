/*
*
* @file AdminServiceImpl.java
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
* @version $Id: AdminServiceImpl.java,v 1.27 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Group;
import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.services.AdministrationService;

/**
 * <code>AdminService</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AdminServiceImpl.java,v 1.27 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public final class AdminServiceImpl extends InternalService implements AdministrationService {
		
	AdminServiceImpl(AuthUser user) {		
		super(user);
		//TODO check here if user is admin or has at least admin rights!!
	}

	
	//USERS
	public final User getUser(String id) {
		AccessController.checkPermission(Right.READ, user);
		try {
			IUserManagement usrMgmt = getUserManagement();
			usrMgmt.reset();
			return (User)usrMgmt.find(id);
		} catch (SQLException e) { /* ignore */
		}
		return null;
	}
	
	public final List<User> getUsers() {
		AccessController.checkPermission(Right.READ, user);
		try {
			IUserManagement usrMgmt = getUserManagement();
			usrMgmt.reset();
			return usrMgmt.findAll();
		} catch (SQLException e) { /* ignore */
		}
		return new ArrayList<User>();
	}
	public final User createUser(String firstname, String lastname,
			String login, String password) throws OperationFailedException {
		try {
			IUserManagement usrMgmt = getUserManagement();
			UserImpl usr = new UserImpl((String)null);
			password = UserImpl.encrypt(password);
			usr.setFirstname(firstname);
			usr.setLastname(lastname);
			usr.setLoginName(login);
			usr.setPassword(password);
			// usr.setAuthentication(new Authentication(connection));
			usrMgmt.insert(usr);
			return usr;
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to create user", e);
		}
	}

	//	protected final void doReset() {		
//		getAccountManagement().reset();
//		getConnectionManagement().reset();
//		getGroupManagement().reset();
//		getReportManagement().reset();
//		getRoleManagement().reset();
//		getUserManagement().reset();
//		getViewManagement().reset();
//	}
	public final void delete(User user) throws OperationFailedException {
		try {
			IUserManagement usrMgmt = getUserManagement();
			usrMgmt.delete(user);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new OperationFailedException("Failed to delete user", e);
		}
	}
	public final void save(User user) throws OperationFailedException {
		try {
			// as update
			IUserManagement usrMgmt = getUserManagement();
			usrMgmt.update(user);
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to save user", e);
		}
	}
	public void setFirstname(String firstname, User ofUser) {
		UserImpl user = (UserImpl) ofUser;
		user.setFirstname(firstname);
	}
	public void setLastname(String lastname, User ofUser) {
		UserImpl user = (UserImpl)ofUser;
		user.setLastname(lastname);
	}
	public void setLoginName(String name, User ofUser) {
		UserImpl user = (UserImpl) ofUser;
		user.setLoginName(name);
	}
	public void setPassword(String password, User ofUser) {
		UserImpl user = (UserImpl) ofUser;
//		password = UserImpl.encrypt(password);
		user.setPassword(password);
	}



	//GROUPS
	public final Group getGroup(String id) {
		AccessController.checkPermission(Right.READ, user);
		try {
			IGroupManagement grpMgmt = getGroupManagement();
			return (Group)grpMgmt.find(id);
		} catch (SQLException e) { /* ignore */
		}
		return null;
	}
	public final List<Group> getGroups() {
		AccessController.checkPermission(Right.READ, user);
		try {
			IGroupManagement grpMgmt = getGroupManagement();
			return grpMgmt.findAll();
		} catch (SQLException e) { /* ignore */
		}
		return new ArrayList<Group>();
	}
	public final List<Group> getGroups(User forUser) {
		AccessController.checkPermission(Right.READ, user);
		try {
			IGroupManagement grpMgmt = getGroupManagement();
			IUserGroupManagement ugAssoc = mapperReg.getUserGroupAssociation();
			List<String> groupIDs = ugAssoc.getGroups(forUser);
			List<Group> groups = new ArrayList<Group>();
			for(String id : groupIDs) {
				Group group = (Group)grpMgmt.find(id);
				if(group != null)
					groups.add(group);
			}
			return groups;
		} catch (SQLException e) { /* ignore */
		}
		return new ArrayList<Group>();
	}
	public final Group createGroup(String name) throws OperationFailedException {
		try {
			IGroupManagement grpMgmt = getGroupManagement();
			GroupImpl group = new GroupImpl(null);
			group.setName(name);
			grpMgmt.insert(group);
			return group;
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to create group", e);
		}
	}
	public final void delete(Group group) throws OperationFailedException {
		IUserManagement usrMgmt = getUserManagement();
		IGroupManagement grpMgmt = getGroupManagement();
		try {
			grpMgmt.delete(group);
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to delete group", e);
		}
		// have to delete group from users and roles too:
		GroupImpl _group = (GroupImpl) group;
		List<User> users = _group.getUsers();
		for (User user : users) {
			try {
				UserImpl usr = (UserImpl) usrMgmt.find(user.getId());
				if (usr != null)
					usr.remove(group);
			} catch (SQLException e) { /* ignore */
			}
		}
		List<Role> roles = group.getRoles();
		for (Role role : roles)
			remove(role, group);
	}
	public final void save(Group group) throws OperationFailedException {
		try {
			// as update
			IGroupManagement grpMgmt = getGroupManagement();
			grpMgmt.update(group);
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to save group", e);
		}
	}
	public void setDescription(String description, Group ofGroup) {
		GroupImpl group = (GroupImpl) ofGroup;
		group.setDescription(description);
	}
	public void setName(String name, Group ofGroup) {
		GroupImpl group = (GroupImpl) ofGroup;
		group.setName(name);
	}

 
	//ROLES
	public final Role getRoleByName(String name) {
		AccessController.checkPermission(Right.READ, user);
		try {
			IRoleManagement roleMgmt = getRoleManagement();
			return (Role) roleMgmt.findByName(name);
		} catch (SQLException e) { /* ignore */
		}
		return null;
	}
	public final Role getRole(String id) {
		AccessController.checkPermission(Right.READ, user);
		try {
			IRoleManagement roleMgmt = getRoleManagement();
			return (Role)roleMgmt.find(id);
		} catch (SQLException e) { /* ignore */
		}
		return null;
	}
	public final List<Role> getRoles() {
		AccessController.checkPermission(Right.READ, user);
		try {
			IRoleManagement roleMgmt = getRoleManagement();
			return roleMgmt.findAll();
		} catch (SQLException e) { /* ignore */
		}
		return new ArrayList<Role>();
	}
	public final List<Role> getRoles(User forUser) {
		AccessController.checkPermission(Right.READ, user);
		try {
			IRoleManagement roleMgmt = getRoleManagement();
			IUserRoleManagement urAssoc = mapperReg.getUserRoleAssociation();
			List<String> roleIDs = urAssoc.getRoles(forUser);
			List<Role> roles = new ArrayList<Role>();
			for(String id : roleIDs) {
				Role role = (Role)roleMgmt.find(id);
				if(role != null)
					roles.add(role);
			}
			return roles;
		} catch (SQLException e) { /* ignore */
		}
		return new ArrayList<Role>();
	}
	public final Role createRole(String name) throws OperationFailedException {
		return createRole(name, Right.NONE);
	}
	public final Role createRole(String name, Right right)
			throws OperationFailedException {
		try {
			IRoleManagement roleMgmt = getRoleManagement();
			RoleImpl role = new RoleImpl(null);
			role.setName(name);
			role.setPermission(right);
			roleMgmt.insert(role);
			return role;
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to create role", e);
		}
	}
	public final void delete(Role role) throws OperationFailedException {
		IUserManagement usrMgmt = getUserManagement();
		IRoleManagement roleMgmt = getRoleManagement();
		IGroupManagement groupMgmt = getGroupManagement();

		try {
			roleMgmt.delete(role);
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to delete role", e);
		}
		try {
			// have to delete from groups and users too:
			List<Group> groups = groupMgmt.findAllGroupsFor(role);
			for (Group group : groups)
				remove(role, group);
		} catch (SQLException e) {
			throw new OperationFailedException(
					"Failed to delete role from groups", e);
		}
		try {
			List<User> users = usrMgmt.findAllUsersFor(role);
			for (User user : users)
				remove(role, user);
		} catch (SQLException e) {
			throw new OperationFailedException(
					"Failed to delete role from users", e);
		}
	}	
	
	public final void save(Role role) throws OperationFailedException {
		try {
			// as update
			IRoleManagement roleMgmt = getRoleManagement();
			roleMgmt.update(role);
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to delete role", e);
		}
	}
	public void setDescription(String description, Role ofRole) {
		RoleImpl role = (RoleImpl)ofRole;
		role.setDescription(description);
	}
	public void setName(String name, Role ofRole) {
		RoleImpl role = (RoleImpl)ofRole;
		role.setName(name);
	}
	public void setPermission(Right right, Role ofRole) {
		RoleImpl role = (RoleImpl)ofRole;
		role.setPermission(right);
	}

	
	//CONNECTIONS
	public final PaloConnection getConnection(String id) {
		AccessController.checkPermission(Right.READ, user);
		try {
			IConnectionManagement connMgmt = getConnectionManagement();
			return (PaloConnection)connMgmt.find(id);
		} catch (SQLException e) { /* ignore */
		}
		return null;
		
	}
	public final List<PaloConnection> getConnections() {
		AccessController.checkPermission(Right.READ, user);
		try {
			IConnectionManagement connMgmt = getConnectionManagement();
			return connMgmt.findAll();
		} catch (SQLException e) { /* ignore */
		}
		return new ArrayList<PaloConnection>();
	}
	public final PaloConnection createConnection(String name, String host,
			String service, int type) throws OperationFailedException {
		try {
			IConnectionManagement connMgmt = getConnectionManagement();
			PaloConnectionImpl connection = new PaloConnectionImpl(null);
			connection.setName(name);
			connection.setHost(host);
			connection.setService(service);
			connection.setType(type);
			connMgmt.insert(connection);
			return connection;
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to create connection", e);
		}
	}
	public final void delete(PaloConnection connection)
			throws OperationFailedException {
		try {
			IConnectionManagement connMgmt = getConnectionManagement();
			connMgmt.delete(connection);
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to delete connection", e);
		}
	}
	public final void save(PaloConnection connection)
			throws OperationFailedException {
		try {
			// as update
			IConnectionManagement connMgmt = getConnectionManagement();
			connMgmt.update(connection);
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to save connection", e);
		}
	}
	public void setDescription(String description, PaloConnection ofConnection) {
		PaloConnectionImpl connection = (PaloConnectionImpl)ofConnection;
		connection.setDescription(description);
	}
	public void setName(String name, PaloConnection ofConnection) {
		PaloConnectionImpl connection = (PaloConnectionImpl)ofConnection;
		connection.setName(name);
	}
	public void setHost(String host, PaloConnection ofConnection) {
		PaloConnectionImpl connection = (PaloConnectionImpl)ofConnection;
		connection.setHost(host);
	}
	public void setService(String service, PaloConnection ofConnection) {
		PaloConnectionImpl connection = (PaloConnectionImpl)ofConnection;
		connection.setService(service);
	}

	public void setType(int type, PaloConnection ofConnection) {
		PaloConnectionImpl connection = (PaloConnectionImpl)ofConnection;
		connection.setType(type);
	}

	//ACCOUNTS
	public final Account getAccount(String id) {
		AccessController.checkPermission(Right.READ, user);
		try {
			IAccountManagement accountMgmt = getAccountManagement();
			return (Account)accountMgmt.find(id);
		} catch (SQLException e) { /* ignore */
		}
		return null;
	}
	public final List<Account> getAccounts() {
		AccessController.checkPermission(Right.READ, user);
		try {
			IAccountManagement accountMgmt = getAccountManagement();
			return accountMgmt.findAll();
		} catch (SQLException e) { /* ignore */
		}
		return new ArrayList<Account>();
	}
	
	public final List<Account> getAccounts(User forUser) {
		AccessController.checkPermission(Right.READ, user);
		try {
			IAccountManagement accountMgmt = getAccountManagement();
			return accountMgmt.getAccounts(forUser);
		} catch (SQLException e) { /* ignore */
		}
		return new ArrayList<Account>();
	}

	public final Account createAccount(String name, String password,
			User forUser, PaloConnection connection)
			throws OperationFailedException {
		try {
			UserImpl user = (UserImpl) forUser;
			IAccountManagement accountMgmt = getAccountManagement();
			AccountImpl account;
			if (connection.getType() == PaloConnection.TYPE_WSS) {
				account = new WSSAccountImpl(null, forUser.getId());
			} else {
				account = new PaloAccountImpl(null, forUser.getId());
			}
			account.setConnection(connection);
			account.setLoginName(name);
			account.setPassword(password);
			accountMgmt.insert(account);
			// internal add to user:
			user.add(account);
			return account;
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to create account", e);
		}
	}
	public final void delete(Account account) throws OperationFailedException {
		try {
			// internal remove from user
			UserImpl user = (UserImpl) account.getUser();
			user.remove(account);
			IAccountManagement accountMgmt = getAccountManagement();
			accountMgmt.delete(account);
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to delete account", e);
		}
	}
	public final void save(Account account) throws OperationFailedException {
		if(account == null)
			return;
		try {
			// as update
			IAccountManagement accountMgmt = getAccountManagement();
			accountMgmt.update(account);
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to save account", e);
		}
	}
	public void setConnection(PaloConnection connection, Account ofAccount) {
		AccountImpl account = (AccountImpl) ofAccount;
		account.setConnection(connection);
	}
	public void setLoginName(String name, Account ofAccount) {
		AccountImpl account = (AccountImpl) ofAccount;
		account.setLoginName(name);
	}
	public void setPassword(String password, Account ofAccount) {
		AccountImpl account = (AccountImpl) ofAccount;
		account.setPassword(password);
	}

	public void setUser(User user, Account ofAccount) {
		AccountImpl account = (AccountImpl) ofAccount;
		account.setUser(user);
	}
	
	public final void add(User user, Group toGroup)
			throws OperationFailedException {
		UserImpl usr = (UserImpl) user;
		usr.add(toGroup);
		// check if group doesn't have this association already...
		if (!toGroup.hasMember(user)) {
			try {
				GroupImpl group = (GroupImpl) toGroup;
				// add to association table:
				IUserGroupManagement ugAssoc = mapperReg
						.getUserGroupAssociation();
				ugAssoc.insert(user, group);
				// add to domain objects:
				group.add(user);
			} catch (SQLException e) {
				// recover and throw exception:
				usr.remove(toGroup);
				throw new OperationFailedException("Failed to add user '"
						+ user.getLastname() + "' to group '"
						+ toGroup.getName() + "'!", e);
			}
		}
	}
	
	public void add(Role role, User toUser) throws OperationFailedException {
		UserImpl usr = (UserImpl) toUser;
		usr.add(role);
	}

	public void add(Role role, Group toGroup) throws OperationFailedException {
		GroupImpl _group = (GroupImpl) toGroup;
		_group.add(role);
	}

	public final void remove(User user, Group fromGroup)
			throws OperationFailedException {
		UserImpl usr = (UserImpl) user;
		usr.remove(fromGroup);
		// check if role doesn't have this association already...
		if (fromGroup.hasMember(user)) {
			try {
				GroupImpl group = (GroupImpl) fromGroup;
				// delete from association table:
				IUserGroupManagement ugAssoc = mapperReg
						.getUserGroupAssociation();
				ugAssoc.delete(user, group);
				group.remove(user);
			} catch (SQLException e) {
				// recover and throw exception:
				usr.add(fromGroup);
				throw new OperationFailedException("Failed to remove user '"
						+ user.getLastname() + "' from group '"
						+ fromGroup.getName() + "'!", e);
			}
		}
	}


	public void remove(Role role, User fromUser)
			throws OperationFailedException {
		UserImpl usr = (UserImpl) fromUser;
		usr.remove(role);
	}


	public void remove(Role role, Group fromGroup)
			throws OperationFailedException {
		GroupImpl _group = (GroupImpl) fromGroup;
		_group.remove(role);
	}

	public final void setRoles(List<Role> roles, Group ofGroup)
			throws OperationFailedException {
		GroupImpl _group = (GroupImpl) ofGroup;
		_group.setRoles(roles);
	}

	public final void setUsers(List<User> users, Group ofGroup)
			throws OperationFailedException {
		// TODO speed this up:
		// remove all old users:
		for (User user : ofGroup.getUsers())
			remove(user, ofGroup);
		// add new users:
		for (User user : users)
			add(user, ofGroup);
	}

	public final void setGroups(List<Group> groups, User user)
			throws OperationFailedException {
		UserImpl _user = (UserImpl) user;
		// TODO speed this up:
		// remove all old groups:
		IGroupManagement grpMgmt = getGroupManagement();
		for (String groupId : _user.getGroupIDs()) {
			try {
				Group group = (Group) grpMgmt.find(groupId);
				if (group != null)
					remove(user, group);
			} catch (SQLException e) { /* ignore */
			}
		}
		// add new groups:
		for (Group group : groups)
			add(user, group);
	}


	public void setRoles(List<Role> roles, User user)
			throws OperationFailedException {
		UserImpl _user = (UserImpl) user;
		_user.setRoles(roles);
	}
}
