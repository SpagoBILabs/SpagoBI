/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
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
