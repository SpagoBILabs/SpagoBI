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

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * <code>AuthenticationFailedException</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AuthenticationFailedException.java,v 1.2 2009/12/17 16:14:21 PhilippBouillon Exp $
 **/
public class AuthenticationFailedException extends SerializableException {

	/** generated serial number */
	private static final long serialVersionUID = 2884703510864237846L;

	public AuthenticationFailedException() {
		super();
	}

	public AuthenticationFailedException(String msg, Throwable cause) {
		super(msg);
		initCause(cause);
	}
	
}
