/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.query;

import it.eng.qbe.utility.StringUtils;

import java.io.IOException;
import java.util.Set;

/**
 * The Class Filter.
 * 
 * @author Andrea Gioia
 */
public class Filter {
	
	/** The entity name. */
	String entityName;
	
	/** The filter condition. */
	String filterCondition;
	
	/** The parameters. */
	Set parameters;
	
	/** The fields. */
	Set fields;
	
	/**
	 * Instantiates a new filter.
	 * 
	 * @param entityName the entity name
	 * @param filterCondition the filter condition
	 */
	public Filter(String entityName, String filterCondition) {
		this.entityName = entityName;
		this.filterCondition = filterCondition;
		try {
			this.fields = StringUtils.getParameters(filterCondition, "F");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			this.parameters = StringUtils.getParameters(filterCondition, "P");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the entity name.
	 * 
	 * @return the entity name
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * Sets the entity name.
	 * 
	 * @param entityName the new entity name
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * Gets the filter condition.
	 * 
	 * @return the filter condition
	 */
	public String getFilterCondition() {
		return filterCondition;
	}

	/**
	 * Sets the filter condition.
	 * 
	 * @param filterCondition the new filter condition
	 */
	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	/**
	 * Gets the fields.
	 * 
	 * @return the fields
	 */
	public Set getFields() {
		return fields;
	}

	/**
	 * Gets the parameters.
	 * 
	 * @return the parameters
	 */
	public Set getParameters() {
		return parameters;
	}
}
