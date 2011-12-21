/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.exceptions;


/**
 * <code>WPaloException</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: WPaloException.java,v 1.3 2009/12/17 16:14:21 PhilippBouillon Exp $
 **/
public class WPaloException extends Exception {

	/** generated serial number */
	private static final long serialVersionUID = -1863198165072189927L;

	public WPaloException() {
		super();
	}

	public WPaloException(String msg, Throwable cause) {
		super(msg);
		initCause(cause);
	}


}
