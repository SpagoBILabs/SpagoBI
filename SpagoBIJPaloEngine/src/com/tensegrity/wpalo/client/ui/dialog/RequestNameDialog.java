/*
*
* @file RequestNameDialog.java
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
* @version $Id: RequestNameDialog.java,v 1.4 2009/12/17 16:14:20 PhilippBouillon Exp $
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
