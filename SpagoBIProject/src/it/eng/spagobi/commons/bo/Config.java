/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.commons.bo;

import java.io.Serializable;

/**
 * Defines a Domain object.
 */

public class Config  implements Serializable  {

	 private Integer id;
     private String label;
     private String name;
     private String description;
     private boolean isActive;
     private String valueCheck;
     private Integer valueTypeId;
     private String category;
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	/**
	 * @return the valueCheck
	 */
	public String getValueCheck() {
		return valueCheck;
	}
	/**
	 * @param valueCheck the valueCheck to set
	 */
	public void setValueCheck(String valueCheck) {
		this.valueCheck = valueCheck;
	}
	/**
	 * @return the valueTypeId
	 */
	public Integer getValueTypeId() {
		return valueTypeId;
	}
	/**
	 * @param valueTypeId the valueTypeId to set
	 */
	public void setValueTypeId(Integer valueTypeId) {
		this.valueTypeId = valueTypeId;
	}

	/**
	 * @return the category to get
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category. 
	 * The category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	
}




