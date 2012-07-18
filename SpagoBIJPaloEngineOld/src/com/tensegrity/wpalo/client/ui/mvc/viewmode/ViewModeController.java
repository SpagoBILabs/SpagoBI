/*
*
* @file ViewModeController.java
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
* @version $Id: ViewModeController.java,v 1.5 2009/12/17 16:14:20 PhilippBouillon Exp $
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
