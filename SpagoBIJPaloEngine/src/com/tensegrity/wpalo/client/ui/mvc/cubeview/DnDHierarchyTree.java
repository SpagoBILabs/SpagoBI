/*
*
* @file DnDHierarchyTree.java
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
* @version $Id: DnDHierarchyTree.java,v 1.20 2010/03/11 10:43:45 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Registry;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementType;
import com.tensegrity.wpalo.client.services.cubeview.WPaloCubeViewServiceProvider;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.model.XObjectModel;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTree;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTreeItem;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastTreeDropController;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;


/**
 * <code>DnDHierarchyTree</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: DnDHierarchyTree.java,v 1.20 2010/03/11 10:43:45 PhilippBouillon Exp $
 **/
public class DnDHierarchyTree {
	private FastMSTree tree;
	private XElementNode[] initialVisibleElements;
	private String initialPaths;
	private int pathCounter;
	private final ArrayList<SelectionCountListener> selectionCountListeners = new ArrayList<SelectionCountListener>();
//	private final PickupDragController dragController;
	
	public DnDHierarchyTree(AbsolutePanel panel) {		
//		dragController = new PickupDragController(panel, true);
		initComponent(panel);		
//		initDnD();
	}

	public void addSelectionCountListener(SelectionCountListener l) {
		selectionCountListeners.add(l);
	}

	public void removeSelectionCountListener(SelectionCountListener l) {
		selectionCountListeners.remove(l);
	}

	private final void fireSelectionNumberChanged() {
		int n = getNumberOfElements();
		for (SelectionCountListener sl : selectionCountListeners) {
			sl.selectionCountChanged(n);
		}
	}

	private final void initComponent(AbsolutePanel dropTarget) {
		tree = createTree(dropTarget);
	}

	public final int getNumberOfElements() {
		if (tree != null) {
			return tree.getTotalSize();
		}
		return 0;
	}

	private final FastMSTree createTree(AbsolutePanel dropTarget) {
		final FastMSTree tree = new FastMSTree(true);
		tree.setMaySelectChildren(false);
		FastTreeDropController dropControl = new FastTreeDropController(dropTarget, tree);
//		dragController.registerDropController(dropControl);
		return tree;
	}

	private final void initDnD() {
		// TreeDragSource source = new TreeDragSource(treeBinder);
		// source.setGroup("treeGroup");
		// TreeDropTarget dropTarget = new EnhancedTreeDropTarget(treeBinder);
		// dropTarget.setGroup("treeGroup");
		// dropTarget.setAllowSelfAsSource(true);
		// dropTarget.setFeedback(Feedback.BOTH);
	}

	void applyAlias(XAxisHierarchy hierarchy) {
		final ArrayList<XElementNode> allXElementNodes = new ArrayList<XElementNode>();
		traverse(new FastMSTreeItemVisitor() {
			public boolean visit(FastMSTreeItem item, FastMSTreeItem parent) {
				if (item.getXObjectModel().getXObject() instanceof XElementNode) {
					allXElementNodes.add((XElementNode) item.getXObjectModel()
							.getXObject());
				}
				return true;
			}
		});

		String sessionId = ((Workbench) Registry.get(Workbench.ID)).getUser()
				.getSessionId();

		WPaloCubeViewServiceProvider.getInstance().applyAlias(sessionId,
				hierarchy.getId(), hierarchy.getViewId(),
				hierarchy.getAxisId(), hierarchy.getActiveAlias(),
				allXElementNodes.toArray(new XElementNode[0]),
				new AsyncCallback<XElementNode[]>() {
					public void onFailure(Throwable arg0) {
					}

					public void onSuccess(final XElementNode[] nodes) {
						traverse(new FastMSTreeItemVisitor() {
							int counter = 0;

							public boolean visit(FastMSTreeItem item,
									FastMSTreeItem parent) {
								if (item.getXObjectModel().getXObject() instanceof XElementNode) {
									item.setHTML(getTreeItemText(nodes[counter]));
									counter++;
								}
								return true;
							}
						});
					}
				});
	}

	public final FastMSTree getTree() {
		return tree;
	}

	public final void reset() {
		tree.clear();
		// treeBinder.getTreeStore().removeAll();
		setInput(initialVisibleElements, initialPaths);
	}

	public final void clearSelection() {
		tree.clear();
		fireSelectionNumberChanged();
	}
	
	public final void refresh() {

	}

	public final boolean isEmpty() {
		return tree.getItemCount() <= 0;
	}

	public final void setInput(XElementNode[] rootNodes, String paths) {
		if (rootNodes == null) {
			((Workbench) Registry.get(Workbench.ID)).hideWaitCursor();
			return;
		}
		initialVisibleElements = rootNodes;
		initialPaths = paths;
		tree.clear();
		pathCounter = 0;
		ArrayList <String> allPaths = new ArrayList<String>();
		if (paths != null) {
			String currentPath = "";
			for (int i = 0; i < paths.length(); i++) {
				char c = paths.charAt(i);
				if (c == ',') {
					if (currentPath.length() != 0) {
						allPaths.add(currentPath);
						currentPath = "";
					}
				} else {
					currentPath += c;
				}
			}
		}

		String [] allPathString = allPaths.toArray(new String[0]);
		for (XElementNode node : rootNodes)
			add(node, null, allPathString);
		tree.expandAll();
		fireSelectionNumberChanged();
		((Workbench) Registry.get(Workbench.ID)).hideWaitCursor();
	}

	private final String getTreeItemText(XElementNode node) {
		String image = "<img paddingTop=\"2px\" width=\"16\" height=\"14\" src=\"icons/element_";
		XElementType xElemType = node.getElement().getElementType();
		if (XElementType.CONSOLIDATED.equals(xElemType)) {
			image += "con2.png\">&nbsp;";
		} else if (XElementType.NUMERIC.equals(xElemType)) {
			image += "num2.png\">&nbsp;";
		} else if (XElementType.STRING.equals(xElemType)) {
			image += "str2.png\">&nbsp;";
		} else {
			image = "";
		}
		return image + node.getName();
	}

	private final void add(XElementNode node, XObjectModel parent,
			String[] paths) {
		XObjectModel model = wrap(node);
		if (paths != null && paths.length > pathCounter) {
			model.set("filterPath", paths[pathCounter++]);
		}
		FastMSTreeItem item = new FastMSTreeItem();
		item.setXObjectModel(model);
		item.setHTML(getTreeItemText(node));
//		dragController.makeDraggable(item.getHtml(), item.getHtml());
		if (parent == null)
			tree.addItem(item);
		else
			parent.getItem().addItem(item);
		// children:
		if (node.getChildren().length > 0) {
			for (XElementNode child : node.getChildren())
				add(child, model, paths);
		}
	}

	private final XObjectModel wrap(XObject xObject) {
		return new XObjectModel(xObject);
	}

	private final XObjectModel determineParent(
			Map<Integer, XObjectModel> nodeMap, TreeNode node) {
		TreeNode nd = node;
		while (nd.getParent() != null) {
			if (nodeMap.containsKey(nd.getParent().getId())) {
				return nodeMap.get(nd.getParent().getId());
			}
			nd = nd.getParent();
		}
		return null;
	}

	public final void addSelection(final List<TreeNode> nodes) {
		final LinkedHashSet<FastMSTreeItem> curSel = tree.getSelectedItems();
		final HashMap<Integer, XObjectModel> nodeMap = new HashMap<Integer, XObjectModel>();
		final LinkedHashSet<FastMSTreeItem> roots = new LinkedHashSet<FastMSTreeItem>();
		XObjectModel selRoot = null;
		if (curSel != null && curSel.size() >= 1) {
			FastMSTreeItem selected = curSel.iterator().next();
			selRoot = selected.getXObjectModel();
			roots.add(selected);
		}
		// TreeStore <XObjectModel> store = treeBinder.getTreeStore();
		final XObjectModel selectedRoot = selRoot;
		DeferredCommand.addCommand(new IncrementalCommand() {
			private int index = 0;
			private final int size = nodes.size();
			
			public boolean execute() {
				TreeNode node = nodes.get(index);
				XObjectModel parent = determineParent(nodeMap, node);
				XObjectModel xObjModel = wrap(node.getXObject());
				if (node.get("filterPath") != null) {
					xObjModel.set("filterPath", node.get("filterPath"));
				} else {
					xObjModel.remove("filterPath");
				}
				FastMSTreeItem item = new FastMSTreeItem();		
				item.setVisible(true);
				item.setXObjectModel(xObjModel);
				if (node.getXObject() instanceof XElementNode) {
					item.setHTML(getTreeItemText((XElementNode) node.getXObject()));
				} else {
					item.setText(node.getXObject().getName());
				}
				xObjModel.setItem(item);
				if (parent == null) {
					if (selectedRoot == null) {
						tree.addItem(item);
						DOM.scrollIntoView(item.getElement());
						roots.add(item);
					} else {
						selectedRoot.getItem().addItem(item);
					}
				} else {
					parent.getItem().addItem(item);
				}
				nodeMap.put(node.getId(), xObjModel);
				index++;
				if (index >= size) {
					tree.deepExpand(roots);
					fireSelectionNumberChanged();
					nodeMap.clear();
					roots.clear();
					tree.setVisible(true);					
					((Workbench) Registry.get(Workbench.ID)).hideWaitCursor();					
				}
				return index < size;
			}
		});
		
//		for (TreeNode node : nodes) {
//			XObjectModel parent = determineParent(nodeMap, node);
//			XObjectModel xObjModel = wrap(node.getXObject());
//			if (node.get("filterPath") != null) {
//				xObjModel.set("filterPath", node.get("filterPath"));
//			} else {
//				xObjModel.remove("filterPath");
//			}
//			FastMSTreeItem item = new FastMSTreeItem();			
//			item.setXObjectModel(xObjModel);
//			if (node.getXObject() instanceof XElementNode) {
//				item.setHTML(getTreeItemText((XElementNode) node.getXObject()));
//			} else {
//				item.setText(node.getXObject().getName());
//			}
////			dragController.makeDraggable(item.getHtml(), item.getHtml());			
//			xObjModel.setItem(item);
//			if (parent == null) {
//				if (selectedRoot == null) {
//					tree.addItem(item);
//					roots.add(item);
////					tree.ensureUnselectedItemVisible(item);
//				} else {
//					selectedRoot.getItem().addItem(item);
////					tree.ensureUnselectedItemVisible(item);
//				}
//			} else {
//				parent.getItem().addItem(item);
////				tree.ensureUnselectedItemVisible(item);
//			}
//			nodeMap.put(node, xObjModel);
//		}
	}

	public final void removeSelection() {
		try {
			LinkedHashSet <FastMSTreeItem> sel = tree.getSelectedItems();
			LinkedHashSet<FastMSTreeItem> selection = new LinkedHashSet<FastMSTreeItem>();
			for (FastMSTreeItem it: sel) {
				selection.add(it);
			}
			if (!selection.isEmpty()) {
				Iterator<FastMSTreeItem> it = selection.iterator();
				while (it.hasNext()) {
					FastMSTreeItem treeItem = it.next();
					if (treeItem.getParentItem() == null) {
						tree.removeItem(treeItem);
					} else {
						treeItem.getParentItem().removeItem(treeItem);
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		fireSelectionNumberChanged();
	}

	public final void moveUpSelection() {
		LinkedHashSet<FastMSTreeItem> selection = tree.getSelectedItems();
		if (!selection.isEmpty()) {
			for (FastMSTreeItem item : selection) {
				FastMSTreeItem parent = item.getParentItem();
				boolean result;
				if (parent == null) {
					result = tree.moveItemUp(item);
				} else {
					result = parent.moveItemUp(item);
				}
				if (!result) {
					break;
				}
			}
		}
	}

	public final void moveDownSelection() {
		ArrayList <FastMSTreeItem> selection = new ArrayList <FastMSTreeItem> (tree.getSelectedItems());
		if (!selection.isEmpty()) {
			int n = selection.size();
			for (int i = n - 1; i >= 0; i--) {
				FastMSTreeItem item = selection.get(i);
				FastMSTreeItem parent = item.getParentItem();
				boolean result;
				if (parent == null) {
					result = tree.moveItemDown(item);
				} else {
					result = parent.moveItemDown(item);
				}
				if (!result) {
					break;
				}
			}
		}
	}

	public final void traverse(FastMSTreeItemVisitor visitor) {
		traverse(tree.getChildren(), null, visitor);
	}

	private final void traverse(List<FastMSTreeItem> items,
			FastMSTreeItem parent, FastMSTreeItemVisitor visitor) {
		for (FastMSTreeItem item : items) {
			visitor.visit(item, parent);
			traverse(item.getChildren(), item, visitor);
		}
	}
}