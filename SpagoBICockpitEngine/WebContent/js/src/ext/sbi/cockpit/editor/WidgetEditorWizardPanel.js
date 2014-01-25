/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.cockpit.editor");

Sbi.cockpit.editor.WidgetEditorWizardPanel = function(config) { 
	
	Sbi.trace("[WidgetEditorWizardPanel.constructor]: IN");

	var defaultSettings = {		
		activeItem : 0 //set to 1 if the dataset was already selected
	};
		
	if(Sbi.settings && Sbi.cockpit && Sbi.cockpit.editor && Sbi.cockpit.editor.widgetEditorWizardPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.cockpit.editor.widgetEditorWizardPanel);
	}
	var c = Ext.apply(defaultSettings, config || {});
	Ext.apply(this, c);
		
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
		
		if (this.pages[0].widget.dataset === undefined || this.pages[0].widget.dataset === null){
			alert('Per procedere e\' necessario selezionare un dataset!');
			return false;
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
		
		var datasetsBrowserPage = new Sbi.cockpit.editor.WidgetEditorDatasetsBrowser({
			widgetManager: this.widgetManager
			, widget: this.widget
			, itemId: 0
		}); 
		datasetsBrowserPage.addListener('click', this.onClick, this);
		this.pages.push(datasetsBrowserPage);
		Sbi.trace("[WidgetEditorWizardPanel.initPages]: dataset browser page succesfully adedd");
		
		var widgetDesignerPage = new Sbi.cockpit.editor.WidgetEditor({
			itemId: 1
		});
		this.pages.push(widgetDesignerPage);
		Sbi.trace("[WidgetEditorWizardPanel.initPages]: widget editor page succesfully adedd");
		
		Sbi.trace("[WidgetEditorWizardPanel.initPages]: OUT");
		
		return this.pages;
	}
});
