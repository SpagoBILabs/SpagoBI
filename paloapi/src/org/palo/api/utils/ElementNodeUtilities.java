/*
*
* @file ElementNodeUtilities.java
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
* @author Arnd Houben
*
* @version $Id: ElementNodeUtilities.java,v 1.9 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.utils;

import java.util.ArrayList;
import java.util.List;

import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.ElementNodeVisitor;
import org.palo.api.Hierarchy;

/**
 * <code>ElementNodeUtilities</code>
 * <p>
 * Some useful methods for dealing with <code>{@link ElementNode}s</code>
 * </p>
 * 
 * @author Arnd Houben
 * @version $Id: ElementNodeUtilities.java,v 1.9 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
public class ElementNodeUtilities {
	

	/**
	 * Determines the path for the given <code>ElementNode</code>. The
	 * path consists of a comma separated list of element identifiers
	 * @param elNode
	 * @return
	 */
	public static final String getPath(ElementNode elNode) {
		StringBuffer path = new StringBuffer();
		path.append(elNode.getElement().getId());
		ElementNode parent;
		while((parent=elNode.getParent())!=null) {
			path.insert(0,ElementPath.ELEMENT_DELIM);
			path.insert(0,parent.getElement().getId());
			elNode = parent;
		}		
		return path.toString();
	}

	/**
	 * Determines the path for the given <code>ElementNode</code>. The
	 * path is expressed as an array of <code>{@link Element}</code>s
	 * @param elNode
	 * @return array of <code>{@link Element}</code>s which build the path to
	 * given <code>ElementNode</code>
	 */
    public static final Element[] getPathElements(ElementNode elNode) {
		ArrayList elements = new ArrayList();
		addElements(elNode, elements);
		return (Element[]) elements.toArray(new Element[elements.size()]);
	}

//    public static final ElementPath getPath(ElementNode elNode, Dimension dimension) {
//    	Element[] path = getPathElements(elNode);
//    	ElementPath elPath = new ElementPath();
//    	elPath.addPart(dimension, path);
//    	return elPath;
//    }
    
    public static final ElementPath getPath(ElementNode elNode, Hierarchy hierarchy) {
    	Element[] path = getPathElements(elNode);
    	ElementPath elPath = new ElementPath();
    	elPath.addPart(hierarchy, path);
    	return elPath;
    }

    public static final void traverse(ElementNode[] roots, ElementNodeVisitor visitor) {
		for (ElementNode root : roots) {
			traverse(root, visitor);
		}
    }
    
    private static final void traverse(ElementNode node, ElementNodeVisitor visitor) {
    	traverse(node, null, visitor);
    }
    
    private static final void addElements(ElementNode elNode, List elements) {
		// check parent
		ElementNode parent = elNode.getParent();
		if (parent != null)
			addElements(parent, elements);

		Element element = elNode.getElement();
		if (!elements.contains(element))
			elements.add(element);
	}
    
    private static void traverse(ElementNode node, ElementNode parent,    		
			ElementNodeVisitor visitor) {
		visitor.visit(node, parent);
		ElementNode[] children = node.getChildren();
		for (ElementNode child : children) {
			traverse(child, node, visitor);
		}
	}

}
