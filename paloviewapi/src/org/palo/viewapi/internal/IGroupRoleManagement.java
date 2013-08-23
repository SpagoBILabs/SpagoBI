/*
*
* @file IGroupRoleManagement.java
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
* @version $Id: IGroupRoleManagement.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.sql.SQLException;
import java.util.List;

import org.palo.viewapi.Group;
import org.palo.viewapi.Role;

/**
 * <code>IGroupRoleManagement</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: IGroupRoleManagement.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public interface IGroupRoleManagement extends IAssociationManagement {
	
	public List<String> getGroups(Role role) throws SQLException;
	public List<String> getGroups(String roleId) throws SQLException;
	public List<String> getRoles(Group group) throws SQLException;
	public List<String> getRoles(String groupId) throws SQLException;
	public void delete(Group group) throws SQLException;
	public void delete(Role role) throws SQLException;

}
