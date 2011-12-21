/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.services.admin;

import com.google.gwt.user.client.rpc.AsyncCallback;
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
 * <code>WPaloAdminServiceAsync</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: WPaloAdminServiceAsync.java,v 1.14 2010/01/13 08:02:41 PhilippBouillon Exp $
 **/
public interface WPaloAdminServiceAsync {
	
	public void getUsers(String sessionId, AsyncCallback<XUser[]> callback);
	public void getGroups(String sessionId, XUser user, AsyncCallback<XGroup[]> callback);
	public void getGroups(String sessionId, AsyncCallback<XGroup[]> callback);
	public void getRoles(String sessionId, XUser user, AsyncCallback<XRole[]> callback);
	public void getAccounts(String sessionId, XUser user, AsyncCallback<XAccount[]> callback);
	public void listAccounts(String sessionId, XUser user, AsyncCallback<XAccount[]> callback);
	public void getConnections(String sessionId, XUser user, AsyncCallback<XConnection[]> callback);

	public void delete(String sessionId, XObject xObj, AsyncCallback<Void> callback);
	public void mayDelete(String sessionId, XObject xObj, AsyncCallback <String []> callback);
	public void saveXObject(String sessionId, XObject xObj, AsyncCallback<XObject> callback);

	public void hasRoles(String sessionId, XUser user, String [] roles, AsyncCallback <Boolean []> callback);
	public void hasAccount(String sessionId, XConnection con, AsyncCallback <Boolean> callback);
	
	public void getUsersForConnection(String sessionId, String viewId, AsyncCallback <XUser []> callback);
	public void getConnection(String sessionId, XView view, AsyncCallback <XConnection> callback);
	public void getDatabases(String sessionId, XConnection con, AsyncCallback <XDatabase []> callback);
	public void getCubes(String sessionId, XConnection con, XDatabase db, AsyncCallback <XCube []> callback);
}
