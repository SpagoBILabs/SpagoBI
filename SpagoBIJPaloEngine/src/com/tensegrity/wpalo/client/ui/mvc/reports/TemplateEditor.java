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


