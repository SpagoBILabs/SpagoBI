/*
*
* @file WPaloCubeViewServiceProvider.java
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
* @version $Id: WPaloCubeViewServiceProvider.java,v 1.56 2010/04/12 11:13:36 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.services.cubeview;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAlias;
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
import com.tensegrity.palo.gwt.core.client.models.subsets.XSubset;


/**
 * <code>WPaloCubeViewServiceProvider</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: WPaloCubeViewServiceProvider.java,v 1.56 2010/04/12 11:13:36 PhilippBouillon Exp $
 **/
public class WPaloCubeViewServiceProvider implements WPaloCubeViewServiceAsync {
	
	private final static WPaloCubeViewServiceProvider instance = new WPaloCubeViewServiceProvider();

	public static WPaloCubeViewServiceProvider getInstance() {
		return instance;
	}

	private final WPaloCubeViewServiceAsync proxy;

//	private final boolean checkAxis(XAxis axis) {
//		if (axis == null) {
//			return false;
//		}
//		for (XAxisHierarchy hier: axis.getAxisHierarchies()) {
//			if (hier.getVisibleElements() != null && hier.getVisibleElements().length > 0) {
//				return true;
//			}
//			if (hier.getOldVisibleElements() != null && hier.getOldVisibleElements().length > 0) {
//				return true;
//			}
//		}
//		return false;
//	}
	
//	private final void checkFilter(XViewModel view, String function) {
//		if (view == null) {
//			return;
//		}
//		boolean result = checkAxis(view.getSelectionAxis());
//		result |= checkAxis(view.getColumnAxis());
//		result |= checkAxis(view.getRowAxis());
//		result |= checkAxis(view.getRepositoryAxis());
//		if (result) {
//			Window.alert("Warning: Serializing local filter in " + function + ". Speed can be improved here!");
//		}
//	}
	
	public WPaloCubeViewServiceAsync getProxy() {
		return proxy;
	}

	public WPaloCubeViewServiceProvider() {
		proxy = GWT.create(WPaloCubeViewService.class);
		((ServiceDefTarget) proxy).setServiceEntryPoint(GWT.getModuleBaseURL()
				+ "wpalo-cubeview-service");
	}

	public final void willExpand(String sessionId, XAxisItem item, String viewId, String axisId,
			AsyncCallback<XLoadInfo> cb) {
		proxy.willExpand(sessionId, item, viewId, axisId, cb);
	}
	public void proceedExpand(String sessionId, XAxisItem item, String viewId, String axisId,
			AsyncCallback<XDelta> cb) {
		proxy.proceedExpand(sessionId, item, viewId, axisId, cb);
	}

	public void cancelExpand(String sessionId, XAxisItem item, String viewId, String axisId,
			AsyncCallback<Void> cb) {
		proxy.cancelExpand(sessionId, item, viewId, axisId, cb);
	}

	public final void willCollapse(String sessionId, XAxisItem item, String viewId, String axisId,
			AsyncCallback<XLoadInfo> cb) {
		proxy.willCollapse(sessionId, item, viewId, axisId, cb);
	}
	public void proceedCollapse(String sessionId, XAxisItem item, String viewId, String axisId,
			AsyncCallback<Void> cb) {
		proxy.proceedCollapse(sessionId, item, viewId, axisId, cb);
	}

	public void cancelCollapse(String sessionId, XAxisItem item, String viewId, String axisId,
			AsyncCallback<Void> cb) {
		proxy.cancelCollapse(sessionId, item, viewId, axisId, cb);
	}

	public void willSetExpandState(String sessionId, XAxisItem[] expanded, XAxisItem[] collapsed, int expandDepth, String viewId,
			String axisId, AsyncCallback<XLoadInfo> cb) {
		proxy.willSetExpandState(sessionId, expanded, collapsed, expandDepth, viewId, axisId, cb);
	}

	public void proceedSetExpandState(String sessionId, String viewId, AsyncCallback<XDelta[]> cb) {
		proxy.proceedSetExpandState(sessionId, viewId, cb);
	}
	public void cancelSetExpandState(String sessionId, String viewId, AsyncCallback<Void> cb) {
		proxy.cancelSetExpandState(sessionId, viewId, cb);
	}

	
	public void collapse(String sessionId, XAxisItem item, String viewId, String axisId,
			AsyncCallback<Void> cb) {
		proxy.collapse(sessionId, item, viewId, axisId, cb);
	}


	public final void updateAxisHierarchy(String sessionId, XAxisHierarchy hierarchy,
			AsyncCallback<XElement> cb) {
		proxy.updateAxisHierarchy(sessionId, hierarchy, cb);
	}

	public final void saveView(String sessionId, XViewModel view, AsyncCallback<XViewModel> cb) {
//		checkFilter(view, "saveView");
		proxy.saveView(sessionId, view, cb);
	}
	public final void saveViewAs(String sessionId, String name, XViewModel view, AsyncCallback<XView> cb) {
//		checkFilter(view, "saveViewAs");
		proxy.saveViewAs(sessionId, name, view, cb);
	}

	public void renameView(String sessionId, XView xView, String newName, AsyncCallback<Void> cb) {
		proxy.renameView(sessionId, xView, newName, cb);
	}
	
	public void writeCell(String sessionId, XCell cell, XViewModel view,
			AsyncCallback<XCellCollection> cb) {
//		checkFilter(view, "writeCell");
		proxy.writeCell(sessionId, cell, view, cb);
	}
	
	public void deleteView(String sessionId, XView xView, AsyncCallback<Void> cb) {
		proxy.deleteView(sessionId, xView, cb);
	}

	public void closeView(String sessionId, XViewModel xViewModel, AsyncCallback<Void> cb) {
//		checkFilter(xViewModel, "closeView");
		proxy.closeView(sessionId, xViewModel, cb);
	}

	public void importViews(String sessionId, XView[] views, AsyncCallback<XView[]> cb) {
		proxy.importViews(sessionId, views, cb);
	}
	
	public void runAsync(String sessionId, int wait, AsyncCallback <Void> cb) {
		proxy.runAsync(sessionId, wait, cb);
	}
	
	public void checkPermission(String sessionId, String viewId, int right, AsyncCallback <Boolean> cb) {
		proxy.checkPermission(sessionId, viewId, right, cb);
	}
	
	public void checkPermission(String sessionId, int right, AsyncCallback <Boolean> cb) {
		proxy.checkPermission(sessionId, right, cb);
	}
	
	public void getWarningThresholds(String sessionId, String[] browserPrefixes,
			AsyncCallback<String []> cb) {
		proxy.getWarningThresholds(sessionId, browserPrefixes, cb);
	}

	public void proceedChangeSelectedElement(String sessionId, XViewModel viewModel,
			XAxisHierarchy axisHierarchy, XElement selectedElement, AsyncCallback<XViewModel> cb) {
//		checkFilter(viewModel, "proceedChangeSelectedElement");
		proxy.proceedChangeSelectedElement(sessionId, viewModel, axisHierarchy, selectedElement, cb);		
	}

	public void proceedOpen(String sessionId, XView view, AsyncCallback<XViewModel> cb) {
		proxy.proceedOpen(sessionId, view, cb);
	}

	public void proceedReload(String sessionId, XViewModel viewModel, AsyncCallback<XViewModel> cb) {
//		checkFilter(viewModel, "proceedReload");
		proxy.proceedReload(sessionId, viewModel, cb);
	}

	public void willChangeSelectedElement(String sessionId, XViewModel viewModel,
			XAxisHierarchy axishHierarchy, AsyncCallback<XLoadInfo> cb) {
//		checkFilter(viewModel, "willChangeSelectedElement");
		proxy.willChangeSelectedElement(sessionId, viewModel, axishHierarchy, cb);		
	}

	public void willOpen(String sessionId, XView view, AsyncCallback<XLoadInfo> cb) {
		proxy.willOpen(sessionId, view, cb);		
	}

	public void willReload(String sessionId, XViewModel viewModel, AsyncCallback<XLoadInfo> cb) {
//		checkFilter(viewModel, "willReload");
		proxy.willReload(sessionId, viewModel, cb);
	}

	public void willUpdateAxisHierarchy(String sessionId, XAxisHierarchy hierarchy,
			AsyncCallback<XLoadInfo> cb) {
		proxy.willUpdateAxisHierarchy(sessionId, hierarchy, cb);
	}

	public void cancelUpdateView(String sessionId, XViewModel viewModel, AsyncCallback<XViewModel> cb) {
//		checkFilter(viewModel, "cancelUpdateView");
		proxy.cancelUpdateView(sessionId, viewModel, cb);
	}

	public void proceedUpdateView(String sessionId, XViewModel viewModel,
			AsyncCallback<XViewModel> cb) {
//		checkFilter(viewModel, "proceedUpdateView");
		proxy.proceedUpdateView(sessionId, viewModel, cb);
	}

	public void proceedUpdateViewWithoutTable(String sessionId, XViewModel viewModel,
			AsyncCallback<XViewModel> cb) {
//		checkFilter(viewModel, "proceedUpdateViewWithoutTable");
		proxy.proceedUpdateViewWithoutTable(sessionId, viewModel, cb);
	}

	public void willUpdateView(String sessionId, XViewModel viewModel, AsyncCallback<XLoadInfo> cb) {
//		checkFilter(viewModel, "willUpdateView");
		proxy.willUpdateView(sessionId, viewModel, cb);
	}

	public void updateView(String sessionId, XViewModel viewModel, AsyncCallback<Void> cb) {
//		checkFilter(viewModel, "updateView");
		proxy.updateView(sessionId, viewModel, cb);
	}

	public void updateAndReloadView(String sessionId, XViewModel viewModel,
			AsyncCallback<XViewModel> cb) {
//		checkFilter(viewModel, "updateAndReloadView");
		proxy.updateAndReloadView(sessionId, viewModel, cb);
	}
	
	public void updateLoadInfo(String sessionId, XViewModel xViewModel, int cellsToDisplay,
			AsyncCallback<XLoadInfo> cb) {
//		checkFilter(xViewModel, "updateLoadInfo");
		proxy.updateLoadInfo(sessionId, xViewModel, cellsToDisplay, cb);
	}

	public void generatePDF(String sessionId, XViewModel xViewModel, XPrintConfiguration config, AsyncCallback<XPrintResult> cb) {
//		checkFilter(xViewModel, "generatePDF");
		proxy.generatePDF(sessionId, xViewModel, config, cb);
	}

	public void getRoles(String sessionId, String viewId, AsyncCallback<Boolean[]> cb) {
		proxy.getRoles(sessionId, viewId, cb);
	}

	public void setVisibility(String sessionId, XFolderElement element,
			boolean visible, boolean editable, String ownerId, String accountId,
			String dbId, String cubeId, AsyncCallback<XView> cb) {
		proxy.setVisibility(sessionId, element, visible, editable, ownerId, accountId, dbId, cubeId, cb);
	}

	public void isOwner(String sessionId, String viewId,
			AsyncCallback<Boolean> cb) {
		proxy.isOwner(sessionId, viewId, cb);
	}

	public void initializeRoles(String sessionId,
			AsyncCallback<HashMap<String, String>> cb) {
		proxy.initializeRoles(sessionId, cb);
	}

	public void applyAlias(String sessionId, String axisHierarchyId,
			String viewId, String axisId, XAlias alias,
			XElementNode[] allNodes, AsyncCallback<XElementNode[]> cb) {
		proxy.applyAlias(sessionId, axisHierarchyId, viewId, axisId, alias, allNodes, cb);
	}

	public void checkLocalFilter(String sessionId, String axisHierarchyId,
			String viewId, String axisId, String filterPaths,
			XElementNode[] visibleElements, boolean displayLeft,
			AsyncCallback<Boolean> cb) {
		proxy.checkLocalFilter(sessionId, axisHierarchyId, viewId, axisId, filterPaths, visibleElements, displayLeft, cb);
	}

	public void containsElement(String sessionId, String axisHierarchyId,
			String viewId, String axisId, XElement element, XSubset subset,
			AsyncCallback<Boolean> cb) {
		proxy.containsElement(sessionId, axisHierarchyId, viewId, axisId, element, subset, cb);
	}

	public void getElementPath(String sessionId, String axisHierarchyId,
			String viewId, String axisId, String selectedElementId,
			AsyncCallback<String> cb) {
		proxy.getElementPath(sessionId, axisHierarchyId, viewId, axisId, selectedElementId, cb);
	}

	public void getNumberOfChildren(String sessionId, String axisHierarchyId,
			String viewId, String axisId, String selectedElementId,
			AsyncCallback<Integer> cb) {
		proxy.getNumberOfChildren(sessionId, axisHierarchyId, viewId, axisId, selectedElementId, cb);
	}

	public void hideItem(String sessionId, XAxisItem item, List <XAxisItem> roots, String viewId,
			String axisId, boolean column, boolean hideLevel, AsyncCallback<String []> cb) {
		proxy.hideItem(sessionId, item, roots, viewId, axisId, column, hideLevel, cb);
	}

	public void willUpdateView(String sessionId, String viewId, String axisHierarchyId,
			String axisId, AsyncCallback<XLoadInfo> cb) {
		proxy.willUpdateView(sessionId, viewId, axisHierarchyId, axisId, cb);
	}

	public void proceedSwapAxes(String sessionId, XViewModel view,
			AsyncCallback<XViewModel> cb) {
		proxy.proceedSwapAxes(sessionId, view, cb);
	}

	public void willSwapAxes(String sessionId, String viewId,
			AsyncCallback<XLoadInfo> cb) {
		proxy.willSwapAxes(sessionId, viewId, cb);
	}

	public void deleteFile(String fileName, AsyncCallback<Void> cb) {
		proxy.deleteFile(fileName, cb);
	}

	public void getSubobjectId(AsyncCallback<String> cb) {
		proxy.getSubobjectId(cb);
	}
	public void getSpagoBIUserMode(AsyncCallback<String> cb) {
		proxy.getSpagoBIUserMode(cb);
	}
}
