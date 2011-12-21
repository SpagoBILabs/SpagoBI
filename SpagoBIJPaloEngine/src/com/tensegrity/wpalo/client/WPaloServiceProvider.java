/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
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

public class WPaloServiceProvider implements WPaloServiceAsync {

	private final static WPaloServiceProvider instance =
		new WPaloServiceProvider();
	private final WPaloServiceAsync proxy;
	
	private WPaloServiceProvider() {
		proxy = GWT.create(WPaloService.class);
		((ServiceDefTarget) proxy).setServiceEntryPoint(
				GWT.getModuleBaseURL() + "wpalo-service");				
	}
	
	public static WPaloServiceProvider getInstance() {
		return instance;
	}
	
	public void loadElements(XDimension dim, AsyncCallback<List<XElement>> cb) {
		proxy.loadElements(dim, cb);
	}

	public void loadChildren(String sessionId, TreeNode parent, AsyncCallback <List<TreeNode>> cb) {
		proxy.loadChildren(sessionId, parent, cb);
	}
	
	public void loadChildren(String sessionId, String parentType, String viewId, String axisId, String parentId, String parentPath, AsyncCallback<List<TreeNode>> cb) {
		proxy.loadChildren(sessionId, parentType, viewId, axisId, parentId, parentPath, cb);
	}

	public void loadWorksheet(XTemplate template, AsyncCallback<XWorksheet> cb) {
		proxy.loadWorksheet(template, cb);
	}
	
	public void loadWorksheet(XReport report, XUser user, AsyncCallback<XWorksheet> cb) {
		proxy.loadWorksheet(report, user, cb);
	}

	public void createStaticFolder(String name, XObject parent, XUser user, AsyncCallback <XStaticReportFolder> cb) {
		proxy.createStaticFolder(name, parent, user, cb);
	}

	public void createDynamicFolder(String name, XObject parent, XUser user,
			AsyncCallback<XDynamicReportFolder> cb) {
		proxy.createDynamicFolder(name, parent, user, cb);
	}

	public void addReceivers(XReportFolder parent, XObject [] receivers, XUser user,
			AsyncCallback<XReport []> cb) {
		proxy.addReceivers(parent, receivers, user, cb);
	}

	public void deleteElementTreeNodes(List<XObject> objects, XUser user,
			AsyncCallback<Boolean> cb) {
		proxy.deleteElementTreeNodes(objects, user, cb);
	}

	public void assignSubsetOrDimension(XDynamicReportFolder folder,
			XObject subdim, XUser user, AsyncCallback<Boolean> cb) {
		proxy.assignSubsetOrDimension(folder, subdim, user, cb);
	}

	public void getVariables(XReport report, XUser user, AsyncCallback<XVariableDescriptor> cb) {
		proxy.getVariables(report, user, cb);
	}

	public void createView(XCube parentCube, String name, XUser user, AsyncCallback<XView> cb) {
		proxy.createView(parentCube, name, user, cb);
	}

	public void createWorkbook(XObject parent, String name,
			AsyncCallback<XWorkbook> cb) {
		proxy.createWorkbook(parent, name, cb);
	}

	public void applyMapping(XReport input, XObject [] keys, String [] values, XUser user, AsyncCallback <Boolean> cb) {
		proxy.applyMapping(input, keys, values, user, cb);
	}

	public void deleteReportTreeNodes(List<XObject> objects, XUser user,
			AsyncCallback<Boolean> cb) {
		proxy.deleteReportTreeNodes(objects, user, cb);
	}

	public void loadAccounts(String sessionId, AsyncCallback<XAccount[]> cb) {
		proxy.loadAccounts(sessionId, cb);
	}

	public void openViewDirectly(String locale, String link, AsyncCallback<XDirectLinkData> cb) {
		proxy.openViewDirectly(locale, link, cb);
	}
	
	public void openViewAfterLogin(String locale, String sessionId, String link, AsyncCallback <XDirectLinkData> cb) {
		proxy.openViewAfterLogin(locale, sessionId, link, cb);
	}

	public void openPaloSuiteView(String locale, String link, AsyncCallback<XDirectLinkData> cb) {
		proxy.openPaloSuiteView(locale, link, cb);
	}

	public void loadHierarchyTree(String sessionId, String hierarchyId, String viewId,
			String axisId, int level, AsyncCallback<List<TreeNode>> cb) {
		proxy.loadHierarchyTree(sessionId, hierarchyId, viewId, axisId, level, cb);
	}

	public void loadPaloSuiteAccounts(String sessionId, String link,
			AsyncCallback<XAccount[]> cb) {
		proxy.loadPaloSuiteAccounts(sessionId, link, cb);
	}
}
