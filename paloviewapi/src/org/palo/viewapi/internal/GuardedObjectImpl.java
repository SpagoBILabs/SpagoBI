/*
*
* @file GuardedObjectImpl.java
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
* @version $Id: GuardedObjectImpl.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
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

import org.palo.viewapi.GuardedObject;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;


/**
 * <code>GuardedObject</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: GuardedObjectImpl.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
abstract class GuardedObjectImpl extends DomainObjectImpl 
		implements GuardedObject {

	protected User owner;
	private final Set<String> roles;
	
	public GuardedObjectImpl(String id) {
		super(id);
		this.roles = new HashSet<String>();		
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

	public final boolean hasRole(Role role) {
		return roles.contains(role.getId());
	}

	public final List<String> getRoleIDs() {
		return new ArrayList<String>(roles);
	}

	final User getOwnerInternal() {
		return owner;
	}
	public final User getOwner() {
		return owner;
	}
	public final boolean isOwner(User user) {
		return owner != null && owner.equals(user);
	}
	
	//--------------------------------------------------------------------------
	// PACKAGE ACCES
	//
	protected final void add(Role role) {
		roles.add(role.getId());
	}
	protected final void setRoles(Set<String> roles) {
		this.roles.clear();
		if(roles != null) {
			this.roles.addAll(roles);
		}
	}	
	final void remove(Role role) {
		roles.remove(role.getId());
	}
	final void setOwner(User owner) {
		this.owner = owner;
	}

}
