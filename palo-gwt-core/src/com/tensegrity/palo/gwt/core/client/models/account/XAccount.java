/*
*
* @file XAccount.java
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
* @version $Id: XAccount.java,v 1.3 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.client.models.account;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;

/**
 * <code>XAccount</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XAccount.java,v 1.3 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public class XAccount extends XObject {

	public static final String TYPE = XAccount.class.getName();
	
	private String password;
	private XUser user;
	private XConnection connection;
//	private boolean loadWBs;
//	private boolean loadTemps;
	
	public XAccount() {		
	}
	
	public XAccount(String id, String login) {
		setId(id);
		setName(login);
//		loadTemps = true;
//		loadWBs = false;
	}
	
	public final XConnection getConnection() {
		return connection;
	}
	
	public final String getLogin() {
		return getName();
	}
	
	public final String getPassword() {
		return password;
	}

	public final String getType() {
		return TYPE;
	}

	public final XUser getUser() {
		return user;
	}
	
	public final void setConnection(XConnection connection) {
		this.connection = connection;
	}
	
	public final void setLogin(String login) {
		setName(login);
	}
	
	public final void setPassword(String password) {
		this.password = password;
	}
	
	public final void setUser(XUser user) {
		this.user = user;
	}
	
//	public void setLoadWorkbooks(boolean lw) {
//		loadWBs = lw;
//	}
//	
//	public final boolean loadWorkbooks() {
//		return loadWBs;
//	}
//	
//	public void setLoadTemplates(boolean lt) {
//		loadTemps = lt;
//	}
//	
//	public final boolean loadTemplates() {
//		return loadTemps;
//	}
}
