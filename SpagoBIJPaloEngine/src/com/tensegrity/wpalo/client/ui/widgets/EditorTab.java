/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.widgets;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.tensegrity.palo.gwt.core.client.models.XObject;

/**
 * <code>EditorTab</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: EditorTab.java,v 1.11 2010/04/15 09:55:22 PhilippBouillon Exp $
 **/
public abstract class EditorTab extends TabItem {
	public EditorTab(String name) {
		super(name);
	}
	public abstract void set(XObject input);
	public abstract boolean save(XObject input);	
	public abstract void saveAs(String name, XObject input);
	
	public void hideHeader() {
		getHeader().hide();
		getHeader().setIntStyleAttribute("height", 0);
//		remove(getHeader());
		getHeader().setStyleName("invisible");
		getHeader().getElement().setAttribute("height", "0px");
		getHeader().getElement().setAttribute("borders", "none");
	}
//TODO we should validate input before saving!	public abstract boolean validate(XObject input);
}
