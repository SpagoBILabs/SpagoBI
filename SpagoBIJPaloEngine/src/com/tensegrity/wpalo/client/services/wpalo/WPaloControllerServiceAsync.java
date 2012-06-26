/*
*
* @file WPaloControllerServiceAsync.java
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
* @version $Id: WPaloControllerServiceAsync.java,v 1.6 2010/02/16 13:53:42 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.services.wpalo;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;

/**
 * <code>WPaloControllerServiceAsync</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: WPaloControllerServiceAsync.java,v 1.6 2010/02/16 13:53:42 PhilippBouillon Exp $
 **/
public interface WPaloControllerServiceAsync {

	public void stop(String sessionId, AsyncCallback<Void> callback);
	public void start(AsyncCallback<Void> callback);
	public void login(String login, String password, String locale, AsyncCallback<XUser> callback);
	public void loginHash(String login, String password, String locale, AsyncCallback<XUser> callback);
	public void logout(String sessionId, AsyncCallback<Void> callback);
}
