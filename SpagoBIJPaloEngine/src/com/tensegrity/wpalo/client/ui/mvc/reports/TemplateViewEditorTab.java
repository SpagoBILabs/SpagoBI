/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
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
