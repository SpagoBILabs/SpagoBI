/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.exceptions;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.widgets.client.container.PaloInsert;
import com.tensegrity.palo.gwt.widgets.client.container.PaloReplace;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

public class WPaloUncaughtExceptionHandler implements UncaughtExceptionHandler {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	public void onUncaughtException(Throwable t) {
		((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
		t.printStackTrace();
		// TODO here we can examine the exception deeper...
		String msg = t.getCause() != null ? 
				t.getCause().getLocalizedMessage() : t.getMessage();
		// Evil hack to remove the exception message that
		// appears when dragging something quickly in IE.
		// What's the stack trace of this exception?? Can't
		// find out...
		if (msg.indexOf("-2146823281") == -1 &&
			msg.indexOf("a.e is null") == -1) {
				MessageBox.alert(constants.error(), msg, null);
		} else {
			// Another hack to remove the drag insertion marks
			// if any are present...
			RootPanel rp = RootPanel.get();
			int widgetCount = rp.getWidgetCount();
			List <PaloInsert> dragMarks = new ArrayList<PaloInsert>();
			Widget w;
			for (int i = 0; i < widgetCount; i++) {
				if ((w = rp.getWidget(i)) instanceof PaloInsert) {
					dragMarks.add((PaloInsert) w);
				}
			}
			for (PaloInsert pi: dragMarks) {
				pi.remove();
			}
			List <PaloReplace> replaceMarks = new ArrayList<PaloReplace>();
			widgetCount = rp.getWidgetCount();
			for (int i = 0; i < widgetCount; i++) {
				if ((w = rp.getWidget(i)) instanceof PaloReplace) {
					replaceMarks.add((PaloReplace) w);
				}
			}
			for (PaloReplace pr: replaceMarks) {
				pr.remove();
			}							
		}
	}
}
