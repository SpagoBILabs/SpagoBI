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

import java.util.HashSet;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.services.admin.WPaloAdminServiceProvider;
import com.tensegrity.wpalo.client.ui.editor.AbstractTabEditor;
import com.tensegrity.wpalo.client.ui.editor.IEditor;
import com.tensegrity.wpalo.client.ui.mvc.admin.AdminNavigatorView;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.EnhancedComboBox;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;
import com.tensegrity.wpalo.client.ui.widgets.EditorTab;

/**
 * <code>AccountEditor</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AccountEditor.java,v 1.22 2010/04/15 09:55:22 PhilippBouillon Exp $
 **/
public class AccountEditor extends AbstractTabEditor {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();
	
	AccountEditor() {
	}

	public EditorTab[] getEditorTabs() {
		return new EditorTab[] {
			new AccountPropertiesTab(this),
		};
	}

	public final String getTitle(XObject input) {
		if (input instanceof XAccount) {
			XAccount account = (XAccount) input;
			String name = account.getName();
			if (name != null && !name.equals(""))
				return messages.account(name);
		}
		return constants.newAccount();
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected final int getSaveType() {
		return WPaloEvent.SAVED_ACCOUNT_ITEM;
	}

	public void setTextCursor() {		
		((AccountPropertiesTab) tabFolder.getItem(0)).login.focus();
	}
	
	public void prefillFields() {		
		AccountPropertiesTab tab = ((AccountPropertiesTab) tabFolder.getItem(0));
		tab.prefillCon = AccountNavigatorView.lastCreatedConnection;
		tab.prefillUser = AdminNavigatorView.lastCreatedUser;
		tab.doSetC = true;
		tab.doSetU = true;
	}
}

class AccountPropertiesTab extends EditorTab {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private EnhancedComboBox<XObjectWrapper> connCombo;
	private ListStore<XObjectWrapper> connStore;
	private EnhancedComboBox<XObjectWrapper> usersCombo;
	private ListStore<XObjectWrapper> usersStore;
	TextField<String> login;
	private TextField<String> password;
	private IEditor editor;
	private SelectionChangedListener<XObjectWrapper> selectionListener;
	private SelectionChangedListener<XObjectWrapper> userSelectionListener;
	private XAccount account = null;
	
	XConnection prefillCon = null;
	XUser prefillUser = null;
	boolean doSetC = false;
	boolean doSetU = false;
	
	public AccountPropertiesTab(IEditor editor) {
		super(constants.general());
		this.editor = editor;
		setText(constants.general());
		setIconStyle("icon-account");
		setClosable(false);
		setScrollMode(Scroll.AUTO);
		add(createPropertiesPanel());
	}

	public final boolean save(XObject input) {
		if(input instanceof XAccount) {
			XConnection conn = getConnection();
			XUser user = getUser();
			if(user == null || conn == null)
				return false;			
			XAccount account = (XAccount) input;
			account.setUser(user);
			account.setConnection(conn);			
			account.setLogin(login.getValue());
			account.setPassword(password.getValue());
		}
		return true;
	}
	public void saveAs(String name, XObject input) {		
	}

	public void set(XObject input) {
		if(input instanceof XAccount) {
			XAccount account = (XAccount) input;			
			this.account = account;
			fillUsers(account.getUser());
			XUser forUser = null;
			if (usersCombo.getValue() != null) {
				forUser = (XUser) usersCombo.getValue().getXObject();
			}
			fillConnections(account.getConnection(), forUser);
			
			login.setValue(account.getLogin());
			password.setValue(account.getPassword());	
			
		}
	}

	private final ContentPanel createPropertiesPanel() {
		FormPanel panel = new FormPanel();
		panel.setHeaderVisible(false);
		panel.setStyleAttribute("padding", "20");		

		KeyListener keyListener = new KeyListener() {
			public void componentKeyUp(ComponentEvent event) {
				editor.markDirty();
			}
		};
		selectionListener = 
				new SelectionChangedListener<XObjectWrapper>() {
			public void selectionChanged(
					SelectionChangedEvent<XObjectWrapper> se) {
				editor.markDirty();
			}
		};
		userSelectionListener = 
			new SelectionChangedListener<XObjectWrapper>() {
			public void selectionChanged(
				SelectionChangedEvent<XObjectWrapper> se) {
				editor.markDirty();
				if (account != null && usersCombo != null) {
					XUser forUser = null;
					if (usersCombo.getValue() != null) {
						forUser = (XUser) usersCombo.getValue().getXObject();
					}
					fillConnections(account.getConnection(), forUser);
				}
			}
		};
		
		usersCombo = new EnhancedComboBox<XObjectWrapper>();
		usersCombo.setEditable(false);
		usersCombo.setFieldLabel(constants.user());
		usersCombo.setDisplayField("name");
		usersCombo.setEmptyText(constants.chooseUser());
		usersStore = new ListStore<XObjectWrapper>();
		usersCombo.setStore(usersStore);
		usersCombo.addSelectionChangedListener(userSelectionListener);
		panel.add(usersCombo);

		connCombo = new EnhancedComboBox<XObjectWrapper>();
		connCombo.setEditable(false);
		connCombo.setFieldLabel(constants.connection());  
		connCombo.setDisplayField("name");
		connCombo.setEmptyText(constants.chooseConnectionType());
		connStore = new ListStore<XObjectWrapper>();
		connCombo.setStore(connStore);
		connCombo.addSelectionChangedListener(selectionListener);
		panel.add(connCombo);  

		login = new TextField<String>();
		login.setFieldLabel(constants.login());
		login.setEmptyText(constants.enterLoginName());
		login.setAllowBlank(false);
		login.setMinLength(2);
		login.addKeyListener(keyListener);
		panel.add(login);

		password = new TextField<String>();
		password.setFieldLabel(constants.password());
		password.setEmptyText(constants.enterPassword());
		password.setAllowBlank(false);
		password.setMinLength(2);
		password.addKeyListener(keyListener);
//		password.setPassword(true);
		panel.add(password);

		return panel;
	}
	
	private final void fillConnections(final XConnection connection, final XUser forUser) {
		connStore.removeAll();
		connCombo.setValue(null);
		if (forUser == null) {
			return;
		}
		Workbench wb = (Workbench) Registry.get(Workbench.ID);
		final XUser admin = wb.getUser();
		if (admin != null) {
			final String sessionId = wb.getUser().getSessionId();
			WPaloAdminServiceProvider.getInstance().getAccounts(sessionId, forUser,
					new Callback <XAccount []>(constants.loadingAllConnectionsFailed()){
						public void onSuccess(final XAccount[] accounts) {
							WPaloAdminServiceProvider.getInstance().getConnections(sessionId, admin,
									new Callback<XConnection[]>(constants.loadingAllConnectionsFailed()) {
										public void onSuccess(XConnection[] connections) {
											XObjectWrapper selection = null;
											boolean disableSelectionListener = false;
											HashSet <String> allUsedConnectionIds = new HashSet<String>();
											for (XAccount a: accounts) {
												XConnection c = a.getConnection();
												if (account != null) {
													if (c != null && !a.getId().equals(account.getId())) {
														allUsedConnectionIds.add(c.getId());
													}
												} else {
													if (c != null) {
														allUsedConnectionIds.add(c.getId());
													}
												}
											}
											for (XConnection conn : connections) {
												if (allUsedConnectionIds.contains(conn.getId())) {
													continue;
												}
												XObjectWrapper w = new XObjectWrapper(conn);
												connStore.add(w);
												if(conn.equals(connection)) {
													disableSelectionListener = true;
													selection = w;
												}
												if (selection == null && prefillCon != null && conn.equals(prefillCon)) {
													selection = w;
												}
											}
											if(selection != null) {
												if (disableSelectionListener) {
													if (selectionListener != null) {
														connCombo.removeSelectionListener(selectionListener);
													}
												}
												connCombo.setValue(selection);
												if (disableSelectionListener) {
													if (selectionListener != null) {
														connCombo.addSelectionChangedListener(selectionListener);
													}
												}
											}
											if (selection == null && !connStore.getModels().isEmpty() && doSetC) {
												selection = connStore.getModels().get(0);
												connCombo.setValue(selection);
											}
											doSetC = false;
											prefillCon = null;
										}
									});							
						}
					});
					
		}
	}
	private final void fillUsers(final XUser user) {
		usersStore.removeAll();
		String sessionId = ((Workbench) Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloAdminServiceProvider.getInstance().getUsers(sessionId, 
				new Callback<XUser[]>(constants.loadingAllUsersFailed()) {
					public void onSuccess(XUser[] users) {
						XObjectWrapper selection = null;
						boolean disableSelectionListener = false;
						for (XUser xUser : users) {
							XObjectWrapper w = new XObjectWrapper(xUser);
							usersStore.add(w);
							if (xUser.equals(user)) {
								disableSelectionListener = true;
								selection = w;
							}
							if (selection == null && prefillUser != null && xUser.equals(prefillUser)) {
								selection = w;
							}
						}
						if (selection != null) {
							if (disableSelectionListener) {
								if (selectionListener != null) {
									usersCombo.removeSelectionListener(selectionListener);
								}
							}
							usersCombo.setValue(selection);
							if (disableSelectionListener) {
								if (selectionListener != null) {
									usersCombo.addSelectionChangedListener(selectionListener);
								}
							}
						}
						if (selection == null && !usersStore.getModels().isEmpty() && doSetU) {
							selection = usersStore.getModels().get(0);
							usersCombo.setValue(selection);
						}
						doSetU = false;						
						prefillUser = null;
					}
				});
	}
	
	private final XConnection getConnection() {
		XConnection xConn = null;
		List<XObjectWrapper> selection = connCombo.getSelection();
		if(!selection.isEmpty()) {
			XObjectWrapper w = selection.get(0);
			xConn = (XConnection)w.getXObject();
		}
		return xConn;
	}
	private final XUser getUser() {
		XUser xUser = null;
		List<XObjectWrapper> selection = usersCombo.getSelection();
		if(!selection.isEmpty()) {
			XObjectWrapper w = selection.get(0);
			xUser = (XUser)w.getXObject();
		}
		return xUser;
	}

}

class XObjectWrapper extends BaseModel {
	
	private static final long serialVersionUID = 5073581456853340071L;

	private final XObject xObj;
	public XObjectWrapper(XObject xObj) {
		this.xObj = xObj;
		set("name", xObj.getName());
	}
	
	public final XObject getXObject() {
		return xObj;
	}
	
}