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
