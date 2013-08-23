/*
*
* @file LocalFilterImpl.java
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
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: LocalFilterImpl.java,v 1.6 2010/03/04 09:12:34 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.viewapi.LocalFilter;

/**
 * <code>LocalFilterImpl</code> TODO DOCUMENT ME
 * 
 * @version $Id: LocalFilterImpl.java,v 1.6 2010/03/04 09:12:34 PhilippBouillon Exp $
 **/
public class LocalFilterImpl implements LocalFilter {

	private final List<ElementNode> visibleRoots = new ArrayList<ElementNode>();

	public void addVisibleElement(ElementNode rootNode) {
		visibleRoots.add(rootNode);
	}
	
	public void addVisibleElement(ElementNode rootNode, int atIndex) {
		visibleRoots.add(atIndex, rootNode);
	}

	private final void removeVisibleElement(ElementNode [] roots, ElementNode node) {
		if (roots == null) {
			return;
		}
		for (ElementNode e: roots) {
			if (e.equals(node)) {
				if (e.getParent() == null) {
					visibleRoots.remove(e);
				} else {
					e.getParent().removeChild(e);
				}
				return;
			}
			removeVisibleElement(e.getChildren(), node);
		}
	}
	
	private final ElementNode findElementNode(ElementNode [] roots, ElementNode node) {
		if (roots == null) {
			return null;
		}
		for (ElementNode e: roots) {
			if (e.equals(node)) {				
				return e;
			}
			ElementNode result = findElementNode(e.getChildren(), node);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	public ElementNode findElementNode(ElementNode node) {
		return findElementNode(visibleRoots.toArray(new ElementNode[0]), node);
	}
	
	public void removeVisibleElement(ElementNode rootNode) {
		removeVisibleElement(visibleRoots.toArray(new ElementNode[0]), rootNode);
	}

	public void clear() {
		visibleRoots.clear();
	}

	public ElementNode[] getVisibleElements() {
		return visibleRoots.toArray(new ElementNode[0]);
	}
	
	public int indexOf(ElementNode nd) {
		return visibleRoots.indexOf(nd);
	}

	public boolean hasVisibleElements() {
		return !visibleRoots.isEmpty();
	}

	public boolean isVisible(final Element element) {
		LocalFilterVisitor visitor = new LocalFilterVisitor() {
			public boolean visit(ElementNode node) {
				if(node.getElement().equals(element))
					return false;
				return true;
			}
		};
		return !traverse(visibleRoots.toArray(new ElementNode[0]), visitor);
	}

	public void setVisibleElements(ElementNode[] rootNodes) {
		visibleRoots.clear();
		visibleRoots.addAll(Arrays.asList(rootNodes));
	}
	
	private boolean traverse(ElementNode[] nodes, LocalFilterVisitor visitor) {
		boolean goOn = true;
		for(ElementNode node : nodes) {
			goOn = goOn && visitor.visit(node);
			if(goOn)
				goOn = goOn && traverse(node.getChildren(), visitor);
			else
				break;
		}
		return goOn;
	}
}

interface LocalFilterVisitor {
	boolean visit(ElementNode node);
}