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
package com.tensegrity.wpalo.client.ui.mvc.viewmode;

import java.util.HashMap;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.reports.XReport;
import com.tensegrity.palo.gwt.core.client.models.reports.XVariableDescriptor;
import com.tensegrity.wpalo.client.WPaloServiceProvider;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.serialization.XConstants;
import com.tensegrity.wpalo.client.serialization.templates.XWorkbook;
import com.tensegrity.wpalo.client.serialization.templates.XWorksheet;
import com.tensegrity.wpalo.client.ui.editor.CloseObserver;
import com.tensegrity.wpalo.client.ui.editor.IEditor;
import com.tensegrity.wpalo.client.ui.model.TreeNode;

public class ViewModeEditor implements IEditor {
	class ReportTab extends TabItem {
		String id;
		
		ReportTab(String id, String text) {
			super(text);
			this.id = id;
		}
	}

	protected ContentPanel content;
	protected String title;
	
	private TreeNode input;
	private final TabPanel tabFolder;

	public ViewModeEditor() {
	    //create content:
	    content = new ContentPanel();
	    content.setBodyBorder(false);
	    content.setHeaderVisible(false);
	    content.setScrollMode(Scroll.AUTO);
//		content.setHeading("Properties");
		content.setButtonAlign(HorizontalAlignment.RIGHT);
		
		tabFolder = new TabPanel();
		tabFolder.setTabScroll(true);

		RowLayout layout = new RowLayout(Orientation.VERTICAL);
		content.setLayout(layout);
		content.add(tabFolder, new RowData(1, 1));
		content.setTitle("Report Viewer");
		
//		RowLayout layout = new RowLayout(Orientation.VERTICAL);
//		content.setLayout(layout);
//		content.add(tabFolder, new RowData(1, 1));
		
	}

	public void beforeClose(AsyncCallback<Boolean> cb) {
		cb.onSuccess(true);
	}
	
	public final void close(CloseObserver observer) {
		tabFolder.removeAll();
		tabFolder.removeFromParent();
		if(observer != null)
			observer.finishedClosed();
	}

	public final ContentPanel getPanel() {
		return content;
	}

	public final String getTitle() {
		return title;
	}
	
	public final Object getInput() {
		return input;
	}
	
	public final void setInput(Object input) {
		if(input instanceof TreeNode) {
			this.input = (TreeNode)input;
			title = "Report Viewer";
			set((TreeNode) input);
		}
	}
	
	public void set(final TreeNode input) {
		if (input == null || input.getXObject() == null) {
			return;
		}
		if (input.getXObject() instanceof XReport) {
			final XReport report = (XReport) input.getXObject();
			if (report.getReceiverType().equals(XConstants.TYPE_FOLDER_ELEMENT_SHEET)) {
				ViewModeWorkbench wb = (ViewModeWorkbench)Registry.get(
						ViewModeWorkbench.ID);
				final XUser user = wb.getUser();
				WPaloServiceProvider.getInstance().loadWorksheet(report, user, 
						new Callback<XWorksheet>(){

							public void onSuccess(final XWorksheet ws) {
								WPaloServiceProvider.getInstance().getVariables(
										report, user, new Callback<XVariableDescriptor>(){
											public void onSuccess(
													XVariableDescriptor desc) {
												XWorkbook wb = ws.getWorkbook();
												XAccount acc = wb.getAccount();
												XConnection con = acc.getConnection();
												
												HashMap <XObject, String> mapping = desc.getVariableMapping();
												
												String host = con.getHost();
												String service = con.getService();
												if (service.indexOf(":") != -1) {
													service = service.split(":")[1];
												}
												
												String url = host + ":" + service + "/cc/auto_login.php?user=";
												url += acc.getLogin() + "&pass=" + acc.getPassword();
												url += "&app=" + wb.getAppName() + "&wb=" + wb.getName();
												url += "&ws=" + ws.getName() + "&lang=en_US&w=1024&h=768";
//												for (XElement xe: report.getElements()) {
//													for (XObject match: mapping.keySet()) {
//														if (match instanceof XHierarchy) {
//															if (xe.getHierarchy().equals(match)) {
//																url += "&var_" + mapping.get(match) + "=" + xe.getName();
//																break;
//															}
//														} else if (match instanceof XSubset) {
//															if (xe.getHierarchy().equals(((XSubset) match).getHierarchy())) {
//																url += "&var_" + mapping.get(match) + "=" + xe.getName();
//																break;																
//															}
//														}
//													}
//												}
												for (TabItem ti: tabFolder.getItems()) {
													ReportTab rt = (ReportTab) ti;
													if (rt.id.equals(url + report.getName())) {
														rt.getTabPanel().scrollToTab(rt, true);
														tabFolder.setSelection(rt);
														return;
													}
												}
												
												Frame frame = new Frame();
												frame.setHeight("100%");
												frame.setWidth("100%");
												WidgetComponent component = new WidgetComponent(frame);
												frame.setUrl(url);
												ReportTab tab = new ReportTab(url + report.getName(), report.getName());
												tab.setClosable(true);
												tab.add(component);
												tabFolder.add(tab);
												tabFolder.setSelection(tab);
												
											}});
							}
						});
			}				
		} else {
		}	
	}
	
	public final void markDirty() {
	}
	
	public boolean isDirty() {
		return false;
	}
	
	public final void doSave(AsyncCallback <Boolean> cb) {
	}

	public String getId() {
		return "viewmodeeditor";
	}

	public void selectFirstTab() {
		// TODO Auto-generated method stub
		
	}

	public void setTextCursor() {
		// TODO Auto-generated method stub
		
	}
}
