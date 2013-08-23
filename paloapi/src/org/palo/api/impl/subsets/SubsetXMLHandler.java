/*
*
* @file SubsetXMLHandler.java
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
* @author ArndHouben
*
* @version $Id: SubsetXMLHandler.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl.subsets;

import java.util.HashMap;

import org.palo.api.Database;
import org.palo.api.Subset;
import org.palo.api.impl.xml.BaseXMLHandler;
import org.palo.api.impl.xml.IPaloEndHandler;
import org.palo.api.impl.xml.IPaloStartHandler;
import org.xml.sax.SAXException;

/**
 * The <code>SubsetXMLHandler</code> is an extension of the 
 * <code>{@link BaseXMLHandler}</code> to support multiple subset versions.
 * 
 * @author ArndHouben
 * @version $Id: SubsetXMLHandler.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
class SubsetXMLHandler extends BaseXMLHandler {

	private final HashMap handlers = new HashMap();
	private XMLSubsetHandler xmlHandler;
	
	SubsetXMLHandler(Database database,String key) {
		handlers.put("legacy", new XMLSubsetHandlerLegacy(database,key));
		handlers.put("1.0", new XMLSubsetHandler1_0(database));
		handlers.put("1.1", new XMLSubsetHandler1_1(database));
	}

	//here we decide which handler to use...
	public final void processingInstruction(String target, String data)
			throws SAXException {
		if (target.equals("palosubset")) {
			String version = data.substring(9, data.length() - 1).trim();
			useHandler((XMLSubsetHandler) handlers.get(version));
		}
		super.processingInstruction(target, data);
	}

	/**
	 * Will use the legacy subset xml handler
	 */
	final void useLegacy() {
		clearAllHandlers();
		useHandler((XMLSubsetHandler)handlers.get("legacy"));
	}

	/**
	 * Returns the read in subset
	 * @return
	 */
	final Subset getSubset() {
		return xmlHandler.getSubset();
	}
	
	private final void useHandler(XMLSubsetHandler xmlHandler) {
		this.xmlHandler = xmlHandler;
		IPaloStartHandler[] startHandlers = xmlHandler.getStartHandlers();
		for(int i=0;i<startHandlers.length;++i)
			putStartHandler(startHandlers[i].getPath(), startHandlers[i]);
		IPaloEndHandler[] endHandlers = xmlHandler.getEndHandlers();
		for(int i=0;i<endHandlers.length;++i)
			putEndHandler(endHandlers[i].getPath(), endHandlers[i]);
	}	
}
