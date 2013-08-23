/*
*
* @file TextFilter.java
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
* @version $Id: TextFilter.java,v 1.12 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.subsets.filter.settings.ObjectParameter;
import org.palo.api.subsets.filter.settings.TextFilterSetting;

/**
 * <code>TextFilter</code>
 * <p>
 * A text filter belongs to the category of restrictive filters.
 * Subset elements are filtered by textual and/or regular expressions. 
 * </p>
 *
 * @author ArndHouben
 * @version $Id: TextFilter.java,v 1.12 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
public class TextFilter extends AbstractSubsetFilter implements RestrictiveFilter{
	
	private final TextFilterSetting setting;
	
	/**
	 * Creates a new <code>TextFilter</code> instance for the given 
	 * dimension
	 * @param dimension the dimension to create the filter for
	 * @deprecated use {@link TextFilter#TextFilter(Hierarchy)} instead.
	 */
	public TextFilter(Dimension dimension) {
		this(dimension.getDefaultHierarchy(), new TextFilterSetting());
	}
	
	/**
	 * Creates a new <code>TextFilter</code> instance for the given 
	 * hierarchy
	 * @param hierarchy the hierarchy to create the filter for
	 */
	public TextFilter(Hierarchy hierarchy) {
		this(hierarchy, new TextFilterSetting());
	}

	/**
	 * Creates a new <code>TextFilter</code> instance for the given 
	 * dimension with the given settings
	 * @param dimension the dimension to create the filter for
	 * @param setting the filter settings to use
	 * @deprecated use {@link TextFilter#TextFilter(Hierarchy, TextFilterSetting)} instead.
	 */
	public TextFilter(Dimension dimension, TextFilterSetting setting) {
		super(dimension.getDefaultHierarchy());
		this.setting = setting;
	}
	
	/**
	 * Creates a new <code>TextFilter</code> instance for the given 
	 * hierarchy with the given settings
	 * @param hierarchy the hierarchy to create the filter for
	 * @param setting the filter settings to use
	 */
	public TextFilter(Hierarchy hierarchy, TextFilterSetting setting) {
		super(hierarchy);
		this.setting = setting;
	}

	public final TextFilter copy() {
		TextFilter copy = new TextFilter(hierarchy);
		copy.getSettings().adapt(setting);
		return copy;
	}
	
	public final TextFilterSetting getSettings() {
		return setting;
	}
	
	public final void filter(Set<Element> elements) {
		HashSet<Element> newElements = new HashSet<Element>();
		ObjectParameter exprParam = setting.getExpressions();
		Set<String> expressions = (Set<String>) exprParam.getValue();		
//		if(!setting.getExtended().getValue()) {
			expressions = createParsedExpressions(expressions);
//		}
		for(Element element : elements) {
			if(accept(element,expressions))
				newElements.add(element);
		}
		elements.clear();
		elements.addAll(newElements);
	}


	
	public int getType() {
		return TYPE_TEXT;
	}

	
	public final void initialize() {
	}


	public final void validateSettings() throws PaloIOException {
		ObjectParameter exprParam = setting.getExpressions();
		HashSet<String> expressions = (HashSet<String>) exprParam.getValue();
		if (expressions.isEmpty())
			throw new PaloIOException(
					"TextFilter: At least one expression is required!");
	}
	
    private final boolean accept(Element element, Set<String> expressions) {
		String elName = getValue(element); //element.getName();		
		//PR 6883: take first matching expression => OR relation instead of AND relation		
		for(String expr : expressions) {
			//PR 6895: we have to look for pattern occurrence...
			Pattern p = Pattern.compile(expr);
			Matcher m = p.matcher(elName);
			if(m.find())
				return true;
//			if(elName.matches(expr))
//				return true;
		}
		return expressions.isEmpty() || false;
    }

    private final String getValue(Element element) {
    	if(effectiveFilters.containsKey(TYPE_ALIAS)) {
        	AliasFilter aliasFilter = 
        		(AliasFilter) effectiveFilters.get(TYPE_ALIAS);
        	return aliasFilter.getAlias(element);
    	}
    	return element.getName();
    }
    
    private final Set<String> createParsedExpressions(Set<String> expressions) {
    	Set<String> _expressions = new HashSet<String>(expressions.size());
    	for(String expr : expressions)
    		_expressions.add(parseWildcards(expr));
    	return _expressions;
    }
    private final String parseWildcards(String str) {
		str = str.replaceAll("\\*", ".*"); //$NON-NLS-1$ //$NON-NLS-2$
		str = str.replaceAll("\\?", ".?"); //$NON-NLS-1$ //$NON-NLS-2$
		return str;
	}

}
