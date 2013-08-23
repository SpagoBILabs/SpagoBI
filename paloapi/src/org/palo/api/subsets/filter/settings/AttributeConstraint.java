/*
*
* @file AttributeConstraint.java
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
* @version $Id: AttributeConstraint.java,v 1.12 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter.settings;

import org.palo.api.Attribute;
import org.palo.api.subsets.Subset2;

/**
 * <code>AttributeFilterColumnEntry</code>
 * <p>
 * This class defines a constraint for an {@link Attribute}. A constraint 
 * consists of an operator and an attribute value.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: AttributeConstraint.java,v 1.12 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public class AttributeConstraint {

	//VALID OPERATORS:
	public static final String NONE = "None";
	public static final String LESS = "<";
	public static final String LESS_EQUAL = "<=";
	public static final String GREATER = ">";
	public static final String GREATER_EQUAL = ">=";
	public static final String NOT_EQUAL = "<>";
	public static final String EQUAL = "=";
	public static final String[] ALL_OPERATORS = new String[]{NONE, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL, EQUAL, NOT_EQUAL};

	private final String attrId;
	
	private String operator = NONE; //EQUAL;
	private String value = "";
	
	private Subset2 subset;
	
	/**
	 * Creates a new <code>AttributeConstraint</code> instance for the 
	 * attribute which is specified by the given name.
	 * @param attrName the attribute to which this constrain belongs
	 */
	public AttributeConstraint(String attrId) {
		this.attrId = attrId;
	}
	
	/**
	 * Returns the name of the attribute this constrain belongs to
	 * @return the attribute name
	 */
	public final String getAttributeId() {
		return attrId;
	}
	
	/**
	 * Returns the currently used constrain operator
	 * @return currently used constrain operator
	 */
	public final String getOperator() {
		return operator;
	}

	/**
	 * Sets the constrain operator to use. Note that the operator must be 
	 * valid, i.e. it should be in the {@link #ALL_OPERATORS} array, otherwise
	 * this method has no effect.
	 * @return the new constrain operator
	 */
	public final void setOperator(String operator) {
		if(!isOperator(operator))
			return;
		this.operator = operator;
		markDirty();
	}

	/**
	 * Returns the currently used constrain value
	 * @return currently used constrain value
	 */
	public final String getValue() {
		return value;
	}
	
	/**
	 * Sets the constrain value to use
	 * @param value the new constraint value
	 */
	public final void setValue(String value) {
		this.value = value;
		markDirty();
	}
	
	/**
	 * Checks if the given attribute value is accepted, i.e. fulfills the
	 * attribute constraint defined by this instance.
	 * @param attrValue the attribute value to check
	 * @return <code>true</code> if the attribute value fulfills this constrain,
	 * <code>false</code> otherwise
	 */
	public final boolean accept(String attrValue, int attrType) {
		if(operator.equals(NONE))
			return true;	//PR 6901 accept everything...
		
		int result = 0;
		//PR 6869: check if we have to compare numbers...
		if(attrType == Attribute.TYPE_NUMERIC) {
			try {
				Double dbValue = (value != null && value.length() > 0) ? 
						Double.valueOf(value) : new Double(0);
				Double dbAttrValue = (value != null && value.length() > 0) ?
						Double.valueOf(attrValue) : new Double(0);
				result = dbValue.compareTo(dbAttrValue);
			}catch(NumberFormatException e) {
				//we silently do a lexicographically comparison
				result = value.compareTo(attrValue);
			}
		} else
			result = value.compareTo(attrValue);
		if (operator.equals(LESS))
			return result > 0;
		else if (operator.equals(LESS_EQUAL))
			return result >= 0;
		else if (operator.equals(GREATER))
			return result < 0;
		else if (operator.equals(GREATER_EQUAL))
			return result <= 0;
		else if (operator.equals(NOT_EQUAL))
			return result != 0;
		else
			return result == 0; //EQUAL?
	}
	
	/**
	 * Returns the index in the {@link #ALL_OPERATORS} array of the currently
	 * used operator.  
	 * @return the operator index in {@link #ALL_OPERATORS}
	 */
	public final int getOperatorIndex() {
		for(int i=0;i<ALL_OPERATORS.length;++i)
			if(ALL_OPERATORS[i].equals(operator))
				return i;
		return -1;
	}
	
	/**
	 * Checks if the given operator string is valid
	 * @param operator an operator string to check
	 * @return <code>true</code> if the given string represents an operator,
	 * <code>false</code> otherwise
	 */
	public final boolean isOperator(String operator) {
		for(int i=0;i<ALL_OPERATORS.length;++i)
			if(ALL_OPERATORS[i].equals(operator))
				return true;
		return false;
	}
	
	public final boolean equals(Object obj) {
		if (obj instanceof AttributeConstraint) {
			AttributeConstraint other = (AttributeConstraint) obj;
			return attrId.equals(other.attrId)
					&& operator.equals(other.operator)
					&& value.equals(other.value);
		}
		return false;
	}
	
	public final int hashCode() {
		int hc = 23;
		hc += 37 * attrId.hashCode();
		hc += 37 * operator.hashCode();
		hc += 37 * value.hashCode();
		return hc;
	}
	
	/**
	 * <p>Binds this instance to the given {@link Subset2}</p>
	 * <b>NOTE: PLEASE DON'T USE! INTERNAL METHOD </b>
	 * @param subset 
	 */
	public final void bind(Subset2 subset) {
		this.subset = subset;
		markDirty();
	}
	/**
	 * <p>Releases this instance from a previously binded {@link Subset2}</p>
	 * <b>NOTE: PLEASE DON'T USE! INTERNAL METHOD </b>
	 */
	public final void unbind() {
		subset = null;
	}

	private final void markDirty() {
		if(subset != null)
			subset.modified();
	}
}
