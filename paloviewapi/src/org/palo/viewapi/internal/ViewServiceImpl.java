/*
*
* @file ViewServiceImpl.java
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
* @version $Id: ViewServiceImpl.java,v 1.19 2010/01/13 08:02:42 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.palo.api.Connection;
import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.api.exceptions.PaloIOException;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.NoAccountException;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.internal.cubeview.CubeViewFactory;
import org.palo.viewapi.internal.io.CubeViewIO;
import org.palo.viewapi.services.ViewService;

/**
 * <code>ViewService</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewServiceImpl.java,v 1.19 2010/01/13 08:02:42 PhilippBouillon Exp $
 **/
final class ViewServiceImpl extends InternalService implements ViewService {

	ViewServiceImpl(AuthUser user) {
		super(user);
	}
	
	
	public final View createView(String name, Cube cube, AuthUser authUser, String sessionId, String externalId)
			throws OperationFailedException {
		AccessController.checkPermission(Right.CREATE, user);
		try {
			// do we have an account for it:
			Account forAccount = getAccount(cube);
			ViewImpl view = new ViewImpl(null);
			view.setName(name);
			view.setOwner(user);
			// use account			
			try {
				view.setAccount(authUser, forAccount, sessionId);
			} catch (PaloIOException e) {
				throw new OperationFailedException(e.getMessage(), e);
			}
			view.setCube(cube.getId());
			view.setDatabase(cube.getDatabase().getId());
			// user should get owner role:
			Role ownerRole = (Role) getRoleManagement().findByName(Role.OWNER);
			if (ownerRole != null) {
				user.add(ownerRole);
			}
			///understands if it is save as SpagoBI subobj 

			getViewManagement().insert(view);

			
			// use cube view:
			CubeView cubeView = CubeViewFactory.createView(view, cube, authUser, externalId);
			view.setDefinition(CubeViewIO.toXML(cubeView));
			return view;
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to create view", e);
		}
	}
	
	public final void delete(View view) throws OperationFailedException {
		AccessController.checkPermission(Right.DELETE, view, user);
		//no exception, so we can remove the view:
		try {
			getViewManagement().delete(view);
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to delete view", e);
		}
	}
	
	public final void save(View view) throws OperationFailedException {
		AccessController.checkPermission(Right.WRITE, view, user);
		//no exception, so we can update the view:
		//TODO where to cache cube view???
		try {
			//setDefinition(CubeViewIO.toXML(view.createCubeView()), view);
			CubeView cubeView = view.getCubeView();
			if(cubeView != null) {
				String xml = CubeViewIO.toXML(cubeView);
				ViewImpl _view = (ViewImpl)view;
				_view.setDefinition(xml, false);
			}
			getViewManagement().update(view);
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to save view", e);
		}
	}
	
	public final List<View> getViews(Account forAccount) {
		AccessController.checkPermission(Right.READ, user);
		IViewManagement viewMgmt = getViewManagement();
		List<View> result = new ArrayList<View>();
		try {
			List<View> views = viewMgmt.findViews(forAccount);
			for (View view : views) {
				if (view.isOwner(user))
					result.add(view);
				else
					for (Role role : view.getRoles()) {
						if(user.hasRole(role)) {
							result.add(view);
							break;
						}
					}
			}
		} catch (SQLException e) { /* ignore */
		}
		return result;
	}
	
	public final boolean hasViews(Account forAccount) {
		AccessController.checkPermission(Right.READ, user);
		IViewManagement viewMgmt = getViewManagement();
		try {
			return viewMgmt.hasViews(forAccount);
		} catch (SQLException e) { /* ignore */
		}
		return false;
	}
	
	public final View getView(String id) {
		AccessController.checkPermission(Right.READ, user);
		ViewImpl view = null;
		try {
			view = (ViewImpl) getViewManagement().find(id);
		} catch (SQLException e) { /* ignore */
		}
		return view;
	}

	public final boolean doesViewExist(String viewName, Cube forCube) {
		return getViewByName(viewName, forCube) != null;
	}
		
	//view names must be unique per cube 
	public final View getViewByName(String name, Cube cube) {
		AccessController.checkPermission(Right.READ, user);
		try {
			// do we have an account for it:
			Account account = getAccount(cube);
			return (View) getViewManagement().findByName(name, cube, account);
		} catch (SQLException e) { /* ignore */
		}
		return null;
	}

	//VIEW-ROLE ASSOCIATION
	public final void add(Role role, View toView) throws OperationFailedException {		
		if (!toView.hasRole(role)) {
			AccessController.checkPermission(Right.WRITE, toView, user);			
			ViewImpl view = (ViewImpl) toView;
			view.add(role);
			try {
				getViewManagement().update(view);
			} catch (SQLException e) {
				//recover and throw exception
				view.remove(role);
				throw new OperationFailedException("Failed to modify view", e);
			}
		}
	}

	public final void remove(Role role, View fromView)
			throws OperationFailedException {		
		if (fromView.hasRole(role)) {
			AccessController.checkPermission(Right.WRITE, fromView, user);
			// remove it:
			ViewImpl view = (ViewImpl) fromView;
			view.remove(role);
			try {
				getViewManagement().update(view);
			} catch (SQLException e) {
				//recover and throw exception
				view.add(role);
				throw new OperationFailedException("Failed to modify view", e);
			}
		}
	}

	public final void setCube(String id, View ofView) {
		AccessController.checkPermission(Right.WRITE, ofView, user);
		ViewImpl view = (ViewImpl)ofView;
		view.setCube(id);
	}

	public final void setDatabase(String id, View ofView) {
		AccessController.checkPermission(Right.WRITE, ofView, user);
		ViewImpl view = (ViewImpl)ofView;
		view.setDatabase(id);
	}

	public final void setDefinition(String xml, View ofView) {
		AccessController.checkPermission(Right.WRITE, ofView, user);
		ViewImpl view = (ViewImpl)ofView;
		view.setDefinition(xml);
	}

	public final void setName(String name, View ofView) {
		AccessController.checkPermission(Right.WRITE, ofView, user);
		ViewImpl view = (ViewImpl)ofView;
		view.setName(name);
	}

	public final void setOwner(User owner, View ofView) {
		AccessController.checkPermission(Right.WRITE, ofView, user);
		((ViewImpl) ofView).setOwner(owner);
	}

	public final void setAccount(Account acc, View ofView) {
		((ViewImpl) ofView).setAccount(acc);
	}
		
	private final Account getAccount(Cube cube) throws SQLException {		
		Connection paloCon = cube.getDatabase().getConnection();		
		PaloConnection connection = getConnectionManagement().findBy(
				paloCon.getServer(), paloCon.getService());
		return getAccount(connection, paloCon.getUsername());
	}
	
	private final Account getAccount(PaloConnection forConnection, String login)
			throws SQLException {
		Account account = getAccountManagement().findBy(login, forConnection);
		if (account == null)
			throw new NoAccountException(user, forConnection, "User '"
					+ user.getLastname() + "' has no account on '"
					+ forConnection.getHost() + "'");
		return account;
	}


	public View createViewAsSubobject(String name, Cube cube, AuthUser authUser,
			String sessionId, String externalId)
			throws OperationFailedException {
		AccessController.checkPermission(Right.CREATE, user);
		try {
			// do we have an account for it:
			Account forAccount = getAccount(cube);
			ViewImpl view = new ViewImpl(null);
			view.setName(name);
			view.setOwner(user);
			// use account			
			try {
				view.setAccount(authUser, forAccount, sessionId);
			} catch (PaloIOException e) {
				throw new OperationFailedException(e.getMessage(), e);
			}
			view.setCube(cube.getId());
			view.setDatabase(cube.getDatabase().getId());
			// user should get owner role:
			Role ownerRole = (Role) getRoleManagement().findByName(Role.OWNER);
			if (ownerRole != null) {
				user.add(ownerRole);
			}
		
			// use cube view:
			CubeView cubeView = CubeViewFactory.createView(view, cube, authUser, externalId);
			view.setDefinition(CubeViewIO.toXML(cubeView));
			return view;
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to create view", e);
		}
	}
}
