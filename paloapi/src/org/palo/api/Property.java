/*
*
* @file Property.java
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
* @author Philipp Bouillon
*
* @version $Id: Property.java,v 1.3 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.api;

/**
 * A <code>Property</code> object can be attached to several classes to provide
 * application specific additional information. The developer can specify
 * arbitrary key/value pairs via this class.
 * 
 * @author Philipp Bouillon
 * @version $Id: Property.java,v 1.3 2009/04/29 10:21:57 PhilippBouillon Exp $
 * @deprecated Use Property2 instead.
 */
public class Property {
	/**
	 * The id of this property.
	 */
	private String id;
	
	/**
	 * The value of this property.
	 */
	private String value;
	
	/**
	 * Creates a new key/value pair with the given data.
	 * 
	 * @param id the id of the new property.
	 * @param value the value of the new property.
	 */
	public Property(String id, String value) {
		this.id = id;
		this.value = value;
	}
	
	/**
	 * Returns the id of this property object.
	 * @return the id of this property object.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the value of this property object.
	 * @return the value of this property object.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets a new value for this property.
	 * 
	 * @param newValue the new value for this property.
	 */
	public void setValue(String newValue) {
		value = newValue;
	}
}
