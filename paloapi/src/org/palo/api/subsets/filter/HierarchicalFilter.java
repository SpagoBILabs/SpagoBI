/*
*
* @file HierarchicalFilter.java
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
* @version $Id: HierarchicalFilter.java,v 1.20 2010/02/09 11:44:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.subsets.filter.settings.HierarchicalFilterSetting;

/**
 * <code>HierarchicalFilter</code>
 * <p>
 * A hierarchical filter is a restrictive filter as well as a structural filter.
 * It is restrictive in the sense that elements which go into the subset can be 
 * filtered by certain criteria, e.g. their level. A structural effect can be
 * achieved by using the revolve filter settings.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: HierarchicalFilter.java,v 1.20 2010/02/09 11:44:57 PhilippBouillon Exp $
 **/
public class HierarchicalFilter extends AbstractSubsetFilter implements
		RestrictiveFilter, StructuralFilter {
	
	private final HierarchicalFilterSetting setting;
	
	
	/**
	 * @deprecated use {@link HierarchicalFilter#HierarchicalFilter(Hierarchy)} instead.
	 */
	public HierarchicalFilter(Dimension dimension) {
		this(dimension.getDefaultHierarchy(), new HierarchicalFilterSetting());
	}

	public HierarchicalFilter(Hierarchy hierarchy) {
		this(hierarchy, new HierarchicalFilterSetting());
	}
	
	/**
	 * @deprecated use {@link HierarchicalFilter#HierarchicalFilter(Hierarchy, HierarchicalFilterSetting)} instead.
	 */
	public HierarchicalFilter(Dimension dimension, HierarchicalFilterSetting setting) {
		super(dimension.getDefaultHierarchy());
		this.setting = setting;
	}

	public HierarchicalFilter(Hierarchy hierarchy, HierarchicalFilterSetting setting) {
		super(hierarchy);
		this.setting = setting;
	}
	
	public final HierarchicalFilter copy() {
		HierarchicalFilter copy = new HierarchicalFilter(hierarchy);
		copy.getSettings().adapt(setting);
		return copy;
	}

	public final HierarchicalFilterSetting getSettings() {
		return setting;
	}
	
	public final Element getReferenceElement() {
		return hierarchy.getElementById(setting.getRefElement().getValue());
	}
	
	public void filter(Set<Element> elements) {
		IndentComparator indentFilter = new IndentComparator(getSubset());
		Iterator<Element> allElements = elements.iterator();
		while(allElements.hasNext()) {
			if(!accept(allElements.next(), indentFilter))
				allElements.remove();
		}
	}
	
	public final void filter(List<ElementNode> hier,
			final Set<Element> elements) {
		if (!setting.doRevolve() || hier.isEmpty())
			return;
		
		//get revolve settings:
		String revId = setting.getRevolveElement().getValue();
		Element revolveElement = hierarchy.getElementById(revId);
		final int revolveLevel = 
					revolveElement != null ? revolveElement.getLevel() : 0;
		final int revolveMode = setting.getRevolveMode().getValue();
		final int revolveCount = setting.getRevolveCount().getValue();

		final int[] counter = new int[1];
		final Set<Element> revolvedElements = new HashSet<Element>();
		final ArrayList<ElementNode> newHierarchy = new ArrayList<ElementNode>();
		ElementNodeVisitor2 visitor = new ElementNodeVisitor2() {
			public ElementNode visit(ElementNode node, ElementNode parent) {
				if(counter[0]<revolveCount) {
					boolean addIt = true;
					int nodeLevel = node.getElement().getLevel();
					switch (revolveMode) {
					case HierarchicalFilterSetting.REVOLVE_ADD_ABOVE:
						if (nodeLevel < revolveLevel)
							addIt = false;
						break;
					case HierarchicalFilterSetting.REVOLVE_ADD_BELOW:
						if (nodeLevel > revolveLevel)
							addIt = false;
						break;
					case HierarchicalFilterSetting.REVOLVE_ADD_DISABLED:
						if (nodeLevel != revolveLevel)
							addIt = false;
						break;
					}
					if (addIt) {
						ElementNode newNode = new ElementNode(node.getElement());
						revolvedElements.add(node.getElement());
						newNode.setParent(parent);
						if (parent != null)
							parent.forceAddChild(newNode);
						else 
							newHierarchy.add(newNode);
						
						++counter[0];
						return newNode;
					} 
//					else {
//						//have to remove it from elements collection
//						elements.remove(node.getElement());
//					}
						 
				} 
//				else {
//					//have to remove corresponding element from collection:
//					elements.remove(node.getElement());
//				}
				return null;
			}
		};
		counter[0] = 0;
		do {
			for(ElementNode node : hier) {
				traverse(node, null, visitor);
			}			
		}while(counter[0]<revolveCount && counter[0] > 0); //stops if no matching element was found...
		elements.retainAll(revolvedElements);
		revolvedElements.clear();
		hier.clear();
		hier.addAll(newHierarchy);
	}

	
	public final int getType() {
		return TYPE_HIERARCHICAL;
	}


	public final void initialize() {
		setting.reset(); //resetInternal();
	}

	
//	public final void resetInternal() {
//		setting.reset();
//	}

	public final void validateSettings() throws PaloIOException {
		/* all settings are optional */
	}
    
    private final boolean accept(Element element, IndentComparator indentFilter) {
    	//above/below selection:
    	if(setting.doAboveBelowSelection()) {
    		//reference element
    		String refElId = setting.getRefElement().getValue();
    		if(element.getId().equals(refElId)) {
    			if(setting.getExclusive().getValue())
    				return false;
    		} else {
        		Element refElement = hierarchy.getElementById(refElId);
        		if(setting.getAbove().getValue()) {
        			if(!isParent(refElement, element))
        				return false;    			
        		} else {
        			if(!isChild(refElement, element))
        				return false;
        		}
    		}
//    		//hide mode
//        	int hideMode = setting.getHideMode().getValue();
//        	if (hideMode != HierarchicalFilterSetting.HIDE_MODE_DISABLED) {
//    			boolean isLeaf = element.getChildCount() <= 0;
//    			if (hideMode == HierarchicalFilterSetting.HIDE_MODE_LEAFS && isLeaf)
//    				return false;
//    			else if (hideMode == HierarchicalFilterSetting.HIDE_MODE_CONSOLIDATIONS
//    					&& !isLeaf)
//    				return false;
//    		} //go on

    	} //go on
    	
    	//PR 7079: hide mode is independent of above/below selection again ;)
		//hide mode
    	int hideMode = setting.getHideMode().getValue();
    	if (hideMode != HierarchicalFilterSetting.HIDE_MODE_DISABLED) {
			boolean isLeaf = element.getChildCount() <= 0;
			if (hideMode == HierarchicalFilterSetting.HIDE_MODE_LEAFS && isLeaf)
				return false;
			else if (hideMode == HierarchicalFilterSetting.HIDE_MODE_CONSOLIDATIONS
					&& !isLeaf)
				return false;
		} //go on
    	

		// start/end selection
    	if(setting.doLevelSelection()) {
    		if(!isInLevelSelectionRegion(element, indentFilter))
    			return false;
    	}

    		
    	return true;
    }
    private final boolean isParent(Element element, Element parent) {
    	Element[] parents = element.getParents();
    	for(Element _parent : parents) {
    		if(_parent.equals(parent))
    			return true;
    	}
    	for(Element _parent : parents) {
    		if(isParent(_parent,parent))
    			return true;
    	}
    	return false;
    }
    
    private final boolean isChild(Element element, Element child) {
    	if(element == null || child == null)
    		return false;
    	Element[] children = element.getChildren();
    	for(Element _child : children) {
    		if(_child.equals(child))
    			return true;
    	}
    	for(Element _child : children) {
    		if(isChild(_child,child))
    			return true;
    	}
    	return false;
    }
    
    private final boolean isInLevelSelectionRegion(Element element,
			IndentComparator indentFilter) {
		int endLevel = setting.getEndLevel().getValue();
		int startLevel = setting.getStartLevel().getValue();
		startLevel = startLevel > -1 ? startLevel : 0;
		endLevel = endLevel > -1 ? endLevel : Integer.MAX_VALUE;

		if (startLevel > endLevel) {
			int tmp = endLevel;
			endLevel = startLevel;
			startLevel = tmp;
		}
		return (indentFilter.compare(element, startLevel) >= 0 
				&& indentFilter.compare(element, endLevel) <= 0);
	}
    
    private final void traverse(ElementNode node, ElementNode parent,
			ElementNodeVisitor2 visitor) {
		ElementNode newNode = visitor.visit(node, parent);
		ElementNode[] children = node.getChildren();
		if (children == null)
			return;
		for (ElementNode child : children)
			traverse(child, newNode, visitor);
	}
}

interface ElementNodeVisitor2 {
	ElementNode visit(ElementNode n, ElementNode p);
}
