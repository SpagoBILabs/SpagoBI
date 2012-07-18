///*
//*
//* @file DnDHierarchyTree.java
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
//* @version $Id: LegacyDnDHierarchyTree.java,v 1.2 2010/02/12 13:49:50 PhilippBouillon Exp $
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
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//
//import com.extjs.gxt.ui.client.Events;
//import com.extjs.gxt.ui.client.Registry;
//import com.extjs.gxt.ui.client.Style.SelectionMode;
//import com.extjs.gxt.ui.client.binder.TreeBinder;
//import com.extjs.gxt.ui.client.data.ModelData;
//import com.extjs.gxt.ui.client.data.ModelStringProvider;
//import com.extjs.gxt.ui.client.data.TreeModel;
//import com.extjs.gxt.ui.client.dnd.TreeDragSource;
//import com.extjs.gxt.ui.client.dnd.TreeDropTarget;
//import com.extjs.gxt.ui.client.dnd.DND.Feedback;
//import com.extjs.gxt.ui.client.event.BaseEvent;
//import com.extjs.gxt.ui.client.event.DNDEvent;
//import com.extjs.gxt.ui.client.event.Listener;
//import com.extjs.gxt.ui.client.store.TreeStore;
//import com.extjs.gxt.ui.client.widget.tree.Tree;
//import com.extjs.gxt.ui.client.widget.tree.TreeItem;
//import com.extjs.gxt.ui.client.widget.tree.Tree.CheckCascade;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.tensegrity.palo.gwt.core.client.models.XObject;
//import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAlias;
//import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
//import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
//import com.tensegrity.palo.gwt.core.client.models.palo.XElementType;
//import com.tensegrity.palo.gwt.core.client.models.palo.XHierarchy;
//import com.tensegrity.wpalo.client.services.cubeview.WPaloCubeViewServiceProvider;
//import com.tensegrity.wpalo.client.ui.model.TreeNode;
//import com.tensegrity.wpalo.client.ui.model.XObjectModel;
//import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;
//
//
///**
// * <code>DnDHierarchyTree</code>
// * TODO DOCUMENT ME
// *
// * @version $Id: LegacyDnDHierarchyTree.java,v 1.2 2010/02/12 13:49:50 PhilippBouillon Exp $
// **/
//public class LegacyDnDHierarchyTree {
//
//	private Tree tree;
//	private TreeBinder<XObjectModel> treeBinder;
//	private XElementNode[] initialVisibleElements;
//	private String [] initialPaths;
//	private int pathCounter;
//	
//	public LegacyDnDHierarchyTree() {
//		initComponent();
//		initDnD();
//	}
//
//	private final void initComponent() {
//		tree = createTree();
//	}
//	
//	private final Tree createTree() {
//		final Tree tree = new Tree();
//		tree.setIndentWidth(10);
//		tree.setCheckStyle(CheckCascade.NONE);
//		tree.setSelectionMode(SelectionMode.MULTI);
//		tree.setAnimate(false);
//		tree.addListener(Events.SelectionChange, new Listener<BaseEvent>() {
//			public void handleEvent(BaseEvent be) {
//				ArrayList <TreeItem> list = new ArrayList<TreeItem>(tree.getSelectedItems());
//				for (TreeItem item: tree.getSelectedItems()) {
//					TreeItem it = item;
//					while (it.getParentItem() != null) {
//						if (list.contains(it.getParentItem())) {
//							list.remove(item);
//							break;
//						}
//						it = it.getParentItem();
//					}
//				}
//				tree.setSelectedItems(list);
//			}
//		});
//		TreeStore<XObjectModel> treeStore = new LegacyEnhancedTreeStore(tree);
//		treeBinder = new LegacyEnhancedTreeBinder(tree, treeStore);
//		treeBinder.setDisplayProperty("name");
//		treeBinder.setAutoSelect(true);
//		treeBinder.setIconProvider(new ModelStringProvider<XObjectModel>() {
//			public String getStringValue(XObjectModel model, String property) {
//				TreeItem item = (TreeItem)treeBinder.findItem(model);
//				String icon = null;
//				XElementNode xElementNode = (XElementNode)model.getXObject();
//				
//				XElementType elType = xElementNode.getElement().getElementType();
//				if (item != null && item.hasChildren())
//					icon = "icon-element-consolidated";
//				else if (elType.equals(XElementType.CONSOLIDATED))
//					icon = "icon-element-consolidated";
//				else if (elType.equals(XElementType.NUMERIC))
//					icon = "icon-element-numeric";
//				else if (elType.equals(XElementType.STRING))
//					icon = "icon-element-string";
//				else if (elType.equals(XElementType.VIRTUAL))
//					icon = "icon-element-virtual";
//				return icon;
//			}
//		});
//		return tree;
//	}
//	private final void initDnD() {
//		TreeDragSource source = new TreeDragSource(treeBinder);
//		source.setGroup("treeGroup");
//		TreeDropTarget dropTarget = new LegacyEnhancedTreeDropTarget(treeBinder);
//		dropTarget.setGroup("treeGroup");
//		dropTarget.setAllowSelfAsSource(true);
//		dropTarget.setFeedback(Feedback.BOTH);
//	}
//	
//	void applyAlias(XAxisHierarchy hierarchy) {
//		final ArrayList <XElementNode> allXElementNodes = new ArrayList<XElementNode>();
//		traverse(new FastMSTreeItemVisitor() {
//			public boolean visit(TreeItem item, TreeItem parent) {
//				if (item.getModel() instanceof XObjectModel) {
//					XObjectModel model = (XObjectModel) item.getModel();
//					if (model.getXObject() instanceof XElementNode) {
//						allXElementNodes.add((XElementNode) model.getXObject());
//					}
//				} 
//				return true;
//			}
//		});			
//		
//		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
//		WPaloCubeViewServiceProvider.getInstance().applyAlias(sessionId, hierarchy, allXElementNodes.toArray(new XElementNode[0]), new AsyncCallback <XElementNode[]>(){
//			public void onFailure(Throwable arg0) {
//			}
//
//			public void onSuccess(final XElementNode[] nodes) {
//				traverse(new FastMSTreeItemVisitor() {
//					int counter = 0;
//					
//					public boolean visit(TreeItem item, TreeItem parent) {
//						if (item.getModel() instanceof XObjectModel) {							
//							XObjectModel model = (XObjectModel) item.getModel();
//							if (model.getXObject() instanceof XElementNode) {
////								model.set("name", nodes[counter].getName());
////								model.getXObject().setName(nodes[counter].getName());
//								item.setText(nodes[counter].getName());
//								counter++;
//							}
//						} 
//						return true;
//					}
//				});	
//				
//			}
//		});
//	}
//	
//	public final Tree getTree() {
//		return tree;
//	}
//	
//	public final void reset() {
//		treeBinder.getTreeStore().removeAll();		
//		setInput(initialVisibleElements, initialPaths);
//	}
//	
//	public final void refresh() {
//		
//	}
//	public final boolean isEmpty() {
//		return tree.getRootItem().getItemCount() <= 0;
//	}
//
//	@SuppressWarnings("unchecked")
//	public final void setInput(XElementNode[] rootNodes, String [] paths) {		
//		if(rootNodes == null)
//			return;
//		initialVisibleElements = rootNodes;
//		initialPaths = paths;
//		treeBinder.getTreeStore().removeAll();
//		TreeStore<XObjectModel> store = treeBinder.getTreeStore();
//		pathCounter = 0;
//		for(XElementNode node : rootNodes)
//			add(node, null, store, paths);
////		tree.addListener(Events.Expand, new Listener<BaseEvent>() {
////			public void handleEvent(BaseEvent be) {
////			}
////		});
//		tree.expandAll();
//	}
//	private final void add(XElementNode node, XObjectModel parent, TreeStore<XObjectModel> store, String [] paths) {		
//		XObjectModel model = wrap(node);
//		if (paths != null && paths.length > pathCounter) {
//			model.set("filterPath", paths[pathCounter++]);
//		}
//		if(parent == null)
//			store.add(model, true);
//		else
//			store.add(parent, model, true);
//		//children:
//		if(node.getChildren().length > 0) {
//			for(XElementNode child : node.getChildren())
//				add(child, model, store, paths);
//		}
//	}
//	
//	public final void add(XObject xObject) {
//		XObjectModel xObjModel = wrap(xObject);
//		store(xObjModel);
//		treeBinder.setSelection(xObjModel);
//	}
//	private final XObjectModel wrap(XObject xObject) {
//		return new XObjectModel(xObject);
//	}
//
//	private final void store(XObjectModel model) {
//		TreeStore<XObjectModel> store = treeBinder.getTreeStore();
//		store.add(model, false);
//	}
//		
//	private final XObjectModel determineParent(Map <TreeNode, XObjectModel> nodeMap, TreeNode node) {
//		TreeNode nd = node;
//		while (nd.getParent() != null) {
//			if (nodeMap.containsKey(nd.getParent())) {
//				return nodeMap.get(nd.getParent());
//			}
//			nd = nd.getParent();
//		}
//		return null;
//	}
//	
//	public final void addSelection(List<TreeNode> nodes) {
//		List <XObjectModel> curSel = treeBinder.getSelection();
//		HashMap <TreeNode, XObjectModel> nodeMap =
//			new HashMap<TreeNode, XObjectModel>();
//
//		XObjectModel selectedRoot = null;
//		if (curSel != null && curSel.size() >= 1) {
//			selectedRoot = curSel.get(0);
//		}
//		TreeStore <XObjectModel> store = treeBinder.getTreeStore();
//		for (TreeNode node: nodes) {
//			XObjectModel parent = determineParent(nodeMap, node);
//			XObjectModel xObjModel = wrap(node.getXObject());
//			if (node.get("filterPath") != null) {
//				xObjModel.set("filterPath", node.get("filterPath"));
//			} else {
//				xObjModel.remove("filterPath");
//			}
//			if (parent == null) {
//				if (selectedRoot == null) {
//					store.add(xObjModel, false);				
//				} else {		
//					store.add(selectedRoot, xObjModel, false);
//					TreeItem item = (TreeItem) treeBinder.findItem(selectedRoot);
//					if (item != null) {
//						treeBinder.getTree().expandPath(item.getPath());
//					}
//				}				
//			} else {
//				store.add(parent, xObjModel, false);
//				TreeItem item = (TreeItem) treeBinder.findItem(parent);
//				if (item != null) {
//					treeBinder.getTree().expandPath(item.getPath());
//				}
//			}
//			nodeMap.put(node, xObjModel);
//		}
//		
//	}
//	
//	public final void removeSelection() {
//		List<XObjectModel> selection = treeBinder.getSelection();
//		if(!selection.isEmpty()) {
//			LegacyEnhancedTreeStore store = 
//				(LegacyEnhancedTreeStore)treeBinder.getTreeStore();
//			for(XObjectModel xObj : selection) {
//				TreeItem item = (TreeItem)treeBinder.findItem(xObj);
////				store.remove(xObj);
//				if (item != null) {
//					store.remove(item);
//				}
//			}
////				removeNodeFromStore(xObj, store);
//		}
//	}
//
//	public final void moveUpSelection() {
//		List<XObjectModel> selection = treeBinder.getSelection();
//		if(!selection.isEmpty()) {
//			LegacyEnhancedTreeStore store = (LegacyEnhancedTreeStore) treeBinder.getTreeStore();
//			for(XObjectModel xObj : selection) {
//				TreeItem item = (TreeItem)treeBinder.findItem(xObj);
//				store.moveUp(item);
//			}
//			treeBinder.setSelection(selection);
//		}
//		
//		
//	}
//	public final void moveDownSelection() {
//		List<XObjectModel> selection = treeBinder.getSelection();
//		if(!selection.isEmpty()) {
//			LegacyEnhancedTreeStore store = (LegacyEnhancedTreeStore) treeBinder.getTreeStore();
//			for(XObjectModel xObj : selection) {
//				TreeItem item = (TreeItem)treeBinder.findItem(xObj);
//				store.moveDown(item);
//			}
//			treeBinder.setSelection(selection);
//		}
//	}
//	
//	public final void traverse(FastMSTreeItemVisitor visitor) {
//		TreeItem root = tree.getRootItem();
//		traverse(root.getItems(), null, visitor);
//	}
//	private final void traverse(List<TreeItem> items, TreeItem parent, FastMSTreeItemVisitor visitor) {
//		for(TreeItem item : items) {
//			visitor.visit(item, parent);
//			traverse(item.getItems(), item, visitor);
//		}
//	}
//}
//
//class LegacyEnhancedTreeStore extends TreeStore<XObjectModel> {
//	class XObjectTreeModel {
//		XObjectTreeModel parent;
//		XObjectModel model;
//		List <XObjectTreeModel> children = new ArrayList<XObjectTreeModel>();
//		
//		XObjectTreeModel(XObjectTreeModel parent, XObjectModel model) {
//			this.parent = parent;
//			this.model = model;
//		}
//		
//		void addChildren(TreeItem item) {
//			for (TreeItem ti: item.getItems()) {
//				XObjectModel xObj = getModel(ti);
//				XObjectTreeModel kid = new XObjectTreeModel(this, xObj); 
//				children.add(kid);
//				kid.addChildren(ti);
//			}			
//		}
//	}
//	
//	private final Tree tree;
//		
//	LegacyEnhancedTreeStore(Tree tree) {
//		super();
//		this.tree = tree;
//	}
//	
//	public final void moveUp(TreeItem item) {
//		XObjectModel model = getModel(item);
//		XObjectTreeModel treeModel = new XObjectTreeModel(null, model);
//		treeModel.addChildren(item);
//		XObjectModel parent = getParentModel(item);
//		int index = getIndex(item);
//		if (index > 0) {
//			remove(item);
//			doRemove(model, parent);
//			doInsert(treeModel, parent, index - 1);
//		}
//	}
//	
//	public final void moveDown(TreeItem item) {
//		XObjectModel model = getModel(item);
//		XObjectTreeModel treeModel = new XObjectTreeModel(null, model);
//		treeModel.addChildren(item);
//		XObjectModel parent = getParentModel(item);
//		int index = getIndex(item);
//		int maxIndex = item.getParentItem().getItemCount() - 1;
//		if (index <  maxIndex) {
//			doRemove(model, parent);
//			doInsert(treeModel, parent, index + 1);
//		}
//	}
//	
//	public final void remove(TreeItem item) {
//		XObjectModel model = getModel(item);
//		XObjectModel parent = getParentModel(item);
//		doRemove(model, parent);
//	}
//	
//	private final XObjectModel getModel(TreeItem item) {
//		return (XObjectModel) item.getModel();
//	}
//	private final XObjectModel getParentModel(TreeItem item) {
//		return getModel(item.getParentItem());
//	}
//	private final int getIndex(TreeItem item) {
//		return item.getParentItem().indexOf(item);
//	}
//	
//	private final void doRemove(XObjectModel model, XObjectModel parent) {
//		if (parent == null) {
//			remove(model);
//		} else {
//			remove(parent, model);
//		}
//	}
//	private final void doInsert(XObjectTreeModel model, XObjectModel parent,
//			int atIndex) {				
//		if (parent == null) {
//			insert(model.model, atIndex, true);
//		} else {
//			insert(parent, model.model, atIndex, true);
//		}
//		int counter = 0;
//		for (XObjectTreeModel kid: model.children) {
//			doInsert(kid, model.model, counter++);
//		}
//		tree.expandAll();
//	}
//}
//
//class LegacyEnhancedTreeBinder extends TreeBinder<XObjectModel> {
//
//	public LegacyEnhancedTreeBinder(Tree tree, TreeStore<XObjectModel> store) {
//		super(tree, store);
//	}
//	
//	public void loadChildrenOf(TreeItem item) {
//		if(item != null) {
//			loadChildren(item, true);
//		}
//	}
//}
//
//class LegacyEnhancedTreeDropTarget extends TreeDropTarget {
//
//	public LegacyEnhancedTreeDropTarget(TreeBinder<XObjectModel> binder) {
//		super(binder);
//	}
//
//	protected void showFeedback(DNDEvent event) {
//	    final TreeItem item = tree.findItem(event.getTarget());
//	    if (item == null) {
//	      event.status.setStatus(false);
//	      return;
//	    }
//	    if (event.target.getComponent() == event.source.getComponent()) {
//	      Tree source = (Tree) event.source.getComponent();
//	      TreeItem sel = source.getSelectedItem();
//	      List<TreeItem> children = sel.getItems(true);
//	      if (children.contains(item)) {
//	        event.status.setStatus(false);
//	        return;
//	      }
//	    }
//
//		if(feedback == Feedback.BOTH) {
////			final TreeItem item = tree.findItem(event.getTarget());
//			if(isAtBoundaryOf(item, event.getClientY()))
//				handleInsert(event, item);
//			else
//				handleAppend(event, item);
//		} else
//			super.showFeedback(event);	
//	}
//	private final boolean isAtBoundaryOf(TreeItem item, int y) {
//		int boundary = 4;
//		int itemTop = item.getAbsoluteTop() + boundary;
//		return !(y > itemTop && y < (itemTop + item.getOffsetHeight() - (2 * boundary)));
//	}
//	
//	protected void handleInsertDrop(DNDEvent event, TreeItem item, int index) {
//		super.handleInsertDrop(event, item, index);
//		tree.expandAll();
////		List<BaseTreeModel<TreeNode>> droppedNodes = (List<BaseTreeModel<TreeNode>>) event.data;		
////		for(BaseTreeModel<TreeNode> node : droppedNodes) {
////			//((EnhancedTreeBinder)binder).loadChildrenOf((TreeItem)binder.findItem(node));
////			try {				
////				binder.getTreeStore().getLoader().loadChildren(node);
////			} catch (Throwable t) {
////				t.printStackTrace();
////			}
////		}
//////		((EnhancedTreeBinder)binder).loadChildrenOf(item);
//	}
//
//	  protected void appendModel(ModelData p, TreeModel model, int index, HashSet <TreeModel> alreadyAppended) {
//		    ModelData child = model.get("model");
//		    if (p == null) {
//		      binder.getTreeStore().insert(child, index, false);
//		    } else {
//		      binder.getTreeStore().insert(p, child, index, false);
//		    }
//		    alreadyAppended.add(model);
//		    List<TreeModel> children = model.getChildren();
//		    for (int i = 0; i < children.size(); i++) {
//		      appendModel(child, children.get(i), i, alreadyAppended);
//		    }
//		  }
//		  
//	  private final void removeChildren(TreeModel tm, ArrayList sel) {
//		  if (tm == null) {
//			  return;
//		  }
//		  for (Object o: tm.getChildren()) {
//			  if (sel.contains(o)) {
//				  sel.remove(o);
//			  }
//			  removeChildren((TreeModel) o, sel);
//		  }
//	  }
//	  
//	  protected void handleAppendDrop(DNDEvent event, TreeItem item) {
//		  
//		  List sel = (List) event.data;
//		    if (sel.size() > 0) {
//		      if (sel.get(0) instanceof ModelData) {
//		        ArrayList selectionCopy = new ArrayList(sel);  
//		        for (Object o: sel) {
//		        	TreeModel tm = (TreeModel) o;		        	
//		        	removeChildren(tm, selectionCopy);
//		        }
//		        ModelData p = item.getModel();		        
//		        for (Object o: selectionCopy) {
//		    	  TreeModel tm = (TreeModel) o;
//    			  appendModel(p, tm, item.getItemCount());
//		        }
//		      } else {
//		        for (int i = 0; i < sel.size(); i++) {
//		          TreeItem ti = (TreeItem) sel.get(i);
//		          item.add(ti);
//		        }
//		      }
//		    }
//		    tree.expandAll();
//	  }	
//	
////	protected void handleAppend(DNDEvent event, TreeItem item) {
////		XObjectModel parent = (XObjectModel)item.getModel();
////        List<XObjectModel> droppedNodes = (List<XObjectModel>) event.data;
////        binder.getTreeStore().add(parent, droppedNodes, true);
////        for(XObjectModel node : droppedNodes) {        	
////        	parent.add(node);        	
////        }
////    }
//}