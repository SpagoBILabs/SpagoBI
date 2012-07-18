/*
*
* @file TemplateEditor.java
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
* @version $Id: TemplateEditor.java,v 1.13 2010/04/15 09:55:22 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.reports;

import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.google.gwt.user.client.ui.Frame;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
import com.tensegrity.wpalo.client.WPaloServiceProvider;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.serialization.templates.XTemplate;
import com.tensegrity.wpalo.client.serialization.templates.XWorkbook;
import com.tensegrity.wpalo.client.serialization.templates.XWorksheet;
import com.tensegrity.wpalo.client.ui.editor.AbstractTabEditor;
import com.tensegrity.wpalo.client.ui.widgets.EditorTab;

/**
 * <code>GroupEditor</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: TemplateEditor.java,v 1.13 2010/04/15 09:55:22 PhilippBouillon Exp $
 **/
public class TemplateEditor extends AbstractTabEditor {
	
	public EditorTab[] getEditorTabs() {
		return new EditorTab[] { new TemplateEditorTab() };
	}

	public TemplateEditor() {
//		super(false);
	}
	
	public String getTitle(XObject input) {
		if (input instanceof XWorkbook) {
			return ((XWorkbook) input).getName();
		}
		return "Template Editor";
	}

	public String getId() {		
		return "template_editor";
	}	
}

class TemplateEditorTab extends EditorTab {
	private final Frame frame;
	
	TemplateEditorTab() {
		super("Template");
		setText("Template");
		setClosable(false);
		frame = new Frame();
		frame.setHeight("100%");
		frame.setWidth("100%");
		WidgetComponent component = new WidgetComponent(frame);
		add(component);
	}
	
	public void set(XObject input) {
		if (input instanceof XTemplate) {			
			XTemplate template = (XTemplate) input;
			WPaloServiceProvider.getInstance().loadWorksheet(template,
					new Callback<XWorksheet>(){
						public void onSuccess(XWorksheet ws) {
							XWorkbook wb = ws.getWorkbook();
							XAccount acc = wb.getAccount();
							XConnection con = acc.getConnection();
							
							String host = con.getHost();
							String service = con.getService();
							if (service.indexOf(":") != -1) {
								service = service.split(":")[1];
							}							
							String url = host + ":" + service + "/cc/auto_login.php?user=";
							url += acc.getLogin() + "&pass=" + acc.getPassword();
							url += "&app=" + wb.getAppName() + "&wb=" + wb.getName();
							url += "&ws=" + ws.getName() + "&lang=en_US&w=1024&h=768"; 
							frame.setUrl(url);
							setText(wb.getName());
						}
					});
		} 
//		else if (input instanceof XView) {
//			XView view = (XView)input;
//			WPaloTable table = new WPaloTable();
//			table.setWidth("100%");
//			table.setHeight("100%");
//			Workbench wb = (Workbench)Registry.get(Workbench.ID);
//			wb.open(table);
//			createInput(table, view);
//		}
	}

//	private final void createInput(final WPaloTable forTable, final XView xView) {
//		// do server call:
//		WPaloCubeViewServiceProvider.getInstance().getCubeView(xView,
//				new AsyncCallback<XViewModel>() {
//					public final void onFailure(Throwable cause) {
//					}
//
//					public final void onSuccess(XViewModel model) {
//						if (model != null) {
//							forTable.setInput(model);
//							layout();
//						}
//
//					}
//				});
//
//	}

	public boolean save(XObject input) {
		return true;
	}
	public void saveAs(String name, XObject input) {		
	}

}


