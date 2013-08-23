/*
*
* @file EffectiveFilter.java
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
* @version $Id: EffectiveFilter.java,v 1.2 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter;

import org.palo.api.subsets.SubsetFilter;

/**
 * <code>EffectiveFilter</code>
 * <p>
 * An affective filter is used to influence other filters.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: EffectiveFilter.java,v 1.2 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
public interface EffectiveFilter extends SubsetFilter {

	/**
	 * Returns the filter types which are affected by this filter.
	 * @return the affected filter types
	 */
	public int[] getEffectiveFilter();
	
//	/**
//	 * Returns the effective value for the given element and filter type.
//	 * Please note that the value is returned as <code>String</code> because 
//	 * the calling filter has to interpret the returned value correctly.
//	 * @param element the affected element
//	 * @param type the effective filter type
//	 * @return the effective element value as string
//	 */
//	public String getEffectiveValue(Element element, int type);

}
