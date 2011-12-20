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

Ext.ns("Sbi.qbe");

Sbi.qbe.DocumentParametersStore = function(config, data) {
	
	var c = Ext.apply({
		// set default values here
	}, config || {});
	
	this.services = new Array();
	
	var params = {};
	
	/*
	this.services['getDocumentParameters'] = Sbi.config.remoteServiceRegistry.getServiceUrl({
		serviceName: 'GET_PARAMETERS_FOR_EXECUTION_ACTION'
		, baseParams: params
	});
	
	c = Ext.apply({}, c, {
        autoLoad: false,
        proxy: new Ext.data.ScriptTagProxy({
	        url: this.services['getDocumentParameters'],
	        method: 'GET'
	    }),
	    reader: new Ext.data.JsonReader({id: 'id'}, [
            {name:'id'},
            {name:'label'},
            {name:'type'}
	    ])
	});
	*/
	
	c = Ext.apply({}, c, {
	    reader: new Ext.data.JsonReader({id: 'id'}, [
             {name:'id'},
             {name:'label'},
             {name:'type'}
 	    ])
	});
	
	// constructor
	Sbi.qbe.DocumentParametersStore.superclass.constructor.call(this, c);
    
};

Ext.extend(Sbi.qbe.DocumentParametersStore, Ext.data.Store, {
  
	services: null
	
	// if a parameter reference is recognized into leftOperandValue or rightOperandValue columns, their description are updated to :
	// Parameter [parameter title]
	, modifyFilter: function (filter) {
		var parameterUrlName = this.getReferencedParameter(filter.leftOperandValue);
		if (parameterUrlName !== null) {
			var parameterRecord = this.getParameterRecord(parameterUrlName);
			if (parameterRecord !== undefined) {
				filter.leftOperandDescription = '[' + parameterRecord.data.label + ']';
				filter.leftOperandLongDescription = LN('sbi.qbe.documentparametersgridpanel.parameterreference') + ' [' + parameterRecord.data.label + ']';
			}
		}
		parameterUrlName = this.getReferencedParameter(filter.rightOperandValue);
		if (parameterUrlName !== null) {
			var parameterRecord = this.getParameterRecord(parameterUrlName);
			if (parameterRecord !== undefined) {
				filter.rightOperandDescription = '[' + parameterRecord.data.label + ']';
				filter.rightOperandLongDescription = LN('sbi.qbe.documentparametersgridpanel.parameterreference') + ' [' + parameterRecord.data.label + ']';
			}
		}
		return filter;
	}

	// this method recognizes if the operand has a parameter reference
	// Example of parameter reference: P{...}, 'P{...}'
	, getReferencedParameter: function(operandValue) {
		// remove quotes, if they surround the value
		if (operandValue.substring(0, 1) == '\'' && operandValue.substring(operandValue.length - 1, operandValue.length) == '\'') {
			operandValue = operandValue.substring(1, operandValue.length - 1);
		}
		if (operandValue.substring(0, 2) == 'P{' && operandValue.substring(operandValue.length - 1, operandValue.length) == '}') {
			parameterUrlName = operandValue.substring(2, operandValue.length - 1);
			return parameterUrlName;
		} else return null;
	}
	
	, getParameterRecord: function(parameterUrlName) {
		var parameter = this.getById(parameterUrlName);
		return parameter;
	}
	
});