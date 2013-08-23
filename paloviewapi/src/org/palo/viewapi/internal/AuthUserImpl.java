/*
*
* @file AuthUserImpl.java
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
* @version $Id: AuthUserImpl.java,v 1.9 2009/12/17 16:14:08 PhilippBouillon Exp $
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
import org.palo.viewapi.DbConnection;
import org.palo.viewapi.Group;
import org.palo.viewapi.GuardedObject;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;
import org.palo.viewapi.services.Service;

/**
 * <code>User</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AuthUserImpl.java,v 1.9 2009/12/17 16:14:08 PhilippBouillon Exp $ 
 **/
public final class AuthUserImpl extends UserImpl implements AuthUser {

	private DbConnection connection;
	
	AuthUserImpl(User user) {
		super((UserImpl)user); 
	}

	final void login(DbConnection connection) {
		this.connection = connection;
	}
	
	final DbConnection getConnection() {
		return connection;
	}
	
	public final void logout() {
		connection = null;
		//TODO here we simply remove our internally used java.sql.Connection
		//		=> maybe we notify ServiceProvider/DatabaseService to give 
		System.err.println("AuthUser#logout() NOT IMPLEMENTED YET!!");
	}
	
	public final List<Account> getAccounts() {
		return new ArrayList<Account>(accounts);
	}

	public final List<Group> getGroups() {
		IGroupManagement groupMgmt = 
			MapperRegistry.getInstance().getGroupManagement();
		List<Group> groups = new ArrayList<Group>();
		for(String id : this.groups) {
			try {
				Group group = (Group) groupMgmt.find(id);
				if (group != null && !groups.contains(group))
					groups.add(group);
			} catch (SQLException e) { /* ignore */
			}
		}
		return groups;
	}
	
	public final List<Role> getRoles() {
		IRoleManagement roleMgmt = 
			MapperRegistry.getInstance().getRoleManagement();
		List<Role> roles = new ArrayList<Role>();
		for(String id : this.roles) {
			try {
				Role role = (Role) roleMgmt.find(id);
				if (role != null && !roles.contains(role))
					roles.add(role);
			} catch (SQLException e) { /* ignore */
			}
		}
		return roles;
	}
	
	public final boolean hasPermission(Right right, GuardedObject forObj) {
		return AccessController.hasPermission(right, forObj, this);
	}

	public final boolean hasPermissionIgnoreOwner(Right right, GuardedObject forObj) {
		return AccessController.hasPermissionIgnoreOwner(right, forObj, this);
	}

	public final boolean hasPermission(Right right,
			Class<? extends Service> forService) {
		return ServiceProviderImpl.hasPermission(right, forService, this);
	}
	
//	//--------------------------------------------------------------------------
//	// INTERNAL API
//	//
//	final void add(Role role) {
//		roles.add(role.getId());
//	}
//	final void add(Group group) {
//		groups.add(group.getId());
//	}
//	final void add(Account account) {
//		if(account != null)
//			accounts.add(account);
//	}
//	final void remove(Account account) {
//		accounts.remove(account);
//	}
//	final void remove(Role role) {
//		roles.remove(role.getId());
//	}
//	final void remove(Group group) {
//		groups.remove(group.getId());
//	}
//	final void setRoles(List<String> roles) {
//		this.roles.clear();
//		if(roles != null)
//			this.roles.addAll(roles);
//	}
//	final void setGroups(List<String> groups) {
//		this.groups.clear();
//		if(groups != null)
//			this.groups.addAll(groups);
//	}
//	final void setAccounts(List<Account> accounts) {
//		this.accounts.clear();
//		if(accounts != null)
//			this.accounts.addAll(accounts);
//	}
//	final void setAuthentication(Authentication authentication) {
//		this.authentication = authentication;
//	}
//	final Authentication getAuthentication() {
//		return authentication;
//	}
}
