/*
*
* @file XElementFactory.java
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
* @version $Id: XElementFactory.java,v 1.5 2010/04/15 09:54:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview;

import org.palo.api.Element;
import org.palo.api.ElementNode;

import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementType;

/**
 * <code>XElementFactory</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XElementFactory.java,v 1.5 2010/04/15 09:54:49 PhilippBouillon Exp $
 **/
public class XElementFactory {

	public static final XElementNode createX(ElementNode elementNode, String axisHierarchyId, String viewId) {
		Element element = elementNode.getElement();
		XElement xElement = createX(element);
		XElementNode xElNode = new XElementNode(xElement, axisHierarchyId, viewId);
		xElNode.setName(elementNode.getName());
		xElNode.setHasChildren(elementNode.hasChildren());
		return xElNode;
	}
	
	public static final XElement createX(Element element) {
		if (element == null) {
			return null;
		}
		XElement xElement = new XElement(element.getId(), element.getName(),
				XElementType.fromString(element.getTypeAsString()));
		xElement.setDepth(element.getDepth());
		xElement.setHasChildren(element.getChildCount() > 0);
		return xElement;
	}

}
