/*
*
* @file AbstractSubsetFilter.java
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
* @version $Id: AbstractSubsetFilter.java,v 1.7 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter;

import java.util.HashMap;

import org.palo.api.Dimension;
import org.palo.api.Hierarchy;
import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.SubsetFilter;

/**
 * <code>AbstractSubsetFilter</code>
 * <p>
 * API internal abstract implementation of the <code>SubsetFilter</code> 
 * interface. This implementation manages the affective filters for each subset
 * filter.
 * </p>
 * 
 * @author ArndHouben
 * @version $Id: AbstractSubsetFilter.java,v 1.7 2009/04/29 10:21:58 PhilippBouillon Exp $
 */
abstract class AbstractSubsetFilter implements SubsetFilter {

	protected final HashMap<Integer, EffectiveFilter> effectiveFilters =
		new HashMap<Integer, EffectiveFilter>();

	protected final Hierarchy hierarchy;
	private Subset2 subset;
	
	AbstractSubsetFilter(Dimension dimension) {
		this.hierarchy = dimension.getDefaultHierarchy();
	}
	
	AbstractSubsetFilter(Hierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}
	
	public final void add(EffectiveFilter filter) {
		effectiveFilters.put(new Integer(filter.getType()),filter);
	}

	public final void remove(EffectiveFilter filter) {
		effectiveFilters.remove(new Integer(filter.getType()));
	}
	
	public final void reset() {
		effectiveFilters.clear();
		getSettings().reset();
	}
	
	public final Subset2 getSubset() {
		return subset;
	}
	
	public final void bind(Subset2 subset) {
		this.subset = subset;
		getSettings().bind(subset);
		markDirty();
	}
	
	public final void unbind() {
		this.subset = null;
		getSettings().unbind();
	}
	
	protected final void markDirty() {
		if(subset != null)
			subset.modified();
	}
	
	public final Dimension getDimension() {
		return hierarchy.getDimension();
	}
	
	public final Hierarchy getHierarchy() {
		return hierarchy;
	}
	
	public final void adapt(SubsetFilter from) {
		if(!(from instanceof AbstractSubsetFilter))
			return;
		
		AbstractSubsetFilter fromFilter = (AbstractSubsetFilter)from;		
		reset();
		
		effectiveFilters.putAll(fromFilter.effectiveFilters);
		//settings:
		getSettings().adapt(fromFilter.getSettings());
	}

}
