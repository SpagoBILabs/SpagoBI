/*
*
* @file XAxisHierarchyFactory.java
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
* @version $Id: XAxisHierarchyFactory.java,v 1.13 2010/03/12 12:49:14 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview;

import java.util.ArrayList;
import java.util.List;

import org.palo.api.Attribute;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.SubsetHandler;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.LocalFilter;
import org.palo.viewapi.Property;

import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAlias;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxis;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.palo.gwt.core.client.models.subsets.XSubset;
import com.tensegrity.palo.gwt.core.server.services.UserSession;

/**
 * <code>XAxisHierarchyFactory</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XAxisHierarchyFactory.java,v 1.13 2010/03/12 12:49:14 PhilippBouillon Exp $
 **/
public class XAxisHierarchyFactory {

	static XAxisHierarchy createX(AxisHierarchy axisHierarchy, XAxis xAxis, UserSession userSession) {
		Hierarchy hierarchy = axisHierarchy.getHierarchy();
		XAxisHierarchy xAxisHierarchy = new XAxisHierarchy(hierarchy.getId(),
				hierarchy.getName(), xAxis.getId(), xAxis.getViewId());
		updateX(xAxisHierarchy, axisHierarchy, userSession);
		setPropertiesFor(xAxisHierarchy, axisHierarchy);
		return xAxisHierarchy;
	}
	
	private static void setPropertiesFor(XAxisHierarchy xAxis, AxisHierarchy fromAxis) {
		for(Property<?> property : fromAxis.getProperties())
			xAxis.addProperty(property.getId(), property.getValue().toString());
	}
	
	static void updateX(XAxisHierarchy xAxisHierarchy, AxisHierarchy axisHierarchy, UserSession userSession) {
		xAxisHierarchy.setMaxDepth(axisHierarchy.getHierarchy().getMaxDepth());
		updateXAlias(xAxisHierarchy, axisHierarchy,userSession);
		updateXSubset(xAxisHierarchy, axisHierarchy,userSession);
		updateXSelectedElement(xAxisHierarchy, axisHierarchy, userSession);
		updateXLocalFilter(xAxisHierarchy, axisHierarchy.getLocalFilter());
	}
	private static final void updateXAlias(XAxisHierarchy xHierarchy, AxisHierarchy hierarchy,UserSession userSession) {
		addXAliases(xHierarchy, hierarchy.getHierarchy().getAttributes());
		if (hierarchy.getAliasMissing() != null) {
			ViewOpenWarnings.getInstance().addWarning(
					userSession.translate("aliasRemoved", hierarchy.getHierarchy().getName()));
//					"The selected alias of dimension '" + 
//					hierarchy.getHierarchy().getName() + "' does no longer exist. No alias is selected instead.");
		}
		setActiveXAlias(xHierarchy, hierarchy.getProperty(AxisHierarchy.USE_ALIAS));
	}
	private static final void addXAliases(XAxisHierarchy xHierarchy, Attribute[] attributes) {
		for(Attribute attribute : attributes) {
			if(attribute.getType() == Attribute.TYPE_STRING && !"format".equalsIgnoreCase(attribute.getName())) {
				XAlias xAlias= XFilterFactory.createX(attribute, xHierarchy.getId());
				xHierarchy.addAlias(xAlias);
			}
		}
	}
	private static final void setActiveXAlias(XAxisHierarchy xHierarchy, Property<?> alias) {
		if(alias == null || !(alias.getValue() instanceof Attribute))
			xHierarchy.setActiveAlias(null);
		else {
			Attribute aliasAttribute = (Attribute) alias.getValue();
			XAlias xAlias= XFilterFactory.createX(aliasAttribute, xHierarchy.getId());
			xHierarchy.setActiveAlias(xAlias);
		}
	}

	private static final void updateXSubset(XAxisHierarchy xHierarchy, AxisHierarchy hierarchy, UserSession userSession) {
		SubsetHandler subsetHandler = hierarchy.getHierarchy().getSubsetHandler();
		addXSubsets(xHierarchy, subsetHandler.getSubsets());
		if (hierarchy.getSubsetMissing() != null) {
			ViewOpenWarnings.getInstance().addWarning(userSession.translate("subsetRemoved", hierarchy.getHierarchy().getName()));
//					"The selected subset of dimension '" + 
//					hierarchy.getHierarchy().getName() + "' does no longer exist. No subset is selected instead.");			
		}
		setActiveXSubset(xHierarchy, hierarchy.getSubset());
	}
	private static final void addXSubsets(XAxisHierarchy xHierarchy, Subset2[] subsets) {
		xHierarchy.removeAllSubsets();
		for(Subset2 subset : subsets) {
			XSubset xSubset = XFilterFactory.createX(subset);
			xHierarchy.addSubset(xSubset);
		}
	}
	private static final void setActiveXSubset(XAxisHierarchy xAxisHierarchy, Subset2 subset) {
		XSubset xSubset = null;
		if (subset != null) {
			xSubset = XFilterFactory.createX(subset);//XObjectMatcher.find(subset);
		} 
		xAxisHierarchy.setActiveSubset(xSubset);
	}
	private static final void updateXSelectedElement(XAxisHierarchy xHierarchy, AxisHierarchy axisHierarchy, UserSession userSession) {
		XElement selectedElement = getSelectedElement(axisHierarchy, userSession);
		if(selectedElement != null) {
			applyAlias(selectedElement, axisHierarchy);
			xHierarchy.setSelectedElement(selectedElement);
		}
	}
	private static final XElement getSelectedElement(AxisHierarchy axisHierarchy, UserSession userSession) {
		Element[] selectedElements = axisHierarchy.getSelectedElements();
		if(selectedElements == null || selectedElements.length < 1)
			return null;
		//in palo we use only first selected element!
		Element sel = selectedElements[0];
		if (sel == null) {
			// Element does no longer exist, select any existing element:
			ElementNode [] roots = axisHierarchy.getRootNodes();
			if (roots != null && roots.length > 0) {
				sel = roots[0].getElement();
				axisHierarchy.clearSelectedElements();
				axisHierarchy.addSelectedElement(sel);
				if (sel != null) {					
					ViewOpenWarnings.getInstance().addWarning(
							userSession.translate("selectedElementRemoved", axisHierarchy.getHierarchy().getName(), sel.getName()));
//							"The selected element of dimension '" + 
//						axisHierarchy.getHierarchy().getName() + "' does no longer exist. The element '" + 
//						sel.getName() + "' has been selected instead.");
				} else {
					ViewOpenWarnings.getInstance().addWarning(
							userSession.translate("selectedElementRemovedForGood", axisHierarchy.getHierarchy().getName()));
//							"The selected element of dimension '" + 
//							axisHierarchy.getHierarchy().getName() + "' does no longer exist. No alternative" +
//							" element could be selected.");
				}
			}
		}
		return XElementFactory.createX(sel);		
	}
	
	private static final String getAliasForElement(String name, String elementName, String format) {
		String result = elementName;
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
		return result;		
	}
	
	private static final void applyAlias(XElement selectedElement, AxisHierarchy axisHierarchy) {
		String alias = getAlias(selectedElement, axisHierarchy);
		if(alias != null) {
			Property prop = axisHierarchy.getProperty("aliasFormat");
			if (prop != null && prop.getValue() != null) {
				selectedElement.setName(getAliasForElement(alias, axisHierarchy.getHierarchy().getElementById(selectedElement.getId()).getName(), prop.getValue().toString()));
			} else {
				selectedElement.setName(alias);
			}
		}
	}
	private static final String getAlias(XElement forElement, AxisHierarchy axisHierarchy) {
		Property<?> aliasProperty = 
			axisHierarchy.getProperty(AxisHierarchy.USE_ALIAS);
		if(aliasProperty != null) {
			Attribute alias = (Attribute)aliasProperty.getValue();
			Element element = 
				axisHierarchy.getHierarchy().getElementById(forElement.getId());
			Object value = alias.getValue(element);
			if (value != null)
				return value.toString();
		}
		return null;
	}
	private static final void updateXLocalFilter(XAxisHierarchy xHierarchy, LocalFilter localFilter) {
		if(localFilter != null && localFilter.hasVisibleElements()) {
			List<XElementNode> xVisibleElements = new ArrayList<XElementNode>();
			ElementNode[] visibleElements = localFilter.getVisibleElements();
			for(ElementNode elNode : visibleElements) {
				xVisibleElements.add(createX(elNode, null, xHierarchy));
			}
			xHierarchy.setVisibleElements(xVisibleElements.toArray(new XElementNode[0]));
		} else
			xHierarchy.setVisibleElements(null);
	}
	private static final XElementNode createX(ElementNode elNode,
			XElementNode xParent, XAxisHierarchy xHierarchy) {
		XElementNode xElNode = XElementFactory.createX(elNode, xHierarchy.getId(), xHierarchy.getViewId());
		if (xParent != null) {
			xParent.forceAddChild(xElNode);
			xElNode.setParent(xParent);
		}
		if (elNode.hasChildren()) {
			for (ElementNode child : elNode.getChildren())
				createX(child, xElNode, xHierarchy);
		}
		return xElNode;
	}

}
