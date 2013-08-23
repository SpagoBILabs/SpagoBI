/*
*
* @file AxisHierarchyImpl.java
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
* @version $Id: AxisHierarchyImpl.java,v 1.12 2010/04/15 09:54:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.cubeview;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.palo.api.Attribute;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.subsets.Subset2;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.LocalFilter;
import org.palo.viewapi.Property;

/**
 * <code>AxisHierarchyImpl</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AxisHierarchyImpl.java,v 1.12 2010/04/15 09:54:49 PhilippBouillon Exp $
 **/
class AxisHierarchyImpl implements AxisHierarchy {

	private Axis axis;
	private final Hierarchy hierarchy;
	private final LinkedHashSet<Element> selectedElements;
	private final HashMap<String, Property<?>> properties;
	private final AxisHierarchyFilter filter;
	private String subsetMissing = null;
	private String aliasMissing = null;
	
	AxisHierarchyImpl(Hierarchy hierarchy, Axis axis) {
		this.axis = axis;
		this.hierarchy = hierarchy;
		this.filter = new AxisHierarchyFilter(hierarchy);
		this.properties = new HashMap<String, Property<?>>();
		this.selectedElements = new LinkedHashSet<Element>();
	}
	
	public final Axis getAxis() {
		return axis;
	}
	
	public final void setAxis(Axis axis) {
		this.axis = axis;
	}
	public final void addProperty(Property<?> property) {
		//is it an alias?
		if(property.getId().equals(USE_ALIAS))
			filter.setAlias((Attribute)property.getValue());
		properties.put(property.getId(), property);
	}

	public final void addSelectedElement(Element element) {
		selectedElements.add(element);
	}

	public final Hierarchy getHierarchy() {
		return hierarchy;
	}

	public final Property<?> getProperty(String id) {
		return properties.get(id);
	}
	
	public final Property<?>[] getProperties() {
		return properties.values().toArray(new Property[0]);
	}

	public final Element[] getSelectedElements() {
		return selectedElements.toArray(new Element[0]);
	}

	public final Subset2 getSubset() {
		return filter.getSubset();
	}

	public final boolean hasSelectedElements() {
		if (selectedElements.isEmpty()) {
			return false;
		}
		for (Element e: selectedElements) {
			if (e != null) {
				return true;
			}
		}
		return false;
	}
	
	public final void removeProperty(Property<?> property) {
		if(property.getId().equals(USE_ALIAS))
			filter.setAlias(null);
		properties.remove(property.getId());
	}

	public final void removeSelectedElement(Element element) {
		selectedElements.remove(element);
	}

	public final void clearSelectedElements() {
		selectedElements.clear();
	}
	
	public final void setSubset(Subset2 subset) {
		filter.setSubset(subset);
	}
	
	public final ElementNode[] getRootNodes() {
		List<ElementNode> roots = filter.applyFilters(getProperty("aliasFormat"));
		return roots.toArray(new ElementNode[0]);
	}
			
	public final LocalFilter getLocalFilter() {
		return filter.getLocalFilter();
	}

	public void setLocalFilter(LocalFilter localFilter) {
		filter.setLocalFilter(localFilter);
	}
	public final String getElementNameFor(String elementID) {		
		return filter.getAliasFor(elementID);
	}
	public final boolean contains(Element element) {
		return filter.contains(element);
	}

	public String getSubsetMissing() {
		return subsetMissing;
	}

	public void setSubsetMissing(String id) {
		subsetMissing = id;
	}
	
	public String getAliasMissing() {
		return aliasMissing;
	}

	public void setAliasMissing(String id) {
		aliasMissing = id;
	}	
}
