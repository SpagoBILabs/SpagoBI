/*
*
* @file RoleConverter.java
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
* @version $Id: RoleConverter.java,v 1.4 2010/02/16 13:54:00 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.converter.admin;

import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Role;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.services.AdministrationService;
import org.palo.viewapi.services.ServiceProvider;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XRole;
import com.tensegrity.palo.gwt.core.server.converter.BaseConverter;

/**
 * <code>RoleConverter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: RoleConverter.java,v 1.4 2010/02/16 13:54:00 PhilippBouillon Exp $
 **/
public class RoleConverter extends BaseConverter {

	protected Class<?> getNativeClass() {
		return Role.class;
	}

	protected Class<?> getXObjectClass() {
		return XRole.class;
	}

//	public Object toNative(XObject obj, AuthUser loggedInUser) throws OperationFailedException {
//		XRole xRole = (XRole) obj;
//		AdministrationService adminService = 
//			ServiceProvider.getAdministrationService(loggedInUser);
//		Role role = adminService.getRoleById(xRole.getId());
//		if(role == null)
//			role = create(xRole, loggedInUser);
//		update(role, xRole, loggedInUser);
//		return role;
//	}

	public XObject toXObject(Object nativeObj) {
		Role role = (Role) nativeObj;
		XRole xRole = new XRole(role.getId(), role.getName());
		xRole.setDescription(role.getDescription());
		xRole.setPermission(role.getPermission().toString());
		return xRole;
	}
	
	private final Role create(XRole xRole, AuthUser loggedInUser)
			throws OperationFailedException {
		AdministrationService adminService = 
				ServiceProvider.getAdministrationService(loggedInUser);
		return adminService.createRole(xRole.getName());
	}

//	private final void update(Role role, XRole xRole, AuthUser loggedInUser)
//			throws OperationFailedException {
//		AdministrationService adminService = 
//				ServiceProvider.getAdministrationService(loggedInUser);
//		adminService.setDescription(xRole.getDescription(), role);
//		adminService.setName(xRole.getName(), role);
//		adminService.setPermission(
//				Right.fromString(xRole.getPermission()), role);
//		// groups:
//		removeAllGroups(role, adminService);
//		addAllGroups(role, xRole, adminService);
//		// users:
//		removeAllUsers(role, adminService);
//		addAllUsers(role, xRole, adminService);
//	}

//	private final void removeAllGroups(Role role,
//			AdministrationService adminService) throws OperationFailedException {
//		for (Group group : role.getGroups())
//			adminService.remove(group, role);
//	}
//	
//	private final void addAllGroups(Role role, XRole xRole,
//			AdministrationService adminService) throws OperationFailedException {
//		for (String id : xRole.getGroupIDs()) {
//			Group group = adminService.getGroupById(id);
//			if (group != null)
//				adminService.add(group, role);
//		}
//	}
//	private final void removeAllUsers(Role role,
//			AdministrationService adminService) throws OperationFailedException {
//		for (User user : role.getUsers())
//			adminService.remove(user, role);
//	}
//	
//	private final void addAllUsers(Role role, XRole xRole,
//			AdministrationService adminService) throws OperationFailedException {
//		for (String id : xRole.getUserIDs()) {
//			User user = adminService.getUserById(id);
//			if (user != null)
//				adminService.add(user, role);
//		}
//	}
}
