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
package com.tensegrity.wpalo.client.ui.mvc.workbench;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.LargeQueryWarningDialog;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.PrintDialog;
import com.tensegrity.wpalo.client.ui.mvc.viewbrowser.ViewBrowserController;

/**
 * <code>WorkbenchController</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: WorkbenchController.java,v 1.15 2010/04/12 11:13:36 PhilippBouillon Exp $
 **/
public class WorkbenchController extends Controller {

	private Workbench workbench;
	private ViewBrowserController viewBrowserController;
	
	public WorkbenchController() {
		//we are interested in following events:
		registerEventTypes(WPaloEvent.LOGOUT, WPaloEvent.INIT, WPaloEvent.LOGIN, 
				WPaloEvent.LOGOUT, WPaloEvent.VIEW_MODE_LOGOUT, WPaloEvent.LOGOUT_CLICKED);
	}
	
	public final void setViewBrowserController(ViewBrowserController vbc) {
		viewBrowserController = vbc;
	}	
	
	public final ViewBrowserController getViewBrowserController() {
		return viewBrowserController;
	}

	public final void handleEvent(AppEvent<?> event) {
		switch (event.type) {
		case WPaloEvent.INIT:
			onInit(event);
			break;
		case WPaloEvent.LOGIN:
			try {
				onLogin(event);
				LargeQueryWarningDialog.showWarning = true;
				PrintDialog.setDefaults();
			} catch (Throwable t) {
				t.printStackTrace();
			}
			break;
		case WPaloEvent.LOGOUT:
			onLogout(event);
			initialized = false;
			break;
		case WPaloEvent.LOGOUT_CLICKED:
			 try {
				 checkForLogout(event);
			 } catch (Throwable t) {
				 t.printStackTrace();
			 }
			 break;
		case WPaloEvent.VIEW_MODE_LOGOUT:
			forwardToView(workbench, event);				
			break;	
		}
	}

	public final void initialize() {
		workbench = new Workbench(this);
	}

	private final void onInit(AppEvent<?> event) {
		forwardToView(workbench, event);
	}
	
	private final void onLogin(AppEvent<?> event) {
		forwardToView(workbench, event);
	}
	
	private final void onLogout(AppEvent<?> event) {
		workbench.logout();
	}
	
	private final void checkForLogout(AppEvent <?> event) {
		workbench.checkForLogout();		
	}
}
