/*
*
* @file ViewService.java
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
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: ViewService.java,v 1.10 2010/01/13 08:02:42 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.services;

import java.util.List;

import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.NoAccountException;
import org.palo.viewapi.exceptions.NoPermissionException;
import org.palo.viewapi.exceptions.OperationFailedException;

/**
 * The <code>ViewService</code> interface defines methods to create and change
 * {@link View} instances.
 *
 * @version $Id: ViewService.java,v 1.10 2010/01/13 08:02:42 PhilippBouillon Exp $
 **/
public interface ViewService extends Service {
	/** 
	 * Creates a new view for the given {@link Cube}. Note that palo requires
	 * that the cube view name must be unique per cube. 
	 * @param name the view name
	 * @param forCube the palo cube to create the view for
	 * @return the new <code>View</code> instance
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to create a new view
	 * @throws NoAccountException if the calling {@link AuthUser} has no account
	 * on the palo connection which host the specified cube 
	 * @throws OperationFailedException if the creation of the new view fails 
	 */
	public View createViewAsSubobject(String name, Cube forCube, AuthUser user, String sessionId, String externalId) throws OperationFailedException;
	
	/** 
	 * Creates a new view for the given {@link Cube}. Note that palo requires
	 * that the cube view name must be unique per cube. 
	 * @param name the view name
	 * @param forCube the palo cube to create the view for
	 * @return the new <code>View</code> instance
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to create a new view
	 * @throws NoAccountException if the calling {@link AuthUser} has no account
	 * on the palo connection which host the specified cube 
	 * @throws OperationFailedException if the creation of the new view fails 
	 */
	public View createView(String name, Cube forCube, AuthUser user, String sessionId, String externalId) throws OperationFailedException;
	/**
	 * Checks if a view with the specified name does already exist for the given
	 * {@link Cube}.
	 * @param name a view name 
	 * @param forCube the cube to check
	 * @return <code>true</code> if a view with the given name exists for the
	 * specified cube, <code>false</code> otherwise
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to read any views
	 * @throws NoAccountException if the calling {@link AuthUser} has no account
	 * on the palo connection which host the specified cube 
	 */
	public boolean doesViewExist(String name, Cube forCube);
	
	/** 
	 * Returns the view with the given id or <code>null</code> if none exists.
	 * @return the corresponding view or <code>null</code>
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to read any views
	 * @throws NoAccountException if the calling {@link AuthUser} has no account
	 * on the palo connection which host the cube specified by given id 
	 */
	public View getView(String id);
	/** 
	 * Returns the view with the given name for the specified {@link Cube} or 
	 * <code>null</code> if none exists.
	 * @return the corresponding view or <code>null</code>
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to read any views
	 * @throws NoAccountException if the calling {@link AuthUser} has no account
	 * on the palo connection which host the specified cube 
	 */
	public View getViewByName(String name, Cube forCube);
	
	/** 
	 * Returns all views which can be accessed via the given {@link Account}.
	 * <b>Note:</b> views which failed to load are simply ignored and therefore 
	 * not within returned list.
	 * @param the account to get the views for
	 * @return all views which can be accessed via the given account
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to read any views of this account
	 */
	public List<View> getViews(Account forAccount);
	//TODO should we pass an observer which get notified about failed views??
	
	public boolean hasViews(Account forAccount);
	
	/**
	 * Saves the given view.
	 * @param view the view to save
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to modify the given view
	 * @throws OperationFailedException if saving of given view fails
	 */
	public void save(View view) throws OperationFailedException;
	/**
	 * Deletes the given view.
	 * @param view the view to delete
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to modify the given view
	 * @throws OperationFailedException if deletion of given view fails
	 */
	public void delete(View view) throws OperationFailedException;
	
	/**
	 * Adds the specified {@link Role} to the given {@link View} in order to 
	 * change the access rights of this view.
	 * @param role the role to add
	 * @param toView the view to add the role to
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to modify the given view
	 * @throws OperationFailedException if modification of given view fails
	 */
	public void add(Role role, View toView) throws OperationFailedException;
	/**
	 * Removes the specified {@link Role} from the given {@link View}. 
	 * @param role the role to remove
	 * @param toView the view to remove the role from
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to modify the given view
	 * @throws OperationFailedException if modification of given view fails
	 */
	public void remove(Role role, View fromView) throws OperationFailedException;

	//view changes:
	/**
	 * Sets the new owner of the given {@link View}.
	 * <b>Note:</b> the change is not persistent until the view is saved
	 * @param owner the new owner
	 * @param ofView the view to change 
	 */
	public void setOwner(User owner, View ofView);
	public void setAccount(Account acc, View ofView);
	public void setDatabase(String dbId, View ofView);
	public void setCube(String cubeId, View ofView);
	
	/**
	 * Sets the new xml definition of the given {@link View}.
	 * <b>Note:</b> the change is not persistent until the view is saved 
	 * @param xml the new xml definition
	 * @param ofView the view to change
	 */
	public void setDefinition(String xml, View ofView);
	
	/**
	 * Sets the new name of the given {@link View}.
	 * <b>Note:</b> the change is not persistent until the view is saved 
	 * @param name the new view name
	 * @param ofView the view to change
	 */
	public void setName(String name, View ofView);
	
	
//	public void setAccount(IAccount account, IView ofView);
	//FOLLOWING CHANGES ARE DONE VIA CUBE VIEW!!!
//	public void setCube(String id, IView ofView);
//	public void setDatabase(String id, IView ofView);
//	public void setDefinition(String xml, IView ofView);
//	public void setName(String name, IView ofView);
	
	
}
