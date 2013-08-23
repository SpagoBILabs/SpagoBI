/*
*
* @file StringParameter.java
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
* @version $Id: StringParameter.java,v 1.3 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter.settings;

/**
 * <code>StringParameter</code>
 * <p> 
 * An implementation of the {@link Parameter} interface for {@link String}
 * values.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: StringParameter.java,v 1.3 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public class StringParameter extends AbstractParameter {
	
	private final String name;
	private String value;

	/**
	 * Creates a new unnamed <code>StringParemeter</code> instance
	 */
	public StringParameter() {
		this(null);
	}

	/**
	 * Creates a new <code>StringParemeter</code> instance with the required
	 * name.
	 * @param name the parameter name. Passing <code>null</code> is valid.
	 */
	public StringParameter(String name) {
		this.name = name;
		value = new String();
	}
	
	public final String getName() {
		return name;
	}

	public final String getValue() {
		return value;	
	}
	
	/**
	 * Sets the parameter value. Specifying <code>null</code> is allowed.
	 * @param value new parameter value
	 */
	public final void setValue(String value) {
		this.value = value;
		markDirty();
	}
}
