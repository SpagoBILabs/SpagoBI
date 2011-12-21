/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.server.childloader;

import java.util.ArrayList;
import java.util.List;

import org.palo.api.exceptions.PaloIOException;
import org.palo.api.parameters.ParameterReceiver;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.PaloAccount;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.internal.ExplorerTreeNode;
import org.palo.viewapi.internal.FolderElement;
import org.palo.viewapi.internal.FolderModel;
import org.palo.viewapi.internal.StaticFolder;
import org.palo.viewapi.services.FolderService;
import org.palo.viewapi.services.ServiceProvider;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.folders.XFolderElement;
import com.tensegrity.palo.gwt.core.client.models.folders.XStaticFolder;
import com.tensegrity.palo.gwt.core.server.converter.XConverter;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.client.serialization.XNode;
import com.tensegrity.wpalo.client.serialization.XObjectWrapper;
import com.tensegrity.wpalo.client.ui.mvc.viewbrowser.ViewBrowserModel;
import com.tensegrity.wpalo.server.childloader.folder.FolderTraverser;
import com.tensegrity.wpalo.server.childloader.folder.FolderVisitor;

/**
 * <code>ViewBrowserTreeLoader</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewBrowserTreeLoader.java,v 1.16 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class ViewBrowserTreeLoader implements ChildLoader {

	public boolean accepts(XObject parent) {
		String type = parent.getType();
		return type.equals(ViewBrowserModel.TYPE);
	}

	public XObject[] loadChildren(XObject parent, UserSession userSession) {
		AuthUser loggedInUser = userSession.getUser();
		if (parent instanceof XObjectWrapper) {			
			XObject xObj = ((XObjectWrapper) parent).getXObject();
			if(xObj instanceof XNode) {
				XObject [] result = loadRoot(loggedInUser);
				return result;
			}
			else if(xObj instanceof XStaticFolder) {
				XObject [] result = loadFolder((XStaticFolder)xObj, loggedInUser);
				return result;
			}
		}
		XView [] result = loadXViewsFromUserAccounts(loggedInUser);
		return result;
	}
	
	private XObject[] loadRoot(AuthUser forUser) {
//		List<View> allViews = loadAllViews(forUser);
//		removeViewsWhichAreInFolders(allViews, forUser);
//		try {
//			addRemainingViewsToRootFolder(allViews, forUser);
//		} catch (OperationFailedException e) {
//			e.printStackTrace();
//		}
		FolderService folderService = ServiceProvider.getFolderService(forUser);
		ExplorerTreeNode folderRoot = folderService.getTreeRoot();
		ExplorerTreeNode[] kids = folderRoot.getChildren();
		boolean hasKids = kids != null && kids.length > 0;
		//it seems that root is always a StaticFolder...
		if (folderRoot instanceof StaticFolder) {
			XStaticFolder xRoot = new XStaticFolder(folderRoot.getId(),
					folderRoot.getName());
			xRoot.setHasChildren(hasKids);
			return new XObject[] {wrap(xRoot)};
		}
		return new XObject[0];
	}
	private final List<View> loadAllViews(AuthUser user) {
		List<View> allViews = new ArrayList<View>();
		for (Account account : user.getAccounts()) {
			if (account instanceof PaloAccount) {
				List<View> viewsPerAccount = 
					ServiceProvider.getViewService(user).getViews(account);
				allViews.addAll(viewsPerAccount);
			}
		}
		return allViews;
	}

	private final void removeViewsWhichAreInFolders(final List<View> allViews, AuthUser user) {
		FolderService folderService = ServiceProvider.getFolderService(user);
		ExplorerTreeNode folderRoot = folderService.getTreeRoot();
		FolderVisitor visitor = new FolderVisitor() {
			public boolean visit(ExplorerTreeNode folder) {
				if(folder instanceof FolderElement) {
					ParameterReceiver srcObj = 
						((FolderElement) folder).getSourceObject();
					allViews.remove(srcObj);
				}
				return true;
			}
		};
		FolderTraverser.traverse(folderRoot, visitor);
	}
	private final void addRemainingViewsToRootFolder(List<View> allViews,
			AuthUser user) throws OperationFailedException {
//		if(allViews.isEmpty())
//			return;
//		FolderService folderService = ServiceProvider.getFolderService(user);
//		ExplorerTreeNode folderRoot = folderService.getTreeRoot();
//		for (View view : allViews) {
//			FolderElement folderElement = folderService.createFolderElement(
//					view.getName(), folderRoot, null);
//			folderElement.setSourceObject(view);
//			folderRoot.addChild(folderElement);
//			folderElement.setParent(folderRoot);
//		}
//		folderService.save(folderRoot.getRoot());
	}
	private ExplorerTreeNode find(ExplorerTreeNode node, String id) {
		if (node.getId().equals(id)) {
			return node;
		}
		for (ExplorerTreeNode nd: node.getChildren()) {
			ExplorerTreeNode result = find(nd, id);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	private XObject[] loadFolder(XStaticFolder xFolder, AuthUser user) {
		FolderService folderService = ServiceProvider.getFolderService(user);
		//ExplorerTreeNode folder = folderService.getTreeNode(xFolder.getId());
//		ExplorerTreeNode root = folderService.getTreeRoot();
//		ExplorerTreeNode folder = find(root, xFolder.getId());
		ExplorerTreeNode root = null;
		try {
			root = FolderModel.getInstance().load(user);
		} catch (PaloIOException e) {
			e.printStackTrace();
		}
		if (root == null) {
			return new XObject[0];
		}
		ExplorerTreeNode folder = find(root, xFolder.getId());
		
		ExplorerTreeNode[] children = folder.getChildren();
		XObject[] xChildren = new XObject[children.length];
//		ArrayList <XObjectWrapper> folders = new ArrayList<XObjectWrapper>();
//		ArrayList <XObjectWrapper> elements = new ArrayList<XObjectWrapper>();
		for (int i = 0; i < children.length; ++i) {
			ExplorerTreeNode child = children[i];
			if (child instanceof StaticFolder) {
				StaticFolder staticFolder = (StaticFolder) child;
				XStaticFolder xStaticFolder = new XStaticFolder(staticFolder
						.getId(), staticFolder.getName());
				xStaticFolder
						.setHasChildren(staticFolder.getChildren().length > 0);
//				folders.add(wrap(xStaticFolder));
				xChildren[i] = wrap(xStaticFolder);
			} else if (child instanceof FolderElement) {
				FolderElement folderElement = (FolderElement) child;
				XView xView = null;
				Object sourceObj = folderElement.getSourceObject();
				if (sourceObj instanceof View) {
					View view = (View) sourceObj;
					xView = (XView) XConverter.createX(view);
				}
				XFolderElement xFolderElement = new XFolderElement(folderElement.getId(), folderElement.getName(), xView);
//				elements.add(wrap(xFolderElement));
				xChildren[i] = wrap(xFolderElement);
			}
		}
//		Collections.sort(folders, new Comparator<XObjectWrapper>() {
//			public int compare(XObjectWrapper o1, XObjectWrapper o2) {
//				if (o1 == null) {
//					return o2 == null ? 0 : -1;
//				}
//				if (o2 == null) {
//					return 1;
//				}
//				return o1.getXObject().getName().compareTo(o2.getXObject().getName());
//			}
//		});
//		Collections.sort(elements, new Comparator<XObjectWrapper>() {
//			public int compare(XObjectWrapper o1, XObjectWrapper o2) {
//				if (o1 == null) {
//					return o2 == null ? 0 : -1;
//				}
//				if (o2 == null) {
//					return 1;
//				}
//				return o1.getXObject().getName().compareTo(o2.getXObject().getName());
//			}
//		});
//		System.arraycopy(folders.toArray(new XObjectWrapper[0]), 0, xChildren, 0, folders.size());
//		System.arraycopy(elements.toArray(new XObjectWrapper[0]), 0, xChildren, folders.size(), elements.size());		
		return xChildren;
	}
	
	private final XObjectWrapper wrap(XObject xObj) {
		XObjectWrapper wrapped= new XObjectWrapper(xObj);
		wrapped.setType(ViewBrowserModel.TYPE);
		wrapped.setHasChildren(xObj.hasChildren());
		return wrapped;
	}

	
	private XView[] loadXViewsFromUserAccounts(AuthUser loggedInUser) {
		List<XView> toAllViews = new ArrayList<XView>();
		AuthUser user = loggedInUser;
		for (Account account : user.getAccounts()) {
				if (account instanceof PaloAccount)
					addAllViewsFor(user, account, toAllViews);
		}
		return toAllViews.toArray(new XView[0]);
	}
	
	private final void addAllViewsFor(AuthUser user, Account account, List<XView> toAllViews) {
		List<XView> accountViews = loadViewsFor(user, account);
		toAllViews.addAll(accountViews);		
	}
	private final List<XView> loadViewsFor(AuthUser user, Account account) {
		List<XView> allViews = new ArrayList<XView>();		
		List<View> viewsPerAccount = ServiceProvider.getViewService(user)
				.getViews(account);
		for (View view : viewsPerAccount) {
			allViews.add((XView)XConverter.createX(view));
		}
		return allViews;
	}
}