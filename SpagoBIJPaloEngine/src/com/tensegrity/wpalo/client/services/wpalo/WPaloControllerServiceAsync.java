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
package com.tensegrity.wpalo.client.services.wpalo;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;

/**
 * <code>WPaloControllerServiceAsync</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: WPaloControllerServiceAsync.java,v 1.6 2010/02/16 13:53:42 PhilippBouillon Exp $
 **/
public interface WPaloControllerServiceAsync {

	public void stop(String sessionId, AsyncCallback<Void> callback);
	public void start(AsyncCallback<Void> callback);
	public void login(String login, String password, String locale, AsyncCallback<XUser> callback);
	public void loginHash(String login, String password, String locale, AsyncCallback<XUser> callback);
	public void logout(String sessionId, AsyncCallback<Void> callback);
}
