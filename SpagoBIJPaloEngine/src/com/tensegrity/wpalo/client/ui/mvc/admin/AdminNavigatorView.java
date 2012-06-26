/*
*
* @file AdminNavigatorView.java
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
* @version $Id: AdminNavigatorView.java,v 1.35 2010/03/02 08:59:12 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.admin;

import java.util.List;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XGroup;
import com.tensegrity.palo.gwt.core.client.models.admin.XRole;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.wpalo.client.DisplayFlags;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.serialization.XConstants;
import com.tensegrity.wpalo.client.serialization.XNode;
import com.tensegrity.wpalo.client.services.admin.WPaloAdminServiceProvider;
import com.tensegrity.wpalo.client.ui.model.TreeLoaderProxy;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.account.AdminHelpDialog;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>AdminNavigator</code> TODO DOCUMENT ME
 * 
 * @version $Id: AdminNavigatorView.java,v 1.35 2010/03/02 08:59:12 PhilippBouillon Exp $
 */
public class AdminNavigatorView extends View {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();

	private Tree navTree;
	private TextToolItem addUser, addGroup, addRole, del;
	private ContentPanel navigator;
	private TreeNode root;
	private TreeStore<TreeNode> treeStore;
	private TreeLoader<TreeNode> treeLoader;
	public static XUser lastCreatedUser = null;
	
	public AdminNavigatorView(Controller controller) {
		super(controller);
	}

	@Override
	protected void handleEvent(AppEvent<?> event) {
		switch (event.type) {
		case WPaloEvent.INIT:
			if(event.data instanceof XUser) {
				XUser user = (XUser)event.data;
				//check if we are admin:
				if(user.isAdmin()) {
					//create ui:
					initUI(user);
					
				}
			} else if (event.data instanceof DisplayFlags) {
				DisplayFlags df = (DisplayFlags) event.data;
				if (!DisplayFlags.isHideNavigator()) {
					XUser user = df.getUser();
					//check if we are admin:
					if(user.isAdmin()) {
						//create ui:
						if(!df.isHideUsersRights()){
							initUI(user);
						}
					}					
				}
			}
			break;
		case WPaloEvent.EXPANDED_ADMIN_SECTION: //load tree data
			TreeNode node = (TreeNode) event.data;
			if (node != null) {
				treeLoader.load(node);
			}
			break;
		case WPaloEvent.SAVED_USER_ITEM:
		case WPaloEvent.SAVED_GROUP_ITEM:
		case WPaloEvent.SAVED_ROLE_ITEM:
			if (treeStore == null) {
				return;
			}
			TreeNode nd = (TreeNode) event.data;
			if (nd != null) {
				if(nd.getParent() != null)
					treeStore.update(nd);
				else  {
					int index = getParentIndex(event.type);
					treeStore.add(root.getChild(index), nd, false);
					root.getChild(index).add(nd);
					//shouldn't the store take care of this???
					if(nd.getParent() == null)
						nd.setParent(root.getChild(index));					
				}
			}
			if (event.type == WPaloEvent.SAVED_USER_ITEM) {
				lastCreatedUser = (XUser) nd.getXObject();
				final XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
				String sessionId = user.getSessionId();
				final String userId = user.getId();
				WPaloAdminServiceProvider.getInstance().hasRoles(sessionId, lastCreatedUser, new String [] {"VIEWER", "EDITOR"},
						new AsyncCallback<Boolean[]>() {							
							public void onSuccess(Boolean[] result) {
								String message = "";
								if (lastCreatedUser.getAccountIDs() == null ||
										lastCreatedUser.getAccountIDs().length == 0) {
									message = constants.adminHintUserCreation();
								}
								if (!result[0]) {
									if (!message.isEmpty()) {
										message += "<br/><br/>";
									}
									message += constants.adminHintViewerRoleMissing();
								}
								if (!result[1]) {
									if (!message.isEmpty()) {
										message += "<br/><br/>";
									}
									message += constants.adminHintEditorRoleMissing();									
								}
								if (!message.isEmpty()) {
									AdminHelpDialog dia = new AdminHelpDialog(message, userId);
									dia.showDialog();
								}
							}
							
							public void onFailure(Throwable arg0) {
								if (lastCreatedUser.getAccountIDs() == null ||
										lastCreatedUser.getAccountIDs().length == 0) {
									String message = constants.adminHintUserCreation(); 
									AdminHelpDialog dia = new AdminHelpDialog(message, userId);
									dia.showDialog();
								}
							}
						});								
			}
			break;
		}
	}

	private final void initUI(XUser forUser) {
		navigator = new ContentPanel();
		navigator.setHeading(constants.webUsersAndRights());
		navigator.setScrollMode(Scroll.AUTO);
		// connect with dispatcher:
		navigator.addListener(Events.Expand, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				Dispatcher.get().dispatch(WPaloEvent.EXPANDED_ADMIN_SECTION);
			}
		});

		//da toolbar
		ToolBar toolbar = new ToolBar();
		fill(toolbar);
		navigator.setTopComponent(toolbar);

		//create the tree which displays the data:
		treeLoader = new BaseTreeLoader<TreeNode>(new TreeLoaderProxy()){
			public boolean hasChildren(TreeNode data) {
				return data != null && data.getXObject() != null &&
				       data.getXObject().hasChildren();
			}
			protected void onLoadSuccess(TreeNode parent,
					List<TreeNode> children) {
				for(TreeNode child : children)
					parent.add(child);
				super.onLoadSuccess(parent, children);
			}
			protected void onLoadFailure(TreeNode loadConfig, Throwable t) {
				if (t instanceof SessionExpiredException)
					Callback.handle((SessionExpiredException) t);
				else
					MessageBox.alert(constants.error(), constants.loadingFailed()
							+ t.getMessage(), null);
			}
		};
		navTree = createTree(treeLoader);
		navigator.add(navTree);
		
		Workbench wb = (Workbench)Registry.get(Workbench.ID);
		wb.addToViewPanel(navigator);
		
		//TODO for testing purpose:
		root = new AdminTreeModel(forUser).getRoot();
		Dispatcher.forwardEvent(WPaloEvent.EXPANDED_ADMIN_SECTION, root);				
	}

	private final Tree createTree(TreeLoader<TreeNode> loader) {
		final Tree tree = new Tree();
		tree.setIndentWidth(1);
		treeStore = new TreeStore<TreeNode>(loader);
		TreeBinder<TreeNode> binder = new TreeNodeBinder(tree, treeStore);
		binder.setDisplayProperty("name");
		binder.setAutoSelect(true);
		binder.setIconProvider(new ModelStringProvider<TreeNode>() {
			public final String getStringValue(TreeNode model, String property) {
				String icon = null;
				String type = model.getXObject().getType();
				if(type.equals(XConstants.TYPE_USERS_NODE))
					icon = "icon-folder";
				else if(type.equals(XConstants.TYPE_GROUPS_NODE))
					icon = "icon-folder";
				else if(type.equals(XGroup.TYPE))
					icon = "icon-group";
				else if(type.equals(XUser.TYPE))
					icon = "icon-user";
				else if (type.equals(XRole.TYPE)) 
					icon =" icon-role2";

				return icon;
			}
		});
		binder.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				TreeNode node = (TreeNode) se.getSelectedItem(); // single selection
				if (node != null && node.getXObject() != null) {
					String type = node.getXObject().getType();
					int eventType = -1;

					if (type.equals(XUser.TYPE))
						eventType = WPaloEvent.EDIT_USER_ITEM;
					else if (type.equals(XGroup.TYPE))
						eventType = WPaloEvent.EDIT_GROUP_ITEM;
					else if (type.equals(XRole.TYPE))
						eventType = WPaloEvent.EDIT_ROLE_ITEM;

					if (eventType > -1) {
						del.setEnabled(true);
						return;
					}
				}
				del.setEnabled(false);
			}
		});
		tree.addListener(Events.OnDoubleClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {			
				TreeNode node = (TreeNode) tree.getSelectedItem().getModel(); // single selection
				if (node != null && node.getXObject() != null) {
					String type = node.getXObject().getType();
					int eventType = -1;

					if (type.equals(XUser.TYPE))
						eventType = WPaloEvent.EDIT_USER_ITEM;
					else if (type.equals(XGroup.TYPE))
						eventType = WPaloEvent.EDIT_GROUP_ITEM;
					else if (type.equals(XRole.TYPE))
						eventType = WPaloEvent.EDIT_ROLE_ITEM;

					if (eventType > -1) {
						fireEvent(new AppEvent<TreeNode>(eventType, node));
						return;
					}
				}
			}
		});		
		return tree;
	}
	
	public void updateGroup(XGroup group) {
		for (TreeNode node: treeStore.getChild(1).getChildren()) {
			if (node.getXObject() != null && node.getXObject().getId() != null) {
				if (group.getId().equals(node.getXObject().getId())) {
					node.setXObject(group);
					treeStore.update(node);
					return;
				}
			}
		}
	}
	
	public void updateUser(XUser user) {
		for (TreeNode node: treeStore.getChild(0).getChildren()) {
			if (node.getXObject() != null && node.getXObject().getId() != null) {
				if (user.getId().equals(node.getXObject().getId())) {
					node.setXObject(user);
					treeStore.update(node);
					return;
				}
			}
		}		
	}
	
	private final void fill(ToolBar toolbar) {
		addUser = createToolItem(constants.addUser(), "icon-user-add", WPaloEvent.ADD_USER_ITEM);
		toolbar.add(addUser);
		addGroup = createToolItem(constants.addGroup(), "icon-group-add", WPaloEvent.ADD_GROUP_ITEM);
		toolbar.add(addGroup);
		addRole = createToolItem(constants.addRole(), "icon-role2-add", WPaloEvent.ADD_ROLE_ITEM);
		toolbar.add(addRole);
		toolbar.add(new SeparatorToolItem());
		del = new TextToolItem("", "icon-delete");
		del.setToolTip(constants.delete()); 
		del.setEnabled(false);
		del.addSelectionListener(new SelectionListener<ComponentEvent>() {
			public final void componentSelected(ComponentEvent ce) {
			}
			public final void handleEvent(ComponentEvent be) {
				TreeItem item = navTree.getSelectedItem();
				final TreeNode node = (TreeNode) item.getModel();
				if(node == null)
					return;
				
				String confirmMsg = null;
				String impossibleMsg = null;
				XObject xObj = node.getXObject();
				String type = xObj.getType();
				if (type.equals(XUser.TYPE)) {
					XUser user = (XUser) node.getXObject();
					if (user != null && user.getLogin() != null && 
							user.getLogin().equalsIgnoreCase("admin")) {
						impossibleMsg = messages.impossibleToDeleteUser(user.getLogin()); 
					} else {
						confirmMsg = messages.deleteUser(user.getLogin()); 
					}
				} else if (type.equals(XGroup.TYPE)) {
					XGroup group = (XGroup) node.getXObject();
					if (group != null && group.getName() != null && 
							group.getName().equalsIgnoreCase("admin")) {
						impossibleMsg = messages.impossibleToDeleteGroup(group.getName()); 
					} else {
						confirmMsg = messages.deleteGroup(group.getName()); 
					}
				} else if (type.equals(XRole.TYPE)) {
					XRole role = (XRole) node.getXObject();
					if (role != null && role.getName() != null && (role.getName().equals("ADMIN") ||
						role.getName().equals("EDITOR") ||
						role.getName().equals("VIEWER") ||
						role.getName().equals("OWNER"))) {
						impossibleMsg = messages.impossibleToDeleteRole(role.getName()); 
					} else {
						confirmMsg = messages.deleteRole(role.getName()); 
					}
				}
				
				// confirm group deletion:
				if (confirmMsg != null) {
					doDelete(node, confirmMsg);
				} else if (impossibleMsg != null) {
					MessageBox.alert(constants.deletionNotPossible(), impossibleMsg, null);
				}
			}
		});
		toolbar.add(del);
	}
	
	private final TextToolItem createToolItem(String text, String icon, final int eventType) {
		TextToolItem item = new TextToolItem("", icon);
		item.setToolTip(text);
		item.addSelectionListener(new SelectionListener<ComponentEvent>() {
			public final void componentSelected(ComponentEvent ce) {
			}
			public final void handleEvent(ComponentEvent be) {
				Dispatcher.get().dispatch(eventType);
			}
		});
		return item;
	}
	
	private final void doDelete(final TreeNode node, final String confirmMsg) {
		final Workbench wb = (Workbench) Registry.get(Workbench.ID);
		XUser admin = wb.getUser();
		if (admin != null) {			
			final XObject xObj = node.getXObject();
			WPaloAdminServiceProvider.getInstance().mayDelete(wb.getUser().getSessionId(), xObj,
					new Callback<String[]>(null) {
						private final void reallyDoDelete() {
							WPaloAdminServiceProvider.getInstance().delete(wb.getUser().getSessionId(), xObj,
									new Callback<Void>(null) {
										public void onSuccess(Void v) {
											treeStore.remove(node.getParent(), node);
											Dispatcher.forwardEvent(new AppEvent<TreeNode>(
													WPaloEvent.DELETED_ITEM, node));
										}
									});							
						}
						
						public void onSuccess(String[] result) {
							if (result == null || result.length == 0) {
								MessageBox.confirm(constants.deleteMessageHeader(), confirmMsg,
										new Listener<WindowEvent>() {
											public void handleEvent(WindowEvent we) {
												Dialog dialog = (Dialog) we.component;
												Button btn = dialog.getButtonPressed();
												if(btn.getItemId().equalsIgnoreCase(Dialog.YES))
													reallyDoDelete();
											}
										});
							} else {
								if (result[0].equals("_NO_ACCOUNT_ERROR_")) {
									if (xObj.getType().equals(XUser.TYPE)) {
										String message = constants.impossibleToDeleteUser(); 
										message += "<br/>" + constants.views() + ":<br/>";
										for (int i = 1; i < result.length; i++) {
											message += result[i] + "<br/>";
										}
										MessageBox.alert(constants.deletionFailed(), message, null);
									} else {
										String message = constants.impossibleToDeleteAccount(); 
										message += "<br/>" + constants.views() + ":<br/>";
										for (int i = 1; i < result.length; i++) {
											message += result[i] + "<br/>";
										}
										MessageBox.alert(constants.deletionFailed(), message, null);																				
									}
								} else {
									String message = constants.deletingUserWarning(); 
									message += "<br/>" + constants.views() + ":<br/>";
									for (String r: result) {
										message += r + "<br/>";
									}
									MessageBox.confirm(constants.proceedWithDelete(), message, new Listener <WindowEvent>(){
										public void handleEvent(WindowEvent be) {
											if (be.buttonClicked.getItemId().equalsIgnoreCase(Dialog.YES)) {
												reallyDoDelete();
											}
										}
									});
								}
							}
						}
					});
				}
	}
	
	private final int getParentIndex(int type) {
		if(type == WPaloEvent.SAVED_GROUP_ITEM)
			return 1;
		else if(type == WPaloEvent.SAVED_ROLE_ITEM)
			return 2;
		return 0;			
	}

	class TreeNodeBinder extends TreeBinder<TreeNode> {

		TreeNodeBinder(Tree tree, TreeStore<TreeNode> store) {
			super(tree, store);
		}

		protected TreeItem createItem(TreeNode model) {
			TreeItem item = super.createItem(model);	
			XObject xObj = model.getXObject();
			//check type:
			String type = xObj.getType();
			if (type.equals(XUser.TYPE)) {
				if ("admin".equals(((XUser) xObj).getLogin())) {
					item.setTextStyle("italic");
				}
			} else if (type.equals(XGroup.TYPE)) {
				if ("admin".equals(((XGroup) xObj).getName())) {
					item.setTextStyle("italic");
				}
			} else if (type.equals(XRole.TYPE)) {
				if ("ADMIN".equals(((XRole) xObj).getName()) ||
					"EDITOR".equals(((XRole) xObj).getName()) ||
					"VIEWER".equals(((XRole) xObj).getName()) ||
					"OWNER".equals(((XRole) xObj).getName())) {
					item.setTextStyle("italic");
				}
			}
			return item;
		}	
	}		
}

class AdminTreeModel {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private final TreeNode root;
	
	public AdminTreeModel(XUser user) {
		//children:
		XNode users = new XNode(user, XConstants.TYPE_USERS_NODE);
		users.setName(constants.users());
		users.setHasChildren(true);
		users.setId("AdminNavigatorView#UsersNode");
		
		XNode groups = new XNode(user, XConstants.TYPE_GROUPS_NODE);
		groups.setName(constants.groups());
		groups.setHasChildren(true);
		groups.setId("AdminNavigatorView#GroupsNode");
		
		XNode roles = new XNode(user, XConstants.TYPE_ROLES_NODE);
		roles.setName(constants.roles());
		roles.setHasChildren(true);
		roles.setId("AdminNavigatorView#RolesNode");
		
		
		//root node
		XNode rootNode = new XNode(null, XConstants.TYPE_ROOT_NODE);
		rootNode.setId("AdminNavigatorView#RootNode");
		rootNode.addChild(users);
		rootNode.addChild(groups);
		rootNode.addChild(roles);
		rootNode.setHasChildren(true);
		
		root = new TreeNode(null, rootNode);
	}
	
	public final TreeNode getRoot() {
		return root;
	}
}
