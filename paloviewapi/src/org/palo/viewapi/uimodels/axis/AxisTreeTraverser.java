/*
*
* @file AxisTreeTraverser.java
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
* @version $Id: AxisTreeTraverser.java,v 1.6 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.uimodels.axis;

import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.subsets.Subset2;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;



/**
 * <code>AxisTreeTraverser</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AxisTreeTraverser.java,v 1.6 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
class AxisTreeTraverser {

	interface AxisTreeVisitor {
		/** return <code>false</code> to stop traversing */
		public boolean visit(AxisItem item);
	}

	interface AxisTreeHierarchyVisitor {
		/** return <code>false</code> to stop traversing */
		public boolean visit(AxisItem item, AxisItem parentInPrevHier);
	}
	
//	interface AxisTreeFlattenVisitor {
//		public void visit(AxisItem item, int hierIndex);		
//	}
	
	private final Axis axis;
	
	AxisTreeTraverser(Axis axis) {
		this.axis = axis;
	}
	
	
	public final void hierarchiesFirst(AxisItem item,
			AxisTreeVisitor visitor) {
		if(visitor.visit(item)) {
			AxisItem[] roots = item.getRootsInNextHierarchy();
			for(AxisItem root : roots)
				hierarchiesFirst(root, visitor);
		}
		//traverse its children:
		if(item.hasChildren()) {
			AxisItem[] children = item.getChildren();
			for(AxisItem child : children)
				hierarchiesFirst(child, visitor);
		}
	}

	public final boolean traverseHierarchiesFirst(AxisItem item,
			AxisTreeVisitor visitor) {
		boolean goOn = visitor.visit(item);

		if (goOn) {
			AxisItem[] roots = item.getRootsInNextHierarchy();
			for (AxisItem root : roots) {
				goOn = traverseHierarchiesFirst(root, visitor);
				if (!goOn)
					break;
			}
		}
		// traverse its children:
		if (goOn) {

			if (item.hasChildren()) {
				AxisItem[] children = item.getChildren();
				for (AxisItem child : children) {
					goOn = traverseHierarchiesFirst(child, visitor);
					if (!goOn)
						break;
				}
			}
		}
		return goOn;
	}

	public final void createAxisTree(AxisItem item,
			AxisTreeHierarchyVisitor visitor) {
		// next hierarchy?
//		Hierarchy nextHierarchy = getNextHierarchy(item);
		AxisHierarchy nxtHierarchy = getNextAxisHierarchy(item);
		if(nxtHierarchy !=  null) {
			// get roots from next hierarchy:
			int index = 0;
			ElementNode[] rootNodes = nxtHierarchy.getRootNodes();
			for (ElementNode root : rootNodes) {
				AxisItem child = new AxisItem(root, 0);
				child.index = index++;
				visitor.visit(child, item);
				createAxisTree(child, visitor);
			}
		}
//		if (nextHierarchy != null) {
//			// get roots from next hierarchy:
//			Element[] roots = getRoots(nextHierarchy);
//			for (Element root : roots) {
//				// if (isVisible(root)) {
//				AxisItem child = new AxisItem(root, 0);
//				visitor.visit(child, item);
//				createAxisTree(child, visitor);
//				// }
//			}
//		}
	}

	
	public final boolean traverse(AxisItem item, AxisTreeVisitor visitor) {
		boolean goOn = true;
		if(visitor.visit(item)) {
			if(item.hasChildren()) {
				AxisItem[] children = item.getChildren();
				for(AxisItem child : children) {
					goOn = traverse(child, visitor);
					if(!goOn)
						break;
				}
			}
			if(goOn) {
				//next hierarchy:
				AxisItem[] roots = item.getRootsInNextHierarchy();
				for(AxisItem root : roots) {
					goOn = traverse(root, visitor);
					if(!goOn)
						break;
				}
			}
		} else
			goOn = false;
		return goOn;
	}
	
	public final void recreateTree(AxisItem item, AxisItem parentInPrevHier, AxisTreeHierarchyVisitor visitor) {
		if(visitor.visit(item, parentInPrevHier)) {
			if(item.hasChildren()) {
				AxisItem[] children = item.getChildren();
				for(AxisItem child : children)
					recreateTree(child, parentInPrevHier, visitor);
			}
			//next hierarchy:
			AxisItem[] roots = item.getRootsInNextHierarchy();
			for(AxisItem root : roots)
				recreateTree(root, item, visitor);
		}
	}
//	public final AxisItem[][] flatten(AxisTreeModel tree, boolean hideParents) {
//		Hierarchy[] hierarchies = axis.getHierarchies();
//		AxisItem[][] model = new AxisItem[hierarchies.length][];
//		final ArrayList<AxisItem>[] items = new ArrayList[model.length];
//		// we need a visitor:
//		AxisTreeFlattenVisitor visitor = new AxisTreeFlattenVisitor() {
//			public final void visit(AxisItem item, int dimIndex) {
//				if (items[dimIndex] == null)
//					items[dimIndex] = new ArrayList<AxisItem>();
//				items[dimIndex].add(item);
//			}
//		};
//		
//		AxisItem[] roots = tree.getRoots();
//		for (AxisItem root : roots) {
//			flatten(root, 0, visitor, hideParents);
//		}
//		for (int i = 0; i < model.length; ++i)
//			model[i] = items[i] != null ? items[i].toArray(new AxisItem[0])
//					: new AxisItem[0];
//		return model;
//
//	}
	
	
	private final Element[] getRoots(Hierarchy hierarchy) {
		AxisHierarchy axisHierarchy = axis.getAxisHierarchy(hierarchy);
		Subset2 subset = axisHierarchy.getSubset();
		Element[] roots;
		if(subset != null) {
			//we use the subset roots definition:
			ElementNode[] rootNodes = subset.getHierarchy();
			roots = new Element[rootNodes.length];
			for(int i=0;i<rootNodes.length; ++i)
				roots[i] = rootNodes[i].getElement();
		} else {
			//no subset defined, we take the root nodes of first dimension
			roots = hierarchy.getRootElements();
		}
		return roots;
	}
	

	private final AxisHierarchy getNextAxisHierarchy(AxisItem item) {
		AxisHierarchy[] axisHierarchies = axis.getAxisHierarchies();
		Hierarchy itemHierarchy = item.getHierarchy();
		int nextHier = 0;
		for (int i = 0; i < axisHierarchies.length; ++i) {
			nextHier = i + 1;
			if (axisHierarchies[i].getHierarchy().equals(itemHierarchy)
					&& nextHier < axisHierarchies.length) {
				return axisHierarchies[nextHier];
			}
		}
		return null;
	}
//	private final Hierarchy getNextHierarchy(AxisItem item) {
//		Hierarchy [] hierarchies = axis.getHierarchies();
//		Hierarchy hierarchy = item.getHierarchy();
//		int nextHier = 0;
//		for (int i = 0; i < hierarchies.length; ++i) {
//			nextHier = i + 1;
//			if (hierarchies[i].equals(hierarchy) && nextHier < hierarchies.length) {
//				return hierarchies[nextHier];
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * Traverses hierarchy first, then calls the visitor and finally traverses
//	 * the items children.
//	 */
//	private final void flatten(AxisItem item, int hierIndex,
//			AxisTreeFlattenVisitor visitor, boolean hideParents) {
//		// go down dimensions...
//		if (item.hasRootsInNextHierarchy()) {
//			AxisItem[] rootsInNextHier = item.getRootsInNextHierarchy();
//			for (AxisItem root : rootsInNextHier)
//				flatten(root, hierIndex + 1, visitor, hideParents);
//		}
//		if (!hideParents || !item.hasChildren()
//				|| !item.hasState(AxisItem.EXPANDED))
//			visitor.visit(item, hierIndex);
//		// go down children...
//		if (item.hasChildren() && item.hasState(AxisItem.EXPANDED)) {
//			AxisItem[] children = item.getChildren();
//			for (AxisItem child : children)
//				flatten(child, hierIndex, visitor, hideParents);
//		}
//	}

}
