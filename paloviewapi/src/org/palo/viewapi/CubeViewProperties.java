/*
*
* @file CubeViewProperties.java
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
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: CubeViewProperties.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi;

/**
 * <code>CubeViewProperties</code>
 * This interface contains some predefined properties which are useful for
 * {@link CubeView}s
 *
 * @version $Id: CubeViewProperties.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public interface CubeViewProperties {

	/**
	 * Property to indicate if empty cells are to be hidden or not.
	 * It is the client's responsibility to implement the logic for this
	 * property.
	 */
	public static final String PROPERTY_ID_HIDE_EMPTY = "hideEmpty";
	
	/**
	 * Property to indicate if the horizontal order of elements is to be
	 * reversed or not (i.e. if set to true, consolidated elements are displayed
	 * to the _right_ of their children; consolidated elements are displayed
	 * on the _left_ hand side of their children if this property is not set
	 * or set to false). 
	 * It is the client's responsibility to implement the logic for this
	 * property.
	 */
	public static final String PROPERTY_ID_REVERSE_HORIZONTAL_LAYOUT = "reverseHorizontal";

	/**
	 * Property to indicate if the vertical order of elements is to be
	 * reversed or not (i.e. if set to true, consolidated elements are displayed
	 * _below_ their children; consolidated elements are displayed
	 * _above_ their children if this property is not set or set to false). 
	 * It is the client's responsibility to implement the logic for this
	 * property.
	 */
	public static final String PROPERTY_ID_REVERSE_VERTICAL_LAYOUT = "reverseVertical";
	
	/**
	 * Property to indicate if cells, which contain a rule should be
	 * highlighted in the ui.
	 */
	public static final String PROPERTY_ID_SHOW_RULES = "showRules";

}
