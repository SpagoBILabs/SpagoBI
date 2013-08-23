/*
*
* @file DefaultSubsetHandler.java
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
* @author ArndHouben
*
* @version $Id: DefaultSubsetHandler.java,v 1.14 2010/02/09 11:44:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.ext.subsets.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.palo.api.Dimension;
import org.palo.api.DimensionFilter;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.ElementNodeVisitor;
import org.palo.api.Hierarchy;
import org.palo.api.HierarchyFilter;
import org.palo.api.SubsetState;
import org.palo.api.ext.subsets.SubsetHandler;
import org.palo.api.ext.subsets.states.RegExState;
import org.palo.api.utils.ElementNodeUtilities;

/**
 * <code>DefaultSubseetHandler</code>
 * A default implementation of the <code>SubsetHandler</code>interface.
 * 
 * @author ArndHouben
 * @version $Id: DefaultSubsetHandler.java,v 1.14 2010/02/09 11:44:57 PhilippBouillon Exp $
 */
class DefaultSubsetHandler implements SubsetHandler {

//	private List elements;
	private Set elements;
	private Hierarchy hierarchy;
	private HierarchyFilter filter;
	private SubsetState subsetState;
	
	
	final synchronized void use(Hierarchy hierarchy,HierarchyFilter filter,SubsetState subsetState) {
		this.hierarchy = hierarchy;
		this.filter = filter;
		this.subsetState = subsetState;
		elements = filterElements();		
	}
	
	public final boolean isFlat() {
		return filter.isFlat();
	}
	
	public final ElementNode[] getVisibleRootNodes() {
		Collection rootNodes = getVisibleRootNodesAsList();
		return (ElementNode[]) rootNodes.toArray(
				new ElementNode[rootNodes.size()]);
	}
	
	public final synchronized List getVisibleRootNodesAsList() {
        final ArrayList rootNodes = new ArrayList();
        final LinkedHashMap elementNodes = new LinkedHashMap();
        final boolean isFlat = isFlat();
        hierarchy.visitElementTree(new ElementNodeVisitor() {
            public void visit(ElementNode elementNode, ElementNode parent) {
//TODO think: maybe it is a good idea if DimensionFilter could handle ElementNodes...    
//=> e.g. that will remove the ugly check path if-clause for reg ex...            	
				Element element = elementNode.getElement();
				if(!elements.contains(element))
					return;
								
				if (element == null) {
					return;
				} else if (isFlat) {
					ElementNode elNode = new ElementNode(element,null);
					rootNodes.add(elNode);
				} else {
					//check path:
					if(!subsetState.getId().equals(RegExState.ID)) {
						String path = ElementNodeUtilities.getPath(elementNode);
						if(!subsetState.containsPath(element, path))
							return;
					}
					ElementNode elNode = 
						new ElementNode(element,elementNode.getConsolidation());
					elementNodes.put(elementNode,elNode);
					ElementNode newParent;
					if((newParent = (ElementNode)elementNodes.get(parent))!=null) {
						newParent.forceAddChild(elNode);
						elNode.setParent(newParent);
					} else
						rootNodes.add(elNode);
				}
			}
        });
        //postprocess root nodes...
		if (isFlat) {
			if (filter instanceof FlatStateFilter) {
				rootNodes.clear();
				((FlatStateFilter)filter).collectRootNodes(rootNodes);
//				Collection result = 
//					((FlatStateFilter)filter).postprocessRootNodes(rootNodes);
//				if (result != null) {
//					rootNodes.clear();
//					rootNodes.addAll(result);
//				}
			} else {
				ElementNode result[] = filter
						.postprocessRootNodes((ElementNode[]) rootNodes
								.toArray(new ElementNode[0]));
				if (result != null) {
					rootNodes.clear();
					rootNodes.addAll(Arrays.asList(result));
				}
			}
		}
        return rootNodes;
	}


	public final synchronized Element[] getVisibleElements() {
//		List elements = filterElements();
        return (Element[])elements.toArray(new Element[elements.size()]);
	}

	public final synchronized List getVisibleElementsAsList() {
		return new ArrayList(elements);
	}
	
	public final synchronized boolean isVisible(Element element) {
		return elements.contains(element);
//		if(subsetState == null)
//			return false;
//		return subsetState.isVisible(element);
	}
	
	//--------------------------------------------------------------------------
	// PRIVATE METHODS
	//
	private final Set filterElements() {
        if(filter == null)
        	return new HashSet(Arrays.asList(hierarchy.getElements()));
		filter.init(hierarchy);        
        Element sourceElements[] = hierarchy.getElements();
        if (sourceElements == null)
            sourceElements = new Element[0];
//        ArrayList elements = new ArrayList();
        Set elements = new HashSet(); 
        for (int i = 0; i < sourceElements.length; ++i) {
			Element element = sourceElements[i];
			if (!filter.acceptElement(element))
				continue;
			elements.add(element);
		}
        return elements;
	}
	
//	private final String getPath(ElementNode node) {
//		ElementNode parent = node.getParent();
//		if(parent != null) {
//			StringBuffer path = new StringBuffer();
//			path.append(getPath(parent));
//			path.append("///");
//			return path.toString();
//		}
//		return node.getElement().getId();
//	}

}