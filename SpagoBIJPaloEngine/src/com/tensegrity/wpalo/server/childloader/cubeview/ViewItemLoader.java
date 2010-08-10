/*
*
* @file ViewItemLoader.java
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
* @version $Id: ViewItemLoader.java,v 1.6 2009/12/17 16:14:21 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.server.childloader.cubeview;

import java.util.ArrayList;
import java.util.List;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewItem;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.server.childloader.ChildLoader;

/**
 * <code>ViewItemLoader</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewItemLoader.java,v 1.6 2009/12/17 16:14:21 PhilippBouillon Exp $
 **/
public class ViewItemLoader implements ChildLoader {
	
	public boolean accepts(XObject parent) {
		return (parent instanceof XAxisHierarchy)
				|| (parent instanceof XViewItem);
	}

	public XObject[] loadChildren(XObject parent, UserSession userSession) {
		if (parent instanceof XAxisHierarchy)
			return loadRootItemsFrom((XAxisHierarchy) parent);
		else if (parent instanceof XViewItem)
			return loadChildrenFrom((XViewItem) parent);

		// no match
		return new XObject[0];
	}
	
	private final XViewItem[] loadChildrenFrom(XViewItem item) {
		if(item.hasChildren() && !item.isLoaded) {
			//have to load item:
//			load(item);
		}
		List<XViewItem> children = item.getChildren();
		return children.toArray(new XViewItem[0]);
//		Element element = (Element) XObjectMatcher.getNativeObject(xElement);
//		ArrayList<XElement> children = new ArrayList<XElement>();
//		if (element != null) {
//			for (Element child : element.getChildren()) {
//				XElement xEl = find(child);
//				children.add(xEl);
//			}
//		}
//		return children.toArray(new XElement[0]);
	}
	
//	private final void load(XViewItem item) {
//		CubeViewController viewController = getViewController(item.getAxis());
//		viewController.loadItem(item);
//	}
	
	private final XViewItem[] loadRootItemsFrom(
			XAxisHierarchy xAxisHierarchy) {
		List<XViewItem> roots = new ArrayList<XViewItem>();
//		CubeViewController viewController = getViewController(xAxisHierarchy.getAxis());
//		if (viewController != null) {
//			roots.addAll(viewController.getRoots(xAxisHierarchy));
//		} 
//		else {
//			AxisHierarchy axisHierarchy = 
//				(AxisHierarchy) XObjectMatcher.getNativeObject(xAxisHierarchy);
//			if (axisHierarchy != null) {
//				for (Element root : axisHierarchy.getHierarchy().getRootElements()) {
//					XElement xEl = find(root);
//					roots.add(xEl);
//				}
//			}
//		}
		return (XViewItem[])roots.toArray(new XViewItem[0]);
	}
	
//	private final CubeViewController getViewController(XAxis axis) {
//		String view = axis.getView().getId();
//		return CubeViewController.getController(view);
//	}
	
//	private final XElement find(Element el) {
//		XElement xEl = (XElement) XObjectMatcher.find(el);
//		if (xEl == null) {
//			xEl = XObjectFactory.createFromElement(el);
//			XObjectMatcher.put(xEl, el);
//		}
//		return xEl;
//	}
}
