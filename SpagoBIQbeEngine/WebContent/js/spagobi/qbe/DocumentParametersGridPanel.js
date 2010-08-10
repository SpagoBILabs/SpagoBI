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

Ext.ns("Sbi.qbe");

Sbi.qbe.DocumentParametersGridPanel = function(config, store) {
	var c = Ext.apply({
		// set default values here
	}, config || {});
	
	/*
	this.services = new Array();
	
	var params = {};
	
	this.services['getDocumentParameters'] = Sbi.config.remoteServiceRegistry.getServiceUrl({
		serviceName: 'GET_PARAMETERS_FOR_EXECUTION_ACTION'
		, baseParams: params
	});

	
	this.store = new Ext.data.Store({
        autoLoad:true,
        proxy: new Ext.data.ScriptTagProxy({
	        url: this.services['getDocumentParameters'],
	        method: 'GET'
	    }),
	    reader: new Ext.data.JsonReader({}, [
	            {name:'id'},
	            {name:'label'},
	            {name:'type'}
	        ])
	});
	*/
	
	this.store = store;
 	
	this.sm = new Ext.grid.RowSelectionModel({singleSelect:true});
 	
 	this.grid = new Ext.grid.GridPanel({
        store: this.store
        , border: false
        , columns: [
            {header: LN('sbi.qbe.documentparametersgridpanel.headers.label'), sortable: true, dataIndex: 'label'}
        ]
		, viewConfig: {
        	forceFit: true
        	, emptyText: LN('sbi.qbe.documentparametersgridpanel.emptytext')
		}
        , sm : this.sm
        , enableDragDrop: true
        , ddGroup: 'gridDDGroup'
        , height: 155
        //, autoScroll: true
        , layout: 'fit'
 	});
 	this.grid.type = this.type;
 	
	c = Ext.apply({}, c, {
		title: LN('sbi.qbe.documentparametersgridpanel.title')
        , border: false
        //, autoScroll: true
        , collapsible: false
        , height: 180
        , items: [this.grid]
	});
	
	// constructor
	Sbi.qbe.DocumentParametersGridPanel.superclass.constructor.call(this, c);
    
};

Ext.extend(Sbi.qbe.DocumentParametersGridPanel, Ext.Panel, {
    
	services: null
	, type: 'documentparametersgrid'
	
});