/*
*
* @file PaloIOException.java
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
* @version $Id: PaloIOException.java,v 1.3 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.exceptions;

/**
 * <code>PaloIoException</code>
 * Signals that an exception during the saving or loading of palo subsets
 * or views has occurred. 
 *
 * @author ArndHouben
 * @version $Id: PaloIOException.java,v 1.3 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
public class PaloIOException extends Exception {

	private static final long serialVersionUID = 3563657088381734321L;
	
	private Object data;
	
    /**
     * Constructs an {@code PaloIOException} with the specified 
     * message.
     * @param message a detailed exception message 
     */
    public PaloIOException(String message) {
		super(message);
	}

    /**
     * Constructs an {@code PaloIOException} with the specified 
     * message and cause.
     * @param message a detailed exception message 
     * @param cause the cause for this exception
     */
    public PaloIOException(String message, Throwable cause) {
		super(message, cause);
	}

    /**
     * Constructs an {@code PaloIOException} with the specified 
     * cause.
     * @param cause the cause for this exception
     */
    public PaloIOException(Throwable cause) {
		super(cause);
	}

    public final void setData(Object data) {
    	this.data = data;
    }
    
    public final Object getData() {
    	return data;
    }
    
}
