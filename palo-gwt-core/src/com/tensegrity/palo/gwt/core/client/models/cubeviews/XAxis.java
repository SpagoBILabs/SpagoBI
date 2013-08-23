/*
*
* @file XAxis.java
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
* @version $Id: XAxis.java,v 1.9 2010/03/11 10:42:20 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.core.client.models.cubeviews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tensegrity.palo.gwt.core.client.models.XObject;

public class XAxis extends XObject {
	
	//TODO how to convert nicely between Axis and XAxis property IDs?? FOR LATER!!
	public static final String PREFERRED_WIDTH = "com.tensegrity.palo.cubview.axis.preferredwidth";
	
	public static final String TYPE = XAxis.class.getName();

	private String viewId;
	private List<XAxisHierarchy> xAxisHierarchies = new ArrayList<XAxisHierarchy>();
//	private List<XAxisItem> expandedItems = new ArrayList<XAxisItem>();
	private XAxisItem[] expandedItems;
	private Map<String, String> properties = new HashMap<String, String>();

	/* required for serialization */
	public XAxis() {
	}

	public XAxis(String id, String name, String viewId) {
		setId(id);
		setName(name);
		this.viewId = viewId;
	}

	public final String getViewId() {
		return viewId;
	}
	
	public final void setExpanded(XAxisItem[] items) {
		expandedItems = items;
	}
	public final XAxisItem[] getExpandedItems() {
		if (expandedItems == null) {
			return new XAxisItem[0];
		}
		return expandedItems;
	}
	
	public final void add(XAxisHierarchy xAxisHierarchy) {
		if(!xAxisHierarchies.contains(xAxisHierarchy))
			xAxisHierarchies.add(xAxisHierarchy);
	}
	public final List<XAxisHierarchy> getAxisHierarchies() {
		return xAxisHierarchies;
	}
	
	public final XAxisHierarchy getAxisHierarchy(String id) {
		for (XAxisHierarchy h: xAxisHierarchies) {
			if (h.getId().equals(id)) {
				return h;
			}
		}
		return null;
	}
	public final void clear() {
		xAxisHierarchies.clear();
	}
	public final String getType() {
		return TYPE;
	}
	
	public final void addProperty(String id, String value) {
		properties.put(id, value);
	}
	public final String getProperty(String id) {
		return properties.get(id);
	}
	public final String[] getPropertyIDs() {
		return properties.keySet().toArray(new String[0]);
	}
	public final void removeProperty(String id) {
		properties.remove(id);		
	}
	public final void removeAllProperties() {
		properties.clear();
	}
}
