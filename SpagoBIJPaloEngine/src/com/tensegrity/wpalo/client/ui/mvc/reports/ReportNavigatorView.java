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
package com.tensegrity.wpalo.client.ui.mvc.reports;

import java.util.ArrayList;
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
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.reports.XStaticReportFolder;
import com.tensegrity.wpalo.client.WPalo;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.serialization.XConstants;
import com.tensegrity.wpalo.client.serialization.XNode;
import com.tensegrity.wpalo.client.serialization.templates.XTemplate;
import com.tensegrity.wpalo.client.ui.model.TreeLoaderProxy;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.reportstructure.CreateNewSheet;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>AdminNavigator</code> TODO DOCUMENT ME
 * 
 * @version $Id: ReportNavigatorView.java,v 1.24 2010/02/12 13:49:50 PhilippBouillon Exp $
 */
public class ReportNavigatorView extends View {

	/** id to access a navigator instance via the global {@link Registry} */
	public static final String ID = "com.tensegrity.wpalo.reports.reportnavigator";

	private ContentPanel navigator;
	private TreeLoader<TreeNode> treeLoader;
	private XUser user;
	private TreeStore<TreeNode> store;
	
	public ReportNavigatorView(Controller controller) {
		super(controller);
		//registry serves as a global context:
		Registry.register(ID, this);
	}

	public final XView[] getViews() {
		List<XView> views = new ArrayList<XView>();
		List<TreeNode> items = store.getAllItems();
		for(TreeNode node : items) {
			if(node.getXObject() instanceof XView) {
				views.add((XView)node.getXObject());
			}
		}
		return views.toArray(new XView[0]);
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
						
			
			Dispatcher.forwardEvent(WPaloEvent.EXPANDED_REPORT_SECTION,
					new ReportTreeModel(user).getRoot());
			
			break;
		case WPaloEvent.EXPANDED_REPORT_SECTION:
			//load tree data
			TreeNode node = (TreeNode) event.data;
			if (node != null) {
				treeLoader.load(node);
			}
		case WPaloEvent.UPDATE_WORKBOOKS:
			if (event.data instanceof XObject) {
				reload((XObject) event.data);
			}
			break;
		}
	}

	void reload(XObject obj) {
		List <TreeNode> nodes = store.getAllItems();
		for (TreeNode n: nodes) {
			if (obj.equals(n.getXObject())) {
				store.getLoader().loadChildren(n);
				break;
			}
		}
	}
	
	void reload(TreeNode node) {
		if (node.getParent() == null) {
			store.getLoader().load(node);
		} else {
			store.getLoader().loadChildren(node.getParent());
		}
	}	
	
	private final void initUI() {		
		navigator = new ContentPanel();
		navigator.setHeading(WPalo.i18n.reportNavigatorView_heading());
//				"Report Templates");
		navigator.setScrollMode(Scroll.AUTO);
		// connect with dispatcher:
		navigator.addListener(Events.Expand, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				Dispatcher.get().dispatch(WPaloEvent.EXPANDED_REPORT_SECTION);
			}
		});

		//create the tree which displays the data:
		treeLoader = new BaseTreeLoader<TreeNode>(new TreeLoaderProxy()){
			public boolean hasChildren(TreeNode data) {
				return data != null && data.getXObject() != null &&
				       data.getXObject().hasChildren();
			}
		};
		final Tree reportsTree = createTree(treeLoader);
		
		ToolBar toolbar = new ToolBar();
		TextToolItem addFolder = new TextToolItem("", "icon-create-folder");
		addFolder.setToolTip(WPalo.i18n.reportNavigatorView_addFolderToolTip());
//				"Create new Folder");
		toolbar.add(addFolder);
		
		TextToolItem addSheet = new TextToolItem("", "icon-create-sheet");
		toolbar.add(addSheet);
		addSheet.setToolTip(WPalo.i18n.reportNavigatorView_addWorkbookTemplateToolTip());
//				"Create new Workbook Template");
		addSheet.addSelectionListener(new CreateNewSheet(reportsTree));
		TextToolItem addView = new TextToolItem("", "icon-create-view");
		addView.setToolTip(WPalo.i18n.reportNavigatorView_addAdhocViewTemplateToolTip());
//				"Create new AdHoc View Template");
		toolbar.add(addView);

		toolbar.add(new SeparatorToolItem());
		TextToolItem delItems = new TextToolItem("", "icon-delete");
		delItems.setToolTip(WPalo.i18n.reportNavigatorView_deleteItemsToolTip());
//				"Delete items");
		toolbar.add(delItems);
		delItems.addSelectionListener(new SelectionListener<ComponentEvent>(){
			public void componentSelected(ComponentEvent ce) {
			}
		});
		navigator.setTopComponent(toolbar);
		
		reportsTree.addListener(Events.OnClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				TreeNode node = (TreeNode) reportsTree.getSelectedItem().getModel();
				if (node.getXObject() instanceof XView) {
					fireEvent(new AppEvent<TreeNode>(WPaloEvent.SHOW_TEMPLATE_VIEW, node));
				}			
			}			
		});
		reportsTree.addListener(Events.OnDoubleClick, new Listener<BaseEvent>(){
			public void handleEvent(BaseEvent be) {
				TreeNode node = (TreeNode) reportsTree.getSelectedItem().getModel();
				if (node.getXObject() instanceof XTemplate) {
					fireEvent(new AppEvent<TreeNode>(WPaloEvent.EDIT_TEMPLATE_ITEM, node));
				}
				else if (node.getXObject() instanceof XView) {
					fireEvent(new AppEvent<TreeNode>(WPaloEvent.EDIT_TEMPLATE_VIEW, node));
				}
			}
		});

		navigator.add(reportsTree);
		
		Workbench wb = (Workbench)Registry.get(Workbench.ID);
		wb.addToViewPanel(navigator);
	}

	private final Tree createTree(TreeLoader<TreeNode> loader) {
		Tree tree = new Tree();
		tree.setIndentWidth(6);
		store = new TreeStore<TreeNode>(loader);
		TreeBinder<TreeNode> binder = new TreeNodeBinder(tree, store);
		binder.setDisplayProperty("name");
		binder.setAutoSelect(true);
		binder.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				TreeNode node = (TreeNode) se.getSelectedItem(); // single selection
//				switch (node.getType()) {
//				case TreeNodeType.USER_ITEM:
//					fireEvent(new AppEvent<TreeNode>(WPaloEvent.EDIT_USER_ITEM, node));
//					break;
//				case TreeNodeType.GROUP_ITEM:
//					fireEvent(new AppEvent<TreeNode>(WPaloEvent.EDIT_GROUP_ITEM, node));
//					break;
//				case TreeNodeType.ROLE_ITEM:
//					fireEvent(new AppEvent<TreeNode>(WPaloEvent.EDIT_ROLE_ITEM, node));
//					break;
//				}
			}
		});
		return tree;
	}
}

class TreeNodeBinder extends TreeBinder<TreeNode> {

	TreeNodeBinder(Tree tree, TreeStore<TreeNode> store) {
		super(tree, store);
	}

	protected TreeItem createItem(TreeNode model) {
		TreeItem item = super.createItem(model);		
		
		if (model.getXObject() instanceof XAccount) {
			model.set("name", ((XAccount) model.getXObject()).getConnection().getName());
		}

		if (model.getXObject() instanceof XTemplate) {
			item.setIconStyle("icon-sheet");
		}
		
		if (model.getXObject() instanceof XView) {
			item.setIconStyle("icon-view");
		}
		
		if (model.getType().equals(XStaticReportFolder.class.getName())) {
			item.setIconStyle("icon-group");
		}
		
		return item;
	}
}

class ReportTreeModel {
	private final TreeNode root;
	
	public ReportTreeModel(XUser user) {
		//children:
		XNode sheetTemplates = new XNode(user, XConstants.TYPE_SHEET_TEMPLATES_NODE);
		sheetTemplates.setName(WPalo.i18n.reportNavigatorView_sheetTemplatesName());
//				"Sheet Templates");
		sheetTemplates.setHasChildren(true);
		sheetTemplates.setId("ReportNavigatorView#SheetTemplatesNode");
		
		XNode adhocTemplates = new XNode(user, XConstants.TYPE_ADHOC_TEMPLATES_NODE);
		adhocTemplates.setName(WPalo.i18n.reportNavigatorView_adhocTemplatesName());
//				"Adhoc Templates");
		adhocTemplates.setHasChildren(true);
		adhocTemplates.setId("ReportNavigatorView#AdhocTemplatesNodes");
		
		//root node
		XNode rootNode = new XNode(null, XConstants.TYPE_ROOT_NODE);
		rootNode.setId("ReportNavigatorView#RootNode");
		rootNode.addChild(sheetTemplates);
		rootNode.addChild(adhocTemplates);
		rootNode.setHasChildren(true);
		
		root = new TreeNode(null, rootNode);
	}
	
	public final TreeNode getRoot() {
		return root;
	}
}