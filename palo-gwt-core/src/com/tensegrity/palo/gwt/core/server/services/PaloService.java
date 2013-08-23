/*
*
* @file PaloService.java
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
* @version $Id: PaloService.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services;

import org.palo.viewapi.AuthUser;
import org.palo.viewapi.exceptions.AuthenticationFailedException;
import org.palo.viewapi.services.ServiceProvider;

import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.server.converter.XConverter;


/**
 * <code>PaloService</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: PaloService.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public class PaloService extends BasePaloServiceServlet {
	
	/** generated default serial number */
	private static final long serialVersionUID = -4443935391013313542L;
	

	public XUser login(String login, String password)
			throws AuthenticationFailedException {
		AuthUser user = authenticateUser(login, password);
		setLoggedInUser(user);
		return (XUser) XConverter.createX(user);
	}

	private final AuthUser authenticateUser(String login, String password)
			throws AuthenticationFailedException {
		try {
			return ServiceProvider.getAuthenticationService().authenticate(
					login, password);
		} catch (org.palo.viewapi.exceptions.AuthenticationFailedException e) {
			throw new AuthenticationFailedException(e.getMessage(), e);
		}
	}
}
