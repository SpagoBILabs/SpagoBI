/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.reports;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.wpalo.client.ui.editor.CloseObserver;
import com.tensegrity.wpalo.client.ui.editor.IEditor;
import com.tensegrity.wpalo.client.ui.model.TreeNode;

/**
 * <code>TemplateViewEditor</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: TemplateViewEditor.java,v 1.11 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class TemplateViewEditor implements IEditor {

	private ContentPanel content;
	private TabPanel tabFolder = new TabPanel();
	
	public TemplateViewEditor() {
	    //create content:
	    content = new ContentPanel();
	    content.setBodyBorder(false);
	    content.setHeaderVisible(false);
	    content.setScrollMode(Scroll.AUTO);
	    
	    //add tab folder as main content
		tabFolder.setTabScroll(true);
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

	public void doSave(AsyncCallback <Boolean> cb) {
		// TODO Auto-generated method stub
		
	}

	public boolean isDirty() {
		return false;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getInput() {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentPanel getPanel() {
		return content;
	}

	public final String getTitle() {
		return "Template View Editor";
	}

	public void markDirty() {
	}

	public void setInput(Object input) {
		if(input instanceof TreeNode) {
			//check if we have a view:
			XObject _input = ((TreeNode)input).getXObject();
			if(!isAlreadyOpen(_input.getId())) {
				//open it:
				TemplateViewEditorTab viewTab = new TemplateViewEditorTab(_input.getName());
//				TabItem item = new TabItem(_input.getName());
				viewTab.setId(_input.getId());
				viewTab.setClosable(true);
				tabFolder.add(viewTab);
				tabFolder.setSelection(viewTab);
				viewTab.set(_input);				
			} else {
				//TODO bring to front...
			}
		}
	}

	/**
	 * shows the view if it is already open, otherwise calling this method has no effect
	 * @param input
	 */
	public final void showView(Object input) {
		if (input instanceof TreeNode) {
			// check if we have a view:
			XObject _input = ((TreeNode) input).getXObject();
			TabItem tab = tabFolder.findItem(_input.getId(), false);
			if (tab != null)
				tabFolder.setSelection(tab);
		}
	}
	
	private final boolean isAlreadyOpen(String id) {
		TabItem item = tabFolder.findItem(id, false);
//		tabFolder.set
		return item != null;
	}

	public void selectFirstTab() {
		// TODO Auto-generated method stub
		
	}

	public void setTextCursor() {
		// TODO Auto-generated method stub
		
	}
}
