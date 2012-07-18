/*
*
* @file WPaloCubeViewService.java
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
* @version $Id: WPaloCubeViewService.java,v 1.58 2010/04/12 11:13:36 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.services.cubeview;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.tensegrity.palo.gwt.core.client.exceptions.PaloGwtCoreException;
import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
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
import com.tensegrity.wpalo.client.exceptions.DbOperationFailedException;

/**
 * <code>WPaloCubeViewService</code> TODO DOCUMENT ME
 * 
 * @version $Id: WPaloCubeViewService.java,v 1.19 2009/06/18 12:43:22 ArndHouben
 *          Exp $
 **/
public interface WPaloCubeViewService extends RemoteService {

//TODO CLEAN THIS UP!!!!
	
	public XLoadInfo willOpen(String sessionId, XView xView) throws SessionExpiredException, PaloGwtCoreException;
	public XViewModel proceedOpen(String sessionId, XView xView) throws SessionExpiredException, PaloGwtCoreException;

	/** for a simple view update without any interaction, e.g. before save */
	public void updateView(String sessionId, XViewModel xViewModel) throws SessionExpiredException;
	public XViewModel updateAndReloadView(String sessionId, XViewModel xViewModel) throws SessionExpiredException;
	public XLoadInfo willUpdateView(String sessionId, XViewModel xViewModel) throws SessionExpiredException;
	public XViewModel proceedUpdateView(String sessionId, XViewModel xViewModel) throws SessionExpiredException;
	public XViewModel proceedUpdateViewWithoutTable(String sessionId, XViewModel xViewModel) throws SessionExpiredException;
	public XViewModel cancelUpdateView(String sessionId, XViewModel xViewModel) throws SessionExpiredException;
	

	public XLoadInfo willUpdateView(String sessionId, String viewId, String axisHierarchyId, String axisId) throws SessionExpiredException; 
	
	public XElement updateAxisHierarchy(String sessionId, XAxisHierarchy hierarchy) throws SessionExpiredException;
	public XLoadInfo willUpdateAxisHierarchy(String sessionId, XAxisHierarchy hierarchy) throws SessionExpiredException;
	
	
	public XLoadInfo willExpand(String sessionId, XAxisItem item, String viewId, String axisId) throws SessionExpiredException;
	public XDelta proceedExpand(String sessionId, XAxisItem item, String viewId, String axisId) throws SessionExpiredException;
	public void cancelExpand(String sessionId, XAxisItem item, String viewId, String axisId) throws SessionExpiredException;
	
	public XLoadInfo willCollapse(String sessionId, XAxisItem item, String viewId, String axisId) throws SessionExpiredException;
	public void proceedCollapse(String sessionId, XAxisItem item, String viewId, String axisId) throws SessionExpiredException;
	public void cancelCollapse(String sessionId, XAxisItem item, String viewId, String axisId) throws SessionExpiredException;
	
	public XLoadInfo willSetExpandState(String sessionId, XAxisItem[] expanded, XAxisItem[] collapsed, int expandDepth, String viewId, String axisId) throws SessionExpiredException;
	public XDelta[] proceedSetExpandState(String sessionId, String viewId) throws SessionExpiredException;
	public void cancelSetExpandState(String sessionId, String viewId) throws SessionExpiredException;

	public XLoadInfo willSwapAxes(String sessionId, String viewId) throws SessionExpiredException;
	public XViewModel proceedSwapAxes(String sessionId, XViewModel view) throws SessionExpiredException;
	
	public XLoadInfo willReload(String sessionId, XViewModel xViewModel) throws SessionExpiredException;
	public XViewModel proceedReload(String sessionId, XViewModel xViewModel) throws SessionExpiredException;

	
	public void collapse(String sessionId, XAxisItem item, String viewId, String axisId) throws SessionExpiredException;

	public XLoadInfo willChangeSelectedElement(String sessionId, XViewModel xViewModel,
			XAxisHierarchy xAxishHierarchy) throws SessionExpiredException;
	public XViewModel proceedChangeSelectedElement(String sessionId, XViewModel xViewModel,
			XAxisHierarchy xAxisHierarchy, XElement selectedElement)
			throws SessionExpiredException;

	public XViewModel saveView(String sessionId, XViewModel view) throws DbOperationFailedException,
			SessionExpiredException;

	public XView saveViewAs(String sessionId, String name, XViewModel view)
			throws DbOperationFailedException, SessionExpiredException;

	public void renameView(String sessionId, XView xView, String newName)
			throws DbOperationFailedException, SessionExpiredException;

	public XCellCollection writeCell(String sessionId, XCell cell, XViewModel view)
			throws SessionExpiredException;

	public void deleteView(String sessionId, XView xView) throws DbOperationFailedException,
			SessionExpiredException;

	public void closeView(String sessionId, XViewModel xViewModel) throws SessionExpiredException;

	public XView[] importViews(String sessionId, XView[] views) throws SessionExpiredException;
	
	public void runAsync(String sessionId, int wait) throws SessionExpiredException;
	public boolean checkPermission(String sessionId, String viewId, int right) throws SessionExpiredException;
	public boolean checkPermission(String sessionId, int right) throws SessionExpiredException;
	public Boolean [] getRoles(String sessionId, String viewId) throws SessionExpiredException;
	public XView setVisibility(String sessionId, XFolderElement element, boolean visible, boolean editable, String ownerId, String accountId, String dbId, String cubeId) throws SessionExpiredException;
	public Boolean isOwner(String sessionId, String viewId) throws SessionExpiredException;
	
	public boolean checkLocalFilter(String sessionId, String axisHierarchyId, String viewId, String axisId, String filterPaths, XElementNode [] visibleElements, boolean displayLeft) throws SessionExpiredException, PaloGwtCoreException;
	public XElementNode [] applyAlias(String sessionId, String axisHierarchyId, String viewId, String axisId, XAlias alias, XElementNode[] allNodes) throws SessionExpiredException, PaloGwtCoreException;
	public String [] getWarningThresholds(String sessionId, String [] browserPrefixes) throws SessionExpiredException;
	public boolean containsElement(String sessionId, String axisHierarchyId, String viewId, String axisId, XElement element, XSubset subset) throws SessionExpiredException, PaloGwtCoreException;
	public XLoadInfo updateLoadInfo(String sessionId, XViewModel xViewModel, int cellsToDisplay) throws SessionExpiredException;
	
	public XPrintResult generatePDF(String sessionId, XViewModel xViewModel, XPrintConfiguration config) throws SessionExpiredException;
	public HashMap <String, String> initializeRoles(String sessionId) throws SessionExpiredException;
	
	public String getElementPath(String sessionId, String axisHierarchyId, String viewId, String axisId, String selectedElementId) throws SessionExpiredException, PaloGwtCoreException;
	public int getNumberOfChildren(String sessionId, String axisHierarchyId, String viewId, String axisId, String selectedElementId) throws SessionExpiredException, PaloGwtCoreException;
	
	public String [] hideItem(String sessionId, XAxisItem item, List <XAxisItem> roots, String viewId, String axisId, boolean column, boolean hideLevel) throws SessionExpiredException;
	
	public void deleteFile(String fileName);
	public String getSubobjectId();  
	public String getSpagoBIUserMode();
}
