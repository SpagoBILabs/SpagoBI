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
package com.tensegrity.wpalo.client.ui.mvc.modeller;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.ui.editor.IEditor;
import com.tensegrity.wpalo.client.ui.mvc.admin.UserEditor;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>AdminController</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ModellerController.java,v 1.8 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class ModellerController extends Controller implements WPaloEvent {

	private ModellerNavigatorView navigatorView;
	private DimensionEditor dimEditor;
	private ServerEditor serverEditor;

	public ModellerController() {
		// we are interested in following events:
		registerEventTypes(INIT, LOGOUT);
		// all admin events...
		registerEventTypes(SELECTED_DIMENSIONS, EDIT_DIMENSION_ITEM, 
							SELECTED_SERVERS, EDIT_SERVER_ITEM, 
							EXPANDED_SERVER_SECTION);
	}
	
	public void handleEvent(AppEvent<?> event) {
		switch(event.type) {
		case INIT:
			forwardToView(navigatorView, event);
			break;
		case EXPANDED_SERVER_SECTION:
			forwardToView(navigatorView, event);
			break;
		case EDIT_DIMENSION_ITEM:
		case EDIT_SERVER_ITEM:
			IEditor editor = getEditor(event.type);
			editor.setInput(event.data);
			Workbench wb = (Workbench)Registry.get(Workbench.ID);
			wb.open(editor);
			break;
		case LOGOUT: initialized = false;
			break;
		}
	}

	@Override
	public final void initialize() {
		super.initialize();
		navigatorView = new ModellerNavigatorView(this);
		dimEditor = new DimensionEditor();
		serverEditor = new ServerEditor();
	}
	
	
	private final IEditor getEditor(int type) {
		if (type == EDIT_DIMENSION_ITEM)
			return dimEditor;
		else if (type == EDIT_SERVER_ITEM)
			return serverEditor;

		return null;
	}
}
