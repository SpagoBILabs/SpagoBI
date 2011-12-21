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

package it.eng.spagobi.tools.dataset.bo;

/**   @author Giulio Gavardi
 *     giulio.gavardi@eng.it
 */
 

public class DataSetParameterItem {

	private String name= "" ;
	private String type = "";
	
	/**
	 * Returns the name.
	 * 
	 * @return the name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 * 
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the type.
	 * 
	 * @return the type.
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 * 
	 * @param type the type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
}
