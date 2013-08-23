/*
*
* @file XAxis2.java
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
* @version $Id: XAxis2.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.core.client.models.cubeviews;

import java.util.ArrayList;
import java.util.List;

import com.tensegrity.palo.gwt.core.client.models.XPaloObject;

public class XAxis2 extends XPaloObject {
	
	public static final String TYPE = XAxis2.class.getName();

	private XView view;
//	private List<String> axisHierarchyIDs = new ArrayList<String>();
	private List<XAxisHierarchy> xAxisHierarchies = new ArrayList<XAxisHierarchy>();

	/* required for serialization */
	public XAxis2() {
	}

	public XAxis2(String id, String name, XView view) {
		setId(id);
		setName(name);
		this.view = view;
	}

	public final void add(XAxisHierarchy xAxisHierarchy) {
		if(!xAxisHierarchies.contains(xAxisHierarchy))
			xAxisHierarchies.add(xAxisHierarchy);
	}
	public final List<XAxisHierarchy> getAxisHierarchies() {
		return xAxisHierarchies;
	}
	public final void clear() {
		xAxisHierarchies.clear();
	}
//	public final void add(String axisHierarchyId) {
//		if(!axisHierarchyIDs.contains(axisHierarchyId)) {
//			axisHierarchyIDs.add(axisHierarchyId);
//		}
//	}
//	
//	public final List<String> getAxisHierarchyIDs() {
//		return axisHierarchyIDs;
//	}
//	
//	public final void clear() {
//		axisHierarchyIDs.clear();
//	}
	
	public final String getType() {
		return TYPE;
	}
	
//	public final String getViewId() {
//		return viewId;
//	}
	public final XView getView() {
		return view;
	}

	public String getAccountId() {
		return view.getAccountId();
	}

	public String getDatabaseId() {
		return view.getDatabaseId();
	}
}
