/*
*
* @file AuthenticationFailedException.java
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
* @version $Id: AuthenticationFailedException.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.exceptions;

/**
 * <code>AuthenticationFailedException</code>
 * This exception is thrown whenever a user tries to log on with wrong
 * credentials or an unknown user tries to log on to the system.
 *
 * @version $Id: AuthenticationFailedException.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public class AuthenticationFailedException extends Exception {		
	private static final long serialVersionUID = 200805291800L;

	/**
	 * Creates a new AuthenticationFailedException with the given message.
	 * 
	 * @param msg the message for the Exception.
	 */
	public AuthenticationFailedException(String msg) {
		super(msg);
	}
	
	/**
	 * Creates a new AuthenticationFailedException with the given message and
	 * a given cause.
	 *  
	 * @param msg the message for the Exception.
	 * @param cause the cause of the Exception.
	 */
	public AuthenticationFailedException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
