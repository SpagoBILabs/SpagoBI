/*
*
* @file AliasFilter.java
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
* @version $Id: AliasFilter.java,v 1.12 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter;

import java.util.List;

import org.palo.api.Attribute;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.subsets.SubsetFilter;
import org.palo.api.subsets.filter.settings.AliasFilterSetting;

/**
 * <code>AliasFilter</code>
 * <p>
 * An alias filter belongs to the category of affective filters.
 * The influenced filters are namely the {@link TextFilter} and 
 * {@link SortingFilter}. When used the alias {@link Attribute} is used to
 * filter subset elements.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: AliasFilter.java,v 1.12 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
public class AliasFilter extends AbstractSubsetFilter implements EffectiveFilter {

	private final int[] effectiveTypes = new int[] { 
			SubsetFilter.TYPE_TEXT, SubsetFilter.TYPE_SORTING };
	
	private final AliasFilterSetting setting;
//	private final Attribute aliases;

	/**
	 * Creates a new <code>AliasFilter</code> instance for the given 
	 * dimension
	 * @param dimension the dimension to create the filter for
	 * @deprecated use {@link AliasFilter#AliasFilter(Hierarchy)} instead.
	 */
	public AliasFilter(Dimension dimension) {
		this(dimension.getDefaultHierarchy(), new AliasFilterSetting());
	}
	
	/**
	 * Creates a new <code>AliasFilter</code> instance for the given 
	 * hierarchy
	 * @param hierarchy the hierarchy to create the filter for
	 */
	public AliasFilter(Hierarchy hierarchy) {
		this(hierarchy, new AliasFilterSetting());
	}

	/**
	 * Creates a new <code>AliasFilter</code> instance for the given 
	 * dimension with the given settings
	 * @param dimension the dimension to create the filter for
	 * @param setting the filter settings to use
	 * @deprecated use {@link AliasFilter#AliasFilter(Hierarchy, AliasFilterSetting)} instead.
	 */
	public AliasFilter(Dimension dimension, AliasFilterSetting setting) {
		super(dimension.getDefaultHierarchy());
		this.setting = setting;
//		this.aliases = getAliases(dimension.getDefaultHierarchy());			
	}

	/**
	 * Creates a new <code>AliasFilter</code> instance for the given 
	 * hierarchy with the given settings
	 * @param hierarchy the hierarchy to create the filter for
	 * @param setting the filter settings to use
	 */
	public AliasFilter(Hierarchy hierarchy, AliasFilterSetting setting) {
		super(hierarchy);
		this.setting = setting;
//		this.aliases = getAliases(hierarchy);
	}

	public final AliasFilter  copy() {
		AliasFilter copy = new AliasFilter(hierarchy);
		copy.getSettings().adapt(setting);
		return copy;
	}

	public final int[] getEffectiveFilter() {
		return effectiveTypes;
	}

	/**
	 * Returns the alias name to use for the given <code>Element</code>
	 * @param element the <code>Element</code> to get the alias for
	 * @return the alias of the given element or its name if no alias was defined 
	 */
	public final String getAlias(Element element) {
		//first we check second alias:
		String alias = getAlias(element, 2);
		//check if it is empty:
		if(!alias.equals(""))
			return alias;
		//fall back to first alias
		alias = getAlias(element, 1);
		if(!alias.equals(""))
			return alias;
		//fall back to element name...
		return element.getName();
	}
	
	public final int getType() {
		return TYPE_ALIAS;
	}
	public final void initialize() {
	}
	
	public final AliasFilterSetting getSettings() {
		return setting;
	}
	public void filter(List<ElementNode> elements) {
	}

	public final void validateSettings() throws PaloIOException {
		/* all settings are optional */
	}

//	private final Attribute getAliasInternal(Element element) {
//		Attribute alias = getAlias(aliases, setting.getAlias(2));
//		if(alias != null) {
//			//check if we have a value here...
//			Object value = alias.getValue(element);
//		}
//			alias = getAlias(aliases, setting.getAlias(1));
//		return alias;
//	}
//
	private final String getAlias(Element element, int aliasNr) {
		Attribute alias = getAlias(setting.getAlias(aliasNr).getValue());
		if(alias != null) {
			Object value = alias.getValue(element);
			if(value != null)
				return value.toString();
		} 
		return "";
	}
	private final Attribute getAlias(String attrId) {
		if (attrId != null) {
			Attribute[] aliases = hierarchy.getAttributes();
			for (Attribute selAlias : aliases) {
				if (selAlias.getId().equals(attrId))
					return selAlias;
			}
		}
		return null;
	}
	
//	private final Attribute getAliases(Hierarchy hierarchy) {
//		Attribute[] attributes = hierarchy.getAttributes();
//		for(Attribute attr : attributes) {
//			if(attr.getName().equals("Alias")) 
//				return attr;
//		}
//		return null;
//	}
}
