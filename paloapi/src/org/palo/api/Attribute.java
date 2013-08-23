/*
*
* @file Attribute.java
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
* @version $Id: Attribute.java,v 1.10 2009/09/22 09:43:17 PhilippBouillon Exp $
*
*/

package org.palo.api;


/**
 * <code>Atribute</code>s are used to provide extra informations for an 
 * {@link Element} of a {@link Dimension}. Each <code>Element</code> can have 
 * several distinct attributes. 
 * <p>
 * With the {@link #setChildren(Attribute[])} method it is possible to build up 
 * an hierarchy of <code>Attributes</code>.
 * </p>
 * 
 * @author ArndHouben
 * @version $Id: Attribute.java,v 1.10 2009/09/22 09:43:17 PhilippBouillon Exp $
 */
public interface Attribute {
	
	public static final int TYPE_STRING = Element.ELEMENTTYPE_STRING;
	public static final int TYPE_NUMERIC = Element.ELEMENTTYPE_NUMERIC;
	
	
	/**
	 * The unique attribute identifier
	 * @return the unique identifier of the attribute
	 */
	public String getId();
	
	/**
	 * The attribute name 
	 * @return the name of the attribute
	 */
	public String getName();
	
	/**
	 * Sets the attribute name
	 * @param name the new attribute name
	 */
	public void setName(String name);
	
	/**
	 * Returns the attribute value for the given <code>Element</code> instance.
	 * @param element the <code>Element</code> to get the attribute value from
	 * @return the attribute value or null if the value is not specified
	 */
	public Object getValue(Element element);
	
	/**
	 * Sets the attribute value for the given <code>Element</code> instance.
	 * @param element the <code>Element</code> which attribute value to set
	 * @param value the new attribute value
	 */
	public void setValue(Element element, Object value);

	/**
	 * Convenient method to set the values for several <code>Element</code>s
	 * at once, i.e. the attribute value for the i.th element is set to the
	 * i.th object.  
	 * @param elements the elements to set the values for
	 * @param values the new values
	 */
	public void setValues(Element[] elements, Object[] values);
	
	/**
	 * Convenient method to receive the values for several <code>Element</code>s
	 * at once.  
	 * @param elements the elements to get the values from
	 * @return the attribute values for the given elements
	 */
	public Object[] getValues(Element[] elements);
	
	
	/**
	 * Checks if this attribute has any children attributes.
	 * @return true if the attribute has children, false otherwise.
	 */
	public boolean hasChildren();
	
	/**
	 * Sets the children attributes of this attribute. 
	 * <p>
	 * <b>Note:</b> this will remove all previously set children. 
	 * Specifying null is allowed and will remove all children! 
	 * </p>
	 * @param attributes the attribute children
	 */
	public void setChildren(Attribute[] attributes);	
	
	/**
	 * Convenient method to remove children attributes from this attribute
	 * @param attributes attribute children to remove
	 */
	public void removeChildren(Attribute[] attributes);
	
	/**
	 * Returns the children of this attribute
	 * @return an array of children attributes
	 */
	public Attribute[] getChildren();
	
	/**
	 * Returns the parent attributes of this attribute
	 * @return an array of parent attributes
	 */
	public Attribute[] getParents();
	
	/**
	 * Returns the attribute type which is one of the defined type constants
	 * @return attribute type
	 */
	public int getType();
	
	/**
	 * Sets the attribute type which is one of the defined type constants.
	 * @param newType the new attribute type
	 */
	public void setType(int newType);
}
