/*
*
* @file ReportStructureController.java
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
* @version $Id: ReportStructureController.java,v 1.13 2009/12/17 16:14:20 PhilippBouillon Exp $
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
