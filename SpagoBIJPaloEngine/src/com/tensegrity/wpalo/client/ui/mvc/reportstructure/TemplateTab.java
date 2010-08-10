/*
*
* @file TemplateTab.java
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
* @version $Id: TemplateTab.java,v 1.21 2010/04/15 09:55:22 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.client.ui.mvc.reportstructure;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.reports.XReport;
import com.tensegrity.palo.gwt.core.client.models.reports.XReportFolder;
import com.tensegrity.wpalo.client.WPaloServiceProvider;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.serialization.XConstants;
import com.tensegrity.wpalo.client.serialization.XNode;
import com.tensegrity.wpalo.client.serialization.templates.XTemplate;
import com.tensegrity.wpalo.client.serialization.templates.XWorkbook;
import com.tensegrity.wpalo.client.ui.model.TreeLoaderProxy;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.widgets.EditorTab;

class TemplateTab extends EditorTab {
	private TreeLoader<TreeNode> treeLoader;
	private XUser user;
	private Tree reportsTree;
	private TreeStore<TreeNode> store;
	private final VariableTab vTab;
	
	TemplateTab(VariableTab vTab) {
		super("Report Templates");
		this.vTab = vTab;
		setText("Report Templates");
		setIconStyle("icon-sheet");
		setClosable(false);
		setScrollMode(Scroll.AUTO);		
	}

	public boolean save(XObject input) {
		return true;
	}
	public void saveAs(String name, XObject input) {		
	}

	public void set(XObject input) {
		if (input instanceof XUser) {
			user = (XUser) input;
			removeAll();
			add(createPanel());
		}
	}	
	
	void reload(XObject obj) {
		if (store == null) {
			return;
		}
		List <TreeNode> nodes = store.getAllItems();
		for (TreeNode n: nodes) {
			if (obj.equals(n.getXObject())) {
				store.getLoader().loadChildren(n);
				break;
			}
		}
	}
	
	private final ContentPanel createPanel() {
		ContentPanel panel = new ContentPanel();
		panel.setScrollMode(Scroll.AUTO);
		panel.setHeaderVisible(false);
		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		panel.setStyleAttribute("padding", "20");
		panel.setLayout(new FlowLayout());
		panel.setWidth("100%");
		panel.setHeight("100%");				
		
		//create the tree which displays the data:
		treeLoader = new BaseTreeLoader<TreeNode>(new TreeLoaderProxy()){
			public boolean hasChildren(TreeNode data) {
				return data != null && data.getXObject() != null &&
				       data.getXObject().hasChildren();
			}
		};
		reportsTree = createTree(treeLoader);

		panel.add(reportsTree);		
		panel.setVisible(true);
		TreeNode node = new ReportTreeModel(user).getRoot();
		if (node != null) {
			treeLoader.load(node);
		}
		reportsTree.setVisible(true);
		
		panel.setTopComponent(createButtonBar());
		
		return panel;
	}	

	private final ToolBar createButtonBar() {
		ToolBar toolbar = new ToolBar();
		
		TextToolItem assignView = new TextToolItem("Add to Folder", "icon-assign-to-folder");
		toolbar.add(assignView);
		assignView.addSelectionListener(new SelectionListener<ComponentEvent>(){
			public void componentSelected(ComponentEvent ce) {
				final TreeItem ti = ReportStructureEditor.view.getTree().getSelectedItem();
				if (ti == null) {
					return;
				}
				XObject remObj = ((TreeNode) ti.getModel()).getXObject();
				if (!(remObj instanceof XReportFolder)) {
					return;
				}
								
				List <TreeItem> items = reportsTree.getSelectedItems();
				if (items == null || items.size() == 0) {
					return;
				}
				
				List <XObject> objs = new ArrayList <XObject> ();
				for (TreeItem it: items) {
					XObject obj = ((TreeNode) it.getModel()).getXObject();
					if (obj == null) {
						continue;
					}
					if (!(obj instanceof XTemplate) &&
						!(obj instanceof XWorkbook) &&
						!(obj instanceof XView)) {
						continue;
					}
					objs.add(obj);
				}
				
				XReportFolder xrf = (XReportFolder) remObj;
				WPaloServiceProvider.getInstance().addReceivers(xrf, objs.toArray(new XObject[0]), user, new Callback<XReport []>(){
					public void onSuccess(XReport [] reports) {
						if (reports != null && reports.length > 0) {
							ReportStructureEditor.view.getTreeLoader().loadChildren((TreeNode) ti.getModel());
							for (XReport rep: reports) {
								vTab.saveReport(rep);
							}
						}						
					}
				});
			}
		});
		
		TextToolItem addSheet = new TextToolItem("", "icon-add");
		toolbar.add(addSheet);
		addSheet.setToolTip("Create new Workbook");
		addSheet.addSelectionListener(new CreateNewSheet(reportsTree));
		
//		TextToolItem addSheet = new TextToolItem("", "icon-add");
//		toolbar.add(addSheet);
//		TextToolItem addView = new TextToolItem("", "icon-create-view");
//		toolbar.add(addView);
//
//		toolbar.add(new SeparatorToolItem());
//		TextToolItem delFolder = new TextToolItem("", "icon-delete-folder");
//		toolbar.add(delFolder);
		return toolbar;		
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
			}
		});
		return tree;
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
//			if (model.getXObject() != null && model.getXObject() instanceof XAccount) {
//				((XAccount) model.getXObject()).setLoadWorkbooks(true);
//			}
			if (model.getXObject() instanceof XWorkbook) {
				item.setIconStyle("icon-sheet");
			} else if (model.getXObject() instanceof XTemplate) {
				item.setIconStyle("icon-template");
			} else if (model.getXObject() instanceof XView) {
				item.setIconStyle("icon-view");
			}
//			ContentPanel cp = new ContentPanel();
//			cp.setCollapsible(false);
//			cp.add(item);
//			Draggable d = new Draggable(cp);
//			d.addDragListener(new DragListener(){
//				public void dragStart(DragEvent de) {
//				}
//			});
			return item;
		}
	}	
	
	class ReportTreeModel {
		private final TreeNode root;
		
		public ReportTreeModel(XUser user) {
			//children:
			XNode sheetTemplates = new XNode(user, XConstants.TYPE_SHEET_TEMPLATES_NODE);
			sheetTemplates.setName("Spreadsheet Reports");
			sheetTemplates.setHasChildren(true);
			sheetTemplates.setId("ReportNavigatorView#SheetTemplatesNode");
			
			XNode adhocTemplates = new XNode(user, XConstants.TYPE_ADHOC_TEMPLATES_NODE);
			adhocTemplates.setName("Adhoc Reports");
			adhocTemplates.setHasChildren(true);
			adhocTemplates.setId("ReportNavigatorView#AdhocTemplatesNodes");
			
			//root node
			XNode rootNode = new XNode(user, XConstants.TYPE_ROOT_NODE);
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
}
