/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
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
