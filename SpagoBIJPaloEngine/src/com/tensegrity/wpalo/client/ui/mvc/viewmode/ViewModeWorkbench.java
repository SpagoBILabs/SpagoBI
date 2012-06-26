/*
*
* @file ViewModeWorkbench.java
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
* @version $Id: ViewModeWorkbench.java,v 1.12 2009/12/17 16:14:20 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.viewmode;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.google.gwt.user.client.ui.RootPanel;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.wpalo.client.WPalo;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.ui.dialog.LoginDialog;
import com.tensegrity.wpalo.client.ui.editor.IEditor;
import com.tensegrity.wpalo.client.ui.widgets.Hyperlink;
import com.tensegrity.wpalo.client.ui.widgets.OnClickListener;
import com.tensegrity.wpalo.client.ui.window.AbstractTopLevelView;

/**
 * <code>Workbench</code>
 * <p>The workbench is the main view of the new wpalo implementation. It simply 
 * defines a view panel on the left side and an editor panel attached on the 
 * right.</p>  
 *
 * @version $Id: ViewModeWorkbench.java,v 1.12 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class ViewModeWorkbench extends AbstractTopLevelView {

	/** id to access a workbench instance via the global {@link Registry} */
	public static final String ID = "com.tensegrity.wpalo.viewmodeworkbench";

	private XUser user;	//the logged in user
			
	ViewModeWorkbench(Controller controller) {
		super(controller);
	}

	void setUser(XUser user) {
		this.user = user;
	}
	
	/** Returns the currently logged in user or <code>null</code> */
	public final XUser getUser() {
		return user;
	}
	
	public final void open(IEditor editor) {
		//currently we just open it in the editorpanel. 
		//future optimisations: 
		//	check if editor is already open
		//	introduce tabs to handle multiple open editors
		//	...
		editorpanel.setHeading(editor.getTitle());
		editorpanel.removeAll();
		editorpanel.add(editor.getPanel());
		editorpanel.layout();
	}
	
	public final void close(IEditor editor) {
		editorpanel.setHeading("");
		editorpanel.removeAll();
	}
	
	protected final void handleEvent(AppEvent<?> event) {
		switch(event.type) {
		case WPaloEvent.INIT_VIEW_MODE:
			initializeUI();
			Dispatcher.forwardEvent(WPaloEvent.OPEN_VIEW_MODE, user);
			break;
		case WPaloEvent.VIEW_MODE_LOGIN:
			login();
			break;
		}
	}

	private final void initializeUI() {
		viewport = new LayoutContainer();
		viewport.setLayout(new BorderLayout());

		createViewPanel();
		createViewPanelStatusLine(user.isAdmin() ? 2 : 1);
		fillViewPanelStatusLine();
		createEditorPanel(true);
		createEditorPanelStatusLine(false);
	
		//registry serves as a global context:
		Registry.register(ID, this);
		
		((WPalo) Registry.get(WPalo.ID)).show(viewport);
	}
		
	private final void fillViewPanelStatusLine() {		
		Hyperlink logout = new Hyperlink("Logout");
		logout.addListener(new OnClickListener() {
			public void clicked(ComponentEvent ce) {
				logout();
			}
		});
		viewpanelStatusLine.add(logout);
		
		if (user.isAdmin()) {
			Hyperlink viewMode = new Hyperlink("Admin Mode");
			viewMode.addListener(new OnClickListener(){
				public void clicked(ComponentEvent ce) {
					openAdminMode();
				}
			});
			viewpanelStatusLine.add(viewMode);
		}
	}
	
	private final void login() {
//		final LoginDialog dlg = new LoginDialog();
//		dlg.addListener(Events.Hide, new Listener<BoxComponentEvent>() {
//			public void handleEvent(BoxComponentEvent be) {	
//				RootPanel.get().remove(dlg);
//				user = dlg.getUser();
//				Dispatcher.forwardEvent(WPaloEvent.INIT_VIEW_MODE, user);
//			}
//		});
//		dlg.show();
	}	
	
	private final void logout() {
//		//remove all:
//		RootPanel.get().remove(viewport);
//		viewport.setVisible(false);
//		user = null;
//		Dispatcher.forwardEvent(WPaloEvent.VIEW_MODE_LOGOUT);
	}	
	
	private final void openAdminMode() {
		RootPanel.get().remove(viewport);
				
	}	
}
