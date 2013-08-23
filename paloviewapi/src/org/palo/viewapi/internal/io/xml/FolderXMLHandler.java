/*
*
* @file FolderXMLHandler.java
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
* @version $Id: FolderXMLHandler.java,v 1.9 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.io.xml;

import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Stack;

import org.palo.viewapi.AuthUser;
import org.palo.viewapi.internal.ExplorerTreeNode;
import org.palo.viewapi.internal.StaticFolder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <code>FolderXMLHandler</code>
 * Reads a folder structure from the database.
 *
 * @author Philipp Bouillon
 * @version $Id: FolderXMLHandler.java,v 1.9 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public class FolderXMLHandler extends DefaultHandler {

	private Stack<String> absPath = new Stack<String>();
	private final StringBuffer strBuffer = new StringBuffer();
	private final HashMap<String, Class<? extends IXMLHandler>> xmlHandlers = 
		new HashMap<String, Class<? extends IXMLHandler>>();
	
	private ExplorerTreeNode root;
	private IXMLHandler handler;
	private final AuthUser user;
	private final Stack <ExplorerTreeNode> parents = new Stack<ExplorerTreeNode>();
	
	public FolderXMLHandler(AuthUser user) {
		this.user = user;
		xmlHandlers.put(StaticFolderHandler.XPATH, StaticFolderHandler.class);
		xmlHandlers.put(DynamicFolderHandler.XPATH, DynamicFolderHandler.class);
		xmlHandlers.put(FolderElementHandler.XPATH, FolderElementHandler.class);		
	}
	
	public final ExplorerTreeNode getRoot() {
		if (root != null && root.getName().equals("Invisible Root")) {
			ExplorerTreeNode [] kids = root.getChildren();
			if (kids != null && kids.length > 0) {
				kids[0].setParent(null);
				return kids[0];
			}
			return null;
		}
		return root;
	}
	
	
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		reset(strBuffer);
		absPath.push(qName);
		String xPath = getXPath();
		if (xPath.equals("/folder")) {		
			root = new StaticFolder(null, "Invisible Root");
			parents.push(root);
		} else {
			String strippedPath = xPath.substring(0, xPath.indexOf("/", 2) + 1);
			strippedPath += xPath.substring(xPath.lastIndexOf("/") + 1);
			if (xmlHandlers.containsKey(strippedPath)) {
				handler = createHandler(strippedPath);
			} 
			if (handler != null) {
				handler.enter(xPath, attributes);
			}
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		String xPath = getXPath();
		if (xPath.equals("/folder")) {		
			if (handler != null) {
				handler.leave(xPath, strBuffer.toString());
			}
		} else {
			String strippedPath = xPath.substring(0, xPath.indexOf("/", 2) + 1);
			strippedPath += xPath.substring(xPath.lastIndexOf("/") + 1);
			if (xmlHandlers.containsKey(strippedPath)) {
				handler = createHandler(strippedPath);
			} 

			if (handler != null) {
				handler.leave(xPath, strBuffer.toString());
			}
		}
		if (absPath.size() > 0)
			absPath.pop();
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		strBuffer.append(ch, start, length);
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		super.processingInstruction(target, data);
	}
	
	AuthUser getUser() {
		return user;
	}
	
	ExplorerTreeNode getCurrentParent() {
		return parents.peek();
	}
		
	ExplorerTreeNode popParent() {
		ExplorerTreeNode node = parents.pop();
		return node;
	}
	
	void pushParent(ExplorerTreeNode node) {
		parents.push(node);
	}
		
	//--------------------------------------------------------------------------
	// PRIVATE METHODS
	//
	private final void reset(StringBuffer strBuffer) {
		strBuffer.delete(0, strBuffer.length());
	}
	
	private final String getXPath() {
		Enumeration<String> allPaths = absPath.elements();
		StringBuffer path = new StringBuffer();
		while (allPaths.hasMoreElements()) {
			path.append("/");
			path.append(allPaths.nextElement());
		}
		return path.toString();
	}
	
	private final IXMLHandler createHandler(String xPath) {
		Class<? extends IXMLHandler> handler = xmlHandlers.get(xPath);
		if (handler != null) {
			try {
				Constructor<? extends IXMLHandler> constructor = 
					handler.getConstructor(new Class[] {FolderXMLHandler.class});
				return constructor.newInstance(new Object[] {this});
			} catch (Exception e) {
//				String msg = "Error creating subset filter"; //$NON-NLS-1$
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void clear() {
		DynamicFolderHandler.clear();
		xmlHandlers.clear();
	}
}
