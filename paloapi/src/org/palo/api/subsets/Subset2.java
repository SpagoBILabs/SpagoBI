/*
*
* @file Subset2.java
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
* @version $Id: Subset2.java,v 1.13 2009/12/16 12:33:25 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets;

import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.PaloObject;
import org.palo.api.Subset;


/**
 * <code>Subset2</code>
 * <p>
 * A new subset definition which corresponds to the subset definitions 
 * specified by Jedox (and its excel client). The content of a subset is 
 * defined by its {@link SubsetFilter}s.
 * This subset definition is different from the old {@link Subset} in the sense
 * that all added subset filters are applied to this subset.
 * Whereas in former subset definition only one was active.
 * </p>
 * <p>Note: call {@link #applyFilters()} so that added filters show some
 * effects. </p> 
 * 
 * @author ArndHouben
 * @version $Id: Subset2.java,v 1.13 2009/12/16 12:33:25 PhilippBouillon Exp $
 **/
public interface Subset2 extends PaloObject {

	/**
	 * Subset type local, i.e. the subset access is restricted to certain users
	 */
	public static int TYPE_LOCAL = 0;
	/** 
	 * Subset type global, i.e. this subset can be read and modified by each
	 * user who can read and modify all database cubes 
	 **/
	public static int TYPE_GLOBAL = 1;
	

	/**
	 * Returns the <code>Dimension</code> to which this subset applies.
	 * @return the subset dimension
	 * @deprecated use {@link Subset2#getDimHierarchy()} instead.
	 */
	public Dimension getDimension();

	/**
	 * Returns the <code>Hierarchy</code> to which this subset applies.
	 * @return the subset hierarchy
	 */
	public Hierarchy getDimHierarchy();
	
	/**
	 * Returns the indent of this subset
	 * @return subset indent
	 */
	public int getIndent();
	/**
	 * Sets the subset indent
	 * @param indent the new subset indent
	 */
	public void setIndent(int indent);

	/**
	 * Resets this subset to its defaults, i.e. all filters and aliases are 
	 * removed.
	 */
	public void reset();
	
	/**
	 * Adds the given subset filter to the list of all subset filters which
	 * should be applied to this subset. Note that this will replace a former
	 * added filter of same type.
	 * @param the subset filter to apply to this subset
	 */
	public void add(SubsetFilter filter);
	/**
	 * Removes the given subset filter from the list of all subset filters which
	 * should be applied to this subset.
	 * @param the subset filter to remove
	 */
	public void remove(SubsetFilter filter);
	
	/**
	 * Returns all subset filters of this subset
	 * @return an array of applied subset filters
	 */
	public SubsetFilter[] getFilters();
	
	/**
	 * Returns the subset filter which corresponds to the given type
	 * @param type a valid subset filter type
	 * @return the corresponding <code>ISubsetFilter</code> or <code>null</code>
	 */
	public SubsetFilter getFilter(int type);

	/**
	 * Checks if the subset filter which corresponds to the given type is 
	 * active, i.e. {@link #getFilter(int)} returns not <code>null</code>
	 * @param filterType the type of filter to check 
	 * @return <code>true</code> if corresponding filter is active, 
	 * <code>false</code> otherwise
	 */
	public boolean isActive(int filterType);
	
	/**
	 * Saves this subset
	 */
	public void save();
	
	/**
	 * Checks if the given <code>Element</code> is inside this subset or not.
	 * @param element the <code>Element</code> to check
	 * @return <code>true</code> if <code>Element</code> is inside this subset,
	 * <code>false</code> if not.
	 */
	public boolean contains(Element element);
	
	/**
	 * Returns all <code>Elements</code> of this subset. 
	 * @return all <code>Elements</code> of this subset
	 */
	public Element[] getElements();

	/**
	 * Returns all root nodes of this subset. To retrieve all defined 
	 * <code>ElementNodes</code> the root nodes should be traversed. 
	 * @return
	 * @deprecated use {@link Subset2#getRootNodes()} instead.
	 */
	public ElementNode[] getHierarchy();
	
	/**
	 * Returns all root nodes of this subset. To retrieve all defined 
	 * <code>ElementNodes</code> the root nodes should be traversed. 
	 * @return
	 */
	public ElementNode[] getRootNodes();
	
//	/**
//	 * Applies the defined filters to this subset. Call this to reflect the
//	 * actual status of the subset.
//	 */
//	public void applyFilters();
	/**
	 * <p>Marks the subset as being modified. The consequence of this method is
	 * that all registered filters are applied the next time the subset elements
	 * are requested.</p>
	 */
	public void modified();

	/**
	 * Renames this subset.
	 * @param newName the new subset name
	 */
	public void rename(String newName);
	
	/**
	 * Returns the subset type which is one of the predefined type constants.
	 * @return the subset type
	 */
	public int getType();
	
	/**
	 * Creates a deep copy of this subset.
	 * @return the subset copy
	 */
	public Subset2 copy();
	
	/**
	 * Sets an optional description for the subset. Specifying <code>null</code>
	 * deletes the description.
	 * @param description a description of the subset
	 */
	public void setDescription(String description);	
	/**
	 * Returns the subset description or <code>null</code> if none was set.
	 * @return the subset description or <code>null</code>
	 */	
	public String getDescription();
	
	public String getDefinition();
	
	public Subset2 setDefinition(String definition);
	
	public boolean validate();
}
