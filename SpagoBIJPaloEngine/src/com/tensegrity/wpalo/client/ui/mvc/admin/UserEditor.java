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
package com.tensegrity.wpalo.client.ui.mvc.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.TableEvent;
import com.extjs.gxt.ui.client.event.TableListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.table.Table;
import com.extjs.gxt.ui.client.widget.table.TableColumn;
import com.extjs.gxt.ui.client.widget.table.TableColumnModel;
import com.extjs.gxt.ui.client.widget.table.TableItem;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
import com.tensegrity.palo.gwt.core.client.models.admin.XGroup;
import com.tensegrity.palo.gwt.core.client.models.admin.XRole;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.services.admin.WPaloAdminServiceProvider;
import com.tensegrity.wpalo.client.ui.editor.AbstractTabEditor;
import com.tensegrity.wpalo.client.ui.editor.IEditor;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;
import com.tensegrity.wpalo.client.ui.widgets.CheckBoxTableColumn;
import com.tensegrity.wpalo.client.ui.widgets.EditorTab;
import com.tensegrity.wpalo.client.ui.widgets.SeparatorField;

/**
 * <code>UserEditor</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: UserEditor.java,v 1.30 2010/04/15 09:55:22 PhilippBouillon Exp $
 **/
public class UserEditor extends AbstractTabEditor {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();

	final AdminController adminController;
	boolean needsUpdate = false;
	
	public UserEditor(AdminController admController) {
		adminController = admController;
	}
	
	public boolean needsUpdate() {
		return needsUpdate;
	}
	
	public void clearNeedsUpdate() {
		needsUpdate = false;
	}
	
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public final EditorTab[] getEditorTabs() {
		return new EditorTab[] { 
				new UserPropertiesTab(this), 
				new AccountTab(this),
				new GroupTab(this), 
				new RolesTab(this) };
	}

	public String getTitle(XObject input) {
		if (input instanceof XUser) {
			XUser user = (XUser) input;
			String login = user.getLogin();
			if(login != null && !login.equals(""))
				return messages.user(login); 
		}
		return constants.newUser(); 
	}
	
	protected final int getSaveType() {
		return WPaloEvent.SAVED_USER_ITEM;
	}
	
	public void setTextCursor() {		
		((UserPropertiesTab) tabFolder.getItem(0)).login.focus();
	}	
}

class UserPropertiesTab extends EditorTab {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	//properties:
	private TextField<String> firstname;
	private TextField<String> lastname;
	TextField<String> login;
	private TextField<String> password;
	private IEditor editor;

	UserPropertiesTab(IEditor editor) {
		super(constants.general());
		this.editor = editor;
		setText(constants.general());
		setIconStyle("icon-user");
		setClosable(false);
		setScrollMode(Scroll.AUTO);
		add(createPropertiesPanel());
	}

	public final boolean save(XObject input) {
		if (input instanceof XUser) {
			XUser user = (XUser) input;
			user.setFirstname(firstname.getValue());
			user.setLastname(lastname.getValue());
			user.setLogin(login.getValue());
			String passw = password.getValue();
			if(passw != null && !passw.equals(""))
				user.setPassword(password.getValue());
			else
				user.setPassword(null);
		}
		return true;
	}
	public void saveAs(String name, XObject input) {		
	}

	public void set(XObject input) {
		if (input instanceof XUser) {
			XUser user = (XUser) input;
			firstname.setValue(user.getFirstname());
			lastname.setValue(user.getLastname());
			login.setValue(user.getLogin());
			if (login.getValue() != null && login.getValue().equals("admin")) {
				login.setEnabled(false);
			} else {
				login.setEnabled(true);
			}
			if (user.getPassword() != null && !user.getPassword().isEmpty()) {
				password.setValue(null);
				password.setEmptyText(constants.hidden());
			} else {
				password.setValue(null);
				password.setEmptyText(constants.enterPassword());
			}
		}
	}

	private final ContentPanel createPropertiesPanel() {
		FormPanel panel = new FormPanel();
		panel.setHeaderVisible(false);
		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		panel.setStyleAttribute("padding", "20");
		
		KeyListener keyListener = new KeyListener() {
			public void componentKeyUp(ComponentEvent event) {
				editor.markDirty();
			}
		};

		firstname = new TextField<String>();
		firstname.setFieldLabel(constants.firstName());
		firstname.setEmptyText(constants.enterFirstName());
		firstname.setAllowBlank(true);
		firstname.addKeyListener(keyListener);
		panel.add(firstname);

		lastname = new TextField<String>();
		lastname.setFieldLabel(constants.lastName());
		lastname.setEmptyText(constants.enterLastName());
		lastname.setAllowBlank(true);
		lastname.addKeyListener(keyListener);
		panel.add(lastname);
		
		panel.add(new SeparatorField());

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
//		password.setPassword(true);
		password.setAllowBlank(true);
//		password.setMinLength(2);
		password.addKeyListener(keyListener);
		panel.add(password);
		
		return panel;
	}
}

class GroupTab extends EditorTab {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private static final String MEMBER_DATA = "com.tensegity.wpalo.usereditor.groupstab";
	
	//the groups table with 3 columns: active, name, description
	private Table groupTable;
	private final IEditor editor;
	
	GroupTab(final IEditor editor) {
		super(constants.groups());
		setText(constants.groups());
		setIconStyle("icon-group");
		setClosable(false);
		this.editor = editor;

		// create groups table
		List<TableColumn> columns = new ArrayList<TableColumn>();
		TableColumn cbox = new CheckBoxTableColumn("mem_check");
		columns.add(cbox);

		TableColumn col = new TableColumn("Group", constants.group(), 150);
		col.setMinWidth(75);
		col.setMaxWidth(300);
		columns.add(col);
	
		col = new TableColumn("Roles", constants.roles(), 320);
		col.setAlignment(HorizontalAlignment.LEFT);
		col.setMaxWidth(480);
		columns.add(col);
		
		col = new TableColumn("Description", constants.description(), 800);
		col.setMaxWidth(800);
		col.setAlignment(HorizontalAlignment.LEFT);
		
		columns.add(col);	
		TableColumnModel cm = new TableColumnModel(columns);

		groupTable = new Table(cm);
		groupTable.setSelectionMode(SelectionMode.SINGLE);
		groupTable.setHorizontalScroll(true);
		groupTable.setAutoHeight(true);
		// disable to allow CheckBox widget!
		groupTable.setBulkRender(false);
		
		groupTable.addTableListener(new TableListener() {
			  public void tableCellClick(TableEvent te) {
				  editor.markDirty();
			  }
		});

		add(groupTable);		
	}
	
	public final boolean save(XObject input) {
		if (input instanceof XUser) {
			XUser user = (XUser) input;
			user.clearGroups();
			if (user.getId() == null) {
				((UserEditor) editor).needsUpdate = true;
				return true;
			}
			for(XGroup group: getAllGroups()) {
				if (isSelected(group)) {
					group.addUserID(user.getId());
					user.addGroupID(group.getId());					
				} else {
					group.removeUserID(user.getId());
					user.removeGroupID(group.getId());
				}
				((UserEditor) editor).adminController.updateGroup(group);
			}
		}		
		return true;
	}
	
	public void saveAs(String name, XObject input) {		
	}

	public void set(XObject input) {
		if (input instanceof XUser) {
			groupTable.removeAll();
			setGroups((XUser) input);
		}
	}
	
	private final String translateDescription(String s) {
		if (s == null) {
			return null;
		}
		if (s.equals("Grants the right to view & edit administration area and modify & share all existing views (System)")) {
			return constants.grantAdmin();
		} else if (s.equals("Grants the right to share views created by this user")) {
			return constants.grantShare();
		} else if (s.equals("Grants the right to modify views shared by other users (System)")) {
			return constants.grantModify();
		} else if (s.equals("Grants the right to create and modify own views")) {
			return constants.grantCreate();
		} else if (s.equals("Grants the right to see views shared by other users (System)")) {
			return constants.grantView();
		} else if (s.equals("Grants the right to create views and edit these views (System)")) {
			return constants.grantCreateEdit();
		} else if (s.equals("Grants the right to create, modify and publish own views")) {
			return constants.grantPublish();
		}
		return s;
	}
	
	private final void setGroups(final XUser user) {
		final String sessionId = ((Workbench) Registry.get(Workbench.ID)).getUser().getSessionId();		
		WPaloAdminServiceProvider.getInstance().getGroups(sessionId,
				new Callback<XGroup[]>(constants.loadingAllGroupsFailed()) {
					public void onSuccess(final XGroup[] groups) {
						WPaloAdminServiceProvider.getInstance().getGroups(sessionId, user, 
								new Callback<XGroup[]>(constants.loadingAllGroupsFailed()) {
									public void onSuccess(XGroup[] userGroups) {
										HashSet <String> userGroupSet = new HashSet<String>();
										for (XGroup xg: userGroups) {
											userGroupSet.add(xg.getId());
										}
										for (XGroup group : groups) {
											StringBuffer roles = new StringBuffer();
											String [] rNames = group.getRoleNames();
											for (int i = 0, n = rNames.length; i < n; i++) {
												roles.append(rNames[i]);
												if (i < (n - 1)) {
													roles.append(", ");
												}
											}
											TableItem item = new TableItem(new Object[] {
													userGroupSet.contains(group.getId()), 
													group.getName(), roles.toString(), translateDescription(group.getDescription())});
											groupTable.add(item);
											item.setData(MEMBER_DATA, group);
										}
									}									
								});						
					}
				});
	}
	
	private final XGroup[] getAllGroups() {
		List<XGroup> groups = new ArrayList<XGroup>();
		for(TableItem item : groupTable.getItems()) {
			groups.add((XGroup)item.getData(MEMBER_DATA));
		}			
		return groups.toArray(new XGroup[0]);
	}
	
	private final boolean isSelected(XGroup group) {
		for(TableItem item : groupTable.getItems()) {
			XGroup xg = (XGroup) item.getData(MEMBER_DATA);
			if (xg.equals(group)) {
				boolean selected = false;
				Object colVal = item.getValue(0);
				if(colVal instanceof CheckBox)
					selected = ((CheckBox)colVal).getValue();
				else if(colVal instanceof Boolean)
					selected = ((Boolean)colVal).booleanValue();
				return selected;
			}			
		}			
		return false;		
	}
}

class AccountTab extends EditorTab {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	//the accounts table with 3 columns: name, server, description
	private Table accountTable;
	
	AccountTab(final IEditor editor) {
		super(constants.accounts());
		setText(constants.accounts());
		setIconStyle("icon-account");
		setClosable(false);

		List<TableColumn> columns = new ArrayList<TableColumn>();

		TableColumn col = new TableColumn("Login", constants.login(), 150);
		columns.add(col);

		col = new TableColumn("Connection", constants.connection(), 150);
		columns.add(col);

		col = new TableColumn("Host", constants.host(), 150);		
		col.setAlignment(HorizontalAlignment.LEFT);
		columns.add(col);
		
		col = new TableColumn("Service", constants.service(), 150);
		columns.add(col);
		
		col = new TableColumn("Type", constants.type(), 150);
		col.setMaxWidth(400);
		columns.add(col);

		TableColumnModel cm = new TableColumnModel(columns);

		accountTable = new Table(cm);
		accountTable.setHorizontalScroll(true);
		accountTable.setAutoHeight(true);
//		accountTable.setBulkRender(false);

		add(accountTable);
	}

	public final boolean save(XObject input) {
		return true;
	}
	public void saveAs(String name, XObject input) {		
	}

	public void set(XObject input) {
		if (input instanceof XUser) {
			accountTable.removeAll();
			setAccounts((XUser) input);
		}
	}
	private final void setAccounts(XUser user) {
		String sessionId = ((Workbench) Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloAdminServiceProvider.getInstance().getAccounts(sessionId, user,
				new Callback<XAccount[]>(constants.loadingAllAccountsFailed()) {
					public void onSuccess(XAccount[] accounts) {
						int index = 0;
						for (XAccount account : accounts) {
							XConnection connection = account.getConnection();
							String type = connection.getConnectionType() == 
								XConnection.TYPE_HTTP ? "Palo" : "XMLA";
							accountTable.insert(new TableItem(new Object[] {
									account.getLogin(), connection.getName(),
									connection.getHost(), connection.getService(),
									type}), index++);
						}
					}
				});
	}
}

class RolesTab extends EditorTab {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private static final String ROLE_DATA = "com.tensegity.wpalo.rolestab";
	
	//the roles table with 3 columns: active, name, description	
	private Table rolesTable;
	private XUser currentUser;
	
	RolesTab(final IEditor editor) {
		super(constants.roles());
		setText(constants.roles());
		setIconStyle("icon-role2");
		setClosable(false);
		// create roles table
		List<TableColumn> columns = new ArrayList<TableColumn>();
		TableColumn cbox = new CheckBoxTableColumn("role_check");
		columns.add(cbox);
		TableColumn col = new TableColumn("Role", constants.role(), 150);
		col.setMinWidth(75);
		col.setMaxWidth(300);
		columns.add(col);
		col = new TableColumn("Right", constants.right(), 100);
		col.setAlignment(HorizontalAlignment.LEFT);
		columns.add(col);
		col = new TableColumn("Description", constants.description(), 800);
		col.setMaxWidth(800);
		col.setAlignment(HorizontalAlignment.LEFT);
		columns.add(col);

		TableColumnModel cm = new TableColumnModel(columns);

		rolesTable = new Table(cm);
		rolesTable.setSelectionMode(SelectionMode.MULTI);
		rolesTable.setHorizontalScroll(true);
		rolesTable.setBulkRender(false);
		rolesTable.setAutoHeight(true);
		
		rolesTable.addTableListener(new TableListener() {
			  public void tableCellClick(TableEvent te) {
				  editor.markDirty();
			  }
		});

		add(rolesTable);
	}

	public final boolean save(XObject input) {
		if (input instanceof XUser) {
			XUser user = (XUser) input;
			user.clearRoles();
			user.clearRoleNames();
			XRole[] selecteRoles = getSelectedRoles();
			for(XRole role : selecteRoles) {
				user.addRoleID(role.getId());
				user.addRoleName(role.getId(), role.getName());
			}
		}
		return true;
	}
	public void saveAs(String name, XObject input) {		
	}

	public void set(XObject input) {
		if (input instanceof XUser) {
			XUser user = (XUser) input;
			currentUser = user;
			boolean isAdm = currentUser != null && currentUser.getLogin() != null && currentUser.getLogin().equals("admin"); 
			((CheckBoxTableColumn) rolesTable.getColumnModel().getColumn(0)).setEnabled(!isAdm);		
			rolesTable.removeAll();
			List<String> roleIDs = Arrays.asList(user.getRoleIDs());
			setRoles(roleIDs);
		} else {
			currentUser = null;
		}
	}

	private final String translateDescription(String s) {
		if (s == null) {
			return null;
		}
		if (s.equals("Grants the right to view & edit administration area and modify & share all existing views (System)")) {
			return constants.grantAdmin();
		} else if (s.equals("Grants the right to share views created by this user")) {
			return constants.grantShare();
		} else if (s.equals("Grants the right to modify views shared by other users (System)")) {
			return constants.grantModify();
		} else if (s.equals("Grants the right to create and modify own views")) {
			return constants.grantCreate();
		} else if (s.equals("Grants the right to see views shared by other users (System)")) {
			return constants.grantView();
		} else if (s.equals("Grants the right to create views and edit these views (System)")) {
			return constants.grantCreateEdit();
		} else if (s.equals("Grants the right to create, modify and publish own views")) {
			return constants.grantPublish();
		}
		return s;
	}
	
	private final void setRoles(final List<String> roleIDs) {
		Workbench wb = (Workbench) Registry.get(Workbench.ID);
		XUser admin = wb.getUser();
		if (admin != null) {
			String sessionId = ((Workbench) Registry.get(Workbench.ID)).getUser().getSessionId();
			WPaloAdminServiceProvider.getInstance().getRoles(sessionId, admin,
					new Callback<XRole[]>(constants.loadingAllRolesFailed()) {
						public void onSuccess(XRole[] roles) {
							for (XRole role : roles) {
								boolean check = roleIDs.contains(role.getId());
								String rightName = constants.none();
								String p = role.getPermission();
								if (p.equals("R")) rightName = constants.read();
								else if (p.equals("W")) rightName = constants.write();
								else if (p.equals("D")) rightName = constants.delete();
								else if (p.equals("C")) rightName = constants.create();
								else if (p.equals("G")) rightName = constants.grant();								
								String desc = translateDescription(role.getDescription());
								TableItem item = new TableItem(new Object[] {
										check, role.getName(), rightName, 
										desc });
								item.setData(ROLE_DATA, role);
								rolesTable.add(item);
							}
						}
					});
		}
	}
	private final XRole[] getSelectedRoles() {
		// collect all selected roles:
		List<XRole> roles = new ArrayList<XRole>();
		for (TableItem item : rolesTable.getItems()) {
			boolean selected = false;
			Object colVal = item.getValue(0);
			// bug in gxt???
			if (colVal instanceof CheckBox) {
				selected = ((CheckBox) colVal).getValue();
			}
			else if (colVal instanceof Boolean)
				selected = ((Boolean) colVal).booleanValue();
			if (selected)
				roles.add((XRole) item.getData(ROLE_DATA));
		}
		return roles.toArray(new XRole[0]);
	}
}
