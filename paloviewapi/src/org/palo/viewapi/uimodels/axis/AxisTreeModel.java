/*
*
* @file AxisTreeModel.java
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
* @version $Id: AxisTreeModel.java,v 1.16 2010/03/11 10:42:19 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.uimodels.axis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.palo.api.Dimension;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.utils.ElementPath;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.uimodels.axis.AxisTreeTraverser.AxisTreeHierarchyVisitor;
import org.palo.viewapi.uimodels.axis.AxisTreeTraverser.AxisTreeVisitor;
import org.palo.viewapi.uimodels.axis.events.AxisModelEvent;


/**
 * <code>AxisTree</code>
 * <p>
 * An <code>AxisTree</code> is a controller for an {@link Axis}
 * </p>
 * TODO DOCUMENT ME
 *
 * @version $Id: AxisTreeModel.java,v 1.16 2010/03/11 10:42:19 PhilippBouillon Exp $
 **/
public class AxisTreeModel extends AbstractAxisModel {

//	public static final int HIDE_PARENTS = 1;
	
//	private final AxisModelFilter filter;
	private final AxisTreeTraverser traverser;	
	protected final Set<AxisItem> roots = new LinkedHashSet<AxisItem>();
	private final Set<String> expandedNodes = new LinkedHashSet<String>();
	
	public AxisTreeModel(Axis axis) {
		super(axis);
		this.traverser = new AxisTreeTraverser(axis);
//		this.filter = new AxisModelFilter(axis);
		init();
	}
	
	public final Hierarchy [] getHierarchies() {
		return axis.getHierarchies();
	}	
	
	public final int getAxisHierarchyCount() {
		return axis.getHierarchies().length;
	}
	
	public final AxisItem[] getRoots() {
		return roots.toArray(new AxisItem[0]);		
	}

	/**
	 * Returns the {@link AxisItem} which is referenced by the given 
	 * {@link ElementPath} or <code>null</code> if no such item could be found 
	 * in current model state. <b>Note:</b> if several items apply to the given 
	 * path only the first matching one is returned.
	 * 
	 * @param path an <code>ElementPath</code>
	 * @return the corresponding referenced <code>AxisItem</code> or 
	 * <code>null</code> if no matching item could be found
	 */
	public final AxisItem getItem(ElementPath path) {
		if(path == null)
			return null;
				
		return getItem(path.toString());
	}

	public final AxisItem getItem(final String path) {
		if(path == null)
			return null;
		final AxisItem[] items = new AxisItem[]{null};
		//we simply traverse the hierarchy until item is found...
		AxisTreeVisitor visitor = new AxisTreeVisitor() {
			public final boolean visit(AxisItem item) {
				String _path = item.getPath();
				if(path.equals(_path)) {
					items[0] = item;
					return false; //stop traversing
				}
				return true;
			}
		};		
		//we traverse axis
		for(AxisItem root : roots)
			traverser.traverse(root, visitor);
		return items[0];
	}
	public final AxisItem getItem(final String path, final int index) {
		if(path == null)
			return null;
		final AxisItem[] items = new AxisItem[]{null};
		//we simply traverse the hierarchy until item is found...
		AxisTreeVisitor visitor = new AxisTreeVisitor() {
			public final boolean visit(AxisItem item) {
				String _path = item.getPath();
				if(path.equals(_path)) {
					boolean foundIt = true;
					//optionally we check index:
					if(index != -1)
						foundIt = item.index == index;
					if (foundIt) {
						items[0] = item;
						return false; // stop traversing
					}
				}
				return true;
			}
		};		
		//we traverse axis
		for(AxisItem root : roots)
			traverser.traverse(root, visitor);
		return items[0];
	}

	public final AxisItem findItem(final String pathExpression,
			final String hierarchyID) {
		if (pathExpression == null)
			return null;
		final AxisItem[] items = new AxisItem[] { null };
		// we simply traverse the hierarchy until item is found...
		AxisTreeVisitor visitor = new AxisTreeVisitor() {
			public final boolean visit(AxisItem item) {
				String _path = item.getPath();
				String _hierID = item.getHierarchy().getId();
				if (_hierID.equals(hierarchyID)
						&& _path.matches(pathExpression)) {
					items[0] = item;
					return false; // stop traversing
				}
				return true;
			}
		};
		// we traverse axis
		for (AxisItem root : roots) {
			if(!traverser.traverseHierarchiesFirst(root, visitor))
				break;
		}
		return items[0];
	}
	public final AxisItem findItem(final String pathExpression, final int index,
			final String hierarchyID) {
		if (pathExpression == null)
			return null;
		final AxisItem[] items = new AxisItem[] { null };
		// we simply traverse the hierarchy until item is found...
		AxisTreeVisitor visitor = new AxisTreeVisitor() {
			public final boolean visit(AxisItem item) {
				String _path = item.getPath();
				String _hierID = item.getHierarchy().getId();
				if (_hierID.equals(hierarchyID)
						&& _path.matches(pathExpression)) {
					boolean foundIt = true;
					//optionally we check index:
					if(index != -1)
						foundIt = item.index == index;
					if (foundIt) {
						items[0] = item;
						return false; // stop traversing
					}
				}
				return true;
			}
		};
		// we traverse axis
		for (AxisItem root : roots) {
			if(!traverser.traverseHierarchiesFirst(root, visitor))
				break;
		}
		return items[0];
	}
	
	public final int getMaximumLevel(final Hierarchy hierarchy) {
		final int[] maxLevel = new int[]{0};
		AxisTreeVisitor visitor = new AxisTreeVisitor() {
			public final boolean visit(AxisItem item) {
				if(item.getHierarchy().equals(hierarchy)) {
					int lvl = item.getLevel();
					if(maxLevel[0] < lvl)
						maxLevel[0] = lvl;
				}
				return true;
			}
		};
		//we traverse up to hierarchy
		for(AxisItem root : roots)
			traverser.hierarchiesFirst(root, visitor);
		return maxLevel[0];
	}

	/**
	 * Retrieves the absolute maximum level of the hierarchy. Invisible levels created by hiding 
	 * elements will also be counted. The value corresponds to the element level.
	 * @param hierarchy
	 * @return
	 */
	public final int getMaximumAbsoluteDepth(final Hierarchy hierarchy) {
		final int[] maxLevel = new int[]{0};
		AxisTreeVisitor visitor = new AxisTreeVisitor() {
			public final boolean visit(AxisItem item) {
				if(item.getHierarchy().equals(hierarchy)) {
					int lvl = item.getElement().getDepth();
					if(maxLevel[0] < lvl)
						maxLevel[0] = lvl;
				}
				return true;
			}
		};
		//we traverse up to hierarchy
		for(AxisItem root : roots)
			traverser.hierarchiesFirst(root, visitor);
		return maxLevel[0];
	}

	public final AxisItem getFirstSibling(AxisItem item) {
		return getSiblings(item)[0];
	}
	
	/**
	 * Returns the next sibling of the given {@link AxisItem} or 
	 * <code>null</code> if the specified item ist last sibling
	 * @param item the {@link AxisItem} to get the next sibling for
	 * @return the next sibling item or <code>null</code>
	 */
	public final AxisItem getNextSibling(AxisItem item) {
		AxisItem[] siblings = getSiblings(item);
		for(int i=0;i<siblings.length; ++i) {
			if(siblings[i].equals(item)) {
				int next = i+1;
				if(next < siblings.length)
					return siblings[next];
				break;
			}
		}
		return null;
	}
	
	public final AxisItem getLastSibling(AxisItem item) {
		AxisItem[] siblings = getSiblings(item);
		return siblings.length > 0 ? siblings[siblings.length - 1] : item;
	}
	
	public final AxisItem getLastChildInNextHierarchy(AxisItem item) {
		AxisItem lastChild = null;
		AxisItem[] rootsInNextHier = item.getRootsInNextHierarchy();
		if(rootsInNextHier.length > 0 ) {
			AxisItem lastRoot = rootsInNextHier[rootsInNextHier.length - 1];
			//have to traverse until last is reached
			lastChild = getLastChild(lastRoot);
		}
		return lastChild;
	}
	
	public final AxisItem getFirstChildInNextHierarchy(AxisItem item) {
		if(item.hasRootsInNextHierarchy())
			return item.getRootsInNextHierarchy()[0];
		return null;
	}
	
	public final AxisItem[] getSiblings(AxisItem item) {
		AxisItem parent = item.getParent();
		if(parent != null)
			return parent.getChildren();
		else {
			//check previous dimension:
			parent = item.getParentInPrevHierarchy();
			if(parent != null)
				return parent.getRootsInNextHierarchy();
		}
		//no parent available, we are in top dimension...
		return roots.toArray(new AxisItem[0]);
	}

	//TODO expand/collapse create/deletes AxisItems every time => caching!!??
	public final void expand(AxisItem item) {
		if(item == null)
			return;
		
//		int itemsCount = willExpand(item);
		AxisModelEvent ev = notifyWillExpand(item); //, itemsCount);
		if(ev.doit) {
			AxisItem[] items = expandInternal(item);
			notifyExpanded(item, new AxisItem[][] { items });
		}
	}
	
	public final void collapse(AxisItem item) {
		if(item == null)
			return;

//		int itemsCount = willCollapse(item);
		AxisModelEvent ev = notifyWillCollapse(item); //, itemsCount);
		if(ev.doit) {
			AxisItem[] items = collapseInternal(item);			
			notifyCollapsed(item, new AxisItem[][] { items });
		}
	}

	public void showAllParents(boolean b) {	
		//rebuild tree structure and fire structure changed event...
		if(b)
			showParents();
		else
			hideParents();
		notifyStructureChange();
	}
	
	public final void expandAll() {		
		expandAllInternal(true);
	}

	public final void expandAll(boolean doItIterative) {
		if(doItIterative)
			expandAllIterative(true);
		else
			expandAllInternal(true);
	}

	public final void collapseAll() {
		AxisTreeVisitor visitor = new AxisTreeVisitor() {
			public final boolean visit(AxisItem item) {
				item.deleteState(AxisItem.EXPANDED);
				//cut off tree...
				item.removeChildren();
				return true;
			}
		};
		for (AxisItem root : roots)			
			traverser.traverse(root, visitor);
		
		notifyStructureChange();
	}
	
	public final void refresh() {
		init();
	}
	protected final void init() {
		AxisTreeHierarchyVisitor visitor = new AxisTreeHierarchyVisitor() {
			public final boolean visit(AxisItem item, AxisItem parentInPrevHier) {
				if(parentInPrevHier != null) {
					parentInPrevHier.addRootInNextHierarchy(item);
					item.setParentInPreviousHierarchy(parentInPrevHier);
				}
				return true;
			}
		};
		
		roots.clear();
		expandedNodes.clear();
		//the tree roots are the roots elements of first dimension:
		AxisHierarchy[] hierarchies = axis.getAxisHierarchies();
		if(hierarchies.length == 0)
			return;

		int topHierarchy = 0;
		ElementNode[] rootNodes = hierarchies[topHierarchy].getRootNodes();
		int index = 0;
		for (ElementNode root : rootNodes) {
			AxisItem iRoot = new AxisItem(root, 0);
			iRoot.index = index++;
			roots.add(iRoot);
			traverser.createAxisTree(iRoot, visitor);
		}
		//expand items:
		applyExpanded(axis.getExpandedPaths());
	}

	public final String [] getExpandedNodes() {
		return expandedNodes.toArray(new String[0]);
	}
	
	public final void expandTheseNodes(String [] nodesToExpand) {				
		if (nodesToExpand == null) {
			return;
		}
		final HashSet <String> allExpandedPaths = new HashSet<String>();
		for (String exp: nodesToExpand) {
			allExpandedPaths.add(exp);
		}
		AxisTreeVisitor visitor = new AxisTreeVisitor() {
			public final boolean visit(AxisItem item) {
				if (allExpandedPaths.isEmpty()) {
					return false;
				}
				if (allExpandedPaths.contains(item.getPath())) {
					expandInternal(item);
					allExpandedPaths.remove(item.getPath());
				}
				return true;
			}
		};
		for (AxisItem root : roots)			
			traverser.traverse(root, visitor);

	}
	
	//--------------------------------------------------------------------------
	// PRIVATE METHODS
	//
	private final AxisItem getLastChild(AxisItem item) {
		if(item.hasChildren() && item.hasState(AxisItem.EXPANDED)) {
			AxisItem[] children = item.getChildren();
			return getLastChild(children[children.length-1]);
		}
		return item;
	}
		
	private final void applyExpanded(ElementPath[] expanded) {
		if(expanded.length < 1)
			return;
		//sort expanded paths before:
		ArrayList<ElementPath> paths = new ArrayList<ElementPath>();
		paths.addAll(Arrays.asList(expanded));
		Collections.sort(paths, new ElementPathComparator());
		for(ElementPath path : paths) {
			AxisItem item = getItem(path); //findItemAfter(lastItem,path);
			if(item != null) {
				expandInternal(item);
			} 
		}
	}
	
	private final void hideParents() {
		final ArrayList<AxisItem> rootItems = new ArrayList<AxisItem>();
		AxisTreeHierarchyVisitor visitor = new AxisTreeHierarchyVisitor() {
			public boolean visit(AxisItem item, AxisItem parentInPrevHier) {
				//we only take leaves or not expanded items:
				if(item.hasChildren() && item.hasState(AxisItem.EXPANDED)) {
					//remove ourself from previous hierarchy:
					if (parentInPrevHier != null) {
						parentInPrevHier.replaceRootInNextHierarchy(item, item
								.getChildren());
					}
				} else {
					if(parentInPrevHier == null)
						rootItems.add(item);
				}
				return true;
			}
		};
		for (AxisItem root : roots)			
			traverser.recreateTree(root, null, visitor);
		if (!rootItems.isEmpty()) {
			roots.clear();
			roots.addAll(rootItems);
		}
	}
	private final void showParents() {
		final ArrayList<AxisItem> rootItems = new ArrayList<AxisItem>();
		AxisTreeHierarchyVisitor visitor = new AxisTreeHierarchyVisitor() {
			public boolean visit(AxisItem item, AxisItem parentInPrevHier) {
				//we only take leaves or not expanded items:
				if(item.hasChildren() && item.hasState(AxisItem.EXPANDED)) {
					//remove ourself from previous hierarchy:
					if (parentInPrevHier != null) {
						parentInPrevHier.replaceRootInNextHierarchy(item, item
								.getChildren());
					}
				} else {
					if(parentInPrevHier == null)
						rootItems.add(item);
				}
				return true;
			}
		};
		for (AxisItem root : roots)			
			traverser.recreateTree(root, null, visitor);
		if (!rootItems.isEmpty()) {
			roots.clear();
			roots.addAll(rootItems);
		}

		
		roots.clear();
		// rebuild structure...
		init();
	}
	
	private final AxisItem[] expandInternal(AxisItem item) {
		item.setState(AxisItem.EXPANDED);
		expandedNodes.add(item.getPath());
		if (item.update()) { // does nothing if it is already cached...
			AxisItem[] children = item.getChildren();
			// create new subtree...
			for (AxisItem child : children) {
				if (expandedNodes.contains(child.getPath())) {
					expandInternal(child);
				}
				child.setParentInPreviousHierarchy(item
						.getParentInPrevHierarchy());
				if (item.hasRootsInNextHierarchy()) {
					AxisTreeHierarchyVisitor visitor = new AxisTreeHierarchyVisitor() {
						public final boolean visit(AxisItem item,
								AxisItem parentInPrevHier) {
							if (parentInPrevHier != null) {
								parentInPrevHier.addRootInNextHierarchy(item);
								item
										.setParentInPreviousHierarchy(parentInPrevHier);
							}
							return true;
						}
					};
					traverser.createAxisTree(child, visitor);
				}
			}
			return children;
		}
		return item.getChildren();
	}
	
	private final AxisItem[] collapseInternal(AxisItem item) {
		expandedNodes.remove(item.getPath());
		item.deleteState(AxisItem.EXPANDED);
		AxisItem[] children = item.getChildren();
		//cut off tree...
//		item.removeChildren();
		return children;
	}
	
	private final void expandAllInternal(boolean doEvents) {
		if(true)
			expandAllIterative(doEvents);
		
		//TODO fire will expand all ;)
		AxisTreeVisitor visitor = new AxisTreeVisitor() {
			public final boolean visit(AxisItem item) {
				if (item.hasChildren() && !item.hasState(AxisItem.EXPANDED))
					expandInternal(item);
				return true;
			}
		};
		for (AxisItem root : roots)			
			traverser.traverse(root, visitor);
		if(doEvents)
			notifyStructureChange();
	}
	
	private final void expandAllIterative(boolean doEvents) {
		LinkedList<AxisItem> stack = new LinkedList<AxisItem>();
//		Stack stack = new Stack();	
		
		//add roots to the stack:
		for (AxisItem root : roots)
			stack.add(root);

		AxisItem item;
		while (!stack.isEmpty()) {
			item = (AxisItem)stack.poll(); //pop(); //pollFirst();
			if (item.hasChildren() && !item.hasState(AxisItem.EXPANDED))
				expandInternal(item);
			if (item.hasChildren()) {
				for (AxisItem child : item.getChildren()) {
					stack.add(child);
				}
			}
			if (item.hasRootsInNextHierarchy()) {
				for (AxisItem root : item.getRootsInNextHierarchy()) {
					stack.add(root);
				}
			}
		}
		if(doEvents)
			notifyStructureChange();
	}
}

class ElementPathComparator implements Comparator<ElementPath> {

	// TODO this will probably have to work for Hierarchies, too...
	public int compare(ElementPath o1, ElementPath o2) {
		Dimension[] dim1 = o1.getDimensions();
		Dimension[] dim2 = o2.getDimensions();
		int dimCount1 = dim1.length;
		int dimCount2 = dim2.length;
		if(dimCount1 < dimCount2)
			return -1; 
		else if(dimCount1 > dimCount2)
			return 1;
		//both same dimension length, take element parts length
		for(int d=0;d<dimCount1;d++) {
			int elCount1 = o1.getPart(dim1[d]).length;
			int elCount2 = o2.getPart(dim2[d]).length;
			if(elCount1 < elCount2)
				return -1; 
			else if(elCount1 > elCount2)
				return 1;
		}
		return 0;
	}
}

