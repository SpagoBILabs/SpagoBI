/*
*
* @file ViewModelUtils.java
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
* @version $Id: ViewModelUtils.java,v 1.5 2010/02/12 13:50:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview.util;

import java.util.ArrayList;
import java.util.List;

import org.palo.viewapi.uimodels.axis.AxisItem;
import org.palo.viewapi.uimodels.axis.AxisModel;

/**
 * <code>ViewModelUtils</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewModelUtils.java,v 1.5 2010/02/12 13:50:49 PhilippBouillon Exp $
 **/
public class ViewModelUtils {

	private static int leafIndex;

	public static final List<AxisItem> getLeafs(AxisModel axis) {
		return getLeafs(axis.getRoots());
	}
	public static final List<AxisItem> getLeafs(AxisItem[] items) {
		final List<AxisItem> leafs = new ArrayList<AxisItem>();
		AxisItemVisitor visitor = new AxisItemVisitor() {
			public void visit(AxisItem item, AxisItem parent,
					AxisItem parentInPrevHierarchy) {
				if(!item.hasRootsInNextHierarchy()) {					
					leafs.add(item);
				} 			
			}
		};
		AxisTraverser traverser = new AxisTraverser();
		traverser.traverse(items, visitor);
		return leafs;
	}

	public static final List<AxisItem> getVisibleLeafs(AxisModel axis) {
		return getVisibleLeafs(axis.getRoots());
	}
	
	private final static void deepAmend(AxisItem [] roots, AxisItem child) {
		for (AxisItem i: roots) {
			AxisItem copy = i.copy();
			if (i.hasRootsInNextHierarchy()) {
				deepAmend(i.getRootsInNextHierarchy(), copy);
			}
			child.addRootInNextHierarchy(copy);
			copy.setParentInPreviousHierarchy(child);
		}		
	}
		
	public static final List<AxisItem> getVisibleLeafs(AxisItem[] items) {
		AxisItemVisitor visitor1 = new AxisItemVisitor() {
			public void visit(AxisItem item, AxisItem parent,
					AxisItem parentInPrevHierarchy) {
				if (item.hasChildren() && item.hasState(AxisItem.EXPANDED)) {
					for (AxisItem child : item.getChildren()) {
						if (item.hasRootsInNextHierarchy() && !child.hasRootsInNextHierarchy()) {
							AxisItem [] items = item.getRootsInNextHierarchy();
							deepAmend(items, child);
						}
						if (item.getParentInPrevHierarchy() != null && child.getParentInPrevHierarchy() == null) {
							child.setParentInPreviousHierarchy(item.getParentInPrevHierarchy());
						}
					}
				}
			}
		};
		AxisTraverser traverser1 = new AxisTraverser();
		traverser1.traverseVisible(items, visitor1);
		
		final List<AxisItem> leafs = new ArrayList<AxisItem>();
		AxisItemVisitor visitor = new AxisItemVisitor() {
			public void visit(AxisItem item, AxisItem parent,
					AxisItem parentInPrevHierarchy) {
				if(!item.hasRootsInNextHierarchy()) {
					leafs.add(item);
				}
			}
		};
		AxisTraverser traverser = new AxisTraverser();
		traverser.traverseVisible(items, visitor);
		return leafs;
	}
	
	public static final void resetAxis(AxisModel axis) {
		AxisItemVisitor visitor = new AxisItemVisitor() {
			public void visit(AxisItem item, AxisItem parent,
					AxisItem parentInPrevHierarchy) {
//				if(!item.hasState(AxisItem.EXPANDED)) {
//					item.removeChildren();
//				}
			}
		};
		//traverse complete axis:
		AxisItem[] roots = axis.getRoots();
		AxisTraverser traverser = new AxisTraverser();
		traverser.traverse(roots, visitor);
	}
	public static final void determineLeafIndexes(AxisModel axis) {
		leafIndex = 0;
		AxisItemVisitor visitor = new AxisItemVisitor() {
			public void visit(AxisItem item, AxisItem parent,
					AxisItem parentInPrevHierarchy) {
				if(!item.hasRootsInNextHierarchy()) {
					item.leafIndex = leafIndex++;
				}
			}
		};
		//traverse complete axis:
		AxisItem[] roots = axis.getRoots();
		AxisTraverser traverser = new AxisTraverser();
		traverser.traverse(roots, visitor);
	}
}
