/*
*
* @file OperationFailedException.java
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
* @version $Id: OperationFailedException.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.exceptions;

/**
 * <code>OperationFailedException</code>
 * <p>
 * Indicates an exception during the execution of an operation.
 * In most cases the cause for this exception lies in a failed access or 
 * modification of the corresponding tables in the underlying database. 
 * To check for this use the {@link #getCause()} method.
 * </p>
 *
 * @version $Id: OperationFailedException.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public class OperationFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6146742614846266279L;
	
	
	public OperationFailedException(String msg) {
		super(msg);
	}
	
	public OperationFailedException(String msg, Throwable cause) {
		super(msg, cause);
	}

	//TODO (how to) provide more information of what went wrong (if possible)
	/*
		maybe constants which indicate the failed operation, e.g. delete, save, create...
	 public static final int UNKNOWN = 0;	 
	 public static final int READ_FAIL = 1;
	 public static final int CREATE_FAIL = 2;
	 public static final int UPDATE_FAIL = 4;	 
	 public static final int DELETE_FAIL = 8;
	 
	 => public OperationFailedException(int reason) { ... }
	 	public int getReason() { ... }
	 */
}
