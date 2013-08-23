/*
*
* @file PicklistFilter.java
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
* @version $Id: PicklistFilter.java,v 1.8 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.subsets.filter.settings.IntegerParameter;
import org.palo.api.subsets.filter.settings.PicklistFilterSetting;

/**
 * <code>PicklistFilter</code>
 * <p>
 * A picklist filter is a restrictive filter as well as a structural filter.
 * It is restrictive in the sense that elements which go into the subset can be 
 * selected. It is structural in the sense that those elements can be sorted if
 * added in front or at the end of current subset elements list.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: PicklistFilter.java,v 1.8 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
public class PicklistFilter extends AbstractSubsetFilter implements RestrictiveFilter, StructuralFilter {

	private final PicklistFilterSetting setting;
	
	/**
	 * Creates a new <code>PicklistFilter</code> instance for the given 
	 * dimension
	 * @param dimension the dimension to create the filter for
	 * @deprecated use {@link PicklistFilter#PicklistFilter(Hierarchy)} instead.
	 */
	public PicklistFilter(Dimension dimension) {
		this(dimension, new PicklistFilterSetting());
	}
	
	/**
	 * Creates a new <code>PicklistFilter</code> instance for the given 
	 * hierarchy
	 * @param hierarchy the hierarchy to create the filter for
	 */
	public PicklistFilter(Hierarchy hierarchy) {
		this(hierarchy, new PicklistFilterSetting());
	}
	
	/**
	 * Creates a new <code>PicklistFilter</code> instance for the given 
	 * dimension with the given settings
	 * @param dimension the dimension to create the filter for
	 * @param setting the filter settings to use
	 * @deprecated use {@link PicklistFilter#PicklistFilter(Hierarchy, PicklistFilterSetting)} instead.
	 */
	public PicklistFilter(Dimension dimension, PicklistFilterSetting setting) {
		super(dimension);
		this.setting = setting;
	}

	/**
	 * Creates a new <code>PicklistFilter</code> instance for the given 
	 * hierarchy with the given settings
	 * @param hierarchy the hierarchy to create the filter for
	 * @param setting the filter settings to use
	 */
	public PicklistFilter(Hierarchy hierarchy, PicklistFilterSetting setting) {
		super(hierarchy);
		this.setting = setting;
	}

	public final PicklistFilter copy() {
		PicklistFilter copy = new PicklistFilter(hierarchy);
		copy.getSettings().adapt(setting);
		return copy;
	}

	public final PicklistFilterSetting getSettings() {
		return setting;
	}
	
	/**
	 * Inserts picked elements at front or back of the given element hierarchy.
	 * Has only effect if filter insert mode is set to either 
	 * {@link PicklistFilterSetting#INSERT_MODE_BACK} or
	 * {@link PicklistFilterSetting#INSERT_MODE_FRONT}
	 * @param hiers the current subset element hierarchy 
	 */
	public final void filter(List<ElementNode> hiers, Set<Element> elements) {
		int insertMode = setting.getInsertMode().getValue();
		HashSet<String> pickedElements = 
			(HashSet<String>) setting.getSelection().getValue();
		if (insertMode == PicklistFilterSetting.INSERT_MODE_BACK) {
			for(String id : pickedElements) {
				Element element = hierarchy.getElementById(id);
				if(element != null) {
					hiers.add(new ElementNode(element));
					elements.add(element);
				}
			}
		} else if(insertMode == PicklistFilterSetting.INSERT_MODE_FRONT) {
			int index = 0;
			for(String id : pickedElements) {
				Element element = hierarchy.getElementById(id);
				if(element != null) {
					hiers.add(index++,new ElementNode(element));
					elements.add(element);
				}
			}
		}
	}
	
	/**
	 * Filters the given elements list, i.e. only the picked elements of the 
	 * list are kept. Has only effect if filter insert mode is set to 
	 * {@link PicklistFilterSetting#INSERT_MODE_SUB}.
	 * @param elements an element list 
	 */
	public final void filter(Set<Element> elements) {
		IntegerParameter modeParam = setting.getInsertMode();
		if (modeParam.getValue() == PicklistFilterSetting.INSERT_MODE_SUB) {
			// actually its vice versa ;)
			HashSet<String> pickedElements = 
				(HashSet<String>) setting.getSelection().getValue();
			Iterator<Element> allElements = elements.iterator();
			while (allElements.hasNext()) {
				Element element = allElements.next();
				if (!pickedElements.contains(element.getId()))
					allElements.remove();
			}
		}
	}


	/**
	 * Merges the picked elements into the given element list. Has only effect
	 * if filter insert mode is set to {@link PicklistFilterSetting#INSERT_MODE_MERGE}.
	 * @param elements the element list to merge picked elements into
	 */
	public final void merge(Set<Element> elements) {
		IntegerParameter modeParam = setting.getInsertMode();
		if (modeParam.getValue() == PicklistFilterSetting.INSERT_MODE_MERGE) {
			HashSet<String> pickedElements = 
				(HashSet<String>) setting.getSelection().getValue();
			for(String id : pickedElements) {
				Element element = hierarchy.getElementById(id);
				if(element != null)
					elements.add(element);
			}
		}
	}

	public final int getType() {
		return TYPE_PICKLIST;
	}

	
	public final void initialize() {
	}

	
	public final void resetInternal() {
		setting.reset();
	}

	public final void validateSettings() throws PaloIOException {
		/* all settings are optional */
	}
}
