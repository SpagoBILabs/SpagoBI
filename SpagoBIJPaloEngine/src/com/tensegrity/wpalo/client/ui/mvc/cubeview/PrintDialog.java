/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */
package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XPrintConfiguration;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.ui.mvc.viewbrowser.ViewBrowserModel;

public class PrintDialog extends Window {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected static transient final ILocalMessages  messages  = Resources.getInstance().getMessages();

	public static final String BUTTON_OK = "ok";
	public static final String BUTTON_CANCEL = "cancel";

	private final XViewModel view;
	
	private EnhancedSimpleComboBox <String> paperFormat;
	private EnhancedSimpleComboBox <String> paperOrientation;
	private CheckBox printTitle;
	private TextField<String> titleField;
	private CheckBox showPOV;
	private CheckBox showExpansionStates;
	private CheckBox indent;
	private CheckBox printPageNumbers;
	private Button okButton;
	private Button cancelButton;
	
	private static String paperFormatDefault;
	private static String paperOrientationDefault;
	private static HashMap <String, String> viewNameDefaults = new HashMap<String, String>();
	private static boolean showTitleDefault;
	private static boolean showPOVDefault;
	private static boolean showExpansionStatesDefault;
	private static boolean indentDefault;
	private static boolean printPageNumbersDefault;
	
	public static void setDefaults() {
		viewNameDefaults.clear();
		paperFormatDefault = constants.a4();
		paperOrientationDefault = constants.portrait();
		showTitleDefault = true;
		showPOVDefault = true;
		showExpansionStatesDefault = true;
		indentDefault = true;
		printPageNumbersDefault = true;
	}
	
	public PrintDialog(XViewModel view) {
		this.view = view;
		setClosable(false);
		setCloseAction(CloseAction.CLOSE);
		setHeading(messages.printHeading(ViewBrowserModel.modify(view.getName())));
		setPixelSize(420, 370);
		setModal(true);
		add(createForm());
		createAndAddOkCancelButtons();
		DOM.setStyleAttribute(getElement(), "backgroundColor", "white");
	}

	public final void addButtonListener(String buttonId,
			Listener<BaseEvent> listener) {
		if (buttonId.equals(BUTTON_OK))
			okButton.addListener(Events.Select, listener);
		else if (buttonId.equals(BUTTON_CANCEL))
			cancelButton.addListener(Events.Select, listener);
	}

	private EnhancedSimpleComboBox <String> addComboBox(LayoutContainer container, String title) {
		EnhancedSimpleComboBox <String> comboBox =
			new EnhancedSimpleComboBox<String>();
		comboBox.setHideLabel(true);
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlign(VerticalAlignment.TOP);		
		LabelField labelL = new LabelField(title + ":");
		labelL.setPixelSize(130, -1);
		comboBox.setPixelSize(220, -1);
		comboBox.setEditable(false);
		hp.add(labelL);
		hp.add(comboBox);
		TableData ldata = new TableData();
		ldata.setColspan(2);

		container.add(hp, ldata);
		return comboBox;
	}
	
	private TextField <String> addTextField(LayoutContainer container, String title) {
		TextField <String> textfield = new TextField<String>();
		textfield.setHideLabel(true);

		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlign(VerticalAlignment.TOP);		
		LabelField labelL = new LabelField(title + ":");
		labelL.setPixelSize(130, -1);
		textfield.setPixelSize(220, -1);
		hp.add(labelL);
		hp.add(textfield);
		TableData ldata = new TableData();
		ldata.setColspan(2);

		container.add(hp, ldata);
		return textfield;		
	}
	
	private CheckBox addCheckBox(LayoutContainer container, String title) {
		CheckBox checkbox = new CheckBox();
		checkbox.setBoxLabel(title);		
		container.add(checkbox);
		return checkbox;		
	}
	
	private void addTitlePanel(LayoutContainer container) {
		printTitle = new CheckBox();
		printTitle.setBoxLabel(constants.title() + ":");
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlign(VerticalAlignment.MIDDLE);
		hp.add(printTitle);

		titleField = new TextField<String>(){
			  protected void onRender(Element target, int index) {
				  super.onRender(target, index);				  
				  setPagePosition(166, getAbsoluteTop() + 2);
			  }
		};
		titleField.setHideLabel(true);

//		LabelField labelL = new LabelField(constants.title() + ":");
//		labelL.setStyleName("x-form-cb-label");
//		labelL.setPixelSize(120, -1);		
		
		titleField.setPixelSize(220, -1);
//		hp.add(labelL);
		hp.add(titleField);
		TableData ldata = new TableData();
		ldata.setColspan(1);

		container.add(hp, ldata);
	}
	
	private FormPanel createForm() { 
		FormPanel panel = new FormPanel();
		panel.setFrame(true);
		panel.setCollapsible(false);
		panel.setHeaderVisible(false);
		panel.setSize(406, -1);
		panel.setButtonAlign(HorizontalAlignment.RIGHT);

		panel.add(createPagePanel());
		panel.add(createOutputPanel());		
		
		return panel;
	}

	private final FieldSet createPagePanel() {
		FieldSet pagePanel = new FieldSet();
		pagePanel.setHeading(constants.paper());

		LayoutContainer fields = new LayoutContainer();		
		TableLayout rLayout = new TableLayout(2);
		fields.setLayout(rLayout);

		pagePanel.add(fields);
		
		paperFormat = addComboBox(fields, constants.paperFormat());		
		fillPaperFormats();
		paperOrientation = addComboBox(fields, constants.paperOrientation()); 
		fillPaperOrientations();
		
		return pagePanel;
	}

	private final FieldSet createOutputPanel() {
		FieldSet outputPanel = new FieldSet();
		outputPanel.setHeading(constants.design());
		
		LayoutContainer fields = new LayoutContainer();		
		TableLayout rLayout = new TableLayout(1);
		fields.setLayout(rLayout);

		outputPanel.add(fields);
		
		addTitlePanel(fields);
//		printTitle = addCheckBox(fields, constants.printTitle());
//		
//		titleField = addTextField(fields, constants.title());
		if (viewNameDefaults.get(view.getId()) != null) {
			titleField.setValue(viewNameDefaults.get(view.getId()));	
		} else {
			titleField.setValue(view.getName());
		}		

		printTitle.addListener(Events.Change, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				titleField.setEnabled(printTitle.getValue());
			}
		});
		printTitle.setValue(showTitleDefault);
		titleField.setEnabled(showTitleDefault);
		
		showPOV = addCheckBox(fields, constants.printPOV());
		showPOV.setValue(showPOVDefault);
		
		showExpansionStates = addCheckBox(fields, constants.showExpansionStates());
		
		indent = addCheckBox(fields, constants.indent());
		indent.setValue(indentDefault);
		indent.setEnabled(!showExpansionStatesDefault);
		
		showExpansionStates.addListener(Events.Change, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				indent.setEnabled(!showExpansionStates.getValue());
			}
		});
		showExpansionStates.setValue(showExpansionStatesDefault);
		
		printPageNumbers = addCheckBox(fields, constants.showPageNumbers());
		printPageNumbers.setValue(printPageNumbersDefault);
		
		return outputPanel;
	}
	
	private final void createAndAddOkCancelButtons() {
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
		okButton = new Button(constants.ok());
		okButton.setItemId(BUTTON_OK);
		cancelButton = new Button(constants.cancel());
		cancelButton.setItemId(BUTTON_CANCEL);
		okButton.addSelectionListener(buttonListener);
		cancelButton.addSelectionListener(buttonListener);
		
		addButton(okButton);
		addButton(cancelButton);
	}
	
	@SuppressWarnings("unchecked")
	private final void fillPaperFormats() {
		List <String> paperFormats = new ArrayList<String>();
		paperFormats.add(constants.a0());
		paperFormats.add(constants.a1());
		paperFormats.add(constants.a2());
		paperFormats.add(constants.a3());
		paperFormats.add(constants.a4());
		paperFormats.add(constants.a5());
		paperFormats.add(constants.a6());
		paperFormats.add(constants.b0());
		paperFormats.add(constants.b1());
		paperFormats.add(constants.b2());
		paperFormats.add(constants.b3());
		paperFormats.add(constants.b4());
		paperFormats.add(constants.b5());
		paperFormats.add(constants.b6());		
		paperFormats.add(constants.executive());
		paperFormats.add(constants.legal());
		paperFormats.add(constants.letter());
		
		paperFormat.add(paperFormats);
		paperFormat.setValue(paperFormat.findModel(paperFormatDefault));
	}
	
	@SuppressWarnings("unchecked")
	private final void fillPaperOrientations() {
		paperOrientation.add(constants.portrait());
		paperOrientation.add(constants.landscape());
		paperOrientation.setValue(paperOrientation.findModel(paperOrientationDefault));
	}
	
	private final int translateFormat(String f) {
		if (constants.a0().equals(f)) { return XPrintConfiguration.FORMAT_A0; }
		if (constants.a1().equals(f)) { return XPrintConfiguration.FORMAT_A1; }
		if (constants.a2().equals(f)) { return XPrintConfiguration.FORMAT_A2; }
		if (constants.a3().equals(f)) { return XPrintConfiguration.FORMAT_A3; }
		if (constants.a4().equals(f)) { return XPrintConfiguration.FORMAT_A4; }
		if (constants.a5().equals(f)) { return XPrintConfiguration.FORMAT_A5; }
		if (constants.a6().equals(f)) { return XPrintConfiguration.FORMAT_A6; }
		if (constants.b0().equals(f)) { return XPrintConfiguration.FORMAT_B0; }
		if (constants.b1().equals(f)) { return XPrintConfiguration.FORMAT_B1; }
		if (constants.b2().equals(f)) { return XPrintConfiguration.FORMAT_B2; }
		if (constants.b3().equals(f)) { return XPrintConfiguration.FORMAT_B3; }
		if (constants.b4().equals(f)) { return XPrintConfiguration.FORMAT_B4; }
		if (constants.b5().equals(f)) { return XPrintConfiguration.FORMAT_B5; }
		if (constants.b6().equals(f)) { return XPrintConfiguration.FORMAT_B6; }
		if (constants.executive().equals(f)) { return XPrintConfiguration.FORMAT_EXECUTIVE; }
		if (constants.legal().equals(f)) { return XPrintConfiguration.FORMAT_LEGAL; }
		if (constants.letter().equals(f)) { return XPrintConfiguration.FORMAT_LETTER; }
		
		return XPrintConfiguration.FORMAT_A4;
	}
	
	public XPrintConfiguration getPrintConfiguration() {
		XPrintConfiguration config = new XPrintConfiguration();
		
		config.setPaperFormat(translateFormat(paperFormat.getValue().getValue()));
		config.setPaperOrientation(paperOrientation.getValue().getValue().equals(constants.portrait()) ? XPrintConfiguration.PORTRAIT : XPrintConfiguration.LANDSCAPE);
		config.setShowTitle(printTitle.getValue());
		config.setTitle(titleField.getValue());
		config.setShowPOV(showPOV.getValue());
		config.setShowExpansionStateIcons(showExpansionStates.getValue());		
		config.setIndent(indent.getValue());
		config.setPrintPageNumbers(printPageNumbers.getValue());
		
		paperFormatDefault = paperFormat.getValue().getValue();
		paperOrientationDefault = paperOrientation.getValue().getValue();
		showTitleDefault = printTitle.getValue();
		if (printTitle.getValue()) {
			viewNameDefaults.put(view.getId(), titleField.getValue());
		}
		showPOVDefault = showPOV.getValue();
		showExpansionStatesDefault = showExpansionStates.getValue();
		if (!showExpansionStates.getValue()) {
			indentDefault = indent.getValue();
		}
		printPageNumbersDefault = printPageNumbers.getValue();
		
		return config;
	}
}
