/*
*
* @file AccessController.java
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
* @version $Id: AccessController.java,v 1.11 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.util.List;

import org.palo.viewapi.AuthUser;
import org.palo.viewapi.DomainObject;
import org.palo.viewapi.Group;
import org.palo.viewapi.GuardedObject;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.exceptions.NoPermissionException;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;
import org.palo.viewapi.internal.io.CubeViewReader;
import org.palo.viewapi.services.ServiceProvider;

/**
 * <code>AccessController</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AccessController.java,v 1.11 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
final class AccessController {

	
	final static boolean isAdmin(AuthUser user) {
		List<Role> roles = user.getRoles();
		for (Role role : roles)
			if (role.getName().equalsIgnoreCase("admin")
					&& role.hasPermission(Right.GRANT))
				return true;
		return false;
	}

	/**
	 * In contrast to {@link #checkPermission(Right, AuthUser)} this method
	 * does not throw an {@link NoPermissionException}!
	 * @param right
	 * @param user
	 * @return
	 */
	final static boolean hasPermission(Right right, AuthUser user) {
		List<Role> roles = user.getRoles();
		for (Role role : roles) {
			if (role.hasPermission(right))
				return true;
		}
		for (Group g: user.getGroups()) {
			for (Role r: g.getRoles()) {
				if (r.hasPermission(right)) {
					return true;
				}
			}
		}
		return false;
	}

	final static boolean hasPermissionIgnoreOwner(Right right, GuardedObject onObj, AuthUser user) {
		// TODO pb hack alert:
		// for the current version of WPalo, we must be able to show that
		// a "viewer" user can only _read_ views. Since in WPalo it is
		// currently not possible to open views of different users, the
		// procedure is as follows: Grant admin rights for the viewer,
		// let the viewer create a view, remove admin rights from the
		// viewer. Now, the viewer must not be allowed to change his
		// view, _although_ he is the owner ==>
		// Let a user not be an owner if he has only read rights...

		if (onObj != null) {
			List<Role> roles = onObj.getRoles();
			for (Role role : roles) {
				if (user.hasRole(role) && role.hasPermission(right))
					return true;
			}
		}
		// Another evil hack to ensure that admins can still write...
		for (Role role: user.getRoles()) {
			if (role.hasPermission(right)) {
				return true;
			}
		}
		for (Group g: user.getGroups()) {
			for (Role r: g.getRoles()) {
				if (r.hasPermission(right)) {
					return true;
				}
			}
		}
		return false;			
	}
	
	/**
	 * In contrast to {@link #checkPermission(Right, GuardedObject, AuthUser)} 
	 * this method does not throw an {@link NoPermissionException}!
	 * @param right
	 * @param onObj
	 * @param user
	 * @return
	 */
	final static boolean hasPermission(Right right, GuardedObject onObj,
			AuthUser user) {
		//check if user is owner:				
		if(onObj.isOwner(user) || ServiceProvider.isAdmin(user))
			return true;
		List<Role> roles = onObj.getRoles();
		for (Role role : roles) {
			if (user.hasRole(role) && role.hasPermission(right))
				return true;
		}
		return false;
	}
	
	/**
	 * @param right
	 * @param user
	 * @throws NoPermissionException if the given user has not the specified 
	 * right
	 */
	final static void checkPermission(Right right, AuthUser user) {
		if (!hasPermission(right, user)) {
			NoPermissionException exception = new NoPermissionException(
					"User has no permission !!", null, user);
			exception.setRequiredRight(right);
			throw exception;
		}
	}
	/**
	 * 
	 * @param right
	 * @param onObj
	 * @param user
	 * @throws NoPermissionException if the given user has not the specified 
	 * right for the given object
	 */
	final static void checkPermission(Right right, GuardedObject onObj,
			AuthUser user) {
		if (!CubeViewReader.CHECK_RIGHTS) {
			return;
		}
		if (!hasPermission(right, onObj, user)) {
			NoPermissionException exception = new NoPermissionException(
					"User has no permission to access object!!", onObj, user);
			exception.setRequiredRight(right);
			throw exception;
		}
	}

	final static void checkAccess(Class<? extends DomainObject> forObj) {
		MapperRegistry.checkAccess(forObj);
	}
}
