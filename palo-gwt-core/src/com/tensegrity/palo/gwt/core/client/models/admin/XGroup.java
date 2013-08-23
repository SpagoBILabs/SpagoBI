/*
*
* @file XGroup.java
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
* @version $Id: XGroup.java,v 1.5 2010/01/13 08:02:44 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.client.models.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.tensegrity.palo.gwt.core.client.models.XObject;

/**
 * <code>XGroup</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XGroup.java,v 1.5 2010/01/13 08:02:44 PhilippBouillon Exp $
 **/
public class XGroup extends XObject {

	public static final String TYPE = XGroup.class.getName();
	
	private String description;
	private Set<String> users = new HashSet<String>();
	private Set<String> roles = new HashSet<String>();
	private ArrayList <String> roleNames = new ArrayList<String>();
	
	public XGroup() {		
	}
	
	public XGroup(String id, String name) {
		setId(id);
		setName(name);
	}
	
	public final void addRoleID(String id) {
		roles.add(id);
	}
	
	public final void addUserID(String id) {
		users.add(id);
	}	
	
	public final void removeUserID(String id) {
		users.remove(id);
	}

	public final void clearRoles() {
		roles.clear();
	}
	public final void clearUsers() {
		users.clear();
	}

	public final String[] getRoleIDs() {
		return roles.toArray(new String[0]);
	}

	public final String[] getUserIDs() {
		return users.toArray(new String[0]);
	}

	public final String getDescription() {
		return description;
	}

//	public final XRole[] getRoles() {
//		if(roles == null)
//			return new XRole[0];
//		return roles;
//	}
//
//	public final XUser[] getUsers() {
//		if(users == null)
//			return new XUser[0];
//		return users;
//	}
	
	public final String getType() {
		return TYPE;
	}

	public final void setDescription(String description) {
		this.description =  description;
	}

	public final void clearRoleNames() {
		roleNames.clear();
	}
	
	public final void addRoleName(String roleName) {
		roleNames.add(roleName);
	}
	
	public String [] getRoleNames() {
		Collections.sort(roleNames);
		return roleNames.toArray(new String[0]);
	}
	
//	public final void setRoles(XRole[] roles) {
//		this.roles = roles;
//	}
//	
//	public final void setUsers(XUser[] users) {
//		this.users = users;
//	}
}
