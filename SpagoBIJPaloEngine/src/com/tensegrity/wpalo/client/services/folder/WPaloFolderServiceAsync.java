/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.services.folder;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.folders.XFolderElement;
import com.tensegrity.palo.gwt.core.client.models.folders.XStaticFolder;

public interface WPaloFolderServiceAsync {

	public void loadFolderRoot(String sessionId, AsyncCallback<XStaticFolder> cb);
	public void createFolder(String sessionId, String name, XStaticFolder xParent, AsyncCallback<XStaticFolder> cb);
	public void createFolderElement(String sessionId, XView xView, XStaticFolder xParentFolder, boolean isPublic, boolean isEditable, AsyncCallback<XFolderElement> cb);
	public void importViewsAsFolderElements(String sessionId, XView[] views, XStaticFolder xParentFolder, boolean isPublic, boolean isEditable, AsyncCallback<XFolderElement[]> cb);
	public void importView(String sessionId, XView view, AsyncCallback <XView> cb);
	public void deleteFolder(String sessionId, XStaticFolder folder, AsyncCallback<Void> cb);
	public void deleteFolderElement(String sessionId, XFolderElement xFolderElement, AsyncCallback<Void> cb);
	public void move(String sessionId, XObject[] xObjects, XStaticFolder toXFolder, AsyncCallback<Void> cb);
	public void renameFolderElement(String sessionId, XFolderElement xFolderElement, String newName, AsyncCallback<Void> cb);
	public void renameFolder(String sessionId, XStaticFolder folder, String newName, AsyncCallback <Void> cb);
	public void hasWritePermission(String sessionId, AsyncCallback <Boolean> cb);
	public void hasCreatePermission(String sessionId, AsyncCallback <Boolean> cb);
}
