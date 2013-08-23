/*
*
* @file PaloPersistenceException.java
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
* @author ArndHouben
*
* @version $Id: PaloPersistenceException.java,v 1.5 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.persistence;


/**
 * <code>PaloPersistenceException</code> defines an exception which is thrown 
 * in case of problems during loading or saving of palo <code>API</code> 
 * persistence objects like <code>CubeView</code>s or <code>Subset</code>s. 
 * <code>{@link PersistenceError}</code>s are used to provide additional 
 * information. Please use {@link #getErrors()} to travers the list of nested
 * errors.
 *
 * @see PersistenceError
 * 
 * @author ArndHouben
 * @version $Id: PaloPersistenceException.java,v 1.5 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
public class PaloPersistenceException extends Exception {
	
	public static final int TYPE_UNDEFINED = -1;
	public static final int TYPE_LOAD_FAILED = 0;
	public static final int TYPE_LOAD_INCOMPLETE = 1;
	public static final int TYPE_SAVE_FAILED = 2;
	public static final int TYPE_SAVE_INCOMPLETE = 4;
	
	private static final long serialVersionUID = 20070510L;
		
	private final PersistenceError[] errors;
	
	private int type;

	/**
	 * Default constructor
	 * @param errors provide additional information about this exception
	 */
	public PaloPersistenceException(PersistenceError[] errors) {
		this(errors,"");
	}
	
	/**
	 * 
	 * @param errors provide additional information about this exception
	 * @param msg a descriptive error message
	 */
	public PaloPersistenceException(PersistenceError[] errors, String msg) {
		super(msg);
		this.errors = errors;
		boolean failed = false;
		for (PersistenceError error: errors) {
			if (error.getType() == PersistenceError.LOADING_FAILED) {
				failed = true;
				break;
			}
		}
		if (failed) {
			type = TYPE_LOAD_FAILED;
		} else {
			type = TYPE_LOAD_INCOMPLETE;
		}
	}
	
	/**
	 * Returns all registered errors or <code>null</code>
	 * @return registered errors or <code>null</code>
	 */
	public PersistenceError[] getErrors() {
		return errors;
	}
	
	public final void setType(int type) {
		this.type = type;
	}
	
	public final int getType() {
		return type;
	}
}
