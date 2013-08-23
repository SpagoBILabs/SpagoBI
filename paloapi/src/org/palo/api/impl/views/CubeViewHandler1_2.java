/*
*
* @file CubeViewHandler1_2.java
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
* @version $Id: CubeViewHandler1_2.java,v 1.3 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl.views;

import org.palo.api.Database;
import org.palo.api.impl.xml.EndHandler;
import org.palo.api.impl.xml.IPaloStartHandler;
import org.palo.api.impl.xml.StartHandler;
import org.xml.sax.Attributes;

/**
 * <code>CubeViewHandler1_2</code>
 * Defines <code>{@link StartHandler}</code>s and 
 * <code>{@link EndHandler}</code>s to read cube views which are stored using
 * version 1.2
 *
 * @author Philipp Bouillon
 * @version $Id: CubeViewHandler1_2.java,v 1.3 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
class CubeViewHandler1_2 extends CubeViewHandler1_1 {
    /**
     * Creates a new instance of the <code>CubeViewHandler1_2</code> operating
     * on the specified <code>Database</code>.
     * 
     * @param database the <code>Database</code> for which CubeViews are to be
     * loaded.
     */
	CubeViewHandler1_2(Database database) {
    	super(database);
    }
    
    /**
     * Adds the "viw/property" path of the xml description to the loader.
     * Any CubeView can be attributed with any number of arbitrary key/value
     * properties. Those properties are stored in xml files for views starting
     * with version 1.2 and are read in here.
     */
	protected void registerStartHandlers() {
    	super.registerStartHandlers();
    	    	
    	// add property handling:
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/property";
			}

			public void startElement(String uri, String localName, String qName, Attributes attributes) {
				try {
					String id = attributes.getValue("id");
					String value = attributes.getValue("value");	
					cubeView.addProperty(id, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
