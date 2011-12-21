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
package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.tensegrity.palo.gwt.core.client.models.XObject;

/**
 * <code>SelectionEvent</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: SelectionEvent.java,v 1.4 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class SelectionEvent extends BaseEvent {
	
	private XObject selection;
	
	public SelectionEvent(Object source, XObject selection) {
		super(source);
		this.selection = selection;
	}
	
	public XObject getSelection() {
		return selection;
	}
}