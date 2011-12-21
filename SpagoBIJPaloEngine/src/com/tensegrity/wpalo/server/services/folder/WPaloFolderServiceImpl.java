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
package com.tensegrity.wpalo.server.services.folder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.palo.api.exceptions.PaloIOException;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.Group;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.internal.ExplorerTreeNode;
import org.palo.viewapi.internal.FolderElement;
import org.palo.viewapi.internal.FolderModel;
import org.palo.viewapi.internal.IRoleManagement;
import org.palo.viewapi.internal.RoleImpl;
import org.palo.viewapi.internal.StaticFolder;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;
import org.palo.viewapi.internal.io.CubeViewIO;
import org.palo.viewapi.services.FolderService;
import org.palo.viewapi.services.ServiceProvider;
import org.palo.viewapi.services.ViewService;

import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.folders.XFolderElement;
import com.tensegrity.palo.gwt.core.client.models.folders.XStaticFolder;
import com.tensegrity.palo.gwt.core.server.services.BasePaloServiceServlet;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.client.exceptions.DbOperationFailedException;
import com.tensegrity.wpalo.client.services.folder.WPaloFolderService;
import com.tensegrity.wpalo.server.services.cubeview.WPaloCubeViewConverter;

/**
 * <code>FolderServiceImpl</code> TODO DOCUMENT ME
 * 
 * @version $Id: WPaloFolderServiceImpl.java,v 1.9 2009/06/24 07:05:44
 *          ArndHouben Exp $
 **/
public class WPaloFolderServiceImpl extends BasePaloServiceServlet implements
		WPaloFolderService {

	/** generated */
	private static final long serialVersionUID = -3585310214177526866L;

	public XStaticFolder loadFolderRoot(String sessionId) throws SessionExpiredException {
		ExplorerTreeNode folderRoot = null;
		try {
			folderRoot = FolderModel.getInstance().load(getLoggedInUser(sessionId));
		} catch (PaloIOException e) {			
		}
		if (folderRoot == null) {
			return null;
		}
		ExplorerTreeNode[] kids = folderRoot.getChildren();
		boolean hasKids = kids != null && kids.length > 0;
		// it seems that root is always a StaticFolder...
		if (folderRoot instanceof StaticFolder) {
			XStaticFolder xRoot = new XStaticFolder(folderRoot.getId(),
					folderRoot.getName());
			xRoot.setHasChildren(hasKids);
			return xRoot;
		}
		return null;
	}

	public XStaticFolder createFolder(String sessionId, String name, XStaticFolder xParent)
			throws DbOperationFailedException, SessionExpiredException {
		try {
			ExplorerTreeNode root = FolderModel.getInstance().load(getLoggedInUser(sessionId));
			FolderService folderService = ServiceProvider
					.getFolderService(getLoggedInUser(sessionId));
			ExplorerTreeNode parentNode = find(root, xParent.getId());
//			ExplorerTreeNode parentNode = folderService.getTreeNode(xParent
//					.getId());
			StaticFolder staticFolder = folderService.createStaticFolder(name,
					parentNode, null);
//			folderService.save(staticFolder.getRoot());
			XStaticFolder xStaticFolder = new XStaticFolder(staticFolder
					.getId(), staticFolder.getName());
			xStaticFolder.setHasChildren(false);
			saveRoot(sessionId, root);
//			print(root, 0);
			return xStaticFolder;
		} catch (OperationFailedException e) {
			UserSession userSession = getUserSession(sessionId);
			throw new DbOperationFailedException(
					userSession.translate("couldNotCreateFolder", name, e.getLocalizedMessage()), e);
		} catch (PaloIOException e) {
			UserSession userSession = getUserSession(sessionId);
			throw new DbOperationFailedException(
					userSession.translate("couldNotCreateFolder", name, e.getLocalizedMessage()), e);
		}
	}

	public void deleteFolder(String sessionId, XStaticFolder xFolder)
			throws DbOperationFailedException, SessionExpiredException {
		try {
			ExplorerTreeNode root = FolderModel.getInstance().load(getLoggedInUser(sessionId));
			FolderService folderService = ServiceProvider
					.getFolderService(getLoggedInUser(sessionId));
			ExplorerTreeNode folder = find(root, xFolder.getId());
//			FolderService folderService = ServiceProvider
//					.getFolderService(getLoggedInUser());
//			ExplorerTreeNode folder = folderService
//					.getTreeNode(xFolder.getId());
			if (folder != null) {
				deleteContentof(sessionId, folder);
				folderService.delete(folder);
				//folderService.save(folder.getRoot());
				saveRoot(sessionId, root);
			}
		} catch (OperationFailedException e) {
			UserSession userSession = getUserSession(sessionId);
			throw new DbOperationFailedException(
					userSession.translate("couldNotDelete", xFolder.getName(), e.getLocalizedMessage()), e);
		} catch (PaloIOException e) {
			UserSession userSession = getUserSession(sessionId);
			throw new DbOperationFailedException(
					userSession.translate("couldNotDelete", xFolder.getName(), e.getLocalizedMessage()), e);
		}
	}

	private final void deleteFolder(String sessionId, FolderService folderService, StaticFolder folder) throws OperationFailedException, SessionExpiredException {
		if (folder != null) {
			deleteContentof(sessionId, folder);
			folderService.delete(folder);
		}
	}
	
	private final void deleteContentof(String sessionId, ExplorerTreeNode folder)
			throws OperationFailedException, SessionExpiredException {
		FolderService folderService = ServiceProvider
				.getFolderService(getLoggedInUser(sessionId));
		for (ExplorerTreeNode child : folder.getChildren()) {
			if (child instanceof StaticFolder) {
				deleteFolder(sessionId, folderService, (StaticFolder) child);
			}
			View view = getViewFrom(child);
			if (view != null)
				delete(sessionId, view);
			folderService.delete(child);
		}
	}

	private final View getViewFrom(ExplorerTreeNode node) {
		if (node != null && node instanceof FolderElement) {
			Object srcObj = ((FolderElement) node).getSourceObject();
			if (srcObj != null && srcObj instanceof View)
				return (View) srcObj;
		}
		return null;
	}

	private final void delete(String sessionId, View view) throws OperationFailedException,
			SessionExpiredException {
		ViewService viewService = ServiceProvider
				.getViewService(getLoggedInUser(sessionId));
		viewService.delete(view);
	}

	public void deleteFolderElement(String sessionId, XFolderElement xFolderElement)
			throws DbOperationFailedException, SessionExpiredException {
		try {
			ExplorerTreeNode root = FolderModel.getInstance().load(getLoggedInUser(sessionId));
			FolderService folderService = ServiceProvider
					.getFolderService(getLoggedInUser(sessionId));
			ExplorerTreeNode folder = find(root, xFolderElement.getId());
			
//			FolderService folderService = ServiceProvider
//					.getFolderService(getLoggedInUser());
//			ExplorerTreeNode folder = folderService.getTreeNode(xFolderElement
//					.getId());
			if (folder != null) {
				folderService.delete(folder);
//				folderService.save(folder.getRoot());
				saveRoot(sessionId, root);
			}
		} catch (OperationFailedException e) {
			e.printStackTrace();
			UserSession userSession = getUserSession(sessionId);
			throw new DbOperationFailedException(
					userSession.translate("couldNotDelete", xFolderElement.getName(), e.getLocalizedMessage()), e);
		} catch (PaloIOException e) {
			e.printStackTrace();
			UserSession userSession = getUserSession(sessionId);
			throw new DbOperationFailedException(
					userSession.translate("couldNotDelete", xFolderElement.getName(), e.getLocalizedMessage()), e);
		}
	}

	public XFolderElement createFolderElement(String sessionId, XView xView,
			XStaticFolder xParentFolder, boolean isPublic, boolean isEditable) throws DbOperationFailedException,
			SessionExpiredException {
		//ExplorerTreeNode parentFolder = getFolder(xParentFolder);
		FolderElement folderElement = createFolderElement(sessionId, xView, xParentFolder, true, isPublic, isEditable);
		return createXFolderElement(folderElement, xView);
	}

	public XFolderElement[] importViewsAsFolderElements(String sessionId, XView[] views,
			XStaticFolder xParentFolder, boolean isPublic, boolean isEditable) throws DbOperationFailedException,
			SessionExpiredException {
		WPaloCubeViewConverter converter = new WPaloCubeViewConverter(
				getLoggedInUser(sessionId));
		List<XFolderElement> xFolderElements = new ArrayList<XFolderElement>();
//		ExplorerTreeNode parentFolder = getFolder(xParentFolder);
		for (XView xView : views) {
			XView importedXView = importViewWith(sessionId, converter, xView);
			if (xParentFolder != null) {
				FolderElement folderElement = createFolderElement(sessionId, importedXView,
					xParentFolder, true, isPublic, isEditable);
				xFolderElements.add(createXFolderElement(folderElement,
					importedXView));
				List <String> roleIds = new ArrayList<String>();
				List <String> roleNames = new ArrayList<String>();
				IRoleManagement rm = MapperRegistry.getInstance().getRoleManagement();
				if (isPublic) {
					try {
						Role r = (Role) rm.findByName("VIEWER");
						roleIds.add(r.getId());
						roleNames.add(r.getName());
					} catch (Throwable t) {
					}
				}
				if (isEditable) {
					try {
						Role r = (Role) rm.findByName("EDITOR");
						roleIds.add(r.getId());
						roleNames.add(r.getName());
					} catch (Throwable t) {
					}
				}
				importedXView.setRoleIds(roleIds);
				importedXView.setRoleNames(roleNames);				
			}
		}
		return xFolderElements.toArray(new XFolderElement[0]);
	}

	public XView importView(String sessionId, XView view) throws DbOperationFailedException,
			SessionExpiredException {
		WPaloCubeViewConverter converter = new WPaloCubeViewConverter(
				getLoggedInUser(sessionId));
		XView importedXView = importViewWith(sessionId, converter, view);
		View realView = getView(sessionId, importedXView);
		try {
			assignViewerAndEditorRole(sessionId, null, realView, true, true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		List <String> roleIds = new ArrayList<String>();
		List <String> roleNames = new ArrayList<String>();
		IRoleManagement rm = MapperRegistry.getInstance().getRoleManagement();
		try {
			Role r = (Role) rm.findByName("VIEWER");
			roleIds.add(r.getId());
			roleNames.add(r.getName());
		} catch (Throwable t) {
		}
		try {
			Role r = (Role) rm.findByName("EDITOR");
			roleIds.add(r.getId());
			roleNames.add(r.getName());
		} catch (Throwable t) {
		}
		importedXView.setRoleIds(roleIds);
		importedXView.setRoleNames(roleNames);				
		//saves xml definition in spagobi
		CubeView  cubeView = realView.getCubeView();
		String xml = CubeViewIO.toXML(cubeView);

		return importedXView;
	}

	private final XView importViewWith(String sessionId, WPaloCubeViewConverter converter,
			XView xView) throws DbOperationFailedException {
		try {
			View newView = converter.convertLegacyView(xView, sessionId);
			if (newView == null)
				newView = converter.createDefaultView(xView, sessionId);
			return converter.createX(newView);
		} catch (Throwable t) {
			t.printStackTrace();
			UserSession userSession = null;
			try {
				userSession = getUserSession(sessionId);
			} catch (SessionExpiredException e) {
			}
			if (userSession != null) {
				throw new DbOperationFailedException(
					userSession.translate("couldNotImportView", xView.getName(), t.getLocalizedMessage()), t);
			} else {
				throw new DbOperationFailedException("Could not import view!");
			}
		}
	}
	
	private final void assignViewerAndEditorRole(String sessionId, FolderElement fe, View view,
			boolean isPublic, boolean isEditable) throws SQLException {
		IRoleManagement roleMgmt = MapperRegistry.getInstance().getRoleManagement();
		Role viewerRole = (Role) roleMgmt.findByName("VIEWER");
		if (viewerRole == null) {
			viewerRole = new RoleImpl.Builder(null).name("VIEWER").
			permission(Right.READ).build();
			roleMgmt.insert(viewerRole);
		}
		Role editorRole = (Role) roleMgmt.findByName("EDITOR");
		if (editorRole == null) {
			editorRole = new RoleImpl.Builder(null).name("EDITOR").
				permission(Right.CREATE).build();
			roleMgmt.insert(editorRole);
		}
		
		if (fe != null) {
		try {
			FolderService folderService = ServiceProvider
				.getFolderService(getLoggedInUser(sessionId));
			if (!fe.hasRole(viewerRole) && isPublic) {
				try {
					folderService.add(viewerRole, fe);
				} catch (OperationFailedException e) {
					e.printStackTrace();
				}
			}
			if (!fe.hasRole(editorRole) && isEditable) {
				try {
					folderService.add(editorRole, fe);
				} catch (OperationFailedException e) {
					e.printStackTrace();
				}
			}
			ExplorerTreeNode nd = fe.getParent();
			while (nd != null) {
				if (!nd.hasRole(viewerRole) && isPublic) {
					try {
						folderService.add(viewerRole, nd);
					} catch (OperationFailedException e) {
						e.printStackTrace();
					}
				}
				if (!nd.hasRole(editorRole) && isEditable) {
					try {
						folderService.add(editorRole, nd);
					} catch (OperationFailedException e) {
						e.printStackTrace();
					}					
				}
				nd = nd.getParent();
			}
		} catch (SessionExpiredException e) {
			e.printStackTrace();
		}
		}
		
		if (!view.hasRole(viewerRole) && isPublic) {
			try {
				ViewService viewService = ServiceProvider.getViewService(getLoggedInUser(sessionId));
				try {
					viewService.add(viewerRole, view);
				} catch (OperationFailedException e) {
					e.printStackTrace();
				}
			} catch (SessionExpiredException e) {
				e.printStackTrace();
			}
		} else if (view.hasRole(viewerRole) && !isPublic) {
			try {
				ViewService viewService = ServiceProvider.getViewService(getLoggedInUser(sessionId));
				try {
					viewService.remove(viewerRole, view);
				} catch (OperationFailedException e) {
					e.printStackTrace();
				}
			} catch (SessionExpiredException e) {
				e.printStackTrace();
			}			
		}
		if (!view.hasRole(editorRole) && isEditable) {
			try {
				ViewService viewService = ServiceProvider.getViewService(getLoggedInUser(sessionId));
				try {
					viewService.add(editorRole, view);
				} catch (OperationFailedException e) {
					e.printStackTrace();
				}
			} catch (SessionExpiredException e) {
				e.printStackTrace();
			}			
		} else if (view.hasRole(editorRole) && !isEditable) {
			try {
				ViewService viewService = ServiceProvider.getViewService(getLoggedInUser(sessionId));
				try {
					viewService.remove(editorRole, view);
				} catch (OperationFailedException e) {
					e.printStackTrace();
				}
			} catch (SessionExpiredException e) {
				e.printStackTrace();
			}						
		} 
	}

	private final void print(ExplorerTreeNode node, int indent) {
		for (int i = 0; i < indent; i++) {
			System.out.print("  ");
		}
		System.out.println(node.getName() + " (" + node.getId() + ")");
		for (ExplorerTreeNode kid: node.getChildren()) {
			print(kid, indent + 1);
		}
	}
	
	private final FolderElement createFolderElement(String sessionId, XView xView,
			XStaticFolder parentFolder, boolean p,
			boolean isPublic, boolean isEditable) throws DbOperationFailedException,
			SessionExpiredException {
		FolderElement fe = null;
		try {
//			CubeViewReader.CHECK_RIGHTS = false;
			ExplorerTreeNode root = FolderModel.getInstance().load(getLoggedInUser(sessionId));
			FolderService folderService = ServiceProvider
					.getFolderService(getLoggedInUser(sessionId));	
			ExplorerTreeNode parent = find(root, parentFolder.getId());
			fe = folderService.createFolderElement(xView.getName(),
					parent, null);
			View view = getView(sessionId, xView);
			try {
				assignViewerAndEditorRole(sessionId, fe, view, isPublic, isEditable);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			fe.setSourceObject(view);
//			ExplorerTreeNode root = fe.getRoot();
//			if (root != null)
//				folderService.save(root);
			saveRoot(sessionId, root);
//			print(root, 0);
		} catch (PaloIOException e) {
			e.printStackTrace();
		} catch (OperationFailedException e) {
			e.printStackTrace();
		} 
//		finally {
//			CubeViewReader.CHECK_RIGHTS = true;
//		}
		return fe;
	}

	private final View getView(String sessionId, XView xView) throws SessionExpiredException {
		ViewService viewService = ServiceProvider
				.getViewService(getLoggedInUser(sessionId));
		return viewService.getView(xView.getId());
	}

	private final XFolderElement createXFolderElement( 
			FolderElement folderElement, XObject xSourceObject) {
		XFolderElement xFolderElement = new XFolderElement(folderElement
				.getId(), folderElement.getName(), xSourceObject);
		xFolderElement.setHasChildren(xSourceObject.hasChildren());
		return xFolderElement;
	}

	private final ExplorerTreeNode find(ExplorerTreeNode root, String id) {
		if (root.getId().equals(id)) {
			return root;
		}
		for (ExplorerTreeNode kid: root.getChildren()) {
			ExplorerTreeNode result = find(kid, id);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	private final void ensureRoles(FolderService folderService, ExplorerTreeNode etn, Role role) {
		if (etn == null) {
			return;
		}
		if (!etn.hasRole(role)) {
			try {
				folderService.add(role, etn);
			} catch (OperationFailedException e) {
				e.printStackTrace();
			}
		}
		for (ExplorerTreeNode node: etn.getChildren()) {
			ensureRoles(folderService, node, role);
		}
	}
		
	private final void saveRoot(String sessionId, ExplorerTreeNode root) throws SessionExpiredException {
		try {
			AuthUser user = getLoggedInUser(sessionId);
			FolderService folderService = ServiceProvider.getFolderService(user);		
			Role viewerRole = null;
			for (Role role: user.getRoles()) {
				if (role.getName().equalsIgnoreCase("viewer")) {
					viewerRole = role;
					break;
				}
			}
			if (viewerRole == null) {
				for (Group g: user.getGroups()) {
					for (Role role: g.getRoles()) {
						if (role.getName().equalsIgnoreCase("viewer")) {
							viewerRole = role;
							break;
						}
					}
				}
			}
			if (viewerRole != null) {
				ensureRoles(folderService, root, viewerRole);
			}
			FolderModel.getInstance().save(user, root);
			folderService.save(root);
		} catch (OperationFailedException e) {
			e.printStackTrace();
		} catch (PaloIOException e) {
			e.printStackTrace();
		}
	}
	
	public final void move(String sessionId, XObject[] xObjects,
			XStaticFolder toXFolder) throws DbOperationFailedException,
			SessionExpiredException {
		try {
			ExplorerTreeNode root = FolderModel.getInstance().load(getLoggedInUser(sessionId));
			FolderService folderService = ServiceProvider
					.getFolderService(getLoggedInUser(sessionId));
			ExplorerTreeNode folder = find(root, toXFolder.getId());
			for (XObject xObject: xObjects) {
				ExplorerTreeNode folderElement = find(root, xObject.getId());
				folderElement.setParent(folder);
			}
			
			saveRoot(sessionId, root);
		} catch (PaloIOException ope) {
			ope.printStackTrace();
			UserSession userSession = getUserSession(sessionId);
			throw new DbOperationFailedException(userSession.translate("couldNotMoveFolderElements", ope.getLocalizedMessage()), ope);
		} 
	}

//	private final ExplorerTreeNode getTreeNode(String sessionId, XFolderElement xf) throws 
//		OperationFailedException, SessionExpiredException {
//		FolderService folderService = ServiceProvider.getFolderService(getLoggedInUser(sessionId));
//		return folderService.getTreeNode(xf.getId());		
//	}
//	
//	private final ExplorerTreeNode removeFromParent(String sessionId, 
//			XFolderElement xFolderElement) throws OperationFailedException,
//			SessionExpiredException {
//		FolderService folderService = ServiceProvider
//				.getFolderService(getLoggedInUser(sessionId));
//		ExplorerTreeNode folderElement = folderService
//				.getTreeNode(xFolderElement.getId());
//		ExplorerTreeNode parent = folderElement.getParent();
//		parent.removeChild(folderElement);
//		folderElement.setParent(null);
//		folderService.save(parent.getRoot());
//		return folderElement;
//	}

	public void renameFolderElement(String sessionId, XFolderElement xFolderElement,
			String newName) throws DbOperationFailedException,
			SessionExpiredException {
		FolderService folderService = ServiceProvider
				.getFolderService(getLoggedInUser(sessionId));			
//			ExplorerTreeNode folder = folderService.getTreeNode(xFolderElement
//					.getId());
		ExplorerTreeNode root = null;
		try {
			root = FolderModel.getInstance().load(getLoggedInUser(sessionId));
		} catch (PaloIOException e) {
			// TODO Auto-generated catch block
		}
		if (root == null) {
			return;
		}
		ExplorerTreeNode folder = find(root, xFolderElement.getId());
		if (folder != null) {
			folderService.setName(newName, folder);
			//folderService.save(folder.getRoot());
			saveRoot(sessionId, root);
		}
	}
		
	public void renameFolder(String sessionId, XStaticFolder folder, String newName) throws DbOperationFailedException,
			SessionExpiredException {
		FolderService folderService = ServiceProvider
			.getFolderService(getLoggedInUser(sessionId));
		AuthUser user = getLoggedInUser(sessionId);
		boolean mayWrite = false;
		for (Role r: user.getRoles()) {
			if (r.hasPermission(Right.WRITE)) {
				mayWrite = true;
				break;
			}
		}
		if (!mayWrite) {
			for (Group g: user.getGroups()) {
				for (Role r: g.getRoles()) {
					if (r.hasPermission(Right.WRITE)) {
						mayWrite = true;
						break;
					}					
				}
			}
		}
		if (!mayWrite) {
			throw new DbOperationFailedException("Not enough rights to rename folder '" +
					folder.getName() + "'.");
		}
		//		ExplorerTreeNode folder = folderService.getTreeNode(xFolderElement
		//				.getId());
		ExplorerTreeNode root = null;
		try {
			root = FolderModel.getInstance().load(getLoggedInUser(sessionId));
		} catch (PaloIOException e) {
			// 	TODO Auto-generated catch block
		}
		if (root == null) {
			return;
		}
		ExplorerTreeNode fold = find(root, folder.getId());
		if (fold != null) {
			folderService.setName(newName, fold);
			saveRoot(sessionId, root);
		}
	}

	public boolean hasWritePermission(String sessionId) throws DbOperationFailedException,
			SessionExpiredException {
		AuthUser user = getLoggedInUser(sessionId);
		for (Role r: user.getRoles()) {
			if (r.hasPermission(Right.WRITE)) {
				return true;
			}
		}
		for (Group g: user.getGroups()) {
			for (Role r: g.getRoles()) {
				if (r.hasPermission(Right.WRITE)) {
					return true;
				}
			}			
		}
		return false;
	}
	
	public boolean hasCreatePermission(String sessionId) throws DbOperationFailedException,
			SessionExpiredException {
		AuthUser user = getLoggedInUser(sessionId);
		for (Role r: user.getRoles()) {
			if (r.hasPermission(Right.CREATE)) {
				return true;
			}
		}
		for (Group g: user.getGroups()) {
			for (Role r: g.getRoles()) {
				if (r.hasPermission(Right.CREATE)) {
					return true;
				}
			}			
		}
		return false;
	}	
}
