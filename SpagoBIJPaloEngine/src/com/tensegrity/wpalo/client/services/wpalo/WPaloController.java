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
package com.tensegrity.wpalo.client.services.wpalo;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>WPaloController</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: WPaloController.java,v 1.13 2010/02/12 13:49:50 PhilippBouillon Exp $
 **/
public class WPaloController extends Controller {
//the main app controller of wpalo.
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	public WPaloController() {
		// we are interested in following events:
		registerEventTypes(WPaloEvent.APP_START, WPaloEvent.APP_STOP);
	}

	public void handleEvent(AppEvent<?> event) {
		switch (event.type) {
		case WPaloEvent.APP_START:
			start();
			break;
		case WPaloEvent.APP_STOP:
			//String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
			stop("");
			break;
		}
	}
	
	private final void start() {
		WPaloControllerServiceProvider.getInstance().start(
				new Callback<Void>(constants.applicationStartFailed())
						 {
					public void onFailure(Throwable t) {
						// go on and login:
						Dispatcher.forwardEvent(WPaloEvent.LOGIN);						
					}
					
					public void onSuccess(Void v) {
						// go on and login:
						Dispatcher.forwardEvent(WPaloEvent.LOGIN);
					}
				});
	}
	private final void stop(String sessionId) {
		WPaloControllerServiceProvider.getInstance().stop(sessionId, new Callback<Void>() {
			public void onFailure(Throwable t) {
				Dispatcher.forwardEvent(WPaloEvent.APP_START);
			}
			public void onSuccess(Void v) {
				Dispatcher.forwardEvent(WPaloEvent.APP_START);
			}
		});
	}
}
