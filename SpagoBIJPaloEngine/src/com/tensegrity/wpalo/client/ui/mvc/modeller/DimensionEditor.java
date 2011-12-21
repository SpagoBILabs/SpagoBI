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
package com.tensegrity.wpalo.client.ui.mvc.modeller;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.table.Table;
import com.extjs.gxt.ui.client.widget.table.TableColumn;
import com.extjs.gxt.ui.client.widget.table.TableColumnModel;
import com.extjs.gxt.ui.client.widget.table.TableItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.palo.XDimension;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.wpalo.client.WPaloServiceProvider;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.ui.editor.CloseObserver;
import com.tensegrity.wpalo.client.ui.editor.IEditor;
import com.tensegrity.wpalo.client.ui.model.TreeNode;

/**
 * <code>UserEditor</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: DimensionEditor.java,v 1.17 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class DimensionEditor implements IEditor { 
	
	private ContentPanel content;
//	private Html header;
	private EditorTab[] tabs;
	private final TabPanel tabFolder;
	
	public DimensionEditor() {
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
		return "Dimension Editor";
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
				new DimGeneralTab(),
				new ElementTab(),
				new DimRightsTab()
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

abstract class EditorTab extends TabItem {
	
	abstract void set(XObject input); 
}

class DimGeneralTab extends EditorTab {

	//properties:
	private TextField<String> dimName;
	
	DimGeneralTab() {
		setText("General");
		setIconStyle("icon-dim");
		setClosable(false);
		setScrollMode(Scroll.AUTO);
		add(createPropertiesPanel());
	}
	
	void set(XObject input) {
		if(input instanceof XDimension) {
			XDimension dim = (XDimension) input;
			dimName.setValue(dim.getName());
		}
	}
	
	private final ContentPanel createPropertiesPanel() {
		FormPanel panel = new FormPanel();
		panel.setHeaderVisible(false);
		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		panel.setStyleAttribute("padding", "20");

		dimName = new TextField<String>();
		dimName.setFieldLabel("Dimension Name");
		dimName.setEmptyText("Please enter the dimension name");
		dimName.setAllowBlank(false);
		dimName.setMinLength(1);
		panel.add(dimName);

		return panel;
	}
}

class ElementTab extends EditorTab {
	
	//the groups table with 3 columns: active, name, description
	private Table elementTable;

	ElementTab() {
		setText("Elements");
		setIconStyle("icon-dim");
		setClosable(false);
		// create groups table
		List<TableColumn> columns = new ArrayList<TableColumn>();
//		TableColumn cbox = DimensionEditor.newCheckboxColumn("grp_check");
//		columns.add(cbox);
		TableColumn col = new TableColumn("Name", 300);
		col.setMinWidth(75);
		col.setMaxWidth(300);
		columns.add(col);
		col = new TableColumn("Type", 100);
		col.setMaxWidth(400);
		col.setAlignment(HorizontalAlignment.LEFT);
		columns.add(col);

		TableColumnModel cm = new TableColumnModel(columns);

		elementTable = new Table(cm);
		elementTable.setSelectionMode(SelectionMode.MULTI);
		elementTable.setHorizontalScroll(true);
		elementTable.setDeferHeight(true);
		// disable to allow CheckBox widget!
		elementTable.setBulkRender(false);
		this.setLayout(new FitLayout());
		add(elementTable);
	}
	void set(XObject input) {
		if (input instanceof XDimension) {
			final XDimension dim = (XDimension) input;
			
			if (input instanceof XDimension) {
				WPaloServiceProvider.getInstance().loadElements(
						dim, 
						new Callback <List<XElement>> (){
						public void onSuccess(List<XElement> elems) {
							elementTable.removeAll();
							for (int i = 0; i < elems.size(); i++) {
								elementTable.add(new TableItem(new Object[] { elems.get(i).getName(), elems.get(i).getElementType()}));
							}
						}
					});
			}
		}
	}
}

class DimRightsTab extends EditorTab {

	//properties:
	
	DimRightsTab() {
		setText("Rights");
		setIconStyle("icon-dim");
		setClosable(false);
		setScrollMode(Scroll.AUTO);
		add(createPropertiesPanel());
	}
	
	void set(XObject input) {
	}
	
	private final ContentPanel createPropertiesPanel() {
		FormPanel panel = new FormPanel();
		panel.setHeaderVisible(false);
		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		panel.setStyleAttribute("padding", "20");

		return panel;
	}
}

