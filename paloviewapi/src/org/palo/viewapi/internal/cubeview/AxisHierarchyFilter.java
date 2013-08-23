/*
*
* @file AxisHierarchyFilter.java
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
* @version $Id: AxisHierarchyFilter.java,v 1.12 2010/03/12 12:49:13 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package org.palo.viewapi.internal.cubeview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.palo.api.Attribute;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.subsets.Subset2;
import org.palo.viewapi.LocalFilter;
import org.palo.viewapi.Property;

/**
 * <code>HierarchyFilter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AxisHierarchyFilter.java,v 1.12 2010/03/12 12:49:13 PhilippBouillon Exp $
 **/
class AxisHierarchyFilter {

	//filters:	
	private Subset2 subset;
	private Attribute alias;
	private LocalFilter localFilter;
	
	private final Hierarchy hierarchy;
	
	public AxisHierarchyFilter(Hierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}

	public final void setAlias(Attribute alias) {
		this.alias = alias;
	}
	
	public final Subset2 getSubset() {
		return subset;
	}
	public final void setSubset(Subset2 subset) {
		this.subset = subset;
	}
	
	public final LocalFilter getLocalFilter() {
		return localFilter;
	}
	public final void setLocalFilter(LocalFilter localFilter) {
		this.localFilter = localFilter;
	}
	
	public final boolean contains(Element element) {
		if(useLocalFilter())
			return localFilter.isVisible(element);
		if(useSubset())
			return subset.contains(element);
		return hierarchy.getElementById(element.getId()) != null;
	}
	
	public final List<ElementNode> applyFilters(Property aliasFormat) {
		final List<ElementNode> roots = new ArrayList<ElementNode>();
		applySubset(roots);
		applyLocalFilter(roots);		
		String af = null;
		if (aliasFormat != null && aliasFormat.getValue() != null) {
			af = aliasFormat.getValue().toString();
		}
		applyAlias(roots, af);
		return roots;
	}
	
	private List<ElementNode> hierarchyRoots() {
		return Arrays.asList(hierarchy.getElementsTree());
	}
	
	private final void applySubset(List<ElementNode> roots) {
		if(useSubset()) {
			roots.addAll(Arrays.asList(subset.getRootNodes()));
		}
		else {
			roots.addAll(hierarchyRoots());
		}
	}
	
	private final void applyLocalFilter(List<ElementNode> roots) {
		if (!useLocalFilter())
			return;		
		roots.clear();
		roots.addAll(Arrays.asList(localFilter.getVisibleElements()));
	}

	private final String getAliasFormat(String elementName, String alias, String format) {
		String result = alias;
		if (format != null) {
			if (format.equals("aliasFormat")) {
				result = alias;
			} else if (format.equals("elementName")) {
				result = elementName;
			} else if (format.equals("elementNameDashAlias")) {
				result = elementName + " - " + alias;
			} else if (format.equals("aliasDashElementName")) {
				result = alias + " - " + elementName;
			} else if (format.equals("elementNameParenAlias")) {
				result = elementName + " (" + alias + ")";
			} else if (format.equals("aliasParenElementName")) {
				result = alias + " (" + elementName + ")";
			} else if (format.equals("elementNameAlias")) {
				result = elementName + " " + alias;
			} else if (format.equals("aliasElementName")) {
				result = alias + " " + elementName;
			} 
		}
		return result;
	}
	
	private final void applyAlias(List<ElementNode> roots, final String aliasFormat) {		
		ElementNodeVisitor visitor = new ElementNodeVisitor() {
			public ElementNode visit(ElementNode node, ElementNode parent) {
				String alias = getAliasFor(node);
				if(alias != null && alias.length() != 0)
					node.setName(getAliasFormat(node.getElement().getName(), alias, aliasFormat));
				else
					node.setName(node.getElement().getName());
				return node;
			}
		};
		for (ElementNode root : roots) {
			traverse(root, null, visitor);
		}		
	}
	private final String getAliasFor(ElementNode node) {
		if (useAlias()) {
			Object value = alias.getValue(node.getElement());
			if (value != null)
				return value.toString();
		}
		return null;
	}
	final String getAliasFor(String elementId) {
		Element elem = hierarchy.getElementById(elementId);
		if (elem == null) {
			return null;
		}
		if (!useAlias()) {
			return elem.getName();
		}		
		Object value = alias.getValue(elem);
		if (value != null) {
			return value.toString();
		}
		return null;
	}
		
	private final boolean useAlias() {
		return alias != null;
	}
	private final boolean useLocalFilter() {
		return localFilter != null && localFilter.hasVisibleElements();
	}
	
	private final boolean useSubset() {
		return subset != null;
	}
	
    private final void traverse(ElementNode node, ElementNode parent,
			ElementNodeVisitor visitor) {
		ElementNode newNode = visitor.visit(node, parent);
		ElementNode[] children = node.getChildren();
		if (children == null)
			return;
		for (ElementNode child : children)
			traverse(child, newNode, visitor);
	}
}

interface ElementNodeVisitor {
	ElementNode visit(ElementNode n, ElementNode p);
}
