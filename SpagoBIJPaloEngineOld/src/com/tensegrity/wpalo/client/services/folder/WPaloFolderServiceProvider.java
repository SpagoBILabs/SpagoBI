/*
*
* @file WPaloFolderServiceProvider.java
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
* @version $Id: WPaloFolderServiceProvider.java,v 1.11 2009/12/17 16:14:20 PhilippBouillon Exp $
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
