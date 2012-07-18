/*
*
* @file WPaloCubeViewServiceImpl.java
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
* @version $Id: WPaloCubeViewServiceImpl.java,v 1.71 2010/04/15 09:55:22 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.server.services.cubeview;

import java.io.File;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.palo.api.Attribute;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.ElementNodeVisitor;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.subsets.Subset2;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.Group;
import org.palo.viewapi.LocalFilter;
import org.palo.viewapi.Property;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.internal.ConnectionPoolManager;
import org.palo.viewapi.internal.ExplorerTreeNode;
import org.palo.viewapi.internal.FolderElement;
import org.palo.viewapi.internal.FolderModel;
import org.palo.viewapi.internal.IRoleManagement;
import org.palo.viewapi.internal.LocalFilterImpl;
import org.palo.viewapi.internal.RoleImpl;
import org.palo.viewapi.internal.VirtualElementImpl;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;
import org.palo.viewapi.internal.io.CubeViewReader;
import org.palo.viewapi.services.FolderService;
import org.palo.viewapi.services.ServiceProvider;
import org.palo.viewapi.services.ViewService;

import com.tensegrity.palo.gwt.core.client.exceptions.PaloGwtCoreException;
import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAlias;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxis;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisItem;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XCellCollection;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XDelta;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XLoadInfo;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XPrintConfiguration;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XPrintResult;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;
import com.tensegrity.palo.gwt.core.client.models.folders.XFolderElement;
import com.tensegrity.palo.gwt.core.client.models.palo.XCell;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementType;
import com.tensegrity.palo.gwt.core.client.models.subsets.XSubset;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.palo.gwt.core.server.services.cubeview.CubeViewController;
import com.tensegrity.palo.gwt.core.server.services.cubeview.CubeViewService;
import com.tensegrity.palo.gwt.core.server.services.cubeview.ViewOpenWarnings;
import com.tensegrity.wpalo.client.exceptions.DbOperationFailedException;
import com.tensegrity.wpalo.client.services.cubeview.WPaloCubeViewService;
import com.tensegrity.wpalo.server.WPaloPropertyServiceImpl;

/**
 * <code>CubeViewServiceImpl</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: WPaloCubeViewServiceImpl.java,v 1.71 2010/04/15 09:55:22 PhilippBouillon Exp $
 **/
public class WPaloCubeViewServiceImpl extends CubeViewService implements
		WPaloCubeViewService {

	/** generated */
	private static final long serialVersionUID = 8204527425758672237L;
	private int pathCounter = 0;
	private final WPaloPropertyServiceImpl properties;
	
	public WPaloCubeViewServiceImpl() {
		properties = new WPaloPropertyServiceImpl();
	}
	
	public XLoadInfo willChangeSelectedElement(String sessionId, XViewModel xViewModel,
			XAxisHierarchy xAxishHierarchy) throws SessionExpiredException {
		return super.willChangeSelectedElement(sessionId, xViewModel, xAxishHierarchy);
	}

	public synchronized XLoadInfo willOpen(String sessionId, XView xView) throws SessionExpiredException, PaloGwtCoreException {
		try {
			return willOpenView(sessionId, xView);
		} catch (Throwable t) {
			throw new PaloGwtCoreException(t.getMessage(), t);
		}
	}
	
	public synchronized XViewModel proceedOpen(String sessionId, XView xView) throws SessionExpiredException, PaloGwtCoreException {
		try {
			ViewOpenWarnings.getInstance().clearWarnings();		
			XViewModel model = proceedOpenView(sessionId, getLoggedInUser(sessionId), xView.getId());
			model.setWarnings(ViewOpenWarnings.getInstance().getWarnings());
			return model;
		} catch (PaloAPIException e) {
			throw new PaloGwtCoreException(e.getMessage(), e);
		}
	}
	
	public XLoadInfo willExpand(String sessionId, XAxisItem item, String viewId, String axisId)
			throws SessionExpiredException {
		try {
			return super.willExpand(sessionId, item, viewId, axisId);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new SessionExpiredException("Session expired!");
		}
	}

	public XDelta proceedExpand(String sessionId, XAxisItem item, String viewId, String axisId)
			throws SessionExpiredException {
		try {
			return super.proceedExpand(sessionId, item, viewId, axisId);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new SessionExpiredException("Session expired!");
		}
	}

	public XViewModel proceedSwapAxes(String sessionId, XViewModel view)
		throws SessionExpiredException {
		try {
			XViewModel model = super.proceedSwapAxes(sessionId, view);
			removeLocalFilter(model);
			model.setNeedsRestore(true);
			return model;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new SessionExpiredException("Session expired!");
		}
	}
	
	public void cancelExpand(String sessionId, XAxisItem item, String viewId, String axisId)
			throws SessionExpiredException {
		try {
			super.cancelExpand(sessionId, item, viewId, axisId);
		} catch (Throwable t) {
			throw new SessionExpiredException("Session expired!");
		}
	}
	
	public XLoadInfo willCollapse(String sessionId, XAxisItem item,
			String viewId, String axisId) throws SessionExpiredException {
		try {
			return super.willCollapse(sessionId, item, viewId, axisId);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new SessionExpiredException("Session expired!");
		}
	}

	public void proceedCollapse(String sessionId, XAxisItem item,
			String viewId, String axisId) throws SessionExpiredException {
		try {
			super.proceedCollapse(sessionId, item, viewId, axisId);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new SessionExpiredException("Session expired!");
		}
	}

	public void cancelCollapse(String sessionId, XAxisItem item, String viewId,
			String axisId) throws SessionExpiredException {
		try {
			super.cancelCollapse(sessionId, item, viewId, axisId);
		} catch (Throwable t) {
			throw new SessionExpiredException("Session expired!");
		}
	}

	public XLoadInfo willSetExpandState(String sessionId, XAxisItem[] expanded, XAxisItem[] collapsed,
			int expandDepth, String viewId, String axisId)
			throws SessionExpiredException {
		try {
			return super.willSetExpandState(sessionId, expanded, collapsed, expandDepth,
					viewId, axisId);
		} catch (Throwable t) {
			throw new SessionExpiredException("Session expired!");
		}
	}
	public XDelta[] proceedSetExpandState(String sessionId, String viewId)
			throws SessionExpiredException {
		try {
			return super.proceedSetExpandState(sessionId, viewId);
		} catch (Throwable t) {
			throw new SessionExpiredException("Session expired!");
		}
	}
	public void cancelSetExpandState(String sessionId, String viewId)
			throws SessionExpiredException {
		try {
			super.cancelSetExpandState(sessionId, viewId);
		} catch (Throwable t) {
			throw new SessionExpiredException("Session expired!");
		}
	}
	
	public void collapse(String sessionId, XAxisItem item, String viewId, String axisId)
			throws SessionExpiredException {
		try {
			super.collapse(sessionId, item, axisId, viewId);
		} catch (Throwable t) {
			throw new SessionExpiredException("Session expired!");			
		}
	}

	public XViewModel saveView(String sessionId, XViewModel xViewModel) throws DbOperationFailedException, SessionExpiredException {
		try {
			return save(sessionId, xViewModel);			
		} catch (OperationFailedException e) {
			if (e.getCause() != null && e.getCause().getMessage() != null && e.getCause().getMessage().toLowerCase().indexOf("not enough rights") != -1) {
				return null;
			}
			UserSession userSession = getUserSession(sessionId);
			throw new DbOperationFailedException(userSession.translate("couldNotSave", xViewModel.getName(), 
					e.getLocalizedMessage()),
					e);
		} catch (Throwable t) {
			if (t.getMessage() != null && t.getMessage().toLowerCase().indexOf("not enough rights") != -1) {
				return null;
			}
			UserSession userSession = getUserSession(sessionId);
			throw new DbOperationFailedException(userSession.translate("couldNotSave", xViewModel.getName(), 
					t.getLocalizedMessage()),
					t);
		}				
	}

	public XView saveViewAs(String sessionId, String name, XViewModel xViewModel)
			throws DbOperationFailedException, SessionExpiredException {
		try {
			
			return saveAs(sessionId, name, xViewModel);
		} catch (OperationFailedException e) {
			UserSession userSession = getUserSession(sessionId);
			throw new DbOperationFailedException(userSession.translate("couldNotSave", xViewModel.getName(), 
					e.getLocalizedMessage()),
					e);
		} catch (Throwable t) {
			t.printStackTrace();
			if (t.getMessage() != null && t.getMessage().toLowerCase().indexOf("not enough rights") != -1) {
				return null;
			}
			UserSession userSession = getUserSession(sessionId);
			throw new DbOperationFailedException(userSession.translate("couldNotSave", xViewModel.getName(), 
					t.getLocalizedMessage()),
					t);
		}
	}
	
	public XElement updateAxisHierarchy(String sessionId, XAxisHierarchy xAxishierarchy)
			throws SessionExpiredException {
		return update(sessionId, xAxishierarchy);
	}

	public void updateView(String sessionId, XViewModel xViewModel)
			throws SessionExpiredException {
		update(sessionId, xViewModel);
	}

	public XCellCollection writeCell(String sessionId, XCell cell, XViewModel xViewModel)
			throws SessionExpiredException {
		NumberFormat format = getNumberFormat(sessionId);
		return super.writeCell(sessionId, cell, xViewModel, format);
	}

	public void deleteView(String sessionId, XView view) throws DbOperationFailedException, SessionExpiredException {
		if (view == null)
			return;
		try {
			delete(sessionId, view);
		} catch (OperationFailedException e) {
			UserSession userSession = getUserSession(sessionId);
			throw new DbOperationFailedException(userSession.translate("couldNotDelete", view.getName(), 
					e.getLocalizedMessage()),
					e);
		}
	}

	public void closeView(String sessionId, XViewModel xViewModel) throws SessionExpiredException {
		remove(sessionId, xViewModel);
	}
	
	public XView[] importViews(String sessionId, XView[] views) throws SessionExpiredException {
		WPaloCubeViewConverter converter = new WPaloCubeViewConverter(
				getLoggedInUser(sessionId));
		List<XView> xViews = new ArrayList<XView>();
		for (XView xView : views) {
			try {
				View newView = converter.convertLegacyView(xView, sessionId);
				if (newView == null)
					newView = converter.createDefaultView(xView, sessionId);
				xViews.add(converter.createX(newView));
			} catch (OperationFailedException e) {
				/* TODO ignore failed view? */
			}
		}
		return xViews.toArray(new XView[0]);
	}

	public void renameView(String sessionId, XView xView, String newName)
			throws DbOperationFailedException, SessionExpiredException {
		try {
			rename(sessionId, xView, newName);
			xView.setName(newName);
		} catch (OperationFailedException e) {
			UserSession userSession = getUserSession(sessionId);
			throw new DbOperationFailedException(userSession.translate("couldNotRename", xView.getName(), 
					e.getLocalizedMessage()),
					e);
		}
	}
	
	public void runAsync(String sessionId, int wait) throws SessionExpiredException {
		if (wait != 0) {
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
			}
		}
	}
	
	private final boolean checkPermission(AuthUser forUser, View view, int right)  {		
		switch (right) {
			case  0: return forUser.hasPermissionIgnoreOwner(Right.NONE, view);
			case  1: return forUser.hasPermissionIgnoreOwner(Right.READ, view);
			case  2: return forUser.hasPermissionIgnoreOwner(Right.WRITE, view);
			case  4: return forUser.hasPermissionIgnoreOwner(Right.DELETE, view);
			case  8: return forUser.hasPermissionIgnoreOwner(Right.CREATE, view);
			case 16: return forUser.hasPermissionIgnoreOwner(Right.GRANT, view);
		}
		return false;		
	}
	
	public boolean checkPermission(String sessionId, String viewId, int right) throws SessionExpiredException {		
		UserSession userSession = getUserSession(sessionId);
		View view = CubeViewController.getViewById(sessionId, viewId);		
		return checkPermission(userSession.getUser(), view, right);
	}
		
	public boolean checkPermission(String sessionId, int right) throws SessionExpiredException {
		AuthUser user = getLoggedInUser(sessionId);
		Right rr = null;
		
		switch (right) {
			case  0: rr = Right.NONE; break;
			case  1: rr = Right.READ; break;
			case  2: rr = Right.WRITE; break;
			case  4: rr = Right.DELETE; break;
			case  8: rr = Right.CREATE; break;
			case 16: rr = Right.GRANT; break;
		}
		
		if (rr != null) {
			for (Role r : user.getRoles()) {
				if (r.hasPermission(rr)) {
					return true;
				}
			}
			for (Group g: user.getGroups()) {
				for (Role r: g.getRoles()) {
					if (r.hasPermission(rr)) {
						return true;
					}					
				}
			}
		}
		return false;
	}	
	
	private final synchronized AxisHierarchy toNative(String sessionId, String axisHierarchyId, String viewId, String axisId) throws SessionExpiredException, PaloGwtCoreException {
//		UserSession userSession = getUserSession(sessionId);
		View view = CubeViewController.getViewById(sessionId, viewId);
		if (view == null) {
			return null;
		}
		CubeView cv = view.getCubeView();
		if (cv == null) {
			try {
				cv = view.createCubeView(getLoggedInUser(sessionId), sessionId);
			} catch (PaloIOException e) {
				e.printStackTrace();
			} finally {
				ConnectionPoolManager.getInstance().disconnect(view.getAccount(), sessionId, "WPaloCubeViewServiceImpl.toNative");				
			}
		}
		Axis axis = cv.getAxis(axisId);
		if (axis == null) {
			return null;
		}
		AxisHierarchy axisHierarchy = axis.getAxisHierarchy(axisHierarchyId);
		if (axisHierarchy == null) {
			for (Axis ax: cv.getAxes()) {
				AxisHierarchy ah = ax.getAxisHierarchy(axisHierarchyId);
				if (ah != null) {
					return ah;
				}
			}
		}
		return axisHierarchy;		
	}
	
	private final synchronized Axis toNative(String sessionId, XAxis xAxis) throws SessionExpiredException, PaloGwtCoreException {
		UserSession userSession = getUserSession(sessionId);
		View view = CubeViewController.getViewById(sessionId, xAxis.getViewId());
		if (view == null) {
			return null;
		}
		CubeView cv = view.getCubeView();
		if (cv == null) {
			try {
				cv = view.createCubeView(getLoggedInUser(sessionId), sessionId);
			} catch (PaloIOException e) {
				throw new PaloGwtCoreException(e.getMessage(), e);
			} finally {
				ConnectionPoolManager.getInstance().disconnect(view.getAccount(), sessionId, "WPaloCubeViewServiceImpl.toNative2");
			}
		}
		Axis axis = cv.getAxis(xAxis.getId());
		if (axis == null) {
			return null;
		}		
		return axis;
	}
	
	private final void display(ElementNode [] nodes, int indent) {
		if (nodes == null) {
			return;
		}
		for (ElementNode node: nodes) {
			for (int i = 0; i < indent; i++) {
				System.err.print("  ");
			}
			System.err.println(node.getName());
			display(node.getChildren(), indent + 1);
		}
	}
		
	private final List <Integer> checkPath(String path, ElementNode [] structure) {
		String [] segments = path.split(":");
		int index = -1;
		ArrayList <Integer> result = new ArrayList<Integer>();
		for (String segment: segments) {
			int rep = 0;
			if ((index = segment.indexOf("(")) != -1) {
				rep = Integer.parseInt(segment.substring(index + 1, segment.indexOf(")")));
				segment = segment.substring(0, index);
			}
			boolean found = false;
			int counter = 0;
			for (ElementNode nd: structure) {				
				if (nd.getElement().getId().equals(segment)) {
					if (rep == 0) {
						// Match is ok, set kids to new structure and continue.
						found = true;
						result.add(counter);
						structure = nd.getChildren();
						break;
					} else {
						rep--;
					}
				}
				counter++;
			}
			if (!found) {				
				// Match is not ok.
				return null;
			}
		}
		return result;
	}
		
	private final boolean isDeeper(List <Integer> deep, List <Integer> last) {
		int lastSize = last.size();
		for (int i = 0; i < deep.size(); i++) {
			if (i >= lastSize) {
				return true; 
			}
			int depthDeep = deep.get(i);
			int depthLast = last.get(i);
			if (depthDeep > depthLast) {
				return true;
			}
			if (depthDeep < depthLast) {
				return false;
			}
		}
		return true;
	}

	private final boolean findNode(ElementNode nd, String segment) {		
		String nodePath = getPath(nd);
		String [] elems = nodePath.split(":");
		String lastSegment = elems[elems.length - 1];
		if (lastSegment.equals(segment)) {
			return true;
		}
		for (ElementNode k: nd.getChildren()) {
			boolean r = findNode(k, segment);
			if (r) {
				return true;
			}
		}
		return false;
	}
	
	private final String modifyPath(String path, ElementNode [] rootNodes) {
		// Eliminates all elements from the path that do not belong
		// into the filter
		String [] temp = path.split(":");
		
		boolean found;
		for (int counter = 0; counter < temp.length; counter++) {
			found = false;
			for (ElementNode nd: rootNodes) {
				found = findNode(nd, temp[counter]);
				if (found) {
					break;
				}
			}
			if (!found) {
				temp[counter] = "*";
			}
		}

		StringBuffer b = new StringBuffer();
		for (String t: temp) {
			if (t.equals("*")) {
				continue;
			}
			b.append(t);
			b.append(":");
		}
		return b.toString();
	}
	
	private final boolean tryToMatch(ElementNode [] rootNodes, ElementNode [] filter, ElementNode [] structure, Hierarchy hier, String [] allPaths, boolean showLeft) {
		if (showLeft) {
			return true;
		}
		List <Integer> lastFound = null;	
		boolean result = true;
		
		HashSet <String> rootPaths = new HashSet<String>();
		for (ElementNode node: filter) {
			// Step 1: Check if the element exists in the hierarchy.
			Element el = hier.getElementById(node.getElement().getId());
			if (el != null) {
				// Step 2: Check if the path matches the element in the structure.
				String path;
				String localPath = getPath(node);
				if (allPaths == null || pathCounter >= allPaths.length) {
					path = getPath(node);
				} else {
					path = allPaths[pathCounter++];
				}
				if (node.getParent() == null) {
					if (rootPaths.contains(path)) {
						return false;
					}
					rootPaths.add(path);
				}				
				int l1 = path.split(":").length;
				int l2 = localPath.split(":").length;
				if (l1 < l2) {
					return false;
				}
				if (l2 < l1) {
					String tempPath = modifyPath(path, rootNodes);
					if (!tempPath.equals(localPath)) {
						return false;
					}
				}
				List <Integer> match = checkPath(path, structure);				
				if (match == null) {
					// No match, so this local filter cannot
					// be displayed on the left hand side. => Finished.
					return false; 
				}
				
				// Step 3: Check if the order of the elements is correct:
				if (lastFound != null) {
					if (!isDeeper(match, lastFound)) {
						// Order is broken, so this local filter cannot
						// be displayed on the left hand side. => Finished.
						return false; 						
					}
				}
				lastFound = match;
			}
			if (node.hasChildren()) {
				result = tryToMatch(rootNodes, node.getChildren(), structure, hier, allPaths, showLeft);				
			}
			if (!result) {
				return false;
			}
		}
		return result;
	}
		
	private static final void updateLocalFilter(AxisHierarchy axisHierarchy, XElementNode[] visibleElements) {
		if(visibleElements == null)
			axisHierarchy.setLocalFilter(null);
		else {
			Hierarchy hierarchy = axisHierarchy.getHierarchy();			
			LocalFilter filter = new LocalFilterImpl();
			for (XElementNode xElement : visibleElements) {
				ElementNode elementNode = createElementNode(xElement, hierarchy);
				filter.addVisibleElement(elementNode);
			}
			axisHierarchy.setLocalFilter(filter);
		}
	}
	private static final void updateLocalFilter(AxisHierarchy axisHierarchy, ElementNode[] visibleElements) {
		if(visibleElements == null)
			axisHierarchy.setLocalFilter(null);
		else {
			LocalFilter filter = new LocalFilterImpl();
			for (ElementNode elementNode: visibleElements) {
				filter.addVisibleElement(elementNode);
			}
			axisHierarchy.setLocalFilter(filter);
		}
	}	
	private static final ElementNode createElementNode(XElementNode xElNode,
			Hierarchy hierarchy) {
		Element element = null;
		XElement xElement = xElNode.getElement();
		if(xElement.getElementType().equals(XElementType.VIRTUAL)) {
			element = new VirtualElementImpl(xElement.getName(), hierarchy);
		}else
			element = hierarchy.getElementById(xElNode.getElement().getId());
		ElementNode node = new ElementNode(element);
		addChildren(node, xElNode, hierarchy);
		return node;
	}
	private static final void addChildren(ElementNode parent, XElementNode xParent, Hierarchy hierarchy) {
		for(XElementNode xElNode : xParent.getChildren()) {
			ElementNode node = createElementNode(xElNode, hierarchy);
			parent.forceAddChild(node);
		}
	}
	
	public boolean checkLocalFilter(String sessionId, String axisHierarchyId, String viewId, String axisId, String filterPaths, XElementNode [] visibleElements, boolean displayLeft) throws SessionExpiredException, PaloGwtCoreException {
		AxisHierarchy axisHierarchy = toNative(sessionId, axisHierarchyId, viewId, axisId);
		if (axisHierarchy == null) {
			return false;
		}
		ElementNode [] initialLocalFilter = axisHierarchy.getLocalFilter() == null ? null :
			axisHierarchy.getLocalFilter().getVisibleElements();
		if (visibleElements != null && visibleElements.length > 0) {
//			String path = xAxisHierarchy.getProperty("filterPaths");			
			updateLocalFilter(axisHierarchy, visibleElements);
			if (filterPaths != null) {
				axisHierarchy.addProperty(new Property<String>("filterPaths", filterPaths));
			} else {
				axisHierarchy.removeProperty(new Property<String>("filterPaths", ""));
			}
		}
		if (axisHierarchy.getLocalFilter() == null) {
			updateLocalFilter(axisHierarchy, initialLocalFilter);
			return false;
		}
		ElementNode [] nodes = axisHierarchy.getLocalFilter().getVisibleElements();
		String [] allPaths = null;
		if (axisHierarchy.getProperty("filterPaths") != null) {
			String filterPath = (String) axisHierarchy.getProperty("filterPaths").getValue();
			if (filterPath != null) {
				allPaths = filterPath.split(",");
			}
		}
		if (nodes == null || nodes.length == 0) {
			updateLocalFilter(axisHierarchy, initialLocalFilter);
			return false;
		}
		pathCounter = 0;
		boolean result;
		if (axisHierarchy.getSubset() == null) {
			result = tryToMatch(nodes, nodes, axisHierarchy.getHierarchy().getElementsTree(), axisHierarchy.getHierarchy(), allPaths, displayLeft);
		} else {
			result = tryToMatch(nodes, nodes, axisHierarchy.getSubset().getRootNodes(), axisHierarchy.getHierarchy(), allPaths, displayLeft);
		}
		updateLocalFilter(axisHierarchy, initialLocalFilter);
		return result;
	}

	private static final void updateAlias(AxisHierarchy hierarchy, XAlias xAlias) {
		Property<?> aliasProperty = hierarchy.getProperty(AxisHierarchy.USE_ALIAS);		
		if(xAlias != null) {
			//get the alias attribute:
			Attribute alias = hierarchy.getHierarchy().getAttribute(xAlias.getId());
			if(alias != null) {
				aliasProperty = new Property<Attribute>(AxisHierarchy.USE_ALIAS, alias);
				hierarchy.addProperty(aliasProperty);
			}			
		} else if(aliasProperty != null)
			hierarchy.removeProperty(aliasProperty);
	}
	
	private void traverse(ElementNode [] nodes, HashMap <String, String> allElems) {
		if (nodes == null) {
			return;
		}
		for (ElementNode nd: nodes) {
			allElems.put(nd.getElement().getId(), nd.getName());
			traverse(nd.getChildren(), allElems);
		}
	}

	private final String getAliasForElement(String name, String elementName, String format) {
		String result = elementName;
		if (name != null) {
			if (format.equals("aliasFormat")) {
				result = name;
			} else if (format.equals("elementName")) {
				// result already is element name;
			} else if (format.equals("elementNameDashAlias")) {
				result = result + " - " + name;
			} else if (format.equals("aliasDashElementName")) {
				result = name + " - " + result;
			} else if (format.equals("elementNameParenAlias")) {
				result = result + " (" + name + ")";
			} else if (format.equals("aliasParenElementName")) {
				result = name + " (" + result + ")";
			} else if (format.equals("elementNameAlias")) {
				result = result + " " + name;
			} else if (format.equals("aliasElementName")) {
				result = name + " " + result;
			} 
		}
		return result;		
	}
	
	public XElementNode[] applyAlias(String sessionId, String axisHierarchyId, String viewId, String axisId,
			XAlias alias, XElementNode[] allNodes) throws SessionExpiredException, PaloGwtCoreException {
		AxisHierarchy axisHierarchy = toNative(sessionId, axisHierarchyId, viewId, axisId);
		if (axisHierarchy == null) {
			return allNodes;
		}
		Property<?> aliasProperty = axisHierarchy.getProperty(AxisHierarchy.USE_ALIAS);		
		updateAlias(axisHierarchy, alias);
		HashMap <String, String> allElems = new HashMap<String, String>();
		traverse(axisHierarchy.getRootNodes(), allElems);
		Property prop = axisHierarchy.getProperty("aliasFormat");
		String aliasFormat = null;
		if (prop != null && prop.getValue() != null) {
			aliasFormat = prop.getValue().toString();
		}
		for (XElementNode node: allNodes) {
			String newName = allElems.get(node.getElement().getId());
			if (newName != null) {
				if (aliasFormat == null) {
					node.setName(newName);
					node.getElement().setName(newName);
				} else {
					String name = getAliasForElement(newName, node.getElement().getName(), aliasFormat);
					node.setName(name);
					node.getElement().setName(name);					
				}
			}
		}
		if (aliasProperty != null) {
			axisHierarchy.addProperty(aliasProperty);
		} else {
			axisHierarchy.removeProperty(new Property(AxisHierarchy.USE_ALIAS, ""));
		}
		return allNodes;
	}

	public String [] getWarningThresholds(String sessionId, String[] browserPrefixes)
			throws SessionExpiredException {
		int newLoadedCellsWarningThreshold = 1000000;
		int displayCellsWarningThreshold = 1000000;
		int totalLoadedCellsWarningThreshold = 1000000;
		int totalTreeChildrenWarningThreshold = 200;
		boolean showWarnDialog = true;
		boolean showTreeWarnDialog = true;
		
				
		for (String prefix: browserPrefixes) {
			newLoadedCellsWarningThreshold = properties.getIntProperty(
					prefix + "newLoadedCellsWarningThreshold", newLoadedCellsWarningThreshold);
			displayCellsWarningThreshold = properties.getIntProperty(
					prefix + "displayCellsWarningThreshold", displayCellsWarningThreshold);
			totalLoadedCellsWarningThreshold = properties.getIntProperty(
					prefix + "totalLoadedCellsWarningThreshold", totalLoadedCellsWarningThreshold);
			showWarnDialog = properties.getBooleanProperty(
					prefix + "cellsWarningThresholdActive", showWarnDialog);
			showTreeWarnDialog = properties.getBooleanProperty(
					prefix + "treeWarningThresholdActive", showWarnDialog);
			totalTreeChildrenWarningThreshold = properties.getIntProperty(
					prefix + "totalTreeChildrenWarningThreshold", totalTreeChildrenWarningThreshold);
			
		}
		return new String [] {"" + showWarnDialog, "" + showTreeWarnDialog, "" + displayCellsWarningThreshold, "" + newLoadedCellsWarningThreshold, "" + totalLoadedCellsWarningThreshold, "" + totalTreeChildrenWarningThreshold};
	}

	public XViewModel cancelUpdateView(String sessionId, XViewModel xViewModel)
			throws SessionExpiredException {
		try {
			return cancelUpdateView(sessionId, getLoggedInUser(sessionId), xViewModel);
		} catch (PaloGwtCoreException e) {
			UserSession userSession = getUserSession(sessionId);
			throw new SessionExpiredException(userSession.translate("noAccount"), e);
		}
	}

	public synchronized XViewModel proceedReload(String sessionId, XViewModel xViewModel)
			throws SessionExpiredException {
		return proceedReload(sessionId, getLoggedInUser(sessionId), xViewModel);
	}

	private final void removeFilterNodes(XAxis axis) {
		for (XAxisHierarchy hier: axis.getAxisHierarchies()) {
			XElementNode [] oldVis = hier.getOldVisibleElements();
			if (oldVis != null) {
				hier.setOldVisibleElements(null);
			}
			XElementNode [] vis = hier.getVisibleElements();
			if (vis != null) {
				hier.setVisibleElements(null);
			}
		}		
	}
	
	private final void removeLocalFilter(XViewModel view) {
		removeFilterNodes(view.getSelectionAxis());
		removeFilterNodes(view.getColumnAxis());
		removeFilterNodes(view.getRepositoryAxis());
		removeFilterNodes(view.getRowAxis());
	}
		
	public synchronized XViewModel proceedUpdateView(String sessionId, XViewModel xViewModel)
			throws SessionExpiredException {
		try {
			XViewModel model = proceedUpdateView(sessionId, getLoggedInUser(sessionId), xViewModel);
			removeLocalFilter(model);
			model.setNeedsRestore(true);
			return model;
		} catch (PaloGwtCoreException e) {			
			UserSession userSession = getUserSession(sessionId);
			throw new SessionExpiredException(userSession.translate("noAccount"), e);
		}
	}
	
	public XViewModel proceedUpdateViewWithoutTable(String sessionId, XViewModel xViewModel)
			throws SessionExpiredException {
		try {
			return proceedUpdateViewWithoutTable(sessionId, getLoggedInUser(sessionId), xViewModel);
		} catch (PaloGwtCoreException e) {
			UserSession userSession = getUserSession(sessionId);
			throw new SessionExpiredException(userSession.translate("noAccount"), e);
		}
	}

	public boolean containsElement(String sessionId, String axisHierarchyId, String viewId, String axisId, XElement element, XSubset subset)
			throws SessionExpiredException, PaloGwtCoreException {
		if (subset == null || element == null) {
			return false;
		}
		AxisHierarchy axisHierarchy = toNative(sessionId, axisHierarchyId, viewId, axisId);
		Hierarchy h = axisHierarchy.getHierarchy();
		Subset2 s = h.getSubsetHandler().getSubset(subset.getId(), Subset2.TYPE_GLOBAL);
		if (s == null) {
			s = h.getSubsetHandler().getSubset(subset.getId(), Subset2.TYPE_LOCAL);
		}
		if (s == null) {
			return false;
		}
		for (Element e: s.getElements()) {
			if (e.getId().equals(element.getId())) {
				return true;
			}
		}
		return false;
	}

	public XPrintResult generatePDF(String sessionId, XViewModel xViewModel, XPrintConfiguration config) throws SessionExpiredException {
		try {
			return generatePdf(sessionId, getLoggedInUser(sessionId), xViewModel, config);
		} catch (PaloGwtCoreException e) {
			UserSession userSession = getUserSession(sessionId);
			throw new SessionExpiredException(userSession.translate("noAccount"), e);
		}		
	}

	public void deleteFile(String fileName) {
		new File(fileName).delete();
	}

	public Boolean[] getRoles(String sessionId, String viewId) throws SessionExpiredException {
		AuthUser user = getLoggedInUser(sessionId);		
		View view = ServiceProvider.getViewService(user).getView(viewId);
		Boolean [] result = new Boolean[2];
		result[0] = false;
		result[1] = false;
		if (view != null) {
			for (Role r: view.getRoles()) {
				if (r.getName().equalsIgnoreCase("VIEWER")) {
					result[0] = true;
				}
				if (r.getName().equalsIgnoreCase("EDITOR")) {
					result[1] = true;
				}
			}
		}
		return result;
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
			} else {
			}
			FolderModel.getInstance().save(user, root);
			folderService.save(root);
		} catch (OperationFailedException e) {
			e.printStackTrace();
		} catch (PaloIOException e) {
			e.printStackTrace();
		}
	}
	
	public XView setVisibility(String sessionId, XFolderElement element,
			boolean visible, boolean editable, String ownerId,
			String accountId, String dbId, String cubeId) throws SessionExpiredException {
		XView xView = (XView) element.getSourceObject();		
		AuthUser user = getLoggedInUser(sessionId);		
		ViewService vService = ServiceProvider.getViewService(user);
		View view = vService.getView(xView.getId());		
		
		if (!accountId.equals(xView.getAccountId())) {
			try {
				Account newAccount = (Account) MapperRegistry.getInstance().getAccountManagement().find(accountId);
				if (newAccount != null) {
					vService.setAccount(newAccount, view);
					xView.setAccountId(newAccount.getId());
				}
			} catch (SQLException e) {				
			}
		}
		if (!dbId.equals(xView.getDatabaseId())) {
			try {
				CubeViewReader.CHECK_RIGHTS = false;
				vService.setDatabase(dbId, view);
				xView.setDatabaseId(dbId);
			} finally {
				CubeViewReader.CHECK_RIGHTS = true;
			}
		}
		if (!cubeId.equals(xView.getCubeId())) {
			try {
				CubeViewReader.CHECK_RIGHTS = false;
				vService.setCube(cubeId, view);
				xView.setCubeId(cubeId);
				String def = view.getDefinition();
				int index = def.indexOf("cube=\"");
				if (index != -1) {
					int rIndex = def.indexOf("\"", index + 6);
					if (rIndex != -1) {
						def = def.substring(0, index + 6) +
							cubeId + def.substring(rIndex);
						vService.setDefinition(def, view);
						xView.setDefinition(def);
					}
				}
			} finally {
				CubeViewReader.CHECK_RIGHTS = true;
			}
		}			
		if (!ownerId.equals(xView.getOwnerId())) {
			try {
				User newOwner = (User) MapperRegistry.getInstance().getUserManagement().find(ownerId);
				if (newOwner != null) {
					try {
						CubeViewReader.CHECK_RIGHTS = false;
						vService.setOwner(newOwner, view);						
						xView.setOwnerId(newOwner.getId());
					} finally {
						CubeViewReader.CHECK_RIGHTS = true;
					}
				}
			} catch (SQLException e) {
			}			
		}
		
		try {
			ExplorerTreeNode root = FolderModel.getInstance().load(getLoggedInUser(sessionId));
			FolderElement folderElement = (FolderElement) find(root, element.getId());
			try {
				CubeViewReader.CHECK_RIGHTS = false;
				assignViewerAndEditorRole(sessionId, folderElement, view, visible, editable);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				CubeViewReader.CHECK_RIGHTS = true;
			}			
			folderElement.setSourceObject(view);
			saveRoot(sessionId, root);
			try {
				CubeViewReader.CHECK_RIGHTS = false;
				vService.save(view);
			} catch (OperationFailedException e) {
				e.printStackTrace();
			} finally {
				CubeViewReader.CHECK_RIGHTS = true;
			}			
		} catch (PaloIOException e) {
			e.printStackTrace();
		} finally {
			CubeViewReader.CHECK_RIGHTS = true;
		}
		List <String> roles = new ArrayList<String>();
		List <String> names = new ArrayList<String>();
		for (Role r: view.getRoles()) {
			roles.add(r.getId());
			names.add(r.getName());
		}
		xView.setRoleIds(roles);
		xView.setRoleNames(names);
		return xView;
	}

	public Boolean isOwner(String sessionId, String viewId)
			throws SessionExpiredException {
		AuthUser user = getLoggedInUser(sessionId);		
		View view = ServiceProvider.getViewService(user).getView(viewId);		
		if (user.getId().equals(view.getOwner().getId())) {
			if (checkPermission(sessionId, 2)) {
				return true;
			}
		}
		return false;
	}

	public HashMap<String, String> initializeRoles(String sessionId)
			throws SessionExpiredException {
		IRoleManagement rm = MapperRegistry.getInstance().getRoleManagement();
		HashMap <String, String> result = new HashMap<String, String>();
		try {
			for (Role r: rm.findAll()) {
				result.put(r.getName(), r.getId());		
			}
		} catch (Throwable t) {			
		}
		return result;
	}

	class Finder implements ElementNodeVisitor {
		StringBuffer path = null;
		String elementId;
		
		Finder(String elementId) {
			this.elementId = elementId;
		}
		
		public void visit(ElementNode elementNode, ElementNode parent) {
			if (elementNode.getElement().getId().equals(elementId) && path == null) {
				path = new StringBuffer();
				path.append(elementNode.getElement().getId());
				ElementNode par = elementNode.getParent();
				while (par != null) {
					path.insert(0, "/");
					path.insert(0, par.getElement().getId());
					par = par.getParent();
				}
			}
		}

		public String getPath() {
			return path == null ? "" : path.toString();
		}
	}
	
	private final void traverse(ElementNode [] nodes, ElementNodeVisitor v) {
		for (ElementNode n: nodes) {
			v.visit(n, n.getParent());			
		}
		for (ElementNode n: nodes) {
			traverse(n.getChildren(), v);
		}
	}
	
	public String getElementPath(String sessionId, String axisHierarchyId, String viewId, String axisId, String selectedElementId)
			throws SessionExpiredException, PaloGwtCoreException {	
		AxisHierarchy hier = toNative(sessionId, axisHierarchyId, viewId, axisId);		
		if (hier != null) {			
			Finder f = new Finder(selectedElementId);
			traverse(hier.getRootNodes(), f);
			return f.getPath();
		}
		return null;
	}	
	
	class Counter implements ElementNodeVisitor {
		int result = -1;
		String elementId;
		
		Counter(String elementId) {
			this.elementId = elementId;
		}
		
		public void visit(ElementNode elementNode, ElementNode parent) {
			if (elementNode.getElement().getId().equals(elementId) && result == -1) {
				result = 0;
				ElementNode par = elementNode.getParent();
				while (par != null) {					
					result += par.getChildren().length;
					par = par.getParent();
				}
			}
		}

		public int getResult() {
			return result == -1 ? 0 : result;
		}
	}
	
	public int getNumberOfChildren(String sessionId, String axisHierarchyId, String viewId, String axisId, String selectedElementId)
		throws SessionExpiredException, PaloGwtCoreException {
		AxisHierarchy hier = toNative(sessionId, axisHierarchyId, viewId, axisId);		
		if (hier != null) {
			Counter c = new Counter(selectedElementId);			
			ElementNode [] roots = hier.getRootNodes();
			traverse(roots, c);
			return c.getResult() + roots.length;
		}
		return 0;
	}

	public String [] hideItem(String sessionId, XAxisItem item, List <XAxisItem> roots, String viewId,
			String axisId, boolean column, boolean hideLevel) throws SessionExpiredException {
		try {
			return super.hideItem(sessionId, item, roots, viewId, axisId, hideLevel);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new SessionExpiredException("Session expired!");
		}
	}

	public String getSubobjectId() {
		// TODO Auto-generated method stub
		HttpSession httpSession = getThreadLocalRequest().getSession(true);  
		return (String)httpSession.getAttribute("saveSubObjectId");  
	}
	
	public String getSpagoBIUserMode() {
		// TODO Auto-generated method stub
		HttpSession httpSession = getThreadLocalRequest().getSession(true);  
		return (String)httpSession.getAttribute("isdeveloper");  
	}

}
