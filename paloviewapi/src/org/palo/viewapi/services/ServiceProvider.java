/*
*
* @file ServiceProvider.java
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
* @version $Id: ServiceProvider.java,v 1.10 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.services;

import java.sql.SQLException;
import java.util.List;

import org.palo.viewapi.AuthUser;
import org.palo.viewapi.DbConnection;
import org.palo.viewapi.Group;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.exceptions.NoPermissionException;
import org.palo.viewapi.exceptions.NotConnectedException;
import org.palo.viewapi.internal.ServiceProviderImpl;

/**
 * The <code>ServiceProvider</code> contains methods for retrieving all defined 
 * services. 
 * Before the <code>ServiceProvider</code> can be used 
 * {@link #initialize(DbConnection)} must be called or otherwise a 
 * {@link NotConnectedException} will be thrown when accessing a service.
 * If a <code>DbConnection</code> is no longer valid {@link #release(DbConnection)}
 * should be called.
 * <p>Most services require an authorised user in order to work. To get
 * such an {@link AuthUser} use the {@link AuthenticationService} which can be 
 * retrieved by calling {@link #getAuthenticationService()}.
 * If a user has not enough permission to use a certain service 
 * a {@link NoPermissionException} will be thrown. To check that in front call
 * {@link #hasPermission(Right, Class, AuthUser)}. 
 * </p>
 *
 * @version $Id: ServiceProvider.java,v 1.10 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public abstract class ServiceProvider {
	
	//--------------------------------------------------------------------------
	// FACTORY
	//
	protected static ServiceProvider instance = new ServiceProviderImpl();
	
	/**
	 * Tells this <code>ServiceProvider</code> to initialise and to use the 
	 * given {@link DbConnection}. To release the connection call 
	 * {@link #release(DbConnection)}. 
	 * <p><b>Note:</b> if the <code>ServiceProvider</code>
	 * was initialised already calling this method will cause the former 
	 * <code>DbConnection</code> to be released before initialising the given 
	 * one.
	 * </p> 
	 * @param connection the <code>DbConnection</code> to use
	 */
	public static final void initialize(DbConnection connection, boolean createDefaultAccounts) {
		try {
			instance.init(connection, createDefaultAccounts);
		} catch (SQLException e) {
			// TODO what is the appropriate exception?
			throw new RuntimeException(
					"Failed to initialize service provider!", e);
		}
	}
	
	public static final DbConnection getDbConnection() {
		if (instance == null) {
			return null;
		}
		return instance.getDbInstanceConnection();
	}
		
	/**
	 * Releases the given {@link DbConnection}. If the
	 * @param connection
	 */
	public static final void release(DbConnection connection) {
		instance.releaseIt(connection);
	}
	
	
	/**
	 * Returns the {@link AuthenticationService} to use for login.
	 * 
	 * @return the <code>AuthenticationService</code>
	 * @throws NotConnectedException
	 *             if no <code>DbConnection</code> was initialised before
	 */
	public static final AuthenticationService getAuthenticationService() {
		return instance.getAuthService();
	}

	/**
	 * Returns the {@link AdministrationService} for the given user.
	 * 
	 * @param forUser
	 *            the user which wants to use the service
	 * @return the <code>AdministrationService</code>
	 * @throws NoPermissionException
	 *             if the user has not at least read permission for this
	 *             service.
	 * @throws NotConnectedException
	 *             if no <code>DbConnection</code> was initialised before
	 */
	public static final AdministrationService getAdministrationService(AuthUser forUser) {
		return instance.getAdminService(forUser);
	}

	/**
	 * Returns the {@link ReportService} for the given user.
	 * 
	 * @param forUser
	 *            the user which wants to use the service
	 * @return the <code>ReportService</code>
	 * @throws NoPermissionException
	 *             if the user has not at least read permission for this
	 *             service.
	 * @throws NotConnectedException
	 *             if no <code>DbConnection</code> was initialised before
	 */
	public static final ReportService getReportService(AuthUser forUser) {
		return instance.getReportServiceFor(forUser);
	}

	/**
	 * Returns the {@link ViewService} for the given user.
	 * 
	 * @param forUser
	 *            the user which wants to use the service
	 * @return the <code>ViewService</code>
	 * @throws NoPermissionException
	 *             if the user has not at least read permission for this
	 *             service.
	 * @throws NotConnectedException
	 *             if no <code>DbConnection</code> was initialised before
	 */
	public static final ViewService getViewService(AuthUser forUser) {
		return instance.getViewServiceFor(forUser);
	}
	
	public static final FolderService getFolderService(AuthUser forUser) {
		return instance.getFolderServiceFor(forUser);
	}
	
	/**
	 * Checks if the given user has enough permission to access the service
	 * specified by the given class with the given right.
	 * 
	 * @param right
	 *            the access right
	 * @param forService
	 *            the service to check
	 * @param user
	 *            the authorised user
	 * @return <code>true</code> if the user has enough rights,
	 *         <code>false</code> otherwise
	 */
	public static final boolean hasPermission(Right right,
			Class<? extends Service> forService, AuthUser user) {
		if (isAdmin(user)) {
			return true;
		}
		if (user != null && forService != null) {
			// currently the user has permission for this service if he has a
			// role with this right
			if (forService.equals(AdministrationService.class)) {
				return isAdmin(user);
			} else if (forService.equals(ViewService.class)
					|| forService.equals(ReportService.class)
					|| forService.equals(FolderService.class)) {
				List<Role> roles = user.getRoles();
				for (Role role : roles) {
					if (role.hasPermission(right))
						return true;
				}
				for (Group g: user.getGroups()) {
					for (Role role: g.getRoles()) {
						if (role.hasPermission(right))
							return true;
					}					
				}
			}
		}
		return false;
	}

	
	//--------------------------------------------------------------------------
	// the abstract methods:
	//
	protected abstract void releaseIt(DbConnection connection);
	
	protected abstract AuthenticationService getAuthService();
	
	protected abstract AdministrationService getAdminService(AuthUser forUser);
	
	protected abstract ReportService getReportServiceFor(AuthUser forUser);
	
	protected abstract ViewService getViewServiceFor(AuthUser forUser);
	
	protected abstract FolderService getFolderServiceFor(AuthUser forUser);
	
	protected abstract void init(DbConnection connection, boolean createDefaultAccounts) throws SQLException;
	
	protected abstract DbConnection getDbInstanceConnection();
	
	//--------------------------------------------------------------------------
	// PRIVATE HELPER METHODS
	//
	public static final boolean isAdmin(AuthUser user) {
		List<Role> roles = user.getRoles();
		for (Role role : roles)
			if (role.getName().equalsIgnoreCase("admin")
					&& role.hasPermission(Right.GRANT))
				return true;
		for (Group g: user.getGroups()) {
			for (Role role : g.getRoles())
				if (role.getName().equalsIgnoreCase("admin")
						&& role.hasPermission(Right.GRANT))
					return true;			
		}
		return false;
	}

}
