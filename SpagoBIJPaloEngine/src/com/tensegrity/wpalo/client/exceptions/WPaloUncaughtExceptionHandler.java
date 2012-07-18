/*
*
* @file WPaloUncaughtExceptionHandler.java
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
* @version $Id: WPaloUncaughtExceptionHandler.java,v 1.11 2010/03/11 10:43:45 PhilippBouillon Exp $
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
