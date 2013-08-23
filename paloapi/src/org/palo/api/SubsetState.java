/*
*
* @file SubsetState.java
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
* @version $Id: SubsetState.java,v 1.10 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2006. All rights reserved.
 */
package org.palo.api;

import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.SubsetFilter;

/**
 * A <code>SubsetState</code> defines the visible {@link Element}s of a 
 * {@link Subset}. Those elements are either enumerated or determined by an
 * expression, e.g. a regular expression. To support arbitrary defined 
 * hierarchies of elements it is possible to define several paths for each 
 * visible element. A path describes the location of a visible element and
 * usually consists of a comma separated list of element identifiers. However, 
 * since a path is of type {@link String} applications may use and store a 
 * different location description, but should not use a colon since it is used 
 * as path delimiter. 
 * 
 * 
 * @author ArndHouben
 * @version $Id: SubsetState.java,v 1.10 2009/04/29 10:21:57 PhilippBouillon Exp $
 * @deprecated old subset definitions are no longer supported. 
 * Please use {@link Subset2} and its {@link SubsetFilter} instead
 */
public interface SubsetState {

	/**
	 * Returns the unique state id.
	 * @return the state id.
	 */
	public String getId();
	
	/**
	 * Returns an optional name for the state.
	 * @return the state name or null if none was set
	 */
	public String getName();
	
	/** 
	 * Sets an optional name, label or description 
	 * @param name a human readable label
	 */
	public void setName(String name);
	/**
	 * Returns an optional expression to determine the visible elements
	 * @return the state expression or null if none was set
	 */
	public String getExpression();
	/**
	 * Sets an expression to determine the visible elements
	 * @param expression a state expression to describe the visible elements
	 */
	public void setExpression(String expression);
	
	/**
	 * Returns an attribute field that is used for searching with the regular 
	 * expression.
	 * @return the state search attribute field or null if none was set
	 */
	public Attribute getSearchAttribute();
	/**
	 * Sets the attribute field that is used instead of the element name when 
	 * filtering with a regular expression
	 * @param searchAttribute field used for filtering
	 */
	public void setSearchAttribute(Attribute searchAttribute);
	
	/**
	 * Returns all visible elements.
	 * <p>Note: the array could be empty but that does not necessarily mean 
	 * that no element is visible because an expression can determine the
	 * elements too.
	 * </p>
	 * @return the visible elements
	 */
	public Element[] getVisibleElements();
	/**
	 * Adds the given element to the list of all visible elements
	 * @param element the element to show
	 */
	public void addVisibleElment(Element element);
	
	/**
	 * Adds the given element to the list of all visible elements. The position
	 * parameter can be used to store additional information about the element
	 * position.
	 * @param element the element to show
	 * @param position an element position
	 */
	public void addVisibleElement(Element element, int position);
	/**
	 * Removes the given element from the list of all visible elements
	 * @param element the element to remove
	 */
	public void removeVisibleElement(Element element);
	
	/**
	 * Convenience method to remove all visible elements
	 */
	public void removeAllVisibleElements();
	
	/**
	 * Returns all paths for the given {@link Element} or <code>null</code>, if 
	 * no paths were defined. An element path consists of a comma separated list
	 * of element ids. Since an element can be referenced several times, 
	 * multiple paths are possible. 
	 * 
	 * @param element a visible element
	 * @return all existing element paths or <code>null</code>
	 */
	public String[] getPaths(Element element);
	
	/**
	 * Adds the specified path for the given visible element
	 * @param element a visible element
	 * @param path a path to the visible element
	 */
	public void addPath(Element element, String path);
	
	/**
	 * Checks if this <code>SubsetState</code> contains the specified path for
	 * the given <code>{@link Element}</code>
	 * @param element a visible element
	 * @param path a valid path to the visible element
	 * @return <code>true</code> if the path is known for the given element,
	 * <code>false</code> otherwise 
	 */
	public boolean containsPath(Element element, String path);
	
	/**
	 * Removes the specified path for the given visible element. 
	 * @param element a visible element
	 * @param path the path to remove
	 */
	public void removePath(Element element, String path);
	
	
	/**
	 * Returns the positions of the given visible element. This is useful if 
	 * subset defines no hierarchy but contains same element several times.
	 * @param element a visible element
	 * @return its positions
	 */
	public int[] getPositions(Element element);
	
	/**
	 * Checks if the given <code>{@link Element}</code> is visible within this 
	 * <code>SubsetState</code>. 
	 * @param element the <code>{@link Element}</code> to check 
	 * @return <code>true</code> if it is visible, <code>false</code> otherwise 
	 */
	public boolean isVisible(Element element);
	
}
