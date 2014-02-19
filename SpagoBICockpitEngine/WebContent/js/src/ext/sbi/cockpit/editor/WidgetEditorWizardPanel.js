/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.cockpit.editor");

Sbi.cockpit.editor.WidgetEditorWizardPanel = function(config) { 
	
	Sbi.trace("[WidgetEditorWizardPanel.constructor]: IN");

	// init properties...
	var defaultSettings = {
		frame: false,
		border: false
	};
	var settings = Sbi.getObjectSettings('Sbi.cockpit.editor.WidgetEditorWizardPanel', defaultSettings);
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);
	
	//c.activeItem = (config && config.widget && config.widget.dataset)?1:0 //sets to 1 if the dataset was already selected
	Sbi.trace("[WidgetEditorWizardPanel.constructor]: initial active page is [" + c.activeItem + "]");
	
	Sbi.cockpit.editor.WidgetEditorWizardPanel.superclass.constructor.call(this, c);
	
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
	
	usedDatasets: null
	
	// =================================================================================================================
	// METHODS
	// =================================================================================================================
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------

	, getDatasetBrowserPage: function() {
		return this.getPage(0);
	}

	, getWidgetEditorPage: function() {
		return this.getPage(1);
	}
	
	, setDatasetBrowserPageState: function(state) {
		this.getDatasetBrowserPage().setPageState(state);
	}
	
	, setWidgetEditorPageState: function(state) {
		this.getWidgetEditorPage().setPageState(state);
	}
	
	, selectDataset: function(dataset) {
		this.setDatasetBrowserPageState({dataset: dataset});
	}
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------

	, initPages: function(){
		Sbi.trace("[WidgetEditorWizardPanel.initPages]: IN");
		
		this.pages = new Array();
		
		var datasetsBrowserPage = this.initDatasetBrowserPage();
		this.pages.push(datasetsBrowserPage);
		Sbi.trace("[WidgetEditorWizardPanel.initPages]: dataset browser page succesfully adedd");
		
		var widgetEditorPage = this.initWidgetEditorPage();
		this.pages.push(widgetEditorPage);
		Sbi.trace("[WidgetEditorWizardPanel.initPages]: widget editor page succesfully adedd");
		
		Sbi.trace("[WidgetEditorWizardPanel.initPages]: OUT");
		
		return this.pages;
	}
	
	, initDatasetBrowserPage: function() {
		Sbi.trace("[WidgetEditorWizardPanel.initDatasetBrowserPage]: IN");
		
		var datasetsBrowserPage = new Sbi.cockpit.editor.dataset.DatasetBrowserPage({
			//itemId: 0, 
			usedDatasets: this.usedDatasets
		});
		
		Sbi.trace("[WidgetEditorWizardPanel.initDatasetBrowserPage]: OUT");
		
		return datasetsBrowserPage;
	}
	
	, initWidgetEditorPage: function() {
//		var widgetEditorPage = new Sbi.cockpit.editor.widget.WidgetEditor({
//			itemId: 1
//			
//		});
	
		var widgetEditorPage = new Sbi.cockpit.editor.widget.WidgetEditorPage({});
		
		return widgetEditorPage;
	}
	

	
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------

});
