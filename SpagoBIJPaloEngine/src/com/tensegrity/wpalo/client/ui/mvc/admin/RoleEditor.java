/*
*
* @file RoleEditor.java
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
* @version $Id: RoleEditor.java,v 1.31 2010/04/15 09:55:22 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.admin;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.TableEvent;
import com.extjs.gxt.ui.client.event.TableListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.table.Table;
import com.extjs.gxt.ui.client.widget.table.TableColumn;
import com.extjs.gxt.ui.client.widget.table.TableColumnModel;
import com.extjs.gxt.ui.client.widget.table.TableItem;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XRole;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.ui.editor.AbstractTabEditor;
import com.tensegrity.wpalo.client.ui.editor.IEditor;
import com.tensegrity.wpalo.client.ui.widgets.EditorTab;
import com.tensegrity.wpalo.client.ui.widgets.RadioTableColumn;

/**
 * <code>RoleEditor</code> TODO DOCUMENT ME
 * 
 * @version $Id: RoleEditor.java,v 1.31 2010/04/15 09:55:22 PhilippBouillon Exp $
 */
public class RoleEditor extends AbstractTabEditor {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public final EditorTab[] getEditorTabs() {
		return new EditorTab[] { 
				new RolesPropertiesTab(this), 
				new RoleRightsTab(this)};
	}

	public final String getTitle(XObject input) {
		if (input instanceof XRole) {
			XRole role = (XRole) input;
			String name = role.getName();
			if (name != null && !name.equals(""))
				return messages.role(name); 
		}
		return constants.newRole(); 
	}
	
	protected final int getSaveType() {
		return WPaloEvent.SAVED_ROLE_ITEM;
	}

	public void setTextCursor() {		
		((RolesPropertiesTab) tabFolder.getItem(0)).name.focus();
	}		
}

class RolesPropertiesTab extends EditorTab {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();

	// properties:
	TextField<String> name;
	private TextArea description;
	private IEditor editor;

	RolesPropertiesTab(IEditor editor) {
		super(constants.general());
		this.editor = editor;
		setText(constants.general());
		setIconStyle("icon-role2");
		// item.setIconStyle("icon-group");
		setClosable(false);
		setScrollMode(Scroll.AUTO);
		add(createPropertiesPanel());
	}

	public final boolean save(XObject input) {
		if (input instanceof XRole) {
			XRole role = (XRole) input;
			role.setName(name.getValue());
			role.setDescription(description.getValue());
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
		if (input instanceof XRole) {
			XRole role = (XRole) input;
			name.setValue(role.getName());
			description.setValue(translateDescription(role.getDescription()));
			if (role != null && role.getName() != null && (role.getName().equals("ADMIN") ||
				role.getName().equals("EDITOR") ||
				role.getName().equals("VIEWER") ||
				role.getName().equals("POWERUSER") ||
				role.getName().equals("OWNER") ||
				role.getName().equals("CREATOR") ||
				role.getName().equals("PUBLISHER"))) {
				name.setEnabled(false);
				description.setEnabled(false);
			} else {
				name.setEnabled(true);
				description.setEnabled(true);
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

		name = new TextField<String>();
		name.setFieldLabel(constants.name());
		name.setEmptyText(constants.roleName());
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

class RoleRightsTab extends EditorTab {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private static final String RIGHT_DATA = "com.tensegity.wpalo.roleeditor.rightstab";
	private Table rightsTable;

	RoleRightsTab(final IEditor editor) {
		super(constants.rights());
		setText(constants.rights());
		setIconStyle("icon-rights");
		setClosable(false);
		List<TableColumn> columns = new ArrayList<TableColumn>();
		TableColumn cbox = new RadioTableColumn("right_check");
		cbox.setWidth(40);
		columns.add(cbox);
		TableColumn col = new TableColumn("Right", constants.right(), 100);
		// col.setMinWidth(75);
		// col.setMaxWidth(400);
		col.setAlignment(HorizontalAlignment.LEFT);
		columns.add(col);

		TableColumnModel cm = new TableColumnModel(columns);

		rightsTable = new Table(cm);
		rightsTable.setSelectionMode(SelectionMode.SINGLE);
		rightsTable.setHorizontalScroll(true);
		// disable to allow Checkbox widget!
		rightsTable.setBulkRender(false);
		rightsTable.setAutoHeight(true);

		rightsTable.add(createTableItem(constants.none(), "N"));
		rightsTable.add(createTableItem(constants.read(), "R"));
		rightsTable.add(createTableItem(constants.write(), "W"));
		rightsTable.add(createTableItem(constants.delete(), "D"));
		rightsTable.add(createTableItem(constants.create(), "C"));
		rightsTable.add(createTableItem(constants.grant(), "G"));

		rightsTable.addTableListener(new TableListener() {
			  public void tableCellClick(TableEvent te) {
				  if (te.cellIndex == 0) {
					  TableItem item = rightsTable.getItem(te.rowIndex);
					  boolean state = false;
					  if (item.getValue(0) instanceof Radio) {
						  state = ((Radio) item.getValue(0)).getValue();
					  }
					  if (state) {
						  for (int i = 0; i < rightsTable.getItemCount(); i++) {
							  if (i == te.rowIndex) {
								  continue;
							  }
							  if (((Radio) rightsTable.getItem(i).getValue(0)).getValue()) {
								  ((Radio) rightsTable.getItem(i).getValue(0)).setValue(false);
							  }
						  }
					  } else {
						  // Make sure that "NONE" is selected if no checkboxes are checked
						  boolean checked = false;
						  for (int i = 0; i < rightsTable.getItemCount(); i++) {
							  if (((Radio) rightsTable.getItem(i).getValue(0)).getValue()) {
								  checked = true;
							  }
						  }
						  if (!checked) {
							  ((Radio) rightsTable.getItem(0).getValue(0)).setValue(true);
						  }
					  }
					  editor.markDirty();
				  }
			  }
		});
		
		add(rightsTable);				
	}

	public final boolean save(XObject input) {
		if (input instanceof XRole) {
			XRole role = (XRole) input;
			for(TableItem item : rightsTable.getItems()) {
				boolean selected = false;
				String rightTag = item.getData(RIGHT_DATA);
				Object colVal = item.getValue(0);
				//bug in gxt???
				if(colVal instanceof Radio)
					selected = ((Radio)colVal).getValue();
				else if(colVal instanceof Boolean)
					selected = ((Boolean)colVal).booleanValue();
				
				if(selected)
					role.setPermission(rightTag);
			}			
		}
		return true;
	}
	public void saveAs(String name, XObject input) {		
	}

	public void set(XObject input) {
		if (input instanceof XRole) {
			XRole role = (XRole) input;
			boolean isAdm = role != null && role.getName() != null && role.getName().equals("ADMIN"); 
			((RadioTableColumn) rightsTable.getColumnModel().getColumn(0)).setEnabled(!isAdm);		
			
			String right = role.getPermission();
			if(right == null)
				right = "N";
			for(TableItem item : rightsTable.getItems()) {
				String tag = item.getData(RIGHT_DATA);
				boolean check = right.equals(tag);
				Object colVal = item.getValue(0);
				if(colVal instanceof Radio) {
					((Radio)colVal).setValue(check);
					((Radio)colVal).setEnabled(!isAdm);
				} 

			}
			rightsTable.recalculate();			
		}
	}
	
	private final TableItem createTableItem(String name, String tag) {
		Radio cb = new Radio();
		cb.setValue(false);
		TableItem item = new TableItem(new Object[]{ cb, name});
		item.setData(RIGHT_DATA, tag);
		return item;
	}
}
