/*
*
* @file CubeViewReader.java
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
* @author Stepan Rutz
*
* @version $Id: CubeViewReader.java,v 1.19 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.api.impl.views;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.palo.api.CubeView;
import org.palo.api.Database;
import org.palo.api.impl.PersistenceErrorImpl;
import org.palo.api.persistence.PersistenceError;

/**
 * <code>CubeQueryReader</code>, reads cube views from xml.
 *
 * @author Stepan Rutz
 * @author Arnd Houben
 * @version $Id: CubeViewReader.java,v 1.19 2009/04/29 10:21:58 PhilippBouillon Exp $
 */
class CubeViewReader {
    //--------------------------------------------------------------------------
	// FACTORY
	//
	private static CubeViewReader instance = new CubeViewReader();
    static CubeViewReader getInstance() {
		return instance;
	}

    //--------------------------------------------------------------------------
    // INSTANCE
    //
    private CubeViewReader() {
	}
    
    CubeView fromXML(InputStream input, String viewId,
			Database database, Collection errors) {
//			throws PaloPersistenceException {
		CubeViewXMLHandler xmlHandler = new CubeViewXMLHandler(database, viewId);
		// check legacy connection:
		if (database.getConnection().isLegacy())
			xmlHandler.useLegacy();
		try {
			SAXParserFactory sF = SAXParserFactory.newInstance();
			SAXParser parser = sF.newSAXParser();
			parser.parse(input, xmlHandler);
		} catch (Exception e) {
			// System.err.println("failed to load view: "+viewId);
			xmlHandler.addViewError(new PersistenceErrorImpl(
					"Failed to load cube view", viewId, null, database, viewId,
					PersistenceError.LOADING_FAILED, null,
					PersistenceError.TARGET_GENERAL));
		} 
//		finally {
//			if (xmlHandler.hasErrors()) {
//				throw new PaloPersistenceException(xmlHandler.getErrors(),
//						"Errors during cube view loading!!");
//			}
//		}
		if(xmlHandler.hasErrors())
			errors.addAll(Arrays.asList(xmlHandler.getErrors()));
		return xmlHandler.getCubeView();
	}
    
    static final String getLeafName(String name) {
    	int cutIt = name.indexOf("@@");
    	if(cutIt > 0)
    		name = name.substring(0, cutIt);
    	return name;
    }

    static final int[] getRepetitions(String reps) {
    	if(reps==null)
    		return new int[]{0};
    	String[] _reps = reps.split(",");
    	int[] repetitions = new int[_reps.length];
    	for(int i=0;i<_reps.length;++i) {
    		repetitions[i] = Integer.parseInt(_reps[i]);
    	}
    	return repetitions;
    }

}
