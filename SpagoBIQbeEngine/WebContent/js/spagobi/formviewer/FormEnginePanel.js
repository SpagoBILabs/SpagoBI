/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
/**
  * Object name 
  * 
  * [description]
  * 
  * 
  * Public Properties
  * 
  * [list]
  * 
  * 
  * Public Methods
  * 
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi.formviewer");

Sbi.formviewer.FormEnginePanel = function(formEngineConfig) {
	
	var defaultSettings = {
		//title: LN('sbi.qbe.queryeditor.title'),
	};
	
	if(Sbi.settings && Sbi.settings.qbe && Sbi.settings.qbe.queryBuilderPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.qbe.queryBuilderPanel);
	}
	
	var c = Ext.apply(defaultSettings, formEngineConfig.config || {});
	
	Ext.apply(this, c);
	
	/*
	this.services = this.services || new Array();	
	this.services['saveQuery'] = this.services['saveQuery'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'SAVE_QUERY_ACTION'
		, baseParams: new Object()
	});
	
	this.addEvents('execute');
	*/
	
	this.initFormViewerPage(formEngineConfig.template, c.formViewerPageConfig || {},formEngineConfig.formValues);
	this.initResultsPage(c.resultsPageConfig || {});
	this.initWorksheetPage(formEngineConfig.worksheet || {});
	
	c = Ext.apply(c, {
		closable: false
		, border: false
		, activeItem: 0
		, hideMode: !Ext.isIE ? 'nosize' : 'display'
		, layout: 'card'
		, items: [this.formViewerPage, this.resultsPage, this.worksheetPage]
	});
	
	
	// constructor
    Sbi.formviewer.FormEnginePanel.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.formviewer.FormEnginePanel, Ext.Panel, {
    
    services: null
    , formViewerPage: null
    , resultsPage: null
   
   
    // -- public methods ----------------------------------------------------------------------------------
    
    
    
    // -- private methods ----------------------------------------------------------------------------------
    
    , initFormViewerPage: function(template, config, formValues) {
		this.formViewerPage = new Sbi.formviewer.FormViewerPage(template, config, formValues);
		this.formViewerPage.on('submit', this.moveToResultsPage, this);
		this.formViewerPage.on('crosstabrequired', this.moveToWorksheetPage, this);
	}

	, initResultsPage: function(config) {
		this.resultsPage = new Sbi.formviewer.ResultsPage(config);
		this.resultsPage.on('edit', this.moveToFormPage, this);
	}
	
	, initWorksheetPage: function(config) {
		this.worksheetPage = new Sbi.formviewer.WorksheetPage(config);
		this.worksheetPage.on('edit', this.moveToFormPage, this);
	}

    , moveToWorksheetPage: function(formState) {
    	this.worksheetPage.updateWorksheetEngine();
    	this.getLayout().setActiveItem( 2 );
    	this.worksheetPage.setFormState(formState);
	}
	
    , moveToResultsPage: function(formState) {
    	this.getLayout().setActiveItem( 1 );
    	this.resultsPage.setFormState(formState);
    	this.resultsPage.loadResults(formState.groupingVariables);
	}
    
    , moveToFormPage: function() {
    	this.getLayout().setActiveItem( 0 );
	}
    
    , validate : function () {
    	return this.worksheetPage.worksheetDesignerPanel.validate(this.getWorksheetTemplateAsString, this.worksheetPage.worksheetDesignerPanel.showValidationErrors, this );	
    }
    
    , getWorksheetTemplateAsString : function () {
	    if (this.worksheetPage !== null) {

			var worksheetDefinition = this.worksheetPage.getWorksheetDefinition();
			var formState = this.formViewerPage.getFormState();
			

			var template = Ext.util.JSON.encode({
				'OBJECT_WK_DEFINITION' : worksheetDefinition,
				'OBJECT_FORM_VALUES' : formState
			});
			return template;
		} else {
			alert('Warning: worksheetDesignerPanel not defined!!');
			return null;
		}
	}
    
    , getFormState : function () {
    	return this.formViewerPage.getFormState();
    }
	
});