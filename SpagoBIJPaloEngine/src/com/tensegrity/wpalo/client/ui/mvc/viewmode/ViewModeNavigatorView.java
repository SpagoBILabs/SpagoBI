/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.viewmode;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.folders.XFolder;
import com.tensegrity.palo.gwt.core.client.models.reports.XDynamicReportFolder;
import com.tensegrity.palo.gwt.core.client.models.reports.XReport;
import com.tensegrity.palo.gwt.core.client.models.reports.XStaticReportFolder;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.serialization.XConstants;
import com.tensegrity.wpalo.client.serialization.XNode;
import com.tensegrity.wpalo.client.ui.model.TreeLoaderProxy;
import com.tensegrity.wpalo.client.ui.model.TreeNode;

/**
 * <code>AdminNavigator</code> TODO DOCUMENT ME
 * 
 * @version $Id: ViewModeNavigatorView.java,v 1.7 2009/12/17 16:14:20 PhilippBouillon Exp $
 */
public class ViewModeNavigatorView extends View {

	private ContentPanel navigator;
	private TreeLoader<TreeNode> treeLoader;
	private XUser user;
	private Tree reportsTree;
	TreeNode rootNode;
	private TreeStore<TreeNode> store;
	
	public ViewModeNavigatorView(Controller controller) {
		super(controller);
	}
		
	void reload(TreeNode node) {
		if (node.getParent() == null) {
			store.getLoader().load(node);
		} else {
			store.getLoader().loadChildren(node); //.getParent());
		}
	}
			
	@Override
	protected void handleEvent(AppEvent event) {
		switch (event.type) {
		case WPaloEvent.OPEN_VIEW_MODE:
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
			Dispatcher.forwardEvent(WPaloEvent.EXPANDED_VIEW_REPORT_STRUCTURE_SECTION,
					rootNode);
									
//			Dispatcher.forwardEvent(WPaloEvent.EXPANDED_REPORT_STRUCTURE_SECTION,
//					new ReportTreeModel(user).getRoot());
			
			break;
		case WPaloEvent.EXPANDED_VIEW_REPORT_STRUCTURE_SECTION: //load tree data
			TreeNode node = (TreeNode) event.data;
			if (node != null) {
				treeLoader.load(node);
			}
			break;
		}
	}

	private final void initUI() {		
		navigator = new ContentPanel();
		navigator.setHeading("Berichte (ViewMode)");
		navigator.setScrollMode(Scroll.AUTO);
		// connect with dispatcher:
		navigator.addListener(Events.Expand, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				Dispatcher.get().dispatch(WPaloEvent.EXPANDED_VIEW_REPORT_STRUCTURE_SECTION);
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
		
		reportsTree.addListener(Events.OnDoubleClick, new Listener<BaseEvent>(){
			public void handleEvent(BaseEvent be) {
				TreeNode node = (TreeNode) reportsTree.getSelectedItem().getModel();
				if (node.getXObject() instanceof XReport) {
					fireEvent(new AppEvent<TreeNode>(WPaloEvent.VIEW_REPORT_EVENT, node));
				}
			}
		});
		
		navigator.add(reportsTree);
		navigator.addListener(Events.Expand, new Listener<BaseEvent>(){
			public void handleEvent(BaseEvent be) {
				fireEvent(new AppEvent<TreeNode>(WPaloEvent.VIEW_REPORT_EVENT, new TreeNode(null, user)));
			}
		});
		ViewModeWorkbench wb = (ViewModeWorkbench)Registry.get(ViewModeWorkbench.ID);
		wb.addToViewPanel(navigator);
	}
	
	private final Tree createTree(TreeLoader<TreeNode> loader) {
		Tree tree = new Tree();
		tree.setIndentWidth(6);
		store = new TreeStore<TreeNode>(loader);
		TreeBinder<TreeNode> binder = new TreeNodeBinder(tree, store, this);
		binder.setDisplayProperty("name");
		binder.setAutoSelect(true);
		return tree;
	}
}

class TreeNodeBinder extends TreeBinder<TreeNode> {

	private final ViewModeNavigatorView view;
	
	TreeNodeBinder(Tree tree, TreeStore<TreeNode> store, ViewModeNavigatorView view) {
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
		if (model.getType().equals(XStaticReportFolder.class.getName())) {
			IconListener staticListener = new IconListener(item, "static-folder");
			item.addListener(Events.Expand, staticListener);
			item.addListener(Events.Collapse, staticListener);
			setIcon(item, "static-folder");
			XStaticReportFolder sf = (XStaticReportFolder) model.getXObject();
			sf.setReturnComputedKids(true);
		} else if (model.getType().equals(XDynamicReportFolder.class.getName())) {
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
			df.setReturnComputedKids(true);		
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