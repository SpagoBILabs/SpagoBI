/*
*
* @file ViewImporterTreeLoader.java
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
* @version $Id: ViewImporterTreeLoader.java,v 1.23 2010/02/16 13:53:42 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.server.childloader;

import java.util.ArrayList;
import java.util.List;

import org.palo.api.Connection;
import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.api.PaloObject;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.PaloAccount;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.View;
import org.palo.viewapi.internal.ConnectionPoolManager;
import org.palo.viewapi.internal.PaloAccountImpl;
import org.palo.viewapi.internal.ServerConnectionPool;
import org.palo.viewapi.services.AdministrationService;
import org.palo.viewapi.services.ServiceProvider;
import org.palo.viewapi.services.ViewService;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.palo.XCube;
import com.tensegrity.palo.gwt.core.client.models.palo.XDatabase;
import com.tensegrity.palo.gwt.core.server.converter.XConverter;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.client.serialization.XObjectWrapper;
import com.tensegrity.wpalo.client.ui.mvc.viewbrowser.importer.ViewImportDialog;

/**
 * <code>ViewImporterTreeLoader</code> TODO DOCUMENT ME
 * 
 * @version $Id: ViewImporterTreeLoader.java,v 1.23 2010/02/16 13:53:42 PhilippBouillon Exp $
 **/
public class ViewImporterTreeLoader implements ChildLoader {

	public boolean accepts(XObject parent) {
		return parent.getType().equals(ViewImportDialog.XOBJECT_TYPE);
	}

	public XObject[] loadChildren(XObject parent, UserSession userSession) {
		XObject xObj = ((XObjectWrapper)parent).getXObject();
		String sessionId = userSession.getSessionId();
		AuthUser loggedInUser = userSession.getUser();
		if (xObj instanceof XAccount)
			return load((XAccount) xObj, loggedInUser, sessionId);
		else if (xObj instanceof XDatabase)
			return load((XDatabase) xObj, loggedInUser, sessionId);
		else if (xObj instanceof XCube)
			return load((XCube) xObj, loggedInUser, sessionId, userSession);
		return new XObject[0];
	}

	private final XObject[] load(XAccount xAccount, AuthUser user, String sessionId) {
		try {
			String accountId = xAccount.getId();
			Connection paloConnection = getConnectionForAccount(accountId, user, sessionId);
			if(paloConnection != null) {
				if (paloConnection.getType() == Connection.TYPE_HTTP) {
					paloConnection.clearCache();
				}
				Database[] databases = paloConnection.getDatabases();
				List<XObject> xDatabases = new ArrayList<XObject>();
				for(Database database : databases) {
					if(isValid(database)) {
						XObject xDatabase = XConverter.createX(database, xAccount.getId());
						xDatabases.add(wrap(xDatabase));
					}
				}				
				return xDatabases.toArray(new XObject[0]);
			}
			return new XObject[0];
		} finally {
			Account acc = getAccount(xAccount.getId(), user);
			ConnectionPoolManager.getInstance().disconnect(acc, sessionId, "ViewImporterTreeLoader.load");
		}
	}

	private final XObject[] load(XDatabase xDatabase, AuthUser user, String sessionId) {
		try {
			Database database = getDatabase(xDatabase, user, sessionId);
			if(database != null) {
				//			if (database.getConnection().getType() == Connection.TYPE_HTTP) {
				//				((DatabaseImpl) database).clearCache();
//				}
				Cube[] cubes = database.getCubes(PaloObject.TYPE_NORMAL);
				List<XObject> xCubes = new ArrayList<XObject>();
				for(Cube cube : cubes) {
					if(isValid(cube)) {
						XObject xCube = 
							new XCube(cube.getId(), cube.getName(), xDatabase);					
						xCubes.add(wrap(xCube));
					}
				}
				return xCubes.toArray(new XObject[0]);
			}
			return new XObject[0];
		} finally {
			Account acc = getAccount(xDatabase.getAccountId(), user);
			ConnectionPoolManager.getInstance().disconnect(acc, sessionId, "ViewImporterTreeLoader.load2");
		}
	}

	//can be used to filter out cubes which are of type normal but shouldn't be
	//shown anyway...
	private boolean isValid(Cube cube) {
		String cubeName = cube.getName();
		if(!cubeName.startsWith("#")) {
			int connectionType = cube.getDatabase().getConnection().getType();
			if(connectionType == Connection.TYPE_XMLA) {
				return !cubeName.equalsIgnoreCase("$INFOCUBE");
			}
			return true;
		}
		return false;
	}
	private boolean isValid(Database database) {
		if (database.isSystem() || database.getType() == Database.TYPE_USER_INFO) {
			return false;
		}
		String databaseName = database.getName();
		int connectionType = database.getConnection().getType();
		if(connectionType == Connection.TYPE_XMLA) {
			boolean isValid = !databaseName.equalsIgnoreCase("$INFOCUBE");
			isValid = isValid && !databaseName.equalsIgnoreCase("0CCA_C11");
			return isValid;
		}
		return true;
	}
	
	private final XObject[] load(XCube xCube, AuthUser user, String sessionId, UserSession userSession) {
		try {
			Cube cube = getCube(xCube, user, sessionId);
			if (cube != null) {
				String viewIds[] = cube.getCubeViewIds();
				List<XObject> views = new ArrayList<XObject>();
				addDefaultViewFor(cube, xCube.getAccountId(), views, user, userSession);
				for(String viewId : viewIds) {
					String viewName = cube.getCubeViewName(viewId);
					XView view = createXView(viewId, viewName, cube, xCube.getAccountId(), user);
					XObjectWrapper wrappedCandidate = wrap(view);
					wrappedCandidate.setHasChildren(false);
					views.add(wrappedCandidate);
				}	
				return views.toArray(new XObject[0]);
			}
			return new XObject[0];
		} finally {
			Account acc = getAccount(xCube.getAccountId(), user);
			ConnectionPoolManager.getInstance().disconnect(acc, sessionId, "ViewImporterTreeLoader.load3");
		}
	}

	private final Cube getCube(XCube xCube, AuthUser user, String sessionId) {
		Database database = getDatabase(xCube.getDatabase(), user, sessionId);
		if(database != null)
			return database.getCubeById(xCube.getId());
		return null;
	}
	private final void addDefaultViewFor(Cube cube, String accountId,
			List<XObject> toViews, AuthUser user, UserSession session) {
		String viewId = null;
		String viewName = cube.getName() + session.translate("defaultView"); 
		XView view = createXView(viewId, viewName, cube, accountId, user);
		XObjectWrapper wrappedCandidate = wrap(view);
		wrappedCandidate.setHasChildren(false);
		toViews.add(wrappedCandidate);
	}
	private final XView createXView(String id, String name, Cube cube,
			String accountId, AuthUser user) {
		XView view = new XView(id, name);
		view.setCubeId(cube.getId());
		view.setDatabaseId(cube.getDatabase().getId());
		view.setAccountId(accountId);
		ViewService vService = ServiceProvider.getViewService(user);
		View v = vService.getView(id);
		if (v != null && v.getOwner() != null) {
			view.setOwnerId(v.getOwner().getId());			
		}
		if (v != null) {
			List <String> ids = new ArrayList<String>();
			List <String> names = new ArrayList <String>();
			for (Role r: v.getRoles()) {
				ids.add(r.getId());
				names.add(r.getName());
			}
			view.setRoleIds(ids);		
			view.setRoleNames(names);
		}
		return view;
	}
	
	private final Database getDatabase(XDatabase xDatabase, AuthUser user, String sessionId) {
		Connection paloConnection = getConnectionForAccount(xDatabase
				.getAccountId(), user, sessionId);
		if (paloConnection != null)
			return paloConnection.getDatabaseById(xDatabase.getId());
		return null;
	}
	private final synchronized Connection getConnectionForAccount(String accountId, AuthUser user, String sessionId) {
		Account account = null;
		if (isAdmin(user)) {
			AdministrationService adminService = ServiceProvider
					.getAdministrationService(user);
			account = adminService.getAccount(accountId);
		} else
			account = getAccount(accountId, user);
		if (account instanceof PaloAccount) {
//			((PaloAccount) account).login();
			ServerConnectionPool pool = 
				ConnectionPoolManager.getInstance().getPool(account, sessionId);
			Connection con = pool.getConnection("ViewImporterTreeLoader.getConnectionForAccount");
			((PaloAccountImpl) account).setConnection(con);
			return con;
		}
		return null;
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
	private final XObjectWrapper wrap(XObject xObj) {
		XObjectWrapper wrapped= new XObjectWrapper(xObj);
		wrapped.setType(ViewImportDialog.XOBJECT_TYPE);
		wrapped.setHasChildren(true);
		return wrapped;
	}
}
