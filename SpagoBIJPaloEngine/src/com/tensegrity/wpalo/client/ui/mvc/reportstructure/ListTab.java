/*
*
* @file ListTab.java
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
* @version $Id: ListTab.java,v 1.19 2010/04/15 09:55:22 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.client.ui.mvc.reportstructure;

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
import com.tensegrity.palo.gwt.core.client.models.palo.XDatabase;
import com.tensegrity.palo.gwt.core.client.models.palo.XDimension;
import com.tensegrity.palo.gwt.core.client.models.palo.XHierarchy;
import com.tensegrity.palo.gwt.core.client.models.reports.XDynamicReportFolder;
import com.tensegrity.palo.gwt.core.client.models.subsets.XSubset;
import com.tensegrity.wpalo.client.WPaloServiceProvider;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.serialization.XConstants;
import com.tensegrity.wpalo.client.serialization.XNode;
import com.tensegrity.wpalo.client.ui.model.TreeLoaderProxy;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.widgets.EditorTab;

class ListTab extends EditorTab {
	private TreeLoader<TreeNode> treeLoader;
	private XUser user;
	private Tree listTree;
	
	ListTab() {
		super("Lists");
		setText("Lists");
		setIconStyle("icon-dim");
		setAutoWidth(true);
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
		listTree = createTree(treeLoader);
		panel.add(listTree);		
		panel.setVisible(true);
		TreeNode node = new ListTreeModel(user).getRoot();
		if (node != null) {
			treeLoader.load(node);
		}
		listTree.setVisible(true);
		
		panel.setTopComponent(createButtonBar());
		
		return panel;
	}	

	private final ToolBar createButtonBar() {
		ToolBar toolbar = new ToolBar();
		
		TextToolItem assignView = new TextToolItem("Assign to Folder");
		toolbar.add(assignView);
		assignView.addSelectionListener(new SelectionListener<ComponentEvent>(){
			public void componentSelected(ComponentEvent ce) {
				final TreeItem ti = ReportStructureEditor.view.getTree().getSelectedItem();
				if (ti == null) {
					return;
				}
				final XObject remObj = ((TreeNode) ti.getModel()).getXObject();
				if (!(remObj instanceof XDynamicReportFolder)) {
					return;
				}
								
				TreeItem item = listTree.getSelectedItem();
				if (item == null) {
					return;
				}
				
				final XObject obj = ((TreeNode) item.getModel()).getXObject();
				if (obj == null) {
					return;
				}
				if (!(obj instanceof XDimension) &&
					!(obj instanceof XSubset) &&
					!(obj instanceof XHierarchy)) {
					return;
				}

				final XDynamicReportFolder xdf = (XDynamicReportFolder) remObj;
				WPaloServiceProvider.getInstance().assignSubsetOrDimension(
						xdf, obj, user, new Callback <Boolean>(){
							public void onSuccess(Boolean arg0) {								
								TreeNode tn = (TreeNode) ti.getModel();
//								if (tn != null) {
//									tn = (TreeNode) tn.getParent();
//								}
								if (tn != null) {
									if (obj instanceof XHierarchy) {
										xdf.setSourceSubset(null);
										xdf.setSourceHierarchy(((XHierarchy) obj));
									} else if (obj instanceof XSubset) {
										xdf.setSourceSubset((XSubset) obj);
									} else if (obj instanceof XHierarchy) {
										xdf.setSourceHierarchy((XHierarchy) obj);
									}
									ReportStructureEditor.view.reload(tn);
								}
							}
						});
			}
		});
		return toolbar;		
	}
	
	private final Tree createTree(TreeLoader<TreeNode> loader) {
		Tree tree = new Tree();
		tree.setIndentWidth(6);
		TreeStore<TreeNode> store = new TreeStore<TreeNode>(loader);
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
			if (model.getXObject() instanceof XAccount) {
				item.setIconStyle("icon-folder");
			} else if (model.getXObject() instanceof XDatabase) {
				item.setIconStyle("icon-db");
			} else if (model.getXObject() instanceof XDimension ||
					   model.getXObject() instanceof XHierarchy) {
				item.setIconStyle("icon-dim");
			} else if (model.getXObject() instanceof XSubset) {
				item.setIconStyle("icon-subset");
			}
			return item;
		}
	}	
	
	class ListTreeModel {
		private final TreeNode root;
		
		public ListTreeModel(XUser user) {
			//root node
			XNode rootNode = new XNode(user, XConstants.TYPE_ROOT_NODE);
			rootNode.setId("ReportNavigatorView_ListTab#RootNode");
			rootNode.setHasChildren(true);
			
			root = new TreeNode(null, rootNode);
		}
		
		public final TreeNode getRoot() {
			return root;
		}
	}	
}
