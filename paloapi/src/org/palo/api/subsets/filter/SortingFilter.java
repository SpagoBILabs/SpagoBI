/*
*
* @file SortingFilter.java
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
* @version $Id: SortingFilter.java,v 1.20 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.palo.api.Attribute;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.ElementNodeVisitor;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.subsets.SubsetFilter;
import org.palo.api.subsets.filter.settings.SortingFilterSetting;
import org.palo.api.utils.ElementNodeUtilities;

/**
 * <code>SortingFilter</code>
 * <p>
 * A sorting filter belongs to the category of structural filters.
 * This filter defines different sorting criteria to influence the element 
 * sequence of the subset as well as the possibility to create an element
 * hierarchy.  
 * </p>
 *
 * @author ArndHouben
 * @version $Id: SortingFilter.java,v 1.20 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
public class SortingFilter extends AbstractSubsetFilter implements StructuralFilter {
	
	private final SortingFilterSetting setting;
	private final TextualSorter textualSorter = new TextualSorter();	
	
	/**
	 * Creates a new <code>SortingFilter</code> instance for the given 
	 * dimension
	 * @param dimension the dimension to create the filter for
	 * @deprecated use {@link SortingFilter#SortingFilter(Hierarchy)} instead.
	 */
	public SortingFilter(Dimension dimension) {
		this(dimension, new SortingFilterSetting());
	}
	
	/**
	 * Creates a new <code>SortingFilter</code> instance for the given 
	 * hierarchy
	 * @param hierarchy the hierarchy to create the filter for
	 */
	public SortingFilter(Hierarchy hierarchy) {
		this(hierarchy, new SortingFilterSetting());
	}
	
	/**
	 * Creates a new <code>SortingFilter</code> instance for the given 
	 * dimension with the given settings
	 * @param dimension the dimension to create the filter for
	 * @param setting the filter settings to use
	 * @deprecated use {@link SortingFilter#SortingFilter(Hierarchy, SortingFilterSetting)}
	 * instead.
	 */
	public SortingFilter(Dimension dimension, SortingFilterSetting setting) {
		super(dimension);
		this.setting = setting;
	}
	
	/**
	 * Creates a new <code>SortingFilter</code> instance for the given 
	 * hierarchy with the given settings
	 * @param hierarchy the hierarchy to create the filter for
	 * @param setting the filter settings to use
	 */
	public SortingFilter(Hierarchy hierarchy, SortingFilterSetting setting) {
		super(hierarchy);
		this.setting = setting;
	}

	public final SortingFilter copy() {
		SortingFilter copy = new SortingFilter(hierarchy);
		copy.getSettings().adapt(setting);
		return copy;
	}
	
	public final SortingFilterSetting getSettings() {
		return setting;
	}
	/**
	 * Sorts the given subset element list using the current settings.
	 * @param elements the subset element
	 * @return the sorted element hierarchy
	 */
	public final List<ElementNode> sort(final Set<Element> elements) {
		Set<ElementNode> preservedNodes = new HashSet<ElementNode>();
		List<ElementNode> sortedNodes = new ArrayList<ElementNode>();
		HashMap<Element, ElementNode> element2node = new HashMap<Element, ElementNode>();
		IndentComparator indentFilter = new IndentComparator(getSubset());
		//initialize and split nodes (if limitations are set)
		
		//limitations:
		int limitByLvl = -1;
		if (setting.doSortPerLevel()) {
//			Element el = hierarchy.getElementById(
//					setting.getSortLevelElement().getValue());
//			if (el != null)
//				limitByLvl = el.getLevel();
			limitByLvl = setting.getSortLevel().getValue();
		}
		boolean limitByType = setting.doSortByType();
		boolean sortLeafsOnly = setting.getSortTypeMode().
						getValue() == SortingFilterSetting.SORT_TYPE_LEAFS_ONLY;

		// from elements...
		Element[] hierElements = hierarchy.getElements();
		int index = -1;
		for(Element element : hierElements) {
			if(elements.contains(element)) {
				index++;
				ElementNode node = new ElementNode(element,null,index);
				element2node.put(element,node);
				//split:				
				if(limitByLvl != -1 && indentFilter.compare(element, limitByLvl) !=0) {
					//limitByLvl != element.getLevel()) {
					preservedNodes.add(node);
					continue;
				}
				if(limitByType) {
					boolean isLeaf = element.getChildCount() == 0;
					if(sortLeafsOnly && !isLeaf) {
						preservedNodes.add(node);
						continue;
					} else if (!sortLeafsOnly && isLeaf) {
						preservedNodes.add(node);
						continue;
					}						
				}
				sortedNodes.add(node);
			}
		}
		
		//sorting criteria:
		//criteria:		
		switch (setting.getSortCriteria().getValue()) {
		case SortingFilterSetting.SORT_CRITERIA_DATA:
			DataFilter dataFilter = 
				(DataFilter)effectiveFilters.get(SubsetFilter.TYPE_DATA);
			if(dataFilter == null)
				throw new PaloAPIException("No data filter defined!");
			Collections.sort(sortedNodes, new DataSorter(dataFilter));
			break;
		case SortingFilterSetting.SORT_CRITERIA_LEXICAL:
			//first sort root nodes:
			Collections.sort(sortedNodes,textualSorter);
			break;
		case SortingFilterSetting.SORT_CRITERIA_ALIAS:
			AliasFilter aliasFilter = 
				(AliasFilter)effectiveFilters.get(SubsetFilter.TYPE_ALIAS);
			if(aliasFilter == null)
				throw new PaloAPIException("No alias filter defined!");
			Collections.sort(sortedNodes, new AliasSorter(aliasFilter));
			break;
		default:
			Collections.sort(sortedNodes, new DefinitionSorter());
		}
		
		//sort by attribute
		if (setting.doSortByAttribute()) {
			Collections.sort(sortedNodes, 
					new AttributeSorter(setting.getSortAttribute().getValue()));
		}

		//reverse order CHANGES BY JEDOX
		if(setting.doReverseOrder()) {
			int reverseOrder = setting.getOrderMode().getValue();
			//total
			if(reverseOrder == SortingFilterSetting.ORDER_MODE_REVERSE_TOTAL) {
				Collections.reverse(sortedNodes);				
			} else { //if (reverseOrder == SortingFilterSetting.ORDER_MODE_REVERSE_HIERARCHY) {
				//CHANGE BY JEDOX: now this is "reverse hierarchy"
				ArrayList<ElementNode> newNodes = new ArrayList<ElementNode>();
				for(ElementNode node : sortedNodes) {
					insert(node,newNodes);
				}
				sortedNodes.clear();
				sortedNodes.addAll(newNodes);
			}
		}
		
		ElementNode[] allNodes = 
			new ElementNode[preservedNodes.size() + sortedNodes.size()];
		for(ElementNode pNode : preservedNodes) {
			allNodes[pNode.getIndex()] = pNode;
		}
		index = 0;
		for(ElementNode node : sortedNodes) {
			index = insert(node, allNodes, index);
			if(index == -1)
				break;
		}
		List<ElementNode> nodes = 
			new ArrayList<ElementNode>(Arrays.asList(allNodes));
		
		//finally we check if we have to create a hierarchy
		if(setting.doHierarchy()) {
			final boolean showChildren = setting.getHierarchicalMode().getValue() 
						== SortingFilterSetting.HIERARCHICAL_MODE_SHOW_CHILDREN;
			
			final List<ElementNode> rootNodes = new ArrayList<ElementNode>();

			createHierarchy(nodes, element2node, showChildren);
				
			for (ElementNode node : nodes) {
				if (node.getParent() == null)
					rootNodes.add(node);
			}
			
//CHANGES BY JEDOX			
//			//do we have to reverse the hierarchy?
//			int reverseOrder = setting.getOrderMode().getValue();
//			if(reverseOrder == SortingFilterSetting.ORDER_MODE_REVERSE_PER_LEVEL) {
//				final List<ElementNode> _nodes = new ArrayList<ElementNode>();
//				ElementNodeVisitor visitor = new ElementNodeVisitor() {
//					public final void visit(ElementNode elementNode,
//							ElementNode parent) {
//						elementNode.setParent(null);
//						//insert node...
//						if(parent != null) {
//							parent.removeChild(elementNode);
//							_nodes.add(_nodes.indexOf(parent), elementNode);
//						} else
//							_nodes.add(elementNode);
//					}
//				};
//				ElementNodeUtilities.traverse(rootNodes.toArray(new ElementNode[0]), visitor);
//				rootNodes.clear();
//				rootNodes.addAll(_nodes);
//			}
			
//			if(!rootNodes.isEmpty()) {
//				nodes.clear();
//				nodes.addAll(rootNodes);
//			}
			//add root nodes:
			nodes.clear();
			nodes.addAll(rootNodes);
		}

		return nodes;
	}
	
	
	public final void filter(final List<ElementNode> hierarchy, Set<Element> elements) {
		if (setting.getShowDuplicates().getValue() == 0) {
			// filter out duplicates!!
			final HashSet<Element> visited = new HashSet<Element>();
			ElementNodeVisitor visitor = new ElementNodeVisitor() {
				public void visit(ElementNode node, ElementNode parent) {
					Element el = node.getElement();
					if(!visited.add(el)) {
						if(parent != null)
							parent.removeChild(node);
						//element already visited:
						hierarchy.remove(node);

					}
				}
			};
			ElementNodeUtilities.traverse(
					hierarchy.toArray(new ElementNode[0]), visitor);
		}
	}
	
	public final int getType() {
		return TYPE_SORTING;
	}
	
	public final void initialize() {
	}

	public final void validateSettings() throws PaloIOException {
		/* all settings are optional */
	}

	private final void insert(ElementNode node, List<ElementNode>nodes) {
//		int nodeLvl = node.getElement().getLevel();
//		int index = 0;
//		for(ElementNode _node : nodes) {
//			if(_node.getElement().getLevel() == nodeLvl)
//				break;
//			index++;
//		}
//		nodes.add(index, node);
		//PR 7069: we use the depth to reverse per level 
		//=> gives more natural results...
		int nodeDepth = node.getElement().getDepth();
		int index = 0;
		for(ElementNode _node : nodes) {
			if(_node.getElement().getDepth() == nodeDepth)
				break;
			index++;
		}
		nodes.add(index, node);
	}
	
	private final int insert(ElementNode node, ElementNode[] nodes, int index) {
		for(int i=index;i<nodes.length;i++) {
			if(nodes[i] == null) {
				nodes[i] = node;
				return ++i;
			}
		}
		return -1;
	}
	
	private final void createHierarchy(List<ElementNode> nodes,
			Map<Element, ElementNode> el2node, boolean showChildren) {
		// we run through nodes...
		Iterator<ElementNode> elNodes = nodes.iterator();
		while (elNodes.hasNext()) {
			ElementNode node = elNodes.next();
			// do we have a parent node and do we want to show children
			Element element = node.getElement();
			Element[] parents = element.getParents();
			for (Element parent : parents) {
				ElementNode parentNode = el2node.get(parent);
				if (parentNode != null) {
//					node.setParent(parentNode);
					parentNode.addChild(node);				
				} else {
					// PR 6892: we take ourself only if show children is true...
					if (!showChildren)
						elNodes.remove();
					else {
						//we have to find top parent:
						parentNode = getFirstNonNullParent(parent, el2node);
						if(parentNode != null) {
//							node.setParent(parentNode);
							parentNode.addChild(node);							
						}
					}
				}
			}
		}
		
	}
	
	private final ElementNode getFirstNonNullParent(Element el, Map<Element, ElementNode> el2node) {
		if(el == null)
			return null;
		ElementNode pNode = null;
		for(Element parent : el.getParents()) {
			pNode = el2node.get(parent);
			if(pNode != null)
				break;
		}
		return pNode;
	}
}

class TextualSorter implements Comparator<ElementNode> {
	public int compare(ElementNode o1, ElementNode o2) {
		String e1Name = o1.getElement().getName();
		String e2Name = o2.getElement().getName();
		return  e1Name.compareTo(e2Name);
	}	
}

class AliasSorter implements Comparator<ElementNode> {
	private final AliasFilter aliasFilter;
	
	AliasSorter(AliasFilter aliasFilter) {
		this.aliasFilter = aliasFilter;
	}
	
	public int compare(ElementNode o1, ElementNode o2) {
		Element e1 = o1.getElement();
		Element e2 = o2.getElement();
//		Object val1 = e1.getAttributeValue(alias);
//		Object val2 = e2.getAttributeValue(alias);
//		
//		String e1val = val1 != null ? val1.toString() : e1.getName();
//		String e2val = val1 != null ? val2.toString() : e2.getName();
//		return  e1val.compareTo(e2val);
		
		String e1Name = aliasFilter.getAlias(e1);
		String e2Name = aliasFilter.getAlias(e2);
		return e1Name.compareTo(e2Name);
	}	
}

class DataSorter implements Comparator<ElementNode> {
	
	private final DataFilter dataFilter;
	
	DataSorter(DataFilter dataFilter) {
		this.dataFilter = dataFilter;
	}
	
	public int compare(ElementNode o1, ElementNode o2) {
		Element e1 = o1.getElement();
		Element e2 = o2.getElement();
		try {
		double d1 = Double.parseDouble(dataFilter.getValue(e1));
		double d2 = Double.parseDouble(dataFilter.getValue(e2));
		
		return  Double.compare(d1, d2);
		}catch(NumberFormatException nfe) {
			/* ignrore */
		}
		//and  compare strings:
		return dataFilter.getValue(e1).compareTo(dataFilter.getValue(e2));
	}		
}

class AttributeSorter implements Comparator<ElementNode> {
	private final String attributeID;
	public AttributeSorter(String attributeID) {
		this.attributeID = attributeID;
	}
	public int compare(ElementNode o1, ElementNode o2) {
		Hierarchy hier1 = o1.getElement().getHierarchy();
		Hierarchy attrHier1 = hier1.getAttributeHierarchy();
		if(attrHier1 == null)
			return 0;
		
		Element attrEl = attrHier1.getElementById(attributeID);
		if(attrEl == null)
			return 0;
		Attribute attr = hier1.getAttribute(attrEl.getId());
//		Attribute attr = o1.getElement().getDimension().getAttribute(attribute);
		if(attr == null)
			return 0;
		Object attr1 = o1.getElement().getAttributeValue(attr);
		Object attr2 = o2.getElement().getAttributeValue(attr);
		String a1 = attr1 != null ? attr1.toString() : "";
		String a2 = attr2 != null ? attr2.toString() : "";
		//PR 6885: we have to check if attribute type is numeric => do numeric comparison then
		if(attr.getType() == Attribute.TYPE_NUMERIC) {
			try {
				Double db1 = a1.length() > 0 ? Double.valueOf(a1) : new Double(0);
				Double db2 = a2.length() > 0 ? Double.valueOf(a2) : new Double(0);
				return db1.compareTo(db2);
			}catch(NumberFormatException e) {
				//we silently do a lexicographically comparison
			}
		} 
		return a1.compareTo(a2);
	}
}

class DefinitionSorter implements Comparator<ElementNode> {
	public final int compare(ElementNode node1, ElementNode node2) {
		int pos1 = node1.getElement().getPosition();
		int pos2 = node2.getElement().getPosition();
		if(pos1 > pos2)
			return 1;
		else if(pos1 < pos2)
			return -1;
		
		return 0;
	}
}