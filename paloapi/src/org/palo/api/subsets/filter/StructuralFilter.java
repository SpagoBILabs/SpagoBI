/*
*
* @file StructuralFilter.java
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
* @version $Id: StructuralFilter.java,v 1.4 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter;

import java.util.List;
import java.util.Set;

import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.subsets.SubsetFilter;

/**
 * <code>StructuralFilter</code>
 * <p>
 * A structural filter is used to influence the order of subset elements.
 * </p>
 * 
 * @author ArndHouben
 * @version $Id: StructuralFilter.java,v 1.4 2009/04/29 10:21:58 PhilippBouillon Exp $
 */
public interface StructuralFilter extends SubsetFilter {

	/**
	 * Filters the given element hierarchy represented by 
	 * <code>ElementNode</code>s. The given list contains only the root nodes.
	 * The second parameter contains all currently used elements.
	 * @param hierarchy the <code>ElementNode</code> hierarchy to filter
	 * @param elements the currently used <code>Element</code>s
	 * @return the new, filtered hierarchy
	 */
	public void filter(List<ElementNode> nodes, Set<Element> elements);
}
