/*
*
* @file ModellerNavigatorView.java
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
* @version $Id: ModellerNavigatorView.java,v 1.14 2010/02/12 13:49:50 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.modeller;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
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
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.palo.XCube;
import com.tensegrity.palo.gwt.core.client.models.palo.XDatabase;
import com.tensegrity.palo.gwt.core.client.models.palo.XDimension;
import com.tensegrity.palo.gwt.core.client.models.palo.XServer;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.serialization.XConstants;
import com.tensegrity.wpalo.client.serialization.XNode;
import com.tensegrity.wpalo.client.ui.model.TreeLoaderProxy;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>AdminNavigator</code> TODO DOCUMENT ME
 * 
 * @version $Id: ModellerNavigatorView.java,v 1.14 2010/02/12 13:49:50 PhilippBouillon Exp $
 */
public class ModellerNavigatorView extends View {

	private ContentPanel navigator;
	private TreeLoader<TreeNode> treeLoader;
	private TreeNode root;
	
	public ModellerNavigatorView(Controller controller) {
		super(controller);
	}

	@Override
	protected void handleEvent(AppEvent event) {
		switch (event.type) {
		case WPaloEvent.INIT:
			initUI();
			Workbench wb = (Workbench)Registry.get(Workbench.ID);
			XUser xuser = wb.getUser();
			XNode root = new XNode(xuser, XConstants.TYPE_ROOT_NODE);
			root.setName("DummyRoot");
			root.setId("0");
			root.setHasChildren(true);
			Dispatcher.forwardEvent(WPaloEvent.EXPANDED_SERVER_SECTION,
					new TreeNode(null, root));
			break;
		case WPaloEvent.EXPANDED_SERVER_SECTION: //load tree data
			TreeNode node = (TreeNode) event.data;
			if (node != null) {
				treeLoader.load(node);
			}
			break;
		}
	}

	private final void initUI() {		
		navigator = new ContentPanel();
		navigator.setHeading("Modeller");
		navigator.setScrollMode(Scroll.AUTO);
		// connect with dispatcher:
		navigator.addListener(Events.Expand, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				Dispatcher.get().dispatch(WPaloEvent.EXPANDED_SERVER_SECTION);
			}
		});

		ToolBar toolbar = new ToolBar();
		TextToolItem addDatabase = new TextToolItem("", "icon-db");
		toolbar.add(addDatabase);
		TextToolItem addCube = new TextToolItem("", "icon-cube");
		toolbar.add(addCube);
		TextToolItem addDimension = new TextToolItem("", "icon-dim");
		toolbar.add(addDimension);
		toolbar.add(new SeparatorToolItem());
		TextToolItem del = new TextToolItem("", "icon-delete");
		toolbar.add(del);
		navigator.setTopComponent(toolbar);
		
		//create the tree which displays the data:
		treeLoader = new BaseTreeLoader<TreeNode>(new TreeLoaderProxy()){
			public boolean hasChildren(TreeNode data) {
				return data != null && data.getXObject() != null &&
				       data.getXObject().hasChildren();
			}
		};
		Tree usersTree = createTree(treeLoader);
		navigator.add(usersTree);
		usersTree.setStyleName("tree-style");
		
		Workbench wb = (Workbench)Registry.get(Workbench.ID);
		wb.addToViewPanel(navigator);
	}

	private final Tree createTree(TreeLoader<TreeNode> loader) {
		Tree tree = new Tree();
		tree.setIndentWidth(6);
		TreeStore<TreeNode> store = new TreeStore<TreeNode>(loader);
		TreeBinder<TreeNode> binder = new TreeNodeBinder(tree, store);
		binder.setDisplayProperty("name");
		//binder.setAutoSelect(true);
		binder.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				if (!navigator.isExpanded()) {
					return;
				}
				TreeNode node = (TreeNode) se.getSelectedItem(); // single selection
				String type = node.getXObject().getType();
				int eventType = -1;
				
				if(type.equals(XDimension.TYPE))
					eventType = WPaloEvent.EDIT_DIMENSION_ITEM;
				else if(type.equals(XServer.TYPE))
					eventType = WPaloEvent.EDIT_SERVER_ITEM;
				
				if(eventType > -1)
					fireEvent(new AppEvent<TreeNode>(eventType, node));
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
		String type = model.getXObject().getType();
		if(type.equals(XCube.TYPE))
			item.setIconStyle("icon-cube");
		else if(type.equals(XDimension.TYPE))
			item.setIconStyle("icon-dim");
		else if(type.equals(XDatabase.TYPE))
			item.setIconStyle("icon-db");
		else if(type.equals(XServer.TYPE))
			item.setIconStyle("icon-server");
		return item;
	}
}
