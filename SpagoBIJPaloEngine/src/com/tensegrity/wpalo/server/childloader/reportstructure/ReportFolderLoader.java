/*
*
* @file ReportFolderLoader.java
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
* @version $Id: ReportFolderLoader.java,v 1.13 2010/02/12 13:49:50 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.server.childloader.reportstructure;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.client.serialization.XNode;
import com.tensegrity.wpalo.server.childloader.ChildLoader;

public class ReportFolderLoader implements ChildLoader {

	public boolean accepts(XObject parent) {
		return parent instanceof XNode &&
			parent.getId().equals("ReportStructureNavigatorView#RootNode"); 
	}

	public XObject[] loadChildren(XObject parent, UserSession userSession) {
//		XNode node = (XNode) parent;
//		XUser xUser = node.getUser();
//		AuthUser user = (AuthUser) XObjectMatcher.getNativeObject(xUser);
//		if (user == null) {
//			return null;
//		}
//		
//		FolderService service = ServiceProvider.getFolderService(user);
//		try {
//			ExplorerTreeNode folderRoot = service.getTreeRoot();
//		ExplorerTreeNode [] kids = folderRoot.getChildren();
//		boolean hasKids = kids != null && kids.length > 0;
//		if (folderRoot instanceof DynamicFolder) {
//			Hierarchy hier = ((DynamicFolder) folderRoot).getSourceHierarchy();
//			Subset2 subset = ((DynamicFolder) folderRoot).getSourceSubset();
//			XHierarchy xHier = null;
//			XSubset sub = null;
//			if (hier != null) {
//				xHier = (XHierarchy)XConverter.createX(hier); //WPaloServiceImpl.createXHierarchy(hier, xUser, user);
//			}
//			if (subset != null) {
//				sub = (XSubset) XConverter.createX(subset); //WPaloServiceImpl.createXSubset(subset, xUser, user); 
//			}
//			XDynamicReportFolder root = new XDynamicReportFolder(
//					folderRoot.getName(), folderRoot.getId(), hasKids, false, xHier, sub, xUser);
//			XObjectMatcher.put(root, folderRoot);			
//			return new XObject [] {root};
//		} else if (folderRoot instanceof StaticFolder) {
//			XStaticReportFolder root = new XStaticReportFolder(
//					folderRoot.getName(), folderRoot.getId(), hasKids, false, xUser);
//			XObjectMatcher.put(root, folderRoot);			
//			return new XObject [] {root};			
//		} else if (folderRoot instanceof FolderElement) {
//			String type;
//			Object o = ReportFolderChildLoader.getSourceObjectFromElement(
//					xUser, (FolderElement) folderRoot);
//			if (o instanceof View) {
//				type = XConstants.TYPE_FOLDER_ELEMENT_VIEW;
//			} else {
//				type = XConstants.TYPE_FOLDER_ELEMENT_SHEET;
//			}
//			List <XElement> elements = new ArrayList<XElement>(); 
////				WPaloServiceImpl.retrieveElements(
////					(FolderElement) folderRoot, xUser, user);
//			XReport root = new XReport(folderRoot.getName(), folderRoot.getId(), 
//					type, elements.toArray(new XElement[0]));
//			XObjectMatcher.put(root, folderRoot);
//			return new XObject [] {root};
//		}
//		} catch (Throwable t) {
//			t.printStackTrace();
//		}
//		return null;
		return new XObject[0];
	}
}
