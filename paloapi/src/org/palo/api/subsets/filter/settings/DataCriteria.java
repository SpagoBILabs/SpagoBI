/*
*
* @file DataCriteria.java
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
* @version $Id: DataCriteria.java,v 1.6 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter.settings;

import org.palo.api.subsets.Subset2;


/**
 * <code>DataCriteria</code>
 * <p>
 * A data criteria consists of one or two operators and their operands. Data
 * criteria are used by a data filter to bound the subset elements by their 
 * value. With a data criteria expressions like <code>take all elements > 400</code>
 * are possible. 
 * </p>
 *
 * @author ArndHouben
 * @version $Id: DataCriteria.java,v 1.6 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public class DataCriteria {

	//VALID OPERATORS:
	public static final String LESSER = "<";
	public static final String LESSER_EQUAL = "<=";
	public static final String GREATER = ">";
	public static final String GREATER_EQUAL = ">=";
	public static final String NOT_EQUAL = "<>";
	public static final String EQUAL = "=";
	public static final String[] ALL_OPERATORS = new String[]{LESSER, LESSER_EQUAL, GREATER, GREATER_EQUAL, NOT_EQUAL, EQUAL};
	
	//we support max. 2 operators-operands:
	private int operator1;
	private int operator2;
	
	private StringParameter operand1;
	private StringParameter operand2;

	private Subset2 subset;
	
	/**
	 * Creates a new <code>DataCriteria</code> instance with the given operator
	 * and operand
	 * @param operator one of the defined operator constants
	 * @param operand an operand value 
	 */
	public DataCriteria(String operator, String operand) {
		this.operator1 = getIndex(operator);
		this.operand1 = new StringParameter();
		this.operand2 = new StringParameter();
		this.operand1.setValue(operand);
	}

	/**
	 * Checks if this criteria has an additional operator and operand
	 * @return <code>true</code> if this criteria has a second expression,
	 * <code>false</code> otherwise
	 */
	public final boolean hasSecondOperator() {
		return operand2 != null && !operand2.equals("");
	}
	/**
	 * Returns the first operator
	 * @return the first operator
	 */
	public final String getFirstOperator() {
		return ALL_OPERATORS[operator1];
	}
	/**
	 * Returns the index of the first operator inside the predefined operator
	 * constants array
	 * @return first operator index
	 */
	public final int getFirstOperatorIndex() {
		return operator1;
	}
	/**
	 * Sets the operator to use as first operator
	 * @param operator1 the new first operator
	 */
	public final void setFirstOperator(String operator1) {
		this.operator1 =getIndex(operator1);
		markDirty();
	}
	/**
	 * Sets the first operator by specifying its index in the predefined
	 * operator constants array
	 * @param index the new first operator
	 */
	public final void setFirstOperator(int index) {
		this.operator1 = index;
		markDirty();
	}
	/**
	 * Returns the second operator
	 * @return the second operator
	 */
	public final String getSecondOperator() {
		return ALL_OPERATORS[operator2];
	}
	/**
	 * Returns the index of the second operator inside the predefined operator
	 * constants array
	 * @return second operator index
	 */
	public final int getSecondOperatorIndex() {
		return operator2;
	}
	/**
	 * Sets the operator to use as second operator
	 * @param operator2 the new second operator
	 */
	public final void setSecondOperator(String operator2) {
		this.operator2 = getIndex(operator2);
		markDirty();
	}
	/**
	 * Sets the second operator by specifying its index in the predefined 
	 * operator constants array
	 * @param index the new second operator
	 */
	public final void setSecondOperator(int index) {
		this.operator2 = index;
		markDirty();
	}
	
//	/**
//	 * Returns the first operand. If no operand was specified an empty string
//	 * is returned.
//	 * @return the first operand or an emtpy string if none was specified
//	 */
//	public final String getFirstOperand() {
//		return operand1 == null ? "" : operand1;
//	}
	public final StringParameter getFirstOperand() {
		return operand1;
	}
	
	/**
	 * Sets the first operand
	 * @param operand1 the new first operand
	 */
	public final void setFirstOperand(String operand1) {
		this.operand1.setValue(operand1);
	}
	
	public final void setFirstOperand(StringParameter operand1) {
		this.operand1 = operand1;
		this.operand1.bind(subset);
		markDirty();
	}
	
//	/**
//	 * Returns the second operand. 
//	 * @return the second operand or <code>null</code> if none was specified
//	 */
//	public final String getSecondOperand() {
//		return operand2;
//	}
	public final StringParameter getSecondOperand() {
		return operand2;
	}

	/**
	 * Sets the second operand
	 * @param operand2 the new second operand
	 */
	public final void setSecondOperand(String operand2) {
		this.operand2.setValue(operand2);
	}
	public final void setSecondOperand(StringParameter operand2) {
		this.operand2 = operand2;
		this.operand2.bind(subset);
		markDirty();
	}

	/**
	 * <p>Binds this instance to the given {@link Subset2}</p>
	 * <b>NOTE: PLEASE DON'T USE! INTERNAL METHOD </b>
	 * @param subset 
	 */
	public final void bind(Subset2 subset) {
		this.subset = subset;
		operand1.bind(subset);
		operand2.bind(subset);
		markDirty();
	}
	/**
	 * <p>Releases this instance from a previously binded {@link Subset2}</p>
	 * <b>NOTE: PLEASE DON'T USE! INTERNAL METHOD </b>
	 */
	public final void unbind() {
		subset = null;
		operand1.unbind();
		operand2.unbind();
	}

	/**
	 * Creates a deep copy of this data criteria instance.
	 * @return
	 */
	final DataCriteria copy() {
		DataCriteria copy = new DataCriteria(ALL_OPERATORS[operator1],operand1.getValue());
		copy.operator2 = operator2;
		copy.operand2 = operand2;
		return copy;
	}
	
	private final int getIndex(String operator) {
		for(int i=0;i<ALL_OPERATORS.length;++i) {
			if(ALL_OPERATORS[i].equals(operator))
				return i;
		}
		throw new RuntimeException("Illegal operator!");
	}
	
	private final void markDirty() {
		if(subset != null)
			subset.modified();
	}
}
