/*
*
* @file ReportStructureEditor.java
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
* @version $Id: ReportStructureEditor.java,v 1.13 2009/12/17 16:14:20 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.client.ui.mvc.reportstructure;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.wpalo.client.ui.editor.AbstractTabEditor;
import com.tensegrity.wpalo.client.ui.widgets.EditorTab;

public class ReportStructureEditor extends AbstractTabEditor {
	static ReportStructureNavigatorView view;
	private TemplateTab templateTab;
	
	public ReportStructureEditor(ReportStructureNavigatorView view) {
		ReportStructureEditor.view = view;
	}
	
	public EditorTab[] getEditorTabs() {		
		VariableTab vTab = new VariableTab(this);
		templateTab = new TemplateTab(vTab);
		return new EditorTab [] {templateTab, new ListTab(), 
				vTab, new RightsTab()};
	}

	void reload(XObject obj) {
		if (templateTab == null) {
			return;
		}
		templateTab.reload(obj);
	}
	
	
	public String getTitle(XObject input) {
		return "Edit Report Structure";
	}

	public String getId() {
		return "reportstructureeditor";
	}
}
