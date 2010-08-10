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
/*
 * Created on 13-mag-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.eng.spagobi.commons.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Role;

import java.util.List;

/**
 * Defines  the interfaces for all methods needed to insert, modify and deleting a role
 * 
 * @author Zoppello
 */
public interface IRoleDAO {
	
	/**
	 * Loads a role identified by its
	 * <code>roleID</code>. All these information, are stored into a
	 * <code>Role</code> object, which is
	 * returned.
	 * 
	 * @param roleID The id for the role to load
	 * 
	 * @return A <code>Role</code> object containing all loaded information
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	public Role loadByID(Integer roleID) throws EMFUserError;
	
	/**
	 * Loads a role identified by its
	 * <code>roleName</code>. All these information, are stored into a
	 * <code>Role</code> object, which is
	 * returned.
	 * 
	 * @param roleName The name for the role to load
	 * 
	 * @return A <code>Role</code> object containing all loaded information
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	public Role loadByName(String roleName) throws EMFUserError;

	/**
	 * Loads all detail information for all roles. For each of them, detail
	 * information is stored into a <code>Role</code> object. After that,
	 * all roles are stored into a <code>List</code>, which is returned.
	 * 
	 * @return A list containing all role objects
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	public List loadAllRoles() throws EMFUserError;

	/**
	 * Implements the query to insert a role. All information needed is stored
	 * into the input <code>Role</code> object.
	 * 
	 * @param aRole The object containing all insert information
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	public void insertRole(Role aRole) throws EMFUserError;

	/**
	 * Implements the query to erase a role. All information needed is stored
	 * into the input <code>Role</code> object.
	 * 
	 * @param aRole The object containing all delete information
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	public void eraseRole(Role aRole) throws EMFUserError;

	/**
	 * Implements the query to modify a role. All information needed is stored
	 * into the input <code>Role</code> object.
	 * 
	 * @param aRole The object containing all modify information
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	public void modifyRole(Role aRole) throws EMFUserError;
	
	/**
	 * Gets all free roles for Insert. When a parameter has some parameter use modes
	 * associated, this association happens with one or more roles. For the same parameter,
	 * roles belonging to a parameter use mode cannot be assigned to others. So, when a new
	 * parameter use mode has to be inserted/modifies, this methods gives the roles that are
	 * still free.
	 * 
	 * @param parameterID The parameter id
	 * 
	 * @return The free roles list
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public List loadAllFreeRolesForInsert(Integer parameterID) throws EMFUserError;
	
	/**
	 * Gets all free roles for detail. When a parameter has some parameter use modes
	 * associated, this association happens with one or more roles. For the same parameter,
	 * roles belonging to a parameter use mode cannot be assigned to others. So, when a
	 * parameter use mode detail is required, this methods gives the roles that are
	 * still free.
	 * 
	 * @param parUseID The parameter use mode id
	 * 
	 * @return The free roles list
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public List loadAllFreeRolesForDetail(Integer parUseID) throws EMFUserError;
	
	
	/**
	 * Gets all the functionalities associated to the role.
	 * 
	 * @param roleID The role id
	 * 
	 * @return The functionalities associated to the role
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public List LoadFunctionalitiesAssociated(Integer roleID) throws EMFUserError;
	
	
	/**
	 * Gets all the parameter uses associated to the role.
	 * 
	 * @param roleID The role id
	 * 
	 * @return The parameter uses associated to the role
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public List LoadParUsesAssociated(Integer roleID) throws EMFUserError;
	/**
	 * Implements the query to insert a role. All information needed is stored
	 * into the input <code>Role</code> object.
	 * 
	 * @param aRole The object containing all insert information, includig the 
	 * role abilitations
	 * @return The role id
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	public Integer insertRoleComplete(Role aRole) throws EMFUserError;
	
}