/*
*
* @file StaticFolderHandler.java
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
* @author Philipp Bouillon
*
* @version $Id: StaticFolderHandler.java,v 1.11 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.io.xml;

import org.palo.api.PaloAPIException;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.View;
import org.palo.viewapi.internal.StaticFolder;
import org.palo.viewapi.services.ServiceProvider;
import org.xml.sax.Attributes;

/**
 * <code>StaticFolderHandler</code>
 * Reads a static folder from the database.
 *
 * @author Philipp Bouillon
 * @version $Id: StaticFolderHandler.java,v 1.11 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public class StaticFolderHandler implements IXMLHandler {

	public static final String XPATH = "/folder/staticFolder";
		
	private FolderXMLHandler xmlHandler;
	
	public StaticFolderHandler(FolderXMLHandler xmlHandler) {
		this.xmlHandler = xmlHandler;
	}
	
	static final View parseSourceView(FolderXMLHandler xmlHandler, String viewId) {		
		AuthUser user = xmlHandler.getUser();		
		try {
			return ServiceProvider.getViewService(user).getView(viewId);
		} catch (Exception e) {
		}
		return null;
	}
	
	public void enter(String path, Attributes attributes) {
		if (path.startsWith("/folder/") && path.endsWith("staticFolder")) {
			// required attributes:
			String id = attributes.getValue("id");
			if (id == null || id.equals("")) {
				throw new PaloAPIException("StaticFolderHandler: no id defined!");
			}
			
			String name = attributes.getValue("name");			
			if (name == null) {
				throw new PaloAPIException("StaticFolderHandler: no name specified!");
			}
			// optional attributes:
			String source = attributes.getValue("source");
			View sourceView = null;
			if (source != null) {
				sourceView = parseSourceView(xmlHandler, source);
			}
				
			// add Static Folder to current parent
			StaticFolder f = StaticFolder.internalCreate(
					xmlHandler.getCurrentParent(), id, name);
			if (sourceView != null) {
				f.setSourceObject(sourceView);
			}
			xmlHandler.pushParent(f);
		}
	}

	public String getXPath() {
		return XPATH;
	}

	public void leave(String path, String value) {
		if (path.startsWith("/folder/") && (path.endsWith("staticFolder"))) {
			xmlHandler.popParent();
		}
	}	    	
}
