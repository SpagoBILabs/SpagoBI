/*
*
* @file UserConverter.java
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
* @version $Id: UserConverter.java,v 1.8 2010/02/16 13:54:00 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.converter.admin;

import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Group;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.internal.IGroupManagement;
import org.palo.viewapi.internal.IRoleManagement;
import org.palo.viewapi.internal.UserImpl;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;
import org.palo.viewapi.services.AdministrationService;
import org.palo.viewapi.services.ServiceProvider;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.server.converter.BaseConverter;

/**
 * <code>UserConverter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: UserConverter.java,v 1.8 2010/02/16 13:54:00 PhilippBouillon Exp $
 **/
public class UserConverter extends BaseConverter {

	protected Class<?> getNativeClass() {
		return User.class;
	}

	protected Class<?> getXObjectClass() {
		return XUser.class;
	}

//	public Object toNative(XObject xObj, AuthUser loggedInUser) throws OperationFailedException {
//		XUser xUser = (XUser) xObj;
//		AdministrationService adminService = 
//			ServiceProvider.getAdministrationService(loggedInUser);
//		User user = adminService.getUserById(xUser.getId());
//		if(user == null)
//			user = create(xUser, loggedInUser);
//		update(user, xUser, loggedInUser);
//		return user;
//	}

	public XObject toXObject(Object nativeObj) {
		UserImpl user = (UserImpl) nativeObj;
		XUser xUser = new XUser(user.getId(), user.getLoginName());
		xUser.setFirstname(user.getFirstname());
		xUser.setLastname(user.getLastname());
		xUser.setPassword(user.getPassword());
		for(Account account : user.getAccounts())
			xUser.addAccountID(account.getId());
		for(String groupId : user.getGroupIDs())
			xUser.addGroupID(groupId);
		IRoleManagement mgmt = MapperRegistry.getInstance().getRoleManagement();
		for(String roleId : user.getRoleIDs()) {
			xUser.addRoleID(roleId);			
			try {
				xUser.addRoleName(roleId, ((Role) mgmt.find(roleId)).getName());
			} catch (Throwable t) {
			}
		}
		IGroupManagement groups = MapperRegistry.getInstance().getGroupManagement();
		for (String groupId: user.getGroupIDs()) {
			try {
				Group g = (Group) groups.find(groupId);
				if (g != null) {
					for (Role r: g.getRoles()) {
						xUser.addRoleID(r.getId());
						xUser.addRoleName(r.getId(), r.getName());
					}
				}
			} catch (Throwable t) {
			}
			
		}
		
		if(user instanceof AuthUser) {
			AuthUser authUser = (AuthUser) user;
			// are we admin?
			xUser.setIsAdmin(ServiceProvider.hasPermission(Right.GRANT,
					AdministrationService.class, authUser));
		}		
		return xUser;
	}
	
	private final User create(XUser xUser, AuthUser loggedInUser)
			throws OperationFailedException {
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(loggedInUser);
		return adminService.createUser(xUser.getFirstname(), 
				xUser.getLastname(), xUser.getLogin(), xUser.getPassword());
	}
//	private final void update(User user, XUser xUser, AuthUser loggedInUser)
//			throws OperationFailedException {
//		AdministrationService adminService = ServiceProvider
//				.getAdministrationService(loggedInUser);
//		adminService.setFirstname(xUser.getFirstname(), user);
//		adminService.setLastname(xUser.getLastname(), user);
//		adminService.setLoginName(xUser.getLogin(), user);
//		adminService.setPassword(xUser.getPassword(), user);
//		// UserImpl _user = (UserImpl)user;
//		// for(String account : xUser.getAccountIDs())
//		// adminService.set_user.saddxUser.addAccountID(account.getId());
//		for (String groupId : xUser.getGroupIDs())
//			adminService.add(user, adminService.getGroupById(groupId));
//		for (String roleId : xUser.getRoleIDs())
//			adminService.add(user, adminService.getRoleById(roleId));
//	}

}
