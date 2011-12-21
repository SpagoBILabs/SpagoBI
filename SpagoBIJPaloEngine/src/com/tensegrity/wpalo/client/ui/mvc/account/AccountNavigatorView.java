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
package com.tensegrity.wpalo.client.ui.mvc.account;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
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
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
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
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>AccountNavigatorView</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AccountNavigatorView.java,v 1.35 2010/03/02 08:59:12 PhilippBouillon Exp $
 **/
public class AccountNavigatorView extends View {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();

	private ContentPanel navigator;
	private TextToolItem addAcc, addCon, del;
	private Tree navTree;
	private TreeNode root;
	private TreeStore<TreeNode> treeStore;
	private TreeLoader<TreeNode> treeLoader;
	public static XConnection lastCreatedConnection = null;

	public AccountNavigatorView(Controller controller) {
		super(controller);		
	}

	protected void handleEvent(AppEvent<?> event) {
		switch (event.type) {
		case WPaloEvent.INIT:
		case WPaloEvent.LOGIN:
			if(event.data instanceof XUser) {
				//System.out.println("instance of XUser");
				XUser user = (XUser)event.data;
				//check if we are admin:
				if(user.isAdmin()) {
					//create ui:
					initUI(user);
				}
			} else if (event.data instanceof DisplayFlags) {
				//System.out.println("instance of display flags");
				DisplayFlags df = (DisplayFlags) event.data;
				if (!df.isHideNavigator()) {
					XUser user = df.getUser();
					//check if we are admin:
					if(user.isAdmin()) {
						//create ui:
						if(!df.isHideConnectionAccount()){
							initUI(user);
						}
					}					
				}
			}
			break;
		case WPaloEvent.EXPANDED_ACCOUNT_SECTION: //load tree data
			TreeNode node = (TreeNode) event.data;
			if (node != null) {
				treeLoader.load(node);
			}
			
			break;
		case WPaloEvent.SAVED_ACCOUNT_ITEM:
		case WPaloEvent.SAVED_CONNECTION_ITEM:
			if (treeStore == null) {
				return;
			}
			TreeNode nd = (TreeNode) event.data;
			if (nd != null) {
				if(nd.getParent() != null)
					treeStore.update(nd);
				else  {
					int index = event.type == WPaloEvent.SAVED_ACCOUNT_ITEM ? 1 : 0;
					treeStore.add(root.getChild(index), nd, false);
					root.getChild(index).add(nd);
					//shouldn't the store take care of this???
					if(nd.getParent() == null)
						nd.setParent(root.getChild(index));
				}
			}
			if (event.type == WPaloEvent.SAVED_CONNECTION_ITEM) {
				lastCreatedConnection = (XConnection) nd.getXObject();
				final XUser admin = ((Workbench)Registry.get(Workbench.ID)).getUser();  
				final String adminUserId = admin.getId();
				WPaloAdminServiceProvider.getInstance().hasAccount(admin.getSessionId(), lastCreatedConnection, new AsyncCallback<Boolean>(){
					public void onFailure(Throwable arg0) {
					}

					public void onSuccess(Boolean result) {
						if (!result) {
							AdminHelpDialog dia = new AdminHelpDialog(
								constants.adminHintAccountCreation(), adminUserId);
							dia.showDialog();
						}
					}
				});
			}
			break;
		case WPaloEvent.DELETED_ITEM:
			if (event.data instanceof TreeNode) {
				XObject target = ((TreeNode) event.data).getXObject();
				if (target != null && target instanceof XUser) {
					HashSet <String> allIds = new HashSet<String>();
					for (String accountId: ((XUser) target).getAccountIDs()) {
						allIds.add(accountId);
					}
					ArrayList <TreeNode> toBeRemoved = new ArrayList<TreeNode>();
					for (TreeNode nod: treeStore.getAllItems()) {						
						if (nod.getXObject() != null && nod.getXObject() instanceof XAccount) {
							XAccount acc = (XAccount) nod.getXObject();
							if (acc.getId() != null && allIds.contains(acc.getId())) {
								toBeRemoved.add(nod);
							}
						}										
					}
					for (TreeNode n: toBeRemoved) {
						TreeNode parent = n.getParent();
						treeStore.remove(parent, n);					
					}
					treeStore.update(root);
				} else if (target != null && target instanceof XConnection) {
					ArrayList <TreeNode> toBeRemoved = new ArrayList<TreeNode>();
					for (TreeNode nod: treeStore.getAllItems()) {						
						if (nod.getXObject() != null && nod.getXObject() instanceof XAccount) {
							XAccount acc = (XAccount) nod.getXObject();
							if (acc.getConnection().getId().equals(((XConnection) target).getId())) {
								toBeRemoved.add(nod);
							}
						}										
					}
					for (TreeNode n: toBeRemoved) {
						TreeNode parent = n.getParent();
						treeStore.remove(parent, n);					
					}
					treeStore.update(root);					
				}
			}
			break;
		}	
	}
	private final void initUI(XUser forUser) {
		navigator = new ContentPanel();
		navigator.setHeading(constants.olapConnectionsAndAccounts());
		navigator.setScrollMode(Scroll.AUTO);
		
		//da toolbar
		ToolBar toolbar = new ToolBar();
		fill(toolbar);
		navigator.setTopComponent(toolbar);

		// connect with dispatcher:
		navigator.addListener(Events.Expand, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				Dispatcher.get().dispatch(WPaloEvent.EXPANDED_ACCOUNT_SECTION);
			}
		});

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
		};
		treeLoader.addLoadListener(new LoadListener() {
			public void loaderLoadException(LoadEvent le) {
				if (le.exception instanceof SessionExpiredException)
					Callback.handle((SessionExpiredException) le.exception);
				else
					MessageBox.alert(constants.error(), constants.loadingFailed() 
							+ le.exception.getMessage(), null);
			}
		});
		navTree = createTree(treeLoader);
		navigator.add(navTree);
		
		Workbench wb = (Workbench)Registry.get(Workbench.ID);
		wb.addToViewPanel(navigator);
		
		//TODO for testing purpose:
		root = new AccountTreeModel(forUser).getRoot();
		Dispatcher.forwardEvent(WPaloEvent.EXPANDED_ACCOUNT_SECTION, root);
	}

	private final Tree createTree(TreeLoader<TreeNode> loader) {
		final Tree tree = new Tree();
		tree.setIndentWidth(6);		
		treeStore = new TreeStore<TreeNode>(loader);
		TreeBinder<TreeNode> binder = new TreeNodeBinder(tree, treeStore);
		binder.setDisplayProperty("name");
		binder.setAutoSelect(true);
		binder.setIconProvider(new ModelStringProvider<TreeNode>() {
			public String getStringValue(TreeNode model, String property) {
				String icon = null;
				String type = model.getXObject().getType();
				if(type.equals(XConstants.TYPE_ACCOUNTS_NODE)) {
					icon = "icon-folder";
				} else if(type.equals(XConstants.TYPE_CONNECTIONS_NODE)) {
					icon = "icon-folder";
				} else if(type.equals(XAccount.class.getName())) {
					icon = "icon-account";
				} else if(type.equals(XConnection.class.getName())) {
					icon = "icon-connection";
				}
				return icon;
			}
		});
		binder.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				TreeNode node = (TreeNode) se.getSelectedItem();
				if (node != null && node.getXObject() != null) {
					String type = node.getXObject().getType();
					int eventType = -1;
					if (type.equals(XAccount.class.getName()))
						eventType = WPaloEvent.EDIT_ACCOUNT_ITEM;
					else if (type.equals(XConnection.class.getName()))
						eventType = WPaloEvent.EDIT_CONNECTION_ITEM;

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
					if (type.equals(XAccount.class.getName()))
						eventType = WPaloEvent.EDIT_ACCOUNT_ITEM;
					else if (type.equals(XConnection.class.getName()))
						eventType = WPaloEvent.EDIT_CONNECTION_ITEM;

					if (eventType > -1) {
						fireEvent(new AppEvent<TreeNode>(eventType, node));
						return;
					}
				}
			}
		});
		return tree;
	}
	
	private final void fill(ToolBar toolbar) {
		//add new connection
		addCon = new TextToolItem("", "icon-connection-add");
		addCon.setToolTip(constants.addConnection());
		addCon.addSelectionListener(new SelectionListener<ComponentEvent>() {
			public final void componentSelected(ComponentEvent ce) {
			}
			public final void handleEvent(ComponentEvent be) {
				Dispatcher.get().dispatch(WPaloEvent.ADD_CONNECTION_ITEM);
			}
		});
		toolbar.add(addCon);
		//add new account
		addAcc = new TextToolItem("", "icon-account-add");
		addAcc.setToolTip(constants.addAccount());
		addAcc.addSelectionListener(new SelectionListener<ComponentEvent>() {
			public final void componentSelected(ComponentEvent ce) {
			}
			public final void handleEvent(ComponentEvent be) {					
				Dispatcher.get().dispatch(WPaloEvent.ADD_ACCOUNT_ITEM);
			}
		});
		toolbar.add(addAcc);
		
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
				
				String confirmMsg = constants.deleteMessageHeader();
				XObject xObj = node.getXObject();
				String type = xObj.getType();
				if (type.equals(XAccount.class.getName())) {
					XAccount acc = (XAccount) xObj;
					XUser usr = acc.getUser();
					String name;
					if (usr != null && usr.getName() != null && !usr.getName().isEmpty()) {
						name = usr.getName(); 
					} else {
						name = acc.getLogin();
					}					
					confirmMsg = messages.deleteAccount(name + " - " + acc.getConnection().getName());
				}
				else if (type.equals(XConnection.class.getName()))
					confirmMsg = messages.deleteConnection(((XConnection) xObj).getName());
				
				// confirm group deletion:
				doDelete(node, confirmMsg);
			}
		});
		toolbar.add(del);
	}
	
	private final void doDelete(final TreeNode node, final String confirmMsg) {
		final Workbench wb = (Workbench) Registry.get(Workbench.ID);
		XUser admin = wb.getUser();
		if (admin != null) {
			final XObject xObj = node.getXObject();
			WPaloAdminServiceProvider.getInstance().mayDelete(wb.getUser().getSessionId(), xObj, 
					new Callback <String []>(null) {
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
									MessageBox.confirm(constants.proceedWithDelete(), message,  new Listener <WindowEvent>(){
										public void handleEvent(WindowEvent be) {
											if (be.buttonClicked.getId().equalsIgnoreCase(Dialog.YES)) {
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
}

class TreeNodeBinder extends TreeBinder<TreeNode> {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	TreeNodeBinder(Tree tree, TreeStore<TreeNode> store) {
		super(tree, store);
	}

	protected TreeItem createItem(TreeNode model) {
		TreeItem item = super.createItem(model);	
		XObject xObj = model.getXObject();
		//check type:
		String name = xObj.getName();
		if(name == null || name.equals(""))
			item.setText(constants.notDefined());
		//set icon:
		String type = xObj.getType();
		if(type.equals(XAccount.TYPE)) {
			XAccount xAcc = (XAccount) xObj;
			item.setText(xAcc.getUser().getLogin()+" - "+xAcc.getConnection().getName());
			item.setIconStyle("icon-account");
		}
		return item;
	}	
}

class AccountTreeModel {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private final TreeNode root;
	
	public AccountTreeModel(XUser user) {
		//children:
		XNode accounts = new XNode(user, XConstants.TYPE_ACCOUNTS_NODE);
		accounts.setName(constants.accounts());
		accounts.setHasChildren(true);
		accounts.setId("AccountNavigatorView#AccountsNode");
		
		XNode connections = new XNode(user, XConstants.TYPE_CONNECTIONS_NODE);
		connections.setName(constants.connections());
		connections.setHasChildren(true);
		connections.setId("AccountNavigatorView#ConnectionsNode");
		
		//root node
		XNode rootNode = new XNode(null, XConstants.TYPE_ROOT_NODE);
		rootNode.setId("AccountNavigatorView#RootNode");
		rootNode.addChild(connections);
		rootNode.addChild(accounts);		
		rootNode.setHasChildren(true);
		
		root = new TreeNode(null, rootNode);
	}
	
	public final TreeNode getRoot() {
		return root;
	}
	
}