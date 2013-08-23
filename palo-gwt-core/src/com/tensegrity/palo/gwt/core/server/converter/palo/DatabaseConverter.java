/*
*
* @file DatabaseConverter.java
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
* @version $Id: DatabaseConverter.java,v 1.7 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.converter.palo;

import org.palo.api.Connection;
import org.palo.api.Database;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.internal.ConnectionPoolManager;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.XPaloObject;
import com.tensegrity.palo.gwt.core.client.models.palo.XDatabase;
import com.tensegrity.palo.gwt.core.server.converter.PaloObjectConverter;

/**
 * <code>DatabaseConverter</code> TODO DOCUMENT ME
 * 
 * @version $Id: DatabaseConverter.java,v 1.2 2009/05/24 17:46:11 ArndHouben Exp
 *          $
 **/
public class DatabaseConverter extends PaloObjectConverter {

	protected Class<?> getNativeClass() {
		return Database.class;
	}

	protected Class<?> getXObjectClass() {
		return XDatabase.class;
	}

	public Object toNative(XObject obj, AuthUser loggedInUser)
			throws OperationFailedException {
		return toNative((XPaloObject)obj, loggedInUser);
	}

	public Object toNative(XPaloObject paloObject, AuthUser loggedInUser)
			throws OperationFailedException {
		Account account = null;
		try {
			XDatabase xDatabase = (XDatabase) paloObject;
			account = getAccountById(xDatabase.getAccountId(), loggedInUser);
			Connection connection = getConnection(account);
			return connection.getDatabaseById(xDatabase.getId());
		} finally {
			if (account != null) {
				ConnectionPoolManager.getInstance().disconnect(account, "", "DatabaseConverter.toNative");
			}
		}
	}

	public XObject toXObject(Object nativeObj) {
		return toXObject(nativeObj, null);
	}

	public XPaloObject toXObject(Object nativeObj, String accountId) {
		assert(accountId != null);
		Database database = (Database) nativeObj;
		XDatabase xDatabase = 
				new XDatabase(database.getId(), database.getName(), accountId);
		return xDatabase;
	}
	
}
