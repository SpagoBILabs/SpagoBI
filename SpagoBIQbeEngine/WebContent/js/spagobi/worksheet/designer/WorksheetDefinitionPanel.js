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
  * - Davide Zerbetto (davide.zerbetto@eng.it)
  */

Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.WorksheetDefinitionPanel = function(config) {	

	var defaultSettings = {
		// default settings
		layout : 'fit'
		, autoScroll : true
		, border : false
	};
	
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.worksheetDefinitionPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.worksheetDefinitionPanel);
	}
 
	this.services = new Array();
	var params = {};
	this.services['setWorkSheetState'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'SET_WORKSHEET_DEFINITION_ACTION'
			, baseParams: params
	});
	this.services['getWorkSheetState'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_WORKSHEET_PREVIEW_ACTION'
			, baseParams: params
	});
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);

	this.init(config);
	
	c = Ext.apply(c, {
	    items: [this.tabs]
	});

	// constructor
	Sbi.worksheet.designer.WorksheetDefinitionPanel.superclass.constructor.call(this, c);
	
};

Ext.extend(Sbi.worksheet.designer.WorksheetDefinitionPanel, Ext.Panel, {

	worksheetDesignerPanel : null
	, worksheetPreviewPanel : null
//	, formState : null  // for SmartFilter engine: TODO move this variable elsewhere
	, tabs : null
	
	,
	init : function (config) {
		
		this.worksheetDesignerPanel = new Sbi.worksheet.designer.WorksheetDesignerPanel({worksheetTemplate : config	});
		this.worksheetPreviewPanel = new Sbi.worksheet.runtime.WorkSheetPreviewPage({}); // was ({closable: false});
			
		this.worksheetPreviewPanel.on('activate', function() {
			//validate
			this.worksheetDesignerPanel.validate(
					function(){
						this.setWorksheetState(this.refreshWorksheetPreview, Sbi.exception.ExceptionHandler.handleFailure, this);
					}
					, this.worksheetDesignerPanel.showValidationErrors
					, this
			);
			
		}, this);
		
		this.tabs = new Ext.TabPanel({
			items: [this.worksheetDesignerPanel, this.worksheetPreviewPanel]
			, activeTab : 0
		});

	}

	,
	setWorksheetState : function (successFn, failureFn, scope) {
		var worksheetDefinition = this.worksheetDesignerPanel.getWorksheetDefinition();
		var params = {
			'worksheetdefinition':  Ext.encode(worksheetDefinition)
		};
		
//		params.formstate = Ext.util.JSON.encode(this.getFormState());
		
		Ext.Ajax.request({
		    url: this.services['setWorkSheetState'],
		    success: successFn,
		    failure: failureFn,
		    scope: scope,
		    params: params
		});   
	}
	
	,
	getWorksheetDefinition : function () {
		return this.worksheetDesignerPanel.getWorksheetDefinition();   
	}
	
	, validate : function () {
		return 	this.worksheetDesignerPanel.validate(this.getWorksheetTemplateAsString, this.worksheetDesignerPanel.showValidationErrors, this );	
	}

	, getWorksheetTemplateAsString : function () {
		var worksheetDefinition = null;
		if (this.worksheetDesignerPanel.rendered === true) {
			// get the current worksheet designer state
			worksheetDefinition = this.worksheetDesignerPanel.getWorksheetDefinition();
		} else {
			// get the initial worksheet template
			worksheetDefinition = this.worksheetDesignerPanel.worksheetTemplate;
		}
		this.addAdditionalData(worksheetDefinition);
		var template = Ext.util.JSON.encode({
			'OBJECT_WK_DEFINITION' : worksheetDefinition
		});
		return template;
	}
	,
	refreshWorksheetPreview : function () {
		this.worksheetPreviewPanel.getFrame().setSrc(this.services['getWorkSheetState']);
	}
	, addAdditionalData : function(sheetTemplate){
		if(this.worksheetPreviewPanel.rendered === true){
			var additionalData = this.worksheetPreviewPanel.getFrame().getWindow().workSheetPanel.getAdditionalData();
			var sheets = sheetTemplate.sheets;
			for(var i=0; i<sheets.length; i++){
				if(additionalData[i].data!=undefined && additionalData[i].data!=null && additionalData[i].data.length>0 ){
					if(sheets[i].content.crosstabDefinition.calculatedFields==undefined || sheets[i].content.crosstabDefinition.calculatedFields==null){
						sheets[i].content.crosstabDefinition.calculatedFields =additionalData[i].data.crosstabDefinition.calculatedFields;
					}else{
						sheets[i].content.crosstabDefinition.calculatedFields = Ext.apply(sheets[i].content.crosstabDefinition.calculatedFields, additionalData[i].data.crosstabDefinition.calculatedFields);
					}
				}
			}
		}
	}


});