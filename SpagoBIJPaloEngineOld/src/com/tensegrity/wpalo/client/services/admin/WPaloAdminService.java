/*
*
* @file WPaloAdminService.java
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
* @version $Id: WPaloAdminService.java,v 1.15 2010/01/13 08:02:41 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.services.admin;

import com.google.gwt.user.client.rpc.RemoteService;
import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
import com.tensegrity.palo.gwt.core.client.models.admin.XGroup;
import com.tensegrity.palo.gwt.core.client.models.admin.XRole;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.palo.XCube;
import com.tensegrity.palo.gwt.core.client.models.palo.XDatabase;
import com.tensegrity.wpalo.client.exceptions.DbOperationFailedException;

/**
 * <code>WPaloAdminService</code> TODO DOCUMENT ME
 * 
 * @version $Id: WPaloAdminService.java,v 1.8 2009/06/16 13:46:08 ArndHouben Exp
 *          $
 **/
public interface WPaloAdminService extends RemoteService {

	public XUser[] getUsers(String sessionId) throws SessionExpiredException;

	public XGroup[] getGroups(String sessionId, XUser user) throws SessionExpiredException;

	public XGroup[] getGroups(String sessionId) throws SessionExpiredException;
	
	public XRole[] getRoles(String sessionId, XUser user) throws SessionExpiredException;

	public XAccount[] getAccounts(String sessionId, XUser user) throws SessionExpiredException;
	public XAccount[] listAccounts(String sessionId, XUser user) throws SessionExpiredException;

	public XConnection[] getConnections(String sessionId, XUser user)
			throws SessionExpiredException;

	public void delete(String sessionId, XObject xOb) throws DbOperationFailedException,
			SessionExpiredException;
	
	public String [] mayDelete(String sessionId, XObject xObj) throws SessionExpiredException;

	public XObject saveXObject(String sessionId, XObject xObj) throws DbOperationFailedException,
			SessionExpiredException;
	
	public Boolean [] hasRoles(String sessionId, XUser user, String [] roles) throws SessionExpiredException;
	public Boolean hasAccount(String sessionId, XConnection con) throws SessionExpiredException;
	
	public XUser [] getUsersForConnection(String sessionId, String viewId) throws SessionExpiredException;
	
	public XConnection getConnection(String sessionId, XView view) throws SessionExpiredException;
	public XDatabase [] getDatabases(String sessionId, XConnection con) throws SessionExpiredException, DbOperationFailedException;
	public XCube [] getCubes(String sessionId, XConnection con, XDatabase db) throws SessionExpiredException;
}
