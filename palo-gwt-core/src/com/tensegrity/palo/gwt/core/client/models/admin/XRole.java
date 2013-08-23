/*
*
* @file XRole.java
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
* @version $Id: XRole.java,v 1.5 2010/03/02 08:58:27 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.client.models.admin;

import com.tensegrity.palo.gwt.core.client.models.XObject;

/**
 * <code>XRole</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XRole.java,v 1.5 2010/03/02 08:58:27 PhilippBouillon Exp $
 **/
public class XRole extends XObject {

	public static final String TYPE = XRole.class.getName();
	
	//role properties:
	private String right;
	private String description;
//	private Set<String> users = new HashSet<String>();
//	private Set<String> groups = new HashSet<String>();
		
	public XRole() {		
	}
	
	public XRole(String id, String name) {
		setId(id);
		setName(name);
	}
	
//	public final void addGroupID(String id) {
//		groups.add(id);
//	}
//	
//	public final void addUserID(String id) {
//		users.add(id);
//	}	
//
//	public final void clearGroups() {
//		groups.clear();
//	}
//	public final void clearUsers() {
//		users.clear();
//	}
//
//	public final String[] getGroupIDs() {
//		return groups.toArray(new String[0]);
//	}
//
//	public final String[] getUserIDs() {
//		return users.toArray(new String[0]);
//	}

	public final String getType() {
		return TYPE;
	}

	public final String getDescription() {
		return description;
	}
	
//	public final XGroup[] getGroups() {
//		if(groups == null)
//			return new XGroup[0];
//		return groups;
//	}
	
	public final String getPermission() {
		return right;
	}
	
//	public final XUser[] getUsers() {
//		if(users == null)
//			return new XUser[0];
//		return users;
//	}
//	
//	public final void setGroups(XGroup[] groups) {
//		this.groups = groups;
//	}
	
	public final void setDescription(String description) {
		this.description =  description;
	}
	
	public final void setPermission(String right) { //XRight right) {
		this.right = right;
	}
	
//	public final void setUsers(XUser[] users) {
//		this.users = users;
//	}
}
