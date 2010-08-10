/*
*
* @file ReportStructureNavigatorView.java
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
* @version $Id: ReportStructureNavigatorView.java,v 1.20 2010/02/12 13:49:50 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.reportstructure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToggleToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.reports.XDynamicReportFolder;
import com.tensegrity.palo.gwt.core.client.models.reports.XReport;
import com.tensegrity.palo.gwt.core.client.models.reports.XStaticReportFolder;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.WPaloServiceProvider;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.serialization.XConstants;
import com.tensegrity.wpalo.client.serialization.XNode;
import com.tensegrity.wpalo.client.serialization.templates.XTemplate;
import com.tensegrity.wpalo.client.ui.dialog.RequestNameDialog;
import com.tensegrity.wpalo.client.ui.dialog.ResultListener;
import com.tensegrity.wpalo.client.ui.model.TreeLoaderProxy;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>AdminNavigator</code> TODO DOCUMENT ME
 * 
 * @version $Id: ReportStructureNavigatorView.java,v 1.20 2010/02/12 13:49:50 PhilippBouillon Exp $
 */
public class ReportStructureNavigatorView extends View {

	private ContentPanel navigator;
	private TreeLoader<TreeNode> treeLoader;
	private XUser user;
	private Tree reportsTree;
	private boolean quickViewEnabled = false;
	TreeNode rootNode;
	private TreeStore<TreeNode> store;
	private TextToolItem deleteItem;
	
	//private final IMessages messages;
	
	public ReportStructureNavigatorView(Controller controller) {
		super(controller);
		//messages = (IMessages) GWT.create(IMessages.class);	  
	}

	boolean isQuickView() {
		return quickViewEnabled;
	}
		
	void reload(TreeNode node) {
		if (node.getParent() == null) {
			store.getLoader().load(node);
		} else {
			store.getLoader().loadChildren(node.getParent());
		}
	}
		
	Tree getTree() {
		return reportsTree; 
	}
	
	TreeLoader <TreeNode> getTreeLoader() {
		return treeLoader;
	}
		
	@Override
	protected void handleEvent(AppEvent event) {
		switch (event.type) {
		case WPaloEvent.INIT:
			if (event.data instanceof XUser) {
				user = (XUser) event.data;
			}
			initUI();
			// Create initial tree structure:
			XNode root = new XNode(user, XConstants.TYPE_ROOT_REPORT_STRUCTURE_NODE);
			root.setName("DummyRoot");
			root.setId("ReportStructureNavigatorView#RootNode");
			root.setHasChildren(true);
			rootNode = new TreeNode(null, root);
			Dispatcher.forwardEvent(WPaloEvent.EXPANDED_REPORT_STRUCTURE_SECTION,
					rootNode);
									
//			Dispatcher.forwardEvent(WPaloEvent.EXPANDED_REPORT_STRUCTURE_SECTION,
//					new ReportTreeModel(user).getRoot());
			
			break;
		case WPaloEvent.EXPANDED_REPORT_STRUCTURE_SECTION: //load tree data
			TreeNode node = (TreeNode) event.data;
			if (node != null) {
				treeLoader.load(node);
			}
			break;
		}
	}

	private final void initUI() {		
		navigator = new ContentPanel();
		navigator.setHeading("Report Structure");
		navigator.setScrollMode(Scroll.AUTO);
		// connect with dispatcher:
		navigator.addListener(Events.Expand, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				Dispatcher.get().dispatch(WPaloEvent.EXPANDED_REPORT_STRUCTURE_SECTION);
			}
		});
		
		//create the tree which displays the data:
		treeLoader = new BaseTreeLoader<TreeNode>(new TreeLoaderProxy()){
			public boolean hasChildren(TreeNode data) {
				return data != null && data.getXObject() != null &&
				       data.getXObject().hasChildren();
			}
		};
		
		reportsTree = createTree(treeLoader);

		ToolBar toolbar = new ToolBar();
		TextToolItem addStaticFolder = new TextToolItem("", "icon-create-folder");
		addStaticFolder.setToolTip("Create a new static folder");
		addStaticFolder.addSelectionListener(new SelectionListener<ComponentEvent>(){
			public void componentSelected(ComponentEvent ce) {
				final TreeItem item = reportsTree.getSelectedItem();
				if (item == null) {
					return;
				}
				final XObject parent = ((TreeNode) item.getModel()).getXObject();
				RequestNameDialog rnd = new RequestNameDialog(
						"Create new static folder", 
						"Name",
						new ResultListener<String>(){
							public void requestCancelled() {								
							}

							public void requestFinished(String result) {
								WPaloServiceProvider.getInstance().
									createStaticFolder(result, parent, user, new Callback<XStaticReportFolder>(){
										public void onSuccess(XStaticReportFolder folder) {
											if (folder == null) {
												return;
											}
											treeLoader.loadChildren((TreeNode) item.getModel());
										}});
							}
						});
				rnd.show();
			}
		});
		toolbar.add(addStaticFolder);
		TextToolItem addDynamicFolder = new TextToolItem("", "icon-create-dynamic-folder");
		addDynamicFolder.setToolTip("Create a new dynamic folder");
		addDynamicFolder.addSelectionListener(new SelectionListener<ComponentEvent>(){
			public void componentSelected(ComponentEvent ce) {
				final TreeItem item = reportsTree.getSelectedItem();
				if (item == null) {
					return;
				}
				final XObject parent = ((TreeNode) item.getModel()).getXObject();
				RequestNameDialog rnd = new RequestNameDialog(
						"Create new dynamic folder", 
						"Name",
						new ResultListener<String>(){
							public void requestCancelled() {								
							}

							public void requestFinished(String result) {
								WPaloServiceProvider.getInstance().
									createDynamicFolder(result, parent, user, new Callback<XDynamicReportFolder>(){
										public void onSuccess(XDynamicReportFolder folder) {
											if (folder == null) {
												return;
											}
											treeLoader.loadChildren((TreeNode) item.getModel());
										}});
							}
						});
				rnd.show();
			}
		});
		toolbar.add(addDynamicFolder);
		
		TextToolItem addSheet = new TextToolItem("", "icon-create-sheet");
		toolbar.add(addSheet);
		addSheet.setToolTip("Create new Workbook");
		
		TextToolItem addView = new TextToolItem("", "icon-create-view");
		toolbar.add(addView);
		addSheet.setToolTip("Create new View");

		toolbar.add(new SeparatorToolItem());
		deleteItem = new TextToolItem("", "icon-delete");
		deleteItem.setToolTip("Delete Element");
		deleteItem.addSelectionListener(new SelectionListener<ComponentEvent>(){
			public void componentSelected(ComponentEvent ce) {
				if (quickViewEnabled) {
					return;
				}
				final List <TreeItem> items = reportsTree.getSelectedItems();
				if (items == null || items.size() == 0) {
					return;
				}
				List <XObject> objs = new ArrayList<XObject>();
				final HashSet <TreeNode> parents = new HashSet<TreeNode>();
				for (TreeItem it: items) {
					TreeNode tn = (TreeNode) it.getModel();
					objs.add(tn.getXObject());
					parents.remove(tn);
					parents.add((TreeNode) tn.getParent());
				}
				WPaloServiceProvider.getInstance().deleteElementTreeNodes(objs, user, new Callback<Boolean>(){
					public void onSuccess(Boolean b) {
						if (b) {
							for (TreeNode p: parents) {
								if (p != null) {
									reload(p);
								}
							}
						}
					}
				});
			}
		});
		toolbar.add(deleteItem);
		
		toolbar.add(new SeparatorToolItem());
		final ToggleToolItem quickView = new ToggleToolItem("Quick View");
		toolbar.add(quickView);
		quickView.addSelectionListener(new SelectionListener<ComponentEvent>(){
			public void componentSelected(ComponentEvent ce) {
				quickViewEnabled = quickView.isPressed();
				if (rootNode != null) {
					reload(rootNode);
				}
//				store.removeAll();
//				XNode root = new XNode(user, XConstants.TYPE_ROOT_REPORT_STRUCTURE_NODE);
//				root.setName("DummyRoot");
//				root.setId("ReportStructureNavigatorView#RootNode");
//				root.setHasChildren(true);
//				rootNode = new TreeNode(null, root);
//				treeLoader.load(rootNode);
			}
		});
		
		navigator.setTopComponent(toolbar);
		
		reportsTree.addListener(Events.OnDoubleClick, new Listener<BaseEvent>(){
			public void handleEvent(BaseEvent be) {
				TreeNode node = (TreeNode) reportsTree.getSelectedItem().getModel();
				if (node.getXObject() instanceof XTemplate) {
					fireEvent(new AppEvent<TreeNode>(WPaloEvent.EDIT_TEMPLATE_ITEM, node));
				}
			}
		});
		
		navigator.add(reportsTree);
		navigator.addListener(Events.Expand, new Listener<BaseEvent>(){
			public void handleEvent(BaseEvent be) {
				fireEvent(new AppEvent<TreeNode>(WPaloEvent.EDIT_REPORT_STRUCTURE, new TreeNode(null, user)));
			}
		});
		Workbench wb = (Workbench)Registry.get(Workbench.ID);
		wb.addToViewPanel(navigator);
	}

	private final void validateButtons(List <ModelData> selection) {
		if (selection == null || selection.size() == 0) {
			deleteItem.setEnabled(false);
			return;
		}
		for (ModelData node: selection) {
			TreeNode parent = (TreeNode) ((TreeNode) node).getParent();
			if (parent == null || parent.getXObject() == null) {
				deleteItem.setEnabled(false);
				return;
			}
			if (parent.getXObject().getId().equals("ReportStructureNavigatorView#RootNode")) {		
				deleteItem.setEnabled(false);
				return;
			}
		}
		deleteItem.setEnabled(true);
	}
	
	private final Tree createTree(TreeLoader<TreeNode> loader) {
		Tree tree = new Tree();
		tree.setIndentWidth(6);
		store = new TreeStore<TreeNode>(loader);
		TreeBinder<TreeNode> binder = new TreeNodeBinder(tree, store, this);
		binder.setDisplayProperty("name");
		binder.setAutoSelect(true);
		binder.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				validateButtons(se.getSelection());
				TreeNode node = (TreeNode) se.getSelectedItem();
				if (node != null && node.getXObject() != null) {
					fireEvent(new AppEvent <TreeNode>(
							WPaloEvent.SET_EDITOR_INPUT, node));
				}
			}
		});
		return tree;
	}
}

class TreeNodeBinder extends TreeBinder<TreeNode> {

	private final ReportStructureNavigatorView view;
	
	TreeNodeBinder(Tree tree, TreeStore<TreeNode> store, ReportStructureNavigatorView view) {
		super(tree, store);
		this.view = view;
	}

	private final void setIcon(TreeItem item, String baseName) {
		if (item.isExpanded()) {
			item.setIconStyle("icon-" + baseName + "-expanded");
		} else {
			item.setIconStyle("icon-" + baseName);
		}		
	}
	
	class IconListener implements Listener <BaseEvent> {
		private final String baseName;
		private final TreeItem item;
		
		public IconListener(TreeItem item, String baseName) {
			this.baseName = baseName;
			this.item = item;
		}
		
		public void handleEvent(BaseEvent be) {
			setIcon(item, baseName);
		}		
	}
	
	protected TreeItem createItem(TreeNode model) {
		final TreeItem item = super.createItem(model);
		if (model.getXObject() instanceof XAccount) {
			model.set("name", ((XAccount) model.getXObject()).getConnection().getName());
		}
		if (model.getType().equals(XStaticReportFolder.TYPE)) {
			IconListener staticListener = new IconListener(item, "static-folder");
			item.addListener(Events.Expand, staticListener);
			item.addListener(Events.Collapse, staticListener);
			setIcon(item, "static-folder");
			XStaticReportFolder sf = (XStaticReportFolder) model.getXObject();
			sf.setReturnComputedKids(view.isQuickView());
		} else if (model.getType().equals(XDynamicReportFolder.TYPE)) {
			IconListener dynamicListener = new IconListener(item, "dynamic-folder");
			item.addListener(Events.Expand, dynamicListener);
			item.addListener(Events.Collapse, dynamicListener);
			setIcon(item, "dynamic-folder");
			String text = item.getText();
			XDynamicReportFolder df = (XDynamicReportFolder) model.getXObject();
			if (df.getSourceHierarchy() == null && df.getSourceSubset() == null) {
				text += " (undef. hierarchy)";
			} else {
				if (df.getSourceSubset() != null) {
					text += " (" + df.getSourceSubset().getName() + ")";
				} else {
					text += " (" + df.getSourceHierarchy().getName() + ")";
				}
			}
			item.setText(text);
			df.setReturnComputedKids(view.isQuickView());		
		} else if (model.getType().equals(XReport.TYPE)) {
			String typeName;
			if (((XReport) model.getXObject()).getReceiverType().equals(
					XConstants.TYPE_FOLDER_ELEMENT_VIEW)) {
				typeName = "icon-view";
			} else {
				typeName = "icon-sheet";
			}
			item.setIconStyle(typeName);			
		}
		return item;
	}
}