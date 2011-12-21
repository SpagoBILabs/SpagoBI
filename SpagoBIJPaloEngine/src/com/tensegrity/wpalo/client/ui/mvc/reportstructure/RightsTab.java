/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.ui.mvc.reportstructure;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.wpalo.client.ui.widgets.EditorTab;

class RightsTab extends EditorTab {
	RightsTab() {
		super("Rights");
		setText("Rights");
		setIconStyle("icon-rights");
		setClosable(false);
		setScrollMode(Scroll.AUTO);
	}

	public boolean save(XObject input) {
		return true;
	}
	public void saveAs(String name, XObject input) {		
	}

	public void set(XObject input) {
	}	
}