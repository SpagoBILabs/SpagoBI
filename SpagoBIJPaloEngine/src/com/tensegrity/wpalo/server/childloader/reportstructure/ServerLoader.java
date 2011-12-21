/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.server.childloader.reportstructure;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.client.serialization.XNode;
import com.tensegrity.wpalo.server.childloader.ChildLoader;

public class ServerLoader implements ChildLoader {

	public boolean accepts(XObject parent) {
		return parent instanceof XNode &&
			parent.getId().equals("ReportNavigatorView_ListTab#RootNode");
	}

	public XObject[] loadChildren(XObject parent, UserSession userSession) {
//		XNode node = (XNode) parent;
//		XUser xUser = node.getUser();
//		AuthUser user = (AuthUser) XObjectMatcher.getNativeObject(xUser);
//		if (user == null) {
//			return null;
//		}
//		List <XAccount> allAccounts = new ArrayList<XAccount>();
//		for (Account acc: user.getAccounts()) {
//			if (!(acc instanceof PaloAccount)) {
//				continue;
//			}
//			Connection con = ((PaloAccount) acc).login();
//			XAccount xAccount = new XAccount(acc.getId(), acc.getLoginName());
//			PaloConnection p = acc.getConnection();
//			XConnection connection = new XConnection(p.getId(), p.getName(), p.getType());
//			connection.setHost(p.getHost());
//			connection.setService(p.getService());
//			xAccount.setUser(xUser);
////			xAccount.setType(XServer.TYPE);
//			xAccount.setHasChildren(con.getDatabaseCount() > 0);
//			String name = acc.getConnection().getName();
//			if (name == null || name.trim().length() == 0) {
//				name = acc.getConnection().getHost() + ":" + 
//						acc.getConnection().getService();
//			}
//			connection.setName(name);
//			xAccount.setConnection(connection);
//			allAccounts.add(xAccount);
//			XObjectMatcher.put(xAccount, acc);
//		}
//		return allAccounts.toArray(new XAccount[0]);
		return new XObject[0];
	}
}
