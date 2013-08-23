/*
*
* @file ElementNode.java
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
* @version $Id: ElementNode.java,v 1.22 2010/03/04 09:12:36 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2005. All rights reserved.
 */
package org.palo.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <code>ElementNode</code>
 * <p>ElementNodes are used to construct a tree of consolidations for
 * a given dimension. Since elements can be consolidated multiple times, it is 
 * not practicable to use them as is for representing tree-nodes in the
 * consolidation-hierarchy instead ElementNodes are used. Each element-node
 * wraps an element where a single element can be wrapped in multiple
 * ElementNodes. The wrapped element can be retrieved by invoking
 * {@link #getElement()}.
 * </p>
 *
 * @author Stepan Rutz
 * @version $Id: ElementNode.java,v 1.22 2010/03/04 09:12:36 PhilippBouillon Exp $
 */
public class ElementNode
{
    protected final Element element;
    private final Consolidation consolidation;
    private int index;
    private String name;
    private ElementNode parent;
    protected ArrayList<ElementNode> children;
//    protected LinkedHashSet<ElementNode> children;
    
    /**
     * Constructs a new <code>ElementNode</code>
     * @param element the {@link Element} to wrap.
     */
    public ElementNode(Element element)
    {
        this(element, null);
    }
    
    /**
     * Constructs a new <code>ElementNode</code>
     * @param element the {@link Element} to wrap.
     * @param consolidation the {@link Consolidation} of this node.
     */
    public ElementNode(Element element, Consolidation consolidation)
    {
        this(element, consolidation, -1);
    }
    
    /**
     * Constructs a new <code>ElementNode</code>
     * @param element the {@link Element} to wrap.
     * @param consolidation the {@link Consolidation} of this node.
     * @param index index in parent (optional)
     */
    public ElementNode(Element element, Consolidation consolidation, int index)
    {
        this.element = element;
        this.consolidation = consolidation;
        this.index = index;
//        this.children = new LinkedHashSet<ElementNode>(1);
        this.children = new ArrayList<ElementNode>(1);
        setName(element.getName());
    }
    
    //-------------------------------------------------------------------------
    // basic
    public final String getName() {
    	return name;
    }
    public final void setName(String name) {
    	this.name = name;
    }
    
    /**
     * Returns the optional index of this instance or -1 if no index was set.
     * @param the index of this instance or -1 if none was set
     */
    public final int getIndex() {
    	return index;
    }
    /**
     * Returns the wrapped {@link Element}.
     * @return the wrapped {@link Element}.
     */
    public final Element getElement()
    {
        return element;
    }
    
    /**
     * Returns the {@link Consolidation} of this instance.
     * @return the {@link Consolidation} of this instance.
     */
    public final Consolidation getConsolidation()
    {
        return consolidation;
    }
    
    public final synchronized void setParent(ElementNode parent)
    {
        this.parent = parent;
    }
    
    public final synchronized void setChildren(ElementNode [] kids) {
    	if (kids == null) {
    		return;
    	}
    	for (ElementNode k: kids) {
    		forceAddChild(k);
    	}
    }
    
    /**
     * Returns the parent <code>ElementNode</code> or
     * <code>null</code>.
     * @return the parent <code>ElementNode</code> or
     * <code>null</code>.
     */
    public final synchronized ElementNode getParent()
    {
        return parent;
    }
    
    /**
     * Returns the depth of this <code>ElementNode</code>
     * in the consolidation hierarchy.
     * @return the depth of this <code>ElementNode</code>
     */
    public final synchronized int getDepth()
    {
        if (parent == null)
            return 0;
        return 1 + parent.getDepth();
    }
    
    //-------------------------------------------------------------------------
    // children
    
    /**
     * Adds a child to this <code>ElementNode</code>.
     * Note: This is an internal method and it is not
     * required to invoke it under most circumstances.
     * 
     * @param child the child to add.
     */
    public final synchronized void addChild(ElementNode child)
    {
    	if (!children.contains(child)) {
			children.add(child);
			child.setParent(this);
			if(child.index == -1)
				child.index = children.size() - 1;
		}
    }
    
    public final void forceAddChild(ElementNode child) {
    	children.add(child);
    	child.setParent(this);
		if(child.index == -1)
			child.index = children.size() - 1;    	
    }
    
    public final void forceAddChild(ElementNode child, int index) {
    	children.add(index, child);
    	child.setParent(this);
		if(child.index == -1)
			child.index = index;    	    	
    }
    
    public final int indexOf(ElementNode child) {
    	return children.indexOf(child);
    }
    
    /**
     * Removes a child from this <code>ElementNode</code>.
     * Note: This is an internal method and it is not
     * required to invoke it under most circumstances.
     * 
     * @param child the child to remove.
     */
    public final synchronized void removeChild(ElementNode child)
    {
        if(children.remove(child)) {
        	child.setParent(null);
        }
    }
    
    /**
     * Removes all children from this <code>ElementNode</code>.
     * Note: This is an internal method and it is not
     * required to invoke it under most circumstances.
     */
    public final synchronized void removeChildren()
    {
    	Iterator<ElementNode> allChildren = children.iterator();
    	while(allChildren.hasNext()) {
    		ElementNode child = allChildren.next();
    		child.setParent(null);
    		allChildren.remove();
    	}
//        for (int i = children.size() - 1; i >= 0; --i)
//        {
//            ElementNode child = (ElementNode) children.get(i);
//            children.remove(child);
//            child.setParent(null);
//        }
    }
    
    /**
     * Returns the children of this <code>ElementNode</code>.
     * @return the children of this <code>ElementNode</code>.
     */
    public synchronized ElementNode[] getChildren()
    {
        return (ElementNode[]) children.toArray(
            new ElementNode[children.size()]);
    }
    
    public int getChildCount() {
    	return children.size();
    }
    
    /**
     * Returns whether this <code>ElementNode</code> has any children.
     * @return whether this <code>ElementNode</code> has any children.
     */
    public synchronized boolean hasChildren()
    {
        return children.size() > 0;
    }
    
    
    //-------------------------------------------------------------------------
    // object overrides
    public final String toString()
    {
        return "ElementNode (" + Integer.toHexString(System.identityHashCode(this)) + "/" + Integer.toHexString(hashCode()) + " " + element.toString() + ")";
    }
    
    public final boolean equals(Object obj)
    {
        if (!(obj instanceof ElementNode))
            return false;
        
        ElementNode other = (ElementNode) obj;
        
        boolean eq = element.equals(other.getElement());
        if (parent != null && other.getParent() != null)
        {
            eq &= parent.getElement().equals(other.getParent().getElement());
        }
        else
        {
            eq &= parent == null && other.getParent() == null;
        }
        
        if (index != -1 && other.index != -1)
        {
            eq &= index == other.index;
        }
        
        return eq;
    }
    
    public final int hashCode() {
		int hc = 3;
		hc += 3 * element.hashCode();
		if (parent != null)
			hc += 3 * parent.hashCode();
		if (index != -1)
			hc += 3 * index;

		return hc;
	}
}

