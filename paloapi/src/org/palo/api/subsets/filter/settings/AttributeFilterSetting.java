/*
*
* @file AttributeFilterSetting.java
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
* @version $Id: AttributeFilterSetting.java,v 1.10 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter.settings;

import org.palo.api.Element;
import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.filter.AttributeFilter;

/**
 * <code>AttributeFilterSetting</code>
 * <p>
 * Manages the settings for the {@link AttributeFilter}. 
 * An {@link AttributeConstraintsMatrix} is used to filter out {@link Element}s. 
 * Therefore an element is accepted if it fulfills at least one row of this 
 * matrix.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: AttributeFilterSetting.java,v 1.10 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public class AttributeFilterSetting extends AbstractFilterSettings {
	
	private ObjectParameter constraintsParam;
	
	/**
	 * Creates a new <code>AttributeFilterSetting</code> instance
	 */
	public AttributeFilterSetting() {
		this.constraintsParam = new ObjectParameter();
		this.constraintsParam.setValue(new AttributeConstraintsMatrix());
	}

	/**
	 * Adds all filter constraints from the given parameter to this settings.
	 * Note that the parameter value should be of type 
	 * {@link AttributeConstraintsMatrix}, otherwise calling this method has 
	 * no effect! 
	 * @param constraintParam the new filter constraints
	 */
	public final void setFilterConstraints(ObjectParameter constraintsParam) {		
		Object value = constraintsParam.getValue();
		if(value instanceof AttributeConstraintsMatrix) {
			copyFilterConstraints((AttributeConstraintsMatrix)value);			
		}
	}
	/**
	 * Returns the currently used filter constraint parameter.
	 * @return the filter constraint parameter
	 */
	public final ObjectParameter getFilterConstraints() {
		return constraintsParam;
	}
	
	public final boolean hasFilterConsraints() {
		AttributeConstraintsMatrix filterMatrix = 
			(AttributeConstraintsMatrix) constraintsParam.getValue();
		return filterMatrix.hasConstraints();
	}
	
	public void adapt(FilterSetting from) {
		if (!(from instanceof AttributeFilterSetting))
			return;
		AttributeFilterSetting setting = (AttributeFilterSetting) from;
		copyFilterConstraints((AttributeConstraintsMatrix) 
				setting.getFilterConstraints().getValue());
	}

	public final void bind(Subset2 subset) {
		super.bind(subset);
		//bind internal:
		constraintsParam.bind(subset);
		AttributeConstraintsMatrix filterMatrix = 
			(AttributeConstraintsMatrix)constraintsParam.getValue();
		if(filterMatrix != null)
			filterMatrix.bind(subset);
	}
	public final void unbind() {
		super.unbind();
		//unbind internal:
		constraintsParam.unbind();
		AttributeConstraintsMatrix filterMatrix = 
			(AttributeConstraintsMatrix)constraintsParam.getValue();
		if(filterMatrix != null)
			filterMatrix.unbind();
	}

	public void reset() {
		AttributeConstraintsMatrix filterMatrix = 
			(AttributeConstraintsMatrix)constraintsParam.getValue();
		filterMatrix.clear();
	}
	
	private final void copyFilterConstraints(
			AttributeConstraintsMatrix newConstraintMatrix) {
		AttributeConstraintsMatrix filterMatrix = 
				(AttributeConstraintsMatrix) constraintsParam.getValue();
		filterMatrix.clear(); // resets old matrix
		// add entries from new matrix:
		for (AttributeConstraint constraint : newConstraintMatrix
				.getConstraints()) {
			constraint.bind(subset);
			filterMatrix.addFilterConstraint(constraint);
		}
	}
}
