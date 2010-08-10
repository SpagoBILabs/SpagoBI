/*
*
* @file ServerEditor.java
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
* @version $Id: ServerEditor.java,v 1.14 2009/12/17 16:14:20 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.modeller;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.palo.XServer;
import com.tensegrity.wpalo.client.ui.editor.CloseObserver;
import com.tensegrity.wpalo.client.ui.editor.IEditor;
import com.tensegrity.wpalo.client.ui.model.TreeNode;

/**
 * <code>UserEditor</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ServerEditor.java,v 1.14 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class ServerEditor implements IEditor { 
	
	private ContentPanel content;
//	private Html header;
	private EditorTab[] tabs;
	private final TabPanel tabFolder;
	
	public ServerEditor() {
	    //create content:
	    content = new ContentPanel();
	    content.setBodyBorder(false);
	    content.setHeaderVisible(false);
	    content.setScrollMode(Scroll.AUTO);
//		content.setHeading("Properties");
		content.setButtonAlign(HorizontalAlignment.RIGHT);

		//da toolbar
		ToolBar toolbar = new ToolBar();
		TextToolItem save = new TextToolItem("Save", "icon-save");
		toolbar.add(save);
		toolbar.add(new SeparatorToolItem());
		content.setTopComponent(toolbar);

		tabFolder = new TabPanel();
		tabFolder.setTabScroll(true);

		addTabs(tabFolder);

		RowLayout layout = new RowLayout(Orientation.VERTICAL);
		content.setLayout(layout);
		content.add(tabFolder, new RowData(1, 1));
	}

	public void beforeClose(AsyncCallback<Boolean> cb) {
		cb.onSuccess(true);
	}
	
	public final void close(CloseObserver observer) {
		tabFolder.removeAll();
		tabFolder.removeFromParent();
		if(observer != null)
			observer.finishedClosed();
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}


	public ContentPanel getPanel() {
		return content;
	}


	public String getTitle() {
		return "Server Editor";
	}

	public final void setInput(Object input) {
		if(input instanceof TreeNode) {			
			XObject _input = ((TreeNode)input).getXObject();
			for(EditorTab tab : tabs)
				tab.set(_input);			
		}
	}

	private final void addTabs(TabPanel tabFolder) {
		tabs = new EditorTab[] {
				new ServerGeneralTab()
		};
		for(EditorTab tab : tabs)
			tabFolder.add(tab);
	}

	public void doSave(AsyncCallback <Boolean> cb) {
		// TODO Auto-generated method stub
		
	}

	public Object getInput() {
		// TODO Auto-generated method stub
		return null;
	}

	public void markDirty() {
		// TODO Auto-generated method stub
		
	}

	public boolean isDirty() {
		return false;
	}

	public void selectFirstTab() {
		// TODO Auto-generated method stub
		
	}

	public void setTextCursor() {
		// TODO Auto-generated method stub
		
	}

}

class ServerGeneralTab extends EditorTab {

	//properties:
	private TextField<String> serverName;
	private TextField<String> lastname;
	private TextField<String> login;
	
	ServerGeneralTab() {
		setText("General");
		setIconStyle("icon-server");
		setClosable(false);
		setScrollMode(Scroll.AUTO);
		add(createPropertiesPanel());
	}
	
	void set(XObject input) {
		if(input instanceof XServer) {
			XServer server = (XServer) input;
			serverName.setValue(server.getName());
//			lastname.setValue(user.getLastname());
//			login.setValue(user.getLogin());
		}
	}
	
	private final ContentPanel createPropertiesPanel() {
		FormPanel panel = new FormPanel();
		panel.setHeaderVisible(false);
		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		panel.setStyleAttribute("padding", "20");

		serverName = new TextField<String>();
		serverName.setFieldLabel("Server Name");
		serverName.setEmptyText("Please enter the server name");
		serverName.setAllowBlank(false);
		serverName.setMinLength(1);
		panel.add(serverName);

		return panel;
	}
}
