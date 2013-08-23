/*
*
* @file CubeViewXMLHandler.java
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
* @version $Id: CubeViewXMLHandler.java,v 1.7 2010/02/12 13:51:05 PhilippBouillon Exp $
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

import org.palo.api.Cube;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.View;
import org.palo.viewapi.internal.cubeview.CubeViewFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <code>CubeViewXMLHandler</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: CubeViewXMLHandler.java,v 1.7 2010/02/12 13:51:05 PhilippBouillon Exp $
 **/
public class CubeViewXMLHandler extends DefaultHandler {

	private Stack<String> absPath = new Stack<String>();
	private final StringBuffer strBuffer = new StringBuffer();
	private final HashMap<String, Class<? extends IXMLHandler>> xmlHandlers = 
		new HashMap<String, Class<? extends IXMLHandler>>();
	private final View view;
	private final Cube srcCube;
	private final AuthUser user;
	private CubeView cubeView;
	private IXMLHandler handler;
	
	public CubeViewXMLHandler(AuthUser user, View view, Cube srcCube) {
		this.view = view;
		this.srcCube = srcCube;
		this.user = user;
		xmlHandlers.put(AxisHandler.XPATH, AxisHandler.class);
		xmlHandlers.put(FormatHandler.XPATH, FormatHandler.class);
		xmlHandlers.put(PropertyHandler.XPATH, PropertyHandler.class);
	}
	
	public final CubeView getView() {
		return cubeView;
	}
	
	
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		reset(strBuffer);
		absPath.push(qName);
		String xPath = getXPath();
		if (xPath.equals("/view"))
			cubeView = createView(user, attributes);
		else {
			if (xmlHandlers.containsKey(xPath)) {
				handler = createHandler(xPath);
			} 
			if (handler != null)
				handler.enter(xPath, attributes);
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		String xPath = getXPath();
		if(xPath.equals("/view"))
			return;
		else if (handler != null) {
			handler.leave(xPath, strBuffer.toString());
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
		// if (target.equals("palocubeview")) {
		// String version = data.substring(12, data.length() - 1).trim();
		// }
		super.processingInstruction(target, data);
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

	private final CubeView createView(AuthUser user, Attributes attributes) {
		//required view attributes:
//		String id = attributes.getValue("id");
//		String name = attributes.getValue("name"); //<- OPTIONAL...
//		String cubeId = attributes.getValue("cube");
		//TODO maybe we should assert that cubeId == srcCube.getId();
		//TODO maybe we should rewrite xml definition of cubeview...
//		if(!defName.equals(name))
//			name = defName;
		//get source cube:
		return CubeViewFactory.createView(view, srcCube, user, null);
	}
	
	private final IXMLHandler createHandler(String xPath) {
		Class<? extends IXMLHandler> handler = xmlHandlers.get(xPath);
		if (handler != null) {
			try {
				Constructor<? extends IXMLHandler> constructor = 
					handler.getConstructor(new Class[] {CubeView.class});
				return constructor.newInstance(new Object[] {cubeView});
			} catch (Exception e) {
//				String msg = "Error creating subset filter"; //$NON-NLS-1$
				e.printStackTrace();
			}
		}
		return null;
	}
}
