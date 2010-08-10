/*
*
* @file ViewModeWorkbenchController.java
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
* @version $Id: ViewModeWorkbenchController.java,v 1.7 2009/12/17 16:14:20 PhilippBouillon Exp $
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
