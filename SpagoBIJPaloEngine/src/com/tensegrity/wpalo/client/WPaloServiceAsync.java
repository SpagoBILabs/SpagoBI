/*
*
* @file WPaloServiceAsync.java
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
* @version $Id: WPaloServiceAsync.java,v 1.30 2010/04/12 11:13:36 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
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

public interface WPaloServiceAsync {
	void loadHierarchyTree(String sessionId, String hierarchyId, String viewId, String axisId, int level, AsyncCallback <List <TreeNode>> cb);
	void loadAccounts(String sessionId, AsyncCallback<XAccount[]> cb);
	void loadPaloSuiteAccounts(String sessionId, String link, AsyncCallback <XAccount []> cb);
	void loadChildren(String sessionId, TreeNode parent, AsyncCallback <List<TreeNode>> cb);
	void loadChildren(String sessionId, String parentType, String viewId, String axisId, String parentId, String parentPath, AsyncCallback <List <TreeNode>> cb);
	void loadElements(XDimension dim, AsyncCallback <List<XElement>> cb);
	void loadWorksheet(XTemplate template, AsyncCallback <XWorksheet> cb);
	void loadWorksheet(XReport report, XUser user, AsyncCallback <XWorksheet> cb);
	void createStaticFolder(String name, XObject parent, XUser user, AsyncCallback <XStaticReportFolder> cb);
	void createDynamicFolder(String name, XObject parent, XUser user, AsyncCallback <XDynamicReportFolder> cb);
	void addReceivers(XReportFolder parent, XObject [] receivers, XUser user, AsyncCallback <XReport []> cb);
	void deleteElementTreeNodes(List <XObject> objects, XUser user, AsyncCallback <Boolean> cb);
	void deleteReportTreeNodes(List <XObject> objects, XUser user, AsyncCallback <Boolean> cb);
	void assignSubsetOrDimension(XDynamicReportFolder folder, XObject subdim, XUser user, AsyncCallback <Boolean> cb);
	void getVariables(XReport report, XUser user, AsyncCallback <XVariableDescriptor> cb);
	
	void createWorkbook(XObject parent, String name, AsyncCallback <XWorkbook> cb);
	void createView(XCube parentCube, String name, XUser user, AsyncCallback <XView> cb);
	void applyMapping(XReport input, XObject [] keys, String [] values, XUser user, AsyncCallback <Boolean> cb);
	void openViewAfterLogin(String locale, String sessionId, String link, AsyncCallback <XDirectLinkData> cb);
	void openViewDirectly(String locale, String link, AsyncCallback <XDirectLinkData> cb);
	void openPaloSuiteView(String locale, String link, AsyncCallback <XDirectLinkData> cb);
}
