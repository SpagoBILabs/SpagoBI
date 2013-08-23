/*
*
* @file NoPermissionException.java
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
* @version $Id: NoPermissionException.java,v 1.5 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.exceptions;


import org.palo.viewapi.AuthUser;
import org.palo.viewapi.DomainObject;	
import org.palo.viewapi.Right;

/**
 * <code>NoPermissionException</code>
 * This Exception is thrown whenever a user tries to run an operation for which
 * he is lacking the right.
 *
 * @version $Id: NoPermissionException.java,v 1.5 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public class NoPermissionException extends RuntimeException {
	private static final long serialVersionUID = 4210538360106309250L;

	//additional data:
	/**
	 * The user trying to do something he is not allowed to.
	 */
	private final AuthUser user;

	/**
	 * The object on which the user tried to operate.
	 */
	private final DomainObject forObj;	
	
	/**
	 * The required right for this operation.
	 */
	private Right reqRight;
	
	/**
	 * Creates a new NoPermissionException with the given message, the failing
	 * object and the user provoking the exception.
	 * 
	 * @param msg the message of the exception.
	 * @param forObj the object which caused the exception to be thrown.
	 * @param user the user who tried to perform the operation.
	 */
	public NoPermissionException(String msg, DomainObject forObj, AuthUser user) {
		super(msg);
		this.forObj = forObj;
		this.user = user;
	}

	/**
	 * Creates a new NoPermissionException with the given message, the failing
	 * object, the user provoking the exception, and the required right for
	 * the operation in question.
	 * 
	 * @param msg the message of the exception.
	 * @param forObj the object which caused the exception to be thrown.
	 * @param user the user who tried to perform the operation.
	 * @param reqRight the required right for the operation.
	 */
	public NoPermissionException(String msg, DomainObject forObj, AuthUser user, Right reqRight) {
		super(msg);
		this.forObj = forObj;
		this.user = user;
		this.reqRight = reqRight;
	}

	/**
	 * Returns the user responsible for the exception or <code>null</code> if 
	 * none was specified.
	 * @return the user responsible for the exception or <code>null</code>.
	 */
	public final AuthUser getUser() {
		return user;	
	}
	
	/**
	 * Returns the object on which the operation was called that created the
	 * exception or <code>null</code> if none was specified.
	 * @return the failing object or <code>null</code>.
	 */
	public final DomainObject getGuardedObject() {
		return forObj;	
	}
	
	/**
	 * The required right for the executed operation or <code>null</code> if 
	 * the right has not been set.
	 * @return the required right for the operation or <code>null</code>.
	 */
	public final Right getRequiredRight() {
		return reqRight;
	}
	
	/**
	 * Sets the required right for the operation.
	 * @param right the required right for the operation.
	 */
	public final void setRequiredRight(Right right) {
		reqRight = right;
	}

}
