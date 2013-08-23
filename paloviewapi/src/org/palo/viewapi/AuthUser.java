/*
*
* @file AuthUser.java
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
* @version $Id: AuthUser.java,v 1.10 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi;

import java.util.List;

import org.palo.viewapi.services.Service;

/**
 * The <code>AuthUser</code> interface represents an authenticated user. 
 * Instances of this class are obtained by authenticate against an 
 * {@link AuthenticationProvider}.
 *
 * @version $Id: AuthUser.java,v 1.10 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public interface AuthUser extends User {

//	/**
//	 * Returns the login name for this user.
//	 * @return the login name
//	 */
//	public String getLoginName();	
//	/**
//	 * Returns the encrypted password use by this user.
//	 * @return the encrypted password
//	 */
//	public String getPassword();
//	
//	/**
//	 * Log out this user.
//	 */
//	public void logout();
	
	/** 
	 * Returns all {@link Account}s this user owns.
	 * @return all accounts of this user
	 */
	public List<Account> getAccounts();
	/**
	 * Returns all {@link Group}s this user is a member of.
	 * @return all groups this user is a member of
	 */
	public List<Group> getGroups();
	
	public boolean isMemberOf(Group group);
	
	/**
	 * Returns all {@link Role}s this user is a member of.
	 * @return all roles this user is a member of
	 */
	public List<Role> getRoles();
	
	public boolean hasRole(Role role);
	
	/**
	 * Checks if this user has the permission to access the given 
	 * {@link GuardedObject} with the specified {@link Right}. 
	 * @param right the access right
	 * @param forObj the guarded object to check
	 * @return <code>true</code> if the user has the given access right, 
	 * <code>false</code> otherwise
	 */
	public boolean hasPermission(Right right, GuardedObject forObj);
	public boolean hasPermissionIgnoreOwner(Right right, GuardedObject forObj);
	
	public boolean hasPermission(Right right, Class<? extends Service> forService);

}
