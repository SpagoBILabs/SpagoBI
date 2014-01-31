/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.cockpit.editor");

Sbi.cockpit.editor.WidgetEditorWizardPanel = function(config) { 
	
	Sbi.trace("[WidgetEditorWizardPanel.constructor]: IN");

	var defaultSettings = {		
		activeItem : (config.widget && config.widget.dataset)?1:0 //sets to 1 if the dataset was already selected
	};
		
	if(Sbi.settings && Sbi.cockpit && Sbi.cockpit.editor && Sbi.cockpit.editor.widgetEditorWizardPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.cockpit.editor.widgetEditorWizardPanel);
	}
	var c = Ext.apply(defaultSettings, config || {});
	Ext.apply(this, c);
		
	Sbi.cockpit.editor.WidgetEditorWizardPanel.superclass.constructor.call(this, c);
	
	this.addEvents('close');
	
	Sbi.trace("[WidgetEditorWizardPanel.constructor]: OUT");
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
Ext.extend(Sbi.cockpit.editor.WidgetEditorWizardPanel, Sbi.widgets.WizardPanel, {
	
	isPageValid: function(page) {
		Sbi.trace("[WidgetEditorWizardPanel.isPageValid]: IN");
		
		var isValid = true;
		
		Sbi.trace("[WidgetEditorWizardPanel.isPageValid]: Page number is equal to [" + this.getPageNumber(page) + "]");
		if (this.getPageNumber(page) === 1){
			isValid = isValid && this.isDatasetBrowserPageValid();	
		}
		Sbi.trace("[WidgetEditorWizardPanel.isPageValid]: OUT");
		
		return isValid;
	}
	
	, isDatasetBrowserPageValid: function() {
		Sbi.trace("[WidgetEditorWizardPanel.isDatasetBrowserPageValid]: IN");
		Sbi.trace("[WidgetEditorWizardPanel.isDatasetBrowserPageValid]: 0.dataset: " + this.pages[0].dataset);
		Sbi.trace("[WidgetEditorWizardPanel.isDatasetBrowserPageValid]: 1.dataset: " + this.pages[1].dataset);
		var sm = this.widgetManager.getStoreManager();
		if ((sm == null || sm.getCount()== 0 ) &&
				this.pages[0].widget.dataset === undefined || this.pages[0].widget.dataset === null){
			alert('Per procedere e\' necessario selezionare un dataset!');
			return false;
		}else{
			//gets the first dataset available
			var dsDefault = sm.get(0);
			if (dsDefault)
				this.pages[0].widget.dataset = dsDefault.datasetLabel;
		}
		Sbi.trace("[WidgetEditorWizardPanel.isDatasetBrowserPageValid]: OUT");
		return true;
	}

	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------

	, initPages: function(){
		Sbi.trace("[WidgetEditorWizardPanel.initPages]: IN");
		
		this.pages = new Array();
		
		var datasetsBrowserPage = new Sbi.widgets.DatasetsBrowserPanel({
			widgetManager: this.widgetManager
			, widget: this.widget
			, itemId: 0
		}); 
		datasetsBrowserPage.on('click', this.onClick, this);
		datasetsBrowserPage.on('selectDataSet',  function(l){
			this.onSelect(l);
			datasetsBrowserPage.viewPanel.refresh();
		}
	, this);	
		this.pages.push(datasetsBrowserPage);
		Sbi.trace("[WidgetEditorWizardPanel.initPages]: dataset browser page succesfully adedd");
		
		var widgetDesignerPage = new Sbi.cockpit.editor.WidgetEditor({
			itemId: 1,
			dataset: this.widget.dataset || undefined
		});
		this.pages.push(widgetDesignerPage);
		Sbi.trace("[WidgetEditorWizardPanel.initPages]: widget editor page succesfully adedd");
		
		Sbi.trace("[WidgetEditorWizardPanel.initPages]: OUT");
		
		return this.pages;
	}
	
	, moveToNextPage: function() {
		this.superclass().moveToNextPage.call(this);
		var newPage = this.superclass().getActivePage.call(this);
		newPage.on('close',this.closeWizard, this);
		newPage.on('confirm',this.defineTemplate, this);
		if (newPage.updateValues){
			var formState = this.getFormState();
			newPage.updateValues(formState);
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------
	, onSelect: function(c){	
		//removes old selection from the storeManager (if exists)
		if (c.label != c.oldLabel && this.widgetManager.getStoreByLabel(c.oldLabel) != null)
			this.widgetManager.removeStore(c.oldLabel);
		//adds the dataset to the storeManager (throught the WidgetManager)
		this.widget.dataset = c.label;
		var storeConfig = {};
	    storeConfig.dsLabel = this.widget.dataset;	
	    this.widgetManager.addStore(storeConfig);		
	}
	
	, getFormState: function (){
		var form = {};
		
		form.dataset = this.widget.dataset;
		
		return form;
	}
});
