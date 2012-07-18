///*
//*
//* @file HierarchyTree.java
//*
//* Copyright (C) 2006-2009 Tensegrity Software GmbH
//*
//* This program is free software; you can redistribute it and/or modify it
//* under the terms of the GNU General Public License (Version 2) as published
//* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
//*
//* This program is distributed in the hope that it will be useful, but WITHOUT
//* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
//* more details.
//*
//* You should have received a copy of the GNU General Public License along with
//* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
//* Place, Suite 330, Boston, MA 02111-1307 USA
//*
//* If you are developing and distributing open source applications under the
//* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
//* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
//* and distribute their source code under the GPL, Tensegrity provides a flexible
//* OEM Commercial License.
//*
//* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
//*
//* @version $Id: LegacyHierarchyTree.java,v 1.2 2010/02/12 13:49:50 PhilippBouillon Exp $
//*
//*/
//
///*
// * (c) Tensegrity Software 2009
// * All rights reserved
// */
//package com.tensegrity.wpalo.client.ui.mvc.cubeview;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import com.extjs.gxt.ui.client.Style.SelectionMode;
//import com.extjs.gxt.ui.client.binder.TreeBinder;
//import com.extjs.gxt.ui.client.data.BaseTreeLoader;
//import com.extjs.gxt.ui.client.data.LoadEvent;
//import com.extjs.gxt.ui.client.data.ModelData;
//import com.extjs.gxt.ui.client.data.ModelStringProvider;
//import com.extjs.gxt.ui.client.data.TreeLoader;
//import com.extjs.gxt.ui.client.event.Listener;
//import com.extjs.gxt.ui.client.event.LoadListener;
//import com.extjs.gxt.ui.client.event.WindowEvent;
//import com.extjs.gxt.ui.client.mvc.Dispatcher;
//import com.extjs.gxt.ui.client.store.TreeStore;
//import com.extjs.gxt.ui.client.widget.Items;
//import com.extjs.gxt.ui.client.widget.LayoutContainer;
//import com.extjs.gxt.ui.client.widget.MessageBox;
//import com.extjs.gxt.ui.client.widget.tree.Tree;
//import com.extjs.gxt.ui.client.widget.tree.TreeItem;
//import com.extjs.gxt.ui.client.widget.tree.TreeSelectionModel;
//import com.extjs.gxt.ui.client.widget.tree.Tree.CheckCascade;
//import com.google.gwt.user.client.Command;
//import com.google.gwt.user.client.DeferredCommand;
//import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
//import com.tensegrity.palo.gwt.core.client.models.XObject;
//import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
//import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
//import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
//import com.tensegrity.palo.gwt.core.client.models.palo.XElementType;
//import com.tensegrity.wpalo.client.WPaloEvent;
//import com.tensegrity.wpalo.client.ui.model.TreeLoaderProxy;
//import com.tensegrity.wpalo.client.ui.model.TreeNode;
//
///**
// * <code>HierarchyTree</code>
// * TODO DOCUMENT ME
// *
// * @version $Id: LegacyHierarchyTree.java,v 1.2 2010/02/12 13:49:50 PhilippBouillon Exp $
// **/
//public class LegacyHierarchyTree {
//	
//	private final Tree tree;
//	private TreeNode root;
//	private final LegacyTreeNodeSelector nodeSelector;
//	private TreeBinder<TreeNode> treeBinder;
//	private final TreeLoader<TreeNode> treeLoader;
//	
//	public LegacyHierarchyTree() {
//		treeLoader = createTreeLoader();
//		tree = createTree();
//		nodeSelector = new LegacyTreeNodeSelector(this);
//	}
//	
//	public final Tree getTree() {
//		return tree;
//	}
//
//	public final LegacyTreeNodeSelectionModel getSelectionModel() {
//		return (LegacyTreeNodeSelectionModel)tree.getSelectionModel();
//	}
//	
//	public final List<TreeNode> getSelection() {
//		return treeBinder.getSelection();
//	}
//
//	public final void setInput(XAxisHierarchy hierarchy) {
//		tree.removeAll();
//		this.root = new TreeNode(null, hierarchy);
//		load();
//	}
//	
//	public final void reset() {
////		setSelection(initialSelectedElements);
//	}	
//	
//	private final void load() {
//		treeLoader.load(root);	
//	}
//	
//	/** 
//	 * selects all currently visible elements. note: this only works if tree is
//	 * a checkbox tree...
//	 */
//	public final void selectAll() {		
//		nodeSelector.selectAll(true);
//	}
//	public final void selectAllVisible() {		
//		nodeSelector.selectAllVisible();
//	}
//	
//	/** 
//	 * deselects all currently visible elements. note: this only works if tree is
//	 * a checkbox tree...
//	 */
//	public final void deselectAll() {
//		nodeSelector.selectAll(false);
//	}
//	
//	public final void selectLevel(int lvl, boolean doIt) {
//		if(lvl < 0)
//			return;
//		nodeSelector.selectBy(lvl, doIt);
//	}
//	
//	public final void selectLeafs(boolean doIt) {
//		nodeSelector.selectLeafs(doIt);
//	}
//	/** 
//	 * inverts the currently visible selection. note: this only works if tree is
//	 * a checkbox tree...
//	 */
//	public final void invertSelection() {
//		nodeSelector.invert();
//	}
//	
//	public final void selectByRegEx(String regex, boolean doIt) {
//		nodeSelector.selectBy(regex, doIt);
//	}
//	
//	public final XElement[] getSelectedElements() {
//		List<TreeNode> selectedNodes = //treeBinder.getCheckedSelection();
//			treeBinder.getSelection();
//		XElement[] selectedElements = new XElement[selectedNodes.size()];
//		int index = 0;
//		for(TreeNode node : selectedNodes)
//			selectedElements[index++] = getElement(node);
//		return selectedElements;
//	}
//
//	public final void apply(XElementNode[] initialSelection, DnDHierarchyTree otherTree, boolean showOnRight, String paths, XAxisHierarchy xAxisHierarchy) {
//		if(initialSelection != null) {
//			final List<XElementNode> flatSelection = toFlat(initialSelection);
//			LegacyInitialTreeData data = new LegacyInitialTreeData();
//			data.tree = otherTree;
//			data.input = initialSelection;
//			nodeSelector.applySelection(flatSelection, data, showOnRight, paths, xAxisHierarchy);
//		}
//	}
//	
//	private final List<XElementNode> toFlat(XElementNode[] nodes) {
//		List<XElementNode> flatStructure = new ArrayList<XElementNode>();
//		addNodes(nodes, flatStructure);
//		return flatStructure;
//	}
//	private final void addNodes(XElementNode[] nodes,
//			List<XElementNode> flatStructure) {
//		for(XElementNode node : nodes) {			
//			flatStructure.add(node);
//			if(node.hasChildren())
//				addNodes(node.getChildren(), flatStructure);
//		}		
//	}
//
//	public final void selectBy(String path, boolean doIt) {
//		nodeSelector.selectBy(path, doIt, true);
//	}
//	public final void setSelection(XElement[] elements) {
//		if(elements != null)
//			nodeSelector.setSelection(elements);		
//	}
//	final TreeItem getItem(TreeNode node) {
//		return (TreeItem)treeBinder.findItem(node);
//	}
//	
//	final List<TreeNode> getRootNodes() {
//		return root.getChildren();
//	}
//	
//	final List<TreeItem> getVisibleItems() {
//		List<TreeItem> allItems = tree.getAllItems();
//		allItems.remove(tree.getRootItem());
//		return allItems;
//	}
//	
//	final void addLoadListener(LoadListener listener) {
//		treeLoader.addLoadListener(listener);
//	}
//	final void removeLoadListener(LoadListener listener) {
//		treeLoader.removeLoadListener(listener);
//	}
//
//	final XElement getElement(TreeItem item) {
//		return getElement((TreeNode) item.getModel());
//	}
//	final XElement getElement(TreeNode node) {
//		if(node.getXObject() instanceof XElementNode) {
//			XElementNode elNode = (XElementNode) node.getXObject();
//			return elNode.getElement();
//		}
//		return (XElement)node.getXObject();
//	}
//
//	private final TreeLoader<TreeNode> createTreeLoader() {
//		TreeLoader<TreeNode> treeLoader = new BaseTreeLoader<TreeNode>(
//				new TreeLoaderProxy()) {
//			public boolean loadChildren(TreeNode parent) {
////				((Workbench)Registry.get(Workbench.ID)).showWaitCursor("Loading children of '" + parent.getXObject().getName() + "'.");
//				return super.loadChildren(parent);
//			}
//			
//			public boolean hasChildren(TreeNode data) {
//				return data != null && data.getXObject() != null
//						&& data.getXObject().hasChildren();
//			}
//
//			protected void onLoadFailure(TreeNode parent, Throwable cause) {
////				((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
//				if(cause != null && cause instanceof SessionExpiredException) {
//					Listener<WindowEvent> callback = new Listener<WindowEvent>() {
//						public void handleEvent(WindowEvent we) {
////							Button clicked = we.buttonClicked;
////							if(clicked.getText().equalsIgnoreCase(Dialog.YES))
//								Dispatcher.forwardEvent(WPaloEvent.APP_STOP);
//						}
//					};		
//					MessageBox.info("SessionExpired", cause.getLocalizedMessage()+"<br/>Please log in again.", callback);
//				}
//				super.onLoadFailure(parent, cause);
//			}
//			
//			protected void onLoadSuccess(TreeNode parent,
//					List<TreeNode> children) {
//				for (TreeNode child : children)
//					parent.add(child);
//				super.onLoadSuccess(parent, children);
//
//				for (TreeNode child : children) {
//					if(nodeSelector.isSelected(child))
//						getItem(child).setChecked(true);
//				}
////				((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
//			}
//		};
//		return treeLoader;
//	}
//
//	private final Tree createTree() {
//		final Tree tree = new Tree();
//		tree.setAnimate(false);
//		tree.setSelectionModel(new LegacyTreeNodeSelectionModel(SelectionMode.MULTI));
//		tree.setIndentWidth(10);
//		tree.setCheckStyle(CheckCascade.NONE);
//		tree.setAnimate(false);
//		TreeStore<TreeNode> treeStore = new TreeStore<TreeNode>(treeLoader);
//		treeBinder = new TreeBinder<TreeNode>(tree, treeStore);
//		treeBinder.setDisplayProperty("nameAndKids");
//		treeBinder.setAutoSelect(true);
//		treeBinder.setIconProvider(new ModelStringProvider<TreeNode>() {
//			public String getStringValue(TreeNode model, String property) {
//				String icon = null;				
//				XElement element = getElement(model);
//				XElementType elType = element.getElementType();				
//				if(elType != null) {
//					if (elType.equals(XElementType.CONSOLIDATED))
//						icon = "icon-element-consolidated";
//					else if (elType.equals(XElementType.NUMERIC))
//						icon = "icon-element-numeric";
//					else if (elType.equals(XElementType.STRING))
//						icon = "icon-element-string";
//				}
//				return icon;
//			}
//		});
//		return tree;
//	}
//	
//	public final void traverse(FastMSTreeItemVisitor visitor) {
//		TreeItem root = tree.getRootItem();
//		traverse(root.getItems(), null, visitor);
//	}
//	private final void traverse(List<TreeItem> items, TreeItem parent, FastMSTreeItemVisitor visitor) {
//		for(TreeItem item : items) {
//			if(isSelected(item))
//				visitor.visit(item, parent);
//			traverse(item.getItems(), item, visitor);
//		}
//	}
//	private final boolean isSelected(TreeItem item) {
//		return tree.getSelectionModel().isSelected(item);
//	}
//}
//
//class LegacyTreeNodeSelectionModel extends TreeSelectionModel {
//
//	public LegacyTreeNodeSelectionModel(SelectionMode mode) {
//		super(mode);
//	}
//	public void select(TreeItem item, boolean keepExisting) {
//		doSelect(new Items<TreeItem>(item), keepExisting, true);
//	}
//}
//
//class LegacyTreeNodeSelector extends LoadListener {
//	private final LegacyHierarchyTree tree;
//	private LegacyFilter filter;
//	private final List<XElement> selection = new ArrayList<XElement>();
//	private final List <TreeItem> initialSelection = new ArrayList<TreeItem>();
//	private List <XElementNode> activeSelection;
//	
//	LegacyTreeNodeSelector(LegacyHierarchyTree tree) {
//		this.tree = tree;
//		this.tree.addLoadListener(this);
//	}
//		
//	public void loaderLoad(LoadEvent le) {
//		TreeNode parent = (TreeNode) le.config;
//		traverse(parent, filter);
//		if (activeSelection != null && activeSelection.size() == 0) {// && postponedSelection.size() == 0 && !mustDisplayRight) {
//			tree.getSelectionModel().select(initialSelection);
//		}
//	}
//		
//	final void applySelection(final List<XElementNode> selection, final LegacyInitialTreeData data, boolean showOnRight, final String paths, XAxisHierarchy xAxisHierarchy) {
//		if (showOnRight) {
//			if(!selection.isEmpty()) {
//				tree.getSelectionModel().deselectAll();
//				if (paths != null) {
//					data.tree.setInput(data.input, paths.split(","));	
//				} else {
//					data.tree.setInput(data.input, null);
//				}				
//			}	
//			data.tree.applyAlias(xAxisHierarchy);
//			return;
//		}
//		filter = new LegacyFilter() {
//			public boolean filter(TreeItem item) {
//				String [] allPaths = null;
//				if (paths != null) {
//					allPaths = paths.split(",");
//				}
//				TreeNode tNode = getTreeNodeFrom(item);
//				if (tNode != null && allPaths != null) {
//					String path = LegacyLocalFilterFieldSet.getPath(tNode);
//					boolean found = false; 
//					for (String p: allPaths) {
//						if (p.equals(path)) {
//							select(item, true);
//							found = true;
//							break;
//						}
//					}
//					if (!found) {
//						select(item, false);
//					}
//				}
//				
//				return true;
//			}
//
//			public boolean shouldExpand(TreeItem item) {
//				String [] allPaths = null;
//				if (paths != null) {
//					allPaths = paths.split(",");
//				}
//				TreeNode tNode = getTreeNodeFrom(item);
//				if (tNode != null && allPaths != null) {
//					String path = LegacyLocalFilterFieldSet.getPath(tNode);
//					for (String p: allPaths) {
//						if (p.indexOf(path) != -1 && !p.equals(path)) {
//							return true;		
//						}
//					}
//				}
//				return false;
//			}
//
//			public boolean traverseChild(TreeNode child) {
//				String [] allPaths = null;
//				if (paths != null) {
//					allPaths = paths.split(",");
//				}
//				if (allPaths != null) {
//					String childPath = LegacyLocalFilterFieldSet.getPath(child);
//					for (String path: allPaths) {
//						if (path.indexOf(childPath) != -1) {
//							return true;
//						}
//					}
//				}					
//				return false;
//			}
//			
//
//		};
//		for(TreeNode root: tree.getRootNodes()) 
//			traverse(root, filter);
//		data.tree.applyAlias(xAxisHierarchy);
//	}
//		
//	private final TreeNode getTreeNodeFrom(TreeItem item) {
//		ModelData model = item.getModel();
//		if(model instanceof TreeNode) {
//			return (TreeNode) model;
//		}
//		return null;
//	}
//	
//	final void selectAllVisible() {
//		deactivateFilter();
//		List<TreeItem> allItems = tree.getVisibleItems();
//		for(TreeItem item : allItems) {
//			select(item, true);
//		}
//	}
//	final void selectAll(final boolean doIt) {
//		filter = new LegacyDefaultFilter() {
//			public boolean filter(TreeItem item) {
//				select(item, doIt);
//				return true;
//			}
//		};
//		
//		for(TreeNode root : tree.getRootNodes())
//			traverse(root, filter);
//	}
//	
//	/** inverts current selection */
//	final void invert() {
//		deactivateFilter();
//		List<TreeItem> allItems = tree.getVisibleItems();
//		for(TreeItem item : allItems) {
//			select(item,!item.isChecked());
//		}
//	}
//	private final void deactivateFilter() {
//		filter = null;
//	}
//	
//	/** selects all nodes which name match the given regular expression */
//	final void selectBy(String regex, boolean doIt) {
//		regex = parseWildcards(regex);
//		List<TreeItem> allItems = tree.getVisibleItems();
//		for(TreeItem item : allItems) {
//			XElement element = tree.getElement(item);
//			if(element.getName().matches(regex))
//				select(item, doIt);
//
//		}
//	}
//	
//	/** selects all nodes which depth equals the given level */
//	final void selectBy(int level, final boolean doIt) {
//		final int depth = level; // - 1;
//		filter = new LegacyDefaultFilter() {
//			public boolean filter(TreeItem item) {
//				int elDepth = 0;
//				TreeItem p = item.getParentItem();
//				while (p != null) {
//					elDepth++;
//					p = p.getParentItem();
//				}
////				XElement element = tree.getElement(item);
////				int elDepth = element.getDepth();
//				if(elDepth  == depth) {
//					select(item, doIt);
//				}				
//				return elDepth < depth;
//			}
//		};
//		for(TreeNode root : tree.getRootNodes())
//			traverse(root, filter);	
//	}
//	
//	final void selectBy(final String path, final boolean doIt, final boolean isPath) {		
//		filter = new LegacyDefaultFilter() {
//			private String currentPath = path;
//			private Set <TreeItem> toBeExpanded = new HashSet<TreeItem>();
//			
//			public boolean shouldExpand(TreeItem node) {
//				return toBeExpanded.contains(node);
//			}
//			
//			public boolean traverseChild(TreeNode node) {
//				if (currentPath == null || currentPath.isEmpty()) {
//					return false;
//				}								
////				TreeItem item = tree.nodeMap.get(node.get("nameAndKids"));
////				if (item == null) {
////					return false;
////				}
////				XElement element = tree.getElement(item);				
//				XObject xObj = node.getXObject();
//				if (xObj != null && xObj instanceof XElementNode) {
//					xObj = ((XElementNode) xObj).getElement();
//				}
//				if (xObj == null) {
//					return false;
//				}
//				String id = xObj.getId();				
//				boolean r = currentPath.startsWith(id + "/") || currentPath.equals(id);
//				return r;
//			}
//			
//			public boolean filter(final TreeItem node) {				
//				if (currentPath == null || currentPath.length() == 0) {
//					return false;
//				}
//				String oldCurrentPath = currentPath;
//				int index = currentPath.indexOf("/");
//				String id = index == -1 ? currentPath : currentPath.substring(0, index);
//				if (index != -1) {
//					if ((index + 1) < currentPath.length()) {
//						currentPath = currentPath.substring(index + 1);
//					} else {
//						currentPath = null;
//					}
//				} else {
//					currentPath = null;
//				}				
//				XElement element = tree.getElement(node);
//				if (element != null && element.getId().equals(id)) {
//					if (currentPath == null || currentPath.length() == 0) {
//						select(node, doIt);
//						if (doIt) {
//							if (tree.getTree().getParent() instanceof LayoutContainer) {
//								DeferredCommand.addCommand(new Command(){
//									public void execute() {
//										((LayoutContainer) tree.getTree().getParent()).scrollIntoView(node);
//									}
//								});																
//							}
////							tree.getTree().scrollIntoView(node);
//						}
//						return false;
//					}
//					toBeExpanded.add(node);
//					return true;
//				}
//				currentPath = oldCurrentPath;
//				return true;
//			}			
//		};
//		for (TreeNode root: tree.getRootNodes()) {
//			traverse(root, filter);
//		}
//	}
//	
//	/** selects or deselects all nodes which are leafs */
//	final void selectLeafs(final boolean doIt) {
//		filter = new LegacyDefaultFilter() {
//			public boolean filter(TreeItem item) {
//				if(item.isLeaf())
//					select(item, doIt);
//				return true;
//			}
//		};
//		for(TreeNode root : tree.getRootNodes())
//			traverse(root, filter);
//	}
//	
//	final void setSelection(XElement[] selectedElements) {
//		selection.clear();
//		selection.addAll(Arrays.asList(selectedElements));
//	}
//
//	final boolean isSelected(TreeNode node) {
//		XElement element = tree.getElement(node);
//		if (selection.contains(element))
//				return true;
//		return false;
//	}
//
//	private final void select(TreeItem item, boolean doIt) {
//		item.setChecked(doIt);
//		LegacyTreeNodeSelectionModel selectionModel = tree.getSelectionModel();		
//		if(doIt) {
//			//bug in gxt? we can select items several times and to deselect it 
//			//we have to do this several times too!!
//			if(!selectionModel.isSelected(item))
//				selectionModel.select(item, true);
//		} else {
//			if (selectionModel.isSelected(item)) 
//				selectionModel.deselect(item);
//		}
//	}
//	
//	private final void traverse(TreeNode node, LegacyFilter filter) {
//		TreeItem item = tree.getItem(node);
//		if(item == null || filter == null)
//			return;
//		if (filter.filter(item)) {			
//			if (filter.shouldExpand(item)) {
//				item.setExpanded(true);
//			} 
//			for (TreeNode child : node.getChildren()) {
//				if (filter.traverseChild(child)) {
//					traverse(child, filter);
//				}
//			}
//		}
//	}
//	
//    private final String parseWildcards(String str) {
//        //simple try:
//        str = str.replaceAll("\\*",".*");
//        str = str.replaceAll("\\?",".?");
//        return str;
//    }
//}
//
//interface LegacyFilter {
//	
//	/** returns <code>true</code> to go on with tree traversal, false otherwise */
//	boolean filter(TreeItem node);
//	boolean traverseChild(TreeNode child);
//	boolean shouldExpand(TreeItem item);
//}
//
//abstract class LegacyDefaultFilter implements LegacyFilter {
//	public boolean traverseChild(TreeNode child) {
//		return true;
//	}
//	
//	public boolean shouldExpand(TreeItem item) {
//		return true;
//	}
//}
//
//class LegacyInitialTreeData {
//	DnDHierarchyTree tree;
//	XElementNode[] input;
//}

