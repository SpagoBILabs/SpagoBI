/*
*
* @file NewFolderDialog.java
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
* @version $Id: NewFolderDialog.java,v 1.7 2010/02/12 13:49:50 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.client.ui.mvc.viewbrowser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;

public class NewFolderDialog extends Window {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	public static final String CREATE = "create";
	public static final String CANCEL = "cancel";
	
	private Button createButton;
	private final TextField<String> name = new TextField<String>();
	
	public NewFolderDialog() {		
		setHeading(constants.createNewFolder());
		add(createForm());
		name.focus();
		setSize(350, 120);
		setLayout(new FitLayout());		
		setCloseAction(CloseAction.CLOSE);
	}
	
	public final void setUsedFolderNames(String[] folderNames) {
		name.setValidator(new FolderNameValidator(folderNames));
	}
	public final String getFolderName() {
		return name.getValue();
	}
	
	private FormPanel createForm() {
		FormData formData = new FormData("100%");
		FormPanel panel = createFormPanel();
		addFolderNameField(panel, formData);
		addCreateCancelButtons(panel);		
		return panel;
	}

	private final FormPanel createFormPanel() {
		FormPanel panel = new FormPanel();
		panel.setFrame(true);
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		FormLayout layout = new FormLayout();
		panel.setLayout(layout);
		return panel;
	}
	private final void addFolderNameField(FormPanel panel, FormData data) {
		name.setFieldLabel(constants.folderName());
		name.addKeyListener(new KeyListener() {
			public void componentKeyUp(ComponentEvent event) {
				super.componentKeyUp(event);
				name.validate();
				if(pressedEnter(event.getKeyCode())) {
					close(createButton);
				}
			}
		});
		panel.add(name, data);		
	}
	private final boolean pressedEnter(int keyCode) {
		return keyCode == 13;
	}

	private final void addCreateCancelButtons(FormPanel panel) {
		SelectionListener<ComponentEvent> listener = new SelectionListener<ComponentEvent>() {
			public void componentSelected(ComponentEvent ce) {
				if (ce.component instanceof Button) {
					Button pressedButton = (Button) ce.component;
					// we close dialog on button press:
					if (closeAction == CloseAction.CLOSE)
						close(pressedButton);
					else
						hide(pressedButton);
				}
			}
		};

		createButton = new Button(constants.create());
		createButton.setItemId(CREATE);
		final Button cancel = new Button(constants.cancel());
		cancel.setItemId(CANCEL);
		createButton.addSelectionListener(listener);
		cancel.addSelectionListener(listener);
		panel.addButton(createButton);
		panel.addButton(cancel);
	}
}

class FolderNameValidator implements Validator<String, TextField<String>> {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private final List<String> folderNames = new ArrayList<String>();
	public FolderNameValidator(String[] viewNames) {
		this.folderNames.addAll(Arrays.asList(viewNames));
	}
	public String validate(TextField<String> field, String folderName) {
		if(folderNames.contains(folderName))
			return constants.folderNameInUse(); 
		return null;
	}
	
}
