/*
*
* @file ElementNodeLoader.java
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
* @version $Id: ElementNodeLoader.java,v 1.16 2010/02/16 13:53:42 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.server.childloader;

import java.util.ArrayList;
import java.util.List;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.palo.gwt.core.server.services.cubeview.CubeViewController;

/**
 * <code>ElementNodeLoader</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ElementNodeLoader.java,v 1.16 2010/02/16 13:53:42 PhilippBouillon Exp $
 **/
public class ElementNodeLoader implements ChildLoader {
	private boolean isBusy = false;
	
	public final boolean accepts(XObject parent) {
		return (parent instanceof XAxisHierarchy)
				|| (parent instanceof XElementNode);
	}
	
	public final boolean accepts(String type) {
		return XAxisHierarchy.class.getName().equals(type) ||
		       XElementNode.class.getName().equals(type);
	}

	public XObject[] loadChildren(XObject parent, UserSession userSession) {
		try {
			isBusy = true;
			XObject [] result;
			if (parent instanceof XAxisHierarchy) {
				result = loadElementsFrom(userSession, (XAxisHierarchy) parent);
			}
			else if (parent instanceof XElementNode) {
				result = loadElementsFrom((XElementNode) parent, userSession);
			} else {
				result = new XObject[0];
			}
			return result;
		} finally {
			isBusy = false;
		}
	}

	public XObject [] loadChildren(String parentType, String viewId, String axisId, String parentId, UserSession userSession) {
		if (parentType.equals(XAxisHierarchy.class.getName())) {
			return loadElementsFromHierarchy(userSession, viewId, axisId, parentId);
		} else if (parentType.equals(XElementNode.class.getName())) {
			return loadElementsFromElementNode(userSession, viewId, parentId);
		} else {
			return new XObject[0];
		}
	}
	
	private final XObject [] loadElementsFromHierarchy(UserSession userSession, String viewId, String axisId, String hierarchyId) {
		List<XElementNode> roots = new ArrayList<XElementNode>();
		CubeViewController viewController = getViewController(userSession, viewId);
		if (viewController != null) {
			roots.addAll(viewController.getRoots(axisId, hierarchyId, viewId));
		}
		return roots.toArray(new XElementNode[0]);
	}
	
	private final XObject [] loadElementsFromElementNode(UserSession userSession, String viewId, String elementNodeId) {		
		CubeViewController viewController = getViewController(userSession, viewId);		
		return viewController.loadChildren(elementNodeId).toArray(new XElementNode[0]);
	}
	
	private final XElementNode[] loadElementsFrom(UserSession userSession,
			XAxisHierarchy xHierarchy) {
		List<XElementNode> roots = new ArrayList<XElementNode>();
		CubeViewController viewController = getViewController(userSession,
				xHierarchy.getViewId());
		if (viewController != null) {
			roots.addAll(viewController.getRoots(xHierarchy));
		}
		return roots.toArray(new XElementNode[0]);
	}
	
	private final XElementNode[] loadElementsFrom(XElementNode node,
			UserSession userSession) {
		String viewId = node.getViewId();
		List<XElementNode> children = new ArrayList<XElementNode>();
		CubeViewController viewController = getViewController(userSession,
				viewId);
		if (viewController != null) {
			children.addAll(viewController.loadChildren(node));
		}			
		return children.toArray(new XElementNode[0]);
	}

//	private final CubeViewController getViewController(UserSession userSession,
//			String viewId) {
//		String view = axis.getViewId();
//		return CubeViewController.getController(userSession.getSessionId(), view);
//	}	
	
	private final CubeViewController getViewController(UserSession userSession, String viewId) {
		return CubeViewController.getController(userSession.getSessionId(), viewId);
	}
}
