/*
*
* @file AuthenticationServiceImpl.java
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
* @version $Id: AuthenticationServiceImpl.java,v 1.5 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.sql.SQLException;

import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.exceptions.AuthenticationFailedException;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;
import org.palo.viewapi.services.AuthenticationService;

/**
 * <code>AuthenticationServiceImpl</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AuthenticationServiceImpl.java,v 1.5 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
class AuthenticationServiceImpl implements AuthenticationService {

	AuthenticationServiceImpl() {
	}
	
	public AuthUser authenticate(String login, String password)
			throws AuthenticationFailedException {
//		Connection sqlConnection = DbService.getConnection();
		try {
//			AuthenticationFactory authFab = AuthenticationFactory.getInstance();
//			Authentication authentication = authFab.createAuthentication(
//					sqlConnection, login, password);

			String pass = AuthUserImpl.encrypt(password);
			// TODO have to load the user and set the authentication:
			IUserManagement usrManager = 
				MapperRegistry.getInstance().getUserManagement();

			User user = (User) usrManager.findByName(login);
			if (user == null)
				throw new AuthenticationFailedException("Unknown user name!");
			// check password:
			if (!user.getPassword().equals(pass))
				throw new AuthenticationFailedException("Wrong password!");

			// create a new authenticated user:
			AuthUserImpl authUser = new AuthUserImpl(user);
			// set the authentication:
			//authUser.setAuthentication(authentication);
			return authUser;
		} catch (SQLException e) {
			System.err.println("Error during authentication!");
			throw new AuthenticationFailedException(
					"Error during authentication!", e);
		}
	}

	public AuthUser authenticateHash(String login, String password)
			throws AuthenticationFailedException {
		// Connection sqlConnection = DbService.getConnection();
		try {
			// AuthenticationFactory authFab =
			// AuthenticationFactory.getInstance();
			// Authentication authentication = authFab.createAuthentication(
			// sqlConnection, login, password);

			// TODO have to load the user and set the authentication:
			IUserManagement usrManager = MapperRegistry.getInstance()
					.getUserManagement();

			User user = (User) usrManager.findByName(login);
			if (user == null)
				throw new AuthenticationFailedException("Unknown user name!");
			// check password:
			if (!user.getPassword().equals(password)) {
				throw new AuthenticationFailedException("Wrong password!");
			}				

			// create a new authenticated user:
			AuthUserImpl authUser = new AuthUserImpl(user);
			// set the authentication:
			// authUser.setAuthentication(authentication);
			return authUser;
		} catch (SQLException e) {
			System.err.println("Error during authentication!");
			throw new AuthenticationFailedException(
					"Error during authentication!", e);
		}
	}

	public AuthUser authenticateAdmin() throws SQLException {
		IUserManagement usrManager = MapperRegistry.getInstance().getUserManagement();
		IUserRoleManagement urManager = MapperRegistry.getInstance().getUserRoleAssociation();
		IRoleManagement roleManager = MapperRegistry.getInstance().getRoleManagement();
		
		Role adminRole = null;
		for (Role r: roleManager.findAll()) {
			if (r.getName().equalsIgnoreCase("admin")) {
				adminRole = r;
				break;
			}
		}
		if (adminRole == null) {
			return null;
		}
		
		for (String usr: urManager.getUsers(adminRole)) {
			User user = (User) usrManager.find(usr);
			if (user != null) {
				try {
					AuthUser authUser = authenticateHash(user.getLoginName(), user.getPassword());			
					if (authUser != null) {
						return authUser;
					}
				} catch (Throwable t) {
				}
			}
		}
		return null;
	}
}
