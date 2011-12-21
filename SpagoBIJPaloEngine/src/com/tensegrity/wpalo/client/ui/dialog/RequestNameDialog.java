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
package com.tensegrity.wpalo.client.ui.dialog;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * <code>LoginDialog</code> TODO DOCUMENT ME
 * 
 * @version $Id: RequestNameDialog.java,v 1.4 2009/12/17 16:14:20 PhilippBouillon Exp $
 */
//TODO get it to work as a dialog!! 
//somehow it doesn't looks good in gwt host mode. maybe its an ie6 thing...
public class RequestNameDialog extends Dialog {

	private TextField<String> name;
	private final ResultListener <String> listener;
	
	public RequestNameDialog(String title, String label, ResultListener <String> listener) {
		this.listener = listener;
		setHeaderVisible(true);
		setHeading(title);
		setStyleAttribute("padding", "20");
		setStyleAttribute("background-color", "white");
		//setBorders(true);
		setButtons(Dialog.OKCANCEL);
		setTitle(title);
		addInputFields(label);
		setModal(true);
		setPlain(true);
	    setResizable(false);
	    setConstrain(true);
	    setMinimizable(false);
	    setMaximizable(false);
	    setMinWidth(300);
	    setClosable(false);
	    setButtonAlign(HorizontalAlignment.RIGHT);
	    setMinHeight(180);
	    setFooter(true);	
	    setHideOnButtonClick(true);
	}
	
	protected void onButtonPressed(Button button) {
		super.onButtonPressed(button);
		if (button.getItemId().equals(OK)) {
			listener.requestFinished(name.getValue());
		} else if (button.getItemId().equals(CANCEL)) {
			listener.requestCancelled();
		}
	}
	
	private final void addInputFields(String label) {
		name = new TextField<String>();
		name.setFieldLabel(label);
		name.setEmptyText("Neuer Name");
		name.setAllowBlank(false);
		add(name);
	}	
}
