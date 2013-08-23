/*
*
* @file XAxisHierarchy.java
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
* @version $Id: XAxisHierarchy.java,v 1.11 2010/02/12 13:50:49 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.core.client.models.cubeviews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.palo.gwt.core.client.models.subsets.XSubset;

public class XAxisHierarchy extends XObject {

	private int maxDepth;
	private XElement selectedElement;
	private XSubset activeSubset;
	private List<XSubset> subsets = new ArrayList<XSubset>();
	private XAlias activeAlias;
	private List<XAlias> aliases = new ArrayList<XAlias>();
	private XElementNode[] visibleElements;
	private XElementNode[] oldVisibleElements;
	private XElement [] localFilter;
	private Map<String, String> properties = new HashMap<String, String>();
	private String axisId;	
	private String viewId;
	
	/* required for serialization */
	public XAxisHierarchy() {
	}

	public XAxisHierarchy(String id, String name, String axisId, String viewId) {
		setId(id);
		setName(name);
		this.axisId = axisId;
		this.viewId = viewId;
	}

	public void setAxisId(String axisId, String viewId) {
		this.axisId = axisId;
		this.viewId = viewId;
	}
	
	public final String getType() {
		return getClass().getName();
	}

	public final void setMaxDepth(int depth) {
		maxDepth = depth;
	}
	public final int getMaxDepth() {
		return maxDepth;
	}
	
	public final void setSelectedElement(XElement selectedElement) {
		this.selectedElement = selectedElement;
	}
	public final XElement getSelectedElement() {
		return selectedElement;
	}
	
	public final String getAxisId() {
		return axisId;
	}
	
	public final String getViewId() {
		return viewId;
	}
	
	public final void removeAllSubsets() {
		subsets.clear();
	}
	public final void addSubset(XSubset subset) {
		if(!subsets.contains(subset))
			subsets.add(subset);
	}
	
	public final List<XSubset> getSubsets() {
		return subsets;
	}
	
	public final void setActiveSubset(XSubset subset) {
		activeSubset = subset;
	}
	
	public final XSubset getActiveSubset() {
		return activeSubset;
	}
	
	public final void removeAllAliases() {
		aliases.clear();
	}
	public final void addAlias(XAlias alias) {
		if(!aliases.contains(alias))
			aliases.add(alias);
	}
	
	public final List<XAlias> getAliases() {
		return aliases;
	}
	
	public final void setActiveAlias(XAlias alias) {
		activeAlias = alias;
	}
	
	public final XAlias getActiveAlias() {
		return activeAlias;
	}

	public final XElementNode[] getVisibleElements() {
		return visibleElements;
	}

	public final void setVisibleElements(XElementNode[] elements) {
		this.visibleElements = elements;
	}
	
	public final void setOldVisibleElements(XElementNode [] elements) {
		if (elements == null) {
			oldVisibleElements = null;
		} else {
			oldVisibleElements = new XElementNode[0];
		}
	}
	
	public final XElementNode [] getOldVisibleElements() {
		return oldVisibleElements;
	}
	
	public final XElement [] getHierarchyLocalFilter() {
		return localFilter;
	}
	
	public final void setHierarchyLocalFilter(XElement [] localFilter) {
		this.localFilter = localFilter;
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
