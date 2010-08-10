/*
*
* @file ViewBrowserController.java
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
* @version $Id: ViewBrowserController.java,v 1.20 2010/02/12 13:49:50 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.viewbrowser;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.wpalo.client.DisplayFlags;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.LargeQueryWarningDialog;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>ViewBrowserController</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewBrowserController.java,v 1.20 2010/02/12 13:49:50 PhilippBouillon Exp $
 **/
public class ViewBrowserController  extends Controller {

	private ViewBrowser viewBrowser;
	private ViewBrowserEditor viewEditor;
	private List <XView> viewsToLoad = new ArrayList<XView>();
	
	public ViewBrowserController() {
		// we are interested in following events:
		registerEventTypes(WPaloEvent.INIT,
				WPaloEvent.LOGOUT,
				WPaloEvent.EXPANDED_VIEWBROWSER_SECTION,
				WPaloEvent.EDIT_VIEWBROWSER_VIEW,
				WPaloEvent.SHOW_VIEWBROWSER_VIEW,
				WPaloEvent.DELETED_VIEWBROWSER_VIEW,
				WPaloEvent.RENAMED_VIEWBROWSER_VIEW,
				WPaloEvent.WILL_DELETE_VIEWBROWSER_VIEW,
				WPaloEvent.DELETED_ITEM,
				WPaloEvent.VIEW_LOADED);
	}

	
	public void addViewToLoad(XView view) {
		viewsToLoad.add(view);
	}
	
	private final void checkOpen(final Callback <Boolean> callback) {
		final Workbench wb = (Workbench) Registry.get(Workbench.ID);
		if (wb.getCurrentEditor() != viewEditor) {
			wb.checkOpen(viewEditor, new Callback<Boolean>() {
				public void onFailure(Throwable t) {
					callback.onSuccess(true);
				}
				
				public void onSuccess(Boolean result) {
					if (result) {
						wb.open(viewEditor);
						forwardToView(viewBrowser, WPaloEvent.EXPANDED_VIEWBROWSER_SECTION, null);						
					}
					callback.onSuccess(result);
				}
			});
		} else {
			callback.onSuccess(true);
		}
	}
		
	public void handleEvent(final AppEvent<?> event) {
		switch (event.type) {
		case WPaloEvent.INIT:
			boolean hideTitlebar = false;
			if (event.data != null && event.data instanceof DisplayFlags) {
				hideTitlebar = DisplayFlags.isHideViewTabs();
			}
			initialize(hideTitlebar);
			if (event.data instanceof DisplayFlags) {
				viewEditor.initUI((DisplayFlags) event.data);

			}
			
			forwardToView(viewBrowser, event);

			break;
		case WPaloEvent.EXPANDED_VIEWBROWSER_SECTION:			
			break;
		case WPaloEvent.EDIT_VIEWBROWSER_VIEW:
			checkOpen(new Callback<Boolean>() {
				public void onSuccess(Boolean result) {
					if (result) {
						viewEditor.setInput(event.data);
					}
				}
			});
			break;
		case WPaloEvent.DELETED_VIEWBROWSER_VIEW:
			viewEditor.closeIfOpen(event.data);
			break;
		case WPaloEvent.SHOW_VIEWBROWSER_VIEW:
			viewEditor.showIfOpen(event.data);
			break;
		case WPaloEvent.RENAMED_VIEWBROWSER_VIEW:
			viewEditor.renameIfOpen(event.data);
			break;
		case WPaloEvent.WILL_DELETE_VIEWBROWSER_VIEW:
			forwardToView(viewBrowser, event);
			break;
		case WPaloEvent.DELETED_ITEM:
			forwardToView(viewBrowser, event);
			break;
		case WPaloEvent.LOGOUT: initialized = false;
			break;
		case WPaloEvent.VIEW_LOADED: loadNextView();
			break;
			
//		case WPaloEvent.LOGOUT:
//			viewBrowser.clear();
//			viewEditor.clear();
//			break;
		}
	}

	public final void initialize(boolean hideTitlebar) {
		super.initialize();
		viewBrowser = new ViewBrowser(this);
		viewEditor = new ViewBrowserEditor(hideTitlebar);
		Workbench wb = (Workbench) Registry.get(Workbench.ID);
		wb.open(viewEditor);
	}

	private final void loadNextView() {
		if (!viewsToLoad.isEmpty()) {
			XView xView = viewsToLoad.get(0);
			viewsToLoad.remove(0);
			DisplayFlags.setDisplayFlagsFor(xView, ((Workbench)Registry.get(Workbench.ID)).getUser(), xView.getDisplayFlags(), null);
			Dispatcher.get().dispatch(WPaloEvent.EDIT_VIEWBROWSER_VIEW, xView);
		} else {
			LargeQueryWarningDialog.hideWarnDialog = false;
		}
	}
}
