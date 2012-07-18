/*
*
* @file WPaloController.java
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
* @version $Id: WPaloController.java,v 1.13 2010/02/12 13:49:50 PhilippBouillon Exp $
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
