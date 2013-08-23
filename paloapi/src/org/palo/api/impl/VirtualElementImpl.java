/*
*
* @file VirtualElementImpl.java
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
import java.util.List;

import org.palo.api.Attribute;
import org.palo.api.Consolidation;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;

import com.tensegrity.palojava.ElementInfo;

/**
 * <code></code>
 *
 * @author Stepan Rutz
 * @version $ID$
 */
class VirtualElementImpl implements Element
{
    private final Element sourceElement;
    private final boolean isFlat;
    private final VirtualDimensionImpl vdim;
    //have to manage children and consolidation ourself, since they can be
    //different from sourceElement children and consolidation...
    private final List parents;
    private final List children;
    private final List consolidations;
    
    VirtualElementImpl (VirtualDimensionImpl vdim, boolean isFlat, Element sourceElement)
    {
        this.vdim = vdim;
        this.isFlat = isFlat;
        this.sourceElement = sourceElement;
        this.parents = new ArrayList();
        this.children = new ArrayList();
        this.consolidations = new ArrayList();
    }
    
    Element getSourceElement()
    {
        return sourceElement;
    }
    
    public String getId() {
    	//TODO is this a good idea?  anyway need this version, because element gets stored in xml...
    	return sourceElement.getId(); //+"@@"+Integer.toHexString(System.identityHashCode(this));
    }
    
    public String getName()
    {
        return sourceElement.getName();
    }
    
    public Dimension getDimension()
    {
        return vdim;
    }
    
	public Hierarchy getHierarchy() {
		// TODO will that work? Can the element be modified and is the state
		// lost?
		return sourceElement.getHierarchy();
	}
    
    public int getType()
    {
        return sourceElement.getType();
    }
    
    public void setType(int type)
    {
        Util.noopWarning();
    }
    
    public String getTypeAsString()
    {
        return sourceElement.getTypeAsString();
    }
    
    public int getDepth()
    {
        return sourceElement.getDepth();
    }
    
    public int getLevel() {
    	return sourceElement.getLevel();
    }
    
    /*public int getIndent() {
    	return sourceElement.getIndent();
    }*/
    
	public final int getPosition() {
		return sourceElement.getPosition();
	}

    
    public void rename(String name)
    {
        Util.noopWarning();
    }
    
    public int getConsolidationCount()
    {
    	return isFlat ? 0 : consolidations.size();
//        return isFlat ? 0 : sourceElement.getConsolidationCount();
    }
    
    public Consolidation getConsolidationAt(int index)
    {
        if (isFlat)
            return null;

        return (Consolidation) consolidations.get(index);
//        Consolidation consolidation = sourceElement.getConsolidationAt(index);
//        return new VirtualConsolidationImpl(
//            vdim.lookupVirtualElement(consolidation.getParent()),
//            vdim.lookupVirtualElement(consolidation.getChild()),
//            consolidation.getWeight());
    }
    
    public Consolidation[] getConsolidations() {
		if (isFlat)
			return null;
		return (Consolidation[]) consolidations.toArray(
					new Consolidation[consolidations.size()]);
		// Consolidation consolidations[] = sourceElement.getConsolidations();
		// if (consolidations == null)
		// return null;
		//        
		// for (int i = 0; i < consolidations.length; ++i)
		// {
		// Consolidation consolidation = consolidations[i];
		// consolidations[i] = new VirtualConsolidationImpl(
		// vdim.lookupVirtualElement(consolidation.getParent()),
		// vdim.lookupVirtualElement(consolidation.getChild()),
		// consolidation.getWeight());
		// }
		// return consolidations;
	}
    
    public void updateConsolidations(Consolidation consolidations[])
    {
        Util.noopWarning();
    	
//		Consolidation[] oldConsolidations = getConsolidations();
//		
//		String[] children = new String[newConsolidations.length];
//		double[] weights = new double[newConsolidations.length];
//		for (int i = 0; i < newConsolidations.length; ++i) {
//			ElementImpl child = (ElementImpl)newConsolidations[i].getChild();
//			weights[i] = newConsolidations[i].getWeight();
//			children[i] = child.getId();
//		}
//// WORKAROUND FOR BUG IN WEB PALO:
//		int elType = ELEMENTTYPE_CONSOLIDATED;
//		if(!connection.isLegacy())
//			elType = elType2infoType(elType);
//		dbConnection.update(elInfo, elType,children, weights);
//		compareConsolidations(oldConsolidations, newConsolidations,doEvents);
//    	
    }
    final void addConsolidation(Consolidation consolidation) {
//    	if(!consolidations.contains(consolidation)) {
//    		
//    	}
    	if(consolidation == null || consolidations.contains(consolidation))
    		return;
    	consolidations.add(consolidation);
    	Element parent = consolidation.getParent();
    	if(parent != this)
    		return;
    	Element child = consolidation.getChild();
    	if(!(child instanceof VirtualElementImpl))
    		return;
    	VirtualElementImpl vElement = (VirtualElementImpl)child;
    	children.add(vElement);
    	vElement.addParent(this);
    }
    
    final void addParent(Element parent) {
    	parents.add(parent);
    }
    
    public int getParentCount()
    {
    	return isFlat ? 0 : parents.size();
//        return isFlat ? 0 : sourceElement.getParentCount();
    }
    
    public Element[] getParents()
    {
        if (isFlat)
            return null;
        return (Element[])parents.toArray(new Element[parents.size()]);
//        Element[] parents = sourceElement.getParents();
//        if (parents == null)
//            return null;
//        
//        for (int i = 0; i < parents.length; ++i)
//            parents[i] = vdim.lookupVirtualElement(parents[i]);
//        return parents;
    }
    
    public int getChildCount()
    {
    	return isFlat ? 0 : children.size();
//        return isFlat ? 0 : sourceElement.getChildCount();
    }
    
    public Element[] getChildren()
    {
        if (isFlat)
            return null;
        return (Element[])children.toArray(new Element[children.size()]);
//        Element[] children = sourceElement.getChildren();
//        if (children == null)
//            return null;
//        
//        ArrayList out = new ArrayList();
//        for (int i = 0; i < children.length; ++i)
//        {
//            Element child = vdim.lookupVirtualElement(children[i]);
//            if (child != null)
//                out.add(child);
//        }
//        return (Element[]) out.toArray(new Element[out.size()]);
    }

    //--------------------------------------------------------------------------
    //PALO 1.5 - PART OF ATTRIBUTE API...
    //
	public Object getAttributeValue(Attribute attribute) {
		return sourceElement.getAttributeValue(attribute);
//		return null;
	}

	public Object[] getAttributeValues() {
		return sourceElement.getAttributeValues();
	}

	public void setAttributeValue(Attribute attribute, Object value) {
		sourceElement.setAttributeValue(attribute, value);
	}

	public void setAttributeValues(Attribute[] attributes, Object[] values) {
		sourceElement.setAttributeValues(attributes, values);
	}
    
	public final ElementInfo getInfo() {
		return ((ElementImpl)sourceElement).getInfo();
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof VirtualElementImpl) {
			return sourceElement.equals(((VirtualElementImpl)obj).sourceElement);
		} else if (obj instanceof ElementImpl) {
			return sourceElement.equals(obj);
		}
		return false;
	}

	public boolean canBeModified() {
		return true;
	}

	public boolean canCreateChildren() {
		return true;
	}

	public final void move(int newPosition) {
		sourceElement.move(newPosition);
	}
}
