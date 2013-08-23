/*
*
* @file ConnectionConverter.java
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
* @version $Id: ConnectionConverter.java,v 1.3 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.converter.admin;

import org.palo.viewapi.AuthUser;
import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.services.AdministrationService;
import org.palo.viewapi.services.ServiceProvider;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
import com.tensegrity.palo.gwt.core.server.converter.BaseConverter;

/**
 * <code>ConnectionConverter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ConnectionConverter.java,v 1.3 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public class ConnectionConverter extends BaseConverter {

	protected Class<?> getNativeClass() {
		return PaloConnection.class;
	}

	protected Class<?> getXObjectClass() {
		return XConnection.class;
	}

	public Object toNative(XObject obj, AuthUser loggedInUser) throws OperationFailedException {
		XConnection xConnection = (XConnection) obj;
		AdministrationService adminService = 
			ServiceProvider.getAdministrationService(loggedInUser);
		PaloConnection connection = adminService.getConnection(xConnection.getId());
		if(connection == null)
			connection = createConnection(xConnection, adminService);
		update(connection, xConnection, adminService);				
		return connection;
	}

	public XObject toXObject(Object nativeObj) {
		PaloConnection connection = (PaloConnection) nativeObj;
		XConnection xConnection = new XConnection(connection.getId(),
				connection.getName(), connection.getType());
		xConnection.setDescription(connection.getDescription());
		xConnection.setHost(connection.getHost());
		xConnection.setService(connection.getService());
		return xConnection;
	}
	
	private final PaloConnection createConnection(XConnection xConnection,
			AdministrationService adminService) throws OperationFailedException {
		return adminService.createConnection(xConnection.getName(), 
				xConnection.getHost(), xConnection.getService(), 
				xConnection.getConnectionType());
	}
	
	private final void update(PaloConnection connection, XConnection xConnection, AdministrationService adminService) {
		adminService.setDescription(xConnection.getDescription(), connection);
		adminService.setHost(xConnection.getHost(), connection);
		adminService.setName(xConnection.getName(), connection);
		adminService.setService(xConnection.getService(), connection);
		adminService.setType(xConnection.getConnectionType(), connection);
	}
}
