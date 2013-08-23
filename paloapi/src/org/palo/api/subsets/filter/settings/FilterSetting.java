/*
*
* @file FilterSetting.java
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
* @version $Id: FilterSetting.java,v 1.4 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter.settings;

import org.palo.api.subsets.Subset2;



/**
 * <code>FilterSetting</code>
 * <p>
 * A simple filter setting definition. We only require that a filter setting
 * can be reseted and be adapted from another filter setting of same kind.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: FilterSetting.java,v 1.4 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public interface FilterSetting {

	/**
	 * Resets the filter setting to its defaults.
	 */
	public void reset();
	/**
	 * Adapt the filter setting from the given filter setting. After that both
	 * settings are the same.
	 * @param from the filter setting to adapt from
	 */
	public void adapt(FilterSetting from);
	
	/**
	 * <p>Binds this filter settings instance to the given {@link Subset2}</p>
	 * <b>NOTE: PLEASE DON'T USE! INTERNAL METHOD </b>
	 * @param subset 
	 */
	public void bind(Subset2 subset);
	/**
	 * <p>Releases this filter settings instance from a previously binded 
	 * {@link Subset2}</p>
	 * <b>NOTE: PLEASE DON'T USE! INTERNAL METHOD </b>
	 */
	public void unbind();

}
