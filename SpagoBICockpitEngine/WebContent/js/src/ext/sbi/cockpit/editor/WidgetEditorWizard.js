/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.cockpit.editor");

Sbi.cockpit.editor.WidgetEditorWizard = function(config) { 
	
	Sbi.trace("[WidgetEditorWizard.constructor]: IN");

	// init properties...
	var defaultSettings = {
	    layout:'fit'
	    , width: 1000
	    , height: 510
	    , closeAction:'hide'
	    , plain: true
	    , modal: true
	    , title: "Widget editor"
	};
	var settings = Sbi.getObjectSettings('Sbi.cockpit.editor.WidgetEditorWizard', defaultSettings);
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);
	
	this.init();
	
	c.items = [this.editorMainPanel];
	
	Sbi.cockpit.editor.WidgetEditorWizard.superclass.constructor.call(this, c);
	
	Sbi.trace("[WidgetEditorWizard.constructor]: OUT");
};

/**
 * @class Sbi.xxx.Xxxx
 * @extends Ext.util.Observable
 * 
 * bla bla bla bla bla ...
 */

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.cockpit.editor.WidgetEditorWizard, Ext.Window, {
	
	editorMainPanel: null
	, targetComponent: null
	, usedDatasets: null
	//, widgetManager: null
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, setUsedDatasets: function(datasets) {
		this.usedDatasets = datasets;
	}
	
	, getWizardTargetComponent: function() {
		return this.targetComponent;
	}

	, setWizardTargetComponent: function(component) {
		Sbi.trace("[WidgetEditorWizard.setWizardTargetComponent]: IN");
		this.targetComponent = component;
		var widget = this.targetComponent.getWidget();
		if(Sbi.isValorized(widget)) {
			Sbi.trace("[WidgetEditorWizard.setWizardTargetComponent]: target component already contains a widget");
			this.resetWizardState();
			var widgetConf = widget.getConfiguration();
			if(widgetConf.dataset) {
				this.editorMainPanel.selectDataset(widgetConf.dataset);
				this.editorMainPanel.setWidgetEditorPageState(widgetConf.custom);
				this.editorMainPanel.moveToPage (1);
			} else {
				this.editorMainPanel.moveToPage(0);
			}	
		} else {
			Sbi.trace("[WidgetEditorWizard.setWizardTargetComponent]: target component does not contains any widget");
			this.resetWizardState();
			this.editorMainPanel.moveToPage(0);
		}
		Sbi.trace("[WidgetEditorWizard.setWizardTargetComponent]: OUT");
	}

	
	, getWizardState: function() {
		return this.editorMainPanel.getWizardState();
	}

	, setWizardState: function(editorState) {
		Sbi.trace("[WidgetEditorWizard.setWizardState]: IN");
		Sbi.trace("[WidgetEditorWizard.setWizardState]: wizard new configuration is equal to [" + Sbi.toSource(editorState) + "]");
		this.editorMainPanel.setWizardState(editorState);
		Sbi.trace("[WidgetEditorWizard.setWizardState]: OUT");
	}
	
	, resetWizardState: function() {
		Sbi.trace("[WidgetEditorWizard.resetWizardState]: IN");
		this.editorMainPanel.resetWizardState();
		Sbi.trace("[WidgetEditorWizard.resetWizardState]: OUT");
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------

	, init: function(){
		Sbi.trace("[WidgetEditorWizard.init]: IN");
		
		this.editorMainPanel = new Sbi.cockpit.editor.WidgetEditorWizardPanel({
			usedDatasets: this.usedDatasets
		});
		this.editorMainPanel.on('cancel', this.onCancel, this);
		this.editorMainPanel.on('confirm', this.onConfirm, this);
		
		Sbi.trace("[WidgetEditorWizard.init]: OUT");
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, onCancel: function(){
		this.resetWizardState();
		this.hide();
	}
	
	, onConfirm: function(editorPanel, editorState){
		var component = this.getWizardTargetComponent();
		component.setWidgetConfiguration(editorState);
		this.hide();
		// TODO update store manager
	}

});
