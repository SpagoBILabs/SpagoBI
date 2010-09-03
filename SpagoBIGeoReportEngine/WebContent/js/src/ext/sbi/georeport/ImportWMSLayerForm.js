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
  * - name (mail)
  */

Ext.ns("Sbi.georeport");

Sbi.georeport.ImportWMSLayerForm = function(config) {
	
	var defaultSettings = {
		border: false
	};
		
	if(Sbi.settings && Sbi.settings.georeport && Sbi.settings.georeport.importWMSLayerForm) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.georeport.importWMSLayerForm);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
		
	Ext.apply(this, c);
		
		
	this.services = this.services || new Array();	
	this.services['getLayersFromWms'] = this.services['getLayersFromWms'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GetLayersFromWMS'
		, baseParams: new Object()
	});
	
	this.initGrid(c.layerGridConfig || {});
	this.initButtons(c.buttonsConfig || {});
	
	c = Ext.apply(c, {
		layout: 'fit',
	   	items: [this.layerGrid]
	});
	if(this.buttons != null) {
		c.buttons = this.buttons;
	}

	// constructor
	Sbi.georeport.ImportWMSLayerForm.superclass.constructor.call(this, c);
    
    //this.addEvents();
};

Ext.extend(Sbi.georeport.ImportWMSLayerForm, Ext.FormPanel, {
    
    services: null
    , store: null
    , slectionModel: null
    , columnModel: null
    , toolbarConfig: null
    , grid: null
    , buttons: null
   
    // --- public methods -------------------------------------------
    
    , loadLayersFromWms: function() {
		Ext.Ajax.request({
   			url :  this.services['getLayersFromWms'], 
  		    params : {urlWms: this.wmsLayerUrl.getValue(), btnAddWms:this.btnAddWms.getValue()},
  		    method: 'POST',
  		    timeout: '300000', 
  		    waitMsg:'Loading',
  		    success: function (result,request) {
  		    	if(result.status == 200){
  		            var stringData = result.responseText;
  		            var jsonData = Ext.util.JSON.decode(stringData);
  		            wmsData = jsonData;
  		            this.store.loadData(wmsData);
  		        }
  		     },
  		     failure: function (result,request) { 
  		    	 Ext.MessageBox.alert('Failed', 'Error '); 
  		     }
  		     , scope: this
		});
	}
    
    , initStore: function() {
		var wmsData= Array();  
		var reader = new Ext.data.JsonReader({}, [
	        {name: 'id'},
	        {name: 'layername'},
	        {name: 'srs'},
	    ]);
	      
		this.store = new Ext.data.Store({
			reader: reader,
	        data: wmsData
		}); 
	}

	,initSelectionModel: function() {
		this.selectionModel = new Ext.grid.CheckboxSelectionModel({}); 
	}
	
	, initColumnModel: function() {
		this.columnModel = new Ext.grid.ColumnModel([
		    this.selectionModel,
		    //expander,
		    {id:'id',header: 'id', width: 10, sortable: true, dataIndex: 'id'},
		    {header: 'layername', width: 20, sortable: true, dataIndex: 'layername'},
		    {header: 'srs', width: 20, sortable: true, dataIndex: 'srs'}
		    //{header: 'imglegend', width: 20, sortable: true, dataIndex: 'imglegend'}
		]);
	}
    
	, initToolbar: function() {		
		this.wmsLayerUrl= new Ext.form.TextField({
			  fieldLabel:'WMS Url', 
			  name:'urlWms', 
			  width:540, 
			  value: 'http://localhost:8080/geoserver/wms'
		});
		this.btnAddWms= new Ext.form.Hidden({name: 'btnAddWms', value: 'AddWms'});
		
		this.toolbarConfig = [
		   this.wmsLayerUrl
		   , '-', 
		   {
			   tooltip:'Add a new wms layer',
			   iconCls:'addWms',
			   wmsLayerUrl: this.wmsLayerUrl,
			   handler: this.loadLayersFromWms, 
			   scope: this
			   
		   }
		];
		
	}
	
    , initGrid: function(c) {
    	this.initStore();
		this.initSelectionModel();
		this.initColumnModel();
		this.initToolbar();
		
		this.layerGrid = new Ext.grid.GridPanel({
	        id:'button-grid',
	        iconCls:'icon-grid',
	        store: this.store,
	        cm: this.columnModel,
	        sm: this.selectionModel,
	        layout: 'fit',
	        viewConfig: {
   	        	forceFit:true
   	        	, enableRowBody:true
   	        	, showPreview:true
   	     	},
	        tbar: this.toolbarConfig
	     });
	}

	, initButtons: function(c) {
		
	}
    
    // --- private methods ------------------------------------------
});