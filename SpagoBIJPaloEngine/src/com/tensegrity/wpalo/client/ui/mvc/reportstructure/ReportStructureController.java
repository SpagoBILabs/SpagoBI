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
package com.tensegrity.wpalo.client.ui.mvc.reportstructure;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.ui.editor.IEditor;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>ReportStructureController</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ReportStructureController.java,v 1.13 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class ReportStructureController extends Controller implements WPaloEvent {

	private ReportStructureNavigatorView navigatorView;
	private ReportStructureEditor editor;

	public ReportStructureController() {
		// we are interested in following events:
		registerEventTypes(INIT, LOGOUT);
		// all admin events...
		registerEventTypes(EDIT_REPORT_STRUCTURE,
				EXPANDED_REPORT_STRUCTURE_SECTION,
				SET_EDITOR_INPUT, UPDATE_WORKBOOKS);
	}
	
	public void handleEvent(AppEvent<?> event) {
		switch(event.type) {
		case INIT:
			forwardToView(navigatorView, event);
			break;
		case EXPANDED_REPORT_STRUCTURE_SECTION:
			forwardToView(navigatorView, event);
			break;
		case UPDATE_WORKBOOKS:
			IEditor ed = getEditor(WPaloEvent.EDIT_REPORT_STRUCTURE);
			if (ed != null) {
				((ReportStructureEditor) ed).reload((XObject) event.data);
			}
			break;
		case EDIT_REPORT_STRUCTURE:
			IEditor editor = getEditor(event.type);
			editor.setInput(event.data);
			Workbench wb = (Workbench) Registry.get(Workbench.ID);
			wb.open(editor);
			break;
		case SET_EDITOR_INPUT:
			IEditor edi = getEditor(WPaloEvent.EDIT_REPORT_STRUCTURE);
			edi.setInput(event.data);
			break;
		case LOGOUT: initialized = false;
        	break;			
		}
	}

	@Override
	public final void initialize() {
		super.initialize();
		navigatorView = new ReportStructureNavigatorView(this);
		editor = new ReportStructureEditor(navigatorView);
	}
	
	
	private final IEditor getEditor(int type) {
		if (type == EDIT_REPORT_STRUCTURE)
			return editor;
		return null;
	}
}
