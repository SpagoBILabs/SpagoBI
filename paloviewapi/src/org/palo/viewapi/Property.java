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
* @author PhilippBouillon
*
* @version $Id: Property.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

package org.palo.viewapi;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A <code>Property</code> represents a generic object that can transport 
 * arbitrary data from a client to all major objects of the view api (for
 * example: views, axes, ...).
 * 
 * @author PhilippBouillon
 * @version $Id: Property.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
 */
public class Property <T> {
	/**
	 * The id of the property.
	 */
	private final String id;
	
	/**
	 * The value of the property.
	 */
	private T value;
	
	/**
	 * The parent of the property, if any. Tree structures of properties
	 * are thus possible.
	 */
	private Property <T> parent;
	
	/**
	 * All children of this property.
	 */
	private final Map <String, Property <T>> children;
	
	/**
	 * Creates a new property with the given parent (may be null), an id and a
	 * value.
	 * 
	 * @param parent parent of this property or null.
	 * @param id id of this property.
	 * @param value value of this property.
	 */
	public Property(Property <T> parent, String id, T value) {
		children = new LinkedHashMap <String, Property <T>> ();
		this.parent = parent;
		this.id = id;
		this.value = value;
	}
	
	/**
	 * Creates a new property with a given id and value. The parent of this
	 * property is null.
	 * 
	 * @param id id of the new property.
	 * @param value value of the new property.
	 */
	public Property(String id, T value) {
		this(null, id, value);
	}

	/**
	 * Returns the id of this property.
	 * @return the id of this property.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the value of this property.
	 * @return the value of this property.
	 */
	public T getValue() {
		return value;
	}
	
	/**
	 * Sets a new value for this property.
	 * @param newValue new value for this property.
	 */
	public void setValue(T newValue) {
		value = newValue;
	}
	
	/**
	 * Returns the parent of this property or null if no parent is set.
	 * @return the parent of this property or null.
	 */
	public Property <T> getParent() {
		return parent;
	}
	
	/**
	 * Returns the number of children of this property.
	 * @return the number of children of this property.
	 */
	public int getChildCount() {
		return children.size();
	}
	
	
	/**
	 * Returns all children of this property.
	 * @return all children of this property.
	 */
	@SuppressWarnings("unchecked")
	public Property <T> [] getChildren() {
		return children.values().toArray(new Property[0]);
	}
	
	/**
	 * Returns the value of the child specified by the given id or null, if no
	 * such child exists.
	 * @param childId the id of the child of which the value is to be returned.
	 * @return the value of the child specified by the given id or null.
	 */
	public T getChildValue(String childId) {		
		Property <T> prop = children.get(childId);
		if (prop == null) {
			return null;
		}
		return prop.getValue();
	}
	
	/**
	 * Adds the specified child to this property.
	 * @param child the child to add.
	 */
	public void addChild(Property <T> child) {
		if (child == null) {
			return;
		}
		children.put(child.getId(), child);
	}
	
	/**
	 * Removes the specified child from this property.
	 * @param child the child to remove.
	 */
	public void removeChild(Property <T> child) {
		if (child == null) {
			return;
		}
		children.remove(child.getId());
	}
	
	/**
	 * Removes all children from this property.
	 */
	public void removeAllChildren() {
		children.clear();
	}
}
