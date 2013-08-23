/*
*
* @file GuardedObject.java
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
* @version $Id: GuardedObject.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi;

import java.util.List;

/**
 * A <code>GuardedObject</code> represents a {@link DomainObject} which
 * access permission is controlled by assigned {@link Role}s. That means only
 * those {@link User}s can access this guarded object, which are members of at 
 * least one assigned role. The access permission is determined by the 
 * {@link Right} which is associated with the role.
 *
 * @version $Id: GuardedObject.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public interface GuardedObject extends DomainObject {
	
	/**
	 * Returns a list of {@link Role}s which controls the access of this object.
	 * @return the roles which controls the access
	 */
	public List<Role> getRoles();
	/**
	 * Checks if this <code>GuardedObject</code> is controlled by the given 
	 * {@link Role}.
	 * @param role the role instance to check
	 * @return <code>true</code> if this <code>GuardedObject</code> is controlled
	 * by the given role, <code>false</code> otherwise
	 */
	public boolean hasRole(Role role);
	/**
	 * Returns the owner of this guarded object.
	 * @return the owner of this guarded object
	 */
	public User getOwner();
	/**
	 * Checks if the given {@link User} owns this guarded object
	 * @param user a user instance.
	 * @return <code>true</code> if the given <code>User</code> owns this object,
	 * <code>false</code> otherwise
	 */
	public boolean isOwner(User user);
}
