/*
*
* @file GroupImpl.java
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
* @version $Id: GroupImpl.java,v 1.8 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.palo.viewapi.Group;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;

/**
 * <code>Group</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: GroupImpl.java,v 1.8 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public final class GroupImpl extends DomainObjectImpl implements Group {

	private String name;
	private String descr;
	private final Set<String> roles = new HashSet<String>();
	private final Set<String> users = new HashSet<String>();

	
	public GroupImpl(String id) {
		super(id);
	}
	private GroupImpl(Builder builder) {
		super(builder.id);
		descr = builder.description;
		name = builder.name;
//		setRoles(builder.roles);
		roles.addAll(builder.roles);
		setUsers(builder.users);
	}
	
	public final String getDescription() {
		return descr;
	}

	public final String getName() {
		return name;
	}
	
	public final List<Role> getRoles() {
		IRoleManagement roleMgmt = 
			MapperRegistry.getInstance().getRoleManagement();
		List<Role> allRoles = new ArrayList<Role>();
		for (String id : roles) {
			try {
				Role role = (Role) roleMgmt.find(id);
				if (role != null && !allRoles.contains(role))
					allRoles.add(role);
			} catch (SQLException e) { /* ignore */
			}
		}
		return allRoles;
	}
	public final List<String> getRoleIDs() {
		return new ArrayList<String>(roles);
	}

	public final List<User> getUsers() {
		IUserManagement usrMgmt = MapperRegistry.getInstance().getUserManagement();
		List<User> allUsers = new ArrayList<User>();
		for(String id : users) {
			try {
				User user = (User) usrMgmt.find(id);
				if (user != null && !allUsers.contains(user))
					allUsers.add(user);
			} catch (SQLException e) { /* ignore */
			}
		}
		return allUsers;
	}
	public final List<String> getUserIDs() {
		return new ArrayList<String>(users);
	}

	public final boolean hasMember(User user) {
		return users.contains(user.getId());
	}

	//--------------------------------------------------------------------------
	// INTERNAL API
	//
	final void add(User user) {
		users.add(user.getId());
	}
	final void add(Role role) {
		roles.add(role.getId());
	}
	final void remove(User user) {
		users.remove(user.getId());
	}
	final void remove(Role role) {
		roles.remove(role.getId());
	}
	final void setDescription(String descr) {
		this.descr = descr;
	}
	final void setName(String name) {
		this.name = name;
	}
	final void setRoles(List<Role> roles) {
		this.roles.clear();
		if (roles != null) {
			for (Role role : roles)
				this.roles.add(role.getId());
		}
	}
	final void setUsers(List<String> users) {
		this.users.clear();
		if(users != null) {
			this.users.addAll(users);
		}
	}
	
	/** 
	 * static builder class
	 * <b>NOTE: not for external usage! </b>
	 */
	public static final class Builder {
		private final String id;
		private String name;
		private String description;		
		private final List<String> roles = new ArrayList<String>();
		private final List<String> users = new ArrayList<String>();
		
		public Builder(String id) {
			AccessController.checkAccess(Group.class);
			this.id = id;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		public Builder roles(List<String> roles) {
			this.roles.clear();
			if(roles != null)
				this.roles.addAll(roles);
			return this;
		}
		public Builder users(List<String> users) {
			this.users.clear();
			if(users != null)
				this.users.addAll(users);
			return this;
		}

		public Group build() {
			return new GroupImpl(this);
		}
	}

}
