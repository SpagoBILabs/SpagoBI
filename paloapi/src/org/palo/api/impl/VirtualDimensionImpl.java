/*
*
* @file VirtualDimensionImpl.java
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
* @author Stepan Rutz
*
* @version $Id$
*
*/

/*
 * (c) Tensegrity Software 2005. All rights reserved.
 */
package org.palo.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.palo.api.Attribute;
import org.palo.api.Consolidation;
import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.ElementNodeVisitor;
import org.palo.api.Hierarchy;
import org.palo.api.Subset;
import org.palo.api.VirtualObject;
import org.palo.api.subsets.SubsetHandler;

import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.HierarchyInfo;

/**
 * <code></code>
 *
 * @author Stepan Rutz
 * @version $ID$
 */
class VirtualDimensionImpl implements Dimension, VirtualObject
{
    private final Dimension sourceDimension;
    private LinkedHashMap velements2elements;
    private LinkedHashMap elements2velements;
    private Map name2element;
    private Map id2element;
    private final ArrayList rootNodes = new ArrayList();
    private Element elements[];
    private Hierarchy activeHierarchy;
    private Hierarchy hierarchy;
//    private boolean hasCustomRootNodes;
    private Object virtualDefinition;
    
//    VirtualDimensionImpl(Dimension sourceDimension, DimensionFilter filter)
//    {
//        if (sourceDimension == null)
//            throw new IllegalArgumentException("sourceDimension cannot be null");
//        this.sourceDimension = sourceDimension;
//        
//        filter.init(this);
//        initElements(filter);
//    }
//    
    //alternative constructor...
    VirtualDimensionImpl(Dimension sourceDimension, Element[] elements, ElementNode[] rootNodes,boolean isFlat, Hierarchy activeHierarchy) {
    	this.sourceDimension = sourceDimension;
    	this.activeHierarchy = activeHierarchy;
    	initElements(elements,rootNodes,isFlat);
    }
    
    public final String getId() {
    	return sourceDimension.getId() + "@@" + Integer.toHexString(System.identityHashCode(this));
    }
    
    public Dimension getSourceDimension()
    {
        return sourceDimension;
    }
    
    public int getExtendedType()
    {
        return DIMENSIONEXTENDEDTYPE_VIRTUAL;
    }
    
    public String getName()
    {
        return sourceDimension.getName() + "@@" + Integer.toHexString(System.identityHashCode(this));
    }
    
    public Database getDatabase()
    {
        return sourceDimension.getDatabase();
    }
    
	public final Cube[] getCubes() {
		DatabaseImpl database = (DatabaseImpl)sourceDimension.getDatabase();
		return database.getCubes(sourceDimension);
	}
	
//	public final Cube[] getCubes(int type) {
//		DatabaseImpl database = (DatabaseImpl)sourceDimension.getDatabase();
//		return database.getCubes(sourceDimension,type);
//	}

    public int getElementCount()
    {
        //return sourceDimension.getElementCount();
        return elements.length;
    }
    
    public Element getElementAt(int index)
    {
        //return sourceDimension.getElementAt(index);
        return elements[index];
    }
    
    public Element[] getElements()
    {
        //return sourceDimension.getElements();
        return (Element[]) elements.clone();
    }
    
    public String[] getElementNames()
    {
        //return sourceDimension.getElementNames();
        if (elements == null)
            return new String[0];
        String names[] = new String[elements.length];
        for (int i = 0; i < names.length; ++i)
        {
            names[i] = elements[i].getName();
        }
        return names;
    }
    
    public Element getElementByName(String name)
    {
        //return sourceDimension.getElementByName(name);
        return (Element) name2element.get(name);
    }

    public Element getElementById(String id)
    {
        return (Element) id2element.get(id);
    }

    public void rename(String name)
    {
        Util.noopWarning();
    }
    
    public void addElements(String names[], int types[])
    {
        Util.noopWarning();
    }
    
    public void updateConsolidations(Consolidation [] cons) {
    	Util.noopWarning();
    }
    
    public void removeConsolidations(Element [] elements) {
    	Util.noopWarning();
    }
    
    //-------------------------------------------------------------------------
    // Element/Consolidation Hierarchies
    
    public Element[] getRootElements()
    {
    	ArrayList out = new ArrayList();
		for (int i = 0, n = rootNodes.size(); i < n; ++i) {
			ElementNode node = (ElementNode) rootNodes.get(i);
			out.add(node.getElement());
		}
		return (Element[]) out.toArray(new Element[0]);

// if (hasCustomRootNodes)
// {
// ArrayList out = new ArrayList();
//            for (int i = 0; i < rootNodes.size(); ++i)
//            {
//                ElementNode node = (ElementNode) rootNodes.get(i);
//                out.add(node.getElement());
//            }
//            return (Element[]) out.toArray(new Element[0]);
//        }
//        
//        //return sourceDimension.getRootElements();
//        ArrayList roots = new ArrayList();
//        if (elements != null)
//        {
//            for (int i = 0; i < elements.length; ++i)
//            {
//                Element element = elements[i];
//                if (element.getParentCount() == 0)
//                    roots.add(element);
//                
//                else
//                {
//                    int realParentCount = 0;
//                    Element parents[] = element.getParents();
//                    for (int j = 0; j < parents.length; ++j)
//                    {
//                        if (velements2elements.containsKey(parents[j]))
//                            ++realParentCount;
//                    }
//                    
//                    if (realParentCount == 0)
//                        roots.add(element);
//                }
//            }
//        }
//        
//        final Map sourceelement2index = new HashMap();
//        ElementNode sources[] = sourceDimension.getAllElementNodes();
//        for (int i = 0; i < sources.length; ++i)
//        {
//            Element source = sources[i].getElement();
//            // use first occurence of element for sorting
//            if (!sourceelement2index.containsKey(source))
//                sourceelement2index.put(source, new Integer(i));
//        }
//        Collections.sort(roots, new Comparator() {
//            public int compare(Object o1, Object o2)
//            {
//                VirtualElementImpl target1 = (VirtualElementImpl) o1;
//                VirtualElementImpl target2 = (VirtualElementImpl) o2;
//                Integer index1 = (Integer) sourceelement2index.get(target1.getSourceElement());
//                Integer index2 = (Integer) sourceelement2index.get(target2.getSourceElement());
//                if (index1 == null || index2 == null)
//                    return 0;
//                return index1.intValue() - index2.intValue();
//            }
//        });
//        
//        return (Element[]) roots.toArray(new Element[0]);
    }
    
    public Element[] getElementsInOrder()
    {
        //return sourceDimension.getElementsInOrder();
        final ArrayList result = new ArrayList();
//        DimensionUtil.ElementVisitor visitor = new DimensionUtil.ElementVisitor() {
//            public void visit(Element element, Element parent)
//            {
//                result.add(element);
//            }
//        };        
//        Element roots[] = getRootElements();
//        for (int i = 0; i < roots.length; ++i)
//            DimensionUtil.traverse(roots[i], visitor);
        ElementNodeVisitor visitor = new ElementNodeVisitor() {
			public void visit(ElementNode elementNode, ElementNode parent) {
				result.add(elementNode.getElement());
			}
        };
        visitElementTree(visitor);
        return (Element[]) result.toArray(new Element[0]);
    }
    
    public ElementNode[] getElementsTree()
    {
        //return sourceDimension.getElementsTree();
        return (ElementNode[]) rootNodes.toArray(new ElementNode[rootNodes.size()]);
    }
    
    public void visitElementTree(ElementNodeVisitor visitor)
    {
    	
    	// ArrayList out = new ArrayList();
		for (int i = 0, n = rootNodes.size(); i < n; ++i) {
			ElementNode rootNode = (ElementNode) rootNodes.get(i);
			traverse(rootNode, null, visitor);
//			rootNode.removeChildren();
			
//PR 6772 since traverse will add children to node again, we go with fresh nodes here...
//			ElementNode newRootNode = new ElementNode(rootNode.getElement(),null);
//			DimensionUtil.traverse(newRootNode,visitor); //rootNode, visitor);
		}
		return;
    	
        // sourceDimension.visitElementTree(visitor);
        
//        if (hasCustomRootNodes)
//        {
//            //ArrayList out = new ArrayList();
//            for (int i = 0; i < rootNodes.size(); ++i)
//            {
//                ElementNode rootNode = (ElementNode) rootNodes.get(i);
//                DimensionUtil.traverse(rootNode, visitor);
//            }
//            return;
//        }
//        
//        Element roots[] = getRootElements();
//        for (int i = 0; i < roots.length; ++i)
//        {
//            ElementNode rootNode = new ElementNode(roots[i], null);
//            DimensionUtil.traverse(rootNode, visitor);
//        }
    }
    
    public ElementNode[] getAllElementNodes()
    {
        //return sourceDimension.getAllElementNodes();
        final ArrayList allnodes = new ArrayList();
        ElementNodeVisitor visitor = new ElementNodeVisitor() {
            public void visit(ElementNode node, ElementNode parent)
            {
                allnodes.add(node);
            }
        };
        Element roots[] = getRootElements();
        for (int i = 0; i < roots.length; ++i)
        {
            ElementNode rootNode = new ElementNode(roots[i], null);
            DimensionUtil.traverse(rootNode, visitor);
        }
        return (ElementNode[]) allnodes.toArray(new ElementNode[0]);
    }
    
    public void dumpElementsTree()
    {
        Util.noopWarning();
        //sourceDimension.dumpElementsTree();
    }
    
    //-------------------------------------------------------------------------
    // Admin
    public Element addElement(String name, int type)
    {
        Util.noopWarning();
        return null;
    }
    
    public void removeElement(Element element)
    {
        Util.noopWarning();
    }
    
	public void removeElements(Element[] elements) {
		Util.noopWarning();		
	}

    
    public void renameElement(Element element,String name) {
    	Util.noopWarning();
//    	//adjust hash look up 
//    	name2element.remove(element.getName());
//    	name2element.put(name,element);
//    	//rename element afterwards
//    	element.rename(name);
    }

    public Consolidation newConsolidation(Element element, Element parent, double weight)
    {
        Util.noopWarning();
        return null;
    }

    
    //-------------------------------------------------------------------------
    // Init elements
//    private void initElements(final DimensionFilter filter)
//    {
//        
//        velements2elements = new LinkedHashMap();
//        elements2velements = new LinkedHashMap();
//        
//        final boolean isFlat = filter != null && filter.isFlat();
//        
//        Element sourceElements[] = sourceDimension.getElements();
//        if (sourceElements == null)
//            sourceElements = new Element[0];
//        for (int i = 0; i < sourceElements.length; ++i)
//        {
//            Element element = sourceElements[i];
//            if (filter != null && !filter.acceptElement(element))
//                continue;
//            VirtualElementImpl velement = new VirtualElementImpl(this, isFlat, element);
//            velements2elements.put(velement, element);
//            elements2velements.put(element, velement);
//        }
//        
//        rootNodes = new ArrayList();
////        final LinkedHashMap velementNodes2elementNodes = new LinkedHashMap();
//        final LinkedHashMap elementNodes2velementNodes = new LinkedHashMap();
//        sourceDimension.visitElementTree(new ElementNodeVisitor() {
//            public void visit(ElementNode elementNode, ElementNode parent)
//            {
//                Element element = elementNode.getElement();
//                Element velement = (Element) elements2velements.get(element);
//                
//                if (velement == null)
//                {
//                    return;
//                }
//                else if (isFlat)
//                {
//                    ElementNode velementNode = new ElementNode(velement, null);
//                    rootNodes.add(velementNode);
//                }
//                else
//                {
//                    Consolidation vconsolidation = null;
//                    if (elementNode.getConsolidation() != null)
//                    {
//                        Consolidation consolidation = elementNode.getConsolidation();
//                        Element vparent = (Element) elements2velements.get(consolidation.getParent());
//                        Element vchild = (Element) elements2velements.get(consolidation.getChild());
//                        vconsolidation = new VirtualConsolidationImpl(
//                            vparent, vchild, consolidation.getWeight());
//                    }
//                        
//                    ElementNode velementNode = new ElementNode(velement, vconsolidation);
////                    velementNodes2elementNodes.put(velementNode, elementNode);
//                    elementNodes2velementNodes.put(elementNode, velementNode);
//                    
//                    ElementNode vparent;
//                    if ((vparent = (ElementNode) elementNodes2velementNodes.get(parent)) != null)
//                    {
//                        vparent.addChild(velementNode);
//                        velementNode.setParent(vparent);
//                    }
//                    else
//                    {
//                        rootNodes.add(velementNode);
//                    }
//                }
//            }
//        });
//        
//        this.elements = (Element[]) velements2elements.keySet().toArray(new Element[0]);
//        
//        name2element = new HashMap();
//        id2element = new HashMap();
//        for (int i = 0; i < elements.length; ++i)
//        {
//            Element element = elements[i];
//            name2element.put(element.getName(), element);
//            id2element.put(element.getId(),element);
//        }
//        
//        if (filter != null && filter.isFlat())
//        {
//            ElementNode result[] = filter.postprocessRootNodes(
//                (ElementNode[])rootNodes.toArray(new ElementNode[0]));
//            if (result != null)
//            {
//                rootNodes.clear();
//                rootNodes.addAll(Arrays.asList(result));
//                hasCustomRootNodes = true;
//            }
//        }
//        
//        final boolean DUMP_TREE = false;
//        if (DUMP_TREE)
//        {
//            visitElementTree(new ElementNodeVisitor() {
//                
//                private int getDepth(ElementNode node)
//                {
//                    return (node.getParent() == null) ? 0 : 1 + getDepth(node.getParent());
//                }
//                
//                public void visit(ElementNode elementNode, ElementNode parent)
//                {
//                    int depth = getDepth(elementNode);
//                    for (int i = 0; i < depth * 2; ++i)
//                        System.err.print(' ');
//                    System.err.println (elementNode + "   parent => " + parent);
//                }
//            });
//        }
//    }
    
//    Element lookupVirtualElement(Element element)
//    {
//        return (Element) elements2velements.get(element);
//    }

	public Attribute addAttribute(String name) {
		return sourceDimension.addAttribute(name);
	}

	public void removeAttribute(Attribute attribute) {
		sourceDimension.removeAttribute(attribute);
	}

	public void removeAllAttributes() {
		sourceDimension.removeAllAttributes();
	}


	public Dimension getAttributeDimension() {
		return sourceDimension.getAttributeDimension();
	}

	public Cube getAttributeCube() {
		return sourceDimension.getAttributeCube();
	}

	public Attribute[] getAttributes() {
		return sourceDimension.getAttributes();
	}
	
	public Attribute getAttribute(String id) {
		return sourceDimension.getAttribute(id);
	}

	public Attribute getAttributeByName(String name) {
		return sourceDimension.getAttributeByName(name);
	}

	public boolean isAttributeDimension() {
		return false;
	}


	public Object[] getAttributeValues(Attribute[] attributes, Element[] elements) {
		return sourceDimension.getAttributeValues(attributes, elements);
	}


	public void setAttributeValues(Attribute[] attributes, Element[] elements, Object[] values) {
		sourceDimension.setAttributeValues(attributes, elements, values);
	}

	public Subset getSubset(String id) {
		return sourceDimension.getSubset(id);
	}

	public Subset[] getSubsets() {
		return sourceDimension.getSubsets();
	}

	public boolean isSubsetDimension() {
		return false;
	}

	//TODO should we support add/removeSubset() ????
	public void removeSubset(Subset subset) {
		sourceDimension.removeSubset(subset);
	}

	public Subset addSubset(String name) {
		return sourceDimension.addSubset(name);
	}

//	public Subset addSubset(String id, String name) {
//		return sourceDimension.addSubset(id, name);
//	}
	
	public int getMaxDepth() {
		return sourceDimension.getMaxDepth();
	}

	/*public int getMaxIndent() {
		return sourceDimension.getMaxIndent();
	}*/

	public int getMaxLevel() {
		return sourceDimension.getMaxLevel();
	}

	private final void initElements(Element[] newElements, ElementNode[] elNodes,
			boolean isFlat) {
//		rootNodes.clear(); // = new ArrayList();
		elements = new Element[newElements.length];
		name2element = new HashMap();
	    id2element = new HashMap();
	        
		velements2elements = new LinkedHashMap();
		elements2velements = new LinkedHashMap();
		for (int i = 0; i < newElements.length; ++i) {			
			VirtualElementImpl velement = new VirtualElementImpl(this, isFlat,
					newElements[i]);
			elements[i] = velement;
			velements2elements.put(velement, newElements[i]);
			elements2velements.put(newElements[i], velement);
			name2element.put(elements[i].getName(), elements[i]);
			id2element.put(elements[i].getId(),elements[i]);
		}
		for(int i=0;i<elNodes.length;++i) {
			checkNode(elNodes[i], null, new LinkedHashMap(), rootNodes, isFlat);
		}
		//PR 6663: do we still need hasCustomRootNodes ?? 
		//Yes!! It is used for flat filters => see PR 6733
//        if (isFlat)
//        	hasCustomRootNodes=true;
        	
	}
		
	private final void checkNode(ElementNode node, ElementNode parent,
			Map elNodes2velNodes, List rootNodes, boolean isFlat) {
		Element element = node.getElement();
		Element vElement = (Element) elements2velements.get(element);
		if (vElement != null) {
			if (isFlat) {
				ElementNode velementNode = new ElementNode(vElement, null);
				rootNodes.add(velementNode);
			} else {
				Consolidation vconsolidation = node.getConsolidation();
				if (vconsolidation != null) {
					VirtualElementImpl vparent = (VirtualElementImpl) elements2velements
							.get(vconsolidation.getParent());
//WAS IST WENN VPARENT == NULL ? => DANN KEINE KONSOLIDIERUNG!!!????!!!
					if (vparent != null) {
						Element vchild = (Element) elements2velements
								.get(vconsolidation.getChild());
						vconsolidation = new VirtualConsolidationImpl(vparent,
								vchild, vconsolidation.getWeight());
						vparent.addConsolidation(vconsolidation);
					} else
						vconsolidation = null;
				}
				ElementNode velNode = new ElementNode(vElement, vconsolidation);
				elNodes2velNodes.put(node, velNode);
				ElementNode vparent;
				if ((vparent = (ElementNode) elNodes2velNodes.get(parent)) != null) {
					vparent.forceAddChild(velNode);
					velNode.setParent(vparent);
				} else {
					rootNodes.add(velNode);
				}
			}
		}
		// check children:
		ElementNode[] children = node.getChildren();
		for (int i = 0; i < children.length; ++i)
			checkNode(children[i], node, elNodes2velNodes, rootNodes, isFlat);
	}

	public boolean isSystemDimension() {
		return sourceDimension.isSystemDimension();
	}

	public boolean isUserInfoDimension() {
		return sourceDimension.isUserInfoDimension();
	}
	
	public final void setVirtualDefinition(Object virtualDefinition) {
		this.virtualDefinition = virtualDefinition;
	}
	public final Object getVirtualDefinition() {
		return virtualDefinition;
	}

	private final void traverse(ElementNode node, ElementNode parent, ElementNodeVisitor visitor) {
		visitor.visit(node, parent);
		ElementNode[] children = node.getChildren();
		for(ElementNode child : children)
			traverse(child, node, visitor);
	}

	public SubsetHandler getSubsetHandler() {
		return sourceDimension.getSubsetHandler();
	}

	public boolean canBeModified() {
		return true;
	}

	public boolean canCreateChildren() {
		return true;
	}

	public int getType() {
		return 0;
	}

	public Hierarchy[] getHierarchies() {
		return sourceDimension.getHierarchies();
	}

	public String[] getHierarchiesIds() {
		return sourceDimension.getHierarchiesIds();
	}

	public Hierarchy getHierarchyAt(int index) {
		return sourceDimension.getHierarchyAt(index);
	}

	public Hierarchy getHierarchyById(String id) {
		return sourceDimension.getHierarchyById(id);
	}

	public int getHierarchyCount() {
		return sourceDimension.getHierarchyCount();
	}
		
	public Hierarchy getDefaultHierarchy() {
		return sourceDimension.getDefaultHierarchy();
	}

	public Hierarchy getHierarchyByName(String name) {
		return sourceDimension.getHierarchyByName(name);
	}
	
	public final void reload(boolean doEvents) {
		sourceDimension.reload(doEvents);
	}

	public final DimensionInfo getInfo() {
		return sourceDimension.getInfo();
	}
	
	public void addElements(String[] names, int type, Element[][] children,
			double[][] weights) {
		// TODO Auto-generated method stub
		
	}

	public void addElements(String[] names, int[] types, Element[][] children,
			double[][] weights) {
		// TODO Auto-generated method stub
		
	}
}
