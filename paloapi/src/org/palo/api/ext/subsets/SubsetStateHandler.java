/*
*
* @file SubsetStateHandler.java
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
* @version $Id: SubsetStateHandler.java,v 1.5 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.ext.subsets;

import org.palo.api.Dimension;
import org.palo.api.DimensionFilter;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.HierarchyFilter;
import org.palo.api.Subset;
import org.palo.api.SubsetState;

/**
 * The <code>SubsetStateHandler</code> interface defines the methods which
 * are required to determine the visible {@link Element}s from a {@link Subset}
 * for a certain {@link SubsetState}. A <code>SubsetStateHandler</code> should
 * be registered to a <code>SubsetHandlerRegsitry</code> which returns a
 * <code>SubsetHandler</code> to access the visible elements for the subset.
 * 
 * @author ArndHouben
 * @version $Id: SubsetStateHandler.java,v 1.5 2009/04/29 10:21:58 PhilippBouillon Exp $
 * @deprecated Legacy subsets are not supported anymore! 
 * Please use {@link Dimension#getSubsetHandler()} instead! 
 */
public interface SubsetStateHandler {

	/**
	 * Initialilze the handler with the given <code>Subset</code> and
	 * <code>SubsetState</code> 
	 * @param subset the <code>Subset</code> to use
	 * @param subsetState <code>SubsetState</code> to use
	 */
	void use(Subset subset, SubsetState subsetState);
	
	/**
	 * Returns the currently used <code>Subset</code>
	 * @return currently used <code>Subset</code> or null if no subset was set
	 * before
	 */
	Subset getSubset();
	
	/**
	 * Returns the currently used <code>SubsetState</code>
	 * @return currently used <code>SubsetState</code> or null if no state
	 * was set before
	 */
	SubsetState getSubsetState();
	
	/**
	 * Creates a <code>{@link DimensionFilter}</code> to determine the visible
	 * elements for the currently used <code>Subset</code> and 
	 * <code>SubsetState</code>
	 * @param dimension the subset's <code>Dimension</code>
	 * @return a new <code>DimensionFilter</code> instance
	 */
	DimensionFilter createDimensionFilter(Dimension dimension);
	
	/**
	 * Creates a <code>{@link HierarchyFilter}</code> to determine the visible
	 * elements for the currently used <code>Subset</code> and 
	 * <code>SubsetState</code>
	 * @param dimension the subset's <code>Hierarchy</code>
	 * @return a new <code>HierarchyFilter</code> instance
	 */
	HierarchyFilter createHierarchyFilter(Hierarchy hierarchy);
	
}
