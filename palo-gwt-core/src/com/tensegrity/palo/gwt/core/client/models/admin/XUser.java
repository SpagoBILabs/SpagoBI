/*
*
* @file XUser.java
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
* @version $Id: XUser.java,v 1.6 2010/01/13 08:02:44 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.client.models.admin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tensegrity.palo.gwt.core.client.models.XObject;

/**
 * <code>XUser</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XUser.java,v 1.6 2010/01/13 08:02:44 PhilippBouillon Exp $
 **/
public class XUser extends XObject {

	public static final String TYPE = XUser.class.getName();
	
	private String firstname;
	private String lastname;
	private String password;
	private boolean isAdmin;
	private String sessionId;
	private Set<String> roles = new HashSet<String>();
	private Map<String, String> roleNames = new HashMap<String, String>();
	private Set<String> groups = new HashSet<String>();
	private Set<String> accounts = new HashSet<String>();
	
	
	public XUser() {
	}
	
	public XUser(String id, String login) {
		setId(id);
		setName(login);	
	}
	
	public final void addAccountID(String id) {
		accounts.add(id);
	}

	public final void addGroupID(String id) {
		groups.add(id);
	}
	
	public final void removeGroupID(String id) {
		groups.remove(id);
	}
	
	public final void addRoleID(String id) {
		roles.add(id);
	}	
	
	public final void addRoleName(String id, String name) {
		roleNames.put(id, name);
	}

	public final void clearAccounts() {
		accounts.clear();
	}
	public final void clearGroups() {
		groups.clear();
	}
	public final void clearRoles() {
		roles.clear();
	}
	public final void clearRoleNames() {
		roleNames.clear();
	}
	
	public final String[] getAccountIDs() {
		return accounts.toArray(new String[0]);
	}
	
	public final String getFirstname() {
		return firstname;
	}
	
	public final String[] getGroupIDs() {
		return groups.toArray(new String[0]);
	}
	
	public final String getLastname() {
		return lastname;
	}
	
	public final String getLogin() {
		return getName();
	}
	
	public final String getPassword() {
		return password;
	}
	
	public final String[] getRoleIDs() {
		return roles.toArray(new String[0]);
	}
	
	public final String getRoleName(String id) {
		return roleNames.get(id);
	}
	
	public final boolean hasRoleName(String name) {
		return roleNames.values().contains(name);
	}
	
	public final String getType() {
		return TYPE;
	}

	public final boolean hasChildren() {
		return false;
	}

	public final boolean isAdmin() {
		return isAdmin;
	}
	
	
	public final void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public final void setIsAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	public final void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public final void setLogin(String login) {
		setName(login);
	}
	
	public final void setPassword(String password) {
		this.password = password;
	}
	
	public final void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public final String getSessionId() {
		return sessionId;
	}
}
