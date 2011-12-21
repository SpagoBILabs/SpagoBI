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
package com.tensegrity.wpalo.client.ui.mvc.reports;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.ui.editor.IEditor;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>AdminController</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ReportController.java,v 1.11 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class ReportController extends Controller implements WPaloEvent {

	//for testing purpose:
	private TemplateViewEditor viewEditor;
	private ReportNavigatorView navigatorView;
	
	public ReportController() {
		// we are interested in following events:
		registerEventTypes(INIT, LOGOUT);
		// all admin events...
		registerEventTypes(EDIT_TEMPLATE_ITEM, EDIT_TEMPLATE_VIEW,
				EXPANDED_REPORT_SECTION, UPDATE_WORKBOOKS, SHOW_TEMPLATE_VIEW);
	}
	
	public void handleEvent(AppEvent<?> event) {
		IEditor editor;
		Workbench wb = (Workbench) Registry.get(Workbench.ID);
		
		switch(event.type) {
		case INIT:
			forwardToView(navigatorView, event);
			break;
		case EXPANDED_REPORT_SECTION:
			//open cube editor:
//			viewEditor.setInput(event.data);
			wb.open(viewEditor);
			forwardToView(navigatorView, event);
			break;
		case UPDATE_WORKBOOKS:
			forwardToView(navigatorView, event);
			break;
		case EDIT_TEMPLATE_ITEM:
		case EDIT_TEMPLATE_VIEW:
//			editor = new TemplateEditor();
//			editor.setInput(event.data);
//			wb.open(editor);
			viewEditor.setInput(event.data);
			break;
		case SHOW_TEMPLATE_VIEW:
			viewEditor.showView(event.data);
			break;
		case LOGOUT: initialized = false;
        	break;			
		}
	}

	@Override
	public final void initialize() {
		super.initialize();
		viewEditor = new TemplateViewEditor();
		navigatorView = new ReportNavigatorView(this);
	}
}
