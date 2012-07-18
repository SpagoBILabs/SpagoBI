/*
*
* @file SaveAsDialog.java
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
* @version $Id: SaveAsDialog.java,v 1.16 2010/04/12 11:13:36 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;

/**
 * <code>SaveAsDialog</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: SaveAsDialog.java,v 1.16 2010/04/12 11:13:36 PhilippBouillon Exp $
 **/
public class SaveAsDialog extends Window {

	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();

	public static final String SAVE = "save";
	public static final String CANCEL = "cancel";
	
	private Button saveButton;
	private Button cancelButton;
	private final TextArea comment = new TextArea();
	private final TextField<String> name = new TextField<String>();
	private final String initialName;
	
	private CheckBox makePublicView;
	private CheckBox makeEditableView;
	private final boolean showBoxes;
	
	public SaveAsDialog(String initialName, boolean showBoxes) {
		this.initialName = initialName;
		this.showBoxes = showBoxes;
		setHeading(constants.saveView());
		setLayoutOnChange(true);
		add(createForm(initialName));
		setSize(450, showBoxes ? 160 : 120);
		setLayout(new FitLayout());		
		setCloseAction(CloseAction.CLOSE);		
	}
	
	public void show() {
		super.show();
		String text = name.getValue();
		if (text != null && text.length() != 0) {			
			name.setCursorPos(text.length());
			name.selectAll();			
		}				
	}
	
	public final void setUsedViewNames(String[] viewNames) {
		name.setValidator(new ViewNameValidator(viewNames, initialName));
	}
	public final String getViewName() {
		return name.getValue();
	}
	public final String getComment() {
		return ""; //comment.getValue();
	}
	
	private FormPanel createForm(String initialName) {
		FormData formData = new FormData("90%");
		FormPanel panel = createFormPanel();
		addViewNameField(panel, formData, initialName);
//		addCommentField(panel, formData);
		
		final LayoutContainer rights = new LayoutContainer();
		RowLayout rLayout = new RowLayout();
		rights.setLayout(rLayout);
		
		makePublicView = new CheckBox();
		makePublicView.setBoxLabel(constants.visibleForAllViewers());
		
		makeEditableView = new CheckBox();	
		makeEditableView.setBoxLabel(constants.visibleAndEditableForAllEditors());
		
		if (showBoxes) {
			rights.add(makePublicView);
			rights.add(makeEditableView);					
			makePublicView.setValue(false);
			makeEditableView.setValue(false);					
		} 
//		rights.add(makePublicView);
//		rights.add(makeEditableView);		
//		makePublicView.setValue(true);
//		makeEditableView.setValue(true);
		
		//panel.add(rights);
		
		addSaveCancelButtons(panel);	
		name.focus();		
		return panel;
	}

	private final FormPanel createFormPanel() {
		FormPanel panel = new FormPanel();
		panel.setFrame(true);
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		panel.setLayoutOnChange(true);
		FormLayout layout = new FormLayout();
		panel.setLayout(layout);
		return panel;
	}
	private final void addViewNameField(FormPanel panel, FormData data, String initialName) {
		name.setFieldLabel(constants.subobjectName());
		
		name.setValue(initialName);
		name.addKeyListener(new KeyListener() {
			public void componentKeyUp(ComponentEvent event) {
				super.componentKeyUp(event);
				int keyCode = event.getKeyCode();
				if (keyCode == KeyboardListener.KEY_ESCAPE) {
					close(cancelButton);
				}
				if (keyCode != KeyboardListener.KEY_ALT &&
					keyCode != KeyboardListener.KEY_CTRL &&
					keyCode != KeyboardListener.KEY_SHIFT &&
					keyCode != KeyboardListener.KEY_LEFT &&
					keyCode != KeyboardListener.KEY_RIGHT &&
					keyCode != KeyboardListener.KEY_DOWN &&					
					keyCode != KeyboardListener.KEY_UP &&
					keyCode != KeyboardListener.KEY_HOME &&
					keyCode != KeyboardListener.KEY_END) {
					name.validate();
				}
				if(pressedEnter(event.getKeyCode())) {
					close(saveButton);
				}
			}
		});		
		panel.add(name, data);
	}
	private final boolean pressedEnter(int keyCode) {
		return keyCode == 13;
	}
	
	private final void addCommentField(FormPanel panel, FormData data) {
		comment.setFieldLabel("Comment");
		panel.add(comment, data);
	}
	private final void addSaveCancelButtons(FormPanel panel) {
		//finally the apply/cancel button:
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

		saveButton = new Button(constants.save());
		saveButton.setItemId(SAVE);
		cancelButton = new Button(constants.cancel());
		cancelButton.setItemId(CANCEL);
		saveButton.addSelectionListener(listener);
		cancelButton.addSelectionListener(listener);
		panel.addButton(saveButton);
		panel.addButton(cancelButton);
	}
	
	public boolean isPublic() {
		return makePublicView.getValue();
	}
	
	public boolean isEditable() {
		return makeEditableView.getValue();
	}	
}

class ViewNameValidator implements Validator<String, TextField<String>> {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private final List<String> viewNames = new ArrayList<String>();
	private final String initialName;
	private boolean initialRender;
	
	public ViewNameValidator(String[] viewNames, String initialName) {
		this.initialName = initialName;
		this.viewNames.addAll(Arrays.asList(viewNames));
		initialRender = true;
	}
	public String validate(TextField<String> field, String value) {
		if (initialName.equals(value) && initialRender) {
			initialRender = false;
			return null;
		}
		if(viewNames.contains(value))
			return constants.viewNameInUse();
		return null;
	}
	
}
