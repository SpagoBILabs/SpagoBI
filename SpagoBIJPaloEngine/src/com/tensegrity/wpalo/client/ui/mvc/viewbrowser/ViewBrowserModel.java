/*
*
* @file ViewBrowserModel.java
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
* @version $Id: ViewBrowserModel.java,v 1.21 2010/04/12 11:13:36 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.viewbrowser;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.folders.XFolderElement;
import com.tensegrity.palo.gwt.core.client.models.folders.XStaticFolder;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.serialization.XNode;
import com.tensegrity.wpalo.client.serialization.XObjectWrapper;
import com.tensegrity.wpalo.client.services.folder.WPaloFolderServiceProvider;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>ViewBrowserModel</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewBrowserModel.java,v 1.21 2010/04/12 11:13:36 PhilippBouillon Exp $
 **/
public class ViewBrowserModel {
	
	public static final String TYPE = ViewBrowserModel.class.getName();
	private TreeNode invisibleRoot;
	private final XUser user;
	private final TreeStore<TreeNode> store;
	
	ViewBrowserModel(XUser user, TreeStore<TreeNode> store) {
		this.user = user;
		this.store = store;
		initialize();
	}

	final XObject getWrappedXObject(TreeNode node) {
		XObjectWrapper wrappedXObj = (XObjectWrapper)node.getXObject();
		return wrappedXObj.getXObject();
	}
	
	/**
	 * Returns the encapsulated {@link XView} of the given node or 
	 * <code>null</code> if it contains none
	 * @param node
	 * @return the contained view or <code>null</code>
	 */
	final XView getView(TreeNode node) {
		XObject wrappedXObject = getWrappedXObject(node);
		if(wrappedXObject instanceof XFolderElement) {
			XFolderElement xFolder = (XFolderElement) wrappedXObject;
			XObject srcXObj = xFolder.getSourceObject();
			if(srcXObj instanceof XView)
				return (XView) srcXObj;
		}
		return null;
	}
	
	final void reload() {
		TreeLoader<TreeNode> loader = store.getLoader();
		loader.load(invisibleRoot);
	}
	
	final void refresh(TreeNode node) {
		store.update(node);
	}
	final XView[] getViews() {
		List<XView> views = new ArrayList<XView>();
		List<TreeNode> items = store.getAllItems();
		for(TreeNode node : items) {
			XObject xObj = getWrappedXObject(node);
			if(xObj instanceof XFolderElement) {
				views.add((XView)((XFolderElement)xObj).getSourceObject());
			}
		}
		return views.toArray(new XView[0]);
	}
	final TreeNode getNodeOf(XView xView) {
		String viewId = xView.getId();
		List<TreeNode> items = store.getAllItems();
		for(TreeNode node : items) {
			XObject xObj = getWrappedXObject(node);
			if(xObj instanceof XFolderElement) {
				XView xv = (XView)((XFolderElement)xObj).getSourceObject();
				if (xv == null) {
					continue;
				}
				if(xv.getId().equals(viewId))
					return node;
			}
		}
		return null;
	}
	final TreeNode getNodeOf(XStaticFolder folder) {
		String folderId = folder.getId();
		List <TreeNode> items = store.getAllItems();
		for (TreeNode node: items) {
			XObject xObj = getWrappedXObject(node);
			if (xObj instanceof XStaticFolder && xObj.getId().equals(folderId)) {
				return node;
			}
		}
		return null;
	}
	final TreeNode getNodeOf(XFolderElement elem) {
		String elementId = elem.getId();
		List <TreeNode> items = store.getAllItems();
		for (TreeNode node: items) {
			XObject xObj = getWrappedXObject(node);
			if (xObj instanceof XFolderElement && xObj.getId().equals(elementId)) {
				return node;
			}
		}
		return null;		
	}
	final String[] getUsedFolderNames() {
		//this actually has to load tree completely!! => hence, no lazy load...
		return new String[0];
	}
	
	final void addFolder(XStaticFolder folder, TreeNode parent) {
		if(parent == null)
			parent = invisibleRoot;
		XObject wrappedFolder = wrap(folder);
		TreeNode newNode = new TreeNode(parent, wrappedFolder);
		addNode(newNode, parent);
	}
	
	public static String modify(String x) {
		x = x.replaceAll("&", "&amp;");
		x = x.replaceAll("\"", "&quot;");
		x = x.replaceAll("'", "&apos;");
		x = x.replaceAll("<", "&lt;");
		x = x.replaceAll(">", "&gt;");
		return x;
	}

	final void addView(final XView xView, final TreeNode parent, final boolean openIt, final ViewBrowser delegate, final boolean isPublic, final boolean isEditable) {
		XStaticFolder xParentFolder = (XStaticFolder) getWrappedXObject(parent);
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloFolderServiceProvider.getInstance().createFolderElement(sessionId, xView,
				xParentFolder, isPublic, isEditable, new Callback<XFolderElement>("Could not create view'"+modify(xView.getName())+"!") {
					public void onSuccess(XFolderElement xFolderElement) {
						TreeNode newNode = new TreeNode(parent, wrap(xFolderElement));
						addNode(newNode, parent);
						if(openIt) {							
							delegate.open(newNode);
						}
					}
				});
	}
	final void addViews(XFolderElement[] xFolderElements, TreeNode parent) {
		for(XFolderElement xFolderElement : xFolderElements) {
			TreeNode newNode = new TreeNode(parent, wrap(xFolderElement));
			addNode(newNode, parent);
		}
	}
	final void delete(TreeNode node) {
		store.remove(node.getParent(), node);
//		reload();
	}
	private final void addNode(TreeNode node, TreeNode parent) {
		store.add(parent, node, false);
		if(node.getParent() == null)
			node.setParent(parent);
	}
	private final void initialize() {
		XNode rootNode = new XNode(user, TYPE);
		rootNode.setId(TYPE);
		setRoot(new TreeNode(null, wrap(rootNode)));
	}
	private final void setRoot(TreeNode root) {
		this.invisibleRoot = root;
		reload();
	}
	
	private final XObjectWrapper wrap(XObject xObj) {
		XObjectWrapper wrapped= new XObjectWrapper(xObj);
		wrapped.setType(TYPE);
		wrapped.setHasChildren(xObj.hasChildren());
		return wrapped;
	}

}