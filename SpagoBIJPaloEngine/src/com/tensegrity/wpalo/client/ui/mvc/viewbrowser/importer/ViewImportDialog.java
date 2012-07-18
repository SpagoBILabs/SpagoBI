/*
*
* @file ViewImportDialog.java
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
* @version $Id: ViewImportDialog.java,v 1.17 2010/04/12 11:13:36 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.viewbrowser.importer;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.google.gwt.user.client.DOM;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.wpalo.client.WPaloServiceProvider;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.ui.model.XObjectModel;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>ViewImporter</code> TODO DOCUMENT ME
 * 
 * @version $Id: ViewImportDialog.java,v 1.17 2010/04/12 11:13:36 PhilippBouillon Exp $
 **/
public class ViewImportDialog extends Window {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();
	
	public static final String XOBJECT_TYPE = "viewimporterobject";

	public static final String BUTTON_OK = "apply";
	public static final String BUTTON_CANCEL = "cancel";

	private ViewSelectionTree selectionTree;
	private AccountComboBox accounts;
	private CheckBox makePublicView;
	private CheckBox makeEditableView;
	
	private Button okButton;
	private Button cancelButton;
	private final boolean importViews;
	private final boolean showBoxes;
	
	public ViewImportDialog(boolean importViews, boolean showBoxes) {
		this.importViews = importViews;
		this.showBoxes = showBoxes;
		setClosable(false);
		setCloseAction(CloseAction.CLOSE);
		if(importViews)
			setHeading(constants.selectViewsToImport());
		else
			setHeading(constants.selectCubesToCreateView());
		setPixelSize(420, showBoxes ? 384 : 344);
		setModal(true);
		add(createForm());
		DOM.setStyleAttribute(getElement(), "backgroundColor", "white");
		initEventHandling();
	}

	public final void addButtonListener(String buttonId,
			Listener<BaseEvent> listener) {
		if (buttonId.equals(BUTTON_OK))
			okButton.addListener(Events.Select, listener);
		else if (buttonId.equals(BUTTON_CANCEL))
			cancelButton.addListener(Events.Select, listener);
	}

	public void show() {
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloServiceProvider.getInstance().loadAccounts(sessionId, 
				new Callback<XAccount[]>(constants.failedToLoadAccountInformation()) {
					public void onSuccess(XAccount[] xAccounts) {
						accounts.setInput(xAccounts);
						ViewImportDialog.super.show();
					}
				});
	}

	public final XView[] getSelectedViews() {
		return selectionTree.getSelectedViews(isPublic(), isEditable());
	}
	private FormPanel createForm() {
		FormPanel panel = new FormPanel();
		panel.setFrame(true);
		// panel.setIconStyle("icon-filter");
		panel.setCollapsible(false);
		panel.setHeaderVisible(false);
		// panel.setHeading("Select views to import");
		panel.setSize(410, -1);
		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		panel.setLayout(new FlowLayout());

		// main container:
		LayoutContainer main = new LayoutContainer();
		RowLayout rowLayout = new RowLayout();
		main.setLayout(rowLayout);

		main.add(createAccountChoice());
		main.add(createTreePanel());

		panel.add(main);

		// Checkboxes for public/editable:
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
		panel.add(rights);
		
		LabelField label = new LabelField();
		label.setHeight("20px");
		panel.add(label);
		
		// finally the apply/cancel button:
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
		okButton = importViews ? new Button(constants.importAction()) : new Button(constants.create());
		okButton.setItemId(BUTTON_OK);
		cancelButton = new Button(constants.cancel());
		cancelButton.setItemId(BUTTON_CANCEL);
		okButton.addSelectionListener(listener);
		cancelButton.addSelectionListener(listener);
		panel.addButton(okButton);
		panel.addButton(cancelButton);
		
		return panel;
	}

	private final LayoutContainer createAccountPanel() {
		LayoutContainer panel = new LayoutContainer();
		FormLayout layout = new FormLayout();
		layout.setLabelAlign(LabelAlign.LEFT);
		layout.setPadding(0);
		layout.setLabelWidth(80);
		panel.setLayout(layout);
		FormData formData = new FormData("100%");

		// add alias field editor;
		accounts = new AccountComboBox(constants.account(), constants.lowerCaseAccount());
//				"Account");
		ComboBox<XObjectModel> accountsCombo = accounts.getComboBox();
		panel.add(accountsCombo, formData);

		return panel;
	}

	private final LayoutContainer createTreePanel() {
		LayoutContainer panel = new LayoutContainer();
		panel.setLayout(new RowLayout());
		panel.add(new LabelField(constants.views() + ":"));
		LayoutContainer treePanel = new LayoutContainer();
		selectionTree = new ViewSelectionTree(importViews, false);
		Tree viewTree = selectionTree.getTree();
		viewTree.setCheckable(true);
//		treePanel.setSize(210, 200);
		treePanel.setWidth("100%");
		treePanel.setHeight(200);
		treePanel.setBorders(true);
		treePanel.setScrollMode(Scroll.AUTOY);
		treePanel.setStyleAttribute("backgroundColor", "white");
		treePanel.add(viewTree);
		panel.add(treePanel);
		return panel;
	}

	private final LayoutContainer createAccountChoice() {
		LayoutContainer panel = new LayoutContainer();
		panel.setLayout(new FillLayout());
		panel.add(createAccountPanel());
		panel.setHeight(30);
		return panel;
	}

	private final void initEventHandling() {
		accounts.addSelectionChangedListener(
			new SelectionChangedListener<XObjectModel>() {
					public void selectionChanged(
							SelectionChangedEvent<XObjectModel> se) {
						XObjectModel selection = se.getSelectedItem();
						selectionTree.setInput((XAccount) selection
								.getXObject());
					}
				});
	}
	
	public boolean isPublic() {
		return makePublicView.getValue();
	}
	
	public boolean isEditable() {
		return makeEditableView.getValue();
	}
}
