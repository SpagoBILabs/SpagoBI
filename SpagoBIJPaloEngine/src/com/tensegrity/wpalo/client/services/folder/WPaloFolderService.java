/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.services.folder;

import com.google.gwt.user.client.rpc.RemoteService;
import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.folders.XFolderElement;
import com.tensegrity.palo.gwt.core.client.models.folders.XStaticFolder;
import com.tensegrity.wpalo.client.exceptions.DbOperationFailedException;

public interface WPaloFolderService extends RemoteService {

	public XStaticFolder loadFolderRoot(String sessionId) throws SessionExpiredException;

	public XStaticFolder createFolder(String sessionId, String name, XStaticFolder xParent)
			throws DbOperationFailedException, SessionExpiredException;

	public XFolderElement createFolderElement(String sessionId, XView xView,
			XStaticFolder xParentFolder, boolean isPublic, boolean isEditable) throws DbOperationFailedException,
			SessionExpiredException;

	public XFolderElement[] importViewsAsFolderElements(String sessionId, XView[] views,
			XStaticFolder xParentFolder, boolean isPublic, boolean isEditable) throws DbOperationFailedException,
			SessionExpiredException;

	public XView importView(String sessionId, XView view) throws 
		DbOperationFailedException, SessionExpiredException;
	
	public void deleteFolder(String sessionId, XStaticFolder xFolder)
			throws DbOperationFailedException, SessionExpiredException;

	public void deleteFolderElement(String sessionId, XFolderElement xFolderElement)
			throws DbOperationFailedException, SessionExpiredException;

	public void move(String sessionId, XObject[] xObjects, XStaticFolder toXFolder)
			throws DbOperationFailedException, SessionExpiredException;

	public void renameFolderElement(String sessionId, XFolderElement xFolderElement,
			String newName) throws DbOperationFailedException,
			SessionExpiredException;
	
	public void renameFolder(String sessionId, XStaticFolder folder, String newName)
		throws DbOperationFailedException, SessionExpiredException;
	
	public boolean hasWritePermission(String sessionId) 
		throws DbOperationFailedException, SessionExpiredException;
	
	public boolean hasCreatePermission(String sessionId) 
		throws DbOperationFailedException, SessionExpiredException;	
}
