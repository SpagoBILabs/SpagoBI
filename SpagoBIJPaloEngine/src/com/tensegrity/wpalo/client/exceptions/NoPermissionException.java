/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.exceptions;

public class NoPermissionException extends Exception {
	
	/** generated */
	private static final long serialVersionUID = 5402547697010821282L;

	
	public NoPermissionException() {
	}

	public NoPermissionException(String msg) {
		super(msg);
	}

	public NoPermissionException(String msg, Throwable cause) {
		super(msg);
		initCause(cause);
	}
}
