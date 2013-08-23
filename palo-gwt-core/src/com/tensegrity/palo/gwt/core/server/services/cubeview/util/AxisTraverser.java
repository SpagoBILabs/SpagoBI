/*
*
* @file AxisTraverser.java
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
* @version $Id: AxisTraverser.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview.util;

import java.util.List;

import org.palo.viewapi.uimodels.axis.AxisItem;

import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisItem;

/**
 * <code>XAxisItemTraverser</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AxisTraverser.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public class AxisTraverser {

	public void traverseVisible(AxisItem[] roots, AxisItemVisitor visitor) {
		for(AxisItem item : roots) {
			traverseVisible(item, null, null, visitor);
		}
	}
	
	public void traverseVisible(AxisItem item, AxisItem parent, AxisItem parentInPrevHierarchy, AxisItemVisitor visitor) {
		visitor.visit(item, parent, parentInPrevHierarchy);
		for(AxisItem rootInNextHierarchy : item.getRootsInNextHierarchy()) {
			traverseVisible(rootInNextHierarchy, null, item, visitor);
		}
		if (item.hasChildren() && item.hasState(AxisItem.EXPANDED)) {
			for (AxisItem child : item.getChildren()) {
				traverseVisible(child, item, parentInPrevHierarchy, visitor);
			}
		}
	}


	public void traverse(AxisItem[] roots, AxisItemVisitor visitor) {
		for(AxisItem item : roots) {
			traverse(item, null, null, visitor);
		}
	}
	
	public void traverse(AxisItem item, AxisItem parent, AxisItem parentInPrevHierarchy, AxisItemVisitor visitor) {
		visitor.visit(item, parent, parentInPrevHierarchy);
		for(AxisItem rootInNextHierarchy : item.getRootsInNextHierarchy()) {
			traverse(rootInNextHierarchy, null, item, visitor);
		}
		for(AxisItem child : item.getChildren()) {
			traverse(child, item, parentInPrevHierarchy, visitor);
		}
	}

	public void traverse(List<XAxisItem> roots, XAxisItemVisitor visitor) {
		for(XAxisItem item : roots) {
			traverse(item, null, null, visitor);
		}
	}
	
	public void traverse(XAxisItem item, XAxisItem parent, XAxisItem parentInPrevHierarchy, XAxisItemVisitor visitor) {
		visitor.visit(item, parent, parentInPrevHierarchy);
		for(XAxisItem rootInNextHierarchy : item.getRootsInNextHier()) {
			traverse(rootInNextHierarchy, null, item, visitor);
		}
		for(XAxisItem child : item.getChildren()) {
			traverse(child, item, parentInPrevHierarchy, visitor);
		}
	}
}