/*
*
* @file CubeView.java
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
* @version $Id: CubeView.java,v 1.14 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.api;

/**
 * A <code>CubeView</code> reflects a certain state of its corresponding palo 
 * cube. Therefore each cube view consists of one or more so called {@link Axis} 
 * which allows the sorting of the cube dimensions. E.g. the palo client defines 
 * axes for a cube table, like column and row, and a third axis for remaining 
 * dimensions.
 * 
 * @author ArndHouben
 * @version $Id: CubeView.java,v 1.14 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
public interface CubeView extends NamedEntity {
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
	
	/**
	 * Returns the unique id of this cube view
	 * @return unique id
	 */
	public String getId();
	
	/**
	 * Returns the corresponding source {@link Cube} of this view
	 * @return the source <code>Cube</code> of this view
	 */
	public Cube getCube();
	
	//optional	
	/**
	 * Sets the name of this cube view. Please note that the new name gets only
	 * stored with the next save of this view!
	 * @param name (new) name of the cube view
	 */
	public void setName(String name);
	
	/**
	 * Returns an optional description or <code>null</code> if none has been set
	 * @return a description of this view
	 */
	public String getDescription();
	/**
	 * Sets an optional description 
	 * @param description
	 */
	public void setDescription(String description);

	/**
	 * Returns a flag that triggers if empty rows or columns are visible
	 * @return the flag state
	 * @deprecated Not used anymore. Please use the property mechanism to
	 * specify properties like this. For <code>isHideEmpty</code>, you should use
	 * <code>getProperty(PROPERTY_ID_HIDE_EMPTY);</code> and check if the return
	 * value is a String representing the boolean value true.
	 */
	public boolean isHideEmpty();
	
	/**
	 * Sets a flag, which shows or hides empty rows or columns. 
	 * @param hideEmpty
	 * @deprecated Not used anymore. Please use the property mechanism to
	 * specify properties like this. For <code>setHideEmpty</code>, you should
	 * use <code>setProperty(PROPERTY_ID_HIDE_EMPTY, "true");</code> (or
	 * <code>.., "false"</code>, respectively).
	 */
	public void setHideEmpty(boolean hideEmpty);

	/**
	 * Returns the internal used raw definition of this cube view.
	 * @return internal representation of this view 
	 */
	String getRawDefinition();
	
	/**
	 * Adds a new {@link Axis} to this cube view.
	 * @param id the unique id of the new axis
	 * @param name the name of the new axis
	 * @return the newly created axis
	 */
	Axis addAxis(String id,String name);	
	/**
	 * Removes the given axis from this cube view
	 * @param axis the axis to remove
	 */
	void removeAxis(Axis axis);
	/**
	 * Returns all axes registered to this cube view
	 * @return all registered axes
	 */
	Axis[] getAxes();
	/**
	 * Returns the axis which is registered to this cube view under the given
	 * axis id or <code>null</code> if no such axis exists.
	 * @param id 
	 * @return the registered axis or <code>null</code>
	 */
	Axis getAxis(String id);
	
	/**
	 * Saves this cube view to its {@link Database}
	 */
	void save();
	
	/**
	 * Adds a property with the given id and given value to this
	 * <code>CubeView</code>. If a property with the same id already exists, the
	 * old value is overwritten with the new value.
	 * 
	 * @param id a unique identifier
	 * @param value the property value
	 */
	void addProperty(String id, String value);
	
	/**
	 * Adds the given <code>Property</code> object to this 
	 * <code>CubeView</code>. If a property with the same id already exists, the
	 * old value is overwritten with the new value.
	 * 
	 * @param property a Property object.
	 */
	void addProperty(Property property);
	
	/**
	 * Removes the property specified by the given id from this
	 * <code>CubeView</code>.
	 * 
	 * @param id the property identifier
	 */
	void removeProperty(String id);

	/**
	 * Removes the property specified by the given <code>Property</code> object
	 * from this <code>CubeView</code>.
	 * 
	 * @param property a Property object.
	 */
	void removeProperty(Property property);
	
	/**
	 * Returns all property ids.
	 * 
	 * @return all property identifiers.
	 */
	public String [] getProperties();

	/**
	 * Returns the value of the property specified by the given id or null if
	 * the id has not been specified.
	 * 
	 * @param id the property identifier
	 * @return the property value
	 */
	public String getPropertyValue(String id);
}
