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
package it.eng.spagobi.wapp.bo;


import java.io.Serializable;
/**
 * Defines a value constraint object.
 * 
 * @author Antonella Giachino (antonella.giachino@eng.it)
 *
 */


public class MenuRoles  implements Serializable   {

	private Integer menuId;
	private Integer extRoleId;
	
	/**
	 * Gets the menu id.
	 * 
	 * @return the menu id
	 */
	public Integer getMenuId() {
		return menuId;
	}
	
	/**
	 * Sets the menu id.
	 * 
	 * @param menuId the new menu id
	 */
	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}
	
	/**
	 * Gets the ext role id.
	 * 
	 * @return the ext role id
	 */
	public Integer getExtRoleId() {
		return extRoleId;
	}
	
	/**
	 * Sets the ext role id.
	 * 
	 * @param extRoleId the new ext role id
	 */
	public void setExtRoleId(Integer extRoleId) {
		this.extRoleId = extRoleId;
	}
	
		


}
