/*
*
* @file PaloObjectNotFoundException.java
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
* @version $Id: PaloObjectNotFoundException.java,v 1.3 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.exceptions;

import org.palo.api.Dimension;
import org.palo.api.PaloAPIException;

/**
 * <code>PaloObjectNotFoundException</code>
 * <p>
 * Indicates that a required palo object like e.g. a <code>{@link Dimension}</code>
 * could not be found or does not exists on palo server.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: PaloObjectNotFoundException.java,v 1.3 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
public class PaloObjectNotFoundException extends PaloAPIException {

	private static final long serialVersionUID = -7046454439997280322L;
	
	public PaloObjectNotFoundException(String msg) {
		super(msg);
	}

	
	public PaloObjectNotFoundException(String msg, Throwable cause) {
		super(msg,cause);
	}

//	TODO any own exception which does not provide more information like the 
//	standard exceptions is a bit useless => provide more information ;)
// SOME ADDITIONS MAYBE:
//	ID, 
//	PaloObject, e.g. database, cube, hierarchy...
	

}
