/*
*
* @file FolderReader.java
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
* @version $Id: FolderReader.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.stream.StreamSource;

import org.palo.api.exceptions.PaloIOException;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.internal.io.xml.FolderXMLHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * <code>FolderReader</code>
 * Reads a folder structure.
 *
 * @author Philipp Bouillon
 * @version $Id: FolderReader.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
class FolderReader {

	//--------------------------------------------------------------------------
	// FACTORY
	//
	private static FolderReader instance = new FolderReader();
	static final FolderReader getInstance() {
		return instance;
	}

	
	//--------------------------------------------------------------------------
	// INSTANCE
	//
	private FolderReader() {
	}

	
	ExplorerTreeNode fromXML(AuthUser user, InputStream input) throws PaloIOException {
		ExplorerTreeNode root = null;
		FolderXMLHandler folderHandler = null;
		try {
			StreamSource xml = new StreamSource(input);
//			TypeInfoProvider typeProvider = vHandler.getTypeInfoProvider();
			folderHandler = 
				new FolderXMLHandler(user);

			XMLReader parser = XMLReaderFactory.createXMLReader();
			parser.setContentHandler(folderHandler);
			parser.parse(new InputSource(xml.getInputStream()));
			root = folderHandler.getRoot();
			// dump(subset);
		} catch (SAXException e) {
			throw new PaloIOException("XML Exception during loading of folder!", e);
		} catch (IOException e) {
			throw new PaloIOException("IOException during loading of folder!", e);
		} catch(Exception e) {
			throw new PaloIOException("Exception during loading of folder!", e);
		} finally {
			if (folderHandler != null) {
				folderHandler.clear();
				folderHandler = null;
			}
		}
//		if (root == null) {
//			root = new StaticFolder(null, user.getLoginName());
//			FolderModel.getInstance().save(user, root);
//		}
		return root;
	}
}
