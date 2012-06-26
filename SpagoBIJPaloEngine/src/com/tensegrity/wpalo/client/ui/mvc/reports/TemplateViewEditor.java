/*
*
* @file TemplateViewEditor.java
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
* @version $Id: TemplateViewEditor.java,v 1.11 2009/12/17 16:14:20 PhilippBouillon Exp $
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
