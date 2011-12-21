/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.services.wpalo;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.user.client.WindowCloseListener;
import com.tensegrity.wpalo.client.WPaloEvent;

class BrowserListener implements WindowCloseListener {

	BrowserListener() {
	}
	
	public void onWindowClosed() {
		Dispatcher.forwardEvent(WPaloEvent.APP_STOP);
	}

	public String onWindowClosing() {
		return null;
	}

}
