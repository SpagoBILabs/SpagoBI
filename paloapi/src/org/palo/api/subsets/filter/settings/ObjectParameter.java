/*
*
* @file ObjectParameter.java
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
* @version $Id: ObjectParameter.java,v 1.4 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter.settings;

/**
 * <code>GeneralParameter</code>
 * <p>
 * An implementation of the {@link Parameter} interface to store a general
 * value.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: ObjectParameter.java,v 1.4 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public class ObjectParameter extends AbstractParameter {

	private String name;
	private Object value;
	
	/**
	 * Creates a new parameter instance with no name
	 */
	public ObjectParameter() {
		this(null);
	}
	
	/**
	 * Creates a new parameter instance with the given name
	 * @param name the parameter name
	 */
	public ObjectParameter(String name) {
		this.name = name;
	}
	
	public final String getName() {
		return name;
	}

	public final Object getValue() {
		return value;
	}

	/**
	 * Sets the parameter name. Specifying <code>null</code> is allowed.
	 * @param name new parameter name
	 */
	public final void setName(String name) {
		this.name = name;		
	}

	/**
	 * Sets the parameter value. Specifying <code>null</code> is allowed.
	 * @param value new parameter value
	 */
	public final void setValue(Object value) {
		this.value = value;
		markDirty();
	}
}
