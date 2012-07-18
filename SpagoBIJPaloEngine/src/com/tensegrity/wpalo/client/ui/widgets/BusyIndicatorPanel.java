/*
*
* @file BusyIndicatorPanel.java
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
* @version $Id: BusyIndicatorPanel.java,v 1.6 2010/02/12 13:49:50 PhilippBouillon Exp $
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
