/*
*
* @file ConsolidationImpl.java
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
* @version $Id: ConsolidationImpl.java,v 1.7 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2005. All rights reserved.
 */
package org.palo.api.impl;

import org.palo.api.Consolidation;
import org.palo.api.Element;

/**
 * <code>ConsolidationInfoImpl</code>
 *
 * @author Stepan Rutz
 * @version $ID$
 */
class ConsolidationImpl implements Consolidation
{
    //-------------------------------------------------------------------------
    // FACTORY
	//
//    static ConsolidationImpl getInstance(
//        ConnectionImpl connection,
//        Element parent,
//        Element element,
//        double weight)
//    {
//        Map cache = connection.getCache(ConsolidationImpl.class);
//        
//        ConsolidationImpl c = new ConsolidationImpl(
//            parent,
//            element,
//            weight);
//        CompoundKey k = c.createKey();
//        ConsolidationImpl cached;
//        if ((cached = (ConsolidationImpl) cache.get(k)) != null)
//            return cached;
//        cache.put(k, c);
//        return c;
//    }
    
    final static ConsolidationImpl create(ConnectionImpl connection,
			Element parent, Element element, double weight) {
		return new ConsolidationImpl(parent, element, weight);
	}
    
    //-------------------------------------------------------------------------
    // instance
    
    private final Element parent;
    private final Element child;
    private final double weight;
    
    private ConsolidationImpl(Element parent, Element child, double weight)
    {
        this.parent = parent;
        this.child = child;
        this.weight = weight;
    }
    
    private final CompoundKey createKey()
    {
        return new CompoundKey(new Object[] {
            ConsolidationImpl.class,
            this.parent,
            this.child,
            new Double(weight),
        });
    }
    
    public final Element getParent()
    {
        return parent;
    }
    
    public final Element getChild()
    {
        return child;
    }
    
    public final double getWeight()
    {
        return weight;
    }
    
    public final boolean equals(Object obj)
    {
        if (!(obj instanceof ConsolidationImpl))
            return false;
        ConsolidationImpl other = (ConsolidationImpl) obj;
        return createKey().equals(other.createKey());
    }
    
    public final int hashCode()
    {
        return createKey().hashCode();
    }
}
