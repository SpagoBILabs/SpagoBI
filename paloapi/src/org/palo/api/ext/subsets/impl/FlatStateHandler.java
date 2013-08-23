/*
*
* @file FlatStateHandler.java
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
* @version $Id: FlatStateHandler.java,v 1.12 2010/02/09 11:44:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.ext.subsets.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.palo.api.Dimension;
import org.palo.api.DimensionFilter;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.HierarchyFilter;
import org.palo.api.SubsetState;
import org.palo.api.ext.subsets.states.FlatState;

/**
 * The <code>FlatStateHandler</code> is a default implementation to handle
 * the {@link FlatState} which interprets all visible dimension 
 * <code>Element</code>s as root elements, i.e. without any hierachy at all. 
 *  
 * @author ArndHouben
 * @version $Id: FlatStateHandler.java,v 1.12 2010/02/09 11:44:57 PhilippBouillon Exp $
 */
class FlatStateHandler extends AbstractStateHandler {
	
	public final boolean isFlat() {
		return true;
	}
	
	public final DimensionFilter createDimensionFilter(Dimension dimension) {
		return null;
	}
	
	public final HierarchyFilter createHierarchyFilter(Hierarchy hierarchy) {
		return new FlatStateFilter(subsetState);
	}
	
//		return new DimensionFilter() {
//
//			private Dimension dimension;
//
//			Set elementSet;
//			{
//				elementSet = new HashSet();
//				elementSet.addAll(Arrays.asList(subsetState.getVisibleElements())); //state.getElements());
//			}
//
//			public void init(Dimension dimension) {
//				this.dimension = dimension;
//			}
//
//			public boolean acceptElement(Element element) {
//				return elementSet.contains(element); // .getName());
//			}
//
//			public boolean isFlat() {
//				return true;
//			}
//
//			public Collection postprocessRootNodes(Collection rootNodes) {
////				PR 6772		//just check if each element is known, but this should have happened in filter already...
//				ArrayList nodes = new ArrayList();
//				Element[] visElements = subsetState.getVisibleElements();
//				for(int i=0;i<visElements.length;++i) {
//					String name = visElements[i].getName();
//					Element element = dimension.getElementByName(name);
//					if(element == null)
//						continue;
////PR 6773	//we use node index to store and sort root nodes regarding to their position...
//					int[] positions = subsetState.getPositions(element);
//					if(positions.length==0) {
//						//old version without positions...
//						ElementNode node = new ElementNode(element, null);
//						nodes.add(node);
//					} else {
//						for (int j = 0; j < positions.length; ++j) {
//							ElementNode node = new ElementNode(element, null,
//									positions[j]);
//							nodes.add(node);
//						}
//					}
//				}
//				//finally sort array according to node index...
//				Collections.sort(nodes, new Comparator() {
//
//					public int compare(Object o1, Object o2) {
//						ElementNode n1 = (ElementNode)o1;
//						ElementNode n2 = (ElementNode)o2;
//	                    if (n1 == null || n2 == null)
//	                        return 0;
//	                    return n1.getIndex() - n2.getIndex();
//					}
//					
//				});
//				return nodes;
//			}
//			
//			public ElementNode[] postprocessRootNodes(ElementNode[] rootNodes) {
////PR 6772		//just check if each element is known, but this should have happened in filter already...
//				ArrayList nodes = new ArrayList();
//				Element[] visElements = subsetState.getVisibleElements();
//				for(int i=0;i<visElements.length;++i) {
//					String name = visElements[i].getName();
//					Element element = dimension.getElementByName(name);
//					if(element == null)
//						continue;
////PR 6773	//we use node index to store and sort root nodes regarding to their position...
//					int[] positions = subsetState.getPositions(element);
//					if(positions.length==0) {
//						//old version without positions...
//						ElementNode node = new ElementNode(element, null);
//						nodes.add(node);
//					} else {
//						for (int j = 0; j < positions.length; ++j) {
//							ElementNode node = new ElementNode(element, null,
//									positions[j]);
//							nodes.add(node);
//						}
//					}
//				}
//				//finally sort array according to node index...
//				Collections.sort(nodes, new Comparator() {
//
//					public int compare(Object o1, Object o2) {
//						ElementNode n1 = (ElementNode)o1;
//						ElementNode n2 = (ElementNode)o2;
//	                    if (n1 == null || n2 == null)
//	                        return 0;
//	                    return n1.getIndex() - n2.getIndex();
//					}
//					
//				});
//				return (ElementNode[])nodes.toArray(new ElementNode[nodes.size()]);
//
////back to old code...				
////				ArrayList nodes = new ArrayList();
////				Element[] elements = subsetState.getVisibleElements();
////				for (int i = 0; i < elements.length; ++i) {
////					String name = elements[i].getName();
////					Element element = dimension.getElementByName(name);
////					if (element == null)
////						continue;
////					// " + element);
////					ElementNode node = new ElementNode(element, null, -1);
////					nodes.add(node);
////				}
////				return (ElementNode[]) nodes.toArray(new ElementNode[0]);
//				
//			}
//		};
//	}
}

class FlatStateFilter implements HierarchyFilter {

	private Hierarchy hierarchy;
	private final SubsetState subsetState;
	private final Set elementSet;
	
	FlatStateFilter(SubsetState subsetState) {
		this.subsetState = subsetState;
		elementSet = new HashSet();
		elementSet.addAll(Arrays.asList(subsetState.getVisibleElements())); //state.getElements());
	}

	public void init(Hierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}

	public boolean acceptElement(Element element) {
		return elementSet.contains(element); // .getName());
	}

	public boolean isFlat() {
		return true;
	}


	final void collectRootNodes(List out) {
		Element[] visElements = subsetState.getVisibleElements();
		for(int i=0;i<visElements.length;++i) {
//			String name = visElements[i].getName();
//			Element element = dimension.getElementByName(name);			
//			if(element == null)
//				continue;
			Element element = visElements[i];
//PR 6773	//we use node index to store and sort root nodes regarding to their position...
			int[] positions = subsetState.getPositions(element);
			if(positions.length==0) {
				//old version without positions...
				ElementNode node = new ElementNode(element, null);
				out.add(node);
			} else {
				for (int j = 0; j < positions.length; ++j) {
					ElementNode node = new ElementNode(element, null,
							positions[j]);
					out.add(node);
				}
			}
		}
		//finally sort array according to node index...
		Collections.sort(out, new Comparator() {

			public int compare(Object o1, Object o2) {
				ElementNode n1 = (ElementNode)o1;
				ElementNode n2 = (ElementNode)o2;
                if (n1 == null || n2 == null)
                    return 0;
                return n1.getIndex() - n2.getIndex();
			}
			
		});
	}
	
	public ElementNode[] postprocessRootNodes(ElementNode[] rootNodes) {
//PR 6772		//just check if each element is known, but this should have happened in filter already...
		ArrayList nodes = new ArrayList();
		Element[] visElements = subsetState.getVisibleElements();
		for(int i=0;i<visElements.length;++i) {
			String name = visElements[i].getName();
			Element element = hierarchy.getElementByName(name);
			if(element == null)
				continue;
//PR 6773	//we use node index to store and sort root nodes regarding to their position...
			int[] positions = subsetState.getPositions(element);
			if(positions.length==0) {
				//old version without positions...
				ElementNode node = new ElementNode(element, null);
				nodes.add(node);
			} else {
				for (int j = 0; j < positions.length; ++j) {
					ElementNode node = new ElementNode(element, null,
							positions[j]);
					nodes.add(node);
				}
			}
		}
		//finally sort array according to node index...
		Collections.sort(nodes, new Comparator() {

			public int compare(Object o1, Object o2) {
				ElementNode n1 = (ElementNode)o1;
				ElementNode n2 = (ElementNode)o2;
                if (n1 == null || n2 == null)
                    return 0;
                return n1.getIndex() - n2.getIndex();
			}
			
		});
		return (ElementNode[])nodes.toArray(new ElementNode[nodes.size()]);

//back to old code...				
//		ArrayList nodes = new ArrayList();
//		Element[] elements = subsetState.getVisibleElements();
//		for (int i = 0; i < elements.length; ++i) {
//			String name = elements[i].getName();
//			Element element = dimension.getElementByName(name);
//			if (element == null)
//				continue;
//			// " + element);
//			ElementNode node = new ElementNode(element, null, -1);
//			nodes.add(node);
//		}
//		return (ElementNode[]) nodes.toArray(new ElementNode[0]);
		
	}
	
}
