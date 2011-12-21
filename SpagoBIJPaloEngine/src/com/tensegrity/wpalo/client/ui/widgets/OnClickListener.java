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

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;

/**
 * <code>ClickedListener</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: OnClickListener.java,v 1.4 2009/12/17 16:14:21 PhilippBouillon Exp $
 **/
public abstract class OnClickListener implements Listener<ComponentEvent> {

	public abstract void clicked(ComponentEvent ce);
	
	public void handleEvent(ComponentEvent ce) {
		switch (ce.type) {
		case Events.OnClick:
			clicked(ce);
			break;
		}
	}

}
