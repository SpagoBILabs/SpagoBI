/*
*
* @file ViewSelectionTree.java
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
* @version $Id: ViewSelectionTree.java,v 1.13 2010/02/12 13:49:50 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.viewbrowser.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.CheckChangedEvent;
import com.extjs.gxt.ui.client.event.CheckChangedListener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.extjs.gxt.ui.client.widget.tree.TreeItemUI;
import com.extjs.gxt.ui.client.widget.tree.Tree.CheckCascade;
import com.google.gwt.user.client.Element;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.palo.XCube;
import com.tensegrity.palo.gwt.core.client.models.palo.XDatabase;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.serialization.XObjectWrapper;
import com.tensegrity.wpalo.client.services.cubeview.WPaloCubeViewServiceProvider;
import com.tensegrity.wpalo.client.ui.model.TreeLoaderProxy;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>ViewSelectionTree</code> TODO DOCUMENT ME
 * 
 * @version $Id: ViewSelectionTree.java,v 1.13 2010/02/12 13:49:50 PhilippBouillon Exp $
 **/
public class ViewSelectionTree {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();
	
	private final Tree tree;
	private TreeNode root;
	private TreeBinder<TreeNode> treeBinder;
	private final boolean doImport;
	private final boolean isSingle;
	private List <TreeNode> oldSelection = null;
	private final static HashMap <String, String> roles = new HashMap<String, String>();
	
	static {
		WPaloCubeViewServiceProvider.getInstance().initializeRoles(
				((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId(), new Callback<HashMap <String, String>>() {
					public void onSuccess(HashMap<String, String> result) {
						for (String key: result.keySet()) {
							roles.put(key, result.get(key));
						}
					}
				});
	}
	
	public ViewSelectionTree(boolean doImport, boolean isSingle) {
		this.doImport = doImport;
		this.isSingle = isSingle;
		tree = createTree();
	}

	public final Tree getTree() {
		return tree;
	}

	public final void setInput(XAccount xAccount) {
		tree.removeAll();
		root = createRootNode(xAccount);
		reloadTree();
	}

	private final TreeNode createRootNode(XAccount xAccount) {
		//we have to adjust XObject type so that it gets loaded by corresponding child loader:
		XObjectWrapper wrappedAccount = new XObjectWrapper(xAccount);
		wrappedAccount.setType(ViewImportDialog.XOBJECT_TYPE);
		TreeNode root = new TreeNode(null, wrappedAccount);
		return root;
		
	}

	@SuppressWarnings("unchecked")
	private final void reloadTree() {
		treeBinder.getTreeStore().getLoader().load(root);
	}

	public final XView[] getSelectedViews(boolean isPublic, boolean isEditable) {
		List<TreeNode> selectedNodes = treeBinder.getCheckedSelection();
		List<XView> selectedViews = new ArrayList<XView>();
		for (TreeNode node : selectedNodes) {
			XView view = getViewFrom(node, isPublic, isEditable);
			if (view != null)
				selectedViews.add(view);
		}
		return selectedViews.toArray(new XView[0]);
	}

	private final XView getViewFrom(TreeNode node, boolean isPublic, boolean isEditable) {
		XObject xObj = ((XObjectWrapper)node.getXObject()).getXObject();
		if(!doImport && xObj instanceof XCube)
			return createDefaultXView((XCube) xObj, isPublic, isEditable);
		else if (xObj instanceof XView)
			return (XView) xObj;
		return null;
	}
	private final XView createDefaultXView(XCube xCube, boolean isPublic, boolean isEditable) {
		XView xView = new XView(null, xCube.getName());
		xView.setAccountId(xCube.getAccountId());
		xView.setCubeId(xCube.getId());
		xView.setDatabaseId(xCube.getDatabaseId());
		xView.setOwnerId(((Workbench)Registry.get(Workbench.ID)).getUser().getId());

		List <String> roleIds = new ArrayList<String>();
		List <String> roleNames = new ArrayList<String>();
		if (isPublic) {
			if (roles.containsKey("VIEWER")) {
				roleIds.add(roles.get("VIEWER"));
				roleNames.add("VIEWER");
			}
		}
		if (isEditable) {
			if (roles.containsKey("EDITOR")) {
				roleIds.add(roles.get("EDITOR"));
				roleNames.add("EDITOR");
			}			
		}		
		xView.setRoleIds(roleIds);
		xView.setRoleNames(roleNames);
		return xView;
	}

	private final TreeLoader<TreeNode> createTreeLoader() {
		TreeLoader<TreeNode> treeLoader = new BaseTreeLoader<TreeNode>(
				new TreeLoaderProxy()) {
			public boolean hasChildren(TreeNode data) {
				return data != null && data.getXObject() != null
						&& data.getXObject().hasChildren();
			}
		};
		treeLoader.addLoadListener(new LoadListener(){
			public void loaderLoadException(LoadEvent le) {
				MessageBox.alert(constants.loadingChildrenFailed(),
						constants.loadingDimensionDataError() +
						(le.exception != null ? messages.errorCause(le.exception.getMessage()) : ""), null); 
			}
			
		});
		return treeLoader;
	}

	private final Tree createTree() {
		final Tree tree = new Tree();
		tree.setIndentWidth(10);
		tree.setCheckStyle(CheckCascade.CHILDREN);
		tree.setAnimate(false);
		TreeLoader<TreeNode> treeLoader = createTreeLoader();
		TreeStore<TreeNode> treeStore = new TreeStore<TreeNode>(treeLoader);
		treeBinder = new ViewTreeBinder(tree, treeStore, doImport);
		treeBinder.setDisplayProperty("name");
		treeBinder.setAutoSelect(true);
		treeBinder.setIconProvider(new ModelStringProvider<TreeNode>() {
			public String getStringValue(TreeNode model, String property) {
				XObject xObj = model.getXObject();
				return getTreeIconFor(xObj);
			}
		});
		if (isSingle) {
			tree.setSelectionMode(SelectionMode.SINGLE);
			treeBinder.addCheckListener(new CheckChangedListener(){
				private boolean currentlyProcessing = false;
				
				public void checkChanged(CheckChangedEvent event) {
					if (currentlyProcessing) {
						return;
					}
					currentlyProcessing = true;
					if (oldSelection == null) {
						oldSelection = new ArrayList<TreeNode>();
					}					
					List<TreeNode> selectedNodes = treeBinder.getCheckedSelection();
					for (TreeNode nd: oldSelection) {
						selectedNodes.remove(nd);
					}
					treeBinder.setCheckedSelection(selectedNodes);
					oldSelection = treeBinder.getCheckedSelection();
					currentlyProcessing = false;
				}			
			});
		}
		return tree;
	}

	private final String getTreeIconFor(XObject xObj) {
		xObj = ((XObjectWrapper)xObj).getXObject();
		if (xObj instanceof XDatabase) {
			return "icon-db";
		} else if (xObj instanceof XCube) {
			return "icon-cube";
		} else if (xObj instanceof XView) {
			return "icon-view";
		}
		return null;
	}
}


class ViewTreeBinder extends TreeBinder<TreeNode> {

	private final boolean doImport;
	ViewTreeBinder(Tree tree, TreeStore<TreeNode> store, boolean doImport) {
		super(tree, store);
		initComponent();
		this.doImport = doImport; 
	}

	protected TreeItem createItem(TreeNode model) {
		XObjectWrapper wrappedXObject = (XObjectWrapper) model.getXObject();
		XObject xObj = wrappedXObject.getXObject();
		boolean isLeaf = doImport ? 
				(xObj instanceof XView) : (xObj instanceof XCube);

		TreeItem item = new ViewTreeItem();
		item.setLeaf(isLeaf);
		update(item, model);
		setModel(item, model);
		return item;
	}

	private final void initComponent() {
		setDisplayProperty("name");
		setAutoSelect(true);
		setIconProvider();
	}
	private final void setIconProvider() {
		setIconProvider(new ModelStringProvider<TreeNode>() {
			public String getStringValue(TreeNode model, String property) {
				XObject xObj = model.getXObject();
				return getTreeIconFor(xObj);
			}
		});
	}
	
	private final String getTreeIconFor(XObject xObj) {
		xObj = ((XObjectWrapper)xObj).getXObject();
		if (xObj instanceof XDatabase) {
			return "icon-db";
		} else if (xObj instanceof XCube) {
			return "icon-cube";
		} else if (xObj instanceof XView) {
			return "icon-view";
		}
		return null;
	}

}

class ViewTreeItem extends TreeItem {

	
	public ViewTreeItem() {
		super();
	}

	public ViewTreeItem(String text) {
		this();
		setText(text);
	}

	protected TreeItemUI getTreeItemUI() {
		if (ui == null) {
			ui = new ViewTreeItemUI(this);
		}
		return ui;
	}

}

class ViewTreeItemUI extends TreeItemUI {

	private boolean isCheckable;

	public ViewTreeItemUI(TreeItem item) {
		super(item);
		isCheckable = item.isLeaf();
	}

	final void setIsCheckable(boolean isCheckable) {
		this.isCheckable = isCheckable;
		fly(checkEl).setVisible(isCheckable);
	}
	public void render(Element target, int index) {
		super.render(target, index);
		fly(checkEl).setVisible(isCheckable);
	}
	public void updateJointStyle() {
		super.updateJointStyle();
		fly(checkEl).setVisible(isCheckable);
	}
}