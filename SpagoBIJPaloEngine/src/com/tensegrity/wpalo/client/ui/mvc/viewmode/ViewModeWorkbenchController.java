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
package com.tensegrity.wpalo.client.ui.mvc.viewmode;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.wpalo.client.WPaloEvent;

/**
 * <code>WorkbenchController</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewModeWorkbenchController.java,v 1.7 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class ViewModeWorkbenchController extends Controller {

	private ViewModeWorkbench workbench;
	
	public ViewModeWorkbenchController() {
		//we are interested in following events:
		registerEventTypes(WPaloEvent.LOGOUT,
						   WPaloEvent.INIT_VIEW_MODE,
				           WPaloEvent.VIEW_MODE_LOGIN,
				           WPaloEvent.VIEW_MODE_LOGOUT);		
	}

	public final void handleEvent(AppEvent<?> event) {
		switch (event.type) {
		case WPaloEvent.INIT_VIEW_MODE:
			XUser user = (XUser) event.data;
			workbench.setUser(user);
			onInit(event);
			break;
		case WPaloEvent.LOGIN:
			onLogin(event);
			break;
		case WPaloEvent.VIEW_MODE_LOGOUT:
			onLogout(event);
			break;
		case WPaloEvent.LOGOUT: initialized = false;
        	break;			
		}
	}

	public final void initialize() {
		workbench = new ViewModeWorkbench(this);
	}

	private final void onInit(AppEvent event) {
		forwardToView(workbench, event);
	}
	
	private final void onLogin(AppEvent event) {
		forwardToView(workbench, event);
	}
	
	private final void onLogout(AppEvent event) {
		forwardToView(workbench, event);
	}
}
