/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.services.folder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.folders.XFolderElement;
import com.tensegrity.palo.gwt.core.client.models.folders.XStaticFolder;

public class WPaloFolderServiceProvider implements WPaloFolderServiceAsync {

	private final static WPaloFolderServiceProvider instance = new WPaloFolderServiceProvider();

	public static WPaloFolderServiceProvider getInstance() {
		return instance;
	}

	private final WPaloFolderServiceAsync proxy;

	
	public WPaloFolderServiceProvider() {
		proxy = GWT.create(WPaloFolderService.class);
		((ServiceDefTarget) proxy).setServiceEntryPoint(GWT.getModuleBaseURL()
				+ "wpalo-folder-service");
	}


	public void createFolder(String sessionId, String name, XStaticFolder xParent,
			AsyncCallback<XStaticFolder> cb) {
		proxy.createFolder(sessionId, name, xParent, cb);
	}

	public void deleteFolder(String sessionId, XStaticFolder folder, AsyncCallback<Void> cb) {
		proxy.deleteFolder(sessionId, folder, cb);
	}

	public void loadFolderRoot(String sessionId, AsyncCallback<XStaticFolder> cb) {
		proxy.loadFolderRoot(sessionId, cb);
	}

	public void deleteFolderElement(String sessionId, XFolderElement xFolderElement,
			AsyncCallback<Void> cb) {
		proxy.deleteFolderElement(sessionId, xFolderElement, cb);
	}

	public void importViewsAsFolderElements(String sessionId, XView[] views,
			XStaticFolder xParentFolder, boolean isPublic, boolean isEditable, AsyncCallback<XFolderElement[]> cb) {
		proxy.importViewsAsFolderElements(sessionId, views, xParentFolder, isPublic, isEditable, cb);
	}
	
	public void importView(String sessionId, XView view, AsyncCallback <XView> cb) {
		proxy.importView(sessionId, view, cb);
	}

	public void createFolderElement(String sessionId, XView xView, XStaticFolder xParentFolder,boolean isPublic, boolean isEditable,
			AsyncCallback<XFolderElement> cb) {
		proxy.createFolderElement(sessionId, xView, xParentFolder, isPublic, isEditable, cb);
	}

	public void move(String sessionId, XObject[] xObjects, XStaticFolder toXFolder,
			AsyncCallback<Void> cb) {
		proxy.move(sessionId, xObjects, toXFolder, cb);		
	}


	public void renameFolderElement(String sessionId, XFolderElement xFolderElement, String newName,
			AsyncCallback<Void> cb) {
		proxy.renameFolderElement(sessionId, xFolderElement, newName, cb);
	}

	public void renameFolder(String sessionId, XStaticFolder folder, String newName,
			AsyncCallback <Void> cb) {
		proxy.renameFolder(sessionId, folder, newName, cb);
	}


	public void hasWritePermission(String sessionId, AsyncCallback<Boolean> cb) {
		proxy.hasWritePermission(sessionId, cb);
	}
	
	public void hasCreatePermission(String sessionId, AsyncCallback <Boolean> cb) {
		proxy.hasCreatePermission(sessionId, cb);
	}
}
