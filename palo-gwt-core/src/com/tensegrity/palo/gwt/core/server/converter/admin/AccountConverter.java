/*
*
* @file AccountConverter.java
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
* @version $Id: AccountConverter.java,v 1.4 2010/02/16 13:54:00 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.converter.admin;

import org.palo.viewapi.Account;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.server.converter.BaseConverter;
import com.tensegrity.palo.gwt.core.server.converter.XConverter;

/**
 * <code>AccountConverter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AccountConverter.java,v 1.4 2010/02/16 13:54:00 PhilippBouillon Exp $
 **/
public class AccountConverter extends BaseConverter {

	protected Class<?> getNativeClass() {
		return Account.class;
	}

	protected Class<?> getXObjectClass() {
		return XAccount.class;
	}

//	public Object toNative(XObject xObj, AuthUser loggedInUser) throws OperationFailedException {
//		XAccount xAccount = (XAccount) xObj;
//		AdministrationService adminService = 
//			ServiceProvider.getAdministrationService(loggedInUser);
//		Account account = adminService.getAccountById(xAccount.getId());
//		if(account == null)
//			account = create(xAccount, loggedInUser);
//		update(account, xAccount, loggedInUser);
//		return account;
//	}

	public XObject toXObject(Object nativeObj) {
		Account account = (Account) nativeObj;
		XAccount xAccount = new XAccount(account.getId(), account.getLoginName());
		xAccount.setPassword(account.getPassword());
		xAccount.setUser((XUser)XConverter.createX(account.getUser()));
		xAccount.setConnection((XConnection)XConverter.createX(account.getConnection()));
		return xAccount;
	}
	
//	private final Account create(XAccount xAccount, AuthUser loggedInUser) throws OperationFailedException {
//		User user = 
//				(User) XConverter.getNative(xAccount.getUser(), loggedInUser);
//		PaloConnection connection = (PaloConnection) XConverter.getNative(
//				xAccount.getConnection(), loggedInUser);
//		AdministrationService adminService = 
//				ServiceProvider.getAdministrationService(loggedInUser);
//		return adminService.createAccount(xAccount.getLogin(), xAccount
//				.getPassword(), user, connection);
//	}
//	private final void update(Account account, XAccount xAccount, AuthUser loggedInUser) throws OperationFailedException {
//		AdministrationService adminService = 
//			ServiceProvider.getAdministrationService(loggedInUser);
//		adminService.setConnection((PaloConnection)XConverter.getNative(xAccount.getConnection(), loggedInUser), account);
//		adminService.setLoginName(xAccount.getLogin(), account);
//		adminService.setPassword(xAccount.getPassword(), account);
//		adminService.setUser((User)XConverter.getNative(xAccount.getUser(), loggedInUser), account);
//	}
}
