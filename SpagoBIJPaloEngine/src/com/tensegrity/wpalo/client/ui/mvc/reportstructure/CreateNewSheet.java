/*
*
* @file CreateNewSheet.java
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
* @version $Id: CreateNewSheet.java,v 1.8 2010/02/12 13:49:50 PhilippBouillon Exp $
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
