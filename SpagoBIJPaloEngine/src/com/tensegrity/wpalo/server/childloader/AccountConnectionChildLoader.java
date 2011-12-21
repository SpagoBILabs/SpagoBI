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
package com.tensegrity.wpalo.server.childloader;

import java.util.ArrayList;
import java.util.List;

import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.User;
import org.palo.viewapi.services.AdministrationService;
import org.palo.viewapi.services.ServiceProvider;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.server.converter.XConverter;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.client.serialization.XConstants;

/**
 * <code>ConnectionsChildLoader</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AccountConnectionChildLoader.java,v 1.7 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class AccountConnectionChildLoader implements ChildLoader {

	public final boolean accepts(XObject parent) {
		String type = parent.getType();
		return type.equals(XConstants.TYPE_CONNECTIONS_NODE)
				|| type.equals(XConstants.TYPE_ACCOUNTS_NODE);
	}

	public final XObject[] loadChildren(XObject parent, UserSession userSession) {
		AuthUser admin = userSession.getUser();
		if(admin == null)
			return new XObject[0];
		
		String type = parent.getType();		
		AdministrationService adminService =
			ServiceProvider.getAdministrationService(admin);
		
		XObject[] ret;
		
		if(type.equals(XConstants.TYPE_ACCOUNTS_NODE)) 
			ret = getAccounts(adminService);
		else if(type.equals(XConstants.TYPE_CONNECTIONS_NODE))
			ret = getConnections(adminService);
		else
			ret = new XObject[0];
		return ret;
	}

	
	private final XAccount[] getAccounts(AdministrationService adminSrv) {
		List<User> allUsers = adminSrv.getUsers();
		List<XAccount> xAccounts = new ArrayList<XAccount>();
		for (User user : allUsers) {
			XUser xUser = (XUser) XConverter.createX(user);
//			XUser xUser = (XUser) WPaloAdminCache.getXObject(user); //XConverter.createUser(user);
//			WPaloAdminCache.add(xUser, user);
			List<Account> accounts = adminSrv.getAccounts(user);
			for(Account account : accounts) {
				XAccount xAccount = (XAccount) XConverter.createX(account);
//				XAccount xAccount = (XAccount) WPaloAdminCache.getXObject(account); //XConverter.createAccount(account, xUser);
//				WPaloAdminCache.add(xAccount, account);
				xAccounts.add(xAccount);
			}			
		}
		return xAccounts.toArray(new XAccount[0]);
	}
	private final XConnection[] getConnections(AdministrationService adminSrv) {
		List<PaloConnection> connections = adminSrv.getConnections();
		List<XConnection> xConnections = new ArrayList<XConnection>();
		for(PaloConnection connection : connections) {
			XConnection xConnection = (XConnection) XConverter.createX(connection);
//			XConnection xConnection = (XConnection) WPaloAdminCache.getXObject(connection); //XConverter.createConnection(connection);
//			WPaloAdminCache.add(xConnection, connection);
			xConnections.add(xConnection);
		}
		return xConnections.toArray(new XConnection[0]);
	}

}
