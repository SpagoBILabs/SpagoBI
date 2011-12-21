/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.ui.mvc.viewbrowser;

import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.user.client.Element;

public class ViewEditorTabFolder extends TabPanel {
	private final boolean hideTitlebar;
	
	public ViewEditorTabFolder(boolean hideTitlebar) {
		this.hideTitlebar = hideTitlebar;
	}
	
	//BUG IN GXT??? WE HAVE TO MANUALLY RESIZE TAB FOLDER ON INITIAL OPEN...
	public boolean layout() {
		setSize(getSize().width, getSize().height);
		return super.layout();
	}
	
	protected void onRender(Element target, int index) {
		super.onRender(target, index);		
		if (hideTitlebar) {
			el().getChild(0).removeFromParent();
		} 
		layout();
	}
}
