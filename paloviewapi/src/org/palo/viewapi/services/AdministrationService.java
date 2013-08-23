/*
*
* @file AdministrationService.java
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
* @version $Id: AdministrationService.java,v 1.13 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.services;

import java.util.List;

import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Group;
import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.exceptions.NoPermissionException;
import org.palo.viewapi.exceptions.OperationFailedException;

/**
 * The <code>AdministrationService</code> interface defines methods for general
 * administration tasks like creating, updating and deleting {@link User}s, 
 * {@link Account}s, {@link Group}s and so on. 
 * This service requires admin rights and is therefore only for those users
 * which belong to the admin role. 
 *
 * @version $Id: AdministrationService.java,v 1.13 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public interface AdministrationService extends Service {

	//GLOBAL ACTIONS:
	/** clears all internally used caches */
//TODO required??	public void reset();
	

	// -- USER API --
	public User getUser(String id);
	/** 
	 * Returns all currently known {@link User}s .
	 * <b>Note:</b> users which failed to load are simply ignored and therefore 
	 * not within returned list.
	 * @return all currently known <code>Users</code>
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to read any users
	 */
	public List<User> getUsers();
	//TODO should we pass an observer which get notified about failed users??
	
	/** 
	 * Creates a new {@link User} with the given first and last name and the
	 * specified login and password. Note that the chosen login must be unique. 
	 * @param firstname the first name of the new user
	 * @param lasstname the last name of the new user
	 * @param login the unique login name of the new user
	 * @param password the password of the new user
	 * @return the new <code>User</code> instance
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to create a new user
	 * @throws OperationFailedException if the creation of the new user fails
	 */
	public User createUser(String firstname, String lastname, String login,
			String password) throws OperationFailedException;
	/**
	 * Deletes the given {@link User}.
	 * @param user the user to delete
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to delete the given user
	 * @throws OperationFailedException if deletion of given user fails
	 */
	public void delete(User user) throws OperationFailedException;
	/**
	 * Saves the given {@link User}.
	 * @param user the user to save
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to save the given user
	 * @throws OperationFailedException if saving of given user fails
	 */
	public void save(User user) throws OperationFailedException;
	/**
	 * Sets the first name of the given {@link User}.
	 * <b>Note:</b> the change is not persistent until the user is saved.
	 * @param name the users new first name
	 * @param ofUser the user to change
	 */
	public void setFirstname(String name, User ofUser);
	/**
	 * Sets the last name of the given {@link User}.
	 * <b>Note:</b> the change is not persistent until the user is saved.
	 * @param name the users new last name
	 * @param ofUser the user to change
	 */
	public void setLastname(String name, User ofUser);
	/**
	 * Sets the login name of the given {@link User}.
	 * <b>Note:</b> the change is not persistent until the user is saved. 
	 * @param name the users new login name
	 * @param ofUser the user to change
	 */
	public void setLoginName(String name, User ofUser);
	/**
	 * Sets the password of the given {@link User}.
	 * <b>Note:</b> the change is not persistent until the user is saved. 
	 * @param password the users new password
	 * @param ofUser the user to change
	 */
	public void setPassword(String password, User ofUser);
	
	//USER-GROUPS ASSOCIATION:
	/**
	 * Adds the given {@link User} to the specified {@link Group}. If the
	 * group already contains the specified user this method has no effect.
	 * @param user the user to add
	 * @param toGroup the group to change
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to modify the given user
	 * @throws OperationFailedException if modification of given group fails
	 */
	public void add(User user, Group toGroup) throws OperationFailedException;
	/**
	 * Removes the given {@link User} from the specified {@link Group}.
	 * @param user the user to remove
	 * @param fromGroup the group to change
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to modify the given user
	 * @throws OperationFailedException if modification of given group fails
	 */
	public void remove(User user, Group fromGroup) throws OperationFailedException;
	/**
	 * Sets the {@link User}s of specified {@link Group}. Note that all formerly
	 * added users are removed before!
	 * @param users list of users
	 * @param ofGroup the group to change
	 * @throws OperationFailedException if adding users fails
	 */
	public void setUsers(List<User> users, Group ofGroup) throws OperationFailedException;
	public void setGroups(List<Group> groups, User user) throws OperationFailedException;

	//USER-ROLE ASSOCIATION:
	/**
	 * Adds the given {@link Role} to the specified {@link User}.
	 * @param role the role to add
	 * @param toUser the user to add the role to
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to modify the given user
	 * @throws OperationFailedException if modification of given user fails
	 */
	public void add(Role role, User toUser) throws OperationFailedException;
	/**
	 * Removes the given {@link Role} from the specified {@link User}.
	 * @param role the role to remove
	 * @param fromUser the user to remove the role from
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to modify the given user
	 * @throws OperationFailedException if modification of given user fails
	 */
	public void remove(Role role, User fromUser) throws OperationFailedException;
//	/**
//	 * Sets the {@link User}s of specified {@link Role}. Note that all formerly
//	 * added users are removed before!
//	 * @param users list of users
//	 * @param ofRole the role to change
//	 * @throws OperationFailedException if adding users fails
//	 */
//	public void setUsers(List<User> users, Role ofRole) throws OperationFailedException;
	public void setRoles(List<Role> roles, User user) throws OperationFailedException;
		
	
	// -- GROUP API --
	public Group getGroup(String id);
	/** 
	 * Returns all currently known {@link Group}s.
	 * <b>Note:</b> groups which failed to load are simply ignored and therefore 
	 * not within returned list.
	 * @return all currently known <code>Groups</code>
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to read any groups
	 */
	public List<Group> getGroups();
	//TODO should we pass an observer which get notified about failed groups??
	
	public List<Group> getGroups(User forUser);
	
	/** 
	 * Creates a new {@link Group} with the given name. Note that the name 
	 * must be unique, i.e. there must be no other group with the same name. 
	 * @param name the group name
	 * @return the new <code>Group</code> instance
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to create a new group
	 * @throws OperationFailedException if the creation of the new group fails
	 */
	public Group createGroup(String name) throws OperationFailedException;
	/**
	 * Deletes the given {@link Group}.
	 * @param group the group to delete
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to delete the given group
	 * @throws OperationFailedException if deletion of given group fails
	 */
	public void delete(Group group) throws OperationFailedException;
	/**
	 * Saves the given {@link Group}.
	 * @param group the group to save
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to save the given group
	 * @throws OperationFailedException if saving of given group fails
	 */
	public void save(Group group) throws OperationFailedException;
	
	//GROUP-ROLE ASSOCIATION:
	/**
	 * Adds the given {@link Role} to the specified {@link Group}.
	 * @param role the role to add
	 * @param toGroup the group to add the role to 
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to modify the given group
	 * @throws OperationFailedException if modification of given group fails
	 */
	public void add(Role role, Group toGroup) throws OperationFailedException;
	/**
	 * Removes the given {@link Role} from the specified {@link Group}.
	 * @param role the role to remove
	 * @param fromGroup the group to remove the role from
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to modify the given group
	 * @throws OperationFailedException if modification of given group fails
	 */
	public void remove(Role role, Group fromGroup) throws OperationFailedException;
//	/**
//	 * Sets the {@link Group}s of specified {@link Role}. Note that all formerly
//	 * added groups are removed before!
//	 * @param groups list of groups to set
//	 * @param ofRole the role to change
//	 * @throws OperationFailedException if adding groups fails
//	 */
//	public void setGroups(List<Group> groups, Role ofRole) throws OperationFailedException;
	/**
	 * Sets the new name of the given {@link Group}.
	 * <b>Note:</b> the change is not persistent until the group is saved. 
	 * @param name the new group name
	 * @param ofGroup the group to change
	 */
	public void setName(String name, Group ofGroup);
	/**
	 * Sets the new description of the given {@link Group}.
	 * <b>Note:</b> the change is not persistent until the group is saved. 
	 * @param description the new group description
	 * @param ofGroup the group to change
	 */
	public void setDescription(String description, Group ofGroup);
	
	public void setRoles(List<Role> roles, Group ofGroup) throws OperationFailedException;
	
	// -- ROLE API --
	/** 
	 * Returns the {@link Role} with the given id or <code>null</code> if no 
	 * role with such an id exists.
	 * @return the <code>Roles</code> with the specified id or 
	 * <code>null</code>
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to read any roles
	 */
	public Role getRole(String id);
	/** 
	 * Returns the {@link Role} with the given name or <code>null</code> if no 
	 * such exists. Note: if several roles have the same name only the first
	 * found is returned.
	 * @return the <code>Roles</code> with the specified name or 
	 * <code>null</code>
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to read any roles
	 */
	public Role getRoleByName(String name);
	/** 
	 * Returns all currently known {@link Role}s .
	 * <b>Note:</b> roles which failed to load are simply ignored and therefore 
	 * not within returned list.
	 * @return all currently known <code>Roles</code>
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to read any roles
	 */
	public List<Role> getRoles();
	//TODO should we pass an observer which get notified about failed roles??
	
	public List<Role> getRoles(User forUser);
	
	/** 
	 * Creates a new {@link Role} with the given name. Note that the name 
	 * must be unique, i.e. there must be no other role with the same name. 
	 * @param name the role name. Initially the new role has only 
	 * {@link Right#NONE} permission.
	 * @return the new <code>Role</code> instance
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to create a new role
	 * @throws OperationFailedException if the creation of the new role fails
	 */
	public Role createRole(String name) throws OperationFailedException;
	/** 
	 * Creates a new {@link Role} with the given name and the specified 
	 * permission. Note that the name must be unique, i.e. there must be no 
	 * other group with the same name. 
	 * @param name the role name
	 * @param right the initial role right
	 * @return the new <code>Role</code> instance
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to create a new role
	 * @throws OperationFailedException if the creation of the new role fails
	 */
	public Role createRole(String name, Right right) throws OperationFailedException;
	/**
	 * Deletes the given {@link Role}.
	 * @param role the role to delete
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to delete the given role
	 * @throws OperationFailedException if deletion of given role fails
	 */
	public void delete(Role role) throws OperationFailedException;
	/**
	 * Saves the given {@link Role}.
	 * @param role the role to save
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to save the given role
	 * @throws OperationFailedException if saving of given role fails
	 */
	public void save(Role role) throws OperationFailedException;
	/**
	 * Sets the new description of the given {@link Role}.
	 * <b>Note:</b> the change is not persistent until the role is saved. 
	 * @param description the new role description
	 * @param ofRole the role to change
	 */
	public void setDescription(String description, Role ofRole);
	/**
	 * Sets the new name of the given {@link Role}.
	 * <b>Note:</b> the change is not persistent until the role is saved. 
	 * @param name the new role name
	 * @param ofRole the role to change
	 */
	public void setName(String name, Role ofRole);
	/**
	 * Sets the new permission of the given {@link Role}.
	 * <b>Note:</b> the change is not persistent until the role is saved. 
	 * @param right the new role permission
	 * @param ofRole the role to change
	 */
	public void setPermission(Right right, Role ofRole);

	
	// -- CONNECTION API --
	public PaloConnection getConnection(String id);
	/** 
	 * Returns all currently known {@link PaloConnection}s.
	 * <b>Note:</b> connections which failed to load are simply ignored and 
	 * therefore not within returned list.
	 * @return all currently known <code>PaloConnections</code>
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to read any connections
	 */
	public List<PaloConnection> getConnections();
	//TODO should we pass an observer which get notified about failed connections??
	
	/** 
	 * Creates a new {@link PaloConnection} of specified type with the given 
	 * name, host and service.
	 * @param name the connection name
	 * @param host the connection host string
	 * @param service the connection service string
	 * @param type the connection type, i.e. one of the predefined constants
	 * @return the new <code>PaloConnection</code> instance
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to create a new connection
	 * @throws OperationFailedException if the creation of the new connection fails
	 */
	public PaloConnection createConnection(String name, String host, String service, int type) throws OperationFailedException;
	/**
	 * Deletes the given {@link PaloConnection}.
	 * @param connection the palo connection to delete
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to delete the given connection
	 * @throws OperationFailedException if deletion of given connection fails
	 */
	public void delete(PaloConnection connection) throws OperationFailedException;
	/**
	 * Saves the given {@link PaloConnection}.
	 * @param connection the palo connection to save
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to save the given connection
	 * @throws OperationFailedException if saving of given connection fails
	 */
	public void save(PaloConnection connection) throws OperationFailedException;
	/**
	 * Sets the new description of the given {@link PaloConnection}.
	 * <b>Note:</b> the change is not persistent until the connection is saved. 
	 * @param description the new palo connection description
	 * @param ofConnection the palo connection to change
	 */
	public void setDescription(String description, PaloConnection ofConnection);
	/**
	 * Sets the new name of the given {@link PaloConnection}.
	 * <b>Note:</b> the change is not persistent until the connection is saved. 
	 * @param name the new palo connection name
	 * @param ofConnection the palo connection to change
	 */
	public void setName(String name, PaloConnection ofConnection);
	/**
	 * Sets the new host of the given {@link PaloConnection}.
	 * <b>Note:</b> the change is not persistent until the connection is saved. 
	 * @param host the new palo connection host
	 * @param ofConnection the palo connection to change
	 */
	public void setHost(String host, PaloConnection ofConnection);
	/**
	 * Sets the new service  of the given {@link PaloConnection}.
	 * <b>Note:</b> the change is not persistent until the connection is saved. 
	 * @param service the new palo connection service
	 * @param ofConnection the palo connection to change
	 */
	public void setService(String service, PaloConnection ofConnection);
	/**
	 * Sets the new connection type of the given {@link PaloConnection}.
	 * <b>Note:</b> the change is not persistent until the connection is saved. 
	 * @param type the new connection type
	 * @param ofConnection the palo connection to change
	 */
	public void setType(int type, PaloConnection ofConnection);
	
	
	// -- ACCOUNT API --
	
	public Account getAccount(String id);
	
	/** 
	 * Returns all currently known {@link Account}s.
	 * <b>Note:</b> accounts which failed to load are simply ignored and 
	 * therefore not within returned list.
	 * @return all currently known <code>Accounts</code>
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to read any accounts
	 */
	public List<Account> getAccounts();
	//TODO should we pass an observer which get notified about failed accounts??
		
	public List<Account> getAccounts(User forUser);
	
	/** 
	 * Creates a new {@link Account} with the given login name and password for
	 * the specified {@link User} and {@link PaloConnection}.
	 * @param login the login name to use for the new account
	 * @param password the password to use for the new account
	 * @param forUser the user to create the account for
	 * @param connection the <code>PaloConnection</code> to connect to with this
	 * account
	 * @return the new <code>Account</code> instance
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to create a new account
	 * @throws OperationFailedException if the creation of the new account fails
	 */
	public Account createAccount(String login, String password,
			User forUser, PaloConnection connection) throws OperationFailedException;
	/**
	 * Deletes the given {@link Account}.
	 * @param account the account to delete
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to delete the given account
	 * @throws OperationFailedException if deletion of given account fails
	 */
	public void delete(Account account) throws OperationFailedException;
	/**
	 * Saves the given {@link Account}.
	 * @param account the account to save
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to save the given account
	 * @throws OperationFailedException if saving of given account fails
	 */
	public void save(Account account) throws OperationFailedException;
	/**
	 * Sets the new {@link PaloConnection} to use for the given {@link Account}.
	 * <b>Note:</b> the change is not persistent until the account is saved. 
	 * @param connection the new connection to use
	 * @param ofAccount the account to change
	 */
	public void setConnection(PaloConnection connection, Account ofAccount);
	/**
	 * Sets the new login name to use for the given {@link Account}.
	 * <b>Note:</b> the change is not persistent until the account is saved. 
	 * @param name the new login to use
	 * @param ofAccount the account to change
	 */
	public void setLoginName(String name, Account ofAccount);
	/**
	 * Sets the new password to use for the given {@link Account}.
	 * <b>Note:</b> the change is not persistent until the account is saved. 
	 * @param password the new password to use
	 * @param ofAccount the account to change
	 */
	public void setPassword(String password, Account ofAccount);
	/**
	 * Sets the new {@link User} of given {@link Account}.
	 * <b>Note:</b> the change is not persistent until the account is saved. 
	 * @param user the new user
	 * @param ofAccount the account to change
	 */
	public void setUser(User user, Account ofAccount);
}
