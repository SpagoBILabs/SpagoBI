/*
*
* @file WorkbenchController.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: WorkbenchController.java,v 1.15 2010/04/12 11:13:36 PhilippBouillon Exp $
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
