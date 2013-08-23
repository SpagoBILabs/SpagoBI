/*
*
* @file XAxisFactory.java
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
* @version $Id: XAxisFactory.java,v 1.5 2010/03/02 08:58:27 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview;

import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.Property;

import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxis;
import com.tensegrity.palo.gwt.core.server.services.UserSession;

/**
 * <code>XAxisFactory</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XAxisFactory.java,v 1.5 2010/03/02 08:58:27 PhilippBouillon Exp $
 **/
public class XAxisFactory {

	static XAxis createX(Axis axis, String viewId, UserSession userSession) {
		XAxis xAxis = new XAxis(axis.getId(), axis.getName(), viewId);
		for(AxisHierarchy axisHierarchy : axis.getAxisHierarchies())
			xAxis.add(XAxisHierarchyFactory.createX(axisHierarchy, xAxis, userSession));
		setPropertiesFor(xAxis, axis);
		return xAxis;
	}
	private static void setPropertiesFor(XAxis xAxis, Axis fromAxis) {
		for(Property<?> property : fromAxis.getProperties())
			xAxis.addProperty(property.getId(), property.getValue().toString());
	}
}
