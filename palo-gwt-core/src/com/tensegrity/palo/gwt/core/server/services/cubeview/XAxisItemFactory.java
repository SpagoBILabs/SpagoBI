/*
*
* @file XAxisItemFactory.java
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
* @version $Id: XAxisItemFactory.java,v 1.8 2010/03/11 10:42:20 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview;

import org.palo.viewapi.uimodels.axis.AxisItem;

import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisItem;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementType;

/**
 * <code>XViewItemFactory</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XAxisItemFactory.java,v 1.8 2010/03/11 10:42:20 PhilippBouillon Exp $
 **/
class XAxisItemFactory {
	
	static final XAxisItem createXRoot(AxisItem root) {
		return createX(root, 0, 0, null);
	}
	static final XAxisItem createX(AxisItem root, int level, int depth, XAxisItem parent) {
		return internalCreateX(root, level, depth, parent);
	}

	private static final XAxisItem internalCreateX(AxisItem forItem, int level,
			int depth, XAxisItem parent) {
		String hierarchyId = forItem.getHierarchy().getId();
		XAxisItem viewItem = new XAxisItem(forItem.getPath(), forItem.getName(), hierarchyId);
		viewItem.index = forItem.index;
		viewItem.depth = depth;
		viewItem.level = level;
		viewItem.elementType = 
			XElementType.fromString(forItem.getElement().getTypeAsString());
		viewItem.setHasChildren(forItem.hasChildren());
		viewItem.isExpanded = forItem.hasState(AxisItem.EXPANDED);
		viewItem.isLoaded = forItem.hasState(AxisItem.CACHED);
		// roots in next hierarchy:
		if (forItem.hasRootsInNextHierarchy()) {
			for (AxisItem root : forItem.getRootsInNextHierarchy())
				viewItem.addRootInNextHierarchy(internalCreateX(root, level + 1,
						0, null));
		}
		
		// children
		for (AxisItem child : forItem.getChildren()) {
			viewItem.addChild(internalCreateX(child, level, depth + 1, null));
		}

		if (parent != null) {
			parent.addChild(viewItem);
		}
		return viewItem;
	}
}
