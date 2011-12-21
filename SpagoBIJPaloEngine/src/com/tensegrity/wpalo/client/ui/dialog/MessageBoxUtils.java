/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.ui.dialog;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;

public class MessageBoxUtils {
	  public static MessageBox yesNoCancel(String title, String msg,
	      Listener<WindowEvent> callback) {
	    MessageBox box = new MessageBox();
	    box.setTitle(title);
	    box.setMessage(msg);
	    box.addCallback(callback);
	    box.setIcon(MessageBox.QUESTION);
	    box.setButtons(MessageBox.YESNOCANCEL);
	    box.show();
	    return box;
	  }
	
}

