/*
*
* @file RoleImpl.java
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
* @version $Id: RoleImpl.java,v 1.9 2009/12/17 16:14:08 PhilippBouillon Exp $
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
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;


/**
 * <code>Role</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: RoleImpl.java,v 1.9 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public final class RoleImpl extends DomainObjectImpl implements Role {
	
	private Right right;
	private String name;
	private String descr;
	
//	private final Set<String> users = new HashSet<String>();
//	private final Set<String> groups = new HashSet<String>();
	
	RoleImpl(String id) {
		super(id);
		//initially we have no rights by default:
		right = Right.NONE;
	}
	
	private RoleImpl(Builder builder) {
		super(builder.id);
		this.name = builder.name;
		this.right = builder.right;
		this.descr = builder.descr;
//		if(builder.users != null)
//			users.addAll(builder.users);
//		if(builder.groups != null)
//			groups.addAll(builder.groups);
	}

	//--------------------------------------------------------------------------
	// INTERFACE
	//
	public final String getDescription() {
		return descr;
	}

//	public final List<Group> getGroups() {
//		ArrayList<Group> groups = new ArrayList<Group>();
//		IGroupManagement grpMgmt = 
//			MapperRegistry.getInstance().getGroupManagement();
//		for(String id : this.groups) {
//			try {
//				Group group = (Group) grpMgmt.find(id);
//				if (group != null)
//					groups.add(group);
//			} catch (SQLException e) { /* ignore */
//			}
//		}
//		return groups;
//	}
//	
//	public final List<User> getUsers() {
//		HashSet<User> users = new HashSet<User>();
//		IUserManagement usrMgmt = 
//			MapperRegistry.getInstance().getUserManagement();
//		for(String id : this.users) {
//			try {
//				User user = (User) usrMgmt.find(id);
//				if (user != null)
//					users.add(user);
//			} catch (SQLException e) { /* ignore */
//			}
//		}
//		List<Group> groups = getGroups();
//		for(Group group : groups) {
//			users.addAll(group.getUsers());
//		}
//		return new ArrayList<User>(users);
//	}
//	
//	public final Group getGroup(String id) {
//		IGroupManagement grpMgmt = MapperRegistry.getInstance()
//				.getGroupManagement();
//		Group group = null;
//		try {
//			group = (Group) grpMgmt.find(id);
//		} catch (SQLException e) { /* ignore */
//		}
//		return group;
//	}

	public final String getName() {
		return name;
	}
	
	public final Right getPermission() {
		return right;
	}
		
	public final boolean hasPermission(Right right) {
		return this.right.getPriority()>=right.getPriority();
	}
	
//	public final boolean hasMember(User user) {
//		boolean isMember = users.contains(user.getId());
//		if(!isMember) {
//			//check if user is member of a groups:
//			List<Group> groups = getGroups();
//			for(Group group : groups) {
//				if(group.hasMember(user)) {
//					isMember = true;
//					break;
//				}
//			}
//		}
//		return isMember;
//	}
//	
//	public final boolean hasMember(Group group) {
//		return groups.contains(group.getId());
//	}


//	//--------------------------------------------------------------------------
//	// ADDITIONAL
//	//
//	public final List<String> getGroupIDs() {
//		return new ArrayList<String>(groups);
//	}
//
//	public final List<String> getUserIDs() {		
//		return new ArrayList<String>(users);
//	}

	
	//--------------------------------------------------------------------------
	// PACKAGE ACCESS
	//
//	final void add(Group group) {
//		groups.add(group.getId());
//	}
//	
//	final void add(User user) {
//		users.add(user.getId());
//	}
//
//	final void remove(User user) {
//		users.remove(user.getId());
//	}
//
//	final void remove(Group group) {
//		groups.remove(group.getId());
//	}

	public final void setDescription(String descr) {
		this.descr = descr;
	}

//	final void setGroups(List<String> groups) {
//		this.groups.clear();
//		if (groups != null) {
//			this.groups.addAll(groups);
//		}
//	}
	final void setName(String name) {
		this.name = name;
	}
	final void setPermission(Right right) {
		this.right = right;
	}

//	final void setUsers(List<String> users) {
//		this.users.clear();
//		if (users != null) {
//			this.users.addAll(users);
//		}
//	}


	/** 
	 * static builder class
	 * <b>NOTE: not for external usage! </b>
	 */
	public static final class Builder {
		private final String id;
		private Right right;
		private String name;
		private String descr;
//		private List<String> groups;
//		private List<String> users = new ArrayList<String>();

		public Builder(String id) {
			AccessController.checkAccess(Role.class);
			this.id = id;
		}
		
		public final Builder name(String name) {
			this.name = name;
			return this;
		}
		public final Builder permission(Right right) {
			this.right = right;
			return this;
		}
		public final Builder description(String descr) {
			this.descr = descr;
			return this;
		}		
//		public final Builder groups(List<String> groups) {
//			this.groups = groups;
//			return this;
//		}
//		public final Builder users(List<String> users) {
//			this.users = users;
//			return this;
//		}
//		public final Builder add(User user) {
//			users.add(user.getId());
//			return this;
//		}
		public final Role build() {
			return new RoleImpl(this);
		}
	}
}
