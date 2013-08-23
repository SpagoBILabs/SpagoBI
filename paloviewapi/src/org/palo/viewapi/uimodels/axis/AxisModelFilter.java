/*
*
* @file AxisModelFilter.java
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
* @version $Id: AxisModelFilter.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.uimodels.axis;

import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.subsets.Subset2;
import org.palo.viewapi.Axis;


/**
 * <code>AxisTreeFilter</code>
 * An AxisTreeFilter can be used to filter an AxisTree for certain elements.
 * Thus, it can be decided, if an AxisItem (or an element) should belong to the
 * AxisTree or not.
 * 
 * @version $Id: AxisModelFilter.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $ 
 **/
public class AxisModelFilter {

//	private HashMap<Hierarchy, Subset2> subsets;
//	private HashSet<AxisHierarchy>
	
	
	/**
	 * Creates a new AxisModelFilter for the specified axis.
	 * @param axis the axis on which this filter operates.
	 */
	public AxisModelFilter(Axis axis) {
//		this.subsets = new HashMap<Dimension, Subset2>();
//		Dimension[] dimensions = axis.getDimensions();
//		for(Dimension dimension : dimensions)
//			subsets.put(dimension, axis.getActiveSubset(dimension));
	}
	
	/**
	 * Tells the filter to use the specified subset as an indicator if an
	 * element belongs to the tree or not.
	 * @param subset the subset to use.
	 */
	public final void use(Subset2 subset) {
//		subsets.put(subset.getDimension(), subset);
	}
	
	/**
	 * Returns true if the filter accepts the given item, false otherwise.
	 * @param item the item to check if belonging to the tree or not.
	 * @return true if the filter accepts the given item, false otherwise.
	 */
	public final boolean accept(AxisItem item) {
		return accept(item.getElement());
	}
	
	/**
	 * Returns true if the filter accepts the given element, false otherwise.
	 * @param element the element to check if belonging to the tree or not.
	 * @return true if the filter accepts the given element, false otherwise.
	 */
	public final boolean accept(Element element) {
		boolean accepted = true;
//		Dimension dimension = element.getDimension();
//		Subset2 subset = subsets.get(dimension);
//		if(subset != null)
//			accepted &= subset.contains(element);
		return accepted;
	}
	
	public final boolean accept(ElementNode element) {
		boolean accepted = true;
		// ...place acceptance code here...
		return accepted;
	}
}
