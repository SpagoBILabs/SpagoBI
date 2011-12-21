/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.cubeview;

/**
 * <code>ConfirmLoadDialogListener</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ConfirmLoadDialogListener.java,v 1.3 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public interface ConfirmLoadDialogListener {
	public void proceed(boolean state);
	public void cancel();
}
