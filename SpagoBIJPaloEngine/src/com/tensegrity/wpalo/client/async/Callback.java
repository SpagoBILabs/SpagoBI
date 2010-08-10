/*
*
* @file Callback.java
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
* @version $Id: Callback.java,v 1.15 2010/03/02 08:59:12 PhilippBouillon Exp $
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
