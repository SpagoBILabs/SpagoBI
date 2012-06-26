/*
*
* @file ApplicationLoader.java
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
* @version $Id: ApplicationLoader.java,v 1.10 2009/12/17 16:14:20 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.server.childloader;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.client.serialization.XConstants;

public class ApplicationLoader implements ChildLoader {
	public boolean accepts(XObject parent) {		
		return parent instanceof XAccount && 
			parent.getType().equals(XConstants.TYPE_WSS_ACCOUNTS_NODE);
	}

	public XObject[] loadChildren(XObject parent, UserSession userSession) {
//		XAccount node = (XAccount) parent;
//		
//		AuthUser user = 
//			(AuthUser) XObjectMatcher.getNativeObject(node.getUser());
//		if(user == null)
//			return new XObject[0];
//		
//		Account acc = (Account) XObjectMatcher.getNativeObject(parent);
//		if (acc == null) {
//			return new XObject[0];
//		}
//
//		WSSConnection con = ((WSSAccount) acc).login();
//		List <XApplication> allApps = new ArrayList<XApplication>();
//		List <XTemplate> allTemplates = new ArrayList<XTemplate>();
////		boolean loadWBs = node.loadWorkbooks();
////		boolean loadTemps = node.loadTemplates();
//		for (WSSTemplate temp: con.getTemplateList()) {
//			XTemplate xtemp = new XTemplate(temp.getId(), temp.getName(), node.getUser(), node);
//			XObjectMatcher.put(xtemp, temp);
//			allTemplates.add(xtemp);
//		}
//		Collections.sort(allTemplates, new XObjectComparator());
////		if (node.loadWorkbooks()) {
////			for (WSSApplication app: con.getApplicationList()) {
////				XApplication nd = new XApplication(node.getUser(), node,
////						app.getId(), loadWBs, loadTemps);			
////		
////				app.select();			
////				nd.setName(app.getName());
////				List <WSSWorkbook> workbooks = app.getWorkbookList();
////				nd.setHasChildren(workbooks.size() > 0);
////				allApps.add(nd);
////				XObjectMatcher.put(nd, app);			
////			}
////		}
//		Collections.sort(allApps, new XObjectComparator());	
//		List <XObject> allKids = new ArrayList<XObject>(allTemplates);
//		allKids.addAll(allApps);
//		return allKids.toArray(new XObject[0]);
		return new XObject[0];
	}
}
