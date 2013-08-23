/*
*
* @file FormatXMLHandler.java
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
* @author Michael Raue <Michael.Raue@tensegrity-software.com>
*
* @version $Id: FormatXMLHandler.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
*
* @file FormatXMLHandler.java
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
* @author Michael Raue <Michael.Raue@tensegrity-software.com>
*
* @version $Id: FormatXMLHandler.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.core.server.services.cubeview.converter.formats.xml;

import java.util.HashMap;

import org.palo.api.impl.xml.BaseXMLHandler;
import org.palo.api.impl.xml.IPaloEndHandler;
import org.palo.api.impl.xml.IPaloStartHandler;
import org.palo.viewapi.CubeView;
import org.xml.sax.SAXException;

class FormatXMLHandler extends BaseXMLHandler {		
	private final HashMap <String, FormatHandler> handlers = 
		new HashMap <String, FormatHandler> ();
	
	FormatXMLHandler(CubeView view) {
		registerHandler("1.0", new FormatHandler1_0(view));
	}
								
	/**
	 * Registers the given <code>{@link FormatHandler}</code> for the 
	 * specified cube view version 
	 * @param version a valid cube view version
	 * @param handler a handler to use for reading cube views of specified version
	 */
	final void registerHandler(String version, FormatHandler handler) {
		handlers.put(version, handler);
	}
		
		
	public final void processingInstruction(String target, String data) throws SAXException {
		if (target.equals("formats")) {
			String version = data.substring(9, data.length() - 1);			
			useHandler(handlers.get(version));
		}
		super.processingInstruction(target, data);
	}
		
	private final void useHandler(FormatHandler xmlHandler) {
		IPaloStartHandler [] startHandlers = xmlHandler.getStartHandlers();
		for (int i = 0; i < startHandlers.length; ++i) {
			putStartHandler(startHandlers[i].getPath(), startHandlers[i]);
		}			
		IPaloEndHandler [] endHandlers = xmlHandler.getEndHandlers();
		for (int i = 0; i < endHandlers.length; ++i) {
			putEndHandler(endHandlers[i].getPath(), endHandlers[i]);
		}
	}
}

