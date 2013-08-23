/*
*
* @file Role.java
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
* @version $Id: Role.java,v 1.6 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi;

import java.util.List;


/**
 * <code>Role</code>
 * <p>
 * A role consists of {@link User}s and {@link Group}s. Furthermore a role is
 * associated with certain {@link Right}s which means that all its members 
 * (users and groups) have these permissions. Roles are used for access control
 * of {@link GuardedObject}s.  
 * </p>
 *
 * @version $Id: Role.java,v 1.6 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public interface Role extends DomainObject {
	
	public static final String ADMIN = "ADMIN";
	public static final String OWNER = "OWNER";
	
	
	/**
	 * Returns the name of this role. The name is unique, i.e. no other role
	 * with the same name exists.
	 * @return the role name
	 */
	public String getName();
	/**
	 * Returns the optional description of this role or <code>null</code> if
	 * no description were added.
	 * @return the role description or <code>null</code>
	 */
	public String getDescription();
	public void setDescription(String desc);

	//users:
//	/**
//	 * Returns a list of all {@link User}s which belong to this role, including
//	 * those which are members of added {@link Group}s. 
//	 * @return a list of users
//	 */
//	public List<User> getUsers();
//	/**
//	 * Checks if the given user belongs to this role, i.e. was added itself or
//	 * is a member of a {@link Group} which was added to this role. 
//	 * @param user a valid user instance
//	 * @return <code>true</code> if the given user belongs to this role, 
//	 * <code>false</code> otherwise
//	 */
//	public boolean hasMember(User user);
	
	//groups:
//	/**
//	 * Returns all groups which were added to this role. 
//	 * @return a list of groups
//	 */
//	public List<Group> getGroups();
//	/**
//	 * Checks if the given group belongs to this role
//	 * @param group a valid group instance
//	 * @return <code>true</code> if the given group belongs to this role, 
//	 * <code>false</code> otherwise
//	 */
//	public boolean hasMember(Group group);
//	public Group getGroup(String id);  TODO required???
	
	/**
	 * Returns the {@link Right} object which is associated with this role.
	 * Currently the rights are sorted, i.e. only the highest right is returned.
	 * @return the roles right
	 */
	public Right getPermission();
	/**
	 * Checks if the role has the given {@link Right}. 
	 * @param right a valid right instance
	 * @return <code>true</code> if the role has the given right, 
	 * <code>false</code> otherwise
	 */
	public boolean hasPermission(Right right);
}
