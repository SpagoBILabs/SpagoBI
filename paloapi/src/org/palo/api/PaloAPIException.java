/*
*
* @file PaloAPIException.java
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
* @author Stepan Rutz
*
* @version $Id: PaloAPIException.java,v 1.9 2010/03/01 09:57:44 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2005. All rights reserved.
 */
package org.palo.api;

import com.tensegrity.palojava.PaloException;


/**
 * <code>PaloException</code>
 * 
 * <p>
 * All operations of the PALO-API in this package potentially
 * throw this exception or other runtime exceptions.
 * </p>
 * 
 * <p>
 * It is best practise to anticipated these exceptions where
 * appropriate. Since checked-exceptions don't scale well and
 * force the clients to write a lot of catch blocks, the
 * <code>PaloAPIException</code> is unchecked.
 * </p>
 *
 * @author Stepan Rutz
 * @version $ID$
 */
public class PaloAPIException extends RuntimeException
{
    static final long serialVersionUID = 1;
    
    
    /**
     * An optional data field.
     */
    private Object data;
    
    /** an optional error code */
    private String errorCode;
    /** an optional error message */
    private String errorMsg; 
    /** an optional error reason */
    private String errorReason;
    
//    /**
//     * Constructs a new <code>PaloAPIException</code>
//     */
//    public PaloAPIException()
//    {
//        super();
//    }
//    
    /**
     * Constructs a new <code>PaloAPIException</code>
     * @param msg the message for the exception.
     */
    public PaloAPIException(String msg) {
    	super(msg);
    }
    
    /**
     * Constructs a new <code>PaloAPIException</code>
     * @param msg the message for the exception.
     * @param cause the nested exception.
     */
    public PaloAPIException(String msg, Throwable cause) {
		super(msg, cause);
		if (cause instanceof PaloException) {
			PaloException ex = (PaloException) cause;
			errorCode = ex.getErrorCode();
			errorMsg = ex.getDescription();
			errorReason = ex.getReason();
		}
	}

    public PaloAPIException(Throwable cause) {
//    	super(cause);
    	super(cause.getMessage() == null ? "PaloException" : cause.getMessage(), cause);
		if (cause instanceof PaloException) {
			PaloException ex = (PaloException) cause;
			errorCode = ex.getErrorCode();
			errorMsg = ex.getDescription();
			errorReason = ex.getReason();
		}
	}

	/**
	 * Returns an optional error code or <code>null</code> if none was defined
	 * @return error code or <code>null</code>
	 */
    public final String getErrorCode() {
    	return errorCode;
    }
    
	/**
	 * Returns an optional error description. If no description was defined 
	 * then calling this method has the same effect as calling 
	 * {@link Exception#getMessage()}
	 * @return an optional error description
	 */
    public final String getDescription() {
    	return errorMsg != null ? errorMsg : getMessage();
    }
    
	/**
	 * Returns an optional description of the error cause. If no reason was
	 * defined then calling this method has same effect as calling
	 * {@link #getDescription()} 
	 * @return an optional error reason
	 */
    public final String getReason() {
    	return errorReason != null ? errorReason : getDescription();
    }
    
    /**
     * Sets an optional data object. The type of the data object and if it is 
     * set at all depends on the context in which this exception is thrown.
     * @param data an optional data object or <code>null</code>
     */
    public final void setData(Object data) {
    	this.data = data;
    }
    
    /**
     * Returns the optional set data object or <code>null</code> if none was 
     * set.
     * @return the data object or <code>null</code>
     */
    public final Object getData() {
    	return data;
    }
}
