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

import com.google.gwt.user.client.rpc.RemoteService;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.wpalo.client.exceptions.AuthenticationFailedException;
import com.tensegrity.wpalo.client.exceptions.WPaloException;

/**
 * <code>WPaloControllerService</code> TODO DOCUMENT ME
 * 
 * @version $Id: WPaloControllerService.java,v 1.1 2008/10/15 09:57:26
 *          ArndHouben Exp $
 **/
public interface WPaloControllerService extends RemoteService {

	public void stop(String sessionId) throws WPaloException;

	public void start() throws WPaloException;

	public XUser login(String login, String password, String locale)
			throws AuthenticationFailedException;

	public XUser loginHash(String login, String password, String locale)
		throws AuthenticationFailedException;
	
	// //register(new WPaloSessionListener(this));
	// // try to authenticate:
	// try {
	// AuthUser usr = ServiceProvider.getAuthenticationService()
	// .authenticate(login, password);
	// setLoggedInUser(usr);
	// return (XUser)XConverter.createX(usr);
	// } catch (org.palo.viewapi.exceptions.AuthenticationFailedException e) {
	// throw new AuthenticationFailedException(e.getMessage(), e);
	// }
	// }

	public void logout(String sessionId);
	// //AuthUser user = getLoggedInUser();
	// //List<Account> accounts = user.getAccounts();
	// //for(Account account : accounts) {
	// // if(account.isLoggedIn())
	// // account.logout();
	// //}
	// //invalidateSession();
	// //}
	// private final void invalidateSession() {
	// HttpSession session = getSession();
	// session.invalidate();
	// }
	//
	//
}
