/*
*
* @file AxisConverter.java
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
* @version $Id: AxisConverter.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.converter.cubeviews;

import java.util.ArrayList;
import java.util.List;

import org.palo.api.Hierarchy;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.services.ServiceProvider;
import org.palo.viewapi.services.ViewService;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.XPaloObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxis2;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.server.converter.PaloObjectConverter;
import com.tensegrity.palo.gwt.core.server.converter.XConverter;

/**
 * <code>AxisConverter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AxisConverter.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public class AxisConverter extends PaloObjectConverter {

	protected Class<?> getNativeClass() {
		return Axis.class;
	}

	protected Class<?> getXObjectClass() {
		return XAxis2.class;
	}

	public Object toNative(XObject obj, AuthUser loggedInUser)
			throws OperationFailedException {
		return toNative((XPaloObject)obj, loggedInUser);
	}

	public Object toNative(XPaloObject paloObject, AuthUser loggedInUser)
			throws OperationFailedException {	
		XAxis2 xAxis = (XAxis2) paloObject;
		View view = getViewOf(xAxis, loggedInUser);
		Axis axis = view.getCubeView().getAxis(xAxis.getId());
		update(axis, xAxis);
		return axis;
	}
	
	private View getViewOf(XAxis2 xAxis, AuthUser user) {
		ViewService viewService = ServiceProvider.getViewService(user);
		return viewService.getView(xAxis.getView().getId());
	}

	private void update(Axis axis, XAxis2 xAxis) {
		axis.setName(xAxis.getName());
		List<String> hierarchyIDs = getHierarchyIDs(axis);
		List<String> xHierarchyIDs = getHierarchyIDs(xAxis);
		List<String> hierarchiesToRemove = new ArrayList<String>(hierarchyIDs);
		hierarchiesToRemove.removeAll(xHierarchyIDs);
		removeAll(hierarchiesToRemove, axis);
		List<String> hierarchiesToAdd = new ArrayList<String>(xHierarchyIDs);
		hierarchiesToAdd.removeAll(hierarchyIDs);
		addAll(hierarchiesToAdd, axis);
	}
	
	private List<String> getHierarchyIDs(Axis axis) {
		List<String> hierarchyIDs = new ArrayList<String>();
		for(Hierarchy hierarchy : axis.getHierarchies())
			hierarchyIDs.add(hierarchy.getId());
		return hierarchyIDs;
	}

	private List<String> getHierarchyIDs(XAxis2 xAxis) {
		List<String> hierarchyIDs = new ArrayList<String>();
		for(XAxisHierarchy hierarchy : xAxis.getAxisHierarchies())
			hierarchyIDs.add(hierarchy.getId());
		return hierarchyIDs;
	}

	private final void removeAll(List<String> hierarchies, Axis axis) {
		for(String id : hierarchies) {
			AxisHierarchy axisHierarchy = axis.getAxisHierarchy(id);
			axis.remove(axisHierarchy);
		}
	}
	
	private final void addAll(List<String> hierarchies, Axis axis) {
//TODO		
//		for(String id : hierarchies) {
//			Hierarchy hierarchy = database.get.getAxisHierarchy(id);
//			axis.add(axisHierarchy);
//		}
	}
	
	
	
	public XObject toXObject(Object nativeObj) {
		return toXObject(nativeObj, null);
	}

	public XPaloObject toXObject(Object nativeObj, String accountId) {
		assert(accountId != null);
		Axis axis = (Axis) nativeObj;
		XView xView = (XView) XConverter.createX(axis.getView());
		XAxis2 xAxis = new XAxis2(axis.getId(), axis.getName(), xView);
//		for(Hierarchy hierarchy : axis.getHierarchies())
//			xAxis.add(hierarchy.getId());
		return xAxis;
	}
}
