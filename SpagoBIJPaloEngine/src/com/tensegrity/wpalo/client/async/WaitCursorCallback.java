/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.async;

import com.extjs.gxt.ui.client.Registry;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

public abstract class WaitCursorCallback <T> extends Callback <T> {
	public WaitCursorCallback(String waitMessage) {		
		super();
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(waitMessage);
	}
	
	public WaitCursorCallback(String waitMessage, String message) {
		super(message);
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(waitMessage);
	}
	
	public void onFailure(Throwable caught) {
		((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
		super.onFailure(caught);
	}
}
