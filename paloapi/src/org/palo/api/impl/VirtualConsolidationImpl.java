/*
*
* @file VirtualConsolidationImpl.java
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

import org.palo.api.Consolidation;
import org.palo.api.Element;

/**
 * <code></code>
 *
 * @author Stepan Rutz
 * @version $ID$
 */
class VirtualConsolidationImpl implements Consolidation
{
    private final Element parent;
    private final Element child;
    private final double weight;
    
    VirtualConsolidationImpl(Element parent, Element child, double weight)
    {
        this.parent = parent;
        this.child = child;
        this.weight = weight;
    }
    
    public Element getParent()
    {
        return parent;
    }
    
    public Element getChild()
    {
        return child;
    }
    
    public double getWeight()
    {
        return weight;
    }
    
    public boolean equals(Object obj) {
    	if(obj instanceof VirtualConsolidationImpl) {
    		VirtualConsolidationImpl other = (VirtualConsolidationImpl) obj;
    		return parent.equals(other.parent) && child.equals(other.child);
    	}
    	return false;
    }
    
    public int hashCode() {
    	int hc = 17;
        hc += 31 * parent.hashCode();
        hc += 31 * child.hashCode();
        hc += 31 * weight;
        return hc;

    }
}
