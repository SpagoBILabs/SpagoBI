/*
*
* @file ServiceProviderImpl.java
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
* @version $Id: ServiceProviderImpl.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.sql.SQLException;

import org.palo.viewapi.AuthUser;
import org.palo.viewapi.DbConnection;
import org.palo.viewapi.Right;
import org.palo.viewapi.exceptions.NoPermissionException;
import org.palo.viewapi.services.AdministrationService;
import org.palo.viewapi.services.AuthenticationService;
import org.palo.viewapi.services.FolderService;
import org.palo.viewapi.services.ReportService;
import org.palo.viewapi.services.Service;
import org.palo.viewapi.services.ServiceProvider;
import org.palo.viewapi.services.ViewService;

/**
 * <code>ServiceProviderImpl</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ServiceProviderImpl.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public final class ServiceProviderImpl extends ServiceProvider {

//	protected DbConnection connection;
//	public ServiceProviderImpl(DbConnection connection) throws SQLException {
//		DbService.initialize(connection);
//	}

	//--------------------------------------------------------------------------
	// REQUIRED METHODS IMPLEMENTATIONs
	//
	protected final AdministrationService getAdminService(AuthUser forUser) {
		DbService.checkConnection();
		checkPermission(Right.GRANT, AdministrationService.class, forUser);
		//everything fine so:
		return new AdminServiceImpl(forUser);
	}

	protected final AuthenticationService getAuthService() {
		DbService.checkConnection();
		return new AuthenticationServiceImpl();
	}

	protected final ReportService getReportServiceFor(AuthUser forUser) {
		DbService.checkConnection();
		checkPermission(Right.READ, ReportService.class, forUser);
		//everything fine so:		
		return new ReportServiceImpl(forUser);
	}

	protected final ViewService getViewServiceFor(AuthUser forUser) {
		DbService.checkConnection();
		checkPermission(Right.READ, ViewService.class, forUser);
		//everything fine so:	
		return new ViewServiceImpl(forUser);
	}

	protected final FolderService getFolderServiceFor(AuthUser forUser) {
		DbService.checkConnection();
		checkPermission(Right.READ, FolderService.class, forUser);
		return new FolderServiceImpl(forUser);
	}
	
	protected final void releaseIt(DbConnection connection) {
		DbService.release(connection);
	}

//	protected final DbConnection getDbConnection() {
//		return DbService.getDbConnection();
//	}
	
	protected final void init(DbConnection connection, boolean createDefaultAccounts) throws SQLException {
		DbService.initialize(connection, createDefaultAccounts);
	}
	
	protected final DbConnection getDbInstanceConnection() {
		return DbService.getDbConnection();
	}
	
	private final void checkPermission(Right right,
			Class<? extends Service> serviceClass, AuthUser forUser) {
		if (!hasPermission(right, serviceClass, forUser))
			throw new NoPermissionException(
					"User has no permission to use this service", null,
					forUser, right);
	}
}
