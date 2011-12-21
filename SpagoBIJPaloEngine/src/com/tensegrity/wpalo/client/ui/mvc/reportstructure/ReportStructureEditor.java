/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
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
