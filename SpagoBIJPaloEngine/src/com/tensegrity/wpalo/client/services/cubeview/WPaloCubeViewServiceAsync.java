/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.services.cubeview;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
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
 * <code>WPaloCubeViewServiceAsync</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: WPaloCubeViewServiceAsync.java,v 1.56 2010/04/12 11:13:36 PhilippBouillon Exp $
 **/
public interface WPaloCubeViewServiceAsync {
	
	
	public void willOpen(String sessionId, XView xView, AsyncCallback<XLoadInfo> cb);
	public void proceedOpen(String sessionId, XView xView, AsyncCallback<XViewModel> cb);

	public void updateView(String sessionId, XViewModel xViewModel, AsyncCallback<Void> cb);
	public void updateAndReloadView(String sessionId, XViewModel xViewModel, AsyncCallback<XViewModel> cb);
	public void willUpdateView(String sessionId, XViewModel xViewModel, AsyncCallback<XLoadInfo> cb);
	public void proceedUpdateView(String sessionId, XViewModel xViewModel, AsyncCallback<XViewModel> cb);
	public void proceedUpdateViewWithoutTable(String sessionId, XViewModel xViewModel, AsyncCallback<XViewModel> cb);
	public void cancelUpdateView(String sessionId, XViewModel xViewModel, AsyncCallback<XViewModel> cb);
	public void isOwner(String sessionId, String viewId, AsyncCallback <Boolean> cb);

	public void willUpdateView(String sessionId, String viewId, String axisHierarchyId, String axisId, AsyncCallback<XLoadInfo> cb);
	
	
	public void updateAxisHierarchy(String sessionId, XAxisHierarchy hierarchy, AsyncCallback<XElement> cb);
	public void willUpdateAxisHierarchy(String sessionId, XAxisHierarchy hierarchy, AsyncCallback<XLoadInfo> cb);

	
	public void willReload(String sessionId, XViewModel xViewModel, AsyncCallback<XLoadInfo> cb);
	public void proceedReload(String sessionId, XViewModel xViewModel, AsyncCallback<XViewModel> cb);

	public void collapse(String sessionId, XAxisItem item, String viewId, String axisId, AsyncCallback<Void> cb);
	
	public void willExpand(String sessionId, XAxisItem item, String viewId, String axisId, AsyncCallback<XLoadInfo> cb);
	public void proceedExpand(String sessionId, XAxisItem item, String viewId, String axisId, AsyncCallback<XDelta> cb);
	public void cancelExpand(String sessionId, XAxisItem item, String viewId, String axisId, AsyncCallback<Void> cb);

	public void willSwapAxes(String sessionId, String viewId, AsyncCallback <XLoadInfo> cb);
	public void proceedSwapAxes(String sessionId, XViewModel view, AsyncCallback <XViewModel> cb);
	
	public void willCollapse(String sessionId, XAxisItem item, String viewId, String axisId, AsyncCallback<XLoadInfo> cb);
	public void proceedCollapse(String sessionId, XAxisItem item, String viewId, String axisId, AsyncCallback<Void> cb);
	public void cancelCollapse(String sessionId, XAxisItem item, String viewId, String axisId, AsyncCallback<Void> cb);
	
	public void willSetExpandState(String sessionId, XAxisItem[] expanded, XAxisItem[] collapsed, int expandDepth, String viewId, String axisId, AsyncCallback<XLoadInfo> cb);
	public void proceedSetExpandState(String sessionId, String viewId, AsyncCallback<XDelta[]> cb);
	public void cancelSetExpandState(String sessionId, String viewId, AsyncCallback<Void> cb);
	
	public void willChangeSelectedElement(String sessionId, XViewModel xViewModel,
			XAxisHierarchy xAxishHierarchy, AsyncCallback<XLoadInfo> cb);
	public void proceedChangeSelectedElement(String sessionId, XViewModel xViewModel,
			XAxisHierarchy xAxisHierarchy, XElement selectedElement, AsyncCallback<XViewModel> cb);

	
	public void saveView(String sessionId, XViewModel view, AsyncCallback<XViewModel> cb);
	public void saveViewAs(String sessionId, String name, XViewModel view, AsyncCallback<XView> cb);
	public void renameView(String sessionId, XView xView, String newName, AsyncCallback<Void> cb);
	
	public void writeCell(String sessionId, XCell cell, XViewModel view, AsyncCallback<XCellCollection> cb);
		
	public void deleteView(String sessionId, XView xView, AsyncCallback<Void> cb);
	public void closeView(String sessionId, XViewModel xViewModel, AsyncCallback<Void> cb);
	
	public void importViews(String sessionId, XView[] views, AsyncCallback<XView[]> cb);
	
	public void runAsync(String sessionId, int wait, AsyncCallback <Void> cb);
	public void checkPermission(String sessionId, String viewId, int right, AsyncCallback<Boolean> cb);
	public void checkPermission(String sessionId, int right, AsyncCallback <Boolean> cb);
	public void getRoles(String sessionId, String viewId, AsyncCallback <Boolean []> cb);
	public void setVisibility(String sessionId, XFolderElement element, boolean visible, boolean editable, String ownerId, String accountId, String dbId, String cubeId, AsyncCallback <XView> cb);
	
	public void checkLocalFilter(String sessionId, String axisHierarchyId, String viewId, String axisId, String filterPaths, XElementNode [] visibleElements, boolean displayLeft, AsyncCallback <Boolean> cb);
	public void applyAlias(String sessionId, String axisHierarchyId, String viewId, String axisId, XAlias alias, XElementNode[] allNodes, AsyncCallback <XElementNode []> cb);
	public void getWarningThresholds(String sessionId, String [] browserPrefixes, AsyncCallback <String []> cb);
	public void containsElement(String sessionId, String axisHierarchyId, String viewId, String axisId, XElement element, XSubset subset, AsyncCallback <Boolean> cb);
	public void updateLoadInfo(String sessionId, XViewModel xViewModel, int cellsToDisplay, AsyncCallback <XLoadInfo> cb);		
	
	public void generatePDF(String sessionId, XViewModel xViewModel, XPrintConfiguration config, AsyncCallback <XPrintResult> cb);
	public void initializeRoles(String sessionId, AsyncCallback <HashMap <String, String>> cb);
	
	public void getElementPath(String sessionId, String axisHierarchyId, String viewId, String axisId, String selectedElementId, AsyncCallback <String> cb);
	public void getNumberOfChildren(String sessionId, String axisHierarchyId, String viewId, String axisId, String selectedElementId, AsyncCallback <Integer> cb);
	
	public void hideItem(String sessionId, XAxisItem item, List <XAxisItem> roots, String viewId, String axisId, boolean column, boolean hideLevel, AsyncCallback <String []> cb);
	
	public void deleteFile(String fileName, AsyncCallback <Void> cb);
	public void getSubobjectId(AsyncCallback <String> cb);
	public void getSpagoBIUserMode(AsyncCallback <String> cb);
}
