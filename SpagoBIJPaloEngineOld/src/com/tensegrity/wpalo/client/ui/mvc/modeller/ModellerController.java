/*
*
* @file ModellerController.java
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
* @version $Id: ModellerController.java,v 1.8 2009/12/17 16:14:20 PhilippBouillon Exp $
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
