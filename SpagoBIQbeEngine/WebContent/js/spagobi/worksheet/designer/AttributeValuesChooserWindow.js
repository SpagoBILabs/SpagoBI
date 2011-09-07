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
	};
	
	var c = Ext.apply(defaultSettings, config || {});
	
	this.attribute = c.attribute; // the json object representing the attribute: it must be in the constructor input object
	
	var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE'};
	this.services = this.services || new Array();
	
	this.services['getValues'] = this.services['getValues'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_VALUES_FOR_CROSSTAB_ATTRIBUTES_ACTION'
		, baseParams: params
	});
	
	c = Ext.apply(c, {
		url : this.services['getValues']
		, columnHeader : "Values"
		, columnName : "Values"
	});
	
	// constructor
	Sbi.worksheet.designer.AttributeValuesChooserWindow.superclass.constructor.call(this, c);
	
	this.on('beforeclose', this.updateValues, this);
 	this.on('load', this.selectValues, this);
	var params = {
		ALIAS : this.attribute.alias
	};
	// if a global variable Sbi.formviewer.formEnginePanel is defined, it is the form engine panel (SmartFilter).
	// Send the form state to the server
	if (Sbi.formviewer && Sbi.formviewer.formEnginePanel) {
		var formState = Sbi.formviewer.formEnginePanel.getFormState();
		params.formState = Ext.encode(formState);
	}
	this.show();
 	this.load({params: params});

};

Ext.extend(Sbi.worksheet.designer.AttributeValuesChooserWindow, Sbi.widgets.SimpleValuesChooserWindow, {

	attribute 	: null // the json object representing the attribute: it must be in the constructor input object
	
	,
	updateValues : function ( ) {
		this.attribute.values = Ext.encode(this.getSelectedValues());
	}
	
	,
	selectValues : function ( ) {
		this.select(Ext.decode(this.attribute.values));
	}
	
});