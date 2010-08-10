/*
*
* @file TemplateViewEditorTab.java
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
* @version $Id: TemplateViewEditorTab.java,v 1.12 2010/04/15 09:55:22 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.reports;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.CubeViewEditor;
import com.tensegrity.wpalo.client.ui.widgets.EditorTab;

/**
 * <code>TemplateViewEditorTab</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: TemplateViewEditorTab.java,v 1.12 2010/04/15 09:55:22 PhilippBouillon Exp $
 **/
public class TemplateViewEditorTab extends EditorTab {

	private final CubeViewEditor vEditor;
	
	public TemplateViewEditorTab(String name) {
		super(name);
		setText(name);
		vEditor = new CubeViewEditor(this);
		vEditor.setWidth("100%");
		vEditor.setHeight("100%");
		add(vEditor);
		vEditor.initialize(true, null);
	}
	public boolean save(XObject input) {
		return true;
	}
	public void saveAs(String name, XObject input) {		
	}

	public void set(XObject input) {
		if (input instanceof XView) {
			XView xView = (XView) input;
			load(xView);
		}
	}
	
	private final void load(XView xView) {
//		// do server call:
//		WPaloCubeViewServiceProvider.getInstance().getCubeView(xView,
//				new Callback<XViewModel>() {
//					public final void onSuccess(XViewModel model) {
//						if (model != null) {
//							vEditor.setInput(model);
//							setText(model.getName());
//							layout();
//							vEditor.layout();
//						} 
//
//					}
//				});
//
	}

}
