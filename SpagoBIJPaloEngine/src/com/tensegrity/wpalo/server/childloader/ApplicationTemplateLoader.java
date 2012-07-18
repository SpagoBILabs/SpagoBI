/*
*
* @file ApplicationTemplateLoader.java
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
* @version $Id: ApplicationTemplateLoader.java,v 1.9 2009/12/17 16:14:20 PhilippBouillon Exp $
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
