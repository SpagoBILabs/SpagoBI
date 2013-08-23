/*
*
* @file NoAccountException.java
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
* @version $Id: NoAccountException.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.exceptions;

import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.User;

/**
 * <code>NoAccountException</code>
 * <p>
 * Indicates that a required {@link Account} does not exist for a certain
 * {@link PaloConnection} and {@link User}.
 * </p>
 *
 * @version $Id: NoAccountException.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public class NoAccountException extends RuntimeException {

	/** */
	private static final long serialVersionUID = 827797655476450856L;

	//additional information:
	private final User user;
	private final PaloConnection connection;
	
	public NoAccountException(User user, PaloConnection connection) {
		this(user, connection, null);
	}

	public NoAccountException(User user, PaloConnection connection, String msg) {
		super(msg);
		this.user = user;
		this.connection = connection;
	}

//	public NoAccountException(User user, PaloConnection connection, String msg, Throwable cause) {
//		super(msg, cause);
//		this.user = user;
//		this.connection = connection;
//	}

	/**
	 * Returns the {@link User} instance which has no account for the 
	 * connection given by {@link #getConnection()}.
	 * @return the user instance 
	 */
	public final User getUser() {
		return user;
	}
	
	/**
	 * Returns the {@link PaloConnection} for which the user, given by 
	 * {@link #getUser()}, has no account.
	 * @return the connection for which the account is missing
	 */
	public final PaloConnection getConnection() {
		return connection;
	}
}
