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
package com.tensegrity.wpalo.client.ui.mvc.viewbrowser;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.ui.dialog.LanguageBox;
import com.tensegrity.wpalo.client.ui.dialog.LanguageData;
import com.tensegrity.wpalo.client.ui.dialog.ThemeBox;
import com.tensegrity.wpalo.client.ui.dialog.ThemeData;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>ViewImporter</code> TODO DOCUMENT ME
 * 
 * @version $Id: CreateDirectLinkDialog.java,v 1.8 2010/04/13 09:45:15 PhilippBouillon Exp $
 **/
public class CreateDirectLinkDialog extends Window {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();

	public static final String XOBJECT_TYPE = "viewimporterobject";

	public static final String BUTTON_OK = "apply";
	public static final String BUTTON_CANCEL = "cancel";

	private LanguageBox languageBox;
	private ThemeBox themeBox;
	private CheckBox hideTitleBar;
	private CheckBox hideToolBar;
	private CheckBox hideSave;
	private CheckBox hideFilter;
	private CheckBox hideStaticFilter;
	private CheckBox hideHorizontalAxis;
	private CheckBox hideVerticalAxis;
	private CheckBox hideViewTabs;
	private CheckBox hideNavigator;
	private CheckBox autoUser;
	private CheckBox autoLogin;
	private TextArea directLink;
	
	private Button okButton;
//	private Button cancelButton;
	private final XView xView;
	
	public CreateDirectLinkDialog(XView view) {
		this.xView = view;
		setClosable(false);
		setCloseAction(CloseAction.CLOSE);
		setHeading(messages.directLinkHeading(ViewBrowserModel.modify(view.getName())));
		setPixelSize(400, 466);
		setModal(true);
		add(createForm());
		DOM.setStyleAttribute(getElement(), "backgroundColor", "white");
	}

	public final void addButtonListener(String buttonId,
			Listener<BaseEvent> listener) {
		if (buttonId.equals(BUTTON_OK))
			okButton.addListener(Events.Select, listener);
//		else if (buttonId.equals(BUTTON_CANCEL))
//			cancelButton.addListener(Events.Select, listener);
	}

	private FormPanel createForm() {
		FormPanel panel = new FormPanel();
		panel.setFrame(true);
		// panel.setIconStyle("icon-filter");
		panel.setCollapsible(false);
		panel.setHeaderVisible(false);
		// panel.setHeading("Select views to import");
		panel.setSize(386, -1);
		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		panel.setLayout(new FlowLayout());
		
		// Checkboxes for public/editable:
		LayoutContainer rights = new LayoutContainer();		
		TableLayout rLayout = new TableLayout(2);
		rights.setLayout(rLayout);

		languageBox = new LanguageBox();
		languageBox.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<LanguageData>>() {
			public void selectionChanged(
					SelectionChangedEvent<SimpleComboValue<LanguageData>> se) {
				directLink.setValue(getLinkString());
			}
		});
		
		themeBox = new ThemeBox();
		themeBox.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<ThemeData>>() {
			public void selectionChanged(
					SelectionChangedEvent<SimpleComboValue<ThemeData>> se) {
				directLink.setValue(getLinkString());
			}
		});
		
		HorizontalPanel hhp = new HorizontalPanel();
		hhp.setHeight(20);
		rights.add(hhp);
		HorizontalPanel hhhp = new HorizontalPanel();
		rights.add(hhhp);
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlign(VerticalAlignment.TOP);
		languageBox.setHideLabel(true);
		LabelField labelL = new LabelField(constants.language() + ":");
		labelL.setPixelSize(150, -1);
		languageBox.setPixelSize(220, -1);
		hp.add(labelL);
		hp.add(languageBox);
				
		TableData ldata = new TableData();
		ldata.setColspan(2);
		rights.add(hp, ldata);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		hp.setVerticalAlign(VerticalAlignment.TOP);
		themeBox.setHideLabel(true);
		LabelField labelL2 = new LabelField(constants.theme() + ":");
		labelL2.setPixelSize(150, -1);
		themeBox.setPixelSize(220, -1);
		hp2.add(labelL2);
		hp2.add(themeBox);
				
		TableData ldata2 = new TableData();
		ldata2.setColspan(2);
		rights.add(hp2, ldata2);

		hideNavigator = new CheckBox();
		hideNavigator.setBoxLabel(constants.hideNavigator());		
		hideViewTabs = new CheckBox();
		hideViewTabs.setBoxLabel(constants.hideViewTabs());
		autoLogin = new CheckBox();
		autoLogin.setBoxLabel(constants.addPassword());
		autoUser = new CheckBox();
		autoUser.setBoxLabel(constants.addUserFlag());
		hideTitleBar = new CheckBox();
		hideTitleBar.setBoxLabel(constants.hideTitleBar());
		hideToolBar = new CheckBox();
		hideToolBar.setBoxLabel(constants.hideToolBar());
		hideSave = new CheckBox();
		hideSave.setBoxLabel(constants.hideSave());
		hideFilter = new CheckBox();
		hideFilter.setBoxLabel(constants.hideFilter());
		hideStaticFilter = new CheckBox();
		hideStaticFilter.setBoxLabel(constants.hideStaticFilter());
		hideHorizontalAxis = new CheckBox();
		hideHorizontalAxis.setBoxLabel(constants.hideHorizontalAxis());
		hideVerticalAxis = new CheckBox();
		hideVerticalAxis.setBoxLabel(constants.hideVerticalAxis());
		
		Listener <BaseEvent> listener = new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				directLink.setValue(getLinkString());
			}
		};
		hideNavigator.addListener(Events.Change, listener);
		hideViewTabs.addListener(Events.Change, listener);
		autoLogin.addListener(Events.Change, listener);
		autoUser.addListener(Events.Change, listener);
		hideTitleBar.addListener(Events.Change, listener);
		hideToolBar.addListener(Events.Change, listener);
		hideSave.addListener(Events.Change, listener);
		hideFilter.addListener(Events.Change, listener);
		hideStaticFilter.addListener(Events.Change, listener);
		hideHorizontalAxis.addListener(Events.Change, listener);
		hideVerticalAxis.addListener(Events.Change, listener);
		
		rights.add(autoUser);
		rights.add(autoLogin);		
		rights.add(hideNavigator);
		rights.add(hideViewTabs);
		rights.add(hideTitleBar);
		rights.add(new LabelField());
		rights.add(hideToolBar);
		rights.add(hideSave);		
		rights.add(hideFilter);
		rights.add(hideStaticFilter);
		rights.add(hideHorizontalAxis);
		rights.add(hideVerticalAxis);
		
		hideNavigator.setValue(true);
		hideViewTabs.setValue(true);
		autoLogin.setValue(true);
		autoUser.setValue(true);
		hideTitleBar.setValue(false);
		hideToolBar.setValue(false);
		hideSave.setValue(false);
		hideFilter.setValue(false);
		hideStaticFilter.setValue(false);
		hideHorizontalAxis.setValue(false);
		hideVerticalAxis.setValue(false);
		
		LabelField desc = new LabelField(constants.directLinkExplanation());				
		
		panel.add(desc);
		panel.add(rights);
		
		LabelField label = new LabelField();
		label.setHeight("20px");
		panel.add(label);
				
		directLink = new TextArea();
		directLink.setReadOnly(true);
		directLink.setSize(375, 100);
		panel.add(directLink);
		
		LabelField label2 = new LabelField();
		label2.setHeight("20px");
		panel.add(label2);
		
		// finally the apply/cancel button:
		SelectionListener<ComponentEvent> buttonListener = new SelectionListener<ComponentEvent>() {
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
		okButton = new Button(constants.close());
		okButton.setItemId(BUTTON_OK);
//		cancelButton = new Button("Cancel");
//		cancelButton.setItemId(BUTTON_CANCEL);
		okButton.addSelectionListener(buttonListener);
//		cancelButton.addSelectionListener(buttonListener);
		panel.addButton(okButton);
//		panel.addButton(cancelButton);

		directLink.setValue(getLinkString());
		
		return panel;
	}
	
	public String getLinkString() {		
		String adaptedHostPage = GWT.getHostPageBaseURL();
		if (!adaptedHostPage.endsWith("/")) {
			adaptedHostPage = adaptedHostPage + "/";
		}
		String link = adaptedHostPage + "SpagoBIJPaloEngine.html?";
		String languageCode = languageBox.getValue().getValue().getId();
		if (languageCode != null && !languageCode.isEmpty()) {
			if (!languageCode.equals("en")) {
				link += "locale=" + languageCode + "&";
			}
		}
		if (themeBox.getValue() == null) {
			SimpleComboValue <ThemeData> val = themeBox.findModel(new ThemeData("n", "blue"));
			themeBox.setValue(val);
		}
		String themeCode = themeBox.getValue().getValue().getId();
		if (themeCode != null && !themeCode.isEmpty()) {
			link += "theme=" + themeCode + "&";
		}		
		link += "options=(";
		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		if (autoUser.getValue()) {
			link += "user=\"" + user.getLogin() + "\",";
		}
		if (autoLogin.getValue()) {
			link += "pass=\"" + user.getPassword() + "\",";
		}
		
		if (hideNavigator.getValue()) {
			link += "hideNavigator,";
		}
		if (hideViewTabs.getValue()) {
			link += "hideViewTabs,";
		}
		
		link += "[openView=\"" + xView.getId() + "\"";
		
		if (hideStaticFilter.getValue()) {
			link += ",hideStaticFilter";
		}
		if (hideTitleBar.getValue()) {
			link += ",hideTitleBar";
		}
		if (hideToolBar.getValue()) {
			link += ",hideToolBar";
		}
		if (hideSave.getValue()) {
			link += ",hideSave";
		}
		if (hideFilter.getValue()) {
			link += ",hideFilter";
		}
		if (hideHorizontalAxis.getValue()) {
			link += ",hideHorizontalAxis";
		}
		if (hideVerticalAxis.getValue()) {
			link += ",hideVerticalAxis";
		}
		link += "])";
		return link;
	}	
}
