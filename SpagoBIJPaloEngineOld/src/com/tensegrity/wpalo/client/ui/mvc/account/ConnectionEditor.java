/*
*
* @file ConnectionEditor.java
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
* @version $Id: ConnectionEditor.java,v 1.21 2010/04/15 09:55:22 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.account;

import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.ui.editor.AbstractTabEditor;
import com.tensegrity.wpalo.client.ui.editor.IEditor;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.EnhancedComboBox;
import com.tensegrity.wpalo.client.ui.widgets.EditorTab;

/**
 * <code>ConnectionEditor</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ConnectionEditor.java,v 1.21 2010/04/15 09:55:22 PhilippBouillon Exp $
 **/
public class ConnectionEditor extends AbstractTabEditor {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();

	public final EditorTab[] getEditorTabs() {
		return new EditorTab[] {
			new ConnectionPropertiesTab(this),
			new ConnectionServerTab(this)
		};
	}
	
	public final String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public final String getTitle(XObject input) {
		if (input instanceof XConnection) {
			XConnection connection = (XConnection) input;
			String name = connection.getName();
			if(name != null && !name.equals(""))
				return messages.connection(name);
		}
		return constants.newConnection(); 
	}
	
	protected final int getSaveType() {
		return WPaloEvent.SAVED_CONNECTION_ITEM;
	}

	public void setTextCursor() {		
		((ConnectionPropertiesTab) tabFolder.getItem(0)).name.focus();
	}		
}

class ConnectionTypeModel extends BaseModel {
	
	private static final long serialVersionUID = 1L;

	public ConnectionTypeModel(String type) {
		set("type", type);
	}
	
	public final String getType() {
		return get("type");
	}
}

class ConnectionPropertiesTab extends EditorTab {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	TextField<String> name;
	private TextArea description;
	private final IEditor editor;
	
	public ConnectionPropertiesTab(IEditor editor) {
		super(constants.general());
		this.editor = editor;
		setText(constants.general());
		setIconStyle("icon-connection");
		setClosable(false);
		setScrollMode(Scroll.AUTO);
		add(createPropertiesPanel());
	}
	
	public final boolean save(XObject input) {
		if(input instanceof XConnection) {
			XConnection connection = (XConnection) input;
			connection.setName(name.getValue());
			connection.setDescription(description.getValue());
		}
		return true;
	}
	public void saveAs(String name, XObject input) {		
	}

	public void set(XObject input) {
		if(input instanceof XConnection) {
			XConnection connection = (XConnection) input;
			name.setValue(connection.getName());
			description.setValue(connection.getDescription());
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
		name.setEmptyText(constants.connectionName());
		name.setAllowBlank(false);
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

class ConnectionServerTab extends EditorTab {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private TextField<String> host;
	private TextField<String> service;
	private EnhancedComboBox<XConnectionType> typeCombo;
	private ListStore<XConnectionType> typeStore;
	private SelectionChangedListener<XConnectionType> selectionListener;
	private XConnection input;
	private final IEditor editor;
	
	public ConnectionServerTab(IEditor editor) {
		super(constants.connection());
		this.editor = editor;
		setText(constants.connection());
		setClosable(false);
		setScrollMode(Scroll.AUTO);
		add(createServerPropsPanel());
	}

	public final boolean save(XObject input) {
		if(input instanceof XConnection) {
			XConnection connection = (XConnection) input;
			connection.setHost(host.getValue());
			connection.setService(service.getValue());
			int type = getType();
			if(type != -1)
				connection.setConnectionType(type);
		}
		return true;
	}
	public void saveAs(String name, XObject input) {		
	}

	public final void set(XObject input) {
		if(input instanceof XConnection) {
			XConnection connection = (XConnection) input;
			this.input = connection;
			host.setValue(connection.getHost());
			service.setValue(connection.getService());
			if (selectionListener != null) {
				typeCombo.removeSelectionListener(selectionListener);
			}
			setType(connection.getConnectionType());
			if (selectionListener != null) {
				typeCombo.addSelectionChangedListener(selectionListener);
			}
		}
	}

	private final ContentPanel createServerPropsPanel() {
		FormPanel panel = new FormPanel();
		panel.setHeaderVisible(false);
		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		panel.setStyleAttribute("padding", "20");

		KeyListener keyListener = new KeyListener() {
			public void componentKeyUp(ComponentEvent event) {
				editor.markDirty();
			}
		};
		selectionListener = 
			new SelectionChangedListener<XConnectionType>() {
		public void selectionChanged(
				SelectionChangedEvent<XConnectionType> se) {
			XConnectionType type = se.getSelectedItem();
			if (type == null) {
				if (input != null && (input.getType() == null || input.getType().isEmpty())) {
					return;
				}				
			} else {
				if (input != null && type.getType() != input.getConnectionType()) {
					editor.markDirty();
				} else if (input == null && type != null) {
					editor.markDirty();
				}
				return;
			}
			editor.markDirty();
		}
	};

		host = new TextField<String>();
		host.setFieldLabel(constants.host());
		host.setEmptyText(constants.hostName());
		host.setAllowBlank(false);
		host.addKeyListener(keyListener);
		panel.add(host);

		service = new TextField<String>();
		service.setFieldLabel(constants.service());
		service.setEmptyText(constants.serviceName());
		service.setAllowBlank(false);
		service.addKeyListener(keyListener);
		panel.add(service);

		typeCombo = new EnhancedComboBox<XConnectionType>();
		typeCombo.setEditable(false);
		typeCombo.setFieldLabel(constants.type());
		typeCombo.setDisplayField("name");
		typeCombo.setEmptyText(constants.chooseConnectionType());
		typeStore = new ListStore<XConnectionType>();
//		typeStore.add(XConnectionType.LEGAGY);
		typeStore.add(XConnectionType.PALO);
		typeStore.add(XConnectionType.XMLA);
//		typeStore.add(XConnectionType.WSS);
		typeCombo.setStore(typeStore);
		typeCombo.addSelectionChangedListener(selectionListener);
		panel.add(typeCombo);  
		
		return panel;
	}
	
	private final void setType(int type) {
		for(XConnectionType xType : typeStore.getModels()) {
			if(xType.getType() == type)
				typeCombo.setValue(xType);
		}
	}
	private final int getType() {
		int type = -1;
		List<XConnectionType> selection = typeCombo.getSelection();
		if(!selection.isEmpty()) {
			XConnectionType t = selection.get(0);
			type = t.getType();
		}
		return type;

	}
}

class XConnectionType extends BaseModel {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private static final long serialVersionUID = -9189014270779101595L;
	
	static final XConnectionType LEGAGY = new XConnectionType(constants.legacy(), 1);
	static final XConnectionType PALO = new XConnectionType(constants.palo(), 2);
	static final XConnectionType XMLA = new XConnectionType(constants.xmla(), 3);
	static final XConnectionType WSS = new XConnectionType(constants.wss(), 4);
	
	private final int type;
	XConnectionType(String name, int type) {
		this.type = type;
		set("name", name);
	}
	
	public final int getType() {
		return type;
	}
}