/*
*
* @file PropertyHandler.java
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
* @version $Id: PropertyHandler.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.io.xml;

import java.util.Stack;

import org.palo.api.PaloAPIException;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.Property;
import org.palo.viewapi.internal.util.XMLUtil;
import org.xml.sax.Attributes;

/**
 * <code>PropertyHandler</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: PropertyHandler.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public class PropertyHandler implements IXMLHandler {

	public static final String XPATH = "/view/property";
		
	private Property <Object> property;
	private final CubeView view;
	private final Stack <Property<Object>> parentProperties;
	
	public PropertyHandler(CubeView view) {
		this.view = view;
		parentProperties = new Stack<Property<Object>>();
	}
	
	public void enter(String path, Attributes attributes) {
		if (property == null && path.equals(XPATH)) {
			// required property attributes:
			String id = attributes.getValue("id");
			if (id == null || id.equals("")) {
				throw new PaloAPIException("PropertyHandler: no property id defined!");
			}
			
			String value = attributes.getValue("value");
			if (value == null) {
				throw new PaloAPIException("PropertyHandler: no property value specified!");
			}
			
			// add property to view...
			property = view.addProperty(id, value);
			parentProperties.push(property);
		} else if (path.startsWith(XPATH) && path.endsWith("property")) {
			if (property == null) {
				throw new PaloAPIException("PropertyHandler: no property created!");
			}
			// required property attributes:
			String id = attributes.getValue("id");
			if (id == null || id.equals("")) {
				throw new PaloAPIException("PropertyHandler: no property id defined!");
			}
			
			String value = attributes.getValue("value");
			if (value == null) {
				throw new PaloAPIException("PropertyHandler: no property value specified!");
			}

			Property <Object> prop = new Property <Object> (
					parentProperties.peek(), id, value);			
			parentProperties.push(prop);
		}
	}

	public String getXPath() {
		return XPATH;
	}

	public void leave(String path, String value) {
		parentProperties.pop();
		if (property == null) {
			throw new PaloAPIException("PropertyHandler: no property created!");
		}
	}	    
	
	public static final String getPersistenceString(Property <Object> property) {
		StringBuffer xml = new StringBuffer();
		xml.append("<property"); //$NON-NLS-1$
		xml.append(" id=\"" + XMLUtil.printQuoted(property.getId()) + "\""); //$NON-NLS-1$
		xml.append(" value=\"" + XMLUtil.printQuoted(property.getValue().toString()) + "\">\r\n");
		for (Property <Object> prop: property.getChildren()) {
			xml.append(getPersistenceString(prop));
		}
		xml.append("</property>");
		return xml.toString();
	}
}
