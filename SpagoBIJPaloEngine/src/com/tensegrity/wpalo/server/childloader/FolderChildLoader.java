/*
*
* @file FolderChildLoader.java
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
* @version $Id: FolderChildLoader.java,v 1.11 2009/12/17 16:14:20 PhilippBouillon Exp $
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
