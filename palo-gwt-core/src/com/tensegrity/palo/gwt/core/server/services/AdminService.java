/*
*
* @file AdminService.java
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
* @version $Id: AdminService.java,v 1.18 2010/01/13 08:02:44 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services;

import java.util.ArrayList;
import java.util.List;

import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Group;
import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.internal.AuthUserImpl;
import org.palo.viewapi.services.AdministrationService;
import org.palo.viewapi.services.ServiceProvider;
import org.palo.viewapi.services.ViewService;

import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
import com.tensegrity.palo.gwt.core.client.models.admin.XGroup;
import com.tensegrity.palo.gwt.core.client.models.admin.XRole;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.server.converter.XConverter;

/**
 * <code>AdminService</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AdminService.java,v 1.18 2010/01/13 08:02:44 PhilippBouillon Exp $
 **/
public class AdminService extends BasePaloServiceServlet {

	/** generated serial number */
	private static final long serialVersionUID = 8619431942621284395L;

	//TODO ROLE support add,remove groups and users
	//TODO GROUP support add,remove roles and users

	
	public final XAccount[] getAccounts(String sessionId) throws SessionExpiredException {
		AuthUser admin = getLoggedInUser(sessionId);
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(admin);
		List<XAccount> xAccounts = new ArrayList<XAccount>();
		for(Account account : adminService.getAccounts()) {			
			XAccount xAccount = (XAccount)XConverter.createX(account);
			xAccounts.add(xAccount);
		}
		return xAccounts.toArray(new XAccount[0]);
	}
	public final XConnection[] getConnections(String sessionId) throws SessionExpiredException {
		AuthUser admin = getLoggedInUser(sessionId);
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(admin);
		List<XConnection> xConnections = new ArrayList<XConnection>();
		for(PaloConnection connection : adminService.getConnections()) {
			XConnection xConnection = 
				(XConnection)XConverter.createX(connection); 				
			xConnections.add(xConnection);
		}
		return xConnections.toArray(new XConnection[0]);
		// TODO throw exception or return empty array !?!?!?
	}

	public final XGroup[] getGroups(String sessionId) throws SessionExpiredException {
		AuthUser admin = getLoggedInUser(sessionId);
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(admin);
		List<XGroup> xGroups = new ArrayList<XGroup>();
		for(Group group : adminService.getGroups()) {
			XGroup xGroup = (XGroup)XConverter.createX(group);
			xGroup.clearRoleNames();
			for (Role r: group.getRoles()) {
				xGroup.addRoleName(r.getName());
			}
			xGroups.add(xGroup);
		}
		return xGroups.toArray(new XGroup[0]);
		//TODO throw exception or return empty array !?!?!?
	}

	public final XRole[] getRoles(String sessionId) throws SessionExpiredException {
		AuthUser admin = getLoggedInUser(sessionId);
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(admin);
		List<XRole> xRoles = new ArrayList<XRole>();
		for(Role role : adminService.getRoles()) {
			XRole xRole = (XRole)XConverter.createX(role);
			xRoles.add(xRole);
		}
		return xRoles.toArray(new XRole[0]);
	}

	public XUser[] getUsers(String sessionId) throws SessionExpiredException {
		AuthUser admin = getLoggedInUser(sessionId);
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(admin);
		List<XUser> xUsers= new ArrayList<XUser>();
		for(User usr : adminService.getUsers()) {
			XUser xUser = (XUser)XConverter.createX(usr);
			xUsers.add(xUser);
		}
		return xUsers.toArray(new XUser[0]);
		//TODO throw exception or return empty array !?!?!?
	}

	public final void delete(String sessionId, Account account) throws OperationFailedException, SessionExpiredException {
		AuthUser admin = getLoggedInUser(sessionId);
		//first delete all views for this account:
//		deleteViewsOf(sessionId, account);
		AdministrationService adminService = 
				ServiceProvider.getAdministrationService(admin);
		adminService.delete(account);
		
	}
	private final void deleteViewsOf(String sessionId, Account account)
			throws SessionExpiredException, OperationFailedException {
		AuthUser admin = getLoggedInUser(sessionId);
		ViewService viewService = ServiceProvider.getViewService(admin);
		for (View view : viewService.getViews(account))
			viewService.delete(view);
	}
	
	public final void delete(String sessionId, PaloConnection connection) throws OperationFailedException, SessionExpiredException {
		AuthUser admin = getLoggedInUser(sessionId);
		AdministrationService adminService = 
				ServiceProvider.getAdministrationService(admin);
		adminService.delete(connection);
	}
	
	public final void delete(String sessionId, Group group) throws OperationFailedException, SessionExpiredException {
		AuthUser admin = getLoggedInUser(sessionId);
		AdministrationService adminService = 
				ServiceProvider.getAdministrationService(admin);
		adminService.delete(group);
	}
	
	public final void delete(String sessionId, Role role) throws OperationFailedException, SessionExpiredException {
		AuthUser admin = getLoggedInUser(sessionId);
		AdministrationService adminService = 
				ServiceProvider.getAdministrationService(admin);
		adminService.delete(role);
	}
	
	public final void delete(String sessionId, User user) throws OperationFailedException, SessionExpiredException {
		AuthUser admin = getLoggedInUser(sessionId);
		AdministrationService adminService = 
				ServiceProvider.getAdministrationService(admin);
		adminService.delete(user);
	}

	
	public final XAccount save(String sessionId, XAccount xAccount) throws OperationFailedException, SessionExpiredException {
		AuthUser admin = getLoggedInUser(sessionId);
		AdministrationService adminService = 
				ServiceProvider.getAdministrationService(admin);
		Account account = null;
		if (xAccount.getId() != null) {
			account = getNative(sessionId, xAccount);
		}
		if(account == null)
			account = createNative(sessionId, xAccount);
		updateNative(sessionId, account, xAccount);
		adminService.save(account);
		xAccount.setId(account.getId());
		if (xAccount.getUser() != null && xAccount.getUser().getId() != null) {
			if (xAccount.getUser().getId().equals(admin.getId())) {
				adminService.save(account.getUser());
				overrideLoggedInUser(sessionId, account.getUser());
			}
		}
		return xAccount;
	}
	private final Account getNative(String sessionId, XAccount xAccount) throws SessionExpiredException {
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(getLoggedInUser(sessionId));
		return adminService.getAccount(xAccount.getId());
	}
	private final Account createNative(String sessionId, XAccount xAccount)
			throws OperationFailedException, SessionExpiredException {
		User user = getNative(sessionId, xAccount.getUser());
		PaloConnection connection = getNative(sessionId, xAccount.getConnection());
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(getLoggedInUser(sessionId));
		return adminService.createAccount(xAccount.getLogin(), xAccount
				.getPassword(), user, connection);
	}	
	private final void updateNative(String sessionId, Account account, XAccount xAccount) throws SessionExpiredException {
		AuthUser user = getLoggedInUser(sessionId);
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(user);
		adminService.setConnection(getNative(sessionId, xAccount.getConnection()), account);
		adminService.setLoginName(xAccount.getLogin(), account);
		adminService.setPassword(xAccount.getPassword(), account);
		adminService.setUser(getNative(sessionId, xAccount.getUser()), account);
	}
	
	
	public final XConnection save(String sessionId, XConnection xConnection)
			throws OperationFailedException, SessionExpiredException {
		AuthUser admin = getLoggedInUser(sessionId);
		AdministrationService adminService = 
				ServiceProvider.getAdministrationService(admin);
		PaloConnection connection = null;
		if (xConnection.getId() != null) {
			connection = getNative(sessionId, xConnection);
		}
		if(connection == null)
			connection = createNative(sessionId, xConnection);
		updateNative(sessionId, connection, xConnection);
		adminService.save(connection);
		xConnection.setId(connection.getId());
		return xConnection;
	}
	private final PaloConnection getNative(String sessionId, XConnection xConnection) throws SessionExpiredException {
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(getLoggedInUser(sessionId));
		return adminService.getConnection(xConnection.getId());
	}
	private final PaloConnection createNative(String sessionId, XConnection xConnection)
			throws OperationFailedException, SessionExpiredException {
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(getLoggedInUser(sessionId));
		return adminService.createConnection(xConnection.getName(), xConnection
				.getHost(), xConnection.getService(), xConnection
				.getConnectionType());
	}
	private final void updateNative(String sessionId, PaloConnection connection, XConnection xConnection) throws SessionExpiredException {
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(getLoggedInUser(sessionId));
		adminService.setDescription(xConnection.getDescription(), connection);
		adminService.setHost(xConnection.getHost(), connection);
		adminService.setName(xConnection.getName(), connection);
		adminService.setService(xConnection.getService(), connection);
		adminService.setType(xConnection.getConnectionType(), connection);
	}
	
	
	public final XGroup save(String sessionId, XGroup xGroup) throws OperationFailedException, SessionExpiredException {
//		if(true)
//			throw new OperationFailedException("OPERATION FAILED");
		AuthUser admin = getLoggedInUser(sessionId);
		AdministrationService adminService = 
				ServiceProvider.getAdministrationService(admin);
		Group group = null;
		if (xGroup.getId() != null) {
			group = getNative(sessionId, xGroup);
		}
		if(group == null)
			group = createNative(sessionId, xGroup);
		updateNative(sessionId, group, xGroup);
		adminService.save(group);
		xGroup.setId(group.getId());
		return xGroup;
	}
	private final Group createNative(String sessionId, XGroup xGroup)
			throws OperationFailedException, SessionExpiredException {
		AdministrationService adminService = 
				ServiceProvider.getAdministrationService(getLoggedInUser(sessionId));
		return adminService.createGroup(xGroup.getName());
	}
	private final Group getNative(String sessionId, XGroup xGroup) throws SessionExpiredException {
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(getLoggedInUser(sessionId));
		return adminService.getGroup(xGroup.getId());
	}
	private final void updateNative(String sessionId, Group group, XGroup xGroup)
			throws OperationFailedException, SessionExpiredException {
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(getLoggedInUser(sessionId));
		adminService.setName(xGroup.getName(), group);
		adminService.setDescription(xGroup.getDescription(), group);
		adminService.setRoles(getRoles(sessionId, xGroup.getRoleIDs()), group);
		adminService.setUsers(getUsers(sessionId, xGroup.getUserIDs()), group);
	}
	private final List<Role> getRoles(String sessionId, String[] ids) throws SessionExpiredException {		
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(getLoggedInUser(sessionId));
		List<Role> roles = new ArrayList<Role>();
		for(String id : ids) {
			roles.add(adminService.getRole(id));
		}
		return roles;
	}
	private final List<User> getUsers(String sessionId, String[] ids) throws SessionExpiredException {		
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(getLoggedInUser(sessionId));
		List<User> users = new ArrayList<User>();
		for(String id : ids) {
			users.add(adminService.getUser(id));
		}
		return users;
	}
	
	
	public final XRole save(String sessionId, XRole xRole) throws OperationFailedException, SessionExpiredException {
		AuthUser admin = getLoggedInUser(sessionId);
		AdministrationService adminService = 
				ServiceProvider.getAdministrationService(admin);
		Role role = null;
		if (xRole.getId() != null) {
			role = getNative(sessionId, xRole);
		}
		if(role == null)
			role = createNative(sessionId, xRole);
		updateNative(sessionId, role, xRole);
		adminService.save(role);
		xRole.setId(role.getId());
		return xRole;
	}
	private final Role getNative(String sessionId, XRole xRole) throws SessionExpiredException {
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(getLoggedInUser(sessionId));
		return adminService.getRole(xRole.getId());
	}
	private final Role createNative(String sessionId, XRole xRole)
			throws OperationFailedException, SessionExpiredException {
		AdministrationService adminService = 
				ServiceProvider.getAdministrationService(getLoggedInUser(sessionId));
		return adminService.createRole(xRole.getName());
	}
	private final void updateNative(String sessionId, Role role, XRole xRole) throws SessionExpiredException {
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(getLoggedInUser(sessionId));
		adminService.setDescription(xRole.getDescription(), role);
		adminService.setName(xRole.getName(), role);
		adminService.setPermission(
				Right.fromString(xRole.getPermission()), role);
	}
	
	
	public final XUser save(String sessionId, XUser xUser) throws OperationFailedException, SessionExpiredException {
		AuthUser admin = getLoggedInUser(sessionId);
		AdministrationService adminService = 
				ServiceProvider.getAdministrationService(admin);
		User user = null;
		if (xUser.getId() != null) {
			user = getNative(sessionId, xUser);
		}
		if(user == null) {
			user = createNative(sessionId, xUser);
			//by default the user gets the owner role:
			//Role ownerRole = adminService.getRoleByName(Role.OWNER);
			//adminService.add(ownerRole, user);
			//xUser.addRoleID(ownerRole.getId());
		}
		updateNative(sessionId, user, xUser);
		adminService.save(user);
		xUser.setId(user.getId());
		return xUser;
	}
	private final User getNative(String sessionId, XUser xUser) throws SessionExpiredException {
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(getLoggedInUser(sessionId));
		return adminService.getUser(xUser.getId());
	}
	private final User createNative(String sessionId, XUser xUser)
			throws OperationFailedException, SessionExpiredException {
		AdministrationService adminService = 
				ServiceProvider.getAdministrationService(getLoggedInUser(sessionId));
		return adminService.createUser(xUser.getFirstname(), xUser
				.getLastname(), xUser.getLogin(), xUser.getPassword());
	}
	private final void updateNative(String sessionId, User user, XUser xUser)
			throws OperationFailedException, SessionExpiredException {
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(getLoggedInUser(sessionId));
		adminService.setFirstname(xUser.getFirstname(), user);
		adminService.setLastname(xUser.getLastname(), user);
		adminService.setLoginName(xUser.getLogin(), user);
		String password = xUser.getPassword();
		if(password != null && !password.equals("")) {
			password = AuthUserImpl.encrypt(password);
		} else {
			password = user.getPassword();
		}
		adminService.setPassword(password, user);
		adminService.setGroups(getGroups(sessionId, xUser.getGroupIDs()), user);
		adminService.setRoles(getRoles(sessionId, xUser.getRoleIDs()), user);
	}
	
	private final List<Group> getGroups(String sessionId, String[] ids) throws SessionExpiredException {
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(getLoggedInUser(sessionId));
		List<Group> groups = new ArrayList<Group>();
		for(String id : ids) {
			groups.add(adminService.getGroup(id));
		}
		return groups;

	}
}
