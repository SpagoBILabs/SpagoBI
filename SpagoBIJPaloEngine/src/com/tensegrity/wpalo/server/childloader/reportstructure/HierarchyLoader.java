/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.server.childloader.reportstructure;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.palo.XDatabase;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.client.serialization.XConstants;
import com.tensegrity.wpalo.server.childloader.ChildLoader;

public class HierarchyLoader implements ChildLoader {

	public boolean accepts(XObject parent) {
		return parent instanceof XDatabase &&
			parent.getType().equals(XConstants.TYPE_DATABASE_NO_CUBES);
	}

	public XObject[] loadChildren(XObject parent, UserSession userSession) {
//		XDatabase node = (XDatabase) parent;
//		XUser xUser = null; //node.getUser();
//		AuthUser user = (AuthUser) XObjectMatcher.getNativeObject(xUser);
//		if (user == null) {
//			return null;
//		}
//		Database db = (Database) XObjectMatcher.getNativeObject(node);
//		if (db == null) {
//			return null;
//		}
//		
//		List <XHierarchy> allHierarchies = new ArrayList<XHierarchy>();
//		for (Dimension dim: db.getDimensions()) {
//			if (dim.isSystemDimension()) {
//				continue;
//			}
//			for (Hierarchy hier: dim.getHierarchies()) {
//				if (!hier.isNormal()) {
//					continue;
//				}
//				boolean hasKids = false;
//				Subset2 [] ss2 = hier.getSubsetHandler().getSubsets();
//				hasKids = ss2 != null && ss2.length > 0;
//				XHierarchy xHier = (XHierarchy) XConverter.createX(hier);
////					WPaloServiceImpl.createXHierarchy(hier, xUser, user);
//				xHier.setHasChildren(hasKids);
////				xHier.setIdPath(hier.getDimension().getDatabase().getId(),
////					      hier.getDimension().getId(),
////					      hier.getId());				
////				xHier.setType(XConstants.TYPE_DIMENSION_WITH_SUBSETS);
//				XObjectMatcher.put(xHier, hier);
//				allHierarchies.add(xHier);
//			}
//		}
//		return allHierarchies.toArray(new XHierarchy[0]);
		return new XObject[0];
	}
}
