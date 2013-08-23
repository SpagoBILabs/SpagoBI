/*
*
* @file PaloException.java
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
* @version $Id: PaloException.java,v 1.9 2010/03/02 08:58:28 PhilippBouillon Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 */
package com.tensegrity.palojava;

/**
 * <code>PaloException</code> is a runtime exception which signals that a
 * communication exception between a palo client and palo server has occurred.
 *
 * @author Stepan Rutz
 * @version $Id: PaloException.java,v 1.9 2010/03/02 08:58:28 PhilippBouillon Exp $
 */
public class PaloException extends RuntimeException {
	static final long serialVersionUID = 1;

	//optional error fields:
	private String errorCode;
	private String errorMsg;
	private String errorReason;

//	/**
//	 * Default constructor which just calls its superclass
//	 */
//	public PaloException() {
//		super();
//	}
//	
	/**
     * Constructs an instance of <code>PaloException</code> with the 
     * specified detailed message which describes this particular exception. 
     * @param msg a detailed message
     */
	public PaloException(String msg) {
		super(msg);
	}

	/**
     * Constructs an instance of <code>PaloException</code> with the 
     * specified <code>Exception</cause> as the cause. 
     * @param cause the cause
     */
	public PaloException(Exception cause) {
		super(cause);
	}

	/**
     * Constructs an instance of <code>PaloException</code> with the 
     * specified detailed message and a cause.
     * @param msg a detailed message 
     * @param cause the cause
     */
	public PaloException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	/**
     * Constructs a new instance of <code>PaloException</code> with the 
     * specified error code, error message and reason. 
     * @param errorCode an optional error code. Specifying <code>null</code> is legal.
     * @param errorMsg an optional error message. Specifying <code>null</code> is legal.
     * @param errorReason an optional error reason. Specifying <code>null</code> is legal.
     */
	public PaloException(String errorCode, String errorMsg, String errorReason) {
		this(errorCode, errorMsg, errorReason, null);
	}

	/**
     * Constructs a new instance of <code>PaloException</code> with the 
     * specified cause and detailed error code, error message and reason.
     * @param errorCode an optional error code. Specifying <code>null</code> is legal.
     * @param errorMsg an optional error message. Specifying <code>null</code> is legal.
     * @param errorReason an optional error reason. Specifying <code>null</code> is legal.
     * @param cause the cause
     */
	public PaloException(String errorCode, String errorMsg, String errorReason, Throwable cause) {
		super(stripErrorNumber(errorMsg) + 
			  (errorReason == null ? "" : " [" + errorReason + "]") +
			  " (Palo Error " + errorCode + ")", cause);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		this.errorReason = errorReason;
	}

	private final static String stripErrorNumber(String txt) {
		if (txt == null) {
			return txt;
		}
		StringBuffer result = new StringBuffer();
		int index = 0;
		txt = txt.trim();
		for (char c: txt.toCharArray()) {
			if (Character.isDigit(c)) {
				index++;
				continue;
			}
			break;
		}
		if (index < txt.length()) {
			result.append(txt.substring(index).trim());
		}
		return result.toString();
	}
	
	/**
	 * Returns an optional error code or <code>null</code> if none was defined
	 * @return error code or <code>null</code>
	 */
	public final String getErrorCode() {
		return errorCode;
	}
	
	/**
	 * Returns an optional error description. If none was defined then calling 
	 * this method is equal to calling {@link #getMessage()}
	 * @return an additional error description
	 */
	public final String getDescription() {
		if(errorMsg == null)
			errorMsg = getMessage();
		return errorMsg;
	}
	
	/**
	 * Returns an optional description of the error cause. If none was defined
	 * then calling this method is equal to calling {@link #getDescription()}
	 * @return an addtional description of the error reason
	 */
	public final String getReason() {
		if(errorReason == null)
			errorReason = getDescription();
		return errorReason;
	}

}
