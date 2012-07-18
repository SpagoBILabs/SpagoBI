/*
*
* @file WPaloCubeViewConverter.java
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
* @version $Id: WPaloCubeViewConverter.java,v 1.16 2010/02/16 13:53:42 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.server.services.cubeview;

import org.palo.api.Connection;
import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.api.exceptions.PaloIOException;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.PaloAccount;
import org.palo.viewapi.Right;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.internal.ConnectionPoolManager;
import org.palo.viewapi.internal.ServerConnectionPool;
import org.palo.viewapi.services.AdministrationService;
import org.palo.viewapi.services.ServiceProvider;

import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.server.converter.XConverter;
import com.tensegrity.palo.gwt.core.server.services.cubeview.converter.CubeViewConverter;

public class WPaloCubeViewConverter {
	
	private final AuthUser user;
	
	public WPaloCubeViewConverter(AuthUser user) {
		this.user = user;
	}
	private final Account getAccount(String accountId) {
		Account account = null;
		if (isAdmin(user)) {
			AdministrationService adminService = ServiceProvider
					.getAdministrationService(user);
			account = adminService.getAccount(accountId);
		} else
			account = getAccount(accountId, user);
		return account;
	}
	
	public final View convertLegacyView(XView xView, String sessionId)
			throws OperationFailedException {
		Cube cube = null;
		try {
			if (!doCreateDefaultView(xView)) {
				try {
					try {
						cube = getCube(xView, sessionId);
						ConnectionPoolManager.getInstance().disconnect(getAccount(xView.getAccountId()), sessionId, "WPaloCubeViewConverter.convertLegacyView");
						View v = convert(cube.getCubeView(xView.getId()), xView.getName(), sessionId);
						return v;
					} finally {
						ConnectionPoolManager.getInstance().disconnect(getAccount(xView.getAccountId()), sessionId, "WPaloCubeViewConverter.convertLegacyView2");
					}
					
				} catch (PaloIOException e) {
					throw new OperationFailedException(
							"Could not find legacy view for '" + xView.getName() + "'!");
				}
			}
			return null;
		} finally {
			ConnectionPoolManager.getInstance().disconnect(getAccount(xView.getAccountId()), sessionId, "WPaloCubeViewConverter.convertLegacyView3");
		}
	}
	private final boolean doCreateDefaultView(XView xView) {
		return xView.getId() == null;
	}
	private final synchronized Cube getCube(XView xView, String sessionId) {
		Connection paloConnection = getConnection(xView.getAccountId(), sessionId);
		Database database = paloConnection.getDatabaseById(xView.getDatabaseId());
		return database.getCubeById(xView.getCubeId());
	}
		
	private final synchronized Connection getConnection(String accountId, String sessionId) {
		/*
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(user);
		Account account = adminService.getAccountById(accountId);
		return ((PaloAccount) account).login();
		*/
		Account account = getAccount(accountId);
		if (account instanceof PaloAccount) {
			Connection con = null;
			ServerConnectionPool pool = ConnectionPoolManager.getInstance().
				getPool(account, sessionId);
			con = pool.getConnection("WPaloCubeViewConverter.getConnection");	
			return con;
		}
		return null;
		
//		Account account = null;
//		if (isAdmin(user)) {
//			AdministrationService adminService = ServiceProvider
//					.getAdministrationService(user);
//			account = adminService.getAccount(accountId);
//		} else
//			account = getAccount(accountId, user);
//		if (account instanceof PaloAccount)
//			return ((PaloAccount) account).login();
//		return null;

	}
	private final boolean isAdmin(AuthUser user) {
		return user.hasPermission(Right.READ, AdministrationService.class);
	}
	private final Account getAccount(String id, AuthUser user) {
		for(Account account : user.getAccounts())
			if(account.getId().equals(id))
				return account;
		return null;
	}

	private final View convert(org.palo.api.CubeView legacyView,
			String newViewName, String sessionId)
			throws OperationFailedException {
		return CubeViewConverter.toView(newViewName, legacyView, user, sessionId);
	}

	
	public final View createDefaultView(XView xView, String sessionId)
			throws OperationFailedException {
		try {
			Cube cube = getCube(xView, sessionId);
			ConnectionPoolManager.getInstance().disconnect(getAccount(xView.getAccountId()), sessionId, "WPaloCubeViewConverter.createDefaultView");
			return createDefaultViewFor(cube, xView.getName(), xView.getAccountId(), sessionId, xView.getExternalId());
		} finally {
			ConnectionPoolManager.getInstance().disconnect(getAccount(xView.getAccountId()), sessionId, "WPaloCubeViewConverter.createDefaultView2");
		}
	}

	private final View createDefaultViewFor(Cube cube, String viewName,
			String accountId, String sessionId, String externalId) throws OperationFailedException {
		return CubeViewConverter.createDefaultView(viewName, cube, accountId,
				user, sessionId, externalId);
	}

	
	public final XView createX(View view) {
		return (XView)XConverter.createX(view);
	}
}
