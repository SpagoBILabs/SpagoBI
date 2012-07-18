/*
*
* @file SubsetLoader.java
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
* @version $Id: SubsetLoader.java,v 1.9 2010/02/12 13:49:50 PhilippBouillon Exp $
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
