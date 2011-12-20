/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
  * - Davide Zerbetto (davide.zerbetto@eng.it)
  */

Ext.ns("Sbi.formviewer");

Sbi.formviewer.WorksheetPage = function(config) {	
	var defaultSettings = {
		title: LN('sbi.worksheet.title')
		, layout: 'fit'
		, autoScroll: true
	};
		
	if(Sbi.settings && Sbi.settings.formviewer && Sbi.settings.formviewer.worksheetPage) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.formviewer.worksheetPage);
	}
		
	this.services={};
	this.services['getWorkSheetState'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_WORKSHEET_PREVIEW_ACTION'
		, baseParams: params
	});
	this.services['setWorkSheetState'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'SET_WORKSHEET_DEFINITION_ACTION'
		, baseParams: params
	});
	this.services['executeWorksheetStartAction'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'START_WORKSHEET_FROM_QBE_ACTION'
			, baseParams: params
	});
	this.services['updateWoksheetDataSet'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'UPDATE_DATA_SET_WITH_SMART_FILTER_VALUES'
			, baseParams: params
	});
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
		
	this.toolbar = new Ext.Toolbar({
		items: [
		    '->'
		    , {
				text: LN('sbi.formviewer.resultspage.backtoform'),
				handler: function() {this.fireEvent('edit');},
				scope: this
		    }
		  ]
	});
	
	this.init(c);
	
	c = Ext.apply(c, {
	    tbar: this.toolbar,
	    style: 'padding:3px;',
	    items: [this.tabs]
	});
		
	// constructor
	Sbi.formviewer.WorksheetPage.superclass.constructor.call(this, c);
	
	this.addEvents('edit');
};

Ext.extend(Sbi.formviewer.WorksheetPage, Ext.Panel, {

	formState: null
	
    // -- public methods -----------------------------------------------------------------------

    , getFormState: function() {
		return this.formState;
	}

	, setFormState: function(formState) {
		this.formState = formState;
	}
    
    // -- private methods -----------------------------------------------------------------------

	, init: function (c) {
		
		var items = [];
	
		this.worksheetDesignerPanel = new Sbi.worksheet.designer.WorksheetDesignerPanel(Ext.apply(c||{},{smartFilter: true}));
		
		items.push(this.worksheetDesignerPanel);
		
		this.worksheetPreviewPanel = new Sbi.worksheet.runtime.WorkSheetPreviewPage({closable: false});
			
		this.worksheetPreviewPanel.on('activate', function() {
			//validate
			
			this.worksheetDesignerPanel.validate(
					function(){
						this.setWorksheetState(this.refreshWorksheetPreview, Sbi.exception.ExceptionHandler.handleFailure, this);
					}
					, this.worksheetDesignerPanel.showValidationErrors
					, this);
			
			//			var errorArray = this.worksheetDesignerPanel.validate();
			//			if(errorArray && errorArray.length>0){
				//				this.worksheetDesignerPanel.showValidationErrors(errorArray);
				//			return;
				//	}
			//	else {
				// valid case
				//		this.setWorksheetState(this.refreshWorksheetPreview, Sbi.exception.ExceptionHandler.handleFailure, this);
		//	}
		}, this);
			
		items.push(this.worksheetPreviewPanel);

		
		this.tabs = new Ext.TabPanel({
			border: false,
	  		activeTab: 0,
	  		items: items
		});
	}

	/**
	 * Update the worksheet engine instance with
	 * the form values
	 * If the worksheet has not been already started
	 * we start it...
	 * 
	 */
	, updateWorksheetEngine : function(){
		this.tabs.setActiveTab(0);
		var worksheetEngineInitialized = this.engineInitialized;
		if (worksheetEngineInitialized === undefined || worksheetEngineInitialized == false) {
			if (!this.notFirstTimeDesignPanelOpened) {
				this.notFirstTimeDesignPanelOpened = true;
				Ext.Ajax.request({
					url: this.services['executeWorksheetStartAction'],
					params: {},
					scope: this,
					success: this.updateWorksheetDataSet,
					failure: Sbi.exception.ExceptionHandler.handleFailure
				});
			} else {
				this.updateWorksheetDataSet();
			}
		} else {
			this.updateWorksheetDataSet();
		}
	}
	
	/**
	 * Update the data set inside the worksheet
	 */
	, updateWorksheetDataSet: function(){
		var params = {
				'formstate':   Ext.util.JSON.encode(this.getFormState())
		};
		Ext.Ajax.request({
			url: this.services['updateWoksheetDataSet'],
			params: params,
			scope: this,
			failure: Sbi.exception.ExceptionHandler.handleFailure
		});
	}
	
	, setWorksheetState : function (successFn, failureFn, scope) {
		var worksheetDefinition = this.worksheetDesignerPanel.getWorksheetDefinition();
		var params = {
				'worksheetdefinition':  Ext.encode(worksheetDefinition)
		};
		
		params.formstate = Ext.util.JSON.encode(this.getFormState());
		
		Ext.Ajax.request({
		    url: this.services['setWorkSheetState'],
		    success: successFn,
		    failure: failureFn,
		    scope: scope,
		    params: params
		});   
	}
	
	, getWorksheetDefinition : function () {
		return this.worksheetDesignerPanel.getWorksheetDefinition();   
	}
	
	, validate : function () {
		return this.worksheetDesignerPanel.validate();
	}

  	, showCrosstabPreview: function(crosstabDefinition) {
  		this.crosstabPreviewPanel.on('beforeload', this.addFormStateParameter, this);
  		this.tabs.activate(this.crosstabPreviewPanel);
  		this.crosstabPreviewPanel.load(crosstabDefinition);
  	}
  	
  	, addFormStateParameter: function(crosstabPreviewPanel, requestParameters) {
  		requestParameters.formstate = Ext.util.JSON.encode(this.getFormState());
  	}
  	
	, refreshWorksheetPreview : function () {
		this.worksheetPreviewPanel.getFrame().setSrc(this.services['getWorkSheetState']);
	}
	


});