/*
*
* @file WPaloService.java
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
* @version $Id: WPaloService.java,v 1.31 2010/04/12 11:13:36 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.tensegrity.palo.gwt.core.client.exceptions.PaloGwtCoreException;
import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XDirectLinkData;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.palo.XCube;
import com.tensegrity.palo.gwt.core.client.models.palo.XDimension;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.palo.gwt.core.client.models.reports.XDynamicReportFolder;
import com.tensegrity.palo.gwt.core.client.models.reports.XReport;
import com.tensegrity.palo.gwt.core.client.models.reports.XReportFolder;
import com.tensegrity.palo.gwt.core.client.models.reports.XStaticReportFolder;
import com.tensegrity.palo.gwt.core.client.models.reports.XVariableDescriptor;
import com.tensegrity.wpalo.client.serialization.templates.XTemplate;
import com.tensegrity.wpalo.client.serialization.templates.XWorkbook;
import com.tensegrity.wpalo.client.serialization.templates.XWorksheet;
import com.tensegrity.wpalo.client.ui.model.TreeNode;

public interface WPaloService extends RemoteService {
	
	List <TreeNode> loadChildren(String sessionId, TreeNode node) throws SessionExpiredException, PaloGwtCoreException;	
	List <TreeNode> loadHierarchyTree(String sessionId, String hierarchyId, String viewId, String axisId, int level) throws SessionExpiredException, PaloGwtCoreException;
	List <TreeNode> loadChildren(String sessionId, String parentType, String viewId, String axisId, String parentId, String parentPath) throws SessionExpiredException, PaloGwtCoreException;
	List<XElement> loadElements(XDimension dim);
	public XAccount[] loadAccounts(String sessionId) throws SessionExpiredException;
	XAccount[] loadPaloSuiteAccounts(String sessionId, String link) throws SessionExpiredException;
	XWorksheet loadWorksheet(XTemplate template);
	XWorksheet loadWorksheet(XReport report, XUser user);
	XStaticReportFolder createStaticFolder(String name, XObject parent, XUser user);
	XDynamicReportFolder createDynamicFolder(String name, XObject parent, XUser user);
	XReport [] addReceivers(XReportFolder parent, XObject [] receiver, XUser user);
	boolean deleteElementTreeNodes(List <XObject> objects, XUser user);
	boolean deleteReportTreeNodes(List <XObject> objects, XUser user);
	boolean assignSubsetOrDimension(XDynamicReportFolder folder, XObject subdim, XUser user);
	XVariableDescriptor getVariables(XReport report, XUser user);
	
	XWorkbook createWorkbook(XObject parent, String name);
	XView createView(XCube parentCube, String name, XUser user);
	boolean applyMapping(XReport input, XObject [] keys, String [] values, XUser user);
	XDirectLinkData openViewAfterLogin(String locale, String sessionId, String link);
	XDirectLinkData openViewDirectly(String locale, String link);
	XDirectLinkData openPaloSuiteView(String locale, String link);
}
