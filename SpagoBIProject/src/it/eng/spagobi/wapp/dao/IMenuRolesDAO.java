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
package it.eng.spagobi.wapp.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.ISpagoBIDao;
import it.eng.spagobi.wapp.bo.MenuRoles;

import java.util.List;

/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 *
 */
public interface IMenuRolesDAO extends ISpagoBIDao{
	
	/**
	 * Loads all detail information for all menu compatible to the role specified
	 * at input. For each of them, name is stored into a <code>String</code> object.
	 * After that, all names are stored into a <code>List</code>, which is returned.
	 * 
	 * @param roleId the role id
	 * 
	 * @return A list containing all menu objects compatible with the role passed at input
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	public List loadMenuByRoleId(Integer roleId) throws EMFUserError;		
}
