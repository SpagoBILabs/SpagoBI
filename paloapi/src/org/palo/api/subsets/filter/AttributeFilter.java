/*
*
* @file AttributeFilter.java
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
* @version $Id: AttributeFilter.java,v 1.15 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.palo.api.Attribute;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.subsets.filter.settings.AttributeConstraint;
import org.palo.api.subsets.filter.settings.AttributeConstraintsMatrix;
import org.palo.api.subsets.filter.settings.AttributeFilterSetting;

/**
 * <code>AttributeFilter</code>
 * <p>
 * An attribute filter belongs to the category of restrictive filters.
 * Subset elements are filtered by their corresponding attribute values. 
 * Therefore the filter setting, {@link AttributeFilterSetting}, defines a
 * matrix of attribute constraints. An accepted element has to fulfill at least
 * one row of this matrix. A column of this matrix contains all defined 
 * constraints for one attribute.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: AttributeFilter.java,v 1.15 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
public class AttributeFilter extends AbstractSubsetFilter implements RestrictiveFilter {

	private final AttributeFilterSetting setting;
	
	/**
	 * Creates a new <code>AttributeFilter</code> instance for the given 
	 * dimension
	 * @param dimension the dimension to create the filter for
	 * @deprecated use {@link AttributeFilter#AttributeFilter(Hierarchy)} instead.
	 */
	public AttributeFilter(Dimension dimension) {
		this(dimension.getDefaultHierarchy(), new AttributeFilterSetting());
	}
	
	/**
	 * Creates a new <code>AttributeFilter</code> instance for the given 
	 * hierarchy
	 * @param hierarchy the hierarchy to create the filter for
	 */
	public AttributeFilter(Hierarchy hierarchy) {
		this(hierarchy, new AttributeFilterSetting());
	}
	
	/**
	 * Creates a new <code>AttributeFilter</code> instance for the given 
	 * dimension with the given settings
	 * @param dimension the dimension to create the filter for
	 * @param setting the filter settings to use
	 * @deprecated use {@link AttributeFilter#AttributeFilter(Hierarchy, AttributeFilterSetting)} instead.
	 */
	public AttributeFilter(Dimension dimension, AttributeFilterSetting setting) {
		super(dimension.getDefaultHierarchy());
		this.setting = setting;
	}
	
	/**
	 * Creates a new <code>AttributeFilter</code> instance for the given 
	 * hierarchy with the given settings
	 * @param hierarchy the hierarchy to create the filter for
	 * @param setting the filter settings to use
	 */
	public AttributeFilter(Hierarchy hierarchy, AttributeFilterSetting setting) {
		super(hierarchy);
		this.setting = setting;
	}

	public final AttributeFilter copy() {
		AttributeFilter copy = new AttributeFilter(hierarchy);
		copy.getSettings().adapt(setting);
		return copy;
	}

	public final AttributeFilterSetting getSettings() {
		return setting;
	}
	
	
	public final void filter(Set<Element> elements) {
		AttributeConstraintsMatrix filterMatrix = 
			(AttributeConstraintsMatrix) setting.getFilterConstraints().getValue();
		if(filterMatrix.getRowsCount() == 0)
			return; //matrix is empty...
		List<Element> newElements = new ArrayList<Element>();
		for(Element element : elements) {
			if(accept(element, filterMatrix))
				newElements.add(element);
		}
		//PR 6869: add new elements even if we have none:
		elements.clear();
		elements.addAll(newElements);
//		
//		if(newElements.size()>0) {
//			elements.clear();
//			elements.addAll(newElements);
//		}
	}

	
	public final int getType() {
		return TYPE_ATTRIBUTE;
	}

	
	public final void initialize() {
	}

	public final void validateSettings() throws PaloIOException {
		//at least one constraint must be set!
		if(!getSettings().hasFilterConsraints())
			throw new PaloIOException(
				"AttributeFilter: at least one attribute constraint must be added!");
			
		/* all settings are optional */
//		if (setting.getFilterColumns().length == 0)
//			throw new PaloIOException(
//					"AttributeFilter: at least one filter column must exist!");
	}

	private final boolean accept(Element element,
			AttributeConstraintsMatrix filterMatrix) {
		for (int i = 0, n = filterMatrix.getRowsCount(); i < n; ++i) {
			AttributeConstraint[] row = filterMatrix.getRow(i);
			if (row != null && rowFulfilled(row, element))
				return true;
		}
		return false;
	}
	
	private final boolean rowFulfilled(AttributeConstraint[] row,
			Element element) {
		for (AttributeConstraint constraint : row) {
			Attribute attribute = 
				hierarchy.getAttribute(constraint.getAttributeId());
			if (attribute != null) {
				String attrValue = element.getAttributeValue(attribute)
						.toString();
				if (!constraint.accept(attrValue,attribute.getType())) {
					return false;
				}
			}
		}
		return true;
	}
}
