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
package com.tensegrity.wpalo.client.ui.editor;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * <code>Editor</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: IEditor.java,v 1.9 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public interface IEditor {
	
	public String getId();
	public ContentPanel getPanel();
	public String getTitle();
	public Object getInput();
	public void setInput(Object input);
	public void doSave(AsyncCallback <Boolean> cb);
	public void markDirty();
	public boolean isDirty();
	public void close(CloseObserver observer);
	public void beforeClose(AsyncCallback <Boolean> callback);
	public void selectFirstTab();
	public void setTextCursor();
}
