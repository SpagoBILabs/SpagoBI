/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.async;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;


public abstract class Callback<T> implements AsyncCallback<T> {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private final String errorMessage;
	
	public Callback() {
		this(null);
	}
	public Callback(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public void onFailure(Throwable caught) {
		hideWaitCursor();
		if(handled(caught) || handled(caught.getCause()))
			return;
		String msg = errorMessage != null ? errorMessage : caught.getMessage();
		MessageBox.alert(constants.error(), msg, null);
	}
	
	protected final void hideWaitCursor() {
		((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
	}

	protected boolean handled(Throwable cause) {
		if (cause != null) {
			if (cause instanceof SessionExpiredException) {
				handle(cause);
				return true;
			}
		}
		return false;
	}
	
	public static final void handle(Throwable ex) {
		Listener<WindowEvent> callback = new Listener<WindowEvent>() {
			public void handleEvent(WindowEvent we) {
				Dispatcher.forwardEvent(WPaloEvent.APP_STOP);
			}
		};		
		String locMessage = ex.getLocalizedMessage();
		if (locMessage != null && locMessage.toLowerCase().indexOf("session expired") != -1) {
			locMessage = constants.sessionExpired();
		}		
		MessageBox.info(constants.sessionExpired(), locMessage +"<br/>" +
				constants.loginAgain(), callback);
	}
}
