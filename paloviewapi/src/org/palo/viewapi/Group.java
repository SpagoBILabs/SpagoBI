/*
*
* @file Group.java
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
* @version $Id: Group.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi;

import java.util.List;

/**
 * <code>Group</code>
 * <p>
 * This interface is used to represent a group of {@link User}s.
 * A group can be added to {@link Role}s to specify certain permissions for this
 * group and hence for all its members.
 * </p>
 *
 * @version $Id: Group.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public interface Group extends DomainObject {

	/**
	 * Returns the unique name for this group, i.e. no other group with the
	 * same name exists.
	 * @return the group name
	 */
	public String getName();
	/**
	 * Returns a description about this group or <code>null</code> if none was
	 * set.
	 * @return a group description or <code>null</code>
	 */
	public String getDescription();
	/**
	 * Returns a list of {@link User}s which are members of this group
	 * @return all group members
	 */
	public List<User> getUsers();
	/**
	 * Checks if the given {@link User} is a member of this group
	 * @param user a user instance
	 * @return <code>true</code> if the given user is a member of this group,
	 * <code>false</code> otherwise
	 */
	public boolean hasMember(User user);
	/**
	 * Returns a list of {@link Role}s to which this group belongs.
	 * @return all roles to which this group belongs
	 */
	public List<Role> getRoles();
}
