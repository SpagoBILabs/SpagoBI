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
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.table.Table;
import com.extjs.gxt.ui.client.widget.table.TableColumn;
import com.extjs.gxt.ui.client.widget.table.TableColumnModel;
import com.extjs.gxt.ui.client.widget.table.TableItem;
import com.tensegrity.palo.gwt.core.client.models.XObject;
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

/**
 * <code>GroupEditor</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: GroupEditor.java,v 1.30 2010/04/15 09:55:22 PhilippBouillon Exp $
 **/
public class GroupEditor extends AbstractTabEditor {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();

	final AdminController adminController;
	boolean needsUpdate = false;
	
	public GroupEditor(AdminController adminController) {
		this.adminController = adminController;
	}
	
	public boolean needsUpdate() {
		return needsUpdate;
	}
	
	public void clearNeedsUpdate() {
		needsUpdate = false;
	}
	
	public final EditorTab[] getEditorTabs() {
		return new EditorTab[] { 
				new GroupPropertiesTab(this),
				new GroupMembersTab(this), 
				new GroupRolesTab(this) };
	}

	public String getTitle(XObject input) {
		if (input instanceof XGroup) {
			XGroup group = (XGroup) input;
			String name = group.getName();
			if (name != null && !name.equals(""))
				return messages.group(name); 
		}
		return constants.newGroup(); 
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected final int getSaveType() {
		return WPaloEvent.SAVED_GROUP_ITEM;
	}

	public void setTextCursor() {		
		((GroupPropertiesTab) tabFolder.getItem(0)).name.focus();
	}	
}

class GroupPropertiesTab extends EditorTab {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();

	//properties:
	TextField<String> name;
	private TextArea description;
	private IEditor editor;

	GroupPropertiesTab(IEditor editor) {
		super(constants.general());
		this.editor = editor;
		setText(constants.general());
		setIconStyle("icon-group");
		setClosable(false);
		setScrollMode(Scroll.AUTO);
		add(createPropertiesContent());
	}

	public final boolean save(XObject input) {
		if (input instanceof XGroup) {
			XGroup group = (XGroup) input;
			group.setName(name.getValue());
			group.setDescription(description.getValue());
		}
		return true;
	}
	public void saveAs(String name, XObject input) {		
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
	
	public void set(XObject input) {
		if (input instanceof XGroup) {
			XGroup group = (XGroup) input;
			name.setValue(group.getName());
			description.setValue(translateDescription(group.getDescription()));
			if (group != null && group.getName() != null && (group.getName().equals("admin") ||
					group.getName().equals("editor") ||
					group.getName().equals("creator") ||
					group.getName().equals("viewer") ||
					group.getName().equals("poweruser") ||
					group.getName().equals("publisher"))) {
					name.setEnabled(false);
					description.setEnabled(false);
				} else {
					name.setEnabled(true);
					description.setEnabled(true);
				}
			
		}
	}

	private final ContentPanel createPropertiesContent() {
		FormPanel panel = new FormPanel();
		panel.setHeaderVisible(false);
		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		panel.setStyleAttribute("padding", "20");
		
		KeyListener keyListener = new KeyListener() {
			public void componentKeyUp(ComponentEvent event) {
				editor.markDirty();
			}
		};

		name = new TextField<String>();
		name.setFieldLabel(constants.name());
		name.setEmptyText(constants.groupName());
		name.setAllowBlank(false);
		name.setMinLength(2);		
		name.addKeyListener(keyListener);
		name.setStyleAttribute("marginTop", "5");
		name.setStyleAttribute("marginBottom", "5");
		panel.add(name);

		description = new TextArea();
		description.setPreventScrollbars(true);
		description.setFieldLabel(constants.description());
		description.addKeyListener(keyListener);
		description.setStyleAttribute("marginTop", "5");
		description.setStyleAttribute("marginBottom", "5");
		panel.add(description);

		return panel;
	}
}

class GroupMembersTab extends EditorTab {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private static final String MEMBER_DATA = "com.tensegity.wpalo.groupeditor.memberstab"; 
	//properties:
	private Table membersTable;
	private final IEditor editor;
	
	public GroupMembersTab(final IEditor editor) {
		super(constants.members());
		this.editor = editor;
		setText(constants.members());
		setIconStyle("icon-user");
		setClosable(false);

		// create groups table
		List<TableColumn> columns = new ArrayList<TableColumn>();
		TableColumn cbox = new CheckBoxTableColumn("mem_check");
		columns.add(cbox);
		TableColumn col = new TableColumn("User", constants.user(), 200);
		col.setMinWidth(75);
		col.setMaxWidth(400);
		columns.add(col);

		TableColumnModel cm = new TableColumnModel(columns);

		membersTable = new Table(cm);
		membersTable.setSelectionMode(SelectionMode.SINGLE);
		membersTable.setHorizontalScroll(true);
		// disable to allow CheckBox widget!
		membersTable.setBulkRender(false);
		membersTable.setAutoHeight(true);
		
		membersTable.addTableListener(new TableListener() {
			  public void tableCellClick(TableEvent te) {
				  editor.markDirty();
			  }
		});

		add(membersTable);
	}

	public final boolean save(XObject input) {
		if (input instanceof XGroup) {
			XGroup group = (XGroup) input;
			group.clearUsers();
			if (group.getId() == null) {
				((GroupEditor) editor).needsUpdate = true;
				return true;
			}	
			for(XUser user : getAllUsers()) {
				if (isSelected(user)) {
					group.addUserID(user.getId());
					user.addGroupID(group.getId());
				} else {
					group.removeUserID(user.getId());
					user.removeGroupID(group.getId());
				}
				((GroupEditor) editor).adminController.updateUser(user);
			}
		}
		return true;
	}
	
	public void saveAs(String name, XObject input) {		
	}

	public void set(XObject input) {
		if (input instanceof XGroup) {
			XGroup group = (XGroup) input;
			membersTable.removeAll();
			List<String> userIDs = Arrays.asList(group.getUserIDs());
			setUsers(userIDs);
		}
	}

	private final void setUsers(final List<String> userIDs) {
		Workbench wb = (Workbench) Registry.get(Workbench.ID);
		// XUser admin = wb.getUser();
		// if (admin != null) {
		WPaloAdminServiceProvider.getInstance().getUsers(wb.getUser().getSessionId(),
				new Callback<XUser[]>(constants.loadingAllUsersFailed()) {
					public void onSuccess(XUser[] users) {
						for (XUser user : users) {
							boolean check = userIDs.contains(user.getId());
							TableItem item = new TableItem(new Object[] {
									check, user.getLogin() });
							item.setData(MEMBER_DATA, user);
							membersTable.add(item);
						}
					}
				});
		// }
	}
	
	private final XUser[] getAllUsers() {
		List<XUser> users = new ArrayList<XUser>();
		for(TableItem item : membersTable.getItems()) {
			users.add((XUser)item.getData(MEMBER_DATA));
		}			
		return users.toArray(new XUser[0]);
	}
	
	private final boolean isSelected(XUser user) {
		for(TableItem item : membersTable.getItems()) {
			XUser xu = (XUser) item.getData(MEMBER_DATA);
			if (xu.equals(user)) {
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

class GroupRolesTab extends EditorTab {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private static final String ROLE_DATA = "com.tensegity.wpalo.groupeditor.rolestab"; 
	//properties:
	private Table rolesTable;

	GroupRolesTab(final IEditor editor) {
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
		if (input instanceof XGroup) {
			XGroup group = (XGroup) input;
			group.clearRoles();
			for(XRole role : getSelectedRoles())
				group.addRoleID(role.getId());
		}
		return true;
	}
	public void saveAs(String name, XObject input) {		
	}

	public void set(XObject input) {
		if (input instanceof XGroup) {
			XGroup group = (XGroup) input;
			rolesTable.removeAll();
			boolean isAdm = group != null && group.getName() != null && group.getName().equals("admin"); 
			((CheckBoxTableColumn) rolesTable.getColumnModel().getColumn(0)).setEnabled(!isAdm);		
			List<String> roleIDs = Arrays.asList(group.getRoleIDs());
			setRoles(roleIDs);			
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
			WPaloAdminServiceProvider.getInstance().getRoles(admin.getSessionId(), admin,
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
								TableItem item = new TableItem(new Object[] {
										check, role.getName(),
										rightName, translateDescription(role.getDescription()) });
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
			if (colVal instanceof CheckBox)
				selected = ((CheckBox) colVal).getValue();
			else if (colVal instanceof Boolean)
				selected = ((Boolean) colVal).booleanValue();
			if (selected)
				roles.add((XRole) item.getData(ROLE_DATA));
		}
		return roles.toArray(new XRole[0]);
	}
}