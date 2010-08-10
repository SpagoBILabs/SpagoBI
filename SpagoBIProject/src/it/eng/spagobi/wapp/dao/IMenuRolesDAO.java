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
import it.eng.spagobi.wapp.bo.MenuRoles;

import java.util.List;

/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 *
 */
public interface IMenuRolesDAO {
	
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
		
		
	/**
	 * Loads the list of MenuRoles associated to the input
	 * <code>menuId</code> and <code>roleId</code>. All these information,
	 * archived by a query to the DB, are stored into a List of <code>MenuRoles</code> object,
	 * which is returned.
	 * 
	 * @param menuId The id for the menu to load
	 * @param roleId The role id for the role to load
	 * 
	 * @return A List of <code>MenuRoles</code> object containing all loaded information
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	public MenuRoles loadMenuRoles(Integer menuId, Integer roleId) throws EMFUserError;

	/**
	 * Implements the query to modify a MenuRole. All information needed is stored
	 * into the input <code>MenuRoles</code> object.
	 * 
	 * @param aMenuRole The MenuRoles containing all modify information
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	public void modifyMenuRole(MenuRoles aMenuRole) throws EMFUserError;

	/**
	 * Implements the query to insert a MenuRole. All information needed is stored
	 * into the input <code>MenuRoles</code> object.
	 * 
	 * @param aMenuRole the a menu role
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	public void insertMenuRole(MenuRoles aMenuRole) throws EMFUserError;

	/**
	 * Implements the query to erase a MenuRoles. All information needed is stored
	 * into the input <code>MenuRoles</code> object.
	 * 
	 * @param aMenuRole The object containing all delete information
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	public void eraseMenuRole(MenuRoles aMenuRole) throws EMFUserError;	
	
}
