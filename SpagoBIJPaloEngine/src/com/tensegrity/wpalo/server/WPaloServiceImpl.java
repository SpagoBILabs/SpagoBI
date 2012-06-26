/*
*
* @file WPaloServiceImpl.java
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
* @version $Id: WPaloServiceImpl.java,v 1.56 2010/04/12 11:13:36 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.server;

import it.eng.spagobi.util.spagobi.JPaloSavingUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.palo.api.Connection;
import org.palo.api.ConnectionConfiguration;
import org.palo.api.ConnectionFactory;
import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.parameters.ParameterReceiver;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.DbConnection;
import org.palo.viewapi.Group;
import org.palo.viewapi.PaloAccount;
import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.AuthenticationFailedException;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.internal.AuthUserImpl;
import org.palo.viewapi.internal.ExplorerTreeNode;
import org.palo.viewapi.internal.FolderElement;
import org.palo.viewapi.internal.IUserRoleManagement;
import org.palo.viewapi.internal.IViewManagement;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;
import org.palo.viewapi.services.AdministrationService;
import org.palo.viewapi.services.ServiceProvider;
import org.palo.viewapi.services.ViewService;

import sun.misc.BASE64Decoder;

import com.tensegrity.palo.gwt.core.client.exceptions.PaloGwtCoreException;
import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XDirectLinkData;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.palo.XCube;
import com.tensegrity.palo.gwt.core.client.models.palo.XDatabase;
import com.tensegrity.palo.gwt.core.client.models.palo.XDimension;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.palo.gwt.core.client.models.palo.XHierarchy;
import com.tensegrity.palo.gwt.core.client.models.reports.XDynamicReportFolder;
import com.tensegrity.palo.gwt.core.client.models.reports.XReport;
import com.tensegrity.palo.gwt.core.client.models.reports.XReportFolder;
import com.tensegrity.palo.gwt.core.client.models.reports.XStaticReportFolder;
import com.tensegrity.palo.gwt.core.client.models.reports.XVariableDescriptor;
import com.tensegrity.palo.gwt.core.server.converter.XConverter;
import com.tensegrity.palo.gwt.core.server.converter.admin.AccountConverter;
import com.tensegrity.palo.gwt.core.server.converter.cubeviews.ViewConverter;
import com.tensegrity.palo.gwt.core.server.services.BasePaloServiceServlet;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.palo.gwt.core.server.services.cubeview.CubeViewController;
import com.tensegrity.palo.gwt.core.server.services.cubeview.CubeViewService;
import com.tensegrity.palo.gwt.core.server.services.cubeview.XElementFactory;
import com.tensegrity.palo.gwt.core.server.services.cubeview.converter.CubeViewConverter;
import com.tensegrity.wpalo.client.WPaloService;
import com.tensegrity.wpalo.client.serialization.templates.XApplication;
import com.tensegrity.wpalo.client.serialization.templates.XTemplate;
import com.tensegrity.wpalo.client.serialization.templates.XWorkbook;
import com.tensegrity.wpalo.client.serialization.templates.XWorksheet;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.server.childloader.ChildLoaderManager;
import com.tensegrity.wpalo.server.dbconnection.HSqlDbConnection;
import com.tensegrity.wpalo.server.dbconnection.MySqlDbConnection;
import com.tensegrity.wpalo.server.services.wpalo.WPaloControllerServiceImpl;

public class WPaloServiceImpl extends BasePaloServiceServlet implements WPaloService {
	private static final long serialVersionUID = 389580199199443146L;
	private final WPaloPropertyServiceImpl properties;
	private static transient Logger logger = Logger.getLogger(WPaloServiceImpl.class);
	
	public WPaloServiceImpl() {
		properties = new WPaloPropertyServiceImpl();
	}

	public List<TreeNode> loadChildren(String sessionId, TreeNode node)
			throws SessionExpiredException, PaloGwtCoreException {
		XObject obj = node.getXObject();

		XObject[] result = ChildLoaderManager.getInstance().loadChildren(obj,
				getUserSession(sessionId));

		ArrayList<TreeNode> nodeList = new ArrayList<TreeNode>();
		try {
			for (XObject r : result) {
				nodeList.add(new TreeNode(node, r, true));
			}
		} catch (Throwable t) {
			logger.error(t.getMessage());
			t.printStackTrace();
		}
		return nodeList;

	}

	public List <TreeNode> loadChildren(String sessionId, 
			String parentType, String viewId, String axisId, String parentId, String parentPath) throws SessionExpiredException,
			PaloGwtCoreException {
		XObject [] result = ChildLoaderManager.getInstance().loadChildren(
				parentType, viewId, axisId, parentId,
				getUserSession(sessionId));
		ArrayList<TreeNode> nodeList = new ArrayList<TreeNode>();
		try {
			HashMap <String, Integer> reps = new HashMap<String, Integer>();
			int rep;
			for (XObject r: result) {
				String id = ((XElementNode) r).getElement().getId();
				if (reps.containsKey(id)) {
					rep = reps.get(id);
				} else {
					rep = 0;
				}
				nodeList.add(new TreeNode(parentPath, r, rep));
				reps.put(id, rep + 1);
			}
		} catch (Throwable t) {
			logger.error(t.getMessage());
			t.printStackTrace();
		}
		// TODO Remember to set parent of each returned TreeNode!
		return nodeList;		
	}
	
	public List<TreeNode> loadHierarchyTree(String sessionId,
			String hierarchyId, String viewId, String axisId, int level)
			throws SessionExpiredException, PaloGwtCoreException {
		List <TreeNode> result = loadTreeNodesFrom(getUserSession(sessionId), hierarchyId, viewId, axisId, level);
		return result;
	}

	private final List <XElementNode> loadElementsFrom(UserSession userSession,
			XAxisHierarchy xHierarchy, int level) {
		List<XElementNode> nodes = new ArrayList<XElementNode>();
		CubeViewController viewController = getViewController(userSession,
				xHierarchy.getViewId());
		if (viewController != null) {
			nodes.addAll(viewController.loadElements(xHierarchy, level));
		}
		return nodes;
	}
	
	private final String fastCreatePath(TreeNode tNode, XElement xKidElement, HashMap <XElement, Integer> repCounter) {
		StringBuffer buffer = new StringBuffer();				
		buffer.append(xKidElement.getId());
		if (tNode.getParent() != null) {
			Integer rep = repCounter.get(xKidElement);
			if (rep == null) {
				rep = 0;
			}
			if (rep != 0) {
				buffer.append("(");
				buffer.append(rep);
				buffer.append(")");
			}
			buffer.append(":");
			return tNode.getParent().getPath() + buffer.toString();
		} else {
			buffer.append(":");
		}
		return buffer.toString();						
	}
	
	private final void addChildren(int currentDepth, int level, ElementNode [] rootNodes, List <TreeNode> nodes, String hierarchyId, String viewId, HashMap <ElementNode, TreeNode> parents) {
		if (currentDepth >= level && level != -1) {
			return;
		}
		ArrayList <ElementNode> nextLevel = new ArrayList<ElementNode>();
		for (ElementNode root: rootNodes) {			
			TreeNode parent = parents.get(root);
			XElementNode parentX = parent == null ? null : (XElementNode) parent.getXObject();
			HashMap <XElement, Integer> repCounter = 
				new HashMap<XElement, Integer>();
			for (ElementNode kid: root.getChildren()) {
				if (kid.getChildCount() != 0) {
					nextLevel.add(kid);
				}
				XElementNode xKid = XElementFactory.createX(kid, hierarchyId, viewId);
				xKid.setParent(parentX);
				xKid.setChildCount(kid.getChildCount());				
				TreeNode n = new TreeNode(parent, xKid, false);
				XElement xKidElement = xKid.getElement();
				n.setPath(fastCreatePath(n, xKidElement, repCounter));				
				if (!repCounter.containsKey(xKidElement)) {
					repCounter.put(xKidElement, 1);
				} else {
					repCounter.put(xKidElement, repCounter.get(xKidElement) + 1);
				}
				if (parent != null) {
					parent.addChild(n);
					((XElementNode) parent.getXObject()).forceAddChild(xKid);
				}				
				if (kid.getChildCount() != 0) {
					parents.put(kid, n);
				}				
				nodes.add(n);
			}
		}
		if (nextLevel.size() != 0 && ((currentDepth + 1) < level || level == -1)) {
			addChildren(currentDepth + 1, level, nextLevel.toArray(new ElementNode[0]), nodes, hierarchyId, viewId, parents);
		}
	}
	
	private final List <TreeNode> loadTreeNodes(CubeViewController cvc, String hierarchyId, String viewId, String axisId, int level) {
		List<TreeNode> nodes = new ArrayList<TreeNode>();
		
		Axis axis = cvc.getCubeView().getAxis(axisId);
		AxisHierarchy axisHierarchy = axis.getAxisHierarchy(hierarchyId);
		if(axisHierarchy != null) {
			ElementNode [] rootNodes = axisHierarchy.getRootNodes();
			HashMap<ElementNode, TreeNode> parents = new HashMap<ElementNode, TreeNode>();
			for (ElementNode rootElement: rootNodes) {
				XElementNode xRoot = XElementFactory.createX(rootElement, hierarchyId, viewId);				
				xRoot.setChildCount(rootElement.getChildCount());
				TreeNode n = new TreeNode(null, xRoot, true);
				if (xRoot.getChildCount() != 0) {
					parents.put(rootElement, n);
				}				
				nodes.add(n);
				cvc.elementNodes.put(xRoot.getId(), rootElement);
			}
			int currentDepth = 1;
			try {
				addChildren(currentDepth, level, rootNodes, nodes, hierarchyId, viewId, parents);
			} catch (Throwable t) {
				logger.error(t.getMessage());
				t.printStackTrace();
			}
		}
		return nodes;		
	}
	
	private final List <TreeNode> loadTreeNodesFrom(UserSession userSession,
			String hierarchyId, String viewId, String axisId, int level) {		
		CubeViewController viewController = getViewController(userSession,
				viewId);
		if (viewController != null) {
			return loadTreeNodes(viewController, hierarchyId, viewId, axisId, level);
		}
		return new ArrayList<TreeNode>();
	}
	
	private final CubeViewController getViewController(UserSession userSession,
			String view) {
		return CubeViewController.getController(userSession.getSessionId(), view);
	}	
	
	/** loads all accounts assigned to the currently logged in user 
	 * @throws SessionExpiredException */ 
	public XAccount[] loadAccounts(String sessionId) throws SessionExpiredException {
		List<Account> accounts = null;
		AuthUser user = getLoggedInUser(sessionId);
		if (isAdmin(user)) {
			AdministrationService adminService = ServiceProvider
					.getAdministrationService(user);
			accounts = adminService.getAccounts(user);
		} else
			accounts = user.getAccounts();
		XAccount[] xAccounts = new XAccount[accounts.size()];
		int index = 0;
		for(Account account : accounts) 
			xAccounts[index++] = (XAccount) XConverter.createX(account);
		return xAccounts;
	}
	private final boolean isAdmin(AuthUser user) {
		return user.hasPermission(Right.READ, AdministrationService.class);
	}

	//	private final Account getAccountById(String accountId) {
//		List<Account> accounts = getLoggedInUser().getAccounts();
//		for(Account account : accounts) {
//			if(account.getId().equals(accountId))
//				return account;
//		}
//		return null;
//	}
	
	public List <XElement> loadElements(XDimension xdim) {
		ArrayList <XElement> elementList = new ArrayList<XElement>();

//		Connection connection = getConnection();
//		Database db = connection.getDatabaseById(xdim.getDbId());
//
//		Dimension dim = db.getDimensionById(xdim.getDimId());
//
//		int nElem = dim.getElementCount();
//		
//		for (int i = 0; i < nElem; i++) {
//			Element elem = dim.getElementAt(i);
//			XElement xelem = new XElement();
//			xelem.setName(elem.getName());			
//			xelem.setElementType(XElementType.fromString(elem.getTypeAsString()));
//			elementList.add(xelem);
//		}
		
		return elementList;
	}
	
	public List<XElement> loadRootElements(XHierarchy hierarchy) {
		ArrayList <XElement> roots = new ArrayList<XElement>();

//		Connection connection = getConnection();
//		Database db = connection.getDatabaseById(hierarchy.getDimension().getDatabase().getId());
//		Dimension dim = db.getDimensionById(hierarchy.getDimension().getId());
//		Hierarchy hier = dim.getHierarchyById(hierarchy.getId());
//		
//		XUser xUser = hierarchy.getUser(); 
//		AuthUser user = (AuthUser) XObjectMatcher.getNativeObject(xUser);
//		
//		for(Element root : hier.getRootElements()) {
//			XElement xElement = createXElement(root, hierarchy.getUser(), user);			
//			roots.add(xElement);
//		}
		return roots;
	}
	
//	private Connection getConnection()
//	{
//		ConnectionConfiguration config = ConnectionFactory.getInstance().getConfiguration("localhost", 
//				"7777", "admin", "admin");
//		config.setLoadOnDemand(true);
//		return ConnectionFactory.getInstance().newConnection(config);
//	}

//	public XUser login(String login, String password)
//			throws AuthenticationFailedException {		
////		register(new WPaloSessionListener(this));
//		// try to authenticate:
//		try {
//			AuthUser usr = ServiceProvider.getAuthenticationService()
//					.authenticate(login, password);
//			setLoggedInUser(usr);
//			return (XUser)XConverter.createX(usr);
//		} catch (org.palo.viewapi.exceptions.AuthenticationFailedException e) {
//			throw new AuthenticationFailedException(e.getMessage(), e);
//		}
//	}
//
////	public void logout() {
////		AuthUser user = getLoggedInUser();
////		List<Account> accounts = user.getAccounts();
////		for(Account account : accounts) {
////			if(account.isLoggedIn())
////				account.logout();
////		}
////		invalidateSession();
////	}
//	private final void invalidateSession() {
//		HttpSession session = getSession();
//		session.invalidate();
//	}

	public XWorksheet loadWorksheet(XTemplate template) {
//		try {
//		WSSTemplate temp = null;
////		(WSSTemplate) 
////			XObjectMatcher.getNativeObject(template);
//		if (temp == null) {
//			return null;
//		}
//
//		WSSApplication tensegrityApp = temp.getConnection().getSystemApplication();
//		XApplication xapp = new XApplication(template.getUser(), template.getAccount(), 
//				tensegrityApp.getId(), false, false);
//		xapp.setName(tensegrityApp.getName());
//		WSSWorkbook wb = tensegrityApp.getTemplateWorkbook(temp);
//		wb.select();
//		WSSWorksheet ws = wb.getDefaultWorksheet();
//		if (ws == null) {
//			// No worksheet present, so create one.
//			ws = wb.addWorksheet("Default");
//		}
//		XWorkbook xwb = new XWorkbook(wb.getId(), wb.getName(),
//				xapp, ws.getName());
//		xwb.setHasChildren(false);
//		XWorksheet xws = new XWorksheet(ws.getId(), ws.getName(), xwb);
//		xws.setHasChildren(false);
//		return xws;
//		} catch (Throwable t) {
//			t.printStackTrace();
//			return null;
//		}
		return null;
	}

//	private final XApplication findXApplication(WSSWorkbook wb, XUser user) {
////		for (XAccount xAcc: user.getAccounts()) {
////			if (xAcc.getConnection().getHost().equals(
////					wb.getApplication().getConnection().getHost())) {
////				XConnection xCon = xAcc.getConnection();
////				WSSConnection con = wb.getApplication().getConnection();
////				if (xCon.getService().equals(con.getService())) {
////					XApplication app = new XApplication(user, xAcc, wb.getApplication().getId(), true, false);
////					app.setName(wb.getApplication().getName());
////					return app;
////				}
////			}
////		}
//		return null;
//	}
	
	public XWorksheet loadWorksheet(XReport report, XUser user) {
//		if (!report.getReceiverType().equals(XConstants.TYPE_FOLDER_ELEMENT_SHEET)) {
//			return null;
//		}
//		FolderElement el = (FolderElement) 
//			XObjectMatcher.getNativeObject(report);		 
//		WSSWorkbook wb = (WSSWorkbook) ReportFolderChildLoader.getSourceObjectFromElement(
//				user, el);
//		wb.getApplication().select();
//		wb.select();
//		WSSWorksheet ws = wb.getDefaultWorksheet();
//		if (ws == null) {
//			// No worksheet present, so create one.
//			ws = wb.addWorksheet("Default");
//		}
//		XApplication xApp = findXApplication(wb, user);
//		XWorkbook xwb = new XWorkbook(wb.getId(), wb.getName(), xApp, ws.getName());
//		xwb.setHasChildren(false);
//		XWorksheet xws = new XWorksheet(ws.getId(), ws.getName(), xwb);
//		xws.setHasChildren(false);
//		return xws;
		return null;
	}
	

	public XStaticReportFolder createStaticFolder(String name, XObject parent, XUser usr) {
//		Object o = XObjectMatcher.getNativeObject(parent);
//		AuthUser user = (AuthUser) XObjectMatcher.getNativeObject(usr);
//		FolderService folderService = ServiceProvider.getFolderService(user);
//		if (o instanceof DynamicFolder) {
//			DynamicFolder df = (DynamicFolder) o;
//			StaticFolder sf;
//			try {
//				sf = folderService.createStaticFolder(name, df, null);
//				XStaticReportFolder xsf = new XStaticReportFolder(
//						sf.getName(), sf.getId(), false, false, usr);
//				XObjectMatcher.put(xsf, sf);
//				XObjectMatcher.put(parent, df);
//				ExplorerTreeNode root = df.getRoot();
//				folderService.save(root);
//				return xsf;
//			} catch (OperationFailedException e1) {
//				e1.printStackTrace();
//			}
//			return null;
//		} else if (o instanceof StaticFolder) {
//			StaticFolder par = (StaticFolder) o;
//			StaticFolder sf;
//			try {
//				sf = folderService.createStaticFolder(name, par, null);
//				XStaticReportFolder xsf = new XStaticReportFolder(
//						sf.getName(), sf.getId(), false, false, usr);
//				XObjectMatcher.put(xsf, sf);
//				XObjectMatcher.put(parent, par);
//				folderService.save(sf.getRoot());
//				return xsf;			
//			} catch (OperationFailedException e) {
//				e.printStackTrace();
//			}
//			return null;
//		} 
		return null;
	}

	public XDynamicReportFolder createDynamicFolder(String name, XObject parent, XUser usr) {
//		Object o = XObjectMatcher.getNativeObject(parent);
//		AuthUser user = (AuthUser) XObjectMatcher.getNativeObject(usr);
//		FolderService folderService = ServiceProvider.getFolderService(user);
//		if (o instanceof DynamicFolder) {
//			DynamicFolder df = (DynamicFolder) o;			
//			DynamicFolder ddf;
//			try {
//				ddf = folderService.createDynamicFolder(name, df, null, null, null);
//				XDynamicReportFolder xdf = new XDynamicReportFolder(ddf.getName(),
//						ddf.getId(), false, false, null, null, usr);
//				XObjectMatcher.put(xdf, ddf);
//				XObjectMatcher.put(parent, df);
//				folderService.save(ddf.getRoot());
//				return xdf;
//			} catch (OperationFailedException e) {
//				e.printStackTrace();
//			}
//			return null;
//		} else if (o instanceof StaticFolder) {
//			StaticFolder par = (StaticFolder) o;
//			DynamicFolder ddf;
//			try {
//				ddf = folderService.createDynamicFolder(name, par, null, null, null);
//				XDynamicReportFolder xdf = new XDynamicReportFolder(ddf.getName(),
//						ddf.getId(), false, false, null, null, usr);
//				XObjectMatcher.put(xdf, ddf);
//				XObjectMatcher.put(parent, par);
//				folderService.save(ddf.getRoot());
//				return xdf;
//			} catch (OperationFailedException e) {
//				e.printStackTrace();
//			}
//			return null;
//		} 
		return null;
	}

	public XReport [] addReceivers(XReportFolder parent, XObject [] receivers, XUser usr) {
//		AbstractExplorerTreeNode node = 
//			(AbstractExplorerTreeNode) XObjectMatcher.getNativeObject(parent);
//		if (receivers == null || receivers.length == 0) {
//			return new XReport[0];
//		}
//		XReport [] result = new XReport[receivers.length];
//		AuthUser user = (AuthUser) XObjectMatcher.getNativeObject(usr);
//		FolderService folderService = ServiceProvider.getFolderService(user);
//		ExplorerTreeNode root = null;
//		for (int i = 0; i < receivers.length; i++) {
//			XObject receiver = receivers[i];
//			Object o = XObjectMatcher.getNativeObject(receiver);
//			ParameterReceiver pr = null;
//			if (o instanceof WSSTemplate) {
//				WSSTemplate temp = (WSSTemplate) o;
//				WSSApplication tensegrityApp = temp.getConnection().getSystemApplication();
//				tensegrityApp.select();
//				pr = tensegrityApp.loadWorkbook(temp);
//			} else if (o instanceof ParameterReceiver) {
//				pr = (ParameterReceiver) o;						
//			} else {
//				continue;
//			}
//			if (pr == null) {
//				result[i] = null;
//				continue;
//			}
//			String name = "<unknown>";
//			String type = "<unknown>";
//			if (pr instanceof WSSWorkbook) {
//				name = ((WSSWorkbook) pr).getName();
//				type = XConstants.TYPE_FOLDER_ELEMENT_SHEET;
//			} else if (pr instanceof View) {
//				name = ((View) pr).getName();
//				type = XConstants.TYPE_FOLDER_ELEMENT_VIEW;
//			}
//			if (node instanceof DynamicFolder) {
//				DynamicFolder df = (DynamicFolder) node;			
////				FolderElement fe;
////				try {
////					fe = folderService.createFolderElement(name, df, null);
////					fe.setSourceObject(pr);
////					List <XElement> elements = retrieveElements(fe, usr, user);					
////					XReport xr = new XReport(name, fe.getId(), type, elements.toArray(new XElement[0]));
////					XObjectMatcher.put(xr, fe);
////					XObjectMatcher.put(parent, df);
////					result[i] = xr;
////					if (root == null) {
////						root = fe.getRoot();
////					}
////				} catch (OperationFailedException e1) {
////					e1.printStackTrace();
////				}
//			} else if (node instanceof StaticFolder) {
//				StaticFolder sf = (StaticFolder) node;
//				FolderElement fe;
////				try {
////					fe = folderService.createFolderElement(name, sf, null);
////					fe.setSourceObject(pr);
////					List <XElement> elements = retrieveElements(fe, usr, user);
////					XReport xr = new XReport(name, fe.getId(), type, 
////							elements.toArray(new XElement[0]));
////					XObjectMatcher.put(xr, fe);
////					XObjectMatcher.put(parent, sf);
////					result[i] = xr;
////					if (root == null) {
////						root = fe.getRoot();
////					}					
////				} catch (OperationFailedException e1) {
////					e1.printStackTrace();
////				}
//			}
//		}
//		if (root != null) {
//			try {
//				FolderModel.getInstance().save(user, root);
//			} catch (PaloIOException e) {
//				e.printStackTrace();
//			}
//		}
//		return result;
		return null;
	}

	private final String getValue(String key, String link) {
		String temp = link.toLowerCase();
		int index = temp.indexOf(key);
		if (index == -1) {
			return null;
		}
		int begin = temp.indexOf("\"", index);
		int end = temp.indexOf("\"", begin + 1);
		if (begin == -1 || end == -1) {
			return null;
		}
		return link.substring(begin + 1, end);
	}
		
	protected void myInitDbConnection(ServletContext globalContext, boolean createDefaultAccounts) {
		// HERE WE CREATE GLOBAL CONNECTIONs AND/OR GLOBAL CONNECTION POOLS
		try {
			DbConnection sqlConnection = (DbConnection) globalContext
					.getAttribute(SQL_CONNECTION);
			DbConnection dbConnection = null;
			if (sqlConnection == null && ServiceProvider.getDbConnection() == null) {
				dbConnection = createConnection();
				ServiceProvider.initialize(dbConnection, createDefaultAccounts);
				globalContext.setAttribute(SQL_CONNECTION, dbConnection);
			} else {
				if (sqlConnection == null) {
					sqlConnection = ServiceProvider.getDbConnection();
					globalContext.setAttribute(SQL_CONNECTION, dbConnection);
				}
				dbConnection = sqlConnection;
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			// TODO introduce an exception here...
			// throw new WPaloException("Couldn't start the application!!", e);
		}
	}

	private final DbConnection createConnection() {
		if (WPaloControllerServiceImpl.USE_MYSQL)
			return MySqlDbConnection.newInstance();
		else
			return HSqlDbConnection.newInstance();
	}
	
	private final View searchView(ExplorerTreeNode node, View view) {
		if (node == null) {
			return null;
		}
		if (node instanceof FolderElement) {
			ParameterReceiver pr = ((FolderElement) node).getSourceObject();
			if (pr != null && pr instanceof View) {
				if (view.getId().equals(((View) pr).getId())) {
					return view; 
				}
			}
		} 
		for (ExplorerTreeNode kid: node.getChildren()) {
			View v = searchView(kid, view);
			if (v != null) {
				return v;
			}		
		}
		return null;
	}
	
	private final boolean checkFlag(String name, String link) {
		String [] tokens = link.split(",");
		for (String t: tokens) {
			t = t.trim();
			if (t.startsWith("(")) {
				t = t.substring(1).trim();
			}
			if (t.endsWith(")")) {
				t = t.substring(0, t.length() - 1).trim();
			}
			if (t.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	private final boolean checkFlag(String name, Map <String, String> param) {
		return "1".equals(param.get(name));
	}

	private final List <Boolean> createDisplayFlags(String directLink) {
		String link = directLink.toLowerCase();
		List <Boolean> displayFlags = new ArrayList<Boolean>();												
		displayFlags.add(checkFlag("hidetitlebar", link));
		displayFlags.add(checkFlag("hidetoolbar", link));
		displayFlags.add(checkFlag("hidesave", link));
		displayFlags.add(checkFlag("hidesaveas", link));
		displayFlags.add(checkFlag("hidefilter", link));
		displayFlags.add(checkFlag("hidestaticfilter", link));
		displayFlags.add(checkFlag("hidehorizontalaxis", link));
		displayFlags.add(checkFlag("hideverticalaxis", link));
		displayFlags.add(checkFlag("hideconnectionpicker", link));
		displayFlags.add(checkFlag("hideconnectionaccount", link));
		displayFlags.add(checkFlag("hideuserrights", link));
//		boolean hideMulti = checkFlag("hideviewtabs", link);
//		boolean hideOverview = checkFlag("hidenavigator", link);
//		if (!hideOverview) {
//			hideMulti = false;
//		}
//		displayFlags.add(hideMulti);
//		displayFlags.add(hideOverview);				
		return displayFlags;		
	}
	
	private final List <Boolean> createGlobalDisplayFlags(String directLink) {
		String link = directLink.toLowerCase();
		List <Boolean> displayFlags = new ArrayList<Boolean>();												
		boolean hideMulti = checkFlag("hideviewtabs", link);
		boolean hideOverview = checkFlag("hidenavigator", link);
		boolean hideconnectionaccount = checkFlag("hideconnectionaccount", link);
		boolean hideuserrights = checkFlag("hideuserrights", link);
		if (!hideOverview) {
			hideMulti = false;
		}
		displayFlags.add(hideMulti);
		displayFlags.add(hideOverview);	
		displayFlags.add(hideconnectionaccount);
		displayFlags.add(hideuserrights);	
		return displayFlags;		
	}

	private final XDirectLinkData parseMultipleViews(String locale, AuthUser authUser, String link, XDirectLinkData data) {
		try {
			ViewService viewService = ServiceProvider.getViewService(authUser);
			int index = -1;
			List <XView> allViews = new ArrayList<XView>();
			while ((index = link.indexOf("[", index + 1)) != -1) {
				int rIndex = link.indexOf("]", index + 1);
				if (rIndex == -1) {
					data.addError(UserSession.trans(locale, "noMatchingClosingBracket"));							
					continue;
				}
				String linkToParse = link.substring(index + 1, rIndex);

				String view = getValue("openview", linkToParse);
				if (view == null) {
					data.addError(UserSession.trans(locale, "noViewIdSpecified"));				
					continue;
				}
				View v = viewService.getView(view);
				if (v == null) {
					data.addError(UserSession.trans(locale, "couldNotFindView", view));							
					continue;
				}
				
				String connectionId = v.getAccount().getConnection().getId();
				Account neededAccount = null;
				for (Account a: authUser.getAccounts()) {
					if (a.getConnection().getId().equals(connectionId)) {
						PaloConnection paloCon = a.getConnection();
						if (paloCon.getType() == PaloConnection.TYPE_WSS) {
							continue;
						} else {
							neededAccount = a;
							break;
						}
					}
				}
				
				if (neededAccount == null) {
					data.addError(UserSession.trans(locale, "noAccountForView", v.getName(), v.getId()));							
					continue;
				}
				
//				FolderService fs = ServiceProvider.getFolderService(authUser);
//				if (searchView(fs.getTreeRoot(), v) == null) {
//					data.addError("The view " + v.getName() + " (" + v.getId() + ") cannot be seen by this user.");
//					continue;
//				}

				boolean allowed = false;
				if (v.getOwner() != null) {
					if (authUser.getId().equals(v.getOwner().getId())) {
						allowed = true;
					}
				} 
				if (!allowed) {				
					for (Role r: v.getRoles()) {
						for (Role r2: authUser.getRoles()) {
							if (r2.getId().equals(r.getId())) {
								if (r2.hasPermission(Right.READ)) {
									allowed = true;
									break;
								}
							}
						}
						for (Group g: authUser.getGroups()) {
							for (Role r2: g.getRoles()) {
								if (r2.getId().equals(r.getId())) {
									if (r2.hasPermission(Right.READ)) {
										allowed = true;
										break;
									}
								}								
							}
						}						
					}
				}
				if (!allowed) {
					data.addError(UserSession.trans(locale, "viewCannotBeSeen", v.getName(), v.getId()));							
					continue;
				}
				
				ViewConverter conv = new ViewConverter();
				XView xView = (XView) conv.toXObject(v);
				if (xView != null) {					
					xView.setDisplayFlags(createDisplayFlags(linkToParse));
					allViews.add(xView);
				}
			}
			data.setViews(allViews.toArray(new XView[0]));
			return data;
		} catch (Throwable t) {
			logger.error(t.getMessage());
			data.addError(UserSession.trans(locale, "errorWhileOpeningMultipleViews", t.getLocalizedMessage()));
		}
		return data;
	}
	
	private final XDirectLinkData parseSingleView(String locale, AuthUser authUser, String link, XDirectLinkData data) {

		String view = getValue("openview", link);
		if (view == null) {
			return data;
		}
		//SpagoBI informations
		String spagoBIUser = getValue("spagobiusr", link);
		if (spagoBIUser != null) {			
			getSession().setAttribute("spagobiuser", spagoBIUser);
		}
		String spagoBIDoc = getValue("spagobidoc", link);
		if (spagoBIDoc != null) {			
			getSession().setAttribute("spagobidocument", spagoBIDoc);
		}
		String spagoBISub = getValue("spagobisubobj", link);
		if (spagoBISub != null) {			
			getSession().setAttribute("spagobisubobj", spagoBISub);
		}

		try {
			ViewService viewService = ServiceProvider.getViewService(authUser);
			/*SpagoBI modification begin*/
			//View v = viewService.getView(view);
			
			//Account name
			String accountName = getValue("account", link);
			//Connection
			String connection = getValue("connection", link);
			String cubeName = null;
			Cube cube = null;
			String accountId = null;
			if(view.equalsIgnoreCase("")){
				//cube name --> create view dinamically
				cubeName = getValue("cubename", link);

			}
			View v = null;

			for (Account a: authUser.getAccounts()) {
				if(a != null){

					if(view != null && !view.equals("")){						
						//if view specified
						List<View> views = viewService.getViews(a);
						if(!views.isEmpty()){
							Iterator it = views.iterator();
							while(it.hasNext()){
								View selView = (View)it.next();
								if(view != null && !view.equals("")){
									if(selView.getName().equals(view)){
										v = selView;
										break;
									}
								}
							}
						}

					}else{
						///tries to get cube
						if(a.getUser().getLoginName().equals(accountName)){
							if(a.getConnection().getName().equals(connection)){
								//use cube name
								accountId = a.getId();
								Connection con = ((PaloAccount) a).login();
								for (Database db: con.getDatabases()) {
									int connectionType = db.getConnection().getType();
									Cube c = db.getCubeByName(cubeName);
									if(c != null){
										cube = c;
										//((PaloAccount) a).logout();
										break;
									}

								}
								
							}
						}
					}
				}
			}

			/*end*/
			
			if (v != null) {
				String connectionId = v.getAccount().getConnection().getId();
				Account neededAccount = null;
				for (Account a: authUser.getAccounts()) {
					if (a.getConnection().getId().equals(connectionId)) {
						PaloConnection paloCon = a.getConnection();
						if (paloCon.getType() == PaloConnection.TYPE_WSS) {
							continue;
						} else {
							neededAccount = a;
							break;
						}
					}
				}
				
				if (neededAccount == null) {
					data.addError(UserSession.trans(locale, "noAccountForView", v.getName(), v.getId()));							
					return data;
				}

				boolean allowed = false;
				if (v.getOwner() != null) {
					if (authUser.getId().equals(v.getOwner().getId())) {
						allowed = true;
					}
				} 
				if (!allowed) {
					for (Role r: v.getRoles()) {
						for (Role r2: authUser.getRoles()) {
							if (r2.getId().equals(r.getId())) {
								if (r2.hasPermission(Right.READ)) {
									allowed = true;
									break;
								}
							}
						}
						for (Group g: authUser.getGroups()) {
							for (Role r2: g.getRoles()) {
								if (r2.getId().equals(r.getId())) {
									if (r2.hasPermission(Right.READ)) {
										allowed = true;
										break;
									}
								}								
							}
						}
					}
				}
				if (!allowed) {
					data.addError(UserSession.trans(locale, "viewCannotBeSeen", v.getName(), v.getId()));	
					logger.error("viewCannotBeSeen");
					return data;
				}

				
			}else if(v == null && cube != null){
				//dinamically create view
				//if doesn't exist
				View existingView = viewService.getViewByName(cubeName, cube);

				if(existingView == null){
					v = CubeViewConverter.createDefaultView(cubeName, cube, accountId, authUser, "", null);
					viewService.save(v);
				}else{
					v = existingView;
				}
			}else{
				data.addError(UserSession.trans(locale, "couldNotFindView", view));		
				logger.error("couldNotFindView");
				return data;
			}
			//SpagoBI modification
			String isDeveloper = getValue("isdeveloper", link);
			if(isDeveloper == null || isDeveloper.equals("")){
				//logger.info("user--> saving subobj");
				JPaloSavingUtil util = new JPaloSavingUtil();
				String xml = util.getSubobjectForJPalo(getSession(), v.getName());
				//logger.info("saved subobj"+xml);
			}
			ViewConverter conv = new ViewConverter();
			XView xView = (XView) conv.toXObject(v);
			xView.setDisplayFlags(createDisplayFlags(link));

			data.setViews(new XView [] {xView});
			return data;	
			
			
		} catch (Throwable t) {
			logger.error(t.getMessage());
			data.addError(UserSession.trans(locale, "errorWhileOpeningDirectLinkView", view, t.getLocalizedMessage()));
		}
		return data;		
	}
	
	public XDirectLinkData openViewAfterLogin(String locale, String sessionId, String link) {
		myInitDbConnection(getServletContext(), true);
		XDirectLinkData data = new XDirectLinkData();
		AuthUser user = null;
		try {
			user = getLoggedInUser(sessionId);
		} catch (SessionExpiredException e) {
		}
		if (user == null) {
			data.setAuthenticated(false);
			try {
				UserSession userSession = getUserSession(sessionId);			
				data.addError(userSession.translate("directLinkAuthenticationFailed"));
			} catch (SessionExpiredException e) {
				data.addError("You specified an invalid username/password combination. The user in the direct link could not be authenticated.");
			}
			return data;
		}
		
		List <Boolean> gFlags = createGlobalDisplayFlags(link);
		data.setAuthenticated(true);
		data.setGlobalDisplayFlags(gFlags);
		
		if (link.indexOf("[") != -1) {			
			return parseMultipleViews(sessionId, user, link, data);
		}
		return parseSingleView(sessionId, user, link, data);
	}
	
	private static final String decodePass(String key, String user, String pass_enc_base64, String viewid, SimpleLogger log) throws Exception {
//		String key = "ld4CfxNWfMTryPxwMYD5RK1WcpKXJw2k";

//		String user = "smith";
//		String pass_enc_base64 = "FiR0BxHyXTx3H+ElEcDMAQ==";
//		String viewid = "fgrp1-h1-n10";

		log.debug("Key  == " + key + " -- " + key.length());
		log.debug("User == " + user + " -- " + user.length());
		log.debug("Pass == " + pass_enc_base64 + " -- " + pass_enc_base64.length());
		log.debug("View == " + viewid + " -- " + viewid.length());
		
		byte[] pass_enc = new BASE64Decoder().decodeBuffer(pass_enc_base64);

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update((user + viewid).getBytes("UTF-8"));
		byte[] iv = md.digest();		

		Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"), new IvParameterSpec(iv));

		String pass = new String(cipher.doFinal(pass_enc), "UTF-8").trim();

		return pass;
	}

	private final String parseLine(String key, String line) {
		int index;	
		if ((index = line.indexOf(key)) != -1) {
			int dIndex = line.indexOf("define");
			if (dIndex != -1 && dIndex < index) {
				int q1 = line.indexOf(",", index);
				if (q1 != -1) {
					q1 = line.indexOf("'", q1);
					if (q1 != -1 && q1 < (line.length() - 1)) {
						int q2 = line.indexOf("'", q1 + 1);
						return line.substring(q1 + 1, q2);
					}
				}
			}
		}
		return null;
	}
	
	private final PaloSuiteData initPaloSuiteData(SimpleLogger log) throws IOException {
		String path = properties.getStringProperty("paloSuiteConfigPath");
		if (path == null || path.isEmpty()) {
			path = "../httpd/app/etc/config.php";
		}
		File file = new File(path);
		
		if (!file.exists()) {
			throw new FileNotFoundException("Configuration file does not exist here: " + file.getAbsolutePath());
		}
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		PaloSuiteData psd = new PaloSuiteData();		
		while ((line = br.readLine()) != null) {
			String temp = parseLine("'CFG_SECRET'", line);
			if (temp != null) psd.secret = temp;
			temp = parseLine("CFG_PALO_HOST", line);
			if (temp != null) psd.host = temp;
			temp = parseLine("CFG_PALO_PORT", line);
			if (temp != null) psd.port = temp;
		}
		br.close();
		log.debug("Done reading config. Result:");
		log.debug("  psd.secret = " + psd.secret + " -- " + psd.secret.length());
		log.debug("  psd.host   = " + psd.host);
		log.debug("  psd.port   = " + psd.port);
		return psd;
	}

	private final String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }
	
	private final boolean checkChecksum(String link, String sharedSecret, SimpleLogger log) {
		int index;
		if (link == null || (index = link.indexOf("&cksum=")) == -1) {
			return false;
		}
		int index2 = link.indexOf("&", index + 2);
		String cksum;
		if (index2 == -1) {
			cksum = link.substring(index + 7).trim();
		} else {
			cksum = link.substring(index + 7, index2).trim();
		}
		link = link.substring(0, index) + sharedSecret;
		log.debug("building checksum for this link: " + link);
	    MessageDigest md;
	    try {
	    	md = MessageDigest.getInstance("SHA-1");
	    	byte[] sha1hash = new byte[40];
	    	md.update(link.getBytes("UTF-8"), 0, link.length());
	    	sha1hash = md.digest();
	    	String hexSha1 = convertToHex(sha1hash);
	    	log.debug("Calculated SHA-1 hash: " + hexSha1);
	    	log.debug("Given SHA-1 hash:      " + cksum);
	    	return hexSha1 != null && hexSha1.equals(cksum);
	    } catch (Exception e) {
	    	logger.error(e.getMessage());
	    	log.error("Exception when creating SHA-1 checksum.", e);
	    	return false;
	    }
	}
	
	private final List <ConnectionDescriptor> readConnections(String user, String pass, PaloSuiteData psd, String viewId, XDirectLinkData data, SimpleLogger log) {
		ConnectionConfiguration cfg = 
			ConnectionFactory.getInstance().getConfiguration(psd.host, psd.port, user, pass);
		cfg.setTimeout(120000);
		cfg.setLoadOnDemand(true);
		try {
			Connection con = ConnectionFactory.getInstance().newConnection(cfg);
			Database db = con.getDatabaseByName("Config");
			if (db != null) {
				Cube c = db.getCubeByName("#_connections");
				if (c != null) {
					Dimension conDim = c.getDimensionByName("connections");
					Dimension conAttribDim = c.getDimensionByName("#_connections_");
					ArrayList <Element []> coords = new ArrayList<Element[]>();
					ArrayList <Element> attributes = new ArrayList<Element>();
					addElement(attributes, conAttribDim, "type");
					addElement(attributes, conAttribDim, "name");
					addElement(attributes, conAttribDim, "host");
					addElement(attributes, conAttribDim, "port");
					addElement(attributes, conAttribDim, "username");
					addElement(attributes, conAttribDim, "password");
					addElement(attributes, conAttribDim, "active");
					addElement(attributes, conAttribDim, "useLoginCred");
					ArrayList <ConnectionDescriptor> connections = 
						new ArrayList<ConnectionDescriptor>();
					for (Element e: conDim.getElements()) {
						for (Element e2: attributes) {
							coords.add(new Element [] {e2, e});
						}
					}
					Object [] result = c.getDataBulk(coords.toArray(new Element[0][0]));
					int counter = 0;
					for (int i = 0; i < conDim.getElementCount(); i++) {
						if (counter >= result.length) {
							log.debug("Found connection to which this user has no rights. Skipping it (or them).");
							break;
						}
						String type = result[counter++].toString();
						String name = result[counter++].toString();
						String host = result[counter++].toString();
						String port = result[counter++].toString();
						String uuser = result[counter++].toString();
						String ppass = result[counter++].toString();
						String active = result[counter++].toString();
						String useLogin = result[counter++].toString();
						ConnectionDescriptor cd = new ConnectionDescriptor(
								type, name, host, port, uuser, ppass, active, useLogin);
						connections.add(cd);
						log.debug("Adding connection descriptor: " + type + ", " + name + ", " + host + ", " + port + ", " + user + ", " + pass + ", " + active + ", " + useLogin);
					}
					return connections;
				}				
			} else {
				log.error("No configuration database found...", new NullPointerException());
				return new ArrayList<ConnectionDescriptor>();
			}			
		} catch (Throwable t) {
			logger.error(t.getMessage());
			log.error("Error authenticating (WPALO!) user", t);
		}
			
		return new ArrayList<ConnectionDescriptor>();
	}

	private final List <ConnectionDescriptor> getConnectionDescriptors(String link) {
		String encodedLink = link.length() > 0 ? link.substring(1) : link;
		try {
			link = URLDecoder.decode(link, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
		}
		XDirectLinkData data = new XDirectLinkData();
		List <Boolean> list = new ArrayList<Boolean>();
		
		PaloSuiteData psd;
				
		try {
			psd = initPaloSuiteData(new SimpleLogger(1));
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
		if (psd.secret == null || psd.host == null || psd.port == null) {
			return new ArrayList<ConnectionDescriptor>();
		}
		
		StringTokenizer tok = new StringTokenizer(link, "&");
		Map <String, String> parameters = new HashMap<String, String>();
		while (tok.hasMoreTokens()) {
			String parameter = tok.nextToken();
			if (parameter.startsWith("?")) {
				parameter = parameter.substring(1);
			}
			int equal = parameter.indexOf("=");
			if (equal == -1) {
				continue;
			}
			String key = parameter.substring(0, equal);
			String value = "";
			if ((equal + 1) < parameter.length()) {
				value = parameter.substring(equal + 1);
			}
			parameters.put(key.toLowerCase(), value);
		}
		String user = parameters.get("user");
		String passEnc64 = parameters.get("pass");
		String viewId = parameters.get("viewid");
		
		if (user == null || passEnc64 == null || viewId == null) {
			return new ArrayList<ConnectionDescriptor>();
		}
		
		if (!checkChecksum(encodedLink, psd.secret, new SimpleLogger(1))) {
			return new ArrayList<ConnectionDescriptor>();
		}
		
		
		String decPass = null;
		try {
			decPass = decodePass(psd.secret, user, passEnc64, viewId, new SimpleLogger(1));
		} catch (Exception e) {			
		}
		if (decPass == null) {
			return new ArrayList<ConnectionDescriptor>();
		}
		
		return readConnections(user, decPass, psd, viewId, data, new SimpleLogger(1)); 		
	}
	
	public XAccount[] loadPaloSuiteAccounts(String sessionId, String link) throws SessionExpiredException {
		List<Account> acc = null;
		AuthUser user = getLoggedInUser(sessionId);
		if (isAdmin(user)) {
			AdministrationService adminService = ServiceProvider
					.getAdministrationService(user);
			acc = adminService.getAccounts(user);
		} else {
			acc = user.getAccounts();
		}
		List <Account> accounts = new ArrayList<Account>();
		List <ConnectionDescriptor> descriptors = getConnectionDescriptors(link);
		for (Account a: acc) {
			for (ConnectionDescriptor c: descriptors) {
				if (c.host.equals(a.getConnection().getHost()) &&
					c.port.equals(a.getConnection().getService())) {
					accounts.add(a);
					break;
				}
			}
		}
		XAccount[] xAccounts = new XAccount[accounts.size()];
		int index = 0;
		for(Account account : accounts) 
			xAccounts[index++] = (XAccount) XConverter.createX(account);
		return xAccounts;
	}

	public XDirectLinkData openPaloSuiteView(String locale, String link) {
		int index, level = 4;
				
		if ((index = link.indexOf("&debug=")) == -1) {
			index = link.indexOf("?debug=");
		}
		if (index != -1) {
			if ((index + 8) <= link.length()) {
				level = Integer.parseInt(link.substring(index + 7, index + 8));
			} else {
				level = Integer.parseInt(link.substring(index + 7));
			}
		}
		
		SimpleLogger log = new SimpleLogger(level);		
		String encodedLink = link.length() > 0 ? link.substring(1) : link;
		log.debug("Encoded Link: " + encodedLink);
		try {
			link = URLDecoder.decode(link, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			logger.error(e1.getMessage());
		}
		log.debug("Decoded Link: " + link);
		XDirectLinkData data = new XDirectLinkData();
		List <Boolean> list = new ArrayList<Boolean>();
		
		try {
		myInitDbConnection(getServletContext(), false);
		PaloSuiteData psd;
				
		try {
			psd = initPaloSuiteData(log);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
		if (psd.secret == null) {
			data.addError("No shared secret information found. Can't continue.<br/>");
		}
		if (psd.host == null) {
			data.addError("No palo configuration database host specified. Can't continue.<br/>");
		}
		if (psd.port == null) {
			data.addError("No palo configuration database port specified. Can't continue.<br/>");
		}
		if (data.getErrors() != null && data.getErrors().length > 0) {
			psd.secret = null;
			psd = null;
			return data;
		}		
		
		StringTokenizer tok = new StringTokenizer(link, "&");
		Map <String, String> parameters = new HashMap<String, String>();
		parameters.put("hideviewtabs", "1");
		parameters.put("hidenavigator", "1");
		parameters.put("hidetitlebar", "1");
		parameters.put("hidesaveas", "1");
		parameters.put("hideconnectionpicker", "1");
		while (tok.hasMoreTokens()) {
			String parameter = tok.nextToken();
			if (parameter.startsWith("?")) {
				parameter = parameter.substring(1);
			}
			int equal = parameter.indexOf("=");
			if (equal == -1) {
				continue;
			}
			String key = parameter.substring(0, equal);
			String value = "";
			if ((equal + 1) < parameter.length()) {
				value = parameter.substring(equal + 1);
			}
			parameters.put(key.toLowerCase(), value);
		}
		list.add(checkFlag("hideviewtabs", parameters));
		list.add(checkFlag("hidenavigator", parameters));
		data.setGlobalDisplayFlags(list);
		String user = parameters.get("user");
		String passEnc64 = parameters.get("pass");
		String viewId = parameters.get("viewid");
		
		if (user == null) {
			data.addError(UserSession.trans(locale, "noDirectLinkUsernameSpecified"));
		}
		if (passEnc64 == null) {
			data.addError(UserSession.trans(locale, "noDirectLinkPasswordSpecified"));
		}
		if (viewId == null) {
			data.addError(UserSession.trans(locale, "noDirectLinkViewSpecified"));
		}
		if (data.getErrors() != null && data.getErrors().length > 0) {
			return data;
		}
		
		if (!checkChecksum(encodedLink, psd.secret, log)) {
			data.addError(UserSession.trans(locale, "checksumDoesNotMatch"));
			return data;
		}
		
		
		String decPass = null;
		try {
			decPass = decodePass(psd.secret, user, passEnc64, viewId, log);
		} catch (Exception e) {
			data.addError(UserSession.trans(locale, "directLinkAuthenticationFailed"));
		}
		if (decPass == null || (data.getErrors() != null && data.getErrors().length > 0)) {
			return data;
		}
		
		data = readConfigForUser(user, decPass, psd, viewId, data, log, locale);
		List <Boolean> displayFlags = new ArrayList<Boolean>();
		displayFlags.add(checkFlag("hidetitlebar", parameters));
		displayFlags.add(checkFlag("hidetoolbar", parameters));
		displayFlags.add(checkFlag("hidesave", parameters));
		displayFlags.add(true); //checkFlag("hidesaveas", link));
		displayFlags.add(checkFlag("hidefilter", parameters));
		displayFlags.add(checkFlag("hidestaticfilter", parameters));
		displayFlags.add(checkFlag("hidehorizontalaxis", parameters));
		displayFlags.add(checkFlag("hideverticalaxis", parameters));
		displayFlags.add(checkFlag("hideconnectionpicker", parameters));
		int counter = 0;
		for (Boolean b: displayFlags) {
			log.debug("Display flag " + counter++ + ": " + b);
		}
		if (data.getViews() != null && data.getViews().length == 1) {
			XView xView = data.getViews()[0];

			xView.setDisplayFlags(displayFlags);
		}
		data.setDisplayFlags(displayFlags);
//		log.debug("DecPass = " + decPass);		
		} catch (Throwable t) {
			log.error("Error parsing configuration: ", t);
		}		
		return data;
	}
	class ConnectionDescriptor {
		ConnectionDescriptor(String type, String name, String host, String port, String user, String pass, String active, String useLoginCredentials) {
			this.type = type;
			this.name = name;
			this.host = host;
			this.port = port;
			this.user = user;
			this.pass = pass;
			this.active = active != null && active.equals("1");
			this.useLoginCredentials = useLoginCredentials != null && useLoginCredentials.equals("1");
		}
		
		String type;
		String name;
		String host;
		String port;
		String user;
		String pass;
		boolean active;
		boolean useLoginCredentials;
	}
	
	private final void addElement(ArrayList <Element> attributes, Dimension dim, String name) {
		Element e = dim.getElementByName(name);
		if (e == null) {
			throw new IllegalArgumentException("Element name " + name + " does not exist in attribute dimension.");
		}
		attributes.add(e);
	}
	
	private final User getUser(AdministrationService admService, AuthUser admin, String user, String pass) throws SQLException, OperationFailedException {
		User viewApiUser = null;
		for (User usr: admService.getUsers()) {
			if (user.equals(usr.getLoginName())) {
				viewApiUser = usr;
				break;
			}			
		}
		if (viewApiUser == null) {
			if (user.equals("admin")) {
				viewApiUser = admin;
			} else {
				viewApiUser = admService.createUser("", "", user, pass);
				admService.save(viewApiUser);
			}					
			Role viewerRole = admService.getRoleByName("VIEWER");
			Role editorRole = admService.getRoleByName("EDITOR");
			IUserRoleManagement urAssoc = MapperRegistry.getInstance().getUserRoleAssociation();				
			urAssoc.insert(viewApiUser, viewerRole);
			urAssoc.insert(viewApiUser, editorRole);
			admService.add(viewerRole, viewApiUser);
			admService.add(editorRole, viewApiUser);
			admService.save(viewApiUser);
		}	
		
		return viewApiUser;
	}
	
	private final void attachConnections(AdministrationService admService, AuthUser admin, ArrayList <ConnectionDescriptor> connections, String host, String port, SimpleLogger log) throws OperationFailedException {
		for (ConnectionDescriptor cd: connections) {
			boolean found = false;
			for (PaloConnection conn: admService.getConnections()) {				
				if (cd.host.equals(conn.getHost()) &&
					cd.port.equals(conn.getService())) {
					found = true;
					break;				
				}
			}
			if (!found) {
				if (cd.type.equalsIgnoreCase("palo")) {
					if (cd.useLoginCredentials) {
						PaloConnection con = admService.createConnection(cd.name, cd.host, cd.port, PaloConnection.TYPE_HTTP);
						admService.save(con);					
					} else {
						PaloConnection con = admService.createConnection(cd.name, cd.host, cd.port, PaloConnection.TYPE_HTTP);
						admService.save(con);						
					}
				} 
//				else if (cd.type.equalsIgnoreCase("dynamic")) {
//					PaloConnection con = admService.createConnection(cd.name, cd.host, cd.port, PaloConnection.TYPE_HTTP);
//					admService.save(con);					
//				}
				else {
					log.warn("Unknown connection type: " + cd.type + " ignored...");
				}
			}
		}
	}
	
	private final AuthUser attachAccounts(String userName, String decodedPass, AdministrationService admService, AuthUser admin, User user, String pass, ArrayList <ConnectionDescriptor> connections, SimpleLogger log) throws SQLException, OperationFailedException, AuthenticationFailedException {
		AuthUser authenticatedUser = null;
		try {
			if (user.getLoginName().equals("admin")) {
				authenticatedUser = ServiceProvider.getAuthenticationService().authenticateAdmin();
			} else {
				authenticatedUser = ServiceProvider.getAuthenticationService().authenticate(user.getLoginName(), pass);
			}
			if (!user.getLoginName().equals("admin")) {
				admService.setPassword(AuthUserImpl.encrypt(pass), user);
				admService.save(user);
			}
		} catch (AuthenticationFailedException e) {
			if (!user.getLoginName().equals("admin")) {
				admService.setPassword(AuthUserImpl.encrypt(pass), user);					
				admService.save(user);
			}
			authenticatedUser = ServiceProvider.getAuthenticationService().authenticate(user.getLoginName(), pass);
		}
		
		if (authenticatedUser == null) {
			log.error("Authentication of PaloPivot user failed! Aborting.", new NullPointerException());
			return authenticatedUser;
		}
		for (ConnectionDescriptor cd: connections) {
			boolean found = false;		
			for (Account acc: authenticatedUser.getAccounts()) {
				if (acc.getConnection().getHost() != null && acc.getConnection().getHost().equals(cd.host) &&
					acc.getConnection().getService() != null && acc.getConnection().getService().equals(cd.port)) {
//					if (acc.getConnection() != null && acc.getConnection().getName().equals(cd.name)) {
//						found = true;
//						break;
//					}
					if (/*cd.type.equalsIgnoreCase("dynamic") ||*/
						cd.useLoginCredentials) {
						admService.setLoginName(userName, acc);
						admService.setPassword(decodedPass, acc);
						admService.save(acc);						
					}
					log.debug("Account already exists: " + acc.getConnection().getHost() + ", " + acc.getConnection().getService());
					found = true; 
					break;
				}
			}
			if (!found) {
				PaloConnection con = null;
				for (PaloConnection conn: admService.getConnections()) {				
					if (cd.host.equals(conn.getHost()) &&
						cd.port.equals(conn.getService())) {
						con = conn;
						break;				
					}
				}
				if (con == null) {
					log.warn("No connection found for " + cd.host + ":" + cd.port + ". Ignoring.");
					continue;
				}
				Account acc = null;
				if (cd.type.equalsIgnoreCase("palo")) {
					if (cd.useLoginCredentials) {
						log.debug("Creating account (dynamic) " + userName + ", " + decodedPass + " for user " + user.getLoginName());
						admService.createAccount(userName, decodedPass, authenticatedUser, con);
					} else {
						log.debug("Creating account (palo) " + cd.user + ", " + cd.pass + " for user " + user.getLoginName());
						admService.createAccount(cd.user, cd.pass, authenticatedUser, con);						
					}
				} 
//				else if (cd.type.equalsIgnoreCase("dynamic")) {
//					log.debug("Creating account (dynamic) " + userName + ", " + decodedPass + " for user " + user.getLoginName());
//					admService.createAccount(userName, decodedPass, authenticatedUser, con);
//				}				
				admService.save(acc);
				admService.save(user);
			}			
		}
		log.debug("  ==> After account creation, the authenticated user has the following accounts:");
		for (Account a: authenticatedUser.getAccounts()) {
			log.debug("       => " + a.getLoginName() + ", " + a.getConnection().getHost() + ", " + a.getConnection().getService());
		}
		return authenticatedUser;
	}
	
	private final boolean checkPaloSuiteId(View v, String viewId, SimpleLogger log) {
		String def = v.getDefinition();
		log.debug("  Searching for: " + viewId);
		int index; 
		if (def != null && (index = def.indexOf("<property id=\"paloSuiteID\" value=")) != -1) {
			log.debug("  Found paloSuiteID");
			int i2 = def.indexOf("value=\"", index) + 7;
			if (i2 != -1) {
				log.debug("  Found paloSuiteID value");
				int i3 = def.indexOf("\"", i2);
				if (i3 != -1) {
					String id = def.substring(i2, i3);
					log.debug("  PaloSuiteID value == " + id);
					if (viewId.equals(id)) {
						log.debug("  All's well, returning true, matching ID found.");
						return true;
					} else {
						log.debug("  ==> IDs DO NOT MATCH!!");
					}
				} else {
					log.debug("  ==> i3 == -1!!");
				}
			} else {
				log.debug("  ===> NO paloSuiteID value found!!");
			}
		} else {
			log.debug("  ===> NO paloSuiteID property found!!");
		}
		return false;
	}
	
	private final XDirectLinkData findPaloSuiteView(AuthUser authenticatedUser, String viewId, SimpleLogger log, XDirectLinkData data, ArrayList <ConnectionDescriptor> connections, String locale) {
		Account accountToUse = null;
		View viewToUse = null;
		IViewManagement mgmt = MapperRegistry.getInstance().getViewManagement();
		Account bestMatch = null;
		View bestView = null;
		View viewIdFound = null;
		
		log.debug("Listing all existing views:");
		try {
			for (View v: mgmt.listViews()) {
				log.debug("  " + v.getName() + " does it match?");
				if (checkPaloSuiteId(v, viewId, log)) {
					log.debug("   => it has the correct paloSuiteId, so go on... List accounts:");
					viewIdFound = v;
					for (Account acc: authenticatedUser.getAccounts()) {
						String host = acc.getConnection().getHost();
						String service = acc.getConnection().getService();
						String user = acc.getLoginName();
						boolean found = false;
						for (ConnectionDescriptor desc: connections) {
							if (desc.host.equals(host) &&
								desc.port.equals(service)) {
								found = true;
								break;
							}
						}
						if (!found) {
							// User has no rights for this connection (anymore).
							continue;
						}
						PaloConnection c = v.getAccount().getConnection();
						log.debug("       account: " + acc.getLoginName() + ", " + acc.getPassword() + " [" + host + ", " + service + ", " + user + "]");
						log.debug("       looking for: " + c.getHost() + ", " + c.getService() + ", " + v.getAccount().getUser().getLoginName());
						if (c.getHost().equals(host) && c.getService().equals(service)) {
							if (v.getAccount().getUser().getLoginName().equals(user)) {
								log.debug("             => HIT! All's well.");
								accountToUse = acc;
								viewToUse = v;
								break;
							}
							log.debug("            => Close match. All's well.");
							bestMatch = acc;
							bestView = v;
						}
					}
				} 
				if (viewToUse != null && accountToUse != null) {
					break;
				}
			}
		} catch (SQLException e) {				
		}
		data.setUserPassword(authenticatedUser.getPassword());
		data.setPaloSuiteViewId(viewId);
		
		if (accountToUse == null && bestMatch == null) {
			log.debug("No account or no view found. Either the view does not exist, or something's rotten in the state of Denmark.");
		}
		
		if (accountToUse == null && bestMatch != null) {
			log.debug("No exact account match found, but something close enough: " + bestMatch.getLoginName() + ", " + bestMatch.getConnection().getHost() + ", " + bestMatch.getConnection().getService());
			accountToUse = bestMatch;
			viewToUse = bestView;
		}
		if (accountToUse != null) {
			AccountConverter co = new AccountConverter();
			XAccount xAccount = (XAccount) co.toXObject(accountToUse);
			data.setXAccount(xAccount);
			log.debug("AccountToUse:");
			log.debug("  Name: " + xAccount.getLogin());
			log.debug("  Pass: " + data.getUserPassword());
			log.debug("View: " + viewToUse.getName());
			data.setAuthenticated(true);			
			ViewConverter conv = new ViewConverter();
			XView xView = (XView) conv.toXObject(viewToUse);										
			data.setViews(new XView [] {xView});										
		}
		if (viewIdFound != null && accountToUse == null) {
			data.addError(UserSession.trans(locale, "viewCannotBeSeen", viewIdFound.getName(), viewIdFound.getId()));
		}
		return data;				
	}
	
	private final XDirectLinkData parsePaloStudioConnectionData(String paloSuiteUser, String paloSuitePass, String paloSuiteHost, String paloSuitePort, Database configDb, String viewId, SimpleLogger log, XDirectLinkData data, String locale) {
		Cube c = configDb.getCubeByName("#_connections");
		if (c != null) {
			Dimension conDim = c.getDimensionByName("connections");
			Dimension conAttribDim = c.getDimensionByName("#_connections_");
			ArrayList <Element []> coords = new ArrayList<Element[]>();
			ArrayList <Element> attributes = new ArrayList<Element>();
			addElement(attributes, conAttribDim, "type");
			addElement(attributes, conAttribDim, "name");
			addElement(attributes, conAttribDim, "host");
			addElement(attributes, conAttribDim, "port");
			addElement(attributes, conAttribDim, "username");
			addElement(attributes, conAttribDim, "password");
			addElement(attributes, conAttribDim, "active");
			addElement(attributes, conAttribDim, "useLoginCred");
			ArrayList <ConnectionDescriptor> connections = 
				new ArrayList<ConnectionDescriptor>();
			for (Element e: conDim.getElements()) {
				for (Element e2: attributes) {
					coords.add(new Element [] {e2, e});
				}
			}
			Object [] result = c.getDataBulk(coords.toArray(new Element[0][0]));
			int counter = 0;
			for (int i = 0; i < conDim.getElementCount(); i++) {
				if (counter >= result.length) {
					log.debug("Found connection to which this user has no rights. Skipping it (or them).");
					break;
				}
				String type = result[counter++].toString();
				String name = result[counter++].toString();
				String host = result[counter++].toString();
				String port = result[counter++].toString();
				String user = result[counter++].toString();
				String pass = result[counter++].toString();
				String active = result[counter++].toString();
				String useLogin = result[counter++].toString();
				ConnectionDescriptor cd = new ConnectionDescriptor(
						type, name, host, port, user, pass, active, useLogin);
				connections.add(cd);
				log.debug("Adding connection descriptor: " + type + ", " + name + ", " + host + ", " + port + ", " + user + ", " + pass + ", " + active + ", " + useLogin);
			}
			// Create account for every connection...
			try {
				AuthUser admin = ServiceProvider.getAuthenticationService().authenticateAdmin();
				AdministrationService admService = ServiceProvider.getAdministrationService(admin);

				// Step 1: GetUser:
				User viewApiUser = getUser(admService, admin, paloSuiteUser, paloSuitePass);
				if (viewApiUser == null) {
					log.error("Null view API User", new NullPointerException());
					return null; 
				}
				log.debug("ViewAPIUSer = " + viewApiUser.getLoginName());
				
				// Step 2: Make sure all connections exist, add new ones:
				attachConnections(admService, admin, connections, paloSuiteHost, paloSuitePort, log);
				
				// Step 3: Make sure all accounts exist, add new ones:
				AuthUser authUser = attachAccounts(paloSuiteUser, paloSuitePass, admService, admin, viewApiUser, paloSuitePass, connections, log);
				
				// Step 4: Now find the view with the Id x and open it...
				if (authUser != null) {
					log.debug("AuthUser != null; " + authUser.getLoginName() + " => finding Views.");
					return findPaloSuiteView(authUser, viewId, log, data, connections, locale);
				} else {
					log.warn("==> authUser == null, so no view could be found?!");
				}
			} catch (Exception e) {
				log.error("Error when mapping user", e);
			}			
		} else {
			log.error("No Configuration database found.", new NullPointerException());
		}
		return null;
	}
	
	private final XDirectLinkData legacyAuthentication(Connection con, String user, String pass, PaloSuiteData psd, String viewId, XDirectLinkData data, SimpleLogger log) throws OperationFailedException, SQLException, AuthenticationFailedException {
		if (con.getDatabaseCount() > 0) {
			AuthUser admin = ServiceProvider.getAuthenticationService().authenticateAdmin();
			AdministrationService admService = ServiceProvider.getAdministrationService(admin);
			User viewApiUser = null;
			for (User usr: admService.getUsers()) {
				if (user.equals(usr.getLoginName())) {
					viewApiUser = usr;
					break;
				}			
			}
			if (viewApiUser == null) {
				if (user.equals("admin")) {
					viewApiUser = admin;
				} else {
					viewApiUser = admService.createUser("", "", user, pass);
					admService.save(viewApiUser);
				}					
				Role viewerRole = admService.getRoleByName("VIEWER");
				Role editorRole = admService.getRoleByName("EDITOR");
				IUserRoleManagement urAssoc = MapperRegistry.getInstance().getUserRoleAssociation();				
				urAssoc.insert(viewApiUser, viewerRole);
				urAssoc.insert(viewApiUser, editorRole);
				admService.add(viewerRole, viewApiUser);
				admService.add(editorRole, viewApiUser);
				admService.save(viewApiUser);
			}
			PaloConnection conToUse = null;
			for (PaloConnection conn: admService.getConnections()) {
				if (psd.host.equals(conn.getHost()) &&
					psd.port.equals(conn.getService())) {
					conToUse = conn;
					break;
				}
			}
			if (conToUse == null) {
				conToUse = admService.createConnection("PaloCon", psd.host, psd.port, PaloConnection.TYPE_HTTP);
				admService.save(conToUse);
			}
			Account accountToUse = null;
			AuthUser authenticatedUser = null;
			try {
				if (viewApiUser.getLoginName().equals("admin")) {
					authenticatedUser = ServiceProvider.getAuthenticationService().authenticateAdmin();
				} else {
					authenticatedUser = ServiceProvider.getAuthenticationService().authenticate(viewApiUser.getLoginName(), pass);
				}
				if (!viewApiUser.getLoginName().equals("admin")) {
					admService.setPassword(pass, viewApiUser);
					admService.save(viewApiUser);
				}
			} catch (AuthenticationFailedException e) {
				if (!viewApiUser.getLoginName().equals("admin")) {
					admService.setPassword(pass, viewApiUser);					
					admService.save(viewApiUser);
				}
				authenticatedUser = ServiceProvider.getAuthenticationService().authenticate(viewApiUser.getLoginName(), pass);
			}
			
			if (authenticatedUser != null) {
				for (Account acc: authenticatedUser.getAccounts()) {
					if (acc.getConnection().getId().equals(conToUse.getId())) {
						accountToUse = acc;
					}
				}
				if (accountToUse == null) {
					accountToUse = admService.createAccount(user, pass, viewApiUser, conToUse);
					admService.save(accountToUse);
				}
			}
			if (accountToUse != null && authenticatedUser != null) {
				AccountConverter co = new AccountConverter();
				XAccount xAccount = (XAccount) co.toXObject(accountToUse);
				data.setXAccount(xAccount);
				data.setUserPassword(accountToUse.getUser().getPassword());
				log.debug("AccountToUse:");
				log.debug("  Name: " + xAccount.getLogin());
				log.debug("  Pass: " + data.getUserPassword());
				data.setAuthenticated(true);
				data.setPaloSuiteViewId(viewId);
				ViewService viewService = ServiceProvider.getViewService(authenticatedUser);
				for (View v: viewService.getViews(accountToUse)) {
					String def = v.getDefinition();
					int index; 
					if (def != null && (index = def.indexOf("<property id=\"paloSuiteID\" value=")) != -1) {
						int i2 = def.indexOf("value=\"", index) + 7;
						if (i2 != -1) {
							int i3 = def.indexOf("\"", i2);
							if (i3 != -1) {
								String id = def.substring(i2, i3);
								if (viewId.equals(id)) {
									ViewConverter conv = new ViewConverter();
									XView xView = (XView) conv.toXObject(v);										
									data.setViews(new XView [] {xView});										
									return data;				
								}
							}
						}
					}
				}
			}			
			log.debug("AuthenticatedUser: " + authenticatedUser);
			log.debug("Account to use: " + accountToUse);
		}	
		return data;
	}
	
	private final XDirectLinkData readConfigForUser(String user, String pass, PaloSuiteData psd, String viewId, XDirectLinkData data, SimpleLogger log, String locale) {
//		// TODO IMPORTANT REMOVE BEFORE BUILD
//		user = "testuser";
//		pass = "testuser";
//		// TODO IMPORTANT REMOVE BEFORE BUILD

		log.debug("Connection data:");
		log.debug("  Host: " + psd.host);
		log.debug("  Port: " + psd.port);
		log.debug("  User: " + user);
		log.debug("  And some password...");
		
		ConnectionConfiguration cfg = 
			ConnectionFactory.getInstance().getConfiguration(psd.host, psd.port, user, pass);
		cfg.setTimeout(120000);
		cfg.setLoadOnDemand(true);
		try {
			Connection con = ConnectionFactory.getInstance().newConnection(cfg);
			Database db = con.getDatabaseByName("Config");
			if (db != null) {
				return parsePaloStudioConnectionData(user, pass, psd.host, psd.port, db, viewId, log, data, locale);
			} else {
				log.error("No configuration database found...", new NullPointerException());
				return null;
			}			
		} catch (Throwable t) {
			log.error("Error authenticating (WPALO!) user", t);
		}
		
		return data;
	}
	
	public XDirectLinkData openViewDirectly(String locale, String link) {		
		myInitDbConnection(getServletContext(), true);

		XDirectLinkData data = new XDirectLinkData();
		
		// Parse link information:
//		String connectionId = getValue("connection", link);
//		if (connectionId == null) return null;
				
		String user = getValue("user", link);
		if (user == null) {
			data.setAuthenticated(false);
			data.addError(UserSession.trans(locale, "noDirectLinkUsernameSpecified"));
			return data;
		}
		
		String pass = getValue("pass", link);
		if (pass == null) {
			data.setAuthenticated(false);
			data.addError(UserSession.trans(locale, "noDirectLinkPasswordSpecified"));
			return data;
		}
		//SpagoBI informations
		String spagoBIUser = getValue("spagobiusr", link);
		if (spagoBIUser != null) {			
			getSession().setAttribute("spagobiuser", spagoBIUser);
		}
		String spagoBIDoc = getValue("spagobidoc", link);
		if (spagoBIDoc != null) {			
			getSession().setAttribute("spagobidocument", spagoBIDoc);
		}
		String isDeveloper = getValue("isdeveloper", link);
		if (isDeveloper != null) {			
			getSession().setAttribute("isdeveloper", isDeveloper);
		}
		
		List <Boolean> gFlags = createGlobalDisplayFlags(link);
		data.setGlobalDisplayFlags(gFlags);
		
		int index = link.indexOf("pass=\"");
		int index2 = link.indexOf("\"", index + 6);
		String modifiedLink = link.substring(0, index) + link.substring(index2 + 1);
		
		AuthUser authUser = null;
		try {
			authUser = ServiceProvider.getAuthenticationService().authenticateHash(user, pass);
		} catch (AuthenticationFailedException e) {
			logger.error(e.getMessage());
		}
		if (authUser == null) {
			logger.error("direct link auth failed");
			data.setAuthenticated(false);
			data.addError("directLinkAuthenticationFailed");
			return data;
		}
				
		data.setAuthenticated(true);
		if (modifiedLink.indexOf("[") != -1) {
			// Multiple views...			
			return parseMultipleViews(locale, authUser, modifiedLink, data);
		}

		return parseSingleView(locale, authUser, link, data);		
	}	

	public boolean deleteElementTreeNodes(List<XObject> objects, XUser usr) {
		if (objects == null || objects.size() == 0) {
			return true;
		}
//		AuthUser user = (AuthUser) XObjectMatcher.getNativeObject(usr);
//		FolderService folderService = ServiceProvider.getFolderService(user);
//		ExplorerTreeNode root = null;
//		
//		for (XObject o: objects) {
//			AbstractExplorerTreeNode node = 
//				(AbstractExplorerTreeNode) XObjectMatcher.getNativeObject(o);
//			XObjectMatcher.remove(o);
//			if (node.getParent() == null) {
//				// Root has been deleted.
//				try {
//					folderService.delete(node);
//					folderService.save(null);
//				} catch (OperationFailedException e) {
//					e.printStackTrace();
//					return false;
//				}
//				return true;
//			} else {
//				if (root == null) {
//					root = node.getRoot();
//				}
//				try {
//					folderService.delete(node);
//				} catch (OperationFailedException e) {
//					e.printStackTrace();
//					return false;
//				}
//			}
//		}
//		try {
//			folderService.save(root);
//		} catch (OperationFailedException e) {
//			e.printStackTrace();
//		}
//		return true;
		return false;
	}

	public boolean deleteReportTreeNodes(List<XObject> objects, XUser usr) {
		if (objects == null || objects.size() == 0) {
			return true;
		}
		
//		for (XObject o: objects) {
//			if (o instanceof XTemplate) {
//				WSSTemplate temp = (WSSTemplate) XObjectMatcher.getNativeObject(o);
//				if (temp == null) {
//					continue;
//				}
//				WSSApplication sys = temp.getConnection().getSystemApplication();
//				sys.select();
//				try {
//					sys.removeWorkbook(temp.getId());
//				} catch (Throwable t) {
//					t.printStackTrace();
//				}
//			}
//		}
//		return true;
		return false;
	}

	public boolean assignSubsetOrDimension(XDynamicReportFolder folder,
			XObject subdim, XUser usr) {
		if (subdim == null) {
			return true;
		}
//		AuthUser user = (AuthUser) XObjectMatcher.getNativeObject(usr);
//		FolderService folderService = ServiceProvider.getFolderService(user);
//		
//		DynamicFolder df = (DynamicFolder) XObjectMatcher.getNativeObject(folder);
//		if (df == null) {
//			return false;
//		}
//		
//		if (subdim instanceof XDimension) {
//			Dimension dim = (Dimension) XObjectMatcher.getNativeObject(subdim);
//			if (dim == null) {
//				return false;
//			}
//			Hierarchy hier = dim.getDefaultHierarchy();
//			df.setSourceSubset(null);
//			df.setSourceHierarchy(hier);
//		} else if (subdim instanceof XSubset) {
//			Object o = XObjectMatcher.getNativeObject(subdim);
//			if (o == null) {
//				return false;
//			}
//			if (o instanceof Subset) {
//				return false;
//			} else if (o instanceof Subset2) {
//				df.setSourceHierarchy(((Subset2) o).getDimHierarchy());
//				df.setSourceSubset((Subset2) o);
//			} else {
//				return false;
//			}
//		} else if (subdim instanceof XHierarchy) {
//			Hierarchy hier = (Hierarchy) XObjectMatcher.getNativeObject(subdim);
//			df.setSourceSubset(null);
//			df.setSourceHierarchy(hier);
//		}
//		try {
//			folderService.save(df.getRoot());
//			return true;
//		} catch (OperationFailedException e) {
//			e.printStackTrace();
//		}
//		return false;
		return false;
	}

	public XVariableDescriptor getVariables(XReport report, XUser usr) {
//		XVariableDescriptor desc = new XVariableDescriptor();
//		AuthUser user = (AuthUser) XObjectMatcher.getNativeObject(usr);
//		
//		FolderElement el = (FolderElement) XObjectMatcher.getNativeObject(report);		
//		Object so = ReportFolderChildLoader.getSourceObjectFromElement(usr, el);
//		if (so == null || !(so instanceof WSSWorkbook)) {
//			return desc;
//		}
//		WSSWorkbook wb = (WSSWorkbook) so;
//		wb.getApplication().select();
//		try {
//			wb.select();
//		} catch (WSSAPIException e) {
//			// ignore...
//		}
//		for (WSSWorksheet ws: wb.getWorksheetList()) {			
//			for (WSSVariable v: ws.getVariableList()) {
//				desc.addVariable(v.getName());
//			}
//		}
////		PaloObject [] vMapKeys = el.getVariableMappingKeys();
////		for (PaloObject o: vMapKeys) {
////			if (o instanceof Hierarchy) {
////				desc.addMapping(createXHierarchy((Hierarchy) o, usr, user), el.getVariableMapping((Hierarchy) o));
////			} else if (o instanceof Subset2) {
////				desc.addMapping(createXSubset((Subset2) o, usr, user), el.getVariableMapping((Subset2) o));
////			}
////		}
//		return desc;
		return null;
	}

	public XView createView(XCube parentCube, String name, XUser user) {
		// TODO Auto-generated method stub
		return null;
	}

	private final XWorkbook createWorkbook(XApplication application, String name) {
//		WSSApplication app = (WSSApplication) XObjectMatcher.getNativeObject(application);
//		if (app == null) {
//			return null;
//		}
//		app.select();
//		WSSWorkbook wb = app.addWorkbook(name);
//		XWorkbook xwb = new XWorkbook(wb.getId(), wb.getName(), application, null);
//		XObjectMatcher.put(xwb, wb);
//		return xwb;
		return null;
	}
	
	private final XWorkbook createWorkbookTemplate(XAccount account, String name) {
//		Account acc = (Account) XObjectMatcher.getNativeObject(account);
//		if (acc == null || !(acc instanceof WSSAccount)) {
//			return null;
//		}
//		WSSConnection con = ((WSSAccount) acc).login();
//		WSSApplication app = con.getSystemApplication();
//		XApplication xapp = null; // (XApplication) XObjectMatcher.find(app);
//		if (xapp == null) {
//			xapp = new XApplication(account.getUser(), account, account.getId(), false, true);
//			xapp.setName(app.getName());
//			//XObjectMatcher.put(xapp, app);
//		}
//		app.select();
//		WSSWorkbook wb = app.addWorkbook(name);
//		XWorkbook xwb = new XWorkbook(wb.getId(), wb.getName(), xapp, null);
//		XObjectMatcher.put(xwb, wb);
//		return xwb;
		return null;
	}
	
	public XWorkbook createWorkbook(XObject parent, String name) {
		if (parent instanceof XApplication) {
			return createWorkbook((XApplication) parent, name); 
		} else if (parent instanceof XAccount) {
			return createWorkbookTemplate((XAccount) parent, name);
		}
		return null;
	}
	
	public boolean applyMapping(XReport input, XObject [] keys, String [] values, XUser usr) {
//		AuthUser user = (AuthUser) XObjectMatcher.getNativeObject(usr);
//		FolderService folderService = ServiceProvider.getFolderService(user);
//		FolderElement el = (FolderElement) XObjectMatcher.getNativeObject(input);
//		if (el == null) {
//			return false;
//		}
//		for (int i = 0; i < keys.length; i++) {
//			Object o = XObjectMatcher.getNativeObject(keys[i]);
//			if (o instanceof Hierarchy) {
//				el.setVariableMapping((Hierarchy) o, values[i]);
//			} else if (o instanceof Subset2) {
//				el.setVariableMapping((Subset2) o, values[i]);
//			} else {
//			}			
//		}
//		try {
//			folderService.save(el.getRoot());
//		} catch (OperationFailedException e) {
//			return false;
//		}
//		return true;
		return false;
	}
	
//	private static final XElement createXElement(Element e, XUser xUser, AuthUser user) {
//		if (e == null) {
//			return null;
//		}
//		
//		XElement xel = (XElement) XObjectMatcher.find(e);
//		if (xel != null) {
//			return xel;
//		}
//		
//		Connection con = 
//			e.getHierarchy().getDimension().getDatabase().getConnection();
//		XAccount xacc = createXAccount(con, xUser, user);
//		if (xacc == null) {
//			return null;
//		}
//		
//		Database db = e.getHierarchy().getDimension().getDatabase();
//		XDatabase xdb = createXDatabase(db, xacc, xUser);
//		
//		Dimension dim = e.getHierarchy().getDimension();
//		XDimension xdim = createXDimension(dim, xdb, xUser);
//		
//		Hierarchy hier = e.getHierarchy();
//		XHierarchy xhier = createXHierarchy(hier, xdim, xUser);
//		
//		xel = new XElement(e.getId(), e.getName(), xhier, xUser);
//		xel.setElementType(XElementType.fromString(e.getTypeAsString()));
//		XObjectMatcher.put(xel, e);
//		return xel;
//	}
//	
//	private static final XDatabase createXDatabase(Database db, XAccount xacc, XUser xUser) {
//		XDatabase xdb = (XDatabase) XObjectMatcher.find(db);
//		if (xdb == null) {
//			xdb = new XDatabase(db.getId(), db.getName(), xacc, 
//					db.getDimensionCount() > 0, xUser);
//			XObjectMatcher.put(xdb, db);
//		}		
//		return xdb;
//	}
//	
//	private static final XDimension createXDimension(Dimension dim, XDatabase xdb, XUser xUser) {
//		XDimension xdim = (XDimension) XObjectMatcher.find(dim);
//		if (xdim == null) {
//			xdim = new XDimension(dim.getId(), dim.getName(), xdb, 
//					dim.getHierarchyCount() > 0, xUser, null);
//			XHierarchy defHierarchy = createXHierarchy(dim.getDefaultHierarchy(),
//					xdim, xUser);
//			xdim.setDefaultHierarchy(defHierarchy);
//			XObjectMatcher.put(xdim, dim);
//		}		
//		return xdim;
//	}
//	
//	private static final XHierarchy createXHierarchy(Hierarchy hier, XDimension xdim, XUser xUser) {
//		XHierarchy xhier = (XHierarchy) XObjectMatcher.find(hier);
//		if (xhier == null) {
//			xhier = new XHierarchy(hier.getId(), hier.getName(), xdim, xUser);
//			XObjectMatcher.put(xhier, hier);
//		}
//		return xhier;
//	}
//	
//	private static final XSubset createXSubset(Subset2 sub, XHierarchy hier, XUser xUser) {
//		XSubset xsub = (XSubset) XObjectMatcher.find(sub);
//		if (sub == null) {
//			xsub = new XSubset(sub.getId(), sub.getName(), false, hier, xUser);
//			XObjectMatcher.put(xsub, sub);
//		}
//		return xsub;
//	}
//	
//	public static final XHierarchy createXHierarchy(Hierarchy hier, XUser xUser, AuthUser user) {
//		if (hier == null) {
//			return null;
//		}
//		
//		XHierarchy xhier = (XHierarchy) XObjectMatcher.find(hier);
//		if (xhier != null) {
//			return xhier;
//		}
//		
//		Connection con = 
//			hier.getDimension().getDatabase().getConnection();
//		XAccount xacc = createXAccount(con, xUser, user);
//		if (xacc == null) {
//			return null;
//		}
//		
//		Database db = hier.getDimension().getDatabase();
//		XDatabase xdb = createXDatabase(db, xacc, xUser);
//		
//		Dimension dim = hier.getDimension();
//		XDimension xdim = createXDimension(dim, xdb, xUser);
//		
//		xhier = createXHierarchy(hier, xdim, xUser);
//		return xhier;
//	}
//	
//	public static final XSubset createXSubset(Subset2 subset, XUser xUser, AuthUser user) {
//		if (subset == null) {
//			return null;
//		}
//		
//		XSubset xsub = (XSubset) XObjectMatcher.find(subset);
//		if (xsub != null) {
//			return xsub;
//		}
//		
//		Connection con = 
//			subset.getDimHierarchy().getDimension().getDatabase().getConnection();
//		XAccount xacc = createXAccount(con, xUser, user);
//		if (xacc == null) {
//			return null;
//		}
//		
//		Database db = subset.getDimHierarchy().getDimension().getDatabase();
//		XDatabase xdb = createXDatabase(db, xacc, xUser);
//		
//		Dimension dim = subset.getDimHierarchy().getDimension();
//		XDimension xdim = createXDimension(dim, xdb, xUser);
//		
//		Hierarchy hier = subset.getDimHierarchy();
//		XHierarchy xhier = createXHierarchy(hier, xdim, xUser);
//		
//		xsub = createXSubset(subset, xhier, xUser);
//		return xsub;
//	}
//	
//	private static final XAccount createXAccount(Connection con, XUser xUser, AuthUser user) {
//		XAccount xacc = null;
//		for (Account a: user.getAccounts()) {
//			if (a instanceof PaloAccount) {
//				PaloConnection pc = a.getConnection();
//				if (pc.getHost().equals(con.getServer())) {
//					if (pc.getService().equals(con.getService())) {
//						xacc = (XAccount) XObjectMatcher.find(a);
//						if (xacc == null) {
//							xacc = new XAccount(a.getId(), a.getLoginName());
//							xacc.setUser(xUser);
//							XConnection xCon = new XConnection(a.getConnection().getId(),
//									a.getConnection().getName(), a.getConnection().getType());
//							xCon.setService(con.getService());
//							xCon.setHost(a.getConnection().getHost());
//							xacc.setPassword(a.getPassword());							
//							xacc.setHasChildren(con.getDatabaseCount() > 0);
//							String name = a.getConnection().getName();
//							if (name == null || name.trim().length() == 0) {
//								name = a.getConnection().getHost() + ":" + 
//										a.getConnection().getService();
//							}
//							xCon.setName(name);
//							xacc.setConnection(xCon);
//							XObjectMatcher.put(xacc, a);
//						}
//						break;
//					}
//				}
//			}
//		}	
//		return xacc;
//	}
//	
//	public static final List <XElement> retrieveElements(FolderElement fe, 
//			XUser xUser, AuthUser user) {
//		Object oo = fe.getParameterValue(CubeView.PARAMETER_ELEMENT);
//		List <XElement> elements = new ArrayList<XElement>();
//		if (oo != null && oo instanceof Object []) {				
//			for (Object o: (Object []) oo) {
//				if (o instanceof Element) {
//					Element e = (Element) o;
//					XElement xel = createXElement(e, xUser, user);
//					elements.add(xel);
//				}
//			}
//		} else if (oo != null && oo instanceof Element) {
//			Element e = (Element) oo;
//			elements.add(createXElement(e, xUser, user));
//		}					
//		return elements;
//	}	
}
