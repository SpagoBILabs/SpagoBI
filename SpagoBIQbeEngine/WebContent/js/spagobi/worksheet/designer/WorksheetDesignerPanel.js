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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.WorksheetDesignerPanel = function(config) { 

	var defaultSettings = {
		title: LN('sbi.worksheet.designer.worksheetdesignerpanel.title')
	};

	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.worksheetDesignerPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.worksheetDesignerPanel);
	}

	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.initPanels(config);
	
	this.setGlobalFilters(this.worksheetTemplate.globalFilters || []);
	
	c = Ext.apply(c, {
			layout: 'border',
			autoScroll: true,
			items: [
		        {
		        	id: 'designToolsPanel',
		        	region: 'west',
		        	width: 275,
		        	collapseMode:'mini',
		        	autoScroll: true,
		        	split: true,
		        	layout: 'fit',
		        	items: [this.designToolsPanel]
		        },
		        {
		        	id: 'sheetsContainerPanel',	  
		        	region: 'center',
		        	split: true,
		        	collapseMode:'mini',
		        	autoScroll: true,
		        	layout: 'fit',
		        	items: [this.sheetsContainerPanel]
		        }
			]
	}); 
	

	Sbi.worksheet.designer.WorksheetDesignerPanel.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.worksheet.designer.WorksheetDesignerPanel, Ext.Panel, {
	designToolsPanel: null,
	sheetsContainerPanel: null,
	worksheetTemplate: {},   // the initial worksheet template; to be passed as a property of the constructor's input object!!!

	initPanels: function(config){
		this.designToolsPanel = new Sbi.worksheet.designer.DesignToolsPanel();
		this.designToolsPanel.on('toolschange',function(change){
			this.sheetsContainerPanel.updateActiveSheet(change);
		},this);
		this.designToolsPanel.on(
				'attributeDblClick', 
				this.attributeDblClickHandler, 
				this
			);

		this.sheetsContainerPanel = new Sbi.worksheet.designer.SheetsContainerPanel(Ext.apply(this.sheetsContainerPanelCfg  || {}, {
			sheets : this.worksheetTemplate.sheets || []  ,
			smartFilter: config.smartFilter || false
		}));
		this.sheetsContainerPanel.on(
			'attributeDblClick', 
			this.attributeDblClickHandler,
			this
		);
		
		this.sheetsContainerPanel.on('sheetchange',function(activeSheet){
			this.designToolsPanel.updateToolsForActiveTab(activeSheet);
		},this);
	}
	
	, attributeDblClickHandler : function (thePanel, attribute, theSheet) {
		var worksheetDefinition = this.getWorksheetDefinition();
		var params = {
			worksheetDefinition : Ext.encode(worksheetDefinition)
		};
		if (theSheet) {
			// double-click event on a sheet
			params.sheetName = theSheet.getName();
			attribute = theSheet.getFilterOnDomainValues(attribute);
			if (attribute.values == '[]') {
				// if there are no filters on sheet, consider the global filters
				var globalFilter = this.getGlobalFilterForAttribute(attribute);
				if (globalFilter !== null) {
					attribute.values = globalFilter.values;
				}
			}
		}
		var c = {
     		attribute : attribute
     		, params : params
	    };
     	var chooserWindow = new Sbi.worksheet.designer.AttributeValuesChooserWindow(c);
     	chooserWindow.on('selectionmade', function(theWindow, sSelection) {
     		attribute.values = Ext.encode(theWindow.getSelection());
     	}, this);
	}
	
	, getWorksheetDefinition: function () {
		var	worksheetDefinition = this.sheetsContainerPanel.getSheetsState();
		worksheetDefinition.globalFilters = this.getGlobalFilters();
		worksheetDefinition.version = Sbi.config.worksheetVersion;
		return worksheetDefinition;
	}
	
	, validate: function (successHandler, failureHandler, scope) {
		// return an array of validationError object, if no error returns an empty array
		var errorArray = this.sheetsContainerPanel.validate();

		if(errorArray && errorArray.length>0){
			return failureHandler.call(scope || this, errorArray);
		}
		else {
			return successHandler.call(scope || this);	
		}
	}
	
	, getGlobalFilters : function () {
		return this.designToolsPanel.getGlobalFilters();
	}
	
    , showValidationErrors : function(errorsArray) {
    	errMessage = '';
    	
    	for(var i = 0; i < errorsArray.length; i++) {
    		var error = errorsArray[i];
    		var sheet = error.sheet;
    		var message = error.message;
    		errMessage += error.sheet + ': ' + error.message + '<br>';
    	}
    	
    	Sbi.exception.ExceptionHandler.showErrorMessage(errMessage, LN('sbi.crosstab.crossTabValidation.title'));
   	
    }
	
    , getGlobalFilterForAttribute : function (attribute) {
    	return this.designToolsPanel.getGlobalFilterForAttribute(attribute);
    }
    
	, setGlobalFilters : function (globalFilters) {
		this.designToolsPanel.setGlobalFilters(globalFilters);
	}
	
});
