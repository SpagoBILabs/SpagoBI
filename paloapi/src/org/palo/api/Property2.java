/*
*
* @file Property2.java
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
* @author PhilippBouillon
*
* @version $Id: Property2.java,v 1.6 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.api;

/**
 * <code>Propery2</code>
 * <p>
 * Property2 objects can be added to PaloObjects in order to attribute
 * additional information to them.</p>
 * <p>
 * For example, an SAP XMLA connection has the notion of "variables", a feature
 * unknown to other XMLA connection. Thus, all variables belonging to a cube
 * are stored as properties for that cube and can be modified by the client.</p>
 * <p>
 * Properties can also be used to add client specific knowledge to PaloObjects.
 * </p>
 * 
 * @author PhilippBouillon
 */
public interface Property2 extends PaloObject {
	/**
	 * Property types. A property can either be a numeric value, a string value
	 * or a boolean flag.
	 */
	static final int 
		TYPE_NUMERIC = 1,
		TYPE_STRING = 2,
		TYPE_BOOLEAN = 3;

	/**
	 * Returns the string representation of this property's value. If the type
	 * of this property is either numeric or boolean, the string representation
	 * can be parsed to a double or a boolean, respectively.
	 *  
	 * @return the string representation of this property's value.
	 */
	String getValue();
	
	/**
	 * Sets the new value for this property.
	 * 
	 * @param newValue a new value for this property.
	 */
	void setValue(String newValue);
	
	/**
	 * Returns the parent of this property or null if no parent exists.
	 * 
	 * @return the parent of this property.
	 */
	Property2 getParent();
	
	/**
	 * Returns the number of children of this property.
	 * 
	 * @return the number of children of this property.
	 */
	int getChildCount();
	
	/**
	 * Returns all child properties of this property.
	 * 
	 * @return all child properties of this property.
	 */
	Property2 [] getChildren();
	
	/**
	 * Returns the value for the first child with the specified id or an empty
	 * string no such child exists.
	 * 
	 * @param childId the id of the child property.
	 * @return the value of the first child property with the specified id
	 * or an empty string if no child with this id exists.
	 */
	String getChildValue(String childId);
	
	/**
	 * Returns all child properties with the specified id.
	 * @param childId the id of the child properties which should be returned.
	 * @return all child properties with the given id.
	 */
	Property2 [] getChildren(String childId);
	
	/**
	 * Adds a new child to this property.
	 * 
	 * @param child the property to add to this property.
	 */
	void addChild(Property2 child);
	
	/**
	 * Removes a given child from this property.
	 * 
	 * @param child
	 */
	void removeChild(Property2 child);
	
	/**
	 * Clears all children of this property.
	 */
	void clearChildren();
	
	/**
	 * Returns true if this property cannot be modified.
	 * 
	 * @return true if this property cannot be modified, false otherwise.
	 */
	boolean isReadOnly();
	
	/**
	 * Returns the type of this property.
	 * 
	 * @return the type of this property as defined in the constants in this
	 * interface.
	 */
	int getType();
}
