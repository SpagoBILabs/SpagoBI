/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.ui.mvc.reportstructure;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.WPaloServiceProvider;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.serialization.templates.XApplication;
import com.tensegrity.wpalo.client.serialization.templates.XWorkbook;
import com.tensegrity.wpalo.client.ui.dialog.RequestNameDialog;
import com.tensegrity.wpalo.client.ui.dialog.ResultListener;
import com.tensegrity.wpalo.client.ui.model.TreeNode;

public class CreateNewSheet extends SelectionListener<ComponentEvent> {
	private final Tree reportsTree;
	
	public CreateNewSheet(Tree tree) {
		reportsTree = tree;
	}
	
	public void componentSelected(ComponentEvent ce) {
		final TreeItem item = reportsTree.getSelectedItem();
		if (item == null) {
			return;
		}
		final XObject parent = ((TreeNode) item.getModel()).getXObject();
		if (parent == null) {
			return;
		}
		String title = "";
		if (parent instanceof XApplication) {
			title = "Create new Workbook";
		} else if (parent instanceof XAccount) {
			XAccount acc = (XAccount) parent;
			if (acc.getConnection().getConnectionType() != XConnection.TYPE_WSS) {
				return;
			}
			title = "Create new Workbook Template";
		} else {		
			return;
		}
		
		RequestNameDialog rnd = new RequestNameDialog(title,
				"Name", new ResultListener<String>() {
					public void requestCancelled() {
					}

					public void requestFinished(String result) {
						WPaloServiceProvider.getInstance().createWorkbook(
								parent, result,
								new Callback<XWorkbook>() {
									public void onSuccess(XWorkbook workbook) {
										if (workbook == null) {
											System.err
													.println("Returned workbook == null");
											return;
										}
										Dispatcher.get().dispatch(
												WPaloEvent.UPDATE_WORKBOOKS, parent);
									}
								});
					}
				});
		rnd.show();
	}
}
