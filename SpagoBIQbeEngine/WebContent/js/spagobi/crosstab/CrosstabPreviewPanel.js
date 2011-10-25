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
  *  contentloaded: fired after the data has been loaded
  * 
  * Authors
  * 
  * - Davide Zerbetto (davide.zerbetto@eng.it)
  */

Ext.ns("Sbi.crosstab");

//config contains:
//					crosstabConfig definition of the crosstab
Sbi.crosstab.CrosstabPreviewPanel = function(config) {
	
	var defaultSettings = {
		title: LN('sbi.crosstab.crosstabpreviewpanel.title')
  	};
	if(Sbi.settings && Sbi.settings.qbe && Sbi.settings.qbe.crosstabPreviewPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.qbe.crosstabPreviewPanel);
	}
	
	this.services = new Array();
	var params = {};
	this.services['loadCrosstab'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'LOAD_CROSSTAB_ACTION'
		, baseParams: params
	});
	

	var c = Ext.apply(defaultSettings, config || {});
	
	c = Ext.apply(c, {
      		layout:'fit',
      		border: false,
      		id: 'CrosstabPreviewPanel'
    	});
	
	this.calculatedFields = new Array();
	if (config.crosstabDefinition !== undefined && config.crosstabDefinition.calculatedFields !== undefined) {
		this.calculatedFields = config.crosstabDefinition.calculatedFields;
	}

	// constructor
    Sbi.crosstab.CrosstabPreviewPanel.superclass.constructor.call(this, c);
    this.addEvents('contentloaded');
    this.addEvents('beforeload');
    
};

Ext.extend(Sbi.crosstab.CrosstabPreviewPanel, Ext.Panel, {
	
	services: null
	, crosstab: null
	, calculatedFields: null
	, loadMask: null
	, sheetName: null
	, requestParameters: null // contains the parameters to be sent to the server on the crosstab load invocation
	
		, exportContent: function(){
			var crosstabData = this.serializeCrossTab(); 
			var crosstabDataEncoded = Ext.util.JSON.encode(crosstabData);
			var exportedCrosstab = {CROSSTAB: crosstabDataEncoded, SHEET_TYPE: 'CROSSTAB'};
			return exportedCrosstab;
		}

		, load: function(crosstabDefinition, filters) {
			var crosstabDefinitionEncoded = Ext.util.JSON.encode(crosstabDefinition);
			this.requestParameters = {
				crosstabDefinition: crosstabDefinitionEncoded
				, sheetName : this.sheetName
			}
			if(filters!=undefined && filters!=null){
				this.requestParameters.optionalfilters = Ext.util.JSON.encode(filters);
			}
			if (this.fireEvent('beforeload', this, this.requestParameters) !== false) { // this permits other objects 
																						// to modify the crosstab requestParameters
				this.showMask();
				this.loadCrosstabAjaxRequest.defer(100, this,[crosstabDefinitionEncoded]);
			}
		}

		, loadCrosstabAjaxRequest: function(crosstabDefinitionEncoded){
			Ext.Ajax.request({
		        url: this.services['loadCrosstab'],
		        params: this.requestParameters,
		        success : function(response, opts) {
		  			this.refreshCrossTab(Ext.util.JSON.decode( response.responseText ));
		        },
		        scope: this,
				failure: function(response, options) {
					this.hideMask();
					Sbi.exception.ExceptionHandler.handleFailure(response, options);
				}      
			});
		}
			
	, refreshCrossTab: function(crosstab){

		if(this.crosstab!=null){
			this.calculatedFields = Ext.apply(this.calculatedFields, this.crosstab.getCalculatedFields());
		}
	
		this.removeAll(true);
		
		var rows = this.fromNodeToArray(crosstab.rows);
		var columns = this.fromNodeToArray(crosstab.columns);
		var data = crosstab.data;
		var config = crosstab.config;
		var measuresMetadata = crosstab.measures_metadata;
		
		var c = {
				 rowHeadersDefinition: rows
				, columnHeadersDefinition: columns
				, entries: data
				, withRowsSum: config.calculatetotalsonrows=="on"
				, withColumnsSum: config.calculatetotalsoncolumns=="on"
				, withColumnsPartialSum: config.calculatesubtotalsonrows=="on"
				, withRowsPartialSum: config.calculatesubtotalsoncolumns=="on"
				, calculatedFields: this.calculatedFields
				, misuresOnRow: config.measureson=='rows'
				, percenton: config.percenton
				, measuresMetadata: measuresMetadata
		};

		c = Ext.apply(c,this.crosstabConfig||{});
		
		this.crosstab =  new Sbi.crosstab.core.CrossTab(c);
		this.crosstab.reloadHeadersAndTable(null,true);
		this.add(this.crosstab);
		
		this.hideMask();
		
		this.doLayout();
		
		if(config.columnsOverflow){		
			Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.crosstab.crosstabpreviewpanel.overflow.warning'), 'Warning');
		}
		
	}

	, fromNodeToArray: function(node){
		var childs = node.node_childs;
		var array = new Array();
		array.push(node.node_key);
		if(childs!=null && childs.length>0){
			var childsArray = new Array();
			for(var i=0; i<childs.length; i++){
				childsArray.push(this.fromNodeToArray(childs[i]));
			}
			array.push(childsArray);
		}
		return array;
	}
	
	, hideMask: function() {
    	if (this.loadMask != null) {
    		this.loadMask.hide();
    	}
    	this.fireEvent('contentloaded');
	}
	
    , showMask : function(){
    	if(!this.hideLoadingMask){
	    	if (this.loadMask == null) {
	    		this.loadMask = new Ext.LoadMask('CrosstabPreviewPanel', {msg: "Loading.."});
	    	}
	    	this.loadMask.show();
    	}
    }
    
    , serializeCrossTab: function () {
    	if (this.crosstab != null) {
    		return this.crosstab.serializeCrossTab();
    	} else {
    		throw "Crosstab not defined";
    	}
    }
    
    , getCalculatedFields: function () {
    	if (this.crosstab != null) {
    		return this.crosstab.getCalculatedFields();
    	} else {
    		return new Array();
    	}
    }

});