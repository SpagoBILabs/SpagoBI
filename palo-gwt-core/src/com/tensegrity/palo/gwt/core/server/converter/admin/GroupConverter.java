/*
*
* @file GroupConverter.java
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
* @version $Id: GroupConverter.java,v 1.4 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.converter.admin;

import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Group;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.services.AdministrationService;
import org.palo.viewapi.services.ServiceProvider;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XGroup;
import com.tensegrity.palo.gwt.core.server.converter.BaseConverter;

/**
 * <code>GroupConverter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: GroupConverter.java,v 1.4 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public class GroupConverter extends BaseConverter {

	protected Class<?> getNativeClass() {
		return Group.class;
	}

	protected Class<?> getXObjectClass() {
		return XGroup.class;
	}

//	public Object toNative(XObject obj, AuthUser loggedInUser) throws OperationFailedException {
//		XGroup xGroup = (XGroup) obj;
//		AdministrationService adminService = 
//			ServiceProvider.getAdministrationService(loggedInUser);
//		Group group = adminService.getGroupById(xGroup.getId());
//		if(group == null)
//			group = create(xGroup, loggedInUser);
//		update(group, xGroup, loggedInUser);
//		return group;
//
//	}

	public XObject toXObject(Object nativeObj) {
		Group group = (Group) nativeObj;
		XGroup xGroup = new XGroup(group.getId(), group.getName());
		xGroup.setDescription(group.getDescription());
		for(User user : group.getUsers())
			xGroup.addUserID(user.getId());
		for(Role role : group.getRoles())
			xGroup.addRoleID(role.getId());
		return xGroup;
	}
	
	private final Group create(XGroup xGroup, AuthUser loggedInUser) throws OperationFailedException {
		AdministrationService adminService = 
				ServiceProvider.getAdministrationService(loggedInUser);
		return adminService.createGroup(xGroup.getName());
	}
//	private final void update(Group group, XGroup xGroup, AuthUser loggedInUser) throws OperationFailedException {
//		AdministrationService adminService = 
//			ServiceProvider.getAdministrationService(loggedInUser);
//		adminService.setDescription(xGroup.getDescription(),group);
//		adminService.setName(xGroup.getName(), group);
//		// roles:
//		removeAllRoles(group, adminService);
//		addAllRoles(group, xGroup, adminService);
//		// users:
//		removeAllUsers(group, adminService);
//		addAllUsers(group, xGroup, adminService);
//	}
//
//	private final void removeAllRoles(Group group,
//			AdministrationService adminService) throws OperationFailedException {
//		for (Role role : group.getRoles())
//			adminService.remove(group, role);
//	}
//	
//	private final void addAllRoles(Group group, XGroup xGroup,
//			AdministrationService adminService) throws OperationFailedException {
//		for (String id : xGroup.getRoleIDs()) {
//			Role role = adminService.getRoleById(id);
//			if (role != null)
//				adminService.add(group, role);
//		}
//	}
//	private final void removeAllUsers(Group group,
//			AdministrationService adminService) throws OperationFailedException {
//		for (User user : group.getUsers())
//			adminService.remove(user, group);
//	}
//	
//	private final void addAllUsers(Group group, XGroup xGroup,
//			AdministrationService adminService) throws OperationFailedException {
//		for (String id : xGroup.getUserIDs()) {
//			User user = adminService.getUserById(id);
//			if (user != null)
//				adminService.add(user, group);
//		}
//	}	
}
