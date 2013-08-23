/*
*
* @file ViewConverter.java
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
* @version $Id: ViewConverter.java,v 1.9 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.converter.cubeviews;

import java.util.ArrayList;
import java.util.List;

import org.palo.api.Connection;
import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Role;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.services.ServiceProvider;
import org.palo.viewapi.services.ViewService;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.server.converter.BaseConverter;

/**
 * <code>ViewConverter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewConverter.java,v 1.9 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public class ViewConverter extends BaseConverter {

	protected Class<?> getNativeClass() {
		return View.class;
	}

	protected Class<?> getXObjectClass() {
		return XView.class;
	}

	public Object toNative(XObject obj, AuthUser loggedInUser)
			throws OperationFailedException {
		XView xView = (XView)obj;
		ViewService viewService = ServiceProvider.getViewService(loggedInUser);
		View view = viewService.getView(xView.getId());
		if(view == null)
			view = create(xView, loggedInUser);
		update(view, xView, viewService);
		return view;
	}
	
	public XObject toXObject(Object nativeObj) {
		View view = (View) nativeObj;
		XView xView = new XView(view.getId(), view.getName());
		xView.setDefinition(view.getDefinition());
		xView.setAccountId(view.getAccount().getId());
		xView.setCubeId(view.getCubeId());
		xView.setDatabaseId(view.getDatabaseId());
		xView.setOwnerId(view.getOwner().getId());
		List <String> ids = new ArrayList<String>();
		List <String> names = new ArrayList<String>();
		for (Role r: view.getRoles()) {
			ids.add(r.getId());
			names.add(r.getName());
		}
		xView.setRoleIds(ids);
		xView.setRoleNames(names);
		//TODO set owner and parameters
		
		return xView;
	}
	private final View create(XView xView, AuthUser user)
			throws OperationFailedException {
		ViewService viewService = ServiceProvider.getViewService(user);
		Cube cube = getCubeOf(xView, user);
		return viewService.createView(xView.getName(), cube, user, "", xView.getExternalId());
	}
	
	private final void update(View view, XView xView, ViewService viewService) {
		viewService.setDefinition(xView.getDefinition(), view);
		viewService.setName(xView.getName(), view);
		//TODO update owner...
//		viewService.setOwner(xView.get, view)
		//TODO set parameters...
	}
	
	private final Cube getCubeOf(XView xView, AuthUser user) {
		Connection connection = getConnection(xView, user);
		Database database = connection.getDatabaseById(xView.getDatabaseId());
		return null; //database.getCubeById(xView.getCube().getId());		
	}
	private final Connection getConnection(XView xView, AuthUser user) {
		
		Account account = getAccountById(xView.getAccountId(), user);
		return getConnection(account);
	}
}
