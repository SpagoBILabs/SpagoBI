/*
*
* @file AdminController.java
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
* @version $Id: AdminController.java,v 1.20 2010/02/12 13:49:50 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.admin;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.tensegrity.palo.gwt.core.client.models.admin.XGroup;
import com.tensegrity.palo.gwt.core.client.models.admin.XRole;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.services.admin.WPaloAdminServiceProvider;
import com.tensegrity.wpalo.client.ui.editor.IEditor;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>AdminController</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AdminController.java,v 1.20 2010/02/12 13:49:50 PhilippBouillon Exp $
 **/
public class AdminController extends Controller implements WPaloEvent {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private AdminNavigatorView navigatorView;
	private UserEditor usrEditor;
	private GroupEditor grpEditor;
	private RoleEditor roleEditor;
	
	public AdminController() {
		// we are interested in following events:
		registerEventTypes(INIT, LOGOUT);
		// all admin events...
		registerEventTypes(SELECTED_GROUPS, SELECTED_ROLES, SELECTED_USERS,
				EDIT_GROUP_ITEM, EDIT_ROLE_ITEM, EDIT_USER_ITEM,
				EXPANDED_ADMIN_SECTION,
				ADD_GROUP_ITEM, ADD_ROLE_ITEM, ADD_USER_ITEM,
				SAVED_USER_ITEM, SAVED_GROUP_ITEM, SAVED_ROLE_ITEM,
				DELETED_ITEM);
	}
	
	void updateGroup(XGroup group) {
		navigatorView.updateGroup(group);
	}
	
	void updateUser(XUser user) {
		navigatorView.updateUser(user);
	}
	
	public void handleEvent(final AppEvent<?> event) {
		switch(event.type) {
		case INIT:
		case SAVED_USER_ITEM:
		case SAVED_GROUP_ITEM:
		case SAVED_ROLE_ITEM:
		case EXPANDED_ADMIN_SECTION:
			forwardToView(navigatorView, event);
			break;
		case EDIT_GROUP_ITEM:
		case EDIT_ROLE_ITEM:
		case EDIT_USER_ITEM:
			final Workbench wb = (Workbench)Registry.get(Workbench.ID);
			final IEditor editor = getEditor(event.type);
			wb.checkOpen(editor, new Callback<Boolean>() {
				public void onSuccess(Boolean result) {
					if (result) {
						editor.setInput(event.data);			
						wb.open(editor);
					}
				}
			});					
			break;
		case ADD_GROUP_ITEM:
		case ADD_ROLE_ITEM:
		case ADD_USER_ITEM:
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
			break;
		case LOGOUT: initialized = false;
        		     break;			
		}
	}

	@Override
	public final void initialize() {
		super.initialize();
		navigatorView = new AdminNavigatorView(this);
		usrEditor = new UserEditor(this);
		grpEditor = new GroupEditor(this);
		roleEditor = new RoleEditor();
	}
	
	
	private final IEditor getEditor(int type) {
		if (type == EDIT_GROUP_ITEM)
			return grpEditor;
		else if (type == EDIT_ROLE_ITEM)
			return roleEditor;
		else
			return usrEditor;
	}
	
	private final void doAdd(int eventType) {
		try {
		Object input = null;
		IEditor editor = null;
		switch(eventType) {
		case ADD_GROUP_ITEM:
			input = new TreeNode(null, new XGroup());
			editor = grpEditor;
			break;
		case ADD_ROLE_ITEM:
			input = new TreeNode(null, new XRole());
			editor = roleEditor;
			break;
		case ADD_USER_ITEM:
			XUser user = ((Workbench) Registry.get(Workbench.ID)).getUser();
			String sessionId = user.getSessionId(); 
			WPaloAdminServiceProvider.getInstance().getRoles(sessionId, user,
					new Callback<XRole[]>(constants.loadingAllRolesFailed()) {
						public void onSuccess(XRole[] result) {
							XUser newXUser = new XUser();
							final Object input = new TreeNode(null, newXUser);
							for (XRole r: result) {
								if (r.getName().equalsIgnoreCase("editor")) {
									newXUser.addRoleID(r.getId());
									newXUser.addRoleName(r.getId(), r.getName());
								}
								if (r.getName().equalsIgnoreCase("viewer")) {
									newXUser.addRoleID(r.getId());
									newXUser.addRoleName(r.getId(), r.getName());
								}
							}
							final Workbench wb = (Workbench)Registry.get(Workbench.ID);
							wb.checkOpen(usrEditor, new Callback<Boolean>() {
								public void onSuccess(Boolean result) {
									if (result) {
										IEditor editor = usrEditor;
										editor.setInput(input);									
										wb.open(editor);
										editor.selectFirstTab();
										editor.setTextCursor();
									}
								}
							});
						}						
					});
			break;
		}		
		if (input != null && editor != null) {
			final Workbench wb = (Workbench)Registry.get(Workbench.ID);
			final IEditor edi = editor;
			final Object inp = input;
			wb.checkOpen(edi, new Callback<Boolean>() {
				public void onSuccess(Boolean result) {
					if (result) {
						edi.setInput(inp);			
						wb.open(edi);
						edi.selectFirstTab();
						edi.setTextCursor();						
					}
				}
			});
		}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
