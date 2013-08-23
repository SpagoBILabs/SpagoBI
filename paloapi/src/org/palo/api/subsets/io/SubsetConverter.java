/*
*
* @file SubsetConverter.java
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
* @version $Id: SubsetConverter.java,v 1.13 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.palo.api.Attribute;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.Subset;
import org.palo.api.SubsetState;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.ext.subsets.SubsetHandlerRegistry;
import org.palo.api.ext.subsets.states.FlatState;
import org.palo.api.ext.subsets.states.HierarchicalState;
import org.palo.api.ext.subsets.states.RegExState;
import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.SubsetFilter;
import org.palo.api.subsets.SubsetHandler;
import org.palo.api.subsets.filter.AliasFilter;
import org.palo.api.subsets.filter.PicklistFilter;
import org.palo.api.subsets.filter.SortingFilter;
import org.palo.api.subsets.filter.TextFilter;
import org.palo.api.subsets.filter.settings.AliasFilterSetting;
import org.palo.api.subsets.filter.settings.PicklistFilterSetting;
import org.palo.api.subsets.filter.settings.SortingFilterSetting;
import org.palo.api.subsets.filter.settings.TextFilterSetting;


/**
 * <code>SubsetConverter</code>
 * <p>
 * This class is used to convert old subsets into new subets.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: SubsetConverter.java,v 1.13 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
class SubsetConverter {

	
	/**
	 * Converts the given legacy subsets into new subsets of given type. 
	 * Specify <code>true</code> for remove parameter to delete old subsets 
	 * definitions.
	 * @param subsets the legacy subset definitions to convert
	 * @param type the type of new subsets
	 * @param remove specify <code>true</code> to delete old subset definitions,
	 * <code>false</code> otherwise
	 * @throws PaloIOException to signal that some subsets could not be transformed. 
	 * In this case {@link PaloIOException#getData()} contains an array with 
	 * the names of the failed subsets
	 */
	final void convert(Subset[] subsets, int type, boolean remove) throws PaloIOException {
		ArrayList<String> failedSubsets = new ArrayList<String>();
		for(Subset subset : subsets) {
			try {
				Subset2 newSubset = toNewSubset(subset,type);
				newSubset.save();
			} catch (PaloIOException e) {
				failedSubsets.add(subset.getName());
			}
			if(remove) {
				Dimension dimension = subset.getDimension();
				try {
					dimension.removeSubset(subset);
				} catch (PaloAPIException pex) {
					System.err.println("failed to remove legacy subset '"
							+ subset.getName() + "'!");
				}
			}
		}
		if(!failedSubsets.isEmpty()) {
			PaloIOException paloEx =
				new PaloIOException("Some subset transformation failed!");
			paloEx.setData(failedSubsets.toArray(new String[0]));
			throw paloEx;
		}
	}
	
	private final Subset2 toNewSubset(Subset subset, int type) throws PaloIOException {
		// required:
		String name = subset.getName();
//		name = name + OLD_SUFFIX;
		Dimension dimension = subset.getDimension();
		SubsetHandler subHandler = dimension.getSubsetHandler();
		
		Subset2 newSubset = null;				
		String subId = subHandler.getSubsetId(name, type);
		if (subId == null) 
			newSubset = subHandler.addSubset(name, type);
		else 
			newSubset = subHandler.getSubset(subId, type);
		
		if (newSubset != null) {
//PR 6905: the "global" legacy alias has only preview character, so no need to 
//			create filter for it
//			ArrayList<String> aliases = new ArrayList<String>();
//			Attribute alias = subset.getAlias();
//			if (alias != null)
//				aliases.add(alias.getName());			
			
			// we can add only one filter namely the active state...
			SubsetState state = subset.getActiveState();
			String stateId = state.getId();
			if(stateId.equals(HierarchicalState.ID)) {
				addHierarchicalFilter(newSubset, subset);
			} 
			else if(stateId.equals(RegExState.ID)) {
				//do we have a search attribute in old state?
				Attribute alias = state.getSearchAttribute();
				if (alias != null) {
					newSubset.add(createAliasFilter(subset, alias.getId()));
				}
				
				//textfilter:
				newSubset.add(createTextFilter(subset, state));
				//PR 7001: on regex we have to create a hierarchy too:
		    	SubsetFilter sortFilter = 
		    		newSubset.getFilter(SubsetFilter.TYPE_SORTING);
		    	if(sortFilter == null) {
		    		sortFilter = new SortingFilter(subset.getDimension());
		    		newSubset.add(sortFilter);
		    	}
		    	SortingFilterSetting settings = 
		    		(SortingFilterSetting)sortFilter.getSettings();
		    	settings.setSortCriteria(SortingFilterSetting.SORT_CRITERIA_DEFINITION);
		    	settings.setHierarchicalMode(SortingFilterSetting.HIERARCHICAL_MODE_SHOW_CHILDREN);
			}
			else if(stateId.equals(FlatState.ID)) {
				//PR 6906 try to get same order as in legacy flat subset...
		    	org.palo.api.ext.subsets.SubsetHandler handler = 
		    		SubsetHandlerRegistry.getInstance().getHandler(subset, FlatState.ID);
		    	List<ElementNode> nodes = handler != null ?
		    			handler.getVisibleRootNodesAsList() : new ArrayList<ElementNode>();
		    	Element[] elements = new Element[nodes.size()];
		    	for(int i=0;i<elements.length;++i)
		    		elements[i] = nodes.get(i).getElement();
		    	
		    	//picklist:
				newSubset.add(createPickList(subset, elements));
			}
		}
		return newSubset;
	}
	
	private final TextFilter createTextFilter(Subset subset, SubsetState state) {		
		TextFilter txtFilter = new TextFilter(subset.getDimension());
		TextFilterSetting tfInfo = txtFilter.getSettings();
		tfInfo.addExpression(state.getExpression());
		tfInfo.setExtended(true);
		return txtFilter;
	}
	
	private final PicklistFilter createPickList(Subset subset, Element[] elements) {
		PicklistFilter pickList = new PicklistFilter(subset.getDimension());
		PicklistFilterSetting plInfo = pickList.getSettings();
		for(int i=0;i<elements.length;++i)
			plInfo.addElement(elements[i].getId());
		plInfo.setInsertMode(PicklistFilterSetting.INSERT_MODE_SUB);
		return pickList;
	}
	
	private final AliasFilter createAliasFilter(Subset subset, String alias) {
		AliasFilterSetting setting = new AliasFilterSetting();
		setting.setAlias(2,alias);
		return new AliasFilter(subset.getDimension(), setting);
	}
	
	private final void addHierarchicalFilter(Subset2 newSubset, Subset subset) {
		SubsetState state = subset.getState(HierarchicalState.ID);
		//the hierarchical filter is a mix of sortfilter and picklist,
		SortingFilter sortFilter = new SortingFilter(subset.getDimension());
		sortFilter.getSettings().setHierarchicalMode(SortingFilterSetting.HIERARCHICAL_MODE_SHOW_CHILDREN);
		Element[] elements = state.getVisibleElements();
		//PR 6906: we sort the elemens before adding them...
		sort(elements);
		PicklistFilter pickList = createPickList(subset, elements);
		pickList.getSettings().setInsertMode(PicklistFilterSetting.INSERT_MODE_SUB);
		newSubset.add(sortFilter);
		newSubset.add(pickList);
	}
		
	private final void sort(Element[] elements) {
		if(elements.length==0)
			return;
		//we sort it in order:
		Hierarchy hierarchy = elements[0].getHierarchy();
		ElementComparator comparator = new ElementComparator(hierarchy);
		Arrays.sort(elements, comparator);
	}
}

class ElementComparator implements Comparator<Element> {
	
	private final HashMap<String, Integer> lookup = 
		new HashMap<String, Integer>();
	
	public ElementComparator(Hierarchy hierarchy) {
		fillLookUpTable(hierarchy);
	}
	public int compare(Element o1, Element o2) {
		if(o1 != null && o2 != null) {
			int p1 = lookup.get(o1.getName());
			int p2 = lookup.get(o2.getName());
			if(p1 < p2)
				return -1;
			else if(p1 > p2)
				return 1;
		}
		return 0;
	}

	private final void fillLookUpTable(Hierarchy hierarchy) {
		lookup.clear();
		Element[] elInOrder = hierarchy.getElementsInOrder();
		for(int i=0;i<elInOrder.length;++i)
			lookup.put(elInOrder[i].getName(), new Integer(i));
	}
}
