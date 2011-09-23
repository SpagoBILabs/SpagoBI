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
 * updateToolsForActiveTab(activeSheet): update the tools: take the configuration of the activeSheet
 * and update the tools
 * 
 * 
 * Public Events
 * 
 * toolschange(change): the value of the tools is changed.. change a map with the change value.
 * for example {layout: layout-header}
 * 
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.DesignToolsPanel = function(config) { 

	var defaultSettings = {};

	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.designTools) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.designTools);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	this.services = this.services || new Array();	
	this.services['getQueryFields'] = this.services['getQueryFields'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_WORKSHEET_FIELDS_ACTION'
		, baseParams: new Object()
	});
	
	this.addEvents("attributeDblClick");
	
	this.initPanels();
	
	c = {
        layout: {
        	type:'border'
            //type:'vbox',
            //align:'stretch'
        },
        items:[this.designToolsFieldsPanel, this.designToolsPallettePanel, this.designToolsLayoutPanel]
	};
	
	Sbi.worksheet.designer.DesignToolsPanel.superclass.constructor.call(this, c);
	
};

Ext.extend(Sbi.worksheet.designer.DesignToolsPanel, Ext.Panel, {
	designToolsFieldsPanel: null,
	designToolsPallettePanel: null,
	designToolsLayoutPanel: null,
	globalFilters: null,

	initPanels: function() {
		
		this.designToolsFieldsPanel = new Sbi.formbuilder.QueryFieldsPanel({
			border: false,
	        gridConfig: {
				ddGroup: 'worksheetDesignerDDGroup'
	        	, type: 'queryFieldsPanel'
	        },
			region : 'north',
			split: true,
			height : 120,
			services : this.services
		});
		this.designToolsFieldsPanel.store.on('load', this.fieldsLoadedHandler, this);
		this.designToolsFieldsPanel.store.on('beforeload', this.getGlobalFilters, this); // forces a calculation of global filters
		this.designToolsFieldsPanel.grid.on('rowdblclick', this.fieldDblClickHandler, this);
		
		this.designToolsPallettePanel = new Sbi.worksheet.designer.DesignToolsPallettePanel({region : 'center'});
		this.designToolsLayoutPanel = new Sbi.worksheet.designer.DesignToolsLayoutPanel({region : 'south', height : 130 , split: true});
//		this.designToolsFieldsPanel.flex = 1;
//		this.designToolsPallettePanel.flex = 1;
//		this.designToolsLayoutPanel.flex = 1;
		this.designToolsLayoutPanel.on('layoutchange', function(sheetLayout){
			var change = {
				'sheetLayout' : sheetLayout
			};
			this.fireEvent('toolschange',change);
		}, this);
	}

	, fieldDblClickHandler : function (grid, rowIndex, event) {
		var record = grid.store.getAt(rowIndex);
		if (record.data.nature == 'attribute' || record.data.nature == 'segment_attribute') {
	     	this.fireEvent("attributeDblClick", this, record.data);
		}
	}
	
	, fieldsLoadedHandler : function (store, records, options) {
		store.each(this.initAttributeValues, this);
	}

	, initAttributeValues : function (record) {
		var globalFilter = this.getGlobalFilterForRecord(record);
		if (globalFilter != null) {
			// global filter was found
			record.data.values = globalFilter.values;
		} else {
			// global filter was not found
			record.data.values = '[]';
		}
	}
	
	, getGlobalFilterForRecord : function (record) {
		var toReturn = null;
		for (var i = 0; i < this.globalFilters.length; i++) {
			var aGlobalFilter = this.globalFilters[i];
			if (record.data.alias == aGlobalFilter.alias) {
				toReturn = aGlobalFilter;
				break;
			}
		}
		return toReturn;
	}
	
	//Update the tools info for the active sheet
	, updateToolsForActiveTab: function(activeSheet){
		if ( activeSheet.sheetLayout !== null ) {
			this.designToolsLayoutPanel.setLayoutValue(activeSheet.sheetLayout);
		}
	}
	
	, refresh: function(){
		this.designToolsFieldsPanel.refresh();
	}
	
    , getFields : function () {
    	return this.designToolsFieldsPanel.getFields();
    }
    
	, getGlobalFilters : function () {
		var fields = this.getFields();
		if (fields.length == 0) {
			// fields were not loaded
			return this.globalFilters;
		}
		// fields were already loaded and initialized by the fieldsLoadedHandler function
		this.globalFilters = [];
		for (var i = 0; i < fields.length; i++) {
			var aField = fields[i];
			if (aField.values != '[]') {
				this.globalFilters.push(aField);
			}
		}
		return this.globalFilters;
	}
    
	, setGlobalFilters : function (globalFilters) {
		this.globalFilters = globalFilters;
	}
	
});
