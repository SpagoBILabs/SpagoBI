/*
*
* @file WPaloAdminServiceProvider.java
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
* @version $Id: WPaloAdminServiceProvider.java,v 1.14 2010/01/13 08:02:41 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.services.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
import com.tensegrity.palo.gwt.core.client.models.admin.XGroup;
import com.tensegrity.palo.gwt.core.client.models.admin.XRole;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.palo.XCube;
import com.tensegrity.palo.gwt.core.client.models.palo.XDatabase;

/**
 * <code>WPaloAdminServiceProvider</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: WPaloAdminServiceProvider.java,v 1.14 2010/01/13 08:02:41 PhilippBouillon Exp $
 **/
public class WPaloAdminServiceProvider implements WPaloAdminServiceAsync {

	private final static WPaloAdminServiceProvider instance = new WPaloAdminServiceProvider();

	private final WPaloAdminServiceAsync proxy;

	private WPaloAdminServiceProvider() {
		proxy = GWT.create(WPaloAdminService.class);
		((ServiceDefTarget) proxy).setServiceEntryPoint(GWT.getModuleBaseURL()
				+ "wpalo-admin-service");
	}

	public static WPaloAdminServiceProvider getInstance() {
		return instance;
	}

	public final void getAccounts(String sessionId, XUser user, AsyncCallback<XAccount[]> callback) {
		proxy.getAccounts(sessionId, user, callback);
	}

	public final void getConnections(String sessionId, XUser user, AsyncCallback<XConnection[]> callback) {
		proxy.getConnections(sessionId, user, callback);
	}

	public final void getGroups(String sessionId, XUser user, AsyncCallback<XGroup[]> callback) {
		proxy.getGroups(sessionId, user, callback);
	}
	
	public final void getGroups(String sessionId, AsyncCallback <XGroup []> callback) {
		proxy.getGroups(sessionId, callback);
	}

	public final void getRoles(String sessionId, XUser user, AsyncCallback<XRole[]> callback) {
		proxy.getRoles(sessionId, user, callback);
	}
	
	public final void getUsers(String sessionId, AsyncCallback<XUser[]> callback) {
		proxy.getUsers(sessionId, callback);
	}

	public final void delete(String sessionId, XObject obj, AsyncCallback<Void> callback) {
		proxy.delete(sessionId, obj, callback);
	}

	public final void saveXObject(String sessionId, XObject obj, AsyncCallback<XObject> callback) {
		proxy.saveXObject(sessionId, obj, callback);		
	}

	public void mayDelete(String sessionId, XObject xObj,
			AsyncCallback<String[]> callback) {
		proxy.mayDelete(sessionId, xObj, callback);
	}

	public void hasRoles(String sessionId, XUser user, String [] roles,
			AsyncCallback<Boolean[]> callback) {
		proxy.hasRoles(sessionId, user, roles, callback);
	}

	public void hasAccount(String sessionId, XConnection con, AsyncCallback<Boolean> callback) {
		proxy.hasAccount(sessionId, con, callback);		
	}

	public void getUsersForConnection(String sessionId, String viewId,
			AsyncCallback<XUser[]> callback) {
		proxy.getUsersForConnection(sessionId, viewId, callback);
	}

	public void getConnection(String sessionId, XView view,
			AsyncCallback<XConnection> callback) {
		proxy.getConnection(sessionId, view, callback);
	}

	public void getCubes(String sessionId, XConnection con, XDatabase db,
			AsyncCallback<XCube[]> callback) {
		proxy.getCubes(sessionId, con, db, callback);
	}

	public void getDatabases(String sessionId, XConnection con,
			AsyncCallback<XDatabase[]> callback) {
		proxy.getDatabases(sessionId, con, callback);
	}

	public void listAccounts(String sessionId, XUser user,
			AsyncCallback<XAccount[]> callback) {
		proxy.listAccounts(sessionId, user, callback);
	}
}
