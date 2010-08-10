/*
*
* @file HierarchyTree.java
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
* @version $Id: HierarchyTree.java,v 1.30 2010/04/12 11:13:36 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.widget.Items;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.extjs.gxt.ui.client.widget.tree.TreeSelectionModel;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.services.cubeview.WPaloCubeViewServiceProvider;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTree;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTreeItem;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.HasFastMSTreeItems;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>HierarchyTree</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: HierarchyTree.java,v 1.30 2010/04/12 11:13:36 PhilippBouillon Exp $
 **/
public abstract class HierarchyTree {
	
	protected final FastMSTree tree;
//	private TreeNode root;
	private final TreeNodeSelector nodeSelector;
	XAxisHierarchy hierarchy;
	protected XViewModel xViewModel;
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();	
	
	public HierarchyTree(boolean multiSelect) {
		tree = createTree(multiSelect);
		nodeSelector = new TreeNodeSelector(this, true);
	}
	
	public ArrayList <FastMSTreeItem> getVisibleItems() {
		return tree.getVisibleItems();
	}

	public final FastMSTree getTree() {
		return tree;
	}
	
	public final LinkedHashSet<FastMSTreeItem> getSelection() {
		return tree.getSelectedItems();
	}

	public void setInput(XAxisHierarchy hierarchy, XViewModel xViewModel) {
		this.hierarchy = hierarchy;
		this.xViewModel = xViewModel;
		tree.clear();
		loadChildren(tree, new TreeNode(null, hierarchy));
	}
		
	public int getNumberOfSelectedElements() {
		return tree.getNumberOfSelectedItems();
	}
	
	public final void reset() {
//		setSelection(initialSelectedElements);
	}	
	
	protected abstract void loadChildren(final HasFastMSTreeItems parentItem, final TreeNode parentNode);
	
//	private final void loadChildren(final HasFastMSTreeItems parentItem, final TreeNode parentNode) {
////		treeLoader.load(root);
//		
//		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
//		final String sessionId = user.getSessionId();
//
//		WPaloServiceProvider.getInstance().loadHierarchyTree(sessionId, hierarchy, -1, new AsyncCallback <List<TreeNode>>() {
//			public void onFailure(Throwable arg0) {
//			}
//
//			public void onSuccess(final List <TreeNode> kids) {				
//				final HashMap <TreeNode, FastMSTreeItem> parents = new HashMap<TreeNode, FastMSTreeItem>();
//				DeferredCommand.addCommand(new IncrementalCommand(){
//					private int index = 0;
//					private int size = kids.size();
//					
//					public boolean execute() {
//						TreeNode tn = kids.get(index);
//						XObject xObj = tn.getXObject();
//						
//						String name;
//						if (xObj instanceof XElementNode) {
//							int count = ((XElementNode) xObj).getChildCount();
//							name = count == 0 ? xObj.getName() : xObj.getName() + " <i><font color=\"gray\">(" + count + ")</i></font>";
//						} else {
//							name = xObj.getName();
//						}
//
//						FastMSTreeItem item = new FastMSTreeItem(name);
//						if (tn.hasChildren()) {
//							item.becomeInteriorNode();
//						}
//						item.setModel(tn);
//						HasFastMSTreeItems parentItem = tree;
//						if (tn.getParent() != null) {
//							FastMSTreeItem pItem = parents.get(tn.getParent());  
//							if (pItem != null) {
//								parentItem = pItem;
//							}
//						}
//						parentItem.addItem(item);
//						parents.put(tn, item);
//						index++;
//						if (index >= size) {
//							LoadEvent le = new LoadEvent(null, parentNode);
//							tree.loaded(le);
//						}
//						return index < size;
//					}
//				});
//			}			
//		});
//		
////		WPaloServiceProvider.getInstance().loadChildren(sessionId, parentNode, new AsyncCallback <List <TreeNode>>() {
////			public void onSuccess(final List <TreeNode> kids) {
////				DeferredCommand.addCommand(new IncrementalCommand() {					
////					private int index = 0;
////					public boolean execute() {						
////						XObject xObj = kids.get(index).getXObject(); 
////						String name;
////						if (xObj instanceof XElementNode) {
////							int count = ((XElementNode) xObj).getChildCount();
////							name = count == 0 ? xObj.getName() : xObj.getName() + " <i><font color=\"gray\">(" + count + ")</i></font>";
////						} else {
////							name = xObj.getName();
////						}
////
////						FastMSTreeItem item = new FastMSTreeItem(name) {
////							public void ensureChildren() {
////								loadChildren(this, getModel());
////							}
////						};
////						if (kids.get(index).hasChildren()) {
////							item.becomeInteriorNode();
////						}
////						item.setModel(kids.get(index));
////						parentItem.addItem(item);
////						if (item.getParentItem() != null &&
////								item.getParentItem().getModel() != null) {
////							item.getParentItem().getModel().add(kids.get(index));
////						}
////						index++;
////						if (item.getParentItem() != null) {
////							if (item.getParentItem().getFinishHandler() != null && index >= kids.size()) {
////								item.getParentItem().getFinishHandler().onSuccess(null);
////							}							
////						}
////						if (index >= kids.size()) {
////							LoadEvent le = new LoadEvent(null, parentNode);
////							tree.loaded(le);
////						}
////						return index < kids.size();
////					}
////				});
////			}
////			
////			public void onFailure(Throwable arg0) {
////			}
////		});
//		
//	}
	
	/** 
	 * selects all currently visible elements. note: this only works if tree is
	 * a checkbox tree...
	 */
	public final void selectAll() {		
		nodeSelector.selectAll(true);
	}
	public final void selectAllVisible() {		
		nodeSelector.selectAllVisible(true);
	}
	
	public final void selectBranch() {
		nodeSelector.selectBranch();
	}
	
	/** 
	 * deselects all currently visible elements. note: this only works if tree is
	 * a checkbox tree...
	 */
	public final void deselectAll() {		
		nodeSelector.selectAll(false);
	}
	public final void deselectAllVisible() {
		boolean isDel = nodeSelector.isDelayed();
		nodeSelector.setDelayed(false);
		nodeSelector.selectAllVisible(false);
		nodeSelector.setDelayed(isDel);
	}
	
	public final void selectLevel(int lvl, boolean doIt, boolean add) {
		if(lvl < 0)
			return;
		if (!add) {
			deselectAllVisible();
		}				
		nodeSelector.selectBy(lvl, doIt);
	}
	
	public final void selectLeafs(boolean doIt, boolean add) {
		if (!add) {
			deselectAllVisible();
		}
		nodeSelector.selectLeafs(doIt);
	}
	/** 
	 * inverts the currently visible selection. note: this only works if tree is
	 * a checkbox tree...
	 */
	public final void invertSelection() {
		nodeSelector.invert();
	}
	
	public final void selectByRegEx(String regex, boolean doIt, boolean add) {
		if (!add) {
			deselectAllVisible();
			tree.fireSelectionNumberChanged();
		}		
		nodeSelector.selectBy(regex, doIt);
	}
	
	public final void expandNextLevel() {
		nodeSelector.expandNextLevel();
	}
	
	public final void expandAll() {
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.expandingLevel());
		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		final String sessionId = user.getSessionId();				
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>() {
			public void onSuccess(Void arg0) {
				tree.expandAll();
				((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
			}
		});
	}
	
	public final void collapseDeepestLevel() {
		nodeSelector.collapseDeepestLevel();
	}
	
	public final void collapseAll() {
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.collapsingLevel());
		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		final String sessionId = user.getSessionId();				
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>() {
			public void onSuccess(Void arg0) {
				tree.collapseAll();
				((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
			}
		});
	}
	
	public final XElement[] getSelectedElements() {
		LinkedHashSet<FastMSTreeItem> selectedNodes = //treeBinder.getCheckedSelection();
			tree.getSelectedItems();
		XElement[] selectedElements = new XElement[selectedNodes.size()];
		int index = 0;
		for(FastMSTreeItem node : selectedNodes)
			selectedElements[index++] = getElement(node);
		return selectedElements;
	}

	public final void apply(XElementNode[] initialSelection, DnDHierarchyTree otherTree, boolean showOnRight, String paths, XAxisHierarchy xAxisHierarchy) {
		if(initialSelection != null) {
			final ArrayList<XElementNode> flatSelection = toFlat(initialSelection);
			InitialTreeData data = new InitialTreeData();
			data.tree = otherTree;
			data.input = initialSelection;
			nodeSelector.applySelection(flatSelection, data, showOnRight, paths, xAxisHierarchy);
		} else {
			((Workbench) Registry.get(Workbench.ID)).hideWaitCursor();
		}
	}
	
	private final ArrayList<XElementNode> toFlat(XElementNode[] nodes) {
		ArrayList<XElementNode> flatStructure = new ArrayList<XElementNode>();
		addNodes(nodes, flatStructure);
		return flatStructure;
	}
	private final void addNodes(XElementNode[] nodes,
			ArrayList<XElementNode> flatStructure) {
		for(XElementNode node : nodes) {			
			flatStructure.add(node);
			if(node.hasChildren())
				addNodes(node.getChildren(), flatStructure);
		}		
	}

	public final void selectBy(String path, boolean doIt) {
		nodeSelector.selectBy(path, doIt, true);
	}
	public final void setSelection(XElement[] elements) {
		if(elements != null)
			nodeSelector.setSelection(elements);		
	}
	
	final ArrayList<TreeNode> getRootNodes() {
		ArrayList <TreeNode> list = new ArrayList<TreeNode>();
		for (FastMSTreeItem it: tree.getChildren()) {
			list.add(it.getModel());
		}
		return list;
	}
		
	final void addLoadListener(LoadListener listener) {
		tree.addLoadListener(listener);
	}
	final void removeLoadListener(LoadListener listener) {
		tree.removeLoadListener(listener);
	}

	final XElement getElement(FastMSTreeItem item) {
		return getElement(item.getModel());
	}
	final XElement getElement(TreeNode node) {
		if(node.getXObject() instanceof XElementNode) {
			XElementNode elNode = (XElementNode) node.getXObject();
			return elNode.getElement();
		}
		return (XElement)node.getXObject();
	}

	final FastMSTreeItem getItem(TreeNode node) {
		return node.getItem();
		//return (TreeItem)treeBinder.findItem(node);
	}
	
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
//						tree.onSelection(getItem(child), true, true, FastMSTree.ADD);
//				}
////				((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
//			}
//		};
//		return treeLoader;
//	}

	private final FastMSTree createTree(boolean multiSelect) {
		FastMSTree.addDefaultCSS();
		final FastMSTree tree = new FastMSTree(multiSelect);
//		tree.setAnimate(false);
//		tree.setSelectionModel(new TreeNodeSelectionModel(SelectionMode.MULTI));
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
		return tree;
	}
	
	public final void traverse(FastMSTreeItemVisitor visitor) {
		FastMSTreeItem root = tree.getRoot();
		traverse(root.getChildren(), null, visitor);
	}
	private final void traverse(ArrayList<FastMSTreeItem> items, FastMSTreeItem parent, FastMSTreeItemVisitor visitor) {
		if (items == null) {
			return;
		}
		for(FastMSTreeItem item : items) {
			if(item.isSelected())
				visitor.visit(item, parent);
			traverse(item.getChildren(), item, visitor);
		}
	}
	private final boolean isSelected(FastMSTreeItem item) {
		return item.isSelected();
	}
}

class TreeNodeSelectionModel extends TreeSelectionModel {

	public TreeNodeSelectionModel(SelectionMode mode) {
		super(mode);
	}
	public void select(TreeItem item, boolean keepExisting) {
		doSelect(new Items<TreeItem>(item), keepExisting, true);
	}
}

class TreeNodeSelector extends LoadListener {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();

	private final HierarchyTree tree;
//	private Filter filter;
	private final LinkedHashSet <XElement> selection = new LinkedHashSet<XElement>();
	private final LinkedHashSet <FastMSTreeItem> initialSelection = new LinkedHashSet<FastMSTreeItem>();
	private LinkedHashSet <XElementNode> activeSelection;
	private boolean delayedSelection;
	private final LinkedHashSet <FastMSTreeItem> nodesToBeSelected = new LinkedHashSet<FastMSTreeItem>();
	private final LinkedHashSet <FastMSTreeItem> nodesToBeDeselected = new LinkedHashSet<FastMSTreeItem>();
	private final SelectionTimer selTimer = new SelectionTimer();
	
	class SelectionTimer {
		int timeout;
		boolean reachedEnd = false;
		boolean started = false;
		
		SelectionTimer() {
			timeout = 500;
		}
				
		public void run() {
			started = true;
			String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
			final int size = nodesToBeSelected.size();
			final int deSize = nodesToBeDeselected.size();				
			WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, timeout, new AsyncCallback<Void>() {
				private final void performSelection() {
					if ((nodesToBeSelected.size() - size) == 0 &&
						(nodesToBeDeselected.size() - deSize) == 0) {
							reachedEnd = true;
							DeferredCommand.addCommand(new Command(){
								public void execute() {
									tree.getTree().selectTheseItems(nodesToBeSelected);									
									tree.getTree().deselectTheseItems(nodesToBeDeselected);
									nodesToBeDeselected.clear();
									nodesToBeSelected.clear();
									((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
								}
							}); 
					} else {						
						run();
					}
					started = false;					
				}
				
				public void onFailure(Throwable arg0) {
					performSelection();
				}

				public void onSuccess(Void arg0) {
					performSelection();
				}
			});
		}	
		
//		public void forceSelectionNow() {
//			tree.getTree().selectTheseItems(nodesToBeSelected);									
//			tree.getTree().deselectTheseItems(nodesToBeDeselected);
//			nodesToBeDeselected.clear();
//			nodesToBeSelected.clear();			
//		}
		
		public boolean isStarted() {
			return started;
		}
	}
	
	TreeNodeSelector(HierarchyTree tree, boolean delay) {
		this.delayedSelection = delay;		
		this.tree = tree;
		this.tree.addLoadListener(this);
	}
			
	public void setDelayed(boolean b) {
		delayedSelection = b;
	}
	
	public boolean isDelayed() {
		return delayedSelection;
	}
	
	public void loaderLoad(LoadEvent le) {
		TreeNode parent = (TreeNode) le.config;
//		traverse(parent, filter);
		if (activeSelection != null && activeSelection.size() == 0) {// && postponedSelection.size() == 0 && !mustDisplayRight) {
			tree.getTree().setSelectedItems(initialSelection);
//			tree.getSelectionModel().select(initialSelection);
		}				
	}
		
	final void applySelection(final ArrayList<XElementNode> selection, final InitialTreeData data, boolean showOnRight, final String paths, final XAxisHierarchy xAxisHierarchy) {
		if (showOnRight) {
			boolean hide = false;
			if(!selection.isEmpty()) {
				tree.getTree().setSelectedItems(null);
				if (paths != null) {
					data.tree.setInput(data.input, paths);	
				} else {
					data.tree.setInput(data.input, null);
				}				
			} else {
				hide = true;
			}
			data.tree.applyAlias(xAxisHierarchy);
			if (hide) {
				((Workbench) Registry.get(Workbench.ID)).hideWaitCursor();
			}
			return;
		}
		final Filter f = new LocalFilterSelectionFilter(paths, this);
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>() {
			public void onSuccess(Void arg0) {
				for(TreeNode root: tree.getRootNodes()) 
					traverse(root, f);
				data.tree.applyAlias(xAxisHierarchy);
				ensureTimer();
			}
		});
	}
			
	final void selectAllVisible(final boolean doIt) {
//		ArrayList<FastMSTreeItem> allItems = tree.getVisibleItems();
//		for(FastMSTreeItem item: allItems) {
//			select(item, doIt);
//		}
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.selectingChildren());
		Filter f = new DefaultFilter() {
			public boolean filter(FastMSTreeItem item) {				
				select(item, true);
				return true;
			}
		};
		
		for(TreeNode root : tree.getRootNodes())
			traverse(root.getItem(), f, false);
		ensureTimer();			

	}
	
	final void selectBranch() {
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.selectingChildren());
		Filter f = new DefaultFilter() {
			private final HashSet <FastMSTreeItem> selPar = new HashSet<FastMSTreeItem>();
			
			public boolean filter(FastMSTreeItem item) {				
				FastMSTreeItem par = item.getParentItem();
				if (par != null && par.isOpen() && (par.isSelected() || selPar.contains(par))) {
					selPar.add(item);
					select(item, true);
				}
				return true;
			}
		};
		
		for(TreeNode root : tree.getRootNodes())
			traverse(root.getItem(), f, false);
		ensureTimer();			
	}
	
	final void selectAll(final boolean doIt) {
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.loadingSelectingChildren());
		Filter f = new DefaultFilter() {
			public boolean filter(FastMSTreeItem item) {				
				select(item, doIt);
				return true;
			}
		};
		
		for(TreeNode root : tree.getRootNodes())
			traverse(root, f);
		ensureTimer();
	}
	
	/** inverts current selection */
	final void invert() {
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.selectingChildren());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>() {
			public void onSuccess(Void arg0) {
				ArrayList<FastMSTreeItem> allItems = tree.getVisibleItems();
				for(FastMSTreeItem item : allItems) {
					select(item,!item.isSelected());
				}
				((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
			}
		});
	}
	
	/** selects all nodes which name match the given regular expression */
	final void selectBy(final String regex, boolean doIt) {
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.selectingChildren());
		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		final String sessionId = user.getSessionId();				
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>() {
			public void onSuccess(Void arg0) {
				final String reg = parseWildcards(regex);
				final HashSet <Boolean> found = new HashSet<Boolean>();
				Filter f = new DefaultFilter() {			
					public boolean filter(FastMSTreeItem item) {				
						String name = item.getModel().getXObject().getName();
						if(name.matches(reg)) {
							if (found.isEmpty()) {
								found.add(true);
							}
							select(item, true);
						}
						return true;
					}
				};		
				for(TreeNode root : tree.getRootNodes())
					traverse(root.getItem(), f, true);
				ensureTimer();
				if (found.size() == 0) {
					MessageBox.info(constants.information(),
							constants.noMatchFound(), null);
				}
			}
		});
	}

	
	final void expandNextLevel() {		
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.expandingLevel());
		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		final String sessionId = user.getSessionId();				
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>() {
			public void onSuccess(Void arg0) {
				Filter f = new DefaultFilter() {
					public boolean filter(FastMSTreeItem item) {				
						if (!item.isOpen()) {
							item.setState(true);
						}
						return true;
					}
				};
				
				for(TreeNode root : tree.getRootNodes())
					traverseNextLevel(root.getItem(), f);
				ensureTimer();				
			}
		});
	}
	
	final void collapseDeepestLevel() {
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.collapsingLevel());
		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		final String sessionId = user.getSessionId();				
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>() {
			public void onSuccess(Void arg0) {
				final HashSet <FastMSTreeItem> itemsToCollapse =
					new HashSet<FastMSTreeItem>();
				final LinkedHashSet <FastMSTreeItem> itemsToDeselect =
					new LinkedHashSet<FastMSTreeItem>();
				Filter f = new DefaultFilter() {
					private int deepestLevel = -1;
					public boolean filter(FastMSTreeItem item) {				
						if (item.isOpen() && item.getChildCount() > 0) {
							if (item.getDepth() > deepestLevel) {
								deepestLevel = item.getDepth();
								itemsToCollapse.clear();
								itemsToDeselect.clear();
							}
							if (item.getDepth() == deepestLevel) {
								itemsToCollapse.add(item);
								itemsToDeselect.addAll(item.getChildren());
							}
						}
						return true;
					}
				};
				
				for(TreeNode root : tree.getRootNodes())
					traverseNextLevel(root.getItem(), f);
				tree.getTree().deselectTheseItems(itemsToDeselect);
				for (FastMSTreeItem item: itemsToCollapse) {
					item.setState(false);
				}
				ensureTimer();						
			}
		});
	}
	
	public final HierarchyTree getTree() {
		return tree;
	}
	
	private final void ensureTimer() {
		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		final String sessionId = user.getSessionId();		
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 500, new AsyncCallback<Void>() {
			private final void startTimer() {
				if (!selTimer.isStarted()) {
					selTimer.run();
				}				
			}
			public void onFailure(Throwable arg0) {
				startTimer();
			}

			public void onSuccess(Void arg0) {
				startTimer();
			}
		});		
	}
	
	/** selects all nodes which depth equals the given level */
	final void selectBy(int level, final boolean doIt) {
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.selectingChildren());
		
		final int depth = level; // - 1;
		Filter f = new DefaultFilter() {
			public boolean filter(FastMSTreeItem item) {
				try {					
					int elDepth = item.getDepth();
					if (elDepth  == depth) {
						select(item, doIt);
					}				
					return elDepth < depth && !item.isLeafNode();
				} catch (Throwable t) {
					t.printStackTrace();
					return false;
				}
			}
		};
		try {
			for(FastMSTreeItem root : tree.getVisibleItems())
				traverse(root, f, false);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		ensureTimer();
	}
	
	final void selectBy(final String path, final boolean doIt, final boolean isPath) {		
		Filter f = new DefaultFilter() {
			private String currentPath = path;
			private HashSet <FastMSTreeItem> toBeExpanded = new HashSet<FastMSTreeItem>();
			
			public boolean shouldExpand(FastMSTreeItem node) {
				boolean result = toBeExpanded.contains(node); 
				return result;
			}
			
			public boolean traverseChild(TreeNode node) {
				if (currentPath == null || currentPath.isEmpty()) {
					return false;
				}
				XObject xObj = node.getXObject();
				if (xObj != null && xObj instanceof XElementNode) {
					xObj = ((XElementNode) xObj).getElement();
				}
				if (xObj == null) {
					return false;
				}
				String id = xObj.getId();				
				boolean r = currentPath.startsWith(id + "/") || currentPath.equals(id);
				return r;
			}
			
			public boolean filter(final FastMSTreeItem node) {				
				if (currentPath == null || currentPath.length() == 0) {
					return false;
				}
				String oldCurrentPath = currentPath;
				int index = currentPath.indexOf("/");
				String id = index == -1 ? currentPath : currentPath.substring(0, index);
				if (index != -1) {
					if ((index + 1) < currentPath.length()) {
						currentPath = currentPath.substring(index + 1);
					} else {
						currentPath = null;
					}
				} else {
					currentPath = null;
				}				
				XElement element = tree.getElement(node);
				if (element != null && element.getId().equals(id)) {
					if (currentPath == null || currentPath.length() == 0) {
						select(node, doIt);
						if (doIt) {
								DeferredCommand.addCommand(new Command(){
									public void execute() {
										tree.getTree().ensureItemVisible(node);
										((Workbench) Registry.get(Workbench.ID)).hideWaitCursor();
//										((LayoutContainer) tree.getTree().getParent()).scrollIntoView(node);
									}
								});																
//							tree.getTree().scrollIntoView(node);
						}
						return false;
					}
					toBeExpanded.add(node);
					return true;
				}
				currentPath = oldCurrentPath;
				return true;
			}			
		};
		for (TreeNode root: tree.getRootNodes()) {
			selectionTraverse(root, f);
		}
	}
	
	/** selects or deselects all nodes which are leafs */
	final void selectLeafs(final boolean doIt) {
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.loadingSelectingChildren());
		Filter f = new DefaultFilter() {
			public boolean filter(FastMSTreeItem item) {
				if(item.isLeafNode()) {
					select(item, doIt);
				}
				return true;
			}
		};
		for(FastMSTreeItem root : tree.getVisibleItems())
			traverse(root, f, false);
		ensureTimer();
	}
	
	final void setSelection(XElement[] selectedElements) {
		selection.clear();
		selection.addAll(Arrays.asList(selectedElements));
	}

	final boolean isSelected(TreeNode node) {
		XElement element = tree.getElement(node);
		if (selection.contains(element))
				return true;
		return false;
	}

	final void select(FastMSTreeItem item, boolean doIt) {
		if (delayedSelection) {
			if (doIt) {
				nodesToBeSelected.add(item);
			} else {
				nodesToBeDeselected.add(item);
			}
			if (!selTimer.isStarted()) {
				selTimer.run();
			}
//			if (nodesToBeSelected.size() > 1000 ||
//				nodesToBeDeselected.size() > 1000) {
//				selTimer.forceSelectionNow();
//			}
			return;
		}
		try {
			if (doIt) {				
				tree.getTree().onSelection(item, false, false, FastMSTree.ADDONLY);
			} else {
				tree.getTree().onDeselection(item);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
//		TreeNodeSelectionModel selectionModel = tree.getSelectionModel();		
//		if(doIt) {
//			//bug in gxt? we can select items several times and to deselect it 
//			//we have to do this several times too!!
//			if(!selectionModel.isSelected(item))
//				selectionModel.select(item, true);
//		} else {
//			if (selectionModel.isSelected(item)) 
//				selectionModel.deselect(item);
//		}
	}
	
	private final void traverse(final TreeNode node, final Filter filter) {
		final FastMSTreeItem item = tree.getItem(node);
		if(item == null || filter == null)
			return;
		if (filter.filter(item)) {			
			boolean doTraverse = true;
			if (filter.shouldExpand(item)) {
				if (tree instanceof LoDHierarchyTree) {
					if (!item.isOpen() && !item.hasBeenOpened()) {
						doTraverse = false;
						item.setFinishHandler(new AsyncCallback<Void>() {
							public void onFailure(Throwable arg0) {
								item.setFinishHandler(null);
							}
							
							public void onSuccess(Void arg0) {
								item.setFinishHandler(null);
								for (TreeNode child : node.getChildren()) {
									if (filter.traverseChild(child)) {
										traverse(child, filter);
									}
								}
							}
						});
					}
				}
				item.setState(true, true);
			}		
			if (doTraverse) {
				for (TreeNode child : node.getChildren()) {
					if (filter.traverseChild(child)) {
						traverse(child, filter);
					}
				}
			}
		}
	}
	
	private final void traverse(final FastMSTreeItem item, final Filter filter, boolean traverseClosed) {
		if(item == null || filter == null)
			return;
		if (filter.filter(item)) {			
			if (item.isOpen() || traverseClosed) {
				for (FastMSTreeItem child : item.getChildren()) {
					if (filter.traverseChild(child.getModel())) {
						traverse(child, filter, traverseClosed);
					}
				}
			}
		}
	}

	private final void traverseNextLevel(final FastMSTreeItem item, final Filter filter) {
		if(item == null || filter == null)
			return;
		boolean cont = item.isOpen();
		if (filter.filter(item)) {			
			if (cont) {
				for (FastMSTreeItem child : item.getChildren()) {
					if (filter.traverseChild(child.getModel())) {
						traverseNextLevel(child, filter);
					}
				}
			}
		}
	}

	private final void selectionTraverse(final TreeNode node, final Filter filter) {
		final FastMSTreeItem item = tree.getItem(node);
		if(item == null || filter == null)
			return;
		if (filter.filter(item)) {			
			boolean doTraverse = filter.shouldExpand(item);
			if (doTraverse) {
				if (tree instanceof LoDHierarchyTree) {
					if (!item.isOpen() && !item.hasBeenOpened()) {
						doTraverse = false;
						item.setFinishHandler(new AsyncCallback<Void>() {
							public void onFailure(Throwable arg0) {
								item.setFinishHandler(null);
							}
							
							public void onSuccess(Void arg0) {
								item.setFinishHandler(null);
								for (TreeNode child : node.getChildren()) {
									if (filter.traverseChild(child)) {
										selectionTraverse(child, filter);
										break;
									}
								}
							}
						});
					}
				}
				item.setState(true, true);
			}		
			if (doTraverse) {
				for (TreeNode child : node.getChildren()) {
					if (filter.traverseChild(child)) {
						selectionTraverse(child, filter);
						break;
					}
				}
			}
		}
	}

	
    private final String parseWildcards(String str) {
        //simple try:
        str = str.replaceAll("\\*",".*");
        str = str.replaceAll("\\?",".?");
        return str;
    }
}

interface Filter {
	
	/** returns <code>true</code> to go on with tree traversal, false otherwise */
	boolean filter(FastMSTreeItem node);
	boolean traverseChild(TreeNode child);
	boolean shouldExpand(FastMSTreeItem item);
}

abstract class DefaultFilter implements Filter {
	public boolean traverseChild(TreeNode child) {
		return true;
	}
	
	public boolean shouldExpand(FastMSTreeItem item) {
		return !item.isLeafNode();
	}
}

class InitialTreeData {
	DnDHierarchyTree tree;
	XElementNode[] input;
}

