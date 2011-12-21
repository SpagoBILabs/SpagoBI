/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.server.childloader.reportstructure;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.palo.XDimension;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.client.serialization.XConstants;
import com.tensegrity.wpalo.server.childloader.ChildLoader;

public class SubsetLoader implements ChildLoader {

	public boolean accepts(XObject parent) {
		return parent instanceof XDimension &&
			parent.getType().equals(XConstants.TYPE_DIMENSION_WITH_SUBSETS);
	}

	public XObject[] loadChildren(XObject parent, UserSession userSession) {
//		XDimension node = (XDimension) parent;
//		XUser xUser = null; //node.getUser();
//		AuthUser user = (AuthUser) XObjectMatcher.getNativeObject(xUser);
//		if (user == null) {
//			return null;
//		}
//		Hierarchy hier = (Hierarchy) XObjectMatcher.getNativeObject(node);
//		if (hier == null) {
//			return null;
//		}
//		
//		List <XSubset> allSubsets = new ArrayList<XSubset>();
//		Subset2 [] ss2 = hier.getSubsetHandler().getSubsets();
//		if (ss2 != null && ss2.length > 0) {
//			for (Subset2 ss: ss2) {
////				XHierarchy xHier = WPaloServiceImpl.createXHierarchy(ss.getDimHierarchy(), xUser, user);
////				XSubset xSub = new XSubset(ss.getId(), ss.getName(), false, xHier, xUser);
////				xSub.setIdPath(ss.getDimHierarchy().getDimension().getDatabase().getId(),
////					      ss.getDimHierarchy().getDimension().getId(),
////					      ss.getDimHierarchy().getId(),
////					      ss.getId());				
////				XObjectMatcher.put(xSub, ss);
////				allSubsets.add(xSub);
//			}
//		}
//		
//		return allSubsets.toArray(new XSubset[0]);
		return new XObject[0];
	}
	
}
