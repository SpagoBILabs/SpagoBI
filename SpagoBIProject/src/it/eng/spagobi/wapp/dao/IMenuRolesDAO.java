/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
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
