/*
*
* @file AccountController.java
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
* @version $Id: AccountController.java,v 1.18 2010/02/12 13:49:50 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.account;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.ui.editor.IEditor;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>AccountController</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AccountController.java,v 1.18 2010/02/12 13:49:50 PhilippBouillon Exp $
 **/
public class AccountController extends Controller implements WPaloEvent {

	private AccountNavigatorView navigatorView;
	private AccountEditor accountEditor;
	private ConnectionEditor connectionEditor;
	
	public AccountController() {
		// we are interested in following events:
		registerEventTypes(INIT, LOGIN, LOGOUT, EXPANDED_ACCOUNT_SECTION, EDIT_ACCOUNT_ITEM,
				EDIT_CONNECTION_ITEM, SELECTED_ACCOUNTS, SELECTED_CONNECTIONS,				
				ADD_ACCOUNT_ITEM, ADD_CONNECTION_ITEM,
				SAVED_ACCOUNT_ITEM, SAVED_CONNECTION_ITEM,
				DELETED_ITEM, LOGOUT_CLICKED);
	}

	public void handleEvent(final AppEvent<?> event) {
		switch(event.type) {
		case LOGIN: 
		case INIT: 				   
		case SAVED_ACCOUNT_ITEM:
		case SAVED_CONNECTION_ITEM:
		case EXPANDED_ACCOUNT_SECTION:
			forwardToView(navigatorView, event);
			break;
		case EDIT_ACCOUNT_ITEM:
		case EDIT_CONNECTION_ITEM:
			final Workbench wb = (Workbench)Registry.get(Workbench.ID);
//			if (wb.getCurrentEditor() != null &&
//				wb.getCurrentEditor().isDirty()) {
//			}
			final IEditor editor = getEditor(event.type);
			wb.checkOpen(editor, new Callback<Boolean>(){
				public void onSuccess(Boolean result) {
					if (result) {
						editor.setInput(event.data);								
						wb.open(editor);						
					}
				}
			});
			break;
		case ADD_ACCOUNT_ITEM:
		case ADD_CONNECTION_ITEM:
			doAdd(event.type);
			break;
		case DELETED_ITEM:			
			// close corresponding editor:
			Workbench _wb = (Workbench) Registry.get(Workbench.ID);
			if (_wb.getCurrentEditor() != null && _wb.getCurrentEditor().getInput() != null) {
				Object input = _wb.getCurrentEditor().getInput();
				if (event.data != null && event.data instanceof TreeNode) {
					if (((TreeNode) event.data).equals(input)) {
						_wb.close(null, null);				
					}
				}
			}			
			forwardToView(navigatorView, event);
			break;
		case LOGOUT: initialized = false;
		             break;
		}
	}

	@Override
	public final void initialize() {
		super.initialize();
		navigatorView = new AccountNavigatorView(this);
		accountEditor = new AccountEditor();
		connectionEditor = new ConnectionEditor();
	}
	
	
	private final IEditor getEditor(int type) {
		if (type == EDIT_ACCOUNT_ITEM)
			return accountEditor;
		else 
			return connectionEditor;
	}

	private final void doAdd(int eventType) {
		Object input = null;
		IEditor editor = null;
		switch(eventType) {
		case ADD_ACCOUNT_ITEM:
			input = new TreeNode(null, new XAccount());
			editor = accountEditor;
			break;
		case ADD_CONNECTION_ITEM:
			input = new TreeNode(null, new XConnection());
			editor = connectionEditor;
			break;
		}
		
		final Workbench wb = (Workbench)Registry.get(Workbench.ID);
		final IEditor edi = editor;
		final Object inp = input;
		wb.checkOpen(edi, new Callback<Boolean>(){
			public void onSuccess(Boolean result) {
				if (result) {
					edi.setInput(inp);
					wb.open(edi);
					edi.selectFirstTab();
					edi.setTextCursor();
					if (edi.equals(accountEditor)) {
						((AccountEditor) edi).prefillFields();
					}
				}
			}
		});

	}
}
