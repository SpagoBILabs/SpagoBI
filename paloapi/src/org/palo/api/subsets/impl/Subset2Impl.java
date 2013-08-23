/*
*
* @file Subset2Impl.java
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
* @version $Id: Subset2Impl.java,v 1.15 2009/12/16 12:33:25 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.impl.CompoundKey;
import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.SubsetFilter;
import org.palo.api.subsets.filter.EffectiveFilter;
import org.palo.api.subsets.filter.PicklistFilter;
import org.palo.api.subsets.filter.RestrictiveFilter;
import org.palo.api.subsets.filter.SortingFilter;
import org.palo.api.subsets.filter.StructuralFilter;

/**
 * <code>Subset2Impl</code>
 * <p>
 * Api internal implementation of the {@link Subset2} interface.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: Subset2Impl.java,v 1.15 2009/12/16 12:33:25 PhilippBouillon Exp $
 **/
class Subset2Impl implements Subset2 {

	private final String id;
	private final Hierarchy hierarchy;
	
	private String name;
	private String description;
	private int indent = 1; //PR 7076: 0 should not be possible...
//	private final String[] aliases = new String[2];
	
	/* contains the ids of all elements which are in this subset */
	private final HashSet<String> elements = new HashSet<String>();
	/* contains all active subset filters */
	private final HashMap<Integer, SubsetFilter> filters = new HashMap<Integer, SubsetFilter>();
	/* contains the element root nodes */
	private final ArrayList<ElementNode> rootNodes = new ArrayList<ElementNode>();
	private final CompoundKey key;
	private final int type;
	
	private boolean modified;
	
	/**
	 * Creates a new <code>Subset2Impl</code> instance with the given id, name
	 * and type for the given dimension
	 * @param id the subset identifier
	 * @param name the subset name
	 * @param dimension the dimension to which this subset belongs
	 * @param type the subset type, i.e. one of the predefined subset type 
	 * constants
	 */
	Subset2Impl(String id, String name, Hierarchy hierarchy, int type) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.hierarchy = hierarchy;
		this.key = new CompoundKey(new Object[] { Subset2Impl.class,
				hierarchy.getDimension().getDatabase().getId(),
				hierarchy.getId(), id,
				type });
		reset();
	}
	
	
	public final Subset2 copy() {
		Subset2Impl copy = new Subset2Impl(id, name, hierarchy, type);		
		//copy all fields...
		copy.indent = indent;
		copy.description = description;
		//and all filters...
		for(SubsetFilter filter : filters.values()) {
			copy.add(filter.copy());
		}
		return copy;
	}
	
	public final String getDescription() {
		return description;
	}
	
	public final String getId() {
		return id;
	}
	
	public final int getType() {
		return type;
	}
		
	public final void modified() {
		modified = true;
	}
	
	public final boolean contains(Element element) {
		applyFilters();
		return elements.contains(element.getId());
	}

	public final String[] getElementIds() {
		applyFilters();
		return elements.toArray(new String[elements.size()]);
	}
	
	
	public final Element[] getElements() {
		applyFilters();
		Element[] _elements = new Element[elements.size()];
		int i = 0;
		for(String elID : elements) {
			Element element = hierarchy.getElementById(elID);
			_elements[i++] = element;
		}
		return _elements;
	}

	
	public final ElementNode[] getHierarchy() {
		return getRootNodes();
	}
	
	public final ElementNode[] getRootNodes() {
		applyFilters();
		return rootNodes.toArray(new ElementNode[rootNodes.size()]);		
	}

	
	public final void reset() {
		for(SubsetFilter filter : filters.values())
			filter.unbind();
		filters.clear();
		elements.clear();
		modified();
	}

	
	public final void save() throws PaloAPIException {
		try {
			Database database = hierarchy.getDimension().getDatabase();
			SubsetStorageHandlerImpl storageHandler = 
				(SubsetStorageHandlerImpl)database.getSubsetStorageHandler();
			storageHandler.save(this);
		} catch (PaloIOException pio) {
			throw new PaloAPIException("Failed to save subset '" + getName()
					+ "'!", pio);
		}
	}

	
	public final void add(SubsetFilter filter) {
		if(filter == null)
			return;
		if (filters.put(filter.getType(), filter) == null) {
			filter.bind(this);
			modified();
		}
	}

	
	public final SubsetFilter[] getFilters() {
		return filters.values().toArray(new SubsetFilter[filters.size()]);
	}

	public final boolean isActive(int filterType) {
		return filters.containsKey(filterType);
	}
	
	public final void remove(SubsetFilter filter) {
		if(filter == null)
			return;
		if (filters.remove(filter.getType()) != null) {
			filter.unbind();
			modified();
		}
	}

	
	public final SubsetFilter getFilter(int type) {
		return filters.get(new Integer(type));
	}

	public final int getIndent() {
		return indent;
	}
	
	public final void setIndent(int indent) {
		//PR 7076: 0 should not be possible...
		if(indent < 1)
			indent = 1;
		this.indent = indent;
		modified();
	}

	
	public final String getName() {
		return name;
	}
	
	public final void rename(String newName) {
		try {
			Database database = hierarchy.getDimension().getDatabase();
			SubsetStorageHandlerImpl storageHandler = 
				(SubsetStorageHandlerImpl)database.getSubsetStorageHandler();
			storageHandler.rename(this, newName);
			name = newName;
		} catch (PaloIOException pio) {
			throw new PaloAPIException("Failed to rename subset '" + getName()
					+ "'!", pio);
		}
	}
	
	public Dimension getDimension() {
		return hierarchy.getDimension(); 
	}
	
	public Hierarchy getDimHierarchy() {
		return hierarchy;
	}

	public final void setDescription(String description) {
		this.description = description;
	}
	
	
	public final boolean equals(Object obj) {
		if(!(obj instanceof Subset2))
			return false;
		
		Subset2Impl other = (Subset2Impl)obj;
		return key.equals(other.key);
	}
	
	public final int hashCode() {
		return key.hashCode();
	}


	public boolean canBeModified() {
		return true;
	}

	public boolean canCreateChildren() {
		return false;
	}
	
	private final void applyFilters() {
		if (modified) {
			modified = false;
			rootNodes.clear();
			this.elements.clear();
			HashSet<Element> elements = new HashSet<Element>(Arrays
					.asList(hierarchy.getElements()));

			// -0- ensure that we have a sorting filter...
			Integer type_sort = new Integer(SubsetFilter.TYPE_SORTING);
			SubsetFilter sortfilter = filters.get(type_sort);
			if (sortfilter == null) {
				sortfilter = new SortingFilter(hierarchy);
				sortfilter.initialize();
				filters.put(type_sort, sortfilter);
			}

			// -1- activate all affective filter
			for (SubsetFilter filter : filters.values()) {
				if (filter instanceof EffectiveFilter) {
					EffectiveFilter effectiveFilter = (EffectiveFilter) filter;
					int[] effectiveTypes = effectiveFilter.getEffectiveFilter();
					for (int type : effectiveTypes) {
						SubsetFilter effectiveSubsetFilter = filters.get(new Integer(
								type));
						// add only if affected filter is active...
						if (effectiveSubsetFilter != null)						
							effectiveSubsetFilter.add(effectiveFilter); 
					}
				}
			}
			// -2- apply restrictive filter
			for (SubsetFilter filter : filters.values()) {
				if (filter instanceof RestrictiveFilter) {
					((RestrictiveFilter) filter).filter(elements);
				}
			}
			// -3a- picklist merge
			SubsetFilter filter = filters.get(new Integer(
					SubsetFilter.TYPE_PICKLIST));
			if (filter != null)
				((PicklistFilter) filter).merge(elements);

			// -3b- sort
			filter = filters.get(type_sort);
			if (filter == null) {
				filter = new SortingFilter(hierarchy);
				filter.initialize();
			}
			List<ElementNode> sortedNodes = ((SortingFilter) filter)
					.sort(elements);
			((StructuralFilter) filter).filter(sortedNodes, elements);

			// -3c- picklist front/back
			filter = filters.get(new Integer(SubsetFilter.TYPE_PICKLIST));
			if (filter != null && filter instanceof StructuralFilter)
				((StructuralFilter) filter).filter(sortedNodes, elements);

			// -3d- revolve
			filter = filters.get(new Integer(SubsetFilter.TYPE_HIERARCHICAL));
			if (filter != null)
				((StructuralFilter) filter).filter(sortedNodes, elements);

			for (Element element : elements)
				this.elements.add(element.getId());

			rootNodes.addAll(sortedNodes);
		}
	}

	public String getDefinition() {
		try {
			Database database = hierarchy.getDimension().getDatabase();
			SubsetStorageHandlerImpl storageHandler = 
				(SubsetStorageHandlerImpl)database.getSubsetStorageHandler();
			return storageHandler.getDefinition(this);
		} catch (PaloIOException pio) {
			throw new PaloAPIException("Failed to get definition of subset '" + getName()
					+ "'!", pio);
		}		
	}

	public Subset2 setDefinition(String definition) {
		try {
			Database database = hierarchy.getDimension().getDatabase();
			SubsetStorageHandlerImpl storageHandler = 
				(SubsetStorageHandlerImpl)database.getSubsetStorageHandler();
			storageHandler.setDefinition(this, definition);
			return storageHandler.load(id, getDimHierarchy(), getType(), (SubsetHandlerImpl) getDimHierarchy().getSubsetHandler());
		} catch (PaloIOException pio) {
			throw new PaloAPIException("Failed to get definition of subset '" + getName()
					+ "'!", pio);
		}		
	}


	public boolean validate() {
		try {
			Database database = hierarchy.getDimension().getDatabase();
			SubsetStorageHandlerImpl storageHandler = 
				(SubsetStorageHandlerImpl)database.getSubsetStorageHandler();
			storageHandler.validate(this);
			return true;
		} catch (PaloIOException pio) {
			throw new PaloAPIException("Validation of subset '" + getName()
					+ "' failed!", pio);
		}		
	}
}
