/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.ui.widgets;

import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ModalPanel;
import com.extjs.gxt.ui.client.widget.StatusBar;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.google.gwt.user.client.ui.RootPanel;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;

public class BusyIndicatorPanel {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private ModalPanel panel;
	private Component content;	
	
	public final void show(final String txt, boolean pushToFront) {
		if (panel != null) {
			hide();
		}
		panel = ModalPanel.pop();
//		panel.setBlink(true);
		content = createContentPanel(txt);
		RootPanel.get().add(content);
		content.el().center(true);		
		panel.show(content);
		if (pushToFront) {
			content.el().updateZIndex(100);
		}
	}

	public final void hide() {
		if (panel != null) {
			ModalPanel.push(panel);
			panel = null;
			RootPanel.get().remove(content);
			content.removeFromParent();
		}
	}
	
	private final Component createContentPanel(String txt) {
		ContentPanel content = new ContentPanel();
		content.setHeading(constants.information());
		content.setLayout(new FitLayout());
		content.setPixelSize(300, 100);
		StatusBar statusBar = new StatusBar();
		statusBar.showBusy(txt);
		content.add(statusBar, new RowData(1.0, 1.0, new Margins(20, 20, 20, 20)));
	    return content;
	}
}
