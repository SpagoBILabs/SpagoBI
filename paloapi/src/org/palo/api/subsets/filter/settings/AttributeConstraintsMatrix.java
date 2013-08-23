/*
*
* @file AttributeConstraintsMatrix.java
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
* @version $Id: AttributeConstraintsMatrix.java,v 1.6 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.api.subsets.filter.settings;

import java.util.ArrayList;
import java.util.HashMap;

import org.palo.api.Attribute;
import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.filter.AttributeFilter;

/**
 * <code>AttributeConstraintsMatrix</code>
 * <p>
 * Manages the {@link AttributeConstraint}s for the {@link AttributeFilter} by
 * storing them in a matrix like way. That means each {@link Attribute} defines 
 * a column which contains all constraints for this certain attribute. The rows
 * of this matrix define the conditions which have to be fulfilled by the 
 * attributes of a certain {@link Element}.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: AttributeConstraintsMatrix.java,v 1.6 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public class AttributeConstraintsMatrix {

	private Subset2 subset;
	private int rowsCount;
	private final HashMap<String, ArrayList<AttributeConstraint>> attrId2constraint;

	public AttributeConstraintsMatrix() {
		attrId2constraint = 
			new HashMap<String, ArrayList<AttributeConstraint>>();
	}

	
	public final String[] getAttributeIDs() {
		return attrId2constraint.keySet().toArray(new String[0]);
	}
	
	/**
	 * Adds the given attribute constraint to the matrix of all attribute 
	 * constraints.
	 * @param constraint the attribute constraint to add
	 */
	public final void addFilterConstraint(AttributeConstraint constraint) {
		ArrayList<AttributeConstraint> constraints = 
			getConstraints(constraint.getAttributeId());
		constraints.add(constraint);
		checkRowsCount();
		markDirty();
	}
	
	/**
	 * Removes the given attribute constraint from the matrix of all attribute 
	 * constraints.
	 * @param constraint the attribute constraint to remove
	 */
	public final void removeFilterConstraint(AttributeConstraint constraint) {
		ArrayList<AttributeConstraint> constraints = 
			getConstraints(constraint.getAttributeId());
		constraints.remove(constraint);
		checkRowsCount();
		markDirty();
	}
	
	/**
	 * Removes all attribute constraints for the attribute which corresponds
	 * to the given attribute identifier. Thus this method clears or removes the 
	 * matrix column which corresponds to the specified attribute. 
	 * @param attrId the identifier of the attribute to remove from the matrix
	 */
	public final void removeAllFilterConstraints(String attrId) {
		ArrayList<AttributeConstraint> constraints = getConstraints(attrId);
		constraints.clear();
		checkRowsCount();
		markDirty();
	}
	
	/**
	 * Returns the column for the attribute which is specified by the given
	 * attribute identifier. The column contains all defined constraints for this
	 * attribute. 
	 * @param attrID the attribute identifier to get the constraints for 
	 * @return all attribute constraints
	 */
	public final AttributeConstraint[] getColumn(String attrId) {
		ArrayList<AttributeConstraint> constraints = getConstraints(attrId);
		return constraints.toArray(new AttributeConstraint[0]);
	}

	/**
	 * Returns the n.th row of the constraints matrix. 
	 * @param index the index of the row to return
	 * @return an array containing the n.th row of the constraint matrix
	 */
	public final AttributeConstraint[] getRow(int index) {
		ArrayList<AttributeConstraint> row = 
			new ArrayList<AttributeConstraint>();
		for(String attrId : attrId2constraint.keySet()) {
			ArrayList<AttributeConstraint> constraints = 
				attrId2constraint.get(attrId);
			if(constraints.size()>index)
				row.add(constraints.get(index));
		}
		return row.toArray(new AttributeConstraint[row.size()]);
	}

	/**
	 * Returns all rows of the attribute constraints matrix
	 * @return all matrix rows
	 */
	public final AttributeConstraint[][] getRows() {
		AttributeConstraint[][] rows = new AttributeConstraint[rowsCount][];
		for(int i=0;i<rowsCount;++i)
			rows[i] = getRow(i);
		return rows;
	}

	/**
	 * Returns the current row count
	 * @return current row count
	 */
	public final int getRowsCount() {
		return rowsCount;
	}

	/**
	 * Clears the complete filter matrix.
	 */
	public final void clear() {
		rowsCount = 0;
		attrId2constraint.clear();
		markDirty();
	}
	
	final AttributeConstraint[] getConstraints() {
		ArrayList<AttributeConstraint> constraints = 
			new ArrayList<AttributeConstraint>();
		for(ArrayList<AttributeConstraint> list : attrId2constraint.values())
			constraints.addAll(list);
		return constraints.toArray(new AttributeConstraint[constraints.size()]);
	}
	
	final boolean hasConstraints() {
		return !attrId2constraint.isEmpty();
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

	private final void checkRowsCount() {
		rowsCount = 0;
		for(String attrId : attrId2constraint.keySet()) {
			ArrayList<AttributeConstraint> constraints = 
				attrId2constraint.get(attrId);
			int size = constraints.size();
			if(rowsCount < size)
				rowsCount = size;
		}
	}

	private final ArrayList<AttributeConstraint> getConstraints(String attrId) {
		ArrayList<AttributeConstraint> constraints =
			attrId2constraint.get(attrId);
		if(constraints == null) {
			constraints = new ArrayList<AttributeConstraint>();
			attrId2constraint.put(attrId, constraints);
		}
		return constraints;
	}


}
