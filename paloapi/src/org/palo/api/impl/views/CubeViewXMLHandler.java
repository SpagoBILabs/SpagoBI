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
* @author ArndHouben
*
* @version $Id: CubeViewXMLHandler.java,v 1.10 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.palo.api.CubeView;
import org.palo.api.Database;
import org.palo.api.impl.PersistenceErrorImpl;
import org.palo.api.impl.xml.BaseXMLHandler;
import org.palo.api.impl.xml.IPaloEndHandler;
import org.palo.api.impl.xml.IPaloStartHandler;
import org.palo.api.persistence.PersistenceError;
import org.xml.sax.SAXException;

/**
 * The <code>CubeViewXMLHandler</code> is an extension of the 
 * <code>{@link BaseXMLHandler}</code> to support multiple cube view versions.
 * 
 * @author ArndHouben
 * @version $Id: CubeViewXMLHandler.java,v 1.10 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
class CubeViewXMLHandler extends BaseXMLHandler {
	
	private final HashMap handlers = new HashMap();
	private CubeViewHandler xmlHandler;
	private final ArrayList errors = new ArrayList();
	
	CubeViewXMLHandler(Database database, String key) {
		registerHandler(CubeViewHandler.LEGACY, 
				new CubeViewHandlerLegacy(database,key));
		registerHandler("1.0", new CubeViewHandler1_0(database));
		registerHandler("1.1", new CubeViewHandler1_1(database));
		registerHandler("1.2", new CubeViewHandler1_2(database));
		registerHandler("1.3", new CubeViewHandler1_3(database));
		registerHandler("1.4", new CubeViewHandler1_4(database));
	}
	
	final boolean hasErrors() {
		return (xmlHandler != null && xmlHandler.hasErrors()) || !errors.isEmpty();
	}
	
	final PersistenceError[] getErrors() {
		if (xmlHandler != null && xmlHandler.hasErrors())
			errors.addAll(Arrays.asList(xmlHandler.getErrors()));
		return (PersistenceError[]) errors.toArray(new PersistenceError[errors
				.size()]);
	}
	
	final void addViewError(PersistenceError error) {
		if(xmlHandler != null)
			xmlHandler.addError(error);
		else
			errors.add(error);
	}
	
	final void useLegacy() {
		clearAllHandlers();
		//use legacy handler:
		useHandler((CubeViewHandler)handlers.get(CubeViewHandler.LEGACY));
	}
	
	
	/**
	 * Registers the given <code>{@link CubeViewHandler}</code> for the 
	 * specified cube view version 
	 * @param version a valid cube view version
	 * @param handler a handler to use for reading cube views of specified version
	 */
	final void registerHandler(String version, CubeViewHandler handler) {
		handlers.put(version, handler);
	}
	
	
	public final void processingInstruction(String target, String data) throws SAXException {
		if(target.equals("paloview")) {
			String version = data.substring(9,data.length()-1);			
			CubeViewHandler handler = 
				(CubeViewHandler) handlers.get(version);
			// PR 7047: "backword" compatibility ;)
			if (handler == null && !version.equalsIgnoreCase("legacy")) {
				// Check if the major version number of the view equals
				// the major version number of the "highest" handler. If
				// so, try to load the view with that handler, otherwise,
				// the view version is incompatible.
				String [] versions = (String []) handlers.keySet().
					toArray(new String[0]);
				String max = versions[0];
				int maxMajor = Integer.parseInt(max.split("\\.")[0]);
				int maxMinor = Integer.parseInt(max.split("\\.")[1]);
				handler = (CubeViewHandler) handlers.get(max);
				for (String vers: versions) {
					if (vers.equals("legacy")) {
						continue;
					}
					int major = Integer.parseInt(vers.split("\\.")[0]);
					int minor = Integer.parseInt(vers.split("\\.")[1]);
					if (maxMajor < major || (maxMajor == major && maxMinor < minor)) {
						maxMajor = major;
						maxMinor = minor;
						handler = (CubeViewHandler) handlers.get(vers);
					}
				}				
				if (Integer.parseInt(version.split("\\.")[0]) > maxMajor) {
					handler = null;
				}
				
//				if (Integer.parseInt(max.split("\\.")[0]) >=
//						Integer.parseInt(version.split("\\.")[0])) {
//					handler = (CubeViewHandler) handlers.get(max);
//				}
			}			
			useHandler(handler);
//			xmlHandler = (ICubeViewHandler)handlers.get(version);
//			IPaloStartHandler[] startHandlers = xmlHandler.getStartHandlers();
//			for(int i=0;i<startHandlers.length;++i)
//				putStartHandler(startHandlers[i].getPath(), startHandlers[i]);
//			IPaloEndHandler[] endHandlers = xmlHandler.getEndHandlers();
//			for(int i=0;i<endHandlers.length;++i)
//				putEndHandler(endHandlers[i].getPath(), endHandlers[i]);
		}
		super.processingInstruction(target, data);
	}
	
	/**
	 * Returns the cube view which was read in by an appropriate handler before.
	 * @return the cube view or <code>null</code> 
	 */
	final CubeView getCubeView() {
		if(xmlHandler == null)
			return null;
		return xmlHandler.getCubeView();
	}

	private final void useHandler(CubeViewHandler xmlHandler) {
		if(xmlHandler == null) {
			throw new RuntimeException("Unsupported palo view version!");
		}
		this.xmlHandler = xmlHandler;
		IPaloStartHandler[] startHandlers = xmlHandler.getStartHandlers();
		for(int i=0;i<startHandlers.length;++i)
			putStartHandler(startHandlers[i].getPath(), startHandlers[i]);
		IPaloEndHandler[] endHandlers = xmlHandler.getEndHandlers();
		for(int i=0;i<endHandlers.length;++i)
			putEndHandler(endHandlers[i].getPath(), endHandlers[i]);
	}
}

