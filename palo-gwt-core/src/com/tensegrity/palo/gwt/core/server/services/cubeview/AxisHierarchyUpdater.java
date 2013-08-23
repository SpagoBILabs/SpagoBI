/*
*
* @file AxisHierarchyUpdater.java
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
* @version $Id: AxisHierarchyUpdater.java,v 1.20 2010/04/15 09:54:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview;

import org.palo.api.Attribute;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.SubsetHandler;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.LocalFilter;
import org.palo.viewapi.Property;
import org.palo.viewapi.internal.LocalFilterImpl;
import org.palo.viewapi.internal.VirtualElementImpl;

import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAlias;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementType;
import com.tensegrity.palo.gwt.core.client.models.subsets.XSubset;
import com.tensegrity.palo.gwt.core.client.models.subsets.XSubsetType;

/**
 * <code>AxisHierarchyUpdater</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AxisHierarchyUpdater.java,v 1.20 2010/04/15 09:54:49 PhilippBouillon Exp $
 **/
public class AxisHierarchyUpdater {

	static XElement update(AxisHierarchy axisHierarchy, XAxisHierarchy xAxisHierarchy) {
		updateAlias(axisHierarchy, xAxisHierarchy.getActiveAlias());
		updateSubset(axisHierarchy, xAxisHierarchy.getActiveSubset());
		updateLocalFilter(axisHierarchy, xAxisHierarchy.getVisibleElements());
		if (xAxisHierarchy.getSelectedElement() == null) {
			ElementNode [] nodes = axisHierarchy.getRootNodes();
			if (nodes != null && nodes.length > 0 && nodes[0] != null) {
				axisHierarchy.clearSelectedElements();
				axisHierarchy.addSelectedElement(nodes[0].getElement());
				XElement x = new XElement(nodes[0].getElement().getId(),
						nodes[0].getElement().getName(),
						XElementType.fromString(nodes[0].getElement().getTypeAsString()));
				xAxisHierarchy.setSelectedElement(x);
			}			
		}
		updateSelectedElement(axisHierarchy, xAxisHierarchy.getSelectedElement());
		updateNativeProperties(axisHierarchy, xAxisHierarchy);		
		XElement elem = setAliasForSelectedElement(axisHierarchy, xAxisHierarchy.getSelectedElement());
		return elem;
	}
	
	private static final void updateAlias(AxisHierarchy hierarchy, XAlias xAlias) {
		Property<?> aliasProperty = hierarchy.getProperty(AxisHierarchy.USE_ALIAS);		
		if(xAlias != null) {
			//get the alias attribute:
			Attribute alias = hierarchy.getHierarchy().getAttribute(xAlias.getId());
			if(alias != null) {
				aliasProperty = new Property<Attribute>(AxisHierarchy.USE_ALIAS, alias);
				hierarchy.addProperty(aliasProperty);
			}			
		} else if(aliasProperty != null)
			hierarchy.removeProperty(aliasProperty);
	}
	private static void updateSubset(AxisHierarchy hierarchy, XSubset subset) {
		if (subset != null) {
			SubsetHandler subsetHandler = 
						hierarchy.getHierarchy().getSubsetHandler();
			int type = subset.getSubsetType() == XSubsetType.LOCAL ? 
								Subset2.TYPE_LOCAL : Subset2.TYPE_GLOBAL;
			Subset2 nativeSubset = subsetHandler.getSubset(subset.getId(), type);
			hierarchy.setSubset(nativeSubset);
		} else
			hierarchy.setSubset(null);
	}

	private static final void updateLocalFilter(AxisHierarchy axisHierarchy, XElementNode[] visibleElements) {
		if(visibleElements == null)
			axisHierarchy.setLocalFilter(null);
		else {
			Hierarchy hierarchy = axisHierarchy.getHierarchy();
			LocalFilter filter = new LocalFilterImpl();
			for (XElementNode xElement : visibleElements) {
				ElementNode elementNode = createElementNode(xElement, hierarchy);
				filter.addVisibleElement(elementNode);
			}
			axisHierarchy.setLocalFilter(filter);
		}
	}
	private static final ElementNode createElementNode(XElementNode xElNode,
			Hierarchy hierarchy) {
		Element element = null;
		XElement xElement = xElNode.getElement();
		if(xElement.getElementType().equals(XElementType.VIRTUAL)) {
			element = new VirtualElementImpl(xElement.getName(), hierarchy);
		}else
			element = hierarchy.getElementById(xElNode.getElement().getId());
		ElementNode node = new ElementNode(element);
		addChildren(node, xElNode, hierarchy);
		return node;
	}
	private static final void addChildren(ElementNode parent, XElementNode xParent, Hierarchy hierarchy) {
		for(XElementNode xElNode : xParent.getChildren()) {
			ElementNode node = createElementNode(xElNode, hierarchy);
			parent.forceAddChild(node);
		}
	}
	
	private static final String findElementName(ElementNode [] roots, String id) {
		if (roots == null) {
			return null;
		}
		for (ElementNode n: roots) {
			if (n.getElement() != null && n.getElement().getId().equals(id)) {
				return n.getName();
			}
			String res = findElementName(n.getChildren(), id);
			if (res != null) {
				return res;
			}
		}
		return null;
	}
	
	private static final void updateSelectedElement(AxisHierarchy hierarchy, XElement selectedElement) {
		Element element = selectedElement == null ? null : hierarchy.getHierarchy().getElementById(selectedElement.getId());
		if(element == null || !hierarchy.contains(element))
			element = resetSelectedElement(hierarchy);
		hierarchy.clearSelectedElements();
		hierarchy.addSelectedElement(element);
	}
		
	private static final XElement setAliasForSelectedElement(AxisHierarchy hierarchy, XElement element) {
		if (element != null) {
			String name = hierarchy.getElementNameFor(element.getId());
				//findElementName(hierarchy.getRootNodes(), element.getId());
			Property p = hierarchy.getProperty("aliasFormat");
			if (p == null || p.getValue() == null) {
				if (name != null) {
					element.setName(name);
				}				
			} else {
				String format = p.getValue().toString();
				String result = hierarchy.getHierarchy().getElementById(element.getId()).getName();
				if (name != null) {
					if (format.equals("aliasFormat")) {
						result = name;
					} else if (format.equals("elementName")) {
						// result already is element name;
					} else if (format.equals("elementNameDashAlias")) {
						result = result + " - " + name;
					} else if (format.equals("aliasDashElementName")) {
						result = name + " - " + result;
					} else if (format.equals("elementNameParenAlias")) {
						result = result + " (" + name + ")";
					} else if (format.equals("aliasParenElementName")) {
						result = name + " (" + result + ")";
					} else if (format.equals("elementNameAlias")) {
						result = result + " " + name;
					} else if (format.equals("aliasElementName")) {
						result = name + " " + result;
					} 
				}
				element.setName(result);
			}
		}	
		return element;
	}
	
	private static final Element resetSelectedElement(AxisHierarchy hierarchy) {
		ElementNode[] roots = hierarchy.getRootNodes();
		if(roots.length > 0)
			return roots[0].getElement();
		return null;
	}

	private static final void updateNativeProperties(AxisHierarchy axis, XAxisHierarchy xAxis) {
		for (Property prop: axis.getProperties()) {
			if (!prop.getId().equals(AxisHierarchy.USE_ALIAS)) {
				axis.removeProperty(prop);
			}
		}
		for(String id : xAxis.getPropertyIDs()) {
			if (!id.equals(AxisHierarchy.USE_ALIAS)) {
				String propValue = xAxis.getProperty(id);
				Property<String> property = new Property<String>(id, propValue);
				axis.addProperty(property);
			}
		}
	}	
}
