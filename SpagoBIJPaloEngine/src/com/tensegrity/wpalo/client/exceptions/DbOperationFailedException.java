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
 * <code>DbOperationFailedException</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: DbOperationFailedException.java,v 1.4 2009/12/17 16:14:21 PhilippBouillon Exp $
 **/
public class DbOperationFailedException extends Exception {

	/** generated */
	private static final long serialVersionUID = 7196249016042575859L;


	public DbOperationFailedException() {
	}

	public DbOperationFailedException(String msg) {
		super(msg);
	}

	public DbOperationFailedException(String msg, Throwable cause) {
		super(msg);
		initCause(cause);
	}

	//TODO provide more information about failed operation!!!
	//maybe introduce some constants?
}
