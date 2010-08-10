/*
*
* @file ServerLoader.java
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
* @version $Id: ServerLoader.java,v 1.8 2010/02/12 13:49:50 PhilippBouillon Exp $
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
