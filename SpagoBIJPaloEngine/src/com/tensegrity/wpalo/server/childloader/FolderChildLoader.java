/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.server.childloader;

import java.util.ArrayList;
import java.util.List;

import org.palo.viewapi.AuthUser;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.client.serialization.XConstants;

public class FolderChildLoader implements ChildLoader {
	public boolean accepts(XObject parent) {
		String type = parent.getType();
		return type.equals(XConstants.TYPE_ADHOC_TEMPLATES_NODE)
				|| type.equals(XConstants.TYPE_SHEET_TEMPLATES_NODE);
	}

	public XObject[] loadChildren(XObject parent, UserSession userSession) {
//		try {
//		XNode node = (XNode) parent;
//		String type = parent.getType();
//		
//		AuthUser user = 
//			(AuthUser) XObjectMatcher.getNativeObject(node.getUser());
//		if(user == null)
//			return new XObject[0];
//						
//		XObject [] ret;
//		if (type.equals(XConstants.TYPE_ADHOC_TEMPLATES_NODE)) { 
//			ret = getAdhocTemplates(node.getUser(), user);
//		} else {
//			ret = getSheetTemplates(node.getUser(), user);
//		}
//		return ret;
//		} catch (Throwable t) {
//			t.printStackTrace();
//		}
		return new XObject[0];
	}

	private final XObject [] getAdhocTemplates(XUser xUser, AuthUser user) {
		List <XAccount> allAccounts = new ArrayList <XAccount> ();
//		for (Account acc: user.getAccounts()) {						
//			if (acc instanceof PaloAccount) {
//				XAccount node = new XAccount(acc.getId(), acc.getLoginName());
//				node.setPassword(acc.getPassword());
//				PaloConnection p = acc.getConnection();
//				XConnection connection = new XConnection(p.getId(), p.getName(), p.getType());
//				connection.setHost(p.getHost());
//				connection.setService(p.getService());
//				node.setUser(xUser);
////				node.setType(XConstants.TYPE_ACCOUNTS_NODE);
//				node.setHasChildren(ServiceProvider.getViewService(user).hasViews(acc));
//				String name = acc.getConnection().getName();
//				if (name == null || name.trim().length() == 0) {
//					name = acc.getConnection().getHost() + ":" + 
//							acc.getConnection().getService();
//				}
//				connection.setName(name);
//				node.setConnection(connection);
//				allAccounts.add(node);
//				XObjectMatcher.put(node, acc);
//			}
//		}
		return allAccounts.toArray(new XObject[0]);		
	}
	
	private final XObject [] getSheetTemplates(XUser xUser, AuthUser user) {
		List <XAccount> allAccounts = new ArrayList <XAccount>();
//		for (Account acc: user.getAccounts()) {
//			if (acc instanceof WSSAccount) {
//				XAccount node = new XAccount(acc.getId(), acc.getLoginName());
////				node.setType(XConstants.TYPE_WSS_ACCOUNTS_NODE);				
//				node.setUser(xUser);
//				WSSConnection con = ((WSSAccount) acc).login();
//				XConnection xCon = new XConnection(acc.getConnection().getId(), acc.getConnection().getName(), XConnection.TYPE_WSS);
//				xCon.setService(con.getUiService());
//				xCon.setHost(acc.getConnection().getHost());
//				node.setPassword(acc.getPassword());
////				List <WSSApplication> apps = con.getApplicationList();
////				boolean hasKids = apps.size() > 0;
//				node.setHasChildren(true); //hasKids);
//				String name = acc.getConnection().getName();
//				if (name == null || name.trim().length() == 0) {
//					name = acc.getConnection().getHost() + ":" + 
//							acc.getConnection().getService();
//				}
//				xCon.setName(name);
//				node.setConnection(xCon);
//				allAccounts.add(node);
//				XObjectMatcher.put(node, acc);
//			}			
//		}
		return allAccounts.toArray(new XObject[0]);
	}
}
