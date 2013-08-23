/*
*
* @file AxisImpl.java
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
* @version $Id: AxisImpl.java,v 1.12 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.cubeview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.palo.api.Hierarchy;
import org.palo.api.utils.ElementPath;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.Property;
import org.palo.viewapi.View;

/**
 * <code>AxisImpl</code>
 * Default implementation of the {@link Axis} interface.
 *
 * @version $Id: AxisImpl.java,v 1.12 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
class AxisImpl implements Axis {
	
	private View view;
	private String name;
	private final String id;	
	private final Set<ElementPath> visiblePaths = new HashSet<ElementPath>();
	private final List<ElementPath> expandedPaths = new ArrayList<ElementPath>();
	private final HashMap<String, Property<?>> properties = new HashMap<String, Property<?>>();
	private final LinkedHashMap<Hierarchy, AxisHierarchy> hierarchies = new LinkedHashMap<Hierarchy, AxisHierarchy>();
//	private final LinkedHashMap<String, AxisHierarchy> hierarchies = new LinkedHashMap<String, AxisHierarchy>();
	
	AxisImpl(String id, String name, View view) {
		this.id = id;
		this.name = name;
		this.view = view;
	}

	private AxisImpl(AxisImpl axis) {
		this.id = axis.id;
		this.name = axis.name;
		this.view = axis.view;
		this.hierarchies.putAll(axis.hierarchies);
		for(ElementPath path : axis.visiblePaths)
			this.visiblePaths.add(path.copy());
		for(ElementPath path : axis.expandedPaths)
			this.expandedPaths.add(path.copy());
		//TODO deep copy properties...
		this.properties.putAll(axis.properties);
	}
	
	public final String getId() {
		return id;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final View getView() {
		return view;
	}
	final void setView(View view) {
		this.view = view;
	}
	
	public final Axis copy() {
		return new AxisImpl(this);
	}

	public final AxisHierarchy add(Hierarchy hierarchy) {
		AxisHierarchy axisHierarchy = null;
		if(hierarchy != null) {
			axisHierarchy = new AxisHierarchyImpl(hierarchy, this);
			hierarchies.put(axisHierarchy.getHierarchy(), axisHierarchy);
		}			
		return axisHierarchy;
	}
	public final void add(AxisHierarchy axisHierarchy) {
		if(axisHierarchy == null || axisHierarchy.getHierarchy() == null)
			return;
		axisHierarchy.setAxis(this);
		hierarchies.put(axisHierarchy.getHierarchy(), axisHierarchy);
	}

	public final void remove(AxisHierarchy axisHierarchy) {
		axisHierarchy.setAxis(null);		
		hierarchies.remove(axisHierarchy.getHierarchy());
	}
	public final AxisHierarchy getAxisHierarchy(Hierarchy hierarchy) {
		return hierarchies.get(hierarchy);
	}
	
	public final AxisHierarchy getAxisHierarchy(String id) {
		for(AxisHierarchy hierarchy : hierarchies.values()) {
			if(hierarchy.getHierarchy().getId().equals(id))
				return hierarchy;
		}
		return null;
	}	

	public final AxisHierarchy[] getAxisHierarchies() {
		return hierarchies.values().toArray(new AxisHierarchy[0]);
	}
	
	public final Hierarchy[] getHierarchies() {
		return hierarchies.keySet().toArray(new Hierarchy[0]);
	}

	public final AxisHierarchy remove(Hierarchy hierarchy) {
		AxisHierarchy axisHierarchy = hierarchies.remove(hierarchy);
		if(axisHierarchy != null)
			axisHierarchy.setAxis(null);
		return axisHierarchy;
	}

	public final void removeAll() {
		for(AxisHierarchy axisHierarchy : hierarchies.values())
			axisHierarchy.setAxis(null);
		hierarchies.clear();
		//without any hierarchies the visible and expanded paths are cleared too
		visiblePaths.clear();
		expandedPaths.clear();
	}

	
	public final void addExpanded(ElementPath path) {
		expandedPaths.add(path);
	}

	public final void addExpanded(ElementPath[] paths) {
		expandedPaths.addAll(Arrays.asList(paths));		
	}

	public final ElementPath[] getExpandedPaths() {
		return expandedPaths.toArray(new ElementPath[0]);
	}
	
	public final void removeExpanded(ElementPath path) {
		expandedPaths.remove(path);
	}

	public final void removeAllExpandedPaths() {
		expandedPaths.clear();
	}

	public final void addVisible(ElementPath path) {
		visiblePaths.add(path);
		
	}

	public final ElementPath[] getVisiblePaths() {		
		return visiblePaths.toArray(new ElementPath[0]);
	}

	public final boolean isVisible(ElementPath path) {
		return visiblePaths.contains(path);
	}

	public final void removeVisible(ElementPath path) {
		visiblePaths.remove(path);
	}

	public final void removeAllVisiblePaths() {
		visiblePaths.clear();
	}
	
	public final void addProperty(Property<?> property) {
		properties.put(property.getId(), property);
	}

	public final Property<?> getProperty(String id) {
		return properties.get(id);
	}
	
	public final Property<?>[] getProperties() {
		return properties.values().toArray(new Property[0]);
	}

	public final void removeProperty(Property<?> property) {
		properties.remove(property.getId());
	}

	public final void removeAllProperties() {
		properties.clear();
	}
	
	public final boolean equals(Object obj) {
		if(obj instanceof Axis) {
			return id.equals(((Axis)obj).getId());
		}
		return false;
	}
	
	public final int hashCode() {
		int hc = 23;
		hc += 37 * id.hashCode();
		return hc; 
	}
}
