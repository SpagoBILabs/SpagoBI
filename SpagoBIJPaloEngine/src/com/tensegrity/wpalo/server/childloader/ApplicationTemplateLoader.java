/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.server.childloader;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.client.serialization.templates.XApplication;

public class ApplicationTemplateLoader implements ChildLoader {
	public boolean accepts(XObject parent) {		
		return parent instanceof XApplication;
	}

	public XObject[] loadChildren(XObject parent, UserSession userSession) {
//		XApplication node = (XApplication) parent;
//		
//		AuthUser user = null;
////			(AuthUser) XObjectMatcher.getNativeObject(node.getUser());
//		if(user == null)
//			return new XObject[0];
//		
//		WSSApplication app = (WSSApplication) XObjectMatcher.getNativeObject(parent);
//		if (app == null) {
//			return new XObject[0];
//		}
//		
//		List <XWorkbook> allWorkbooks = new ArrayList<XWorkbook>();
//		
//		if (node.loadWorkbooks()) {			
//			app.select();
//			List <WSSWorkbook> workbooks = app.getWorkbookList();
//		
//			for (WSSWorkbook w: workbooks) {
//				WSSWorksheet ws = w.getDefaultWorksheet();
//				String wsName = ws == null ? "" : ws.getName();
//				XWorkbook xWork = new XWorkbook(w.getId(), w.getName(), 
//						node, wsName); 
//				allWorkbooks.add(xWork);
//				XObjectMatcher.put(xWork, w);
//			}			
//		}
//		Collections.sort(allWorkbooks, new XObjectComparator());	
//		return allWorkbooks.toArray(new XObject[0]);
		return new XObject[0];
	}

}
