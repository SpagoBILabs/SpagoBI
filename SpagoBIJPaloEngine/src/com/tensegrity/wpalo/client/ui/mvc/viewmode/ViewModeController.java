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

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.ui.editor.IEditor;

/**
 * <code>AccountController</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewModeController.java,v 1.5 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class ViewModeController extends Controller implements WPaloEvent {

	private ViewModeNavigatorView navigatorView;
	private ViewModeEditor editor;
	
	public ViewModeController() {
		// we are interested in following events:
		registerEventTypes(LOGOUT, OPEN_VIEW_MODE, EXPANDED_VIEW_REPORT_STRUCTURE_SECTION, 
				VIEW_REPORT_EVENT);
	}

	public void handleEvent(AppEvent<?> event) {
		switch(event.type) {
		case OPEN_VIEW_MODE:
			forwardToView(navigatorView, event);
			break;
		case EXPANDED_VIEW_REPORT_STRUCTURE_SECTION:
			forwardToView(navigatorView, event);
			break;
		case VIEW_REPORT_EVENT:
			IEditor editor = getEditor(event.type);
			editor.setInput(event.data);
			ViewModeWorkbench wb = (ViewModeWorkbench) 
				Registry.get(ViewModeWorkbench.ID);
			wb.open(editor);
			break;
		case LOGOUT: initialized = false;
        	break;			
		}
	}

	@Override
	public final void initialize() {
		super.initialize();
		navigatorView = new ViewModeNavigatorView(this);
		editor = new ViewModeEditor();
	}
	
	
	private final IEditor getEditor(int type) {
		if (type == VIEW_REPORT_EVENT)
			return editor;
		return null;
	}
}
