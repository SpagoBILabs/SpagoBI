/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.cockpit.editor");

Sbi.cockpit.editor.WidgetEditorWizardPanel = function(config) { 
	
	Sbi.trace("[WidgetEditorWizardPanel.constructor]: IN");

	// init properties...
	var defaultSettings = {
		// set default values here
	};
	var settings = Sbi.getObjectSettings('Sbi.cockpit.editor.WidgetEditorWizardPanel', defaultSettings);
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);
	
	
	c.activeItem = (config && config.widget && config.widget.dataset)?1:0 //sets to 1 if the dataset was already selected
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
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
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
				this.pages[0].selectedDatasetLabel === undefined || this.pages[0].selectedDatasetLabel === null){
			alert('Per procedere e\' necessario selezionare un dataset!');
			return false;
		}else{
			//gets the first dataset available
			var dsDefault = sm.get(0);
			if (dsDefault)
				this.pages[0].selectedDatasetLabel = dsDefault.datasetLabel;
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
		
//		var datasetsBrowserPage = new Sbi.widgets.DatasetsBrowserPanel({
//			widgetManager: this.widgetManager
//			//, widget: this.widget
//			, itemId: 0
//		}); 
//		datasetsBrowserPage.on('click', this.onClick, this);
//		datasetsBrowserPage.on('selectDataSet',  function(l){
//			this.onSelect(l);
//			datasetsBrowserPage.viewPanel.refresh();
//		}
//		, this);	
		
		Sbi.trace("[WidgetEditorWizardPanel.initDatasetBrowserPage]: IN");
		
		var datasetsBrowserPage = new Sbi.cockpit.editor.dataset.DatasetBrowserPage({
			itemId: 0
			, widgetManager: this.widgetManager
		});
		//var datasetsBrowserPage = new Ext.Panel({itemId: 0, html: "DatasetsBrowser"});
		
		Sbi.trace("[WidgetEditorWizardPanel.initDatasetBrowserPage]: OUT");
		
		return datasetsBrowserPage;
	}
	
	, initWidgetEditorPage: function() {
		var widgetEditorPage = new Sbi.cockpit.editor.widget.WidgetEditor({
			itemId: 1
			//dataset: this.widget.dataset || undefined
		});
	
		return widgetEditorPage;
	}
	

	
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------

});
