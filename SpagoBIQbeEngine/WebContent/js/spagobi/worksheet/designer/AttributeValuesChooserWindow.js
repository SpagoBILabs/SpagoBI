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
 * Authors - Davide Zerbetto (davide.zerbetto@eng.it)
 */
Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.AttributeValuesChooserWindow = function(config) {
	
	if (!(config && config.attribute)) {
		throw "The input object must contain the attribute property";
	}
	
	var defaultSettings = {
		singleSelect : false
	};
	
	var c = Ext.apply(defaultSettings, config || {});
	
	this.addEvents('load');	
	
	this.attribute = c.attribute; // the json object representing the attribute: it must be in the constructor input object
	this.worksheetDefinition = c.worksheetDefinition; // the json object representing the worksheet: it must be in the constructor input object
	
	var service_params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE'};
	
	this.services = this.services || new Array();
	
	this.services['getValues'] = this.services['getValues'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_VALUES_FOR_CROSSTAB_ATTRIBUTES_ACTION'
		, baseParams: service_params
	});
	
	this.store = new Ext.data.JsonStore({
		url: this.services['getValues']  + '&ALIAS=' + this.attribute.alias + '&ENTITY_ID=' + this.attribute.id
	});
	c.store = this.store;

	
	this.store.on('loadexception', function(store, options, response, e) {
		Sbi.exception.ExceptionHandler.handleFailure(response, options);
	});
	this.store.on('load', function (store, records, options) {
		this.fireEvent('load', this, records, options);
	}, this);
	
	this.store.on('loadexception', function(store, options, response, e) {
		Sbi.exception.ExceptionHandler.handleFailure(response, options);
	});

	this.on('selectionmade', this.updateValues, this);

	
	// PARAMS
	
	var params = {
		worksheetdefinition:  Ext.encode(this.worksheetdefinition)
	};
	// if a global variable Sbi.formviewer.formEnginePanel is defined, it is the form engine panel (SmartFilter).
	if (Sbi.formviewer && Sbi.formviewer.formEnginePanel) {
		var formState = Sbi.formviewer.formEnginePanel.getFormState();
		params.formState = Ext.encode(formState);
	}
	var p = Ext.apply({}, params, {
		start: this.start
		, limit: this.limit
	});
	
	c.params = p;
	
	// add selection values
	if (this.attribute.values) {
		c.startValues = this.attribute.values; 	
	}
	
	// constructor
	Sbi.worksheet.designer.AttributeValuesChooserWindow.superclass.constructor.call(this, c);
	// set first selection
	this.setSelection(Ext.decode(this.attribute.values));
 	
	this.show(this);

	this.store.load({params: p});
};

Ext.extend(Sbi.worksheet.designer.AttributeValuesChooserWindow, Sbi.widgets.FilterLookupPopupWindow, {

    start: 0 
    , limit: 20
	, attribute 	: null // the json object representing the attribute: it must be in the constructor input object
	, worksheetDefinition : null // the json object representing the worksheet: it must be in the constructor input object
	
	,
	updateValues : function ( ) {
		this.attribute.values = Ext.encode(this.getSelection());
	}

	,
	selectValues : function ( ) {
		this.select(Ext.decode(this.attribute.values));
	}
	
});