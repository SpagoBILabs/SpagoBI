/*
*
* @file ElementNode2.java
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
* @version $Id: ElementNode2.java,v 1.4 2010/02/09 11:44:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.api.impl;

import org.palo.api.Consolidation;
import org.palo.api.Element;
import org.palo.api.ElementNode;


/**
 * <code>ElementNode2</code>
 * <b>INTERNAL CLASS ONLY</b>
 *
 * @author ArndHouben
 * @version $Id: ElementNode2.java,v 1.4 2010/02/09 11:44:57 PhilippBouillon Exp $
 **/
class ElementNode2 extends ElementNode {

	
    /**
	 * Constructs a new <code>ElementNode</code>
	 * @param element the {@link Element} to wrap.
	 */
	ElementNode2(Element element) {
		this(element, null);
	}

	/**
	 * Constructs a new <code>ElementNode</code>
	 * @param element the {@link Element} to wrap.
	 * @param consolidation the {@link Consolidation} of this node.
	 */
	ElementNode2(Element element, Consolidation consolidation) {
		this(element, consolidation, -1);
	}

	/**
	 * Constructs a new <code>ElementNode</code>
	 * @param element the {@link Element} to wrap.
	 * @param consolidation the {@link Consolidation} of this node.
	 * @param index index in parent (optional)
	 */
	ElementNode2(Element element, Consolidation consolidation, int index) {
		super(element, consolidation, index);
	}

	
    public final synchronized boolean hasChildren() {
        return (element.getChildCount() > 0);
    }
    
    public final synchronized ElementNode[] getChildren() {
		if (element.getChildCount() > 0 && children.isEmpty()) {
			//load them
			Element[] _children = element.getChildren();
			Consolidation consolidations[] = element.getConsolidations();
			for (int i = 0; i < _children.length; ++i) {
				if (_children[i] == null)
					continue;
				ElementNode child = new ElementNode2(_children[i],
						consolidations[i]);
				forceAddChild(child);
			}
		}
		return (ElementNode[]) children
				.toArray(new ElementNode[children.size()]);
	}


}
