/*
*
* @file DimensionUtil.java
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
import org.palo.api.ElementNode;
import org.palo.api.ElementNodeVisitor;

/**
 * <code>DimensionUtil</code>
 *
 * @author Stepan Rutz
 * @version $ID$
 */
class DimensionUtil
{
    public static interface ElementVisitor
    {
        void visit(Element element, Element parent);
    }
    
    public static void traverse(Element e, ElementVisitor v)
    {
    	traverse(e, null, v);
    }
    
    static void traverse(Element e, Element p, ElementVisitor v)
    {
        v.visit(e, p);
        Element children[] = e.getChildren();
        if (children == null)        	
        	return;        
        for (int i = 0; i < children.length; ++i)
        {
            traverse(children[i], e, v);
        }
    }
    
    //-------------------------------------------------------------------------
    
    public static void traverse(ElementNode n, ElementNodeVisitor v)
    {
        traverse(n, null, v);
    }

    public static void forceTraverse(ElementNode n, ElementNodeVisitor v)
    {
        forceTraverse(n, null, v);
    }
    
    static void traverse(ElementNode n, ElementNode p, ElementNodeVisitor v)
    {
        v.visit(n, p);
        Element children[] = n.getElement().getChildren();
        Consolidation consolidations[] = n.getElement().getConsolidations();
        if (children == null)
            return;
        for (int i = 0; i < children.length; ++i)
        {
        	if(children[i] == null)
        		continue;
            ElementNode child = new ElementNode(children[i], consolidations[i]);
            n.forceAddChild(child);
            traverse(child, n, v);
        }
    }

    static void forceTraverse(ElementNode n, ElementNode p, ElementNodeVisitor v)
    {
        v.visit(n, p);
        Element children[] = n.getElement().getChildren();
        Consolidation consolidations[] = n.getElement().getConsolidations();
        if (children == null)
            return;
        int index = 0;
        for (int i = 0; i < children.length; ++i)
        {
        	if(children[i] == null)
        		continue;
            ElementNode child = new ElementNode(children[i], consolidations[i], index++);
            n.forceAddChild(child);
            forceTraverse(child, n, v);
        }
    }
    
}
