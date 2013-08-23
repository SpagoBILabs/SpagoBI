/*
*
* @file SubsetFilter.java
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
* @version $Id: SubsetFilter.java,v 1.8 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets;

import org.palo.api.Hierarchy;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.subsets.filter.AliasFilter;
import org.palo.api.subsets.filter.AttributeFilter;
import org.palo.api.subsets.filter.DataFilter;
import org.palo.api.subsets.filter.EffectiveFilter;
import org.palo.api.subsets.filter.HierarchicalFilter;
import org.palo.api.subsets.filter.PicklistFilter;
import org.palo.api.subsets.filter.RestrictiveFilter;
import org.palo.api.subsets.filter.SortingFilter;
import org.palo.api.subsets.filter.StructuralFilter;
import org.palo.api.subsets.filter.TextFilter;
import org.palo.api.subsets.filter.settings.FilterSetting;

/**
 * <code>SubsetFilter</code>
 * <p>
 * A subset filter defines certain settings and criteria to determine the
 * elements within a subset. There exists three main filter categories namely 
 * {@link EffectiveFilter} which influences other filters, 
 * {@link RestrictiveFilter} which filters out subset elements and finally
 * {@link StructuralFilter} which sorts the subset elements. 
 * </p>
 * 
 * @author ArndHouben
 * @version $Id: SubsetFilter.java,v 1.8 2009/04/29 10:21:57 PhilippBouillon Exp $
 * @see {@link AliasFilter}, {@link AttributeFilter}, {@link DataFilter},
 *      {@link HierarchicalFilter}, {@link PicklistFilter},
 *      {@link SortingFilter}, {@link TextFilter}
 */
public interface SubsetFilter {
	
	
	/* supported types */
	/** type constant for textual filter */
	public static final int TYPE_TEXT = 1<<0;
	/** type constant for hierarchical filter */
	public static final int TYPE_HIERARCHICAL = 1<<1;
	/** type constant for picklist filter */
	public static final int TYPE_PICKLIST = 1<<2;
	/** type constant for data filter */
	public static final int TYPE_DATA = 1<<3;
	/** type constant for sorting filter */
	public static final int TYPE_SORTING = 1<<4;
	/** type constant for attribute filter */
	public static final int TYPE_ATTRIBUTE = 1<<5;
	/** type constant for alias filter */
	public static final int TYPE_ALIAS = 1<<6;

	
	/**
	 * Returns the filter type which is one of the defined type constants.
	 * @return the filter type
	 */
	public int getType();
	
	
	/**
	 * Resets this filter, i.e. its internal setting is switched back to its
	 * default. Clears all internal used caches too.
	 */
	void reset();
	
	/**
	 * Returns the internal settings of this filter.
	 * @return
	 */
	FilterSetting getSettings();

	/**
	 * Initializes this filter.
	 */
	public void initialize();
	
	/**
	 * Convenient method to access the subset dimension.
	 * @return the subset dimension
	 * @deprecated use {@link SubsetFilter#getHierarchy()} instead.
	 */
	//public Dimension getDimension();
	
	/**
	 * Convenient method to access the subset hierarchy.
	 * @return the subset hierarchy
	 */
	public Hierarchy getHierarchy();

	/**
	 * Adds the given <code>EffectiveFilter</code> to the list of all affective
	 * filters which affect this subset filter
	 * @param filter a filter which affects this subset filter.
	 */
	public void add(EffectiveFilter filter);
	/**
	 * Removes the given <code>EffectiveFilter</code> from the list of all 
	 * affective filters which affect this subset filter
	 * @param filter the affective filter to remove
	 */
	public void remove(EffectiveFilter filter);
	/**
	 * Adapts this subset filter from the given one. Both filter must be of 
	 * same type!
	 * @param from the subset filter to adapt from
	 */
	public void adapt(SubsetFilter from);

	/**
	 * Creates a deep copy of this subset filter
	 * @return a copy of this subset filter
	 */
	public SubsetFilter copy();
	
	/**
	 * Returns the {@link Subset2} to which this filter belongs or 
	 * <code>null</code> if this filter isn't bind to a subset yet.
	 * @return the {@link Subset2} to which this filter belongs or 
	 * <code>null</code>
	 */
	public Subset2 getSubset();
	
	/**
	 * <p>Binds this filter instance to the given {@link Subset2}</p>
	 * <b>NOTE: PLEASE DON'T USE! INTERNAL METHOD </b>
	 * @param subset 
	 */
	public void bind(Subset2 subset);
	/**
	 * <p>Releases this filter instance from a previously binded {@link Subset2}</p>
	 * <b>NOTE: PLEASE DON'T USE! INTERNAL METHOD </b>
	 */
	public void unbind();
	
	/**
	 * Checks if the internal subset settings are valid.
	 * @throws PaloIOException if internal subset settings are not valid.
	 */
	public void validateSettings() throws PaloIOException;
		
}
