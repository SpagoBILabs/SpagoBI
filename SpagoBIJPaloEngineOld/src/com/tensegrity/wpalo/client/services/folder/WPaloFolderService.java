/*
*
* @file WPaloFolderService.java
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
* @version $Id: WPaloFolderService.java,v 1.12 2009/12/17 16:14:20 PhilippBouillon Exp $
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
