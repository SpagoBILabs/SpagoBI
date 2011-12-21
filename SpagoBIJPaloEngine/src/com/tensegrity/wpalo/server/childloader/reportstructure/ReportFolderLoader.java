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
